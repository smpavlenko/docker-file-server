package com.pavlenko.service;

import java.util.Date;

import com.pavlenko.dto.Response;
import com.pavlenko.util.ContentTypeUtil;
import com.pavlenko.util.HttpCode;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ResponseService {

    private static final String ERROR_RESPONSE_PATTERN = "<html><body><h1>500 Internal Server Error</h1><h2>%s</h2></body></html>";
    private static final String CONTENT_TYPE = "content-type";
    private static final String DATE = "date";
    private static final String CONTENT_LENGTH = "content-length";
    private static final String E_TAG = "ETag";
    private static final String LAST_MODIFIED = "Last-Modified";

    public Response buildEmptyHtmlResponse() {
        final Response response = new Response();
        response.setHttpCode(HttpCode.OK);

        response.addHeader(DATE, new Date().toString());
        response.addHeader(CONTENT_TYPE, ContentTypeUtil.TEXT_HTML);
        return response;
    }

    public Response buildEmptyContentResponse(final String contentType,
                                              final long fileLength,
                                              final String fileEtag,
                                              final long fileLastModified) {
        final Response response = new Response();
        response.setHttpCode(HttpCode.OK);

        response.addHeader(DATE, new Date().toString());
        response.addHeader(CONTENT_TYPE, contentType);
        response.addHeader(CONTENT_LENGTH, String.valueOf(fileLength));
        response.addHeader(E_TAG, fileEtag);
        response.addHeader(LAST_MODIFIED, new Date(fileLastModified).toString());
        return response;
    }

    public Response buildFailoverResponse(final String message) {
        final Response response = new Response();
        response.setHttpCode(HttpCode.INTERNAL_SERVER_ERROR);
        response.addHeader(DATE, new Date().toString());
        response.addHeader(CONTENT_TYPE, ContentTypeUtil.TEXT_HTML);
        response.setPayload(String.format(ERROR_RESPONSE_PATTERN, message).getBytes());
        return response;
    }
}
