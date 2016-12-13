package com.github.salvatorenovelli.seo.redirect;

import com.github.salvatorenovelli.model.RedirectCheckResponse;
import com.github.salvatorenovelli.model.RedirectSpecification;

public interface RedirectSpecAnalyser {
    RedirectCheckResponse checkRedirect(RedirectSpecification spec);
}
