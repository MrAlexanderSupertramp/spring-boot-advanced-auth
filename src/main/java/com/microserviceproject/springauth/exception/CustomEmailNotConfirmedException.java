package com.microserviceproject.springauth.exception;

import org.springframework.security.core.AuthenticationException;

public class CustomEmailNotConfirmedException extends AuthenticationException {
    String message;

    public CustomEmailNotConfirmedException(String message) {
        super(String.format(message));
        this.message = message;
    }
}
