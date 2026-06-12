package com.gallery.fineart.mfineart.exception.content;

import com.gallery.fineart.mfineart.enumeration.ContentStatus;

public class InvalidContentStatusTransitionException extends RuntimeException {

    public InvalidContentStatusTransitionException(ContentStatus from, ContentStatus to) {
        super(String.format("Invalid content status transition from %s to %s", from, to));
    }
}
