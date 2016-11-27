package com.github.salvatorenovelli;

import com.github.salvatorenovelli.cli.TextProgressBar;
import com.github.salvatorenovelli.http.DefaultHttpConnectorFactory;
import com.github.salvatorenovelli.io.ParsedSpecificationHandler;
import com.github.salvatorenovelli.io.RedirectCheckResponseCsvSerializer;
import com.github.salvatorenovelli.io.RedirectSpecExcelParser;
import com.github.salvatorenovelli.io.RedirectSpecificationParser;
import com.github.salvatorenovelli.model.InvalidRedirectSpecification;
import com.github.salvatorenovelli.model.RedirectCheckResponse;
import com.github.salvatorenovelli.model.RedirectSpecification;
import com.github.salvatorenovelli.redirectcheck.RedirectCheckResponseFactory;
import com.github.salvatorenovelli.redirectcheck.domain.DefaultRedirectChainAnalyser;
import com.github.salvatorenovelli.redirectcheck.domain.RedirectChainAnalyser;
import com.github.salvatorenovelli.seo.redirect.ParallelRedirectSpecAnalyser;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class Application implements ParsedSpecificationHandler {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private static final int NUM_WORKERS = 50;
    private final RedirectChainAnalyser redirectChainAnalyser;
    private final RedirectCheckResponseCsvSerializer csvWriter;


    private TextProgressBar progressBar;
    private RedirectSpecificationParser parser;
    private List<RedirectSpecification> validSpec = new ArrayList<>();
    private List<InvalidRedirectSpecification> invalidSpec = new ArrayList<>();

    private Application(String sourceFilename) throws IOException {


        String outFileName = sourceFilename + "_out.csv";

        try {
            System.out.println("Opening input file: " + sourceFilename);
            if (sourceFilename.endsWith(".csv")) {
                throw new UnsupportedOperationException("CSV files are no longer supported. Please use excel workbook.");
                //this.parser = new RedirectSpecCSVParser(Paths.get(sourceFilename));
            } else {
                this.parser = new RedirectSpecExcelParser(sourceFilename, this);
            }

            this.csvWriter = new RedirectCheckResponseCsvSerializer(outFileName);
            this.redirectChainAnalyser = new DefaultRedirectChainAnalyser(new DefaultHttpConnectorFactory());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Unable to create output file: " + outFileName + ". File may be busy or write protected.");
        } catch (InvalidFormatException e) {
            throw new RuntimeException("Unable to read input file: " + sourceFilename + ":" + e.getMessage());
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
        System.err.println("Please specify input filename");
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
        List<RedirectCheckResponse> responses = analyseRedirects();
        csvWriter.writeall(responses);
    }

    private List<RedirectCheckResponse> analyseRedirects() throws IOException, ExecutionException, InterruptedException {
        try {

            progressBar = new TextProgressBar(parser.getNumSpecs());
            progressBar.startPrinting();

            parser.parse();

            ParallelRedirectSpecAnalyser analyser = new ParallelRedirectSpecAnalyser(
                    redirectChainAnalyser, new RedirectCheckResponseFactory(), NUM_WORKERS);

            analyser.setProgressMonitor(progressBar);

            return analyser.runParallelAnalysis(validSpec);
        } finally {
            progressBar.stopPrinting();
        }
    }

    @Override
    public void handleValidSpec(RedirectSpecification spec) {
        validSpec.add(validSpec.size(), spec);
    }

    @Override
    public void handleInvalidSpec(InvalidRedirectSpecification spec) {
        invalidSpec.add(invalidSpec.size(), spec);
        progressBar.tick();
    }


}