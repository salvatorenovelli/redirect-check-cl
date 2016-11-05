package com.github.salvatorenovelli;

import com.github.salvatorenovelli.cli.ProgressMonitor;
import com.github.salvatorenovelli.model.RedirectCheckResponse;
import com.github.salvatorenovelli.model.RedirectSpecification;
import com.github.salvatorenovelli.redirectcheck.RedirectCheckResponseFactory;
import com.github.salvatorenovelli.redirectcheck.domain.RedirectChainAnalyser;
import com.github.salvatorenovelli.redirectcheck.model.RedirectChain;
import com.github.salvatorenovelli.seo.redirect.ParallelRedirectSpecAnalyser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ParallelRedirectSpecAnalyserTest {

    private static final List<RedirectSpecification> EMPTY_SPECS = Collections.emptyList();
    private static final int NUM_WORKERS = 10;
    ParallelRedirectSpecAnalyser sut;
    @Mock private RedirectChainAnalyser redirectSpecAnalyser;
    @Mock private RedirectCheckResponseFactory redirectCheckResponseFactory;
    @Mock private RedirectCheckResponse REDIRECT_CHAIN_RESPONSE;
    private List<RedirectCheckResponse> expectedResponses = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        sut = new ParallelRedirectSpecAnalyser(redirectSpecAnalyser, redirectCheckResponseFactory, NUM_WORKERS);
        when(redirectCheckResponseFactory.createResponse(any(), any())).thenReturn(Mockito.mock(RedirectCheckResponse.class));
    }

    @Test
    public void testRunAnalysis() throws Exception {
        List<RedirectCheckResponse> responseList = sut.runParallelAnalysis(EMPTY_SPECS);
        assertNotNull(responseList);
        assertThat(responseList, hasSize(0));
    }

    @Test
    public void analysisShouldWrapResponse() throws Exception {
        List<RedirectCheckResponse> redirectCheckResponses = sut.runParallelAnalysis(createTestSpecWithSize(1));
        assertThat(redirectCheckResponses, hasSize(1));
        assertThat(redirectCheckResponses.get(0), is(expectedResponses.get(0)));
    }

    @Test
    public void multipleSpecShouldBeAllProcessed() throws Exception {
        List<RedirectCheckResponse> redirectCheckResponses = sut.runParallelAnalysis(createTestSpecWithSize(10));
        assertThat(redirectCheckResponses, hasSize(10));

        for (int i = 0; i < redirectCheckResponses.size(); i++) {
            assertThat(redirectCheckResponses.get(i), is(expectedResponses.get(i)));
        }
    }

    @Test
    public void completedRedirectCheckShouldTriggerProgress() throws Exception {
        ProgressMonitor progressMonitor = Mockito.mock(ProgressMonitor.class);
        sut.setProgressMonitor(progressMonitor);

        sut.runParallelAnalysis(createTestSpecWithSize(10));

        verify(progressMonitor, times(10)).tick();
    }

    @Test(timeout = 5000)
    public void multipleSpecsShouldBeAnalysedInParallel() throws Exception {
        int GREATER_THAN_SYSTEM_DEFAULT_NUM_WORKERS = Runtime.getRuntime().availableProcessors() + 5;

        CountDownLatch latch = new CountDownLatch(GREATER_THAN_SYSTEM_DEFAULT_NUM_WORKERS);

        sut = new ParallelRedirectSpecAnalyser(s -> {
            awaitForOtherThreads(latch);
            return new RedirectChain();
        }, redirectCheckResponseFactory, GREATER_THAN_SYSTEM_DEFAULT_NUM_WORKERS);

        sut.runParallelAnalysis(createTestSpecWithSize(GREATER_THAN_SYSTEM_DEFAULT_NUM_WORKERS));

    }

    private void awaitForOtherThreads(CountDownLatch latch) {
        try {
            latch.countDown();
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<RedirectSpecification> createTestSpecWithSize(int size) {

        List<RedirectSpecification> spec = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            RedirectSpecification curSpec = new RedirectSpecification("http://www.example.com/" + i, "http://www.example.com/" + i + "/dst");

            RedirectChain curResponse = new RedirectChain();
            RedirectCheckResponse curChainResponse = Mockito.mock(RedirectCheckResponse.class);

            expectedResponses.add(curChainResponse);
            when(redirectSpecAnalyser.analyseRedirectChain("http://www.example.com/" + i)).thenReturn(curResponse);
            when(redirectCheckResponseFactory.createResponse(curSpec, curResponse)).thenReturn(curChainResponse);

            spec.add(curSpec);
        }

        return spec;

    }
}