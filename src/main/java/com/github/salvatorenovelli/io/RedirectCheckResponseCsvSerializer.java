package com.github.salvatorenovelli.io;


import com.github.salvatorenovelli.model.RedirectCheckResponse;
import com.github.salvatorenovelli.model.RedirectSpecification;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class RedirectCheckResponseCsvSerializer {

    private final FileWriter csvOutput;
    private SortedSet<ResponseWrapper> responses = new TreeSet<>(Comparator.comparingInt(ResponseWrapper::getLineNumber));

    public RedirectCheckResponseCsvSerializer(String outFileName) throws IOException {
        csvOutput = new FileWriter(new File(outFileName));
        csvOutput.append(csvHeader()).append("\n");
    }

    public void addResponses(List<RedirectCheckResponse> responses) throws IOException {
        this.responses.addAll(responses.stream().map(ResponseWrapper::new).collect(Collectors.toList()));
    }

    public void addInvalidSpecs(List<RedirectSpecification> invalid) {
        this.responses.addAll(invalid.stream().map(ResponseWrapper::new).collect(Collectors.toList()));
    }

    public void write() throws IOException {
        try {
            responses.forEach(this::tocsv);
        } finally {
            csvOutput.close();
        }
    }

    private String csvHeader() {
        return "Line #, SourceURI, RESULT, ResultReason, Expected URI, Actual URI, Last HTTP Status";
    }

    private void tocsv(ResponseWrapper cr) {
        List<String> fields = Arrays.asList(String.valueOf(cr.lineNumber), cr.sourceURI, cr.result, cr.reason, cr.expectedURI, cr.actualURI, cr.lastHTTPStatus);
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

    private static class ResponseWrapper {

        private final String result;
        private final String reason;
        private final String expectedURI;
        private final String actualURI;
        private final String lastHTTPStatus;
        private final String sourceURI;
        private final int lineNumber;

        private ResponseWrapper(RedirectCheckResponse cr) {
            this(cr.getRequestLineNumber(), cr.getSourceURI(),
                    cr.getStatus().toString(),
                    cr.getStatusMessage(),
                    cr.getExpectedDestinationURI(),
                    cr.getActualDestinationURI() != null ? cr.getActualDestinationURI() : "n/a",
                    cr.getLastHttpStatus() != -1 ? "" + cr.getLastHttpStatus() : "n/a");
        }

        public ResponseWrapper(RedirectSpecification specification) {
            this(specification.getLineNumber(),
                    specification.getSourceURI(),
                    RedirectCheckResponse.Status.FAILURE.toString(),
                    specification.getErrorMessage(),
                    specification.getExpectedDestination(),
                    "n/a", "n/a");
        }

        private ResponseWrapper(int lineNumber, String sourceURI, String result, String reason, String expectedURI, String actualURI, String lastHTTPStatus) {
            this.lineNumber = lineNumber;
            this.sourceURI = sourceURI;
            this.result = result;
            this.reason = reason;
            this.expectedURI = expectedURI;
            this.actualURI = actualURI;
            this.lastHTTPStatus = lastHTTPStatus;
        }

        public int getLineNumber() {
            return lineNumber;
        }
    }
}
