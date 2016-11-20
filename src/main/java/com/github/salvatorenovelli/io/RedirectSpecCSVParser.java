package com.github.salvatorenovelli.io;

import com.github.salvatorenovelli.model.RedirectSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RedirectSpecCSVParser implements RedirectSpecificationParser {


    private static final String[] CHARSETS = new String[]{"ISO-8859-1", "UTF-8", "US-ASCII", "UTF-16BE", "UTF-16LE", "UTF-16"};
    private static final RedirectSpecification INVALID_URI_SPEC = null;
    private static Logger logger = LoggerFactory.getLogger(RedirectSpecCSVParser.class);
    private final Path inputCsvFile;
    private int currentLine = 0;

    public RedirectSpecCSVParser(Path inputCsvFile) {
        this.inputCsvFile = inputCsvFile;
    }

    @Override
    public List<RedirectSpecification> parse() throws IOException {
        try {
            for (String charset : CHARSETS) {
                try {
                    logger.debug("Attempting decoding using charset: " + charset);
                    return Files.lines(inputCsvFile, Charset.forName(charset))
                            .map(s -> s.split(","))
                            .map(toRedirectSpecification())
                            .filter(isValid())
                            .collect(Collectors.toList());
                } catch (MalformedInputException e) {
                    logger.debug("Unable to decode using charset:" + charset);
                }
            }
        } catch (Exception e) {
            logger.error("Unable to complete analysis because: " + e.getMessage(), e);
        }

        return Collections.emptyList();
    }

    private Function<String[], RedirectSpecification> toRedirectSpecification() {


        return strings -> {
            currentLine++;
            if (strings.length > 1) {
                return new RedirectSpecification(strings[0], strings[1], 200);
            } else {
                if (strings.length > 0) {
                    logger.warn("Missing expected url in line: {} ", currentLine);
                }
            }
            return INVALID_URI_SPEC;
        };
    }

    private Predicate<RedirectSpecification> isValid() {
        return redirectSpecification -> redirectSpecification != INVALID_URI_SPEC;
    }
}
