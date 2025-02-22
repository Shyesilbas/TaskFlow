package com.serhat.jwt.interfaces;

public interface PasswordValidationInterface {
    void validatePassword(String rawPassword, String encodedPassword);
}
