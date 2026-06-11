package com.gallery.fineart.mfineart.exception.collection;

public class CollectionWithNameAlreadyExists extends RuntimeException {
    public CollectionWithNameAlreadyExists(String name) {
        super(String.format("Collection with name=%s already exists.", name));
    }
}
