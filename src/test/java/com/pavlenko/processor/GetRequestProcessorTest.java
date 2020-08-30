package com.pavlenko.processor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.pavlenko.dto.Request;
import com.pavlenko.dto.Response;
import com.pavlenko.service.FileService;
import com.pavlenko.service.HtmlService;
import com.pavlenko.service.ResponseService;
import com.pavlenko.util.HttpCode;

public class GetRequestProcessorTest {
    private static final byte[] RAW_CONTENT = "content".getBytes();
    private static final String CONTENT_TYPE = "text/plain";
    private static final String ETAG = "\"827ccb0eea8a706c4c34a16891f84e7b\"";
    private static final List<String> LIST = Arrays.asList();
    private static final String PARENT_PATH = "parentPath";
    private static final String HTML = "html";
    private static final String TEMPLATE = "%LIST%";

    private final FileService fileServiceMock = mock(FileService.class);
    private final ResponseService responseServiceMock = mock(ResponseService.class);
    private final HtmlService htmlServiceMock = mock(HtmlService.class);

    private final GetRequestProcessor getRequestProcessor = new GetRequestProcessor(fileServiceMock, responseServiceMock, htmlServiceMock);

    private final Request request = new Request();
    private final Response response = new Response();
    private File file;

    @Before
    public void before() {
        file = new File(getClass().getClassLoader().getResource("textfile.txt").getFile());
        request.setPath("/");
    }

    @Test
    public void testProcessFileRequestNotModified() throws IOException {
        when(fileServiceMock.getContentType(eq(file))).thenReturn(CONTENT_TYPE);
        when(fileServiceMock.getFileEtag(eq(file))).thenReturn(ETAG);
        when(responseServiceMock.buildEmptyContentResponse(eq(CONTENT_TYPE), anyLong(), eq(ETAG), anyLong())).thenReturn(response);
        when(fileServiceMock.isCacheFlowValid(eq(file), eq(request.getHeaders()))).thenReturn(false);

        final Response result = getRequestProcessor.processFileRequest(request, file);

        assertEquals(response, result);
        assertEquals(HttpCode.NOT_MODIFIED, result.getHttpCode());

        verify(fileServiceMock).getContentType(eq(file));
        verify(fileServiceMock).getFileEtag(eq(file));
        verify(fileServiceMock).isCacheFlowValid(eq(file), eq(request.getHeaders()));
        verify(responseServiceMock).buildEmptyContentResponse(eq(CONTENT_TYPE), anyLong(), eq(ETAG), anyLong());
    }

    @Test
    public void testProcessFileRequestModified() throws IOException {
        when(fileServiceMock.getContentType(eq(file))).thenReturn(CONTENT_TYPE);
        when(fileServiceMock.getFileEtag(eq(file))).thenReturn(ETAG);
        when(fileServiceMock.getFileContent(eq(file))).thenReturn(RAW_CONTENT);
        when(responseServiceMock.buildEmptyContentResponse(eq(CONTENT_TYPE), anyLong(), eq(ETAG), anyLong())).thenReturn(response);
        when(fileServiceMock.isCacheFlowValid(eq(file), eq(request.getHeaders()))).thenReturn(true);

        final Response result = getRequestProcessor.processFileRequest(request, file);

        assertEquals(response, result);
        assertSame(RAW_CONTENT, result.getPayload());

        verify(fileServiceMock).getContentType(eq(file));
        verify(fileServiceMock).getFileEtag(eq(file));
        verify(fileServiceMock).isCacheFlowValid(eq(file), eq(request.getHeaders()));
        verify(fileServiceMock).getFileContent(eq(file));
        verify(responseServiceMock).buildEmptyContentResponse(eq(CONTENT_TYPE), anyLong(), eq(ETAG), anyLong());
    }

    @Test
    public void testProcessDirectoryRequest() throws IOException {
        when(responseServiceMock.buildEmptyHtmlResponse()).thenReturn(response);
        when(fileServiceMock.getFilesList(eq(file))).thenReturn(LIST);
        when(htmlServiceMock.makeParentBlock(eq(""))).thenReturn(PARENT_PATH);
        when(htmlServiceMock.makeFilesHtmlBlock(eq("/"), eq(LIST))).thenReturn(HTML);
        when(fileServiceMock.getFileStringContent(eq("templates/dir_template.html"))).thenReturn(TEMPLATE);

        final Response result = getRequestProcessor.processDirectoryRequest(request, file);

        assertEquals(response, result);
        assertEquals(PARENT_PATH + HTML, new String(result.getPayload()));

        verify(responseServiceMock).buildEmptyHtmlResponse();
        verify(fileServiceMock).getFilesList(eq(file));
        verify(htmlServiceMock).makeParentBlock(eq(""));
        verify(htmlServiceMock).makeFilesHtmlBlock(eq("/"), eq(LIST));
        verify(fileServiceMock).getFileStringContent(eq("templates/dir_template.html"));
    }

    @Test
    public void testProcessNotFoundRequest() throws IOException {
        when(responseServiceMock.buildEmptyHtmlResponse()).thenReturn(response);
        when(fileServiceMock.getFileStringContent(eq("404_response.html"))).thenReturn(HTML);

        final Response result = getRequestProcessor.processNotFoundRequest();

        assertEquals(response, result);
        assertEquals(HTML, new String(result.getPayload()));

        verify(responseServiceMock).buildEmptyHtmlResponse();
        verify(fileServiceMock).getFileStringContent(eq("404_response.html"));
    }
}
