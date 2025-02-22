package com.serhat.jwt.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String s) {
        super(s);
    }
}
