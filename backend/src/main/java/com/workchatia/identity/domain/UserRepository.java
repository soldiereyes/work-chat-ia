package com.workchatia.identity.domain;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<UserProfile> findProfileById(UUID userId);
}
