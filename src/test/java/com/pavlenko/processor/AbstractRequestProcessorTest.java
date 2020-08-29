package com.pavlenko.processor;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.pavlenko.dto.Request;
import com.pavlenko.dto.Response;

public class AbstractRequestProcessorTest {
    private final Response fileResponse = new Response();
    private final Response dirResponse = new Response();
    private final Response notFoundResponse = new Response();

    private final AbstractRequestProcessor abstractRequestProcessor = new AbstractRequestProcessor() {

        @Override
        protected Response processFileRequest(final Request request,
                                              final File file) {
            return fileResponse;
        }

        @Override
        protected Response processDirectoryRequest(final Request request,
                                                   final File file) {
            return dirResponse;
        }

        @Override
        protected Response processNotFoundRequest() {
            return notFoundResponse;
        }
    };

    private final Request request = new Request();
    private File file;

    @Before
    public void before() {
        fileResponse.setPayload("file".getBytes());
        fileResponse.setPayload("dir".getBytes());
        fileResponse.setPayload("notfound".getBytes());

        file = new File(getClass().getClassLoader().getResource("textfile.txt").getFile());
        abstractRequestProcessor.setRootDir("");
    }

    @Test
    public void testProcessFileRequest() {
        request.setPath(file.getAbsolutePath());

        final Response response = abstractRequestProcessor.process(request);

        assertEquals(fileResponse, response);
    }

    @Test
    public void testProcessDirRequest() {
        request.setPath(file.getParentFile().getAbsolutePath());

        final Response response = abstractRequestProcessor.process(request);

        assertEquals(dirResponse, response);
    }

    @Test
    public void testProcessNotFoundRequest() {
        request.setPath("wrong");

        final Response response = abstractRequestProcessor.process(request);

        assertEquals(notFoundResponse, response);
    }
}
