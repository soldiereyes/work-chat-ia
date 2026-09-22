package com.workchatia.identity;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthenticationControllerTest {

    private static final UUID ALICE_USER_ID = UUID.fromString("22222222-2222-2222-2222-222222222201");
    private static final UUID BOB_USER_ID = UUID.fromString("22222222-2222-2222-2222-222222222202");

    private static final String DEFAULT_JDBC_URL = "jdbc:postgresql://localhost:55432/workchat";

    @DynamicPropertySource
    static void datasourceProps(DynamicPropertyRegistry registry) {
        String fromEnv = System.getenv("TEST_JDBC_URL");
        final String jdbcUrl =
                (fromEnv == null || fromEnv.isBlank()) ? DEFAULT_JDBC_URL : fromEnv;
        registry.add("spring.datasource.url", () -> jdbcUrl);
        registry.add("spring.datasource.username", () -> System.getenv().getOrDefault("TEST_DB_USER", "workchat"));
        registry.add("spring.datasource.password", () -> System.getenv().getOrDefault("TEST_DB_PASSWORD", "workchat"));
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("${workchat.security.jwt.secret}")
    private String jwtSecret;

    @Test
    void loginWithValidCredentialsReturnsToken() {
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/v1/auth/login", Map.of("email", "alice@workchat.test", "password", "secret123"), Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKeys("accessToken", "tokenType");
        assertThat(response.getBody().get("tokenType")).isEqualTo("Bearer");
        assertThat(response.getBody().get("accessToken")).isNotNull();
    }

    @Test
    void loginWithInvalidCredentialsReturns401() {
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/v1/auth/login", Map.of("email", "alice@workchat.test", "password", "wrong"), Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void loginWithInvalidEmailFormatReturns400() {
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/v1/auth/login", Map.of("email", "not-an-email", "password", "x"), Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", "validation_failed");
    }

    @Test
    void sendMessageWithoutPermissionReturns403() {
        String token = loginToken("viewer@workchat.test", "secret123");
        HttpHeaders headers = authHeaders(token);
        ResponseEntity<Void> response =
                restTemplate.exchange("/api/v1/messages", HttpMethod.POST, new HttpEntity<>(headers), Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void sendMessageWithPermissionReturns204() {
        String token = loginToken("alice@workchat.test", "secret123");
        HttpHeaders headers = authHeaders(token);
        ResponseEntity<Void> response =
                restTemplate.exchange("/api/v1/messages", HttpMethod.POST, new HttpEntity<>(headers), Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void protectedRouteWithoutTokenReturns401() {
        ResponseEntity<Void> response =
                restTemplate.exchange("/api/v1/messages", HttpMethod.POST, HttpEntity.EMPTY, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void protectedRouteWithInvalidTokenReturns401() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("not-a-valid-jwt");
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/v1/messages", HttpMethod.POST, new HttpEntity<>(headers), Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void protectedRouteWithExpiredTokenReturns401() {
        HttpHeaders headers = authHeaders(expiredJwt());
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/v1/me", HttpMethod.GET, new HttpEntity<>(headers), Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void sameAccountUserAccessReturns200() {
        String token = loginToken("alice@workchat.test", "secret123");
        HttpHeaders headers = authHeaders(token);
        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/v1/users/" + ALICE_USER_ID, HttpMethod.GET, new HttpEntity<>(headers), Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("id", ALICE_USER_ID.toString());
    }

    @Test
    void meEndpointReturnsCurrentUser() {
        String token = loginToken("alice@workchat.test", "secret123");
        HttpHeaders headers = authHeaders(token);
        ResponseEntity<Map> response =
                restTemplate.exchange("/api/v1/me", HttpMethod.GET, new HttpEntity<>(headers), Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("email", "alice@workchat.test");
    }

    @Test
    void crossAccountUserAccessReturns403() {
        String token = loginToken("alice@workchat.test", "secret123");
        HttpHeaders headers = authHeaders(token);
        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/v1/users/" + BOB_USER_ID, HttpMethod.GET, new HttpEntity<>(headers), Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private String expiredJwt() {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Instant past = Instant.now().minus(1, ChronoUnit.HOURS);
        return Jwts.builder()
                .subject(ALICE_USER_ID.toString())
                .claim("accountId", "11111111-1111-1111-1111-111111111101")
                .claim("email", "alice@workchat.test")
                .claim("permissions", List.of("SEND_MESSAGE"))
                .issuedAt(Date.from(past.minus(1, ChronoUnit.HOURS)))
                .expiration(Date.from(past))
                .signWith(key)
                .compact();
    }

    private String loginToken(String email, String password) {
        ResponseEntity<Map> response =
                restTemplate.postForEntity("/api/v1/auth/login", Map.of("email", email, "password", password), Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return (String) response.getBody().get("accessToken");
    }

    private static HttpHeaders authHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }
}
