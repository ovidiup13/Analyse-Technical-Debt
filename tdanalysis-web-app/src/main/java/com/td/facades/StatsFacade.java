package com.td.facades;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.td.models.CommitModel;
import com.td.models.IssueStats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StatsFacade {

    private static final int WORK_HOURS_PER_DAY = 8;

    @Autowired
    private RepositoryFacade repositoryFacade;

    /***
     * Returns a map of statistics of the calculated work effort and technical debt.
     */
    public List<IssueStats> getSimpleStats(String repositoryId) {
        List<Map<String, List<CommitModel>>> commits = repositoryFacade.getIssuesAndCommitsFiltered(repositoryId);
        List<IssueStats> result = new ArrayList<>(commits.size());
        commits.forEach(item -> {
            Entry<String, List<CommitModel>> entry = item.entrySet().iterator().next();
            String issueKey = entry.getKey();
            List<CommitModel> issueCommits = entry.getValue();

            // generate simple stats
            IssueStats stats = new IssueStats();
            stats.setIssueKey(issueKey);
            stats.setTechnicalDebt(getSimpleTechnicalDebt(issueCommits));
            stats.setWorkEffort(getWorkEffortByCommits(issueCommits));
            stats.setTotalCommits(issueCommits.size());
            stats.setAuthor(issueCommits.get(0).getAuthor());
            result.add(stats);
        });
        return result;
    }

    /***
     * Returns the overall work effort spent on a sequence of commits, based on commit timestamps.
     * It is a crude way to calculate work effort, there are better approaches.
     */
    private double getWorkEffortByCommits(List<CommitModel> commits) {
        int numberOfCommits = commits.size();
        CommitModel firstCommit = commits.get(0);
        CommitModel lastCommit = commits.get(numberOfCommits - 1);

        // get last commit before ticket start
        Optional<CommitModel> previousOpt = getPreviousCommitByAuthor(firstCommit);

        // if "previous" commit does not exist, use first commit
        CommitModel previousCommit = previousOpt.isPresent() ? previousOpt.get() : firstCommit;

        return normalizeWorkEffort(lastCommit.getTimestamp(), previousCommit.getTimestamp());
    }

    /**
     * Returns the previous commit by the same author.
     */
    private Optional<CommitModel> getPreviousCommitByAuthor(CommitModel commit) {
        String author = commit.getAuthor();
        String repoId = commit.getRepositoryId();

        // sorted by timestamp
        List<CommitModel> allCommits = repositoryFacade.getAllCommitsByAuthor(repoId, author);

        // binary search for current commit
        int index = Collections.binarySearch(allCommits, commit);

        // return previous commit
        return index < 1 ? Optional.empty() : Optional.of(allCommits.get(index - 1));
    }

    /***
     * Returns a count of the unique issues available in the list of commits. 
     */
    private long getSimpleTechnicalDebt(List<CommitModel> commits) {
        return commits.stream().map(commit -> commit.getBugs()).flatMap(bugs -> bugs.stream()).distinct().count();
    }

    /**
     * Calculates the work effort between two dates, assuming that the normal working day is 8 hours.
     */
    private double normalizeWorkEffort(LocalDateTime t1, LocalDateTime t2) {
        Duration duration = Duration.between(t1, t2);
        long days = Math.abs(duration.toDays());

        // case same day
        if (days < 1) {
            double hours = Math.abs(duration.toMinutes()) / 60.0;
            return hours < WORK_HOURS_PER_DAY ? hours : WORK_HOURS_PER_DAY;
        }

        // case any other day
        return (days + 1) * WORK_HOURS_PER_DAY;
    }

}