package com.github.salvatorenovelli.io;

import com.github.salvatorenovelli.model.InvalidRedirectSpecification;
import com.github.salvatorenovelli.model.RedirectSpecification;

public interface ParsedSpecificationHandler {
    void handleValidSpec(RedirectSpecification spec);

    void handleInvalidSpec(InvalidRedirectSpecification spec);
}
