package com.pavlenko;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pavlenko.controller.Controller;
import com.sun.net.httpserver.HttpServer;

public class Server {
    private static final Logger logger = LogManager.getLogger();

    private final String rootDir;
    private final HttpServer server;

    public Server(final int port, final String rootDir) throws IOException {
        this.rootDir = rootDir;
        server = HttpServer.create(new InetSocketAddress(port), 0);
    }

    void start() {
        final var module = new Module();
        final Controller controller = new Controller(rootDir, module.processorFactory);

        final ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        server.createContext("/", controller);
        server.setExecutor(threadPoolExecutor);
        server.start();

        logger.info("Server is started");
        logger.info("Listening port: {}", server.getAddress().getPort());
    }
}
