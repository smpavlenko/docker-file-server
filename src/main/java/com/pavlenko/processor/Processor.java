package com.pavlenko.processor;

import com.pavlenko.dto.Request;
import com.pavlenko.dto.Response;

public interface Processor {
    void setRootDir(final String rootDir);

    Response process(final Request request);

}
