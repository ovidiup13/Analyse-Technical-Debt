package com.td.models;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class WorkItem {

    private IssueModel issue;
    private List<CommitModel> commits;

    /**
     * @return the issue
     */
    public IssueModel getIssue() {
        return issue;
    }

    /**
     * @param issue the issue to set
     */
    public void setIssue(IssueModel issue) {
        this.issue = issue;
    }

    /**
     * @return the commits
     */
    public List<CommitModel> getCommits() {
        return commits;
    }

    /**
     * @param commits the commits to set
     */
    public void setCommits(List<CommitModel> commits) {
        this.commits = commits;
    }

    public static Comparator<WorkItem> sortByLastCommitTimestamp() {
        return (item1, item2) -> {
            // sort by last commit timestamp?
            List<CommitModel> commits1 = item1.getCommits();
            LocalDateTime last1 = commits1.get(commits1.size() - 1).getTimestamp();

            List<CommitModel> commits2 = item2.getCommits();
            LocalDateTime last2 = commits2.get(commits2.size() - 1).getTimestamp();

            return last1.compareTo(last2);
        };
    }

}