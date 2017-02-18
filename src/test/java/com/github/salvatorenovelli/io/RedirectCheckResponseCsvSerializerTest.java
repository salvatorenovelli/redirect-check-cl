package com.github.salvatorenovelli.io;

import com.github.salvatorenovelli.model.RedirectCheckResponse;
import com.github.salvatorenovelli.model.RedirectSpecification;
import com.github.salvatorenovelli.redirectcheck.model.RedirectChain;
import com.github.salvatorenovelli.redirectcheck.model.RedirectChainElement;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(MockitoJUnitRunner.class)
public class RedirectCheckResponseCsvSerializerTest {

    @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();
    RedirectCheckResponseCsvSerializer sut;
    private File file;

    @Before
    public void setUp() throws Exception {
        file = temporaryFolder.newFile();
        sut = new RedirectCheckResponseCsvSerializer(file.getAbsolutePath());
    }

    @Test
    public void shouldUnescapeTheLatestLocation() throws Exception {

        RedirectSpecification request = RedirectSpecification.createValid(0, "http://example.com", "http://www.example.com?test=%C3%BC", 200);

        RedirectChain redirectChain = new RedirectChain();
        redirectChain.addElement(new RedirectChainElement(200, new URI("http://www.example.com?test=%C3%BC")));

        RedirectCheckResponse response = RedirectCheckResponse.createResponse(request, redirectChain);

        sut.addResponses(Collections.singletonList(response));
        sut.write();

        assertThat(actualUriFieldIn(getLine(0)), is("http://www.example.com?test=ü"));

    }

    private String actualUriFieldIn(String line) {
        return line.split(",")[5];
    }


    private String getLine(int lineNumber) throws IOException {
        return Files.readAllLines(file.toPath()).get(lineNumber + 1);
    }
}