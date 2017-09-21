package com.github.salvatorenovelli.cli;

import com.github.salvatorenovelli.redirectcheck.cli.ProgressMonitor;
import net.jcip.annotations.ThreadSafe;

import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
public class TextProgressBar implements Runnable, ProgressMonitor {

    public static final int DEFAULT_BAR_WIDTH = 50;
    public final int barWith;

    private final int totalTicksRequired;
    private final Thread thread = new Thread(this, TextProgressBar.class.getName());
    private final AtomicInteger ticks = new AtomicInteger(0);
    private final PrintStream printStream;
    private volatile boolean stopPrinting = false;


    public TextProgressBar(int totalTicksRequired, PrintStream printStream) {
        this(totalTicksRequired, printStream, DEFAULT_BAR_WIDTH);
    }

    public TextProgressBar(int totalTicksRequired, PrintStream printStream, int barWith) {
        this.totalTicksRequired = totalTicksRequired;
        this.printStream = printStream;
        this.barWith = barWith;
    }

    public void startPrinting() {
        thread.start();
    }

    @Override
    public void tick() {
        ticks.incrementAndGet();
    }

    synchronized void printCompletionPercentage() {
        printStream.print("|");
        int i = 0;
        for (; i < getPercentage() / 100.0 * barWith; i++) {
            printStream.print("=");
        }
        for (; i < barWith; i++) {
            printStream.print("-");
        }
        printStream.printf("| %d%%\r" + (getPercentage() == 100 ? "\n" : ""), (int) getPercentage());
    }

    private double getPercentage() {
        return ticks.get() / (double) totalTicksRequired * 100.0;
    }

    @Override
    public void run() {
        while (getPercentage() < 100 && !stopPrinting) {
            try {
                printCompletionPercentage();
                Thread.sleep(200);
            } catch (InterruptedException ignore) { }
        }

        printCompletionPercentage();
    }

    public void stopPrinting() {
        stopPrinting = true;
        try {
            thread.join();
        } catch (InterruptedException ignore) { }
    }
}
