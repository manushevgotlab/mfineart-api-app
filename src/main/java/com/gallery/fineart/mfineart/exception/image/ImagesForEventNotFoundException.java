package com.gallery.fineart.mfineart.exception.image;

public class ImagesForEventNotFoundException extends RuntimeException {
    public ImagesForEventNotFoundException(String eventName, String imageName) {
      super(String.format("Event name=%s does not match any prefix of image with name=%s.", eventName, imageName));
    }
}
