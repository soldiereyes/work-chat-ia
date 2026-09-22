package com.workchatia.identity.infrastructure.web;

import com.workchatia.identity.domain.AuthenticatedUser;
import com.workchatia.identity.domain.PermissionCode;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me")
public class MeController {

    @GetMapping
    public MeResponse me() {
        AuthenticatedUser user = SecuritySupport.currentUser();
        List<String> permissions =
                user.permissions().stream().map(PermissionCode::name).sorted().toList();
        return new MeResponse(user.userId(), user.accountId(), user.email(), permissions);
    }

    public record MeResponse(UUID userId, UUID accountId, String email, List<String> permissions) {}
}
