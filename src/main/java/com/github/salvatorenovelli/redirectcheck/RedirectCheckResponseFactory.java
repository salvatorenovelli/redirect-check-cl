package com.github.salvatorenovelli.redirectcheck;

import com.github.salvatorenovelli.model.RedirectCheckResponse;
import com.github.salvatorenovelli.model.RedirectSpecification;
import com.github.salvatorenovelli.redirectcheck.model.RedirectChain;

public class RedirectCheckResponseFactory {
    public RedirectCheckResponse createResponse(RedirectSpecification spec, RedirectChain redirectChain) {
        return new RedirectCheckResponse(spec, redirectChain);
    }
}