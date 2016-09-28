package com.snovelli.http;

import org.springframework.http.HttpStatus;

import java.net.URI;

/**
 * Created by Salvatore on 24/04/2016.
 */
public class HttpResponse {

    private final HttpStatus status;
    private final URI location;

    public HttpResponse(HttpStatus status, URI location) {
        this.status = status;
        this.location = location;
    }

    public URI getLocation() {
        return location;
    }

    public HttpStatus getStatusCode() {
        return status;
    }
}
