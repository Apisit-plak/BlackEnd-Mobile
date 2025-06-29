package com.example.IOT_HELL.Exception;

import lombok.Getter;

import java.util.List;

public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException(String message) {
        super(message);
    }

    public static class BadCredentialsException extends AlreadyExistsException {
        public BadCredentialsException(String message) {
            super(message);
        }
    }

    public static class DataNotFoundException extends AlreadyExistsException{
        public DataNotFoundException(String message){
            super(message);
        }
    }

    @Getter
    public static class UserAlreadyExistsException extends RuntimeException {
        private final List<String> errors;

        public UserAlreadyExistsException(List<String> errors) {
            super("User creation failed due to existing values");
            this.errors = errors;
        }

    }
}
