package com.github.salvatorenovelli.model;

public final class RedirectSpecification {

    private final String sourceURI;
    private final String expectedDestination;
    private final int expectedStatusCode;
    private final int lineNumber;

    public RedirectSpecification(int lineNumber, String sourceURI, String expectedDestination, int expectedStatusCode) {
        this.lineNumber = lineNumber;
        this.sourceURI = sourceURI;
        this.expectedDestination = expectedDestination;
        this.expectedStatusCode = expectedStatusCode;
    }

    public String getSourceURI() {
        return sourceURI;
    }

    public String getExpectedDestination() {
        return expectedDestination;
    }

    public int getExpectedStatusCode() {
        return expectedStatusCode;
    }

    @Override
    public String toString() {
        return "RedirectSpecification{" +
                "sourceURI='" + sourceURI + '\'' +
                ", expectedDestination='" + expectedDestination + '\'' +
                ", expectedStatusCode=" + expectedStatusCode +
                '}';
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
