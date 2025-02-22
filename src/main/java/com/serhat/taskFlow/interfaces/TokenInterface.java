package com.serhat.taskFlow.interfaces;

import com.serhat.taskFlow.entity.AppUser;
import com.serhat.taskFlow.entity.enums.Role;
import jakarta.servlet.http.HttpServletRequest;

public interface TokenInterface {
    String extractTokenFromRequest(HttpServletRequest request);
    AppUser getUserFromToken(HttpServletRequest request);

    void validateRole(HttpServletRequest request, Role... allowedRoles);
}
