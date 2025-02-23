package com.serhat.taskFlow.exception;


public class UserAlreadyAssignedToAdminException extends RuntimeException {
    public UserAlreadyAssignedToAdminException(String s) {
        super(s);
    }
}
