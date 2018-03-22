package com.td.models;

import com.td.models.TechnicalDebtItem.CompositeKey;

public class CommitTD {

    private CompositeKey id;
    private TechnicalDebtPriority priority;
    private CodeLocation location;

    /**
     * @return the id
     */
    public CompositeKey getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(CompositeKey id) {
        this.id = id;
    }

    /**
     * @return the priority
     */
    public TechnicalDebtPriority getPriority() {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(TechnicalDebtPriority priority) {
        this.priority = priority;
    }

    /**
     * @return the location
     */
    public CodeLocation getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(CodeLocation location) {
        this.location = location;
    }

}