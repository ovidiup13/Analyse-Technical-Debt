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

    @Override
    public boolean equals(Object o) {
        CommitTD target = (CommitTD) o;
        return this.getId().equals(target.getId()) && this.getPriority().equals(target.getPriority())
                && this.getLocation().equals(target.getLocation());
    }

    public static class CodeLocation {

        private String fileName;
        private String lineNumber;

        public CodeLocation() {
        }

        public CodeLocation(String fileName, String number) {
            this.fileName = fileName;
            this.lineNumber = number;
        }

        /**
         * @return the file name
         */
        public String getFileName() {
            return fileName;
        }

        /**
         * @param fileName the file name to set
         */
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        /**
         * @return the line number
         */
        public String getLine() {
            return lineNumber;
        }

        /**
         * @param line the line number to set
         */
        public void setLine(String line) {
            this.lineNumber = line;
        }

        @Override
        public boolean equals(Object o) {
            CodeLocation other = (CodeLocation) o;
            if (this.getLine() == null) {
                if (other.getLine() == null) {
                    return this.getFileName().equals(other.getFileName());
                } else {
                    return false;
                }
            } else {
                return this.getFileName().equals(other.getFileName()) && this.getLine().equals(other.getLine());
            }
        }

    }

}