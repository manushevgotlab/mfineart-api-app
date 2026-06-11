package com.gallery.fineart.mfineart.exception.painting;

public class PaintingNotFoundException extends RuntimeException {
    public PaintingNotFoundException(String var) {
        super(String.format("Painting with id or name %s not found", var));
    }
}
