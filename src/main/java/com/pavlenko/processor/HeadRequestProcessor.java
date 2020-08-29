package com.pavlenko.processor;

import com.pavlenko.dto.Request;
import com.pavlenko.dto.Response;
import com.pavlenko.service.FileService;
import com.pavlenko.service.ResponseService;
import com.pavlenko.util.HttpResults;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

@RequiredArgsConstructor
public class HeadRequestProcessor extends AbstractRequestProcessor {
    private static final Logger logger = LogManager.getLogger();

    private final FileService fileService;
    private final ResponseService responseService;

    @Override
    protected Response processFileRequest(final Request request, final File file) {
        try {
            final String contentType = fileService.getContentType(file);
            final String etag = fileService.getFileEtag(file);
            final Response response = responseService.buildEmptyContentResponse(contentType, file.length(), etag, file.lastModified());
            if (!fileService.isCacheFlowValid(file, request.getHeaders())) {
                logger.debug("File {} is not modified", file.getName());
                response.setHttpResult(HttpResults.NOT_MODIFIED);
            }
            return response;
        } catch (final IOException e) {
            logger.error("Creating file response failed: {}", e.getMessage());
            return responseService.buildFailoverResponse(e.getMessage());
        }
    }

    @Override
    protected Response processDirectoryRequest(final Request request, final File file) {
        return responseService.buildEmptyHtmlResponse();
    }

    @Override
    protected Response processNotFoundRequest() {
        final Response response = responseService.buildEmptyHtmlResponse();
        response.setHttpResult(HttpResults.NOT_FOUND);
        return response;
    }
}
