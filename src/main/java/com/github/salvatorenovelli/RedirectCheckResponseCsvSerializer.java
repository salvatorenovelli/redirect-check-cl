package com.github.salvatorenovelli;

import com.github.salvatorenovelli.model.RedirectCheckResponse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class RedirectCheckResponseCsvSerializer {

    private final FileWriter csvOutput;

    public RedirectCheckResponseCsvSerializer(String outFileName) throws IOException {
        this.csvOutput = new FileWriter(new File(outFileName));
        csvOutput.append(csvHeader()).append("\n");
    }

    public void writeall(Collection<RedirectCheckResponse> responses) throws IOException {
        try {
            responses.forEach(this::tocsv);
        } finally {
            csvOutput.close();
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
