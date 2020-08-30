package com.pavlenko.service;

import com.pavlenko.dto.Response;
import com.pavlenko.util.ContentTypeUtil;
import com.pavlenko.util.HttpCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
public class ResponseService {
    public Response buildEmptyHtmlResponse() {
        final Response response = new Response();
        response.setHttpCode(HttpCode.OK);

        response.addHeader("date", new Date().toString());
        response.addHeader("content-type", ContentTypeUtil.TEXT_HTML);
        return response;
    }

    public Response buildEmptyContentResponse(final String contentType, final long fileLength, final String fileEtag, final long fileLastModified) {
        final Response response = new Response();
        response.setHttpCode(HttpCode.OK);

        response.addHeader("date", new Date().toString());
        response.addHeader("content-type", contentType);
        response.addHeader("content-length", String.valueOf(fileLength));
        response.addHeader("ETag", fileEtag);
        response.addHeader("Last-Modified", new Date(fileLastModified).toString());
        return response;
    }

    public Response buildFailoverResponse(final String message) {
        final Response response = new Response();
        response.setHttpCode(HttpCode.INTERNAL_SERVER_ERROR);
        response.addHeader("date", new Date().toString());
        response.addHeader("content-type", ContentTypeUtil.TEXT_HTML);
        response.setPayload(("<html><body><h1>500 Internal Server Error</h1><h2>" + message + "</h2></body></html>").getBytes());
        return response;
    }
    }
