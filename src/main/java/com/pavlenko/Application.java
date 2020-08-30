package com.pavlenko;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Application {
    private static final Logger logger = LogManager.getLogger();
    private static final int PORT = 8080;

    public static void main(final String[] args) {
        if (args.length < 1) {
            logger.error("No root directory path defined!");
            logger.error("Usage: jar -jar app.jar <rootDirPath>");
            logger.error("Example(mac): jar -jar app.jar /Users/$(whoami)");
            return;
        }

        final String rootDirPath = args[0];
        final File rootDir = new File(rootDirPath);
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            logger.error("Specified path is not a directory: {}", rootDirPath);
            return;
        }

        try {
            logger.info("Creating server");

            final Server server = new Server(PORT, rootDir.getAbsolutePath());
            server.start();
        } catch (final IOException e) {
            logger.error("Server Connection error: {}", e.getMessage());
        }
    }
}
