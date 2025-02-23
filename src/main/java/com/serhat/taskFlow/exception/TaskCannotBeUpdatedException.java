package com.serhat.taskFlow.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TaskCannotBeUpdatedException extends RuntimeException {
    public TaskCannotBeUpdatedException(String s) {
        super(s);
    }
}
