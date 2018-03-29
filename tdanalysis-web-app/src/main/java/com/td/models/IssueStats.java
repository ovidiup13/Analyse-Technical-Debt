package com.td.models;

public class IssueStats {

    private String issueKey;
    private WorkEffort workEffort;
    private long technicalDebt;
    private int totalCommits;
    private int totalIssues;
    private String author;

    /**
     * @return the workEffort
     */
    public WorkEffort getWorkEffort() {
        return workEffort;
    }

    /**
     * @param workEffort the workEffort to set
     */
    public void setWorkEffort(WorkEffort workEffort) {
        this.workEffort = workEffort;
    }

    /**
     * @return the technicalDebt
     */
    public long getTechnicalDebt() {
        return technicalDebt;
    }

    /**
     * @param technicalDebt the technicalDebt to set
     */
    public void setTechnicalDebt(long technicalDebt) {
        this.technicalDebt = technicalDebt;
    }

    /**
     * @return the issueKey
     */
    public String getIssueKey() {
        return issueKey;
    }

    /**
     * @param issueKey the issueKey to set
     */
    public void setIssueKey(String issueKey) {
        this.issueKey = issueKey;
    }

    /**
     * @return the totalCommits
     */
    public Integer getTotalCommits() {
        return totalCommits;
    }

    /**
     * @param totalCommits the totalCommits to set
     */
    public void setTotalCommits(Integer totalCommits) {
        this.totalCommits = totalCommits;
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the totalIssues
     */
    public int getTotalIssues() {
        return totalIssues;
    }

    /**
     * @param totalIssues the totalIssues to set
     */
    public void setTotalIssues(int totalIssues) {
        this.totalIssues = totalIssues;
    }

}