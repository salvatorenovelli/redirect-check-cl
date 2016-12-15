package com.github.salvatorenovelli.cli;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;

public class TextProgressBarTest {

    public static final int THREAD_SHOULD_BE_RUNNING_BEFORE_THIS_TIMEOUT = 5000;
    public static final int TOTAL_TICKS_REQUIRED = 10;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream printStream = spy(new PrintStream(baos));
    TextProgressBar sut = new TextProgressBar(TOTAL_TICKS_REQUIRED, printStream, 10);

    @Test
    public void completionPercentage10_test() throws Exception {
        sut.tick();
        sut.printCompletionPercentage();
        assertThat(printedProgressBar(), equalTo("|=---------| 10%\r"));
    }

    @Test
    public void completionPercentage50_test() throws Exception {
        for (int i = 0; i < 5; i++) {
            sut.tick();
        }
        sut.printCompletionPercentage();
        assertThat(printedProgressBar(), equalTo("|=====-----| 50%\r"));
    }

    @Test
    public void completitionPercentage100_test() throws Exception {
        for (int i = 0; i < 10; i++) {
            sut.tick();
        }
        sut.printCompletionPercentage();
        assertThat(printedProgressBar(), equalTo("|==========| 100%\r\n"));
    }

    @Test(timeout = THREAD_SHOULD_BE_RUNNING_BEFORE_THIS_TIMEOUT)
    public void shouldPrintTheStatusAutonomously() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        doAnswer(invocationOnMock -> {
            latch.countDown();
            return null;
        }).when(printStream).print(anyString());

        sut.startPrinting();
        latch.await();
    }

    private String printedProgressBar() {
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }
}