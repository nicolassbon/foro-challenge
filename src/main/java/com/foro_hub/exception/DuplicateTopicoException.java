package com.foro_hub.exception;

public class DuplicateTopicoException extends RuntimeException {
    public DuplicateTopicoException(final String message) {
        super(message);
    }
}
