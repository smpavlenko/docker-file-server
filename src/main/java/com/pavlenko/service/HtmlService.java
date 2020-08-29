package com.pavlenko.service;

import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@NoArgsConstructor
public class HtmlService {
    private static final Logger logger = LogManager.getLogger();
    private static final String EMPTY = "";

    public String makeParentBlock(final String urlPath) {
        logger.debug("Creating html block with parent dir link and path: {}", urlPath);

        if ((urlPath == null) || urlPath.isEmpty()) {
            return EMPTY;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("<li><a href=\"").append(urlPath).append("\">..</a></li>");
        return sb.toString();
    }

    public String makeFilesHtmlBlock(final String urlPath, final List<String> filesNames) {
        logger.debug("Creating html block with files list and path: {}", urlPath);

        if ((urlPath == null) || urlPath.isEmpty() || (filesNames == null)) {
            return EMPTY;
        }

        final StringBuilder sb = new StringBuilder();
        for (final String fileName : filesNames) {
            sb.append("<li><a href=\"").append(urlPath);
            if (!urlPath.endsWith("/")) {
                sb.append('/');
            }
            sb.append(fileName).append("\">").append(fileName).append("</a></li>");
        }
        return sb.toString();
    }
}
