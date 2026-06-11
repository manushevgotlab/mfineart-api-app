package com.gallery.fineart.mfineart.exception.image;

public class MissingEntityBoundForImage extends RuntimeException {
    public MissingEntityBoundForImage(Long id) {
        super(String.format("Missing entity bound for image: %s", id));
    }
}
