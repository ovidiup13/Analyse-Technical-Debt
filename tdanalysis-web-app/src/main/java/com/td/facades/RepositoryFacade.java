package com.td.facades;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.td.db.CommitRepository;
import com.td.db.IssueRepository;
import com.td.db.ProjectRepository;
import com.td.models.CommitModel;
import com.td.models.IssueModel;
import com.td.models.RepositoryModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class RepositoryFacade {

    private static final int OVER_COMMITS_PER_ISSUE = 1;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private CommitRepository commitRepository;

    /***
     * Returns all repositories from the database.
     */
    public List<RepositoryModel> getRepositories() {
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        return projectRepository.findAll(sort);
    }

    /***
     * Returns a RepositoryModel wrapped in an optional. 
     */
    public Optional<RepositoryModel> getRepository(String repositoryId) {
        RepositoryModel result = projectRepository.findOne(repositoryId);
        return Optional.ofNullable(result);
    }

    /***
     * Returns a list of all collaborators of the repository.
     */
    public List<String> getCollaborators(String id) {
        return getAllCommits(id).stream().map(commit -> commit.getAuthor()).distinct().collect(Collectors.toList());
    }

    /***
     * Returns all issues within the repository.
     */
    public List<IssueModel> getAllIssues(String repositoryId) {
        Sort sort = new Sort(Sort.Direction.ASC, "issueKey");
        return issueRepository.findIssueModelsByRepositoryId(repositoryId, sort);
    }

    /***
     * Returns an {@link Optional} object with an IssueModel object inside.
     */
    public Optional<IssueModel> getIssue(String repositoryId, String issueKey) {
        IssueModel issue = issueRepository.findIssueModelByIssueKeyAndRepositoryId(issueKey, repositoryId);
        return Optional.ofNullable(issue);
    }

    /***
     * Returns all commits in the repository.
     */
    public List<CommitModel> getAllCommits(String repositoryId) {
        Sort sort = new Sort(Sort.Direction.ASC, "timestamp");
        return commitRepository.findCommitModelsByRepositoryId(repositoryId, sort);
    }

    /***
     * Returns all commits pushed by an author to the repository.
     */
    public List<CommitModel> getAllCommitsByAuthor(String repositoryId, String author) {
        Sort sort = new Sort(Sort.Direction.ASC, "timestamp");
        return commitRepository.findCommitModelsByRepositoryIdAndAuthor(repositoryId, author, sort);
    }

    /***
     * Returns a single commit from the repository, given its SHA.
     */
    public Optional<CommitModel> getCommit(String repositoryId, String sha) {
        CommitModel result = commitRepository.findCommitModelByShaAndRepositoryId(sha, repositoryId);
        return Optional.ofNullable(result);
    }

    /***
     * Returns all commits that reference a specific issue. Commits are sorted by timestamp.
     */
    public List<CommitModel> getAllCommitsByIssue(String repoId, String issueKey) {
        IssueModel issue = issueRepository.findIssueModelByIssueKeyAndRepositoryId(issueKey, repoId);

        if (issue == null) {
            return new ArrayList<CommitModel>();
        }

        Sort sort = new Sort(Sort.Direction.ASC, "timestamp");
        return commitRepository.findCommitModelsByIssueModels(issue.getIssueId(), sort);
    }

    /**
    * Returns an array of maps, with each map containing an issue-commits pair.
    * Issues are sorted by created date while commits are sorted by timestamp.
    */
    public Stream<Map<String, List<CommitModel>>> getIssuesAndCommitsRaw(String repositoryId) {
        Sort sortClosed = new Sort(Sort.Direction.ASC, "closed");
        Sort sortTimestamp = new Sort(Sort.Direction.ASC, "timestamp");
        List<IssueModel> issues = issueRepository.findIssueModelsByRepositoryId(repositoryId, sortClosed);

        return issues.stream().map(issue -> {
            Map<String, List<CommitModel>> map = new HashMap<>();
            List<CommitModel> commits = commitRepository.findCommitModelsByIssueModels(issue.getIssueId(),
                    sortTimestamp);
            map.put(issue.getIssueKey(), commits);
            return map;
        });
    }

    /***
     * Similar to the getIssuesAndCommitsRaw() method, but result is filtered by
     * number of commits per issue and by author. Sorts the stream of
     * issue-commits entries by the last commit timestamp. 
     */
    public Stream<Map<String, List<CommitModel>>> getIssuesAndCommitsFiltered(String repositoryId) {
        // filter by author and number of commits
        return getIssuesAndCommitsRaw(repositoryId).map(this::getItemsByAuthor).filter(this::filterByCount)
                .sorted((item1, item2) -> {
                    // sort by last commit timestamp?
                    Entry<String, List<CommitModel>> entry1 = item1.entrySet().iterator().next();
                    List<CommitModel> commits1 = entry1.getValue();
                    LocalDateTime last1 = commits1.get(commits1.size() - 1).getTimestamp();

                    Entry<String, List<CommitModel>> entry2 = item2.entrySet().iterator().next();
                    List<CommitModel> commits2 = entry2.getValue();
                    LocalDateTime last2 = commits2.get(commits2.size() - 1).getTimestamp();

                    return last1.compareTo(last2);
                });
    }

    /**
     * Filters the issue-commit map by the number of commits associated with an issue.
     */
    private boolean filterByCount(Map<String, List<CommitModel>> item) {
        Entry<String, List<CommitModel>> entry = item.entrySet().iterator().next();
        return entry.getValue().size() >= OVER_COMMITS_PER_ISSUE;
    }

    /**
     * Filters map of issue-commits to only include revisions made by a single author.
     */
    private Map<String, List<CommitModel>> getItemsByAuthor(Map<String, List<CommitModel>> item) {
        Entry<String, List<CommitModel>> entry = item.entrySet().iterator().next();
        String issue = entry.getKey();
        List<CommitModel> commits = filterByAuthor(entry.getValue());
        item.replace(issue, commits);
        return item;
    }

    /**
     * Returns a list of commits filtered by a single (supposedly) author.  
     */
    private List<CommitModel> filterByAuthor(List<CommitModel> commits) {
        if (commits.isEmpty()) {
            return commits;
        }
        String author = getMainAuthor(commits);
        return commits.stream().filter(commit -> commit.getAuthor().equals(author)).collect(Collectors.toList());
    }

    /***
     * Retrieves the main author of the list of commits.
     * The "main author" is the author that has the most revisions.
     */
    private String getMainAuthor(List<CommitModel> commits) {
        Map<String, Integer> authorMap = new HashMap<>();
        commits.stream().forEach(commit -> {
            String author = commit.getAuthor();
            if (authorMap.containsKey(author)) {
                authorMap.replace(author, authorMap.get(author) + 1);
            } else {
                authorMap.put(author, 1);
            }
        });

        //TODO: what happens if there is a tie?
        return Collections.max(authorMap.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
    }

}