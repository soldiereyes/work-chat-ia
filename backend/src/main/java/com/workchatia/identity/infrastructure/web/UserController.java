package com.workchatia.identity.infrastructure.web;

import com.workchatia.identity.application.AccountScopedAccess;
import com.workchatia.identity.domain.AuthenticatedUser;
import com.workchatia.identity.domain.UserProfile;
import com.workchatia.identity.domain.UserRepository;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserRepository userRepository;
    private final AccountScopedAccess accountScopedAccess;

    public UserController(UserRepository userRepository, AccountScopedAccess accountScopedAccess) {
        this.userRepository = userRepository;
        this.accountScopedAccess = accountScopedAccess;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID id) {
        AuthenticatedUser requester = SecuritySupport.currentUser();
        return userRepository
                .findProfileById(id)
                .map(profile -> {
                    accountScopedAccess.requireSameAccount(requester, profile.accountId());
                    return ResponseEntity.ok(UserResponse.from(profile));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    public record UserResponse(UUID id, UUID accountId, String email, String displayName) {
        static UserResponse from(UserProfile profile) {
            return new UserResponse(profile.id(), profile.accountId(), profile.email(), profile.displayName());
        }
    }
}
