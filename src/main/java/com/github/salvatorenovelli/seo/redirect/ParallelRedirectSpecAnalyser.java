package com.github.salvatorenovelli.seo.redirect;


import com.github.salvatorenovelli.cli.ProgressMonitor;
import com.github.salvatorenovelli.cli.TextProgressBar;
import com.github.salvatorenovelli.model.RedirectCheckResponse;
import com.github.salvatorenovelli.model.RedirectSpecification;
import com.github.salvatorenovelli.redirectcheck.RedirectCheckResponseFactory;
import com.github.salvatorenovelli.redirectcheck.domain.RedirectChainAnalyser;
import com.github.salvatorenovelli.redirectcheck.model.RedirectChain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ParallelRedirectSpecAnalyser {

    private static final Logger logger = LoggerFactory.getLogger(ParallelRedirectSpecAnalyser.class);
    private final RedirectChainAnalyser analyser;
    private final int numWorkers;
    private RedirectCheckResponseFactory redirectCheckResponseFactory;
    private ProgressMonitor progressMonitor = () -> {};

    public ParallelRedirectSpecAnalyser(RedirectChainAnalyser redirectChainAnalyser, RedirectCheckResponseFactory redirectCheckResponseFactory, int numWorkers) {
        this.analyser = redirectChainAnalyser;
        this.redirectCheckResponseFactory = redirectCheckResponseFactory;
        this.numWorkers = numWorkers;
    }

    public List<RedirectCheckResponse> runParallelAnalysis(List<RedirectSpecification> redirectCheckSpecs) throws ExecutionException, InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(numWorkers);
        try {
            List<CompletableFuture<RedirectCheckResponse>> collect = redirectCheckSpecs.stream()
                    .map(spec -> CompletableFuture.supplyAsync(() -> checkRedirect(spec), executorService))
                    .collect(Collectors.toList());

            return collect.stream().map(CompletableFuture::join).collect(Collectors.toList());
        } finally {
            executorService.shutdownNow();
        }
    }


    private RedirectCheckResponse checkRedirect(RedirectSpecification spec) {
        logger.debug("Analysing " + spec);
        RedirectChain redirectChain = analyser.analyseRedirectChain(spec.getSourceURI());
        progressMonitor.tick();
        return redirectCheckResponseFactory.createResponse(spec, redirectChain);
    }

    public void setProgressMonitor(TextProgressBar progressMonitor) {
        this.progressMonitor = progressMonitor;
    }
}

