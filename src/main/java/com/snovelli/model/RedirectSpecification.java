package com.snovelli.model;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Salvatore on 24/04/2016.
 */
public final class RedirectSpecification {

    private final URI sourceURI;
    private final URI expectedDestination;


    public RedirectSpecification(String sourceURI, String expectedDestinationURI) throws URISyntaxException {
        this(new URI(sourceURI.trim()), new URI(expectedDestinationURI.trim()));
    }

    public RedirectSpecification(URI sourceURI, URI expectedDestination) {
        this.sourceURI = sourceURI;
        this.expectedDestination = expectedDestination;
    }

    public URI getSourceURI() {
        return sourceURI;
    }

    public URI getExpectedDestination() {
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
