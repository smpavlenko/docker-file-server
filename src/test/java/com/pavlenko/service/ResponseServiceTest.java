package com.pavlenko.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.Test;

import com.pavlenko.dto.Response;
import com.pavlenko.util.ContentTypeUtil;
import com.pavlenko.util.HttpCode;

public class ResponseServiceTest {

    private static final String ETAG = "\"f77ca612a9a4776e2ecb34f8e46286b0\"";
    private final ResponseService responseService = new ResponseService();

    @Test
    public void testBuildFailoverResponse() {
        final Response response = responseService.buildFailoverResponse("message");

        assertEquals(HttpCode.INTERNAL_SERVER_ERROR, response.getHttpCode());
        assertNotNull(response.getHeaders().get("date"));
        assertEquals(ContentTypeUtil.TEXT_HTML, response.getHeaders().get("content-type"));
        assertEquals("<html><body><h1>500 Internal Server Error</h1><h2>message</h2></body></html>", new String(response.getPayload()));
    }

    @Test
    public void testBuildEmptyHtmlResponse() {
        final Response response = responseService.buildEmptyHtmlResponse();

        assertEquals(HttpCode.OK, response.getHttpCode());
        assertNotNull(response.getHeaders().get("date"));
        assertEquals(ContentTypeUtil.TEXT_HTML, response.getHeaders().get("content-type"));
        assertEquals(0, response.getPayload().length);
    }

    @Test
    public void testBuildEmptyContentResponse() {
        final Response response = responseService.buildEmptyContentResponse(ContentTypeUtil.TEXT_PLAIN,
                                                                            123,
                                                                            ETAG,
                                                                            new Date().getTime());

        assertEquals(HttpCode.OK, response.getHttpCode());
        assertNotNull(response.getHeaders().get("date"));
        assertEquals(ETAG, response.getHeaders().get("ETag"));
        assertEquals("123", response.getHeaders().get("content-length"));
        assertNotNull(response.getHeaders().get("Last-Modified"));
        assertEquals(ContentTypeUtil.TEXT_PLAIN, response.getHeaders().get("content-type"));
        assertEquals(0, response.getPayload().length);
    }
}
