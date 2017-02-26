package com.github.salvatorenovelli.http;


import com.github.salvatorenovelli.redirectcheck.domain.HttpRequest;
import com.github.salvatorenovelli.redirectcheck.domain.HttpRequestFactory;
import com.github.salvatorenovelli.redirectcheck.http.DefaultConnectionFactory;
import com.github.salvatorenovelli.redirectcheck.http.HttpGetRequest;
import org.springframework.stereotype.Component;

import java.net.URI;


@Component
public class DefaultHttpConnectorFactory implements HttpRequestFactory {

    @Override
    public HttpRequest createRequest(URI httpURI) {
        return new HttpGetRequest(httpURI, new DefaultConnectionFactory());
    }
}
