package com.workchatia.identity.infrastructure.web;

import com.workchatia.identity.application.AuthorizationService;
import com.workchatia.identity.domain.AuthenticatedUser;
import com.workchatia.identity.domain.PermissionCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Probe temporário para validar {@code SEND_MESSAGE} no MVP-01.
 * Substituir pelo {@code MessageController} real no MVP-03.
 */
@Deprecated(since = "0.0.1", forRemoval = false)
@RestController
@RequestMapping("/api/v1/messages")
public class MessageAuthorizationProbeController {

    private final AuthorizationService authorizationService;

    public MessageAuthorizationProbeController(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void probeSendMessage() {
        AuthenticatedUser user = SecuritySupport.currentUser();
        authorizationService.requirePermission(user, PermissionCode.SEND_MESSAGE);
    }
}
