package com.pavlenko.processor;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pavlenko.dto.Request;
import com.pavlenko.dto.Response;
import com.pavlenko.service.FileService;
import com.pavlenko.service.ResponseService;
import com.pavlenko.util.HttpCode;

public class UnsupportedRequestProcessor extends GetRequestProcessor {
    private static final Logger logger = LogManager.getLogger();
    private static final String METHOD_NOT_ALLOWED_RESPONSE = "405_response.html";

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
            response.setHttpCode(HttpCode.METHOD_NOT_ALLOWED);

            final String content = fileService.getFileStringContent(METHOD_NOT_ALLOWED_RESPONSE);
            response.setPayload(content.getBytes());
            return response;
        } catch (final IOException e) {
            logger.error("Creating not found response failed: {}", e.getMessage());
            return responseService.buildFailoverResponse(e.getMessage());
        }
    }
}
