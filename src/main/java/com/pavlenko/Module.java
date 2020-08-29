package com.pavlenko;

import com.pavlenko.processor.GetRequestProcessor;
import com.pavlenko.processor.HeadRequestProcessor;
import com.pavlenko.processor.ProcessorFactory;
import com.pavlenko.processor.UnsupportedRequestProcessor;
import com.pavlenko.service.FileService;
import com.pavlenko.service.HtmlService;
import com.pavlenko.service.RequestPatternService;
import com.pavlenko.service.ResponseService;

class Module {
    private final FileService fileService = new FileService();
    private final HtmlService htmlService = new HtmlService();
    private final ResponseService responseService = new ResponseService();

    private final HeadRequestProcessor headRequestProcessor = new HeadRequestProcessor(fileService, responseService);
    private final GetRequestProcessor getRequestProcessor = new GetRequestProcessor(fileService, responseService, htmlService);
    private final UnsupportedRequestProcessor unsupportedRequestProcessor = new UnsupportedRequestProcessor(fileService, responseService);

    final RequestPatternService requestPatternService = new RequestPatternService();
    final ProcessorFactory processorFactory = new ProcessorFactory(headRequestProcessor, getRequestProcessor, unsupportedRequestProcessor);
}
