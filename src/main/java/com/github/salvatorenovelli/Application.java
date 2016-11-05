package com.github.salvatorenovelli;

import com.github.salvatorenovelli.cli.TextProgressBar;
import com.github.salvatorenovelli.http.DefaultHttpConnectorFactory;
import com.github.salvatorenovelli.model.RedirectCheckResponse;
import com.github.salvatorenovelli.model.RedirectSpecification;
import com.github.salvatorenovelli.redirectcheck.RedirectCheckResponseFactory;
import com.github.salvatorenovelli.redirectcheck.domain.DefaultRedirectChainAnalyser;
import com.github.salvatorenovelli.redirectcheck.domain.RedirectChainAnalyser;
import com.github.salvatorenovelli.seo.redirect.ParallelRedirectSpecAnalyser;
import com.github.salvatorenovelli.seo.redirect.RedirectSpecificationCSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private static final int NUM_WORKERS = 50;
    private final RedirectChainAnalyser redirectChainAnalyser;
    private final String filename;
    private final FileWriter csvOutput;
    private TextProgressBar progressBar;


    private Application(String sourceFilename) throws IOException {
        this.filename = sourceFilename;

        String outFileName = sourceFilename + "_out.csv";
        try {
            this.csvOutput = new FileWriter(new File(outFileName));
            csvOutput.append(csvHeader() + "\n");
            this.redirectChainAnalyser = new DefaultRedirectChainAnalyser(new DefaultHttpConnectorFactory());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Unable to create output file: " + outFileName+". File may be busy or write protected.");
        }

    }

    public static void main(String[] args) throws IOException {


        if (args.length < 1) {
            printUsage();
        }

        setUncaughtExceptionHandler();

        long start = System.currentTimeMillis();


        try {
            Application application = new Application(args[0]);

            System.out.println("Running analysis... (this may take several minutes)");
            application.runAnalysis();
            long elapsedTime = (System.currentTimeMillis() - start) / 1000;
            System.out.println("\rAnalysis complete in " + elapsedTime + " secs. :)");

        } catch (Throwable e) {
            logger.error("Error while running analysis: " + e.getMessage());
        } finally {
            pressKey();
        }


    }

    private static void setUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> logger.error("Uncaught exception:", e));
    }

    private static void printUsage() {
        System.err.println("Please specify CSV filename");
        pressKey();
        System.exit(1);
    }

    private static void pressKey() {
        try {
            System.out.println("\rPress any key to exit...");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void runAnalysis() throws IOException, ExecutionException, InterruptedException {
        List<RedirectCheckResponse> responses = analyseRedirectsInCSV(filename);
        responses.forEach(this::tocsv);
        csvOutput.close();

    }

    private List<RedirectCheckResponse> analyseRedirectsInCSV(String filePath) throws IOException, ExecutionException, InterruptedException {
        try {
            List<RedirectSpecification> specs = RedirectSpecificationCSVReader.parse(Paths.get(filePath));
            progressBar = new TextProgressBar(specs.size());
            progressBar.startPrinting();

            ParallelRedirectSpecAnalyser analyser = new ParallelRedirectSpecAnalyser(
                    redirectChainAnalyser, new RedirectCheckResponseFactory(), NUM_WORKERS);

            analyser.setProgressMonitor(progressBar);

            return analyser.runParallelAnalysis(specs);
        } finally {
            progressBar.stopPrinting();
        }
    }


    private String csvHeader() {
        return "SourceURI, RESULT, ResultReason, Expected URI, Actual URI, Last HTTP Status";
    }

    private void tocsv(RedirectCheckResponse cr) {
        List<String> fields = Arrays.asList(
                cr.getSourceURI(),
                cr.getStatus().toString(),
                cr.getStatusMessage(),
                cr.getExpectedDestinationURI(),
                cr.getActualDestinationURI() != null ? cr.getActualDestinationURI() : "n/a",
                cr.getLastHttpStatus() != -1 ? "" + cr.getLastHttpStatus() : "n/a"
        );

        fields.forEach(this::appendToCSVOutput);
        appendToCSVFile("\n");
    }

    private void appendToCSVOutput(String field) {
        appendToCSVFile(field + ",");
    }

    private void appendToCSVFile(String field) {
        try {
            csvOutput.append(field);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
