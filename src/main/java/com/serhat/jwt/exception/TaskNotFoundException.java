package com.serhat.jwt.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String s) {
        super(s);
    }
}
