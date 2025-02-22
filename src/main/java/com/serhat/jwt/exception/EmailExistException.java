package com.serhat.jwt.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EmailExistException extends RuntimeException {
    public EmailExistException(String s) {
        super(s);
    }
}
