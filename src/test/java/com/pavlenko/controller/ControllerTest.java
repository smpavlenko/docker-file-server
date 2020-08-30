package com.pavlenko.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.OutputStream;
import java.net.URI;

import org.junit.Before;
import org.junit.Test;

import com.pavlenko.dto.Request;
import com.pavlenko.dto.Response;
import com.pavlenko.processor.GetRequestProcessor;
import com.pavlenko.processor.ProcessorFactory;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public class ControllerTest {
    private static final String rootDir = "/";
    private static final byte[] PAYLOAD = "content".getBytes();
    private final ProcessorFactory processorFactoryMack = mock(ProcessorFactory.class);
    private final HttpExchange httpExchangeMock = mock(HttpExchange.class);
    private final OutputStream responseOutputStreamMock = mock(OutputStream.class);
    private final GetRequestProcessor getRequestProcessorMock = mock(GetRequestProcessor.class);

    private final Headers requestHeaders = new Headers();
    private final Headers responseHeaders = new Headers();
    private final Response response = new Response();

    private final Controller controller = new Controller(rootDir, processorFactoryMack);

    @Before
    public void before() {
        requestHeaders.set("key", "value");
        requestHeaders.set("Connection", "Keep-Alive");
        response.setPayload(PAYLOAD);
    }

    @Test
    public void testHandleGetRequest() throws Exception {
        when(httpExchangeMock.getRequestMethod()).thenReturn("GET");
        when(httpExchangeMock.getRequestHeaders()).thenReturn(requestHeaders);
        when(httpExchangeMock.getResponseHeaders()).thenReturn(responseHeaders);
        when(httpExchangeMock.getRequestURI()).thenReturn(new URI(""));
        when(httpExchangeMock.getResponseBody()).thenReturn(responseOutputStreamMock);
        when(processorFactoryMack.getRequestProcessor(eq("GET"))).thenReturn(getRequestProcessorMock);
        when(getRequestProcessorMock.process(any(Request.class))).thenReturn(response);

        controller.handle(httpExchangeMock);

        assertEquals("Keep-Alive", responseHeaders.getFirst("Connection"));
        assertNotNull(responseHeaders.get("Keep-Alive"));

        verify(getRequestProcessorMock).setRootDir(eq(rootDir));
        verify(responseOutputStreamMock).write(PAYLOAD);
        verify(responseOutputStreamMock).flush();
    }
}
