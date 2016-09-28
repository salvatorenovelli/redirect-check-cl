package com.snovelli.http;

import com.snovelli.model.HttpClientRequestFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.stereotype.Component;
import sun.java2d.pipe.SpanShapeRenderer;

import java.net.URI;

/**
 * Created by Salvatore on 24/04/2016.
 */

@Component
public class DefaultHttpConnectorFactory implements HttpClientRequestFactory {
    @Override
    public HttpRequest getConnector(URI httpURI) {
        return new DefaultHttpRequest(httpURI, HttpMethod.GET);
    }
}
