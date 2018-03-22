package com.td.models;

public class CodeLocation {

    private String fileName;
    private int lineNumber;

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
    public int getLine() {
        return lineNumber;
    }

    /**
     * @param line the line number to set
     */
    public void setLine(int line) {
        this.lineNumber = line;
    }

}