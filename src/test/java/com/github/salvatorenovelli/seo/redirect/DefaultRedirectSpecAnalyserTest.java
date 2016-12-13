package com.github.salvatorenovelli.seo.redirect;

import com.github.salvatorenovelli.cli.ProgressMonitor;
import com.github.salvatorenovelli.model.RedirectCheckResponse;
import com.github.salvatorenovelli.model.RedirectSpecification;
import com.github.salvatorenovelli.redirectcheck.RedirectCheckResponseFactory;
import com.github.salvatorenovelli.redirectcheck.domain.RedirectChainAnalyser;
import com.github.salvatorenovelli.redirectcheck.model.RedirectChain;
import com.github.salvatorenovelli.redirectcheck.model.RedirectChainElement;
import com.github.salvatorenovelli.redirectcheck.model.exception.RedirectLoopException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultRedirectSpecAnalyserTest {

    private static final RedirectSpecification INVALID_SPEC = RedirectSpecification.createInvalid(0, "Invalid spec test");

    DefaultRedirectSpecAnalyser sut;

    @Mock private RedirectChainAnalyser redirectSpecAnalyser;
    @Mock private ProgressMonitor progressMonitor;
    @Spy private RedirectCheckResponseFactory redirectCheckResponseFactory = new RedirectCheckResponseFactory();
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
    public void invalidSpecShouldNotBeAnalysed() throws Exception {
        final RedirectCheckResponse response = sut.checkRedirect(INVALID_SPEC);

        assertNotNull(response);
        assertThat(response.getStatus(), is(RedirectCheckResponse.Status.FAILURE));
        assertThat(response.getRequestLineNumber(), is(INVALID_SPEC.getLineNumber()));
        assertThat(response.getStatusMessage(), is(INVALID_SPEC.getErrorMessage()));
    }

    @Test
    public void progressShouldBeNotifiedAlsoInCaseOfInvalidSpecs() throws Exception {
        sut.checkRedirect(INVALID_SPEC);
        verify(progressMonitor).tick();
    }

    @Test
    public void progressMonitorShouldBeNotified() throws Exception {
        sut.checkRedirect(createTestSpecWithIndex(0));
        sut.checkRedirect(createTestSpecWithIndex(1));
        verify(progressMonitor, times(2)).tick();
    }


    public RedirectSpecification createTestSpecWithIndex(int i) throws RedirectLoopException, URISyntaxException {
        final String expectedDestination = "http://www.example.com/" + i + "/dst";

        RedirectSpecification curSpec = RedirectSpecification.createValid(0, "http://www.example.com/" + i, expectedDestination, 200);

        RedirectChain redirectChain = new RedirectChain();
        redirectChain.addElement(new RedirectChainElement(200, new URI(expectedDestination)));

        RedirectCheckResponse curChainResponse = RedirectCheckResponse.createResponse(curSpec, redirectChain);

        when(redirectSpecAnalyser.analyseRedirectChain("http://www.example.com/" + i)).thenReturn(redirectChain);
        when(redirectCheckResponseFactory.createResponse(curSpec, redirectChain)).thenReturn(curChainResponse);
        expectedResponse = curChainResponse;
        return curSpec;
    }

}