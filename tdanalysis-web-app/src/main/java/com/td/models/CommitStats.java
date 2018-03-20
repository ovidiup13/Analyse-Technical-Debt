package com.td.models;

public class CommitStats {

    private int totalCommits;
    private int commitsWithIssues;
    private int commitsWithoutIssues;
    private int numberOfAuthors;
    private double meanTicketsPerCommit;
    private double meanTDItemsPerCommit;

    /**
     * @return the totalCommits
     */
    public int getTotalCommits() {
        return totalCommits;
    }

    /**
     * @param totalCommits the totalCommits to set
     */
    public void setTotalCommits(int totalCommits) {
        this.totalCommits = totalCommits;
    }

    /**
     * @return the commitsWithIssues
     */
    public int getCommitsWithIssues() {
        return commitsWithIssues;
    }

    /**
     * @param commitsWithIssues the commitsWithIssues to set
     */
    public void setCommitsWithIssues(int commitsWithIssues) {
        this.commitsWithIssues = commitsWithIssues;
    }

    /**
     * @return the commitsWithoutIssues
     */
    public int getCommitsWithoutIssues() {
        return commitsWithoutIssues;
    }

    /**
     * @param commitsWithoutIssues the commitsWithoutIssues to set
     */
    public void setCommitsWithoutIssues(int commitsWithoutIssues) {
        this.commitsWithoutIssues = commitsWithoutIssues;
    }

    /**
     * @return the numberOfAuthors
     */
    public int getNumberOfAuthors() {
        return numberOfAuthors;
    }

    /**
     * @param numberOfAuthors the numberOfAuthors to set
     */
    public void setNumberOfAuthors(int numberOfAuthors) {
        this.numberOfAuthors = numberOfAuthors;
    }

    /**
     * @return the meanIssuesPerCommit
     */
    public double getMeanTicketsPerCommit() {
        return meanTicketsPerCommit;
    }

    /**
     * @param meanIssuesPerCommit the meanIssuesPerCommit to set
     */
    public void setMeanTicketsPerCommit(double meanTicketsPerCommit) {
        this.meanTicketsPerCommit = meanTicketsPerCommit;
    }

    /**
     * @return the meanTDItemsPerCommit
     */
    public double getMeanTDItemsPerCommit() {
        return meanTDItemsPerCommit;
    }

    /**
     * @param meanTDItemsPerCommit the meanTDItemsPerCommit to set
     */
    public void setMeanTDItemsPerCommit(double meanTDItemsPerCommit) {
        this.meanTDItemsPerCommit = meanTDItemsPerCommit;
    }

}