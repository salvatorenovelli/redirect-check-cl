package com.github.salvatorenovelli.model;


public class InvalidRedirectSpecification {
    private final int lineNumber;
    private final String errorMessage;


    public InvalidRedirectSpecification(int lineNumber, String errorMessage) {
        this.lineNumber = lineNumber;
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
