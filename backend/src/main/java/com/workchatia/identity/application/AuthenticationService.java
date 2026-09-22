package com.workchatia.identity.application;

import com.workchatia.identity.domain.IdentityProvider;
import com.workchatia.identity.infrastructure.security.JwtService;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final IdentityProvider identityProvider;
    private final JwtService jwtService;

    public AuthenticationService(IdentityProvider identityProvider, JwtService jwtService) {
        this.identityProvider = identityProvider;
        this.jwtService = jwtService;
    }

    public AuthenticationResult authenticate(LoginCommand command) {
        var user = identityProvider
                .authenticate(command.email(), command.password())
                .orElseThrow(InvalidCredentialsException::new);
        String token = jwtService.generateToken(user);
        return new AuthenticationResult(token, "Bearer");
    }
}
