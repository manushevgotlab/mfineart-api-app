package com.gallery.fineart.mfineart.exception.collection;

public class ImagesForCollectionNotFoundException extends RuntimeException {
    public ImagesForCollectionNotFoundException(String collectionName, String imageName) {
        super(String.format("Collection name=%s does not match any prefix of image with name=%s.", collectionName, imageName));
    }
}
