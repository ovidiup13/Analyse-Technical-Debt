package com.td.facades;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.td.db.CommitRepository;
import com.td.db.IssueRepository;
import com.td.db.ProjectRepository;
import com.td.models.CommitModel;
import com.td.models.IssueModel;
import com.td.models.RepositoryModel;
import com.td.models.WorkItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

@Component
public class RepositoryFacade {

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

    /**
    * Retrieves all work items for a specific repository.
    */
    public Stream<WorkItem> getWorkItems(String id) {
        List<IssueModel> issues = issueRepository.findIssueModelsByRepositoryId(id, new Sort(Direction.ASC, "closed"));
        return issues.stream().map(issue -> {
            WorkItem item = new WorkItem();
            item.setIssue(issue);
            item.setCommits(commitRepository.findCommitModelsByIssueModels(issue.getIssueId(),
                    new Sort(Direction.ASC, "timestamp")));
            return item;
        });
    }

    public Stream<WorkItem> getWorkItemSingleAuthor(String id) {
        return getWorkItems(id).map(item -> {
            item.setCommits(filterByAuthor(item.getCommits()));
            return item;
        });
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

        return Collections.max(authorMap.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
    }

}