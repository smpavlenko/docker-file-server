package com.pavlenko.service;

import com.pavlenko.dto.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestPatternService {
    private static final Logger logger = LogManager.getLogger();

    private final Pattern requestPattern = Pattern.compile("(GET|HEAD|POST|PUT|DELETE|CONNECT|OPTIONS|TRACE|PATCH)\\s([^\\s]*)\\s.*");
    private final Pattern headerPattern = Pattern.compile("([\\w-]+):\\s(.*)");

    public void fillRequestWithData(final Request request, final String line) {
        logger.debug("Filling Request with data from line: {}", line);

        if ((request == null) || (line == null)) {
            return;
        }

        fillRequestWithHttpData(request, line);
        fillRequestWithHeaders(request, line);
    }

    private void fillRequestWithHeaders(final Request request, final String line) {
        final Matcher headerMatcher = headerPattern.matcher(line);
        if (headerMatcher.find()) {
            request.addHeader(headerMatcher.group(1), headerMatcher.group(2));
        }
    }

    private void fillRequestWithHttpData(final Request request, final String line) {
        final Matcher requestMatcher = requestPattern.matcher(line);
        if (requestMatcher.find()) {
            request.setMethod(requestMatcher.group(1));
            request.setPath(requestMatcher.group(2));
        }
    }
}
