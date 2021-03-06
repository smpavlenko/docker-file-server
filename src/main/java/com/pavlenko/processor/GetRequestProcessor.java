package com.pavlenko.processor;

import com.pavlenko.dto.Request;
import com.pavlenko.dto.Response;
import com.pavlenko.service.FileService;
import com.pavlenko.service.HtmlService;
import com.pavlenko.service.ResponseService;
import com.pavlenko.util.HttpCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GetRequestProcessor extends HeadRequestProcessor {
    private static final Logger logger = LogManager.getLogger();

    private static final String NOT_FOUND_RESPONSE = "404_response.html";
    private static final String LIST_PLACEHOLDER = "%LIST%";
    private static final String TEMPLATE_LIST_HTML = "templates/dir_template.html";

    private final FileService fileService;
    private final ResponseService responseService;
    private final HtmlService htmlService;

    public GetRequestProcessor(final FileService fileService, final ResponseService responseService, final HtmlService htmlService) {
        super(fileService, responseService);

        this.fileService = fileService;
        this.responseService = responseService;
        this.htmlService = htmlService;
    }

    @Override
    protected Response processFileRequest(final Request request, final File file) {
        try {
            final Response response = super.processFileRequest(request, file);
            if (HttpCode.NOT_MODIFIED == response.getHttpCode()) {
                return response;
            }

            final byte[] fileData = fileService.getFileContent(file);
            response.setPayload(fileData);
            return response;
        } catch (final IOException e) {
            logger.error("Creating file response failed: {}", e.getMessage());
            return responseService.buildFailoverResponse(e.getMessage());
        }
    }

    @Override
    protected Response processDirectoryRequest(final Request request, final File file) {
        final String filePath = request.getPath();
        try {
            final Response response = super.processDirectoryRequest(request, file);
            final List<String> list = fileService.getFilesList(file);

            final String parentPath = htmlService.makeParentBlock(getParentPath(filePath));

            final String html = htmlService.makeFilesHtmlBlock(filePath, list);

            final String templateContent = fileService.getFileStringContent(TEMPLATE_LIST_HTML);
            final String newText = templateContent.replaceAll(LIST_PLACEHOLDER, parentPath + html);

            response.setPayload(newText.getBytes());
            return response;
        } catch (final IOException e) {
            logger.error("Creating directory response failed: {}", e.getMessage());
            return responseService.buildFailoverResponse(e.getMessage());
        }
    }

    private static String getParentPath(final String filePath) {
        if (!"/".equals(filePath)) {
            final String lastWord = (filePath.endsWith("/")) ? filePath.substring(0, filePath.length() - 1) : filePath;
            return lastWord.substring(0, lastWord.lastIndexOf('/') + 1);
        }

        return "";
    }

    @Override
    protected Response processNotFoundRequest() {
        try {
            final Response response = super.processNotFoundRequest();
            final String content = fileService.getFileStringContent(NOT_FOUND_RESPONSE);
            response.setPayload(content.getBytes());
            return response;
        } catch (final IOException e) {
            logger.error("Creating not found response failed: {}", e.getMessage());
            return responseService.buildFailoverResponse(e.getMessage());
        }
    }
}
