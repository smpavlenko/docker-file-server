package com.pavlenko.processor;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProcessorFactory {
    private final HeadRequestProcessor headRequestProcessor;
    private final GetRequestProcessor getRequestProcessor;
    private final UnsupportedRequestProcessor unsupportedRequestProcessor;

    public Processor getRequestProcessor(final String methodName) {
        switch (methodName) {
            case "HEAD":
                return headRequestProcessor;
            case "GET":
                return getRequestProcessor;
            default:
                return unsupportedRequestProcessor;
        }
    }
}
