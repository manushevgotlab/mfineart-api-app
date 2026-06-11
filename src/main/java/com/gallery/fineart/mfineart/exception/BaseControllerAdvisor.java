package com.gallery.fineart.mfineart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

import static com.gallery.fineart.mfineart.exception.GenerateExceptionResponseBody.generateBodyResponse;

@ControllerAdvice
public class BaseControllerAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> body = generateBodyResponse(ex);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }


}
