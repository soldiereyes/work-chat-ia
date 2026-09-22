package com.workchatia.identity.infrastructure;

import com.workchatia.identity.domain.UserProfile;
import com.workchatia.identity.domain.UserRepository;
import com.workchatia.identity.infrastructure.persistence.UserJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JpaUserRepository implements UserRepository {

    private final UserJpaRepository jpa;

    public JpaUserRepository(UserJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<UserProfile> findProfileById(UUID userId) {
        return jpa.findById(userId)
                .map(u -> new UserProfile(u.getId(), u.getAccountId(), u.getEmail(), u.getDisplayName()));
    }
}
