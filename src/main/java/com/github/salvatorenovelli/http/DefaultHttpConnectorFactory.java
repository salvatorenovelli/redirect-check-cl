package com.github.salvatorenovelli.http;


import com.github.salvatorenovelli.redirectcheck.domain.HttpRequest;
import com.github.salvatorenovelli.redirectcheck.domain.HttpRequestFactory;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;


import java.net.URI;


@Component
public class DefaultHttpConnectorFactory implements HttpRequestFactory {

    @Override
    public HttpRequest createRequest(URI httpURI) {
        return new DefaultHttpRequest(httpURI, HttpMethod.GET);
    }
}
