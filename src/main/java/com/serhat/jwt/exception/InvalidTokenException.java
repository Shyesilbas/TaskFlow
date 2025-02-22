package com.serhat.jwt.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String s) {
        super(s);
    }
}
