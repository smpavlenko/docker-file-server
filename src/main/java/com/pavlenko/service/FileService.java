package com.pavlenko.service;

import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor
public class FileService {
    private static final Logger logger = LogManager.getLogger();
    private static final String IF_MATCH_HEADER = "If-Match";
    private static final String IF_NONE_MATCH_HEADER = "If-None-Match";
    private static final String IF_MODIFIED_SINCE_HEADER = "If-Modified-Since";

    public byte[] getFileContent(final File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    public List<String> getFilesList(final File file) throws IOException {
        final List<String> files = new ArrayList<>();
        if ((file != null) && file.isDirectory()) {
            try (final Stream<Path> pathStream = Files.list(file.toPath())) {
                pathStream.forEach(path -> {

                    final File file1 = path.toFile();
                    if (file1.isDirectory()) {
                        files.add(file1.getName() + '/');
                    } else {
                        files.add(file1.getName());
                    }
                });
            }
        }

        return files;
    }

    public String getFileContent(final String filePath) throws IOException {
        try (final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            final String result = bufferedReader.lines().collect(Collectors.joining("\n"));
            return result;
        }
    }

    public String getContentType(final File file) throws IOException {
        return Files.probeContentType(file.toPath());
    }

    public String getFileEtag(final File file) throws IOException {
        try (final InputStream inputStream = Files.newInputStream(file.toPath())) {
            final String md5Hex = DigestUtils.md5Hex(inputStream);
            return '"' + md5Hex + '"';
        }
    }

    public boolean isCacheFlowValid(final File file, final Map<String, String> requestHeaders) throws IOException {
        final String etag = getFileEtag(file);
        final String ifMatch = requestHeaders.get(IF_MATCH_HEADER);
        if ((ifMatch != null) && !etag.equals(ifMatch)) {
            return false;
        }

        final String ifNoneMatch = requestHeaders.get(IF_NONE_MATCH_HEADER);
        if ((ifNoneMatch != null) && etag.equals(ifNoneMatch)) {
            return false;
        }

        final String ifModifiedSince = requestHeaders.get(IF_MODIFIED_SINCE_HEADER);
        if (ifModifiedSince == null) {
            return true;
        }
        try {
            final Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(ifModifiedSince);
            return file.lastModified() > date.getTime();
        } catch (final ParseException e) {
            logger.error("Can't parse If-Modified-Since header: {}", e.getMessage());
            return true;
        }
    }

}
