package com.gallery.fineart.mfineart.exception.painting;

public class InvalidPaintingParam extends RuntimeException {
    public InvalidPaintingParam(String var) {
        super(String.format("Invalid Painting param found: %s", var));
    }
}
