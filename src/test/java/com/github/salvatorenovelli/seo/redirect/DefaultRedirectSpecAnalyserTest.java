package com.github.salvatorenovelli.seo.redirect;

import com.github.salvatorenovelli.cli.ProgressMonitor;
import com.github.salvatorenovelli.model.RedirectCheckResponse;
import com.github.salvatorenovelli.model.RedirectSpecification;
import com.github.salvatorenovelli.redirectcheck.RedirectCheckResponseFactory;
import com.github.salvatorenovelli.redirectcheck.domain.RedirectChainAnalyser;
import com.github.salvatorenovelli.redirectcheck.model.RedirectChain;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultRedirectSpecAnalyserTest {


    RedirectSpecAnalyser sut;
    @Mock private RedirectChainAnalyser redirectSpecAnalyser;
    @Mock private RedirectCheckResponseFactory redirectCheckResponseFactory;
    @Mock private ProgressMonitor progressMonitor;
    private RedirectCheckResponse expectedResponse;

    @Before
    public void setUp() throws Exception {
        sut = new DefaultRedirectSpecAnalyser(redirectSpecAnalyser, redirectCheckResponseFactory, progressMonitor);
    }


    @Test
    public void validSpecResponseShouldBeAnalysed() throws Exception {
        RedirectCheckResponse response = sut.checkRedirect(createTestSpecWithIndex(0));
        assertThat(response, is(expectedResponse));
    }

    @Test
    public void progressMonitorShouldBeNotified() throws Exception {
        sut.checkRedirect(createTestSpecWithIndex(0));
        sut.checkRedirect(createTestSpecWithIndex(1));
        verify(progressMonitor, times(2)).tick();
    }

    public RedirectSpecification createTestSpecWithIndex(int i) {
        RedirectSpecification curSpec = RedirectSpecification.createValid(0, "http://www.example.com/" + i, "http://www.example.com/" + i + "/dst", 200);
        RedirectChain curResponse = new RedirectChain();
        RedirectCheckResponse curChainResponse = Mockito.mock(RedirectCheckResponse.class);
        when(redirectSpecAnalyser.analyseRedirectChain("http://www.example.com/" + i)).thenReturn(curResponse);
        when(redirectCheckResponseFactory.createResponse(curSpec, curResponse)).thenReturn(curChainResponse);
        expectedResponse = curChainResponse;
        return curSpec;
    }

}