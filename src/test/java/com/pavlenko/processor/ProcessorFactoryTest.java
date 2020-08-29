package com.pavlenko.processor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Test;

public class ProcessorFactoryTest {
    private final HeadRequestProcessor headRequestProcessorMock = mock(HeadRequestProcessor.class);
    private final GetRequestProcessor getRequestProcessor = mock(GetRequestProcessor.class);
    private final UnsupportedRequestProcessor unsupportedRequestProcessor = mock(UnsupportedRequestProcessor.class);

    private final ProcessorFactory processorFactory = new ProcessorFactory(headRequestProcessorMock,
                                                                           getRequestProcessor,
                                                                           unsupportedRequestProcessor);

    @Test
    public void testGetRequestProcessorForGet() {
        assertEquals(getRequestProcessor, processorFactory.getRequestProcessor("GET"));
    }

    @Test
    public void testGetRequestProcessorForHead() {
        assertEquals(headRequestProcessorMock, processorFactory.getRequestProcessor("HEAD"));
    }

    @Test
    public void testGetRequestProcessorForPut() {
        assertEquals(unsupportedRequestProcessor, processorFactory.getRequestProcessor("PUT"));
    }
}
