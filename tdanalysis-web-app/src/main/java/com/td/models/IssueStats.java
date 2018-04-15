package com.td.models;

public class IssueStats {

    private String issueKey;
    private int totalCommits;
    private String author;
    private String status;

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
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }
}