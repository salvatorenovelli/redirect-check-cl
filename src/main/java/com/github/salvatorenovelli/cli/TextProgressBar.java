package com.github.salvatorenovelli.cli;

import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
public class TextProgressBar implements Runnable, ProgressMonitor {

    public static final int COMPLETION_BAR_WIDTH = 50;

    private final int totalTicksRequired;
    private final Thread thread = new Thread(this, TextProgressBar.class.getName());
    private final AtomicInteger ticks = new AtomicInteger(0);
    private volatile boolean stopPrinting = false;

    public TextProgressBar(int totalTicksRequired) {
        this.totalTicksRequired = totalTicksRequired;
    }

    public void startPrinting() {
        thread.start();
    }

    @Override
    public void tick() {
        ticks.incrementAndGet();
    }

    private synchronized void printCompletionPercentage() {
        System.out.print("|");
        int i = 0;
        for (; i < getPercentage() / 100.0 * COMPLETION_BAR_WIDTH; i++) {
            System.out.print("=");
        }
        for (; i < COMPLETION_BAR_WIDTH; i++) {
            System.out.print("-");
        }
        System.out.printf("| %d%%\r" + (getPercentage() == 100 ? "\n" : ""), (int) getPercentage());
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
            } catch (InterruptedException ignore) {

            }
        }

        printCompletionPercentage();
    }

    public void stopPrinting() {
        stopPrinting = true;
        try {
            thread.join();
        } catch (InterruptedException ignore) {
        }
    }
}
