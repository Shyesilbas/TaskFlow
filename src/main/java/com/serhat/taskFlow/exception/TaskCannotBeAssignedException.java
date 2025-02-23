package com.serhat.taskFlow.exception;

public class TaskCannotBeAssignedException extends RuntimeException {
    public TaskCannotBeAssignedException(String s) {
        super(s);
    }
}
