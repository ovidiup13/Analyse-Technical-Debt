package com.td.models;

import java.time.LocalDateTime;
import java.util.List;

public class CommitModel {

    private String sha;
    private LocalDateTime timestamp;
    private String message;
    private String author;
    private DiffModel diff;
    private List<BugModel> bugs;

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
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the sha
     */
    public String getSha() {
        return sha;
    }

    /**
     * @param sha the sha to set
     */
    public void setSha(String sha) {
        this.sha = sha;
    }

    /**
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public DiffModel getDiff() {
        return diff;
    }

    public void setDiff(DiffModel diff) {
        this.diff = diff;
    }

    public List<BugModel> getBugs() {
        return bugs;
    }

    public void setBugs(List<BugModel> bugs) {
        this.bugs = bugs;
    }
}