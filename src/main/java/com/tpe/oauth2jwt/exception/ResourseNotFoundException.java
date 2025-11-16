package com.tpe.oauth2jwt.exception;

public class ResourseNotFoundException extends RuntimeException {
    public ResourseNotFoundException(String message) {
        super(message);
    }
}
