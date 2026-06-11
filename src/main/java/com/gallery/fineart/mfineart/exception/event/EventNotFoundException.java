package com.gallery.fineart.mfineart.exception.event;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(String var) {
        super(String.format("Event with id or name %s not found", var));
    }
}
