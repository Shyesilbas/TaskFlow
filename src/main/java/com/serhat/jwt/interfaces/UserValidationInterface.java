package com.serhat.jwt.interfaces;

import com.serhat.jwt.dto.requests.RegisterRequest;

public interface UserValidationInterface {
    void validateUserRegistration(RegisterRequest request);

}
