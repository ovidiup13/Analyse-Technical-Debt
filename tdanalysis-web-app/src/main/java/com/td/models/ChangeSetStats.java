package com.td.models;

public class ChangeSetStats {
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