package com.serhat.taskFlow.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AdminNotFoundException extends RuntimeException {
    public AdminNotFoundException(String s) {
        super(s);
    }
}
