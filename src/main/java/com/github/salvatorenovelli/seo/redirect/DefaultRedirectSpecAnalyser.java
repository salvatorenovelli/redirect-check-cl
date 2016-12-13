package com.github.salvatorenovelli.seo.redirect;

import com.github.salvatorenovelli.cli.ProgressMonitor;
import com.github.salvatorenovelli.model.RedirectCheckResponse;
import com.github.salvatorenovelli.model.RedirectSpecification;
import com.github.salvatorenovelli.redirectcheck.RedirectCheckResponseFactory;
import com.github.salvatorenovelli.redirectcheck.domain.RedirectChainAnalyser;
import com.github.salvatorenovelli.redirectcheck.model.RedirectChain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultRedirectSpecAnalyser implements RedirectSpecAnalyser {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRedirectSpecAnalyser.class);
    private final RedirectChainAnalyser analyser;
    private final ProgressMonitor progressMonitor;
    private RedirectCheckResponseFactory redirectCheckResponseFactory;

    public DefaultRedirectSpecAnalyser(RedirectChainAnalyser redirectChainAnalyser,
                                       RedirectCheckResponseFactory redirectCheckResponseFactory,
                                       ProgressMonitor progressMonitor) {
        this.analyser = redirectChainAnalyser;
        this.redirectCheckResponseFactory = redirectCheckResponseFactory;
        this.progressMonitor = progressMonitor;
    }

    @Override
    public RedirectCheckResponse checkRedirect(RedirectSpecification spec) {

        try {
            logger.debug("Analysing " + spec);
            if (spec.isValid()) {
                RedirectChain redirectChain = analyser.analyseRedirectChain(spec.getSourceURI());
                return redirectCheckResponseFactory.createResponse(spec, redirectChain);
            } else {
                return redirectCheckResponseFactory.createResponseForInvalidSpec(spec);
            }
        } finally {
            progressMonitor.tick();
        }

    }
}
