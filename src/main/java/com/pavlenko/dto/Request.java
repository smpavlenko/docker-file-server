package com.pavlenko.dto;

import lombok.Data;

import java.util.Map;
import java.util.TreeMap;

@Data
public class Request {
    private String method;
    private String path;
    private Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public void addHeader(final String key, final String value) {
        headers.put(key, value);
    }
}
