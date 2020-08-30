package com.pavlenko.dto;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class Response {
    private static final byte[] EMPTY = new byte[0];
    private int httpCode;
    private Map<String, String> headers = new HashMap<>();
    private byte[] payload = EMPTY;

    public void addHeader(final String key, final String value) {
        headers.put(key, value);
    }
}
