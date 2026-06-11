package com.gallery.fineart.mfineart.exception.collection;

public class CollectionNotFoundException extends RuntimeException {
    public CollectionNotFoundException(String var) {
        super(String.format("Collection with id or name %s not found", var));
    }
}
