package com.serhat.taskFlow.interfaces;

import com.serhat.taskFlow.dto.requests.RegisterRequest;

public interface UserValidationInterface {
    void validateUserRegistration(RegisterRequest request);

}
