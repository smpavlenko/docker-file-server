package com.pavlenko.controller;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pavlenko.dto.Request;
import com.pavlenko.dto.Response;
import com.pavlenko.processor.Processor;
import com.pavlenko.processor.ProcessorFactory;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Controller implements HttpHandler {
    private static final Logger logger = LogManager.getLogger();

    private static final String CONNECTION = "Connection";
    private static final String KEEP_ALIVE = "Keep-Alive";
    private static final long DEFAULT_IDLE_INTERVAL = 30; // 5 min
    private static final int DEFAULT_MAX_IDLE_CONNECTIONS = 200;
    private static final String IDLE_INTERVAL_SYS_PROPERTY = "sun.net.httpserver.idleInterval";
    private static final String MAX_IDLE_CONNECTIONS_SYS_PROPERTY = "sun.net.httpserver.maxIdleConnections";
    private static final long idleInterval = Long.getLong(IDLE_INTERVAL_SYS_PROPERTY, DEFAULT_IDLE_INTERVAL) * 1000;
    private static final Integer maxIdleConnections = Integer.getInteger(MAX_IDLE_CONNECTIONS_SYS_PROPERTY,
                                                                         DEFAULT_MAX_IDLE_CONNECTIONS);

    private final String rootDir;
    private final ProcessorFactory processorFactory;

    @Override
    public void handle(final HttpExchange httpExchange) {
        final Request request = buildRequest(httpExchange);

        final Processor requestProcessor = processorFactory.getRequestProcessor(request.getMethod());
        requestProcessor.setRootDir(rootDir);
        final Response response = requestProcessor.process(request);

        final Headers responseHeaders = httpExchange.getResponseHeaders();
        response.getHeaders().forEach(responseHeaders::add);

        handleKeepAlive(httpExchange.getRequestHeaders(), responseHeaders);

        try (final OutputStream outputStream = httpExchange.getResponseBody()) {
            httpExchange.sendResponseHeaders(response.getHttpCode(), response.getPayload().length);

            outputStream.write(response.getPayload());
            outputStream.flush();
        } catch (final IOException e) {
            logger.error("Error during processing response: {}", e.getMessage());
        }
    }

    private static Request buildRequest(final HttpExchange httpExchange) {
        final Request request = new Request();
        final String method = httpExchange.getRequestMethod();

        request.setMethod(method);
        request.setPath(httpExchange.getRequestURI().getPath());
        httpExchange.getRequestHeaders().keySet().forEach(key -> request.addHeader(key, httpExchange.getRequestHeaders().getFirst(key)));
        return request;
    }

    private static void handleKeepAlive(final Headers requestHeaders,
                                        final Headers responseHeaders) {
        if (requestHeaders.containsKey(CONNECTION) && KEEP_ALIVE.equals(requestHeaders.getFirst(CONNECTION))) {
            responseHeaders.add(CONNECTION, KEEP_ALIVE);
            responseHeaders.add(KEEP_ALIVE, String.format("timeout=%s, max=%s", idleInterval, maxIdleConnections));
        }
    }
}

