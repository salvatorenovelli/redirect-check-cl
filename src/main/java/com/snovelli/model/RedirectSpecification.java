package com.snovelli.model;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Salvatore on 24/04/2016.
 */
public final class RedirectSpecification {

    private final String sourceURI;
    private final String expectedDestination;


//    public RedirectSpecification(String sourceURI, String expectedDestinationURI) throws URISyntaxException {
//        this(new URI(sourceURI.trim()), new URI(expectedDestinationURI.trim()));
//    }

    public RedirectSpecification(String sourceURI, String expectedDestination) {
        this.sourceURI = sourceURI;
        this.expectedDestination = expectedDestination;
    }

    public String getSourceURI() {
        return sourceURI;
    }

    public String getExpectedDestination() {
        return expectedDestination;
    }


    @Override
    public String toString() {
        return "RedirectSpecification{" +
                "sourceURI=" + sourceURI +
                ", expectedDestination=" + expectedDestination +
                '}';
    }
}
