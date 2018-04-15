package com.td.facades;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.td.models.BuildStatus;
import com.td.models.CommitModel;
import com.td.models.CommitStats;
import com.td.models.IssueModel;
import com.td.models.IssueStats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StatsFacade {

    @Autowired
    private RepositoryFacade repositoryFacade;

    /**
     * Returns a list of total number of commits for all issues.
     */
    public Stream<IssueStats> getIssueStatsRaw(String repositoryId) {
        return repositoryFacade.getWorkItems(repositoryId).map(item -> {

            IssueModel issue = item.getIssue();
            List<CommitModel> commits = item.getCommits();

            IssueStats stats = new IssueStats();
            stats.setTotalCommits(commits.size());
            stats.setIssueKey(issue.getIssueKey());
            stats.setAuthor(issue.getAssignee());
            stats.setStatus(issue.getStatus());

            return stats;
        });
    }

    /***
     * Returns statistics for commits in the repository.
     * @param id repository ID 
     */
    public CommitStats getCommitStats(String id) {
        CommitStats result = new CommitStats();

        List<CommitModel> commits = this.repositoryFacade.getAllCommits(id).stream().collect(Collectors.toList());

        int totalCommits = commits.size();
        int numberOfAuthors = repositoryFacade.getCollaborators(id).size();

        // tickets
        int withIssues = (int) commits.stream().filter(commit -> commit.getIssueIds().size() > 0).count();
        int withoutIssues = totalCommits - withIssues;

        List<CommitModel> successfulCommits = commits.stream()
                .filter(commit -> commit.getBuildStatus().equals(BuildStatus.SUCCESSFUL)).collect(Collectors.toList());

        // builds
        int successfulBuilds = successfulCommits.size();
        int failedBuilds = totalCommits - successfulBuilds;

        // stats
        double meanTicketsPerCommit = successfulCommits.stream().map(commit -> commit.getIssueIds().size()).reduce(0,
                (prev, cur) -> prev + cur) / ((double) totalCommits);
        double meanTDItemsPerCommit = successfulCommits.stream()
                .map(commit -> commit.getTechnicalDebt().getTotalCount()).reduce(0, (prev, cur) -> prev + cur)
                / ((double) totalCommits);

        result.setTotalCommits(totalCommits);
        result.setCommitsWithIssues(withIssues);
        result.setCommitsWithoutIssues(withoutIssues);
        result.setNumberOfAuthors(numberOfAuthors);
        result.setMeanTicketsPerCommit(meanTicketsPerCommit);
        result.setMeanTDItemsPerCommit(meanTDItemsPerCommit);
        result.setSuccessfulBuilds(successfulBuilds);
        result.setFailedBuilds(failedBuilds);

        return result;
    }
}