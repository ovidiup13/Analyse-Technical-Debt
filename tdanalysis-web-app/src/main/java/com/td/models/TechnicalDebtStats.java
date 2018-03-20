package com.td.models;

public class TechnicalDebtStats {

    private double mean;
    private double standardDeviation;

    /**
     * @return the mean
     */
    public double getMean() {
        return mean;
    }

    /**
     * @param mean the mean to set
     */
    public void setMean(double mean) {
        this.mean = mean;
    }

    /**
     * @return the standardDeviation
     */
    public double getStandardDeviation() {
        return standardDeviation;
    }

    /**
     * @param standardDeviation the standardDeviation to set
     */
    public void setStandardDeviation(double standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

}