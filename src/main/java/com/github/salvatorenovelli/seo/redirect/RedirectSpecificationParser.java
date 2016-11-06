package com.github.salvatorenovelli.seo.redirect;

import com.github.salvatorenovelli.model.RedirectSpecification;

import java.io.IOException;
import java.util.List;

public interface RedirectSpecificationParser {
    List<RedirectSpecification> parse() throws IOException;
}
