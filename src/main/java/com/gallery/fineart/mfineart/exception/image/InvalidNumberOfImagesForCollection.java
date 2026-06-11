package com.gallery.fineart.mfineart.exception.image;

public class InvalidNumberOfImagesForCollection extends RuntimeException {
    public InvalidNumberOfImagesForCollection(int expectedCount, long providedCount) {
        super(String.format("ArtCollection should have exactly %s images, but was=%s ", expectedCount, providedCount));
    }
}
