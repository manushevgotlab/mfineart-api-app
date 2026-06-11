package com.gallery.fineart.mfineart.exception.image;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

import static com.gallery.fineart.mfineart.exception.GenerateExceptionResponseBody.generateBodyResponse;

@ControllerAdvice
public class ImageControllerAdvisor {

    @ExceptionHandler(ImageNotFoundException.class)
    public ResponseEntity<Object> imageNotFound(Exception ex) {
        Map<String, Object> body = generateBodyResponse(ex);

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ImagesForPaintingNotFoundException.class)
    public ResponseEntity<Object> imagesForPaintingNotFound(Exception ex) {
        Map<String, Object> body = generateBodyResponse(ex);

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
}
