package com.gallery.fineart.mfineart.exception.painting;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

import static com.gallery.fineart.mfineart.exception.GenerateExceptionResponseBody.generateBodyResponse;


@ControllerAdvice
public class PaintingControllerAdvisor {

    @ExceptionHandler(PaintingNotFoundException.class)
    public ResponseEntity<Object> paintingNotFound(Exception ex) {
        Map<String, Object> body = generateBodyResponse(ex);

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
}
