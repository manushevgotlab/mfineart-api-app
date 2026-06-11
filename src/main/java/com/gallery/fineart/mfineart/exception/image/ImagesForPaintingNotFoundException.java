package com.gallery.fineart.mfineart.exception.image;

public class ImagesForPaintingNotFoundException extends RuntimeException {
    public ImagesForPaintingNotFoundException(String paintingName, String imageName) {
        super(String.format("Painting name=%s does not match any prefix of image with name=%s.", paintingName, imageName));
    }
}
