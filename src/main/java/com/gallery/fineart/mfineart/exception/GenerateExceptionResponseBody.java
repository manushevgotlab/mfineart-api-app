package com.gallery.fineart.mfineart.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class GenerateExceptionResponseBody {

    private GenerateExceptionResponseBody() {
        // prevent instantiation
    }

    public static Map<String, Object> generateBodyResponse(Exception ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        return body;
    }
}
