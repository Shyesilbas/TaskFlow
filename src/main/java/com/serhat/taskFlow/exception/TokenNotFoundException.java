package com.serhat.taskFlow.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException(String s) {
        super(s);
    }
}
