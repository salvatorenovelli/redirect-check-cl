package com.github.salvatorenovelli;

import com.github.salvatorenovelli.cli.TextProgressBar;
import com.github.salvatorenovelli.http.DefaultHttpConnectorFactory;
import com.github.salvatorenovelli.io.RedirectCheckResponseExcelSerializer;
import com.github.salvatorenovelli.io.RedirectSpecExcelParser;
import com.github.salvatorenovelli.io.RedirectSpecificationParser;
import com.github.salvatorenovelli.model.RedirectCheckResponse;
import com.github.salvatorenovelli.model.RedirectSpecification;
import com.github.salvatorenovelli.redirectcheck.RedirectCheckResponseFactory;
import com.github.salvatorenovelli.redirectcheck.domain.DefaultRedirectChainAnalyser;
import com.github.salvatorenovelli.redirectcheck.domain.RedirectChainAnalyser;
import com.github.salvatorenovelli.seo.redirect.DefaultRedirectSpecAnalyser;
import com.github.salvatorenovelli.seo.redirect.ParallelRedirectSpecAnalyser;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private static final int NUM_WORKERS = 50;
    private final RedirectChainAnalyser chainAnalyser;
    private final RedirectCheckResponseExcelSerializer output;


    private TextProgressBar progressBar;
    private RedirectSpecificationParser parser;
    private List<RedirectSpecification> specs = new ArrayList<>();

    private Application(String sourceFilename) throws IOException {


        String outFileName = sourceFilename + "_out.xlsx";

        try {
            System.out.println("Opening input file: " + sourceFilename);
            if (sourceFilename.endsWith(".csv")) {
                throw new UnsupportedOperationException("CSV files are no longer supported. Please use excel workbook.");
            } else {
                this.parser = new RedirectSpecExcelParser(sourceFilename);
            }

            this.output = new RedirectCheckResponseExcelSerializer(outFileName);
            this.chainAnalyser = new DefaultRedirectChainAnalyser(new DefaultHttpConnectorFactory());
            initializeProgressBar();


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
        parser.parse(specs::add);
        List<RedirectCheckResponse> responses = analyseRedirects(valid(specs));
        output.addInvalidSpecs(invalid(specs));
        output.addResponses(responses);

        output.write();
    }

    private List<RedirectSpecification> invalid(List<RedirectSpecification> specs) {
        return specs.stream().filter((specification) -> !specification.isValid()).collect(Collectors.toList());
    }

    private List<RedirectSpecification> valid(List<RedirectSpecification> specs) {
        return specs.stream().filter(RedirectSpecification::isValid).collect(Collectors.toList());
    }

    private void initializeProgressBar() {
        progressBar = new TextProgressBar(parser.getNumSpecs(), System.out, 50);
        progressBar.startPrinting();
    }

    private List<RedirectCheckResponse> analyseRedirects(List<RedirectSpecification> specs) throws IOException, ExecutionException, InterruptedException {
        try {

            ParallelRedirectSpecAnalyser analyser =
                    new ParallelRedirectSpecAnalyser(NUM_WORKERS,
                            new DefaultRedirectSpecAnalyser(chainAnalyser,
                                    new RedirectCheckResponseFactory(),
                                    progressBar));
            return analyser.runParallelAnalysis(specs);
        } finally {
            progressBar.stopPrinting();
        }
    }


}