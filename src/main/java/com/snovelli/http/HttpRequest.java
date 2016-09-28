package com.snovelli.http;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by Salvatore on 24/04/2016.
 */
public interface HttpRequest {
    HttpResponse execute() throws IOException, URISyntaxException;
}
