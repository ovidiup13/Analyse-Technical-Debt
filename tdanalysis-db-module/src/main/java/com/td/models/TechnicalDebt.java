package com.td.models;

import java.util.List;

public class TechnicalDebt {

    private int totalCount;
    private int highCount;
    private int mediumCount;
    private int lowCount;

    private List<CommitTD> tdItems;

    /**
     * @return the totalCount
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * @param totalCount the totalCount to set
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * @return the highCount
     */
    public int getHighCount() {
        return highCount;
    }

    /**
     * @param highCount the highCount to set
     */
    public void setHighCount(int highCount) {
        this.highCount = highCount;
    }

    /**
     * @return the mediumCount
     */
    public int getMediumCount() {
        return mediumCount;
    }

    /**
     * @param mediumCount the mediumCount to set
     */
    public void setMediumCount(int mediumCount) {
        this.mediumCount = mediumCount;
    }

    /**
     * @return the lowCount
     */
    public int getLowCount() {
        return lowCount;
    }

    /**
     * @param lowCount the lowCount to set
     */
    public void setLowCount(int lowCount) {
        this.lowCount = lowCount;
    }

    /**
     * @return the tdItems
     */
    public List<CommitTD> getTdItems() {
        return tdItems;
    }

    /**
     * @param tdItems the tdItems to set
     */
    public void setTdItems(List<CommitTD> tdItems) {
        this.tdItems = tdItems;
    }

}