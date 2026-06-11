package com.gallery.fineart.mfineart.exception.image;

public class ImageNotFoundException extends RuntimeException {
    public ImageNotFoundException(String var) {
        super(String.format("Image with id or name prefix %s not found", var));
    }
}
