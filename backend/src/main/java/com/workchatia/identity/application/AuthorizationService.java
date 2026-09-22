package com.workchatia.identity.application;

import com.workchatia.identity.domain.AuthenticatedUser;
import com.workchatia.identity.domain.PermissionCode;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    public void requirePermission(AuthenticatedUser user, PermissionCode permission) {
        if (user == null || !user.hasPermission(permission)) {
            throw new ForbiddenException("Missing permission: " + permission);
        }
    }
}
