package com.pavlenko.processor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.anyLong;
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

public class HeadRequestProcessorTest {
    private static final String CONTENT_TYPE = "text/plain";
    private static final String ETAG = "\"827ccb0eea8a706c4c34a16891f84e7b\"";
    private final FileService fileServiceMock = mock(FileService.class);
    private final ResponseService responseServiceMock = mock(ResponseService.class);
    private final HeadRequestProcessor headRequestProcessor = new HeadRequestProcessor(fileServiceMock, responseServiceMock);

    private final Request request = new Request();
    private final Response response = new Response();
    private File file;

    @Before
    public void before() {
        file = new File(getClass().getClassLoader().getResource("textfile.txt").getFile());
    }

    @Test
    public void testProcessFileRequestWithInvalidCacheFlow() throws IOException {
        when(fileServiceMock.getContentType(eq(file))).thenReturn(CONTENT_TYPE);
        when(fileServiceMock.getFileEtag(eq(file))).thenReturn(ETAG);
        when(responseServiceMock.buildEmptyContentResponse(eq(CONTENT_TYPE), anyLong(), eq(ETAG), anyLong())).thenReturn(response);
        when(fileServiceMock.isCacheFlowValid(eq(file), eq(request.getHeaders()))).thenReturn(false);

        final Response result = headRequestProcessor.processFileRequest(request, file);

        assertSame(response, result);
        assertEquals(HttpCode.NOT_MODIFIED, result.getHttpCode());

        verify(fileServiceMock).getContentType(eq(file));
        verify(fileServiceMock).getFileEtag(eq(file));
        verify(fileServiceMock).isCacheFlowValid(eq(file), eq(request.getHeaders()));
        verify(responseServiceMock).buildEmptyContentResponse(eq(CONTENT_TYPE), anyLong(), eq(ETAG), anyLong());
    }

    @Test
    public void testProcessFileRequestWithValidCacheFlow() throws IOException {
        when(fileServiceMock.getContentType(eq(file))).thenReturn(CONTENT_TYPE);
        when(fileServiceMock.getFileEtag(eq(file))).thenReturn(ETAG);
        when(responseServiceMock.buildEmptyContentResponse(eq(CONTENT_TYPE), anyLong(), eq(ETAG), anyLong())).thenReturn(response);
        when(fileServiceMock.isCacheFlowValid(eq(file), eq(request.getHeaders()))).thenReturn(true);

        final Response result = headRequestProcessor.processFileRequest(request, file);

        assertEquals(response, result);

        verify(fileServiceMock).getContentType(eq(file));
        verify(fileServiceMock).getFileEtag(eq(file));
        verify(fileServiceMock).isCacheFlowValid(eq(file), eq(request.getHeaders()));
        verify(responseServiceMock).buildEmptyContentResponse(eq(CONTENT_TYPE), anyLong(), eq(ETAG), anyLong());
    }

    @Test
    public void testProcessDirectoryRequest() {
        when(responseServiceMock.buildEmptyHtmlResponse()).thenReturn(response);

        final Response result = headRequestProcessor.processDirectoryRequest(request, file);

        assertEquals(response, result);

        verify(responseServiceMock).buildEmptyHtmlResponse();
    }

    @Test
    public void testProcessNotFoundRequest() {
        when(responseServiceMock.buildEmptyHtmlResponse()).thenReturn(response);

        final Response result = headRequestProcessor.processNotFoundRequest();

        assertEquals(response, result);
        assertEquals(HttpCode.NOT_FOUND, result.getHttpCode());

        verify(responseServiceMock).buildEmptyHtmlResponse();
    }
}
