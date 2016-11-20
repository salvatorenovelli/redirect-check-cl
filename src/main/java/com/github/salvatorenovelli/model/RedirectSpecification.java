package com.github.salvatorenovelli.model;

public final class RedirectSpecification {

    private final String sourceURI;
    private final String expectedDestination;
    private final int expectedStatusCode;


    public RedirectSpecification(String sourceURI, String expectedDestination) {
        this(sourceURI,expectedDestination,200);
    }

    public RedirectSpecification(String sourceURI, String expectedDestination, int expectedStatusCode) {
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
}
