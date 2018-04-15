package com.td.facades;

import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.td.models.BuildStatus;
import com.td.models.CommitModel;
import com.td.models.CommitStats;
import com.td.models.IssueModel;
import com.td.models.IssueStats;
import com.td.models.TDStats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StatsFacade {

    @Autowired
    private RepositoryFacade repositoryFacade;

    @Autowired
    private TDFacade tdFacade;

    /***
     * Returns list of issue stats by calculating work effort using commit
     * timestamps.
     */
    public Stream<IssueStats> getIssueStatsByCommitTimestamp(String repositoryId) {
        return repositoryFacade.getIssuesAndCommitsFiltered(repositoryId).map(item -> {
            Entry<String, List<CommitModel>> entry = item.entrySet().iterator().next();
            String issueKey = entry.getKey();
            List<CommitModel> issueCommits = entry.getValue();

            // generate simple stats
            IssueStats stats = new IssueStats();
            stats.setIssueKey(issueKey);
            // stats.setTechnicalDebt(getTechnicalDebtCount(issueCommits));
            stats.setTotalCommits(issueCommits.size());
            stats.setAuthor(issueCommits.get(0).getAuthor());
            // stats.setWorkEffort(WorkEffortCalculator.getWorkEffortByCommitTimestamp(issueCommits));
            // stats.setChangeSetStats(ChangeSetCalculator.getChangeSetStats(issueCommits));

            Optional<TDStats> opt = tdFacade.getTechnicalDebtForIssue(repositoryId, issueCommits);
            stats.setTdStats(opt.orElse(null));

            return stats;
        });
    }

    /***
    * Returns a list of issue stats by calculating work effort using ticket
    * timestamps.
    */
    public Stream<IssueStats> getIssueStatsByIssueTimestamp(String repositoryId) {
        return repositoryFacade.getIssuesAndCommitsFiltered(repositoryId).map(item -> {
            Entry<String, List<CommitModel>> entry = item.entrySet().iterator().next();
            String issueKey = entry.getKey();

            // get issue model
            Optional<IssueModel> issueOpt = repositoryFacade.getIssue(repositoryId, issueKey);
            if (!issueOpt.isPresent()) {
                return null;
            }
            IssueModel issue = issueOpt.get();
            if (issue.getStatus().equalsIgnoreCase("Open")) {
                return null;
            }

            List<CommitModel> issueCommits = entry.getValue();

            // generate simple stats
            IssueStats stats = new IssueStats();
            stats.setIssueKey(issueKey);
            // stats.setTechnicalDebt(getTechnicalDebtCount(issueCommits));
            stats.setTotalCommits(issueCommits.size());
            stats.setAuthor(issueCommits.get(0).getAuthor());
            // stats.setWorkEffort(WorkEffortCalculator.getWorkEffortByTicketTimestamp(issue));
            // stats.setChangeSetStats(ChangeSetCalculator.getChangeSetStats(issueCommits));

            Optional<TDStats> opt = tdFacade.getTechnicalDebtForIssue(repositoryId, issueCommits);
            stats.setTdStats(opt.orElse(null));

            return stats;
        }).filter(item -> item != null);
    }

    /**
     * Returns a list of total number of commits for all issues.
     */
    public Stream<IssueStats> getIssueStatsRaw(String repositoryId) {
        return repositoryFacade.getIssuesAndCommitsRaw(repositoryId).map(item -> {
            Entry<String, List<CommitModel>> entry = item.entrySet().iterator().next();
            String issueKey = entry.getKey();
            List<CommitModel> issueCommits = entry.getValue();

            IssueStats stats = new IssueStats();
            stats.setTotalCommits(issueCommits.size());
            stats.setIssueKey(issueKey);
            // stats.setChangeSetStats(ChangeSetCalculator.getChangeSetStats(issueCommits));

            Optional<TDStats> opt = tdFacade.getTechnicalDebtForIssue(repositoryId, issueCommits);
            stats.setTdStats(opt.orElse(null));

            return stats;
        });
    }

    /***
     * Returns statistics for commits in the repository.
     * @param id repository ID 
     */
    public CommitStats getCommitStats(String id) {
        CommitStats result = new CommitStats();

        List<CommitModel> commits = this.repositoryFacade.getAllCommits(id).stream()
                .filter(commit -> commit.getBuildStatus().equals(BuildStatus.SUCCESSFUL)).collect(Collectors.toList());

        int totalCommits = commits.size();
        int withIssues = (int) commits.stream().filter(commit -> commit.getIssueIds().size() > 0).count();
        int withoutIssues = totalCommits - withIssues;
        int numberOfAuthors = repositoryFacade.getCollaborators(id).size();

        double meanTicketsPerCommit = commits.stream().map(commit -> commit.getIssueIds().size()).reduce(0,
                (prev, cur) -> prev + cur) / ((double) totalCommits);
        double meanTDItemsPerCommit = commits.stream().map(commit -> commit.getTechnicalDebt().getTotalCount())
                .reduce(0, (prev, cur) -> prev + cur) / ((double) totalCommits);

        result.setTotalCommits(totalCommits);
        result.setCommitsWithIssues(withIssues);
        result.setCommitsWithoutIssues(withoutIssues);
        result.setNumberOfAuthors(numberOfAuthors);
        result.setMeanTicketsPerCommit(meanTicketsPerCommit);
        result.setMeanTDItemsPerCommit(meanTDItemsPerCommit);

        return result;
    }
}