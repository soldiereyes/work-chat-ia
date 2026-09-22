package com.workchatia.shared.application;

public class DomainConflictException extends RuntimeException {

    public DomainConflictException(String message) {
        super(message);
    }
}
