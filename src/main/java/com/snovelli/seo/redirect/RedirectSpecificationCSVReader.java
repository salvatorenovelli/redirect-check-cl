package com.snovelli.seo.redirect;

import com.snovelli.model.RedirectSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Salvatore on 02/05/2016.
 */
public class RedirectSpecificationCSVReader {


    private static final RedirectSpecification INVALID_URI_SPEC = null;
    private static Logger logger = LoggerFactory.getLogger(RedirectSpecificationCSVReader.class);

    public static List<RedirectSpecification> parse(Path csvFile) throws IOException {


        return Files.lines(csvFile, Charset.defaultCharset())
                .map(s -> s.split(","))
                .map(toRedirectSpecification())
                .filter(isValid())
                .collect(Collectors.toList());


    }

    private static Function<String[], RedirectSpecification> toRedirectSpecification() {
        return strings -> {
            if (strings.length > 1) {
                try {
                    return new RedirectSpecification(strings[0], strings[1]);
                } catch (URISyntaxException e) {
                    logger.warn("Unable to parse URI: " + e.getMessage());
                }
            } else {
                if (strings.length > 0) {
                    logger.warn("Missing parameter in: " + Arrays.toString(strings));
                }
            }
            return INVALID_URI_SPEC;
        };
    }

    private static Predicate<RedirectSpecification> isValid() {
        return redirectSpecification -> redirectSpecification != INVALID_URI_SPEC;
    }
}
