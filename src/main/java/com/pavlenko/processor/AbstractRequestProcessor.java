package com.pavlenko.processor;

import com.pavlenko.dto.Request;
import com.pavlenko.dto.Response;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@RequiredArgsConstructor
public abstract class AbstractRequestProcessor implements Processor {
    private static final Logger logger = LogManager.getLogger();

    private String rootDir;

    @Override
    public void setRootDir(final String rootDir) {
        this.rootDir = rootDir;
    }

    @Override
    public Response process(final Request request) {
        final String filePath = request.getPath();
        final File file = new File(rootDir + filePath);
        if (!file.exists()) {
            logger.debug("File {} does not exist", filePath);
            return processNotFoundRequest();
        }

        if (file.isDirectory()) {
            logger.debug("File {} is directory", filePath);
            return processDirectoryRequest(request, file);
        } else {
            return processFileRequest(request, file);
        }
    }

    protected abstract Response processFileRequest(final Request request, File file);

    protected abstract Response processDirectoryRequest(final Request request, File file);

    protected abstract Response processNotFoundRequest();

}
