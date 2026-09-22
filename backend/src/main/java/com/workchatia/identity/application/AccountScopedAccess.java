package com.workchatia.identity.application;

import com.workchatia.identity.domain.AuthenticatedUser;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AccountScopedAccess {

    public void requireSameAccount(AuthenticatedUser requester, UUID resourceAccountId) {
        if (requester == null || resourceAccountId == null || !requester.accountId().equals(resourceAccountId)) {
            throw new ForbiddenException("Cross-account access denied");
        }
    }
}
