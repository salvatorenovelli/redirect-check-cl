package com.github.salvatorenovelli.model;




import com.github.salvatorenovelli.redirectcheck.domain.HttpRequest;

import java.net.URI;

public interface HttpClientRequestFactory {
    HttpRequest getConnector(URI httpURI);
}
