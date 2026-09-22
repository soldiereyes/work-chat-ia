package com.workchatia.identity.domain;

import java.util.Set;
import java.util.UUID;

public record AuthenticatedUser(UUID userId, UUID accountId, String email, Set<PermissionCode> permissions) {

    public boolean hasPermission(PermissionCode permission) {
        return permissions.contains(permission);
    }
}
