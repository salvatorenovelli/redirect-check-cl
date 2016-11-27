package com.github.salvatorenovelli.model;


public class InvalidRedirectSpecification {
    private final int rowNum;
    private final String errorMessage;

    public InvalidRedirectSpecification(int rowNum, String errorMessage) {

        this.rowNum = rowNum;
        this.errorMessage = errorMessage;
    }

    public int getRowNum() {
        return rowNum;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
