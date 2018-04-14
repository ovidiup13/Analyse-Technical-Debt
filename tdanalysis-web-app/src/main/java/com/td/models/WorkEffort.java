package com.td.models;

public class WorkEffort {

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