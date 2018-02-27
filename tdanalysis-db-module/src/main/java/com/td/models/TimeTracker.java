package com.td.models;

public class TimeTracker {

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