package com.pavlenko;

import com.pavlenko.processor.Processor;
import com.pavlenko.processor.ProcessorFactory;
import com.pavlenko.dto.Request;
import com.pavlenko.dto.Response;
import com.pavlenko.service.RequestPatternService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class Server implements Runnable {
    private static final Logger logger = LogManager.getLogger();

    private final RequestPatternService patternService;
    private final Socket socket;
    private final String rootDir;
    private final ProcessorFactory processorFactory;

    public Server(final Socket socket, final String rootDir, final RequestPatternService patternService, final ProcessorFactory processorFactory) {
        this.socket = socket;

        this.rootDir = rootDir;
        this.patternService = patternService;
        this.processorFactory = processorFactory;
    }

    @Override
    public void run() {
        final Request request = new Request();
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             final PrintWriter headerOut = new PrintWriter(socket.getOutputStream());
             final BufferedOutputStream payloadOut = new BufferedOutputStream(socket.getOutputStream())) {

            String line;
            while (isNotEmpty(line = in.readLine())) {
                patternService.fillRequestWithData(request, line);
            }

            final Processor requestProcessor = processorFactory.getRequestProcessor(request.getMethod());
            requestProcessor.setRootDir(rootDir);

            logger.debug("Processing {} request", request.getMethod());
            final Response response = requestProcessor.process(request);

            logger.debug("Sending response {}", response.getHttpResult());
            headerOut.println(response.getHttpResult());
            for (final Map.Entry<String, String> entry : response.getHeaders().entrySet()) {
                headerOut.println(entry.getKey() + ": " + entry.getValue());
            }

            headerOut.println();
            headerOut.flush();

            payloadOut.write(response.getPayload(), 0, response.getPayload().length);
            payloadOut.flush();

        } catch (final IOException e) {
            logger.error("Server processing request error: {}", e.getMessage());
        }
    }

    private boolean isNotEmpty(final String str) {
        return (str != null) && !str.isEmpty();
    }

}
