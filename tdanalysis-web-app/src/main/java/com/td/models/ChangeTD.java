package com.td.models;

public class ChangeTD {

    private ChangeSetStats changeSet;
    private TDStats technicalDebt;

    /**
     * @return the changeSet
     */
    public ChangeSetStats getChangeSet() {
        return changeSet;
    }

    /**
     * @param changeSet the changeSet to set
     */
    public void setChangeSet(ChangeSetStats changeSet) {
        this.changeSet = changeSet;
    }

    /**
     * @return the technicalDebt
     */
    public TDStats getTechnicalDebt() {
        return technicalDebt;
    }

    /**
     * @param technicalDebt the technicalDebt to set
     */
    public void setTechnicalDebt(TDStats technicalDebt) {
        this.technicalDebt = technicalDebt;
    }

}