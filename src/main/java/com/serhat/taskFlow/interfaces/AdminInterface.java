package com.serhat.taskFlow.interfaces;

import com.serhat.taskFlow.entity.Admin;
import com.serhat.taskFlow.entity.AppUser;

public interface AdminInterface {
    Admin findById(Long id);
    Admin findByUsername(String username);
}
