package com.github.salvatorenovelli;

import com.github.salvatorenovelli.model.RedirectCheckResponse;
import com.github.salvatorenovelli.model.RedirectSpecification;
import com.github.salvatorenovelli.redirectcheck.RedirectCheckResponseFactory;
import com.github.salvatorenovelli.redirectcheck.domain.RedirectChainAnalyser;
import com.github.salvatorenovelli.redirectcheck.model.RedirectChain;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ParallelRedirectSpecAnalyserTest {


    public static final RedirectChain REDIRECT_CHAIN = new RedirectChain();
    private static final String EXAMPLE_URL = "http://www.example.com";
    private static final List<RedirectSpecification> REDIRECT_CHECK_SPECS = Arrays.asList(new RedirectSpecification(EXAMPLE_URL, "http://www.example.com/"));
    private static final List<RedirectSpecification> EMPTY_SPECS = Collections.emptyList();
    private static final int NUM_WORKERS = 2;
    ParallelRedirectSpecAnalyser sut;
    @Mock private RedirectChainAnalyser redirectSpecAnalyser;
    @Mock private RedirectCheckResponseFactory redirectCheckResponseFactory;
    @Mock private RedirectCheckResponse REDIRECT_CHAIN_RESPONSE;

    @Before
    public void setUp() throws Exception {
        sut = new ParallelRedirectSpecAnalyser(redirectSpecAnalyser, redirectCheckResponseFactory, NUM_WORKERS);

        when(redirectSpecAnalyser.analyseRedirectChain(EXAMPLE_URL)).thenReturn(REDIRECT_CHAIN);
        for (RedirectSpecification curSpec : REDIRECT_CHECK_SPECS) {
            when(redirectCheckResponseFactory.createResponse(curSpec, REDIRECT_CHAIN)).thenReturn(REDIRECT_CHAIN_RESPONSE);
        }

    }

    @Test
    public void testRunAnalysis() throws Exception {
        List<RedirectCheckResponse> responseList = sut.runParallelAnalysis(EMPTY_SPECS);
        assertNotNull(responseList);
        assertThat(responseList, hasSize(0));
    }


    @Test
    public void analysisShouldWrapResponse() throws Exception {
        List<RedirectCheckResponse> redirectCheckResponses = sut.runParallelAnalysis(REDIRECT_CHECK_SPECS);
        assertThat(redirectCheckResponses, hasSize(1));
        assertThat(redirectCheckResponses.get(0), is(REDIRECT_CHAIN_RESPONSE));
    }
}