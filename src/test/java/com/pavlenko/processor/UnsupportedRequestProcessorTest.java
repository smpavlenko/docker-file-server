package com.pavlenko.processor;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.pavlenko.dto.Request;
import com.pavlenko.dto.Response;
import com.pavlenko.service.FileService;
import com.pavlenko.service.ResponseService;
import com.pavlenko.util.HttpCode;

public class UnsupportedRequestProcessorTest {
    private static final String CONTENT = "content";
    private final FileService fileServiceMock = mock(FileService.class);
    private final ResponseService responseServiceMock = mock(ResponseService.class);

    private final UnsupportedRequestProcessor unsupportedRequestProcessor = new UnsupportedRequestProcessor(fileServiceMock,
                                                                                                            responseServiceMock);

    private final Request request = new Request();
    private final Response response = new Response();
    private File file;

    @Before
    public void before() {
        file = new File(getClass().getClassLoader().getResource("textfile.txt").getFile());
    }

    @Test
    public void testProcessFileRequest() throws IOException {
        when(responseServiceMock.buildEmptyHtmlResponse()).thenReturn(response);
        when(fileServiceMock.getFileStringContent(eq("403_response.html"))).thenReturn(CONTENT);

        final Response result = unsupportedRequestProcessor.processFileRequest(request, file);

        assertEquals(response, result);
        assertEquals(HttpCode.FORBIDDEN, result.getHttpCode());
        assertEquals(CONTENT, new String(result.getPayload()));

        verify(responseServiceMock).buildEmptyHtmlResponse();
        verify(fileServiceMock).getFileStringContent(eq("403_response.html"));
    }

    @Test
    public void testProcessDirectoryRequest() throws IOException {
        when(responseServiceMock.buildEmptyHtmlResponse()).thenReturn(response);
        when(fileServiceMock.getFileStringContent(eq("403_response.html"))).thenReturn(CONTENT);

        final Response result = unsupportedRequestProcessor.processDirectoryRequest(request, file);

        assertEquals(response, result);
        assertEquals(HttpCode.FORBIDDEN, result.getHttpCode());
        assertEquals(CONTENT, new String(result.getPayload()));

        verify(responseServiceMock).buildEmptyHtmlResponse();
        verify(fileServiceMock).getFileStringContent(eq("403_response.html"));
    }
}
