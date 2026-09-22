package com.workchatia.identity.infrastructure;

import com.workchatia.identity.domain.AuthenticatedUser;
import com.workchatia.identity.domain.IdentityProvider;
import com.workchatia.identity.domain.PermissionCode;
import com.workchatia.identity.infrastructure.persistence.PermissionEntity;
import com.workchatia.identity.infrastructure.persistence.UserEntity;
import com.workchatia.identity.infrastructure.persistence.UserJpaRepository;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DbIdentityProvider implements IdentityProvider {

    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DbIdentityProvider(UserJpaRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<AuthenticatedUser> authenticate(String email, String rawPassword) {
        return userRepository
                .findByEmail(email.trim().toLowerCase())
                .filter(UserEntity::isEnabled)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPasswordHash()))
                .map(this::toAuthenticatedUser);
    }

    private AuthenticatedUser toAuthenticatedUser(UserEntity user) {
        Set<PermissionCode> permissions = new LinkedHashSet<>();
        user.getRoles().forEach(role -> role.getPermissions().forEach(p -> mapPermission(p, permissions)));
        return new AuthenticatedUser(user.getId(), user.getAccountId(), user.getEmail(), permissions);
    }

    private void mapPermission(PermissionEntity entity, Set<PermissionCode> target) {
        try {
            target.add(PermissionCode.valueOf(entity.getCode()));
        } catch (IllegalArgumentException ignored) {
            // unknown permission codes are ignored in MVP
        }
    }
}
