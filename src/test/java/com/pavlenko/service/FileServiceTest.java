package com.pavlenko.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class FileServiceTest {

    private static final String ETAG = "\"827ccb0eea8a706c4c34a16891f84e7b\"";
    private final FileService fileService = new FileService();
    private final Map<String, String> headers = new HashMap<>();
    private File file;

    @Before
    public void before() {
        file = new File(getClass().getClassLoader().getResource("textfile.txt").getFile());
    }

    @Test
    public void testGetFileContent() throws Exception {
        final byte[] content = fileService.getFileContent(file);

        assertEquals("12345", new String(content));
    }

    @Test
    public void testGetContentType() throws Exception {
        final String contentType = fileService.getContentType(file);

        assertEquals("text/plain", contentType);
    }

    @Test
    public void testGetFileEtag() throws Exception {
        final String etag = fileService.getFileEtag(file);

        assertEquals(ETAG, etag);
    }

    @Test
    public void testIsCacheFlowValidWithNoHeaders() throws Exception {
        assertTrue(fileService.isCacheFlowValid(file, headers));
    }

    @Test
    public void testGetFilesList() throws Exception {
        final List<String> filesList = fileService.getFilesList(file.getParentFile());

        assertFalse(filesList.isEmpty());
        assertTrue(filesList.contains("textfile.txt"));
    }

    @Test
    public void testisCacheFlowValidWithValidIfMatch() throws Exception {
        headers.put("If-Match", ETAG);
        assertTrue(fileService.isCacheFlowValid(file, headers));
    }

    @Test
    public void testisCacheFlowValidWithInvalidIfMatch() throws Exception {
        headers.put("If-Match", "");
        assertFalse(fileService.isCacheFlowValid(file, headers));
    }

    @Test
    public void testisCacheFlowValidWithValidIfNoneMatch() throws Exception {
        headers.put("If-None-Match", "");
        assertTrue(fileService.isCacheFlowValid(file, headers));
    }

    @Test
    public void testisCacheFlowValidWithInvalidIfNoneMatch() throws Exception {
        headers.put("If-None-Match", ETAG);
        assertFalse(fileService.isCacheFlowValid(file, headers));
    }

    @Test
    public void testisCacheFlowValidWithValidIfModifiedSince() throws Exception {
        headers.put("If-Modified-Since", "Sat Aug 29 16:14:56 UTC 2000");
        assertTrue(fileService.isCacheFlowValid(file, headers));
    }

    @Test
    public void testisCacheFlowValidWithInvalidIfModifiedSince() throws Exception {
        headers.put("If-Modified-Since", "Sat Aug 29 16:14:56 UTC 2021");
        assertFalse(fileService.isCacheFlowValid(file, headers));
    }
}
