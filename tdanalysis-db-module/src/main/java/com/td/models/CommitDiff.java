package com.td.models;

import java.util.ArrayList;
import java.util.List;

public class CommitDiff {

    private int totalChanges;

    private int additions;
    private int deletions;
    private int modifications;

    private List<String> additionSet;
    private List<String> deletionSet;
    private List<String> modificationSet;

    public CommitDiff() {
        this.additionSet = new ArrayList<>();
        this.deletionSet = new ArrayList<>();
        this.modificationSet = new ArrayList<>();
    }

    public int getTotalChanges() {
        return totalChanges;
    }

    public void setTotalChanges(int totalChanges) {
        this.totalChanges = totalChanges;
    }

    public int getAdditions() {
        return additions;
    }

    public void setAdditions(int additions) {
        this.additions = additions;
    }

    public int getDeletions() {
        return deletions;
    }

    public void setDeletions(int deletions) {
        this.deletions = deletions;
    }

    public int getModifications() {
        return modifications;
    }

    public void setModifications(int modifications) {
        this.modifications = modifications;
    }

    public List<String> getAdditionSet() {
        return additionSet;
    }

    public void setAdditionSet(List<String> additionSet) {
        this.additionSet = additionSet;
    }

    public List<String> getDeletionSet() {
        return deletionSet;
    }

    public void setDeletionSet(List<String> deletionSet) {
        this.deletionSet = deletionSet;
    }

    public List<String> getModificationSet() {
        return modificationSet;
    }

    public void setModificationSet(List<String> modificationSet) {
        this.modificationSet = modificationSet;
    }

    public void add(String addition) {
        this.additionSet.add(addition);
        this.additions++;
    }

    public void modify(String modification) {
        this.modificationSet.add(modification);
        this.modifications++;
    }

    public void delete(String deletion) {
        this.deletionSet.add(deletion);
        this.deletions++;
    }
}
