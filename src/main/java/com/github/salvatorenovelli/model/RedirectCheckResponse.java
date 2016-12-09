package com.github.salvatorenovelli.model;

import com.github.salvatorenovelli.redirectcheck.model.RedirectChain;
import com.github.salvatorenovelli.redirectcheck.model.RedirectChainElement;

import org.springframework.http.HttpStatus;

import java.util.List;

public class RedirectCheckResponse {


    private final Status status;
    private final String statusMessage;
    private final String sourceURI;
    private final String expectedDestinationURI;
    private final int requestLineNumber;
    private String actualDestinationURI;
    private int lastHttpStatus = -1;

    private List<RedirectChainElement> redirectChain;

    private int numberOfRedirects;

    public RedirectCheckResponse(RedirectSpecification request, RedirectChain redirectChain) {

        this.requestLineNumber = request.getLineNumber();
        this.sourceURI = request.getSourceURI();
        this.redirectChain = redirectChain.getElements();
        this.expectedDestinationURI = request.getExpectedDestination();


        if (redirectChain.isFailed()) {
            status = Status.FAILURE;
            statusMessage = redirectChain.getStatus();
            return;
        }


        this.actualDestinationURI = redirectChain.getDestinationURI();
        this.lastHttpStatus = redirectChain.getLastHttpStatus();
        this.numberOfRedirects = redirectChain.getNumOfRedirect();


        if (!request.getExpectedDestination().equals(actualDestinationURI)) {
            status = Status.FAILURE;
            statusMessage = "Destination doesn't match";
            return;
        }

        if (lastHttpStatus != HttpStatus.OK.value()) {
            status = Status.FAILURE;
            statusMessage = "HTTP Status is not 200 (OK)";
            return;
        }


        status = Status.SUCCESS;
        statusMessage = "";


    }


    public String getActualDestinationURI() {
        return actualDestinationURI;
    }

    public int getNumberOfRedirects() {
        return numberOfRedirects;
    }

    public int getLastHttpStatus() {
        return lastHttpStatus;
    }

    public Status getStatus() {
        return status;
    }

    public List<RedirectChainElement> getRedirectChain() {
        return redirectChain;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getExpectedDestinationURI() {
        return expectedDestinationURI;
    }

    @Override
    public String toString() {
        return "RedirectCheckResponse{" +
                "status=" + status +
                ", statusMessage='" + statusMessage + "'" +
                '}';
    }

    public String getSourceURI() {
        return sourceURI;
    }

    public int getRequestLineNumber() {
        return requestLineNumber;
    }

    public enum Status {
        SUCCESS, FAILURE
    }
}
