package com.devvault.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String entityName, Long id) {
        super(entityName + " not found with id: " + id);
    }
}
