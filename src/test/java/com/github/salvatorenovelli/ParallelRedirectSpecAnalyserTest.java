package com.github.salvatorenovelli;

import com.github.salvatorenovelli.model.RedirectCheckResponse;
import com.github.salvatorenovelli.model.RedirectSpecification;
import com.github.salvatorenovelli.redirectcheck.domain.RedirectChainAnalyser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ParallelRedirectSpecAnalyserTest {


    private static final int NUM_WORKERS = 2;

    @Mock RedirectChainAnalyser redirectSpecAnalyser;
    ParallelRedirectSpecAnalyser sut = new ParallelRedirectSpecAnalyser(redirectSpecAnalyser, NUM_WORKERS);

    @Test
    public void testRunAnalysis() throws Exception {
        List<RedirectSpecification> redirectCheckSpecs = Collections.emptyList();
        List<RedirectCheckResponse> responseList = sut.runParallelAnalysis(redirectCheckSpecs);
        assertNotNull(responseList);
        assertThat(responseList, hasSize(0));
    }

}