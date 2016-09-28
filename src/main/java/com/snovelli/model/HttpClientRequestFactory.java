package com.snovelli.model;

import com.snovelli.http.DefaultHttpRequest;
import com.snovelli.http.HttpRequest;

import java.net.URI;

/**
 * Created by Salvatore on 24/04/2016.
 */
public interface HttpClientRequestFactory {
    HttpRequest getConnector(URI httpURI);
}
