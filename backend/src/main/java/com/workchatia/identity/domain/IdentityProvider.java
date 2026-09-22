package com.workchatia.identity.domain;

import java.util.Optional;

public interface IdentityProvider {

    Optional<AuthenticatedUser> authenticate(String email, String rawPassword);
}
