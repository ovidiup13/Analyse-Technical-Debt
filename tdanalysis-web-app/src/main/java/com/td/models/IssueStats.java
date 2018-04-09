package com.td.models;

public class IssueStats {

    private String issueKey;
    private WorkEffort workEffort;
    private long technicalDebt;
    private int totalCommits;
    private int totalIssues;
    private String author;
    private TDStats tdStats;
    private ChangeSetStats changeSetStats;

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

    /**
     * @return the tdStats
     */
    public TDStats getTdStats() {
        return tdStats;
    }

    /**
     * @param tdStats the tdStats to set
     */
    public void setTdStats(TDStats tdStats) {
        this.tdStats = tdStats;
    }

    public static class ChangeSetStats {
        private int totalChanges;

        private int additions;
        private int deletions;
        private int modifications;

        /**
         * @return the totalChanges
         */
        public int getTotalChanges() {
            return totalChanges;
        }

        /**
         * @param totalChanges the totalChanges to set
         */
        public void setTotalChanges(int totalChanges) {
            this.totalChanges = totalChanges;
        }

        /**
         * @return the additions
         */
        public int getAdditions() {
            return additions;
        }

        /**
         * @param additions the additions to set
         */
        public void setAdditions(int additions) {
            this.additions = additions;
        }

        /**
         * @return the deletions
         */
        public int getDeletions() {
            return deletions;
        }

        /**
         * @param deletions the deletions to set
         */
        public void setDeletions(int deletions) {
            this.deletions = deletions;
        }

        /**
         * @return the modifications
         */
        public int getModifications() {
            return modifications;
        }

        /**
         * @param modifications the modifications to set
         */
        public void setModifications(int modifications) {
            this.modifications = modifications;
        }
    }

    public static class TDStats {

        private int added;
        private int removed;
        private int total;
        private int high;
        private int medium;
        private int low;

        /**
         * @return the added
         */
        public int getAdded() {
            return added;
        }

        /**
         * @param added the added to set
         */
        public void setAdded(int added) {
            this.added = added;
        }

        /**
         * @return the removed
         */
        public int getRemoved() {
            return removed;
        }

        /**
         * @param removed the removed to set
         */
        public void setRemoved(int removed) {
            this.removed = removed;
        }

        /**
         * @return the totalPain
         */
        public int getTotalPain() {
            return total;
        }

        /**
         * @param totalPain the totalPain to set
         */
        public void setTotalPain(int totalPain) {
            this.total = totalPain;
        }

        /**
         * @return the high
         */
        public int getHigh() {
            return high;
        }

        /**
         * @param high the high to set
         */
        public void setHigh(int high) {
            this.high = high;
        }

        /**
         * @return the medium
         */
        public int getMedium() {
            return medium;
        }

        /**
         * @param medium the medium to set
         */
        public void setMedium(int medium) {
            this.medium = medium;
        }

        /**
         * @return the low
         */
        public int getLow() {
            return low;
        }

        /**
         * @param low the low to set
         */
        public void setLow(int low) {
            this.low = low;
        }

    }

    public static class WorkEffort {

        private double hours;

        public WorkEffort() {
        }

        public WorkEffort(double hours) {
            this.hours = hours;
        }

        /**
         * @return the hours
         */
        public double getHours() {
            return hours;
        }

        /**
         * @param hours the hours to set
         */
        public void setHours(double hours) {
            this.hours = hours;
        }
    }

    /**
     * @return the changeSetStats
     */
    public ChangeSetStats getChangeSetStats() {
        return changeSetStats;
    }

    /**
     * @param changeSetStats the changeSetStats to set
     */
    public void setChangeSetStats(ChangeSetStats changeSetStats) {
        this.changeSetStats = changeSetStats;
    }

}