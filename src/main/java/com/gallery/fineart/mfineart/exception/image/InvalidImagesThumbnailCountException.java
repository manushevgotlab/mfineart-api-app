package com.gallery.fineart.mfineart.exception.image;

public class InvalidImagesThumbnailCountException extends RuntimeException {
    public InvalidImagesThumbnailCountException(int imagesCount, long thumbnailCount) {
        super(String.format("Collection of images (with size %s) should have exactly 1 thumbnail, but was=%s ", imagesCount, thumbnailCount));
    }
}
