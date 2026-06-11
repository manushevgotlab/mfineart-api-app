package com.gallery.fineart.mfineart.exception;

public class InvalidThumbnailException extends RuntimeException {
    public InvalidThumbnailException(String id, Long count) {
      super(String.format("Invalid number of thumbnails for object with id or name=%s, needed=1, found=%s", id, count));
    }
}
