package com.pavlenko.service;

import com.pavlenko.dto.Request;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RequestPatternServiceTest {

    private final RequestPatternService requestPatternService = new RequestPatternService();
    private Request request;

    @Before
    public void before() {
        request = new Request();
    }

    @Test
    public void testFillRequestWithNullLine() {
        requestPatternService.fillRequestWithData(request, null);

        assertNull(request.getMethod());
        assertNull(request.getPath());
    }

    @Test
    public void testFillRequestWithGetMethod() {
        requestPatternService.fillRequestWithData(request, "GET /pom.xml HTTP/1.1");

        assertEquals("GET", request.getMethod());
        assertEquals("/pom.xml", request.getPath());
    }

    @Test
    public void testFillRequestWithHeadMethod() {
        requestPatternService.fillRequestWithData(request, "HEAD /pom.xml HTTP/1.1");

        assertEquals("HEAD", request.getMethod());
        assertEquals("/pom.xml", request.getPath());
    }

    @Test
    public void testFillRequestWithUnknownMethod() {
        requestPatternService.fillRequestWithData(request, "UNKNOWN /pom.xml HTTP/1.1");

        assertNull(request.getMethod());
        assertNull(request.getPath());
    }

    @Test
    public void testFillRequestWithHeader() {
        requestPatternService.fillRequestWithData(request, "User-Agent: curl/7.64.1");

        assertTrue(request.getHeaders().containsKey("User-Agent"));
        assertEquals("curl/7.64.1", request.getHeaders().get("User-Agent"));
    }
}
