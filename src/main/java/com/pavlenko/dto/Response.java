package com.pavlenko.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Response {
    private String httpResult;
    private Map<String, String> headers = new HashMap<>();
    private byte[] payload;

    public void addHeader(final String key, final String value) {
        headers.put(key, value);
    }
}
