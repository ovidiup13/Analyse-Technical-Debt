package com.td.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "issues")
public class IssueModel {

    @Id
    private String issueId;

    @Indexed
    private String repositoryId;

    private String issueKey;
    private String type;
    private String summary;
    private String description;
    private String assignee;
    private double storyPoints;
    private String priority;
    private String status;
    private LocalDateTime created;
    private LocalDateTime closed;
    private LocalDateTime due;

    private Set<String> labels;

    private TimeTracker timeTracker;

    private List<Transition> transitions;

    /**
     * @return the issueId
     */
    public String getIssueId() {
        return issueId;
    }

    /**
     * @param issueId the issueId to set
     */
    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    /**
     * @return the repositoryId
     */
    public String getRepositoryId() {
        return repositoryId;
    }

    /**
     * @param repositoryId the repositoryId to set
     */
    public void setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId;
    }

    /**
     * @return the summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * @param summary the summary to set
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the assignee
     */
    public String getAssignee() {
        return assignee;
    }

    /**
     * @param assignee the assignee to set
     */
    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    /**
     * @return the storyPoints
     */
    public double getStoryPoints() {
        return storyPoints;
    }

    /**
     * @param storyPoints the storyPoints to set
     */
    public void setStoryPoints(double storyPoints) {
        this.storyPoints = storyPoints;
    }

    /**
     * @return the priority
     */
    public String getPriority() {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(String priority) {
        this.priority = priority;
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

    /**
     * @return the created
     */
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime getCreated() {
        return created;
    }

    /**
     * @param created the created to set
     */
    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    /**
     * @return the closed
     */
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime getClosed() {
        return closed;
    }

    /**
     * @param closed the closed to set
     */
    public void setClosed(LocalDateTime closed) {
        this.closed = closed;
    }

    /**
     * @return the due
     */
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime getDue() {
        return due;
    }

    /**
     * @param due the due to set
     */
    public void setDue(LocalDateTime due) {
        this.due = due;
    }

    /**
     * @return the timeTracker
     */
    public TimeTracker getTimeTracker() {
        return timeTracker;
    }

    /**
     * @param timeTracker the timeTracker to set
     */
    public void setTimeTracker(TimeTracker timeTracker) {
        this.timeTracker = timeTracker;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
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
     * @return the labels
     */
    public Set<String> getLabels() {
        return labels;
    }

    /**
     * @param labels the labels to set
     */
    public void setLabels(Set<String> labels) {
        this.labels = labels;
    }

    /**
    * @return the transitions
    */
    public List<Transition> getTransitions() {
        return transitions;
    }

    /**
     * @param transitions the transitions to set
     */
    public void setTransitions(List<Transition> transitions) {
        this.transitions = transitions;
    }

    /**
     * Time tracker class.
     */
    public static class TimeTracker {

        private int estimate;
        private int remaining;
        private int logged;

        /**
         * @return the estimate
         */
        public int getEstimate() {
            return estimate;
        }

        /**
         * @param estimate the estimate to set
         */
        public void setEstimate(int estimate) {
            this.estimate = estimate;
        }

        /**
         * @return the remaining
         */
        public int getRemaining() {
            return remaining;
        }

        /**
         * @param remaining the remaining to set
         */
        public void setRemaining(int remaining) {
            this.remaining = remaining;
        }

        /**
         * @return the logged
         */
        public int getLogged() {
            return logged;
        }

        /**
         * @param logged the logged to set
         */
        public void setLogged(int logged) {
            this.logged = logged;
        }
    }

    /**
     * Transition class.
     */
    public static class Transition {
        private String field;
        private String from;
        private String to;
        private String author;
        private LocalDateTime created;

        /**
         * @return the field
         */
        public String getField() {
            return field;
        }

        /**
         * @param field the field to set
         */
        public void setField(String field) {
            this.field = field;
        }

        /**
         * @return the from
         */
        public String getFrom() {
            return from;
        }

        /**
         * @param from the from to set
         */
        public void setFrom(String from) {
            this.from = from;
        }

        /**
         * @return the to
         */
        public String getTo() {
            return to;
        }

        /**
         * @param to the to to set
         */
        public void setTo(String to) {
            this.to = to;
        }

        /**
         * @return the created
         */
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        public LocalDateTime getCreated() {
            return created;
        }

        /**
         * @param created the created to set
         */
        public void setCreated(LocalDateTime created) {
            this.created = created;
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
    }
}