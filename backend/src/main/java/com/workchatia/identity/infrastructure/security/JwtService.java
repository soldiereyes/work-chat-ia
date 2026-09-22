package com.workchatia.identity.infrastructure.security;

import com.workchatia.identity.domain.AuthenticatedUser;
import com.workchatia.identity.domain.PermissionCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final JwtProperties properties;
    private final SecretKey key;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        this.key = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(AuthenticatedUser user) {
        Instant now = Instant.now();
        List<String> permissionCodes =
                user.permissions().stream().map(Enum::name).sorted().collect(Collectors.toList());
        return Jwts.builder()
                .subject(user.userId().toString())
                .claim("accountId", user.accountId().toString())
                .claim("email", user.email())
                .claim("permissions", permissionCodes)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(properties.getExpirationMinutes(), ChronoUnit.MINUTES)))
                .signWith(key)
                .compact();
    }

    public AuthenticatedUser parseToken(String token) {
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        UUID userId = UUID.fromString(claims.getSubject());
        UUID accountId = UUID.fromString(claims.get("accountId", String.class));
        String email = claims.get("email", String.class);
        Set<PermissionCode> permissions = new LinkedHashSet<>();
        List<?> raw = claims.get("permissions", List.class);
        if (raw != null) {
            raw.forEach(item -> {
                try {
                    permissions.add(PermissionCode.valueOf(String.valueOf(item)));
                } catch (IllegalArgumentException ignored) {
                    // skip
                }
            });
        }
        return new AuthenticatedUser(userId, accountId, email, permissions);
    }
}
