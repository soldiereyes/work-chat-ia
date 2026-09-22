package com.workchatia.identity.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.workchatia.identity.domain.AuthenticatedUser;
import com.workchatia.identity.domain.PermissionCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private static final String SECRET = "test-secret-key-with-enough-length-for-hs256-algorithm-required";

    @Test
    void parseTokenRejectsExpiredJwt() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret(SECRET);
        properties.setExpirationMinutes(60);
        JwtService jwtService = new JwtService(properties);

        String expired = expiredToken(SECRET);

        assertThatThrownBy(() -> jwtService.parseToken(expired)).isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void generateAndParseRoundTrip() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret(SECRET);
        properties.setExpirationMinutes(60);
        JwtService jwtService = new JwtService(properties);

        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        AuthenticatedUser user =
                new AuthenticatedUser(userId, accountId, "user@test.com", Set.of(PermissionCode.SEND_MESSAGE));

        String token = jwtService.generateToken(user);
        AuthenticatedUser parsed = jwtService.parseToken(token);

        assertThat(parsed.userId()).isEqualTo(userId);
        assertThat(parsed.accountId()).isEqualTo(accountId);
        assertThat(parsed.email()).isEqualTo("user@test.com");
        assertThat(parsed.hasPermission(PermissionCode.SEND_MESSAGE)).isTrue();
    }

    private static String expiredToken(String secret) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Instant past = Instant.now().minus(1, ChronoUnit.HOURS);
        return Jwts.builder()
                .subject(UUID.randomUUID().toString())
                .claim("accountId", UUID.randomUUID().toString())
                .claim("email", "expired@test.com")
                .claim("permissions", List.of("SEND_MESSAGE"))
                .issuedAt(Date.from(past.minus(1, ChronoUnit.HOURS)))
                .expiration(Date.from(past))
                .signWith(key)
                .compact();
    }
}
