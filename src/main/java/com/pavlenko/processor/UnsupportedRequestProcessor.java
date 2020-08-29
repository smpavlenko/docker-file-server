package com.pavlenko.processor;

import com.pavlenko.dto.Request;
import com.pavlenko.dto.Response;
import com.pavlenko.service.FileService;
import com.pavlenko.service.ResponseService;
import com.pavlenko.util.HttpResults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class UnsupportedRequestProcessor extends GetRequestProcessor {
    private static final Logger logger = LogManager.getLogger();

    private final FileService fileService;
    private final ResponseService responseService;

    public UnsupportedRequestProcessor(final FileService fileService, final ResponseService responseService) {
        super(fileService, responseService, null);

        this.fileService = fileService;
        this.responseService = responseService;
    }

    @Override
    protected Response processFileRequest(final Request request, final File file) {
        return processUnsupportedRequest();
    }

    @Override
    protected Response processDirectoryRequest(final Request request, final File file) {
        return processUnsupportedRequest();
    }

    private Response processUnsupportedRequest() {
        try {
            final Response response = responseService.buildEmptyHtmlResponse();
            response.setHttpResult(HttpResults.FORBIDDEN);

            final String content = fileService.getFileContent("403_response.html");
            response.setPayload(content.getBytes());
            return response;
        } catch (final IOException e) {
            logger.error("Creating not found response failed: {}", e.getMessage());
            return responseService.buildFailoverResponse(e.getMessage());
        }
    }
}
