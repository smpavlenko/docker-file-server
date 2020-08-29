package com.pavlenko.service;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HtmlServiceTest {
    private final HtmlService service = new HtmlService();

    @Test
    public void testMakeParentBlockWithNullInput() {
        final String result = service.makeParentBlock(null);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testMakeParentBlockWithEmptyInput() {
        final String result = service.makeParentBlock("");

        assertTrue(result.isEmpty());
    }

    @Test
    public void testMakeParentBlockWithNormalInput() {
        final String result = service.makeParentBlock("path");

        assertEquals("<li><a href=\"path\">..</a></li>", result);
    }

    @Test
    public void testMakeFilesHtmlBlockWithEmptyList() {
        final String result = service.makeFilesHtmlBlock("/path", Arrays.asList());

        assertTrue(result.isEmpty());
    }

    @Test
    public void testMakeFilesHtmlBlockWithNullList() {
        final String result = service.makeFilesHtmlBlock("/path", null);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testMakeFilesHtmlBlockWithNullPath() {
        final String result = service.makeFilesHtmlBlock(null, Arrays.asList("dir1/", "file1", "file2"));

        assertTrue(result.isEmpty());
    }

    @Test
    public void testMakeFilesHtmlBlockWithNormalInput() {
        final String result = service.makeFilesHtmlBlock("/path", Arrays.asList("dir1/", "file1", "file2"));

        assertEquals(
                "<li><a href=\"/path/dir1/\">dir1/</a></li>" +
                        "<li><a href=\"/path/file1\">file1</a></li>" +
                        "<li><a href=\"/path/file2\">file2</a></li>", result);
    }

}
