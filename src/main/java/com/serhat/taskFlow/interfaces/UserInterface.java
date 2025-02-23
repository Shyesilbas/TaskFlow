package com.serhat.taskFlow.interfaces;

import com.serhat.taskFlow.entity.AppUser;

public interface UserInterface {
    AppUser findById(Long id);
    AppUser findByUsername(String username);
}
