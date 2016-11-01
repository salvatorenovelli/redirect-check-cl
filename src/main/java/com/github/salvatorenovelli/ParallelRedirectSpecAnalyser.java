package com.github.salvatorenovelli;


import com.github.salvatorenovelli.model.RedirectCheckResponse;
import com.github.salvatorenovelli.model.RedirectSpecification;
import com.github.salvatorenovelli.redirectcheck.RedirectCheckResponseFactory;
import com.github.salvatorenovelli.redirectcheck.domain.RedirectChainAnalyser;
import com.github.salvatorenovelli.redirectcheck.model.RedirectChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class ParallelRedirectSpecAnalyser {

    private static final Logger logger = LoggerFactory.getLogger(ParallelRedirectSpecAnalyser.class);
    private final RedirectChainAnalyser analyser;
    private RedirectCheckResponseFactory redirectCheckResponseFactory;

    public ParallelRedirectSpecAnalyser(RedirectChainAnalyser redirectSpecAnalyser, RedirectCheckResponseFactory redirectCheckResponseFactory, int numWorkers) {
        this.analyser = redirectSpecAnalyser;
        this.redirectCheckResponseFactory = redirectCheckResponseFactory;
        setNumWorker(numWorkers);
    }

    public List<RedirectCheckResponse> runParallelAnalysis(List<RedirectSpecification> redirectCheckSpecs) {
        return redirectCheckSpecs.parallelStream().map(this::checkRedirect).collect(Collectors.toList());
    }

    private RedirectCheckResponse checkRedirect(RedirectSpecification spec) {
        logger.debug("Analysing " + spec);
        RedirectChain redirectChain = analyser.analyseRedirectChain(spec.getSourceURI());
        return redirectCheckResponseFactory.createResponse(spec, redirectChain);
    }


    private void setNumWorker(int numWorkers) {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", String.valueOf(numWorkers));
    }
}
