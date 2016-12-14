package com.github.salvatorenovelli.cli;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TextProgressBarTest {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(baos);
    TextProgressBar sut = new TextProgressBar(10, printStream, 10);

    @Test
    public void completitionPercentage10_test() throws Exception {
        sut.tick();
        sut.printCompletionPercentage();
        assertThat(printedProgressBar(), equalTo("|=---------| 10%\r"));
    }

    @Test
    public void completitionPercentage50_test() throws Exception {
        for (int i = 0; i < 5; i++) {
            sut.tick();
        }
        sut.printCompletionPercentage();
        assertThat(printedProgressBar(), equalTo("|=====-----| 50%\r"));
    }

    @Test
    public void completitionPercentage100_test() throws Exception {
        for (int i = 0; i <10; i++) {
            sut.tick();
        }
        sut.printCompletionPercentage();
        assertThat(printedProgressBar(), equalTo("|==========| 100%\r\n"));
    }

    private String printedProgressBar() {
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }
}