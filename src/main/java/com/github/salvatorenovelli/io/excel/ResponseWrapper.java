package com.github.salvatorenovelli.io.excel;

import com.github.salvatorenovelli.redirectcheck.model.RedirectCheckResponse;
import com.github.salvatorenovelli.redirectcheck.model.RedirectSpecification;

class ResponseWrapper {

    final String result;
    final String reason;
    final String expectedURI;
    final String actualURI;
    final String lastHTTPStatus;
    final String sourceURI;
    final int lineNumber;

    ResponseWrapper(RedirectCheckResponse cr) {
        this(cr.getRequestLineNumber(), cr.getSourceURI(),
                cr.getStatus().toString(),
                cr.getStatusMessage(),
                cr.getExpectedDestinationURI(),
                cr.getActualDestinationURI() != null ? cr.getActualDestinationURI() : "n/a",
                cr.getLastHttpStatus() != -1 ? "" + cr.getLastHttpStatus() : "n/a");
    }

    ResponseWrapper(RedirectSpecification specification) {
        this(specification.getLineNumber(),
                specification.getSourceURI(),
                RedirectCheckResponse.Status.FAILURE.toString(),
                specification.getErrorMessage(),
                specification.getExpectedDestination(),
                "n/a", "n/a");
    }

    private ResponseWrapper(int lineNumber, String sourceURI, String result, String reason, String expectedURI, String actualURI, String lastHTTPStatus) {
        this.lineNumber = lineNumber;
        this.sourceURI = sourceURI;
        this.result = result;
        this.reason = reason;
        this.expectedURI = expectedURI;
        this.actualURI = actualURI;
        this.lastHTTPStatus = lastHTTPStatus;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
