package com.td.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "commits")
public class CommitModel {

    @Id
    private String sha;

    @Indexed
    private String repositoryId;

    private List<String> issueIds;

    @Indexed
    private LocalDateTime timestamp;

    private String message;
    private String author;
    private BuildStatus buildStatus;
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
    @JsonSerialize(using = LocalDateTimeSerializer.class)
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

    public String getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId;
    }

    public BuildStatus getBuildStatus() {
        return buildStatus;
    }

    public void setBuildStatus(BuildStatus buildStatus) {
        this.buildStatus = buildStatus;
    }

    /**
     * @return the issueIds
     */
    public List<String> getIssueIds() {
        return issueIds;
    }

    /**
     * @param issueIds the issueIds to set
     */
    public void setIssueIds(List<String> issueIds) {
        this.issueIds = issueIds;
    }
}