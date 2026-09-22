package com.workchatia.conversation;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workchatia.shared.infrastructure.persistence.OutboxEventEntity;
import com.workchatia.shared.infrastructure.persistence.OutboxEventJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
class ConversationMessageControllerTest {

    private static final String DEFAULT_JDBC_URL = "jdbc:postgresql://localhost:55432/workchat";
    private static final UUID ACCOUNT_ALPHA_ID = UUID.fromString("11111111-1111-1111-1111-111111111101");
    private static final UUID ALICE_USER_ID = UUID.fromString("22222222-2222-2222-2222-222222222201");

    @DynamicPropertySource
    static void datasourceProps(DynamicPropertyRegistry registry) {
        String fromEnv = System.getenv("TEST_JDBC_URL");
        final String jdbcUrl = (fromEnv == null || fromEnv.isBlank()) ? DEFAULT_JDBC_URL : fromEnv;
        registry.add("spring.datasource.url", () -> jdbcUrl);
        registry.add("spring.datasource.username", () -> System.getenv().getOrDefault("TEST_DB_USER", "workchat"));
        registry.add("spring.datasource.password", () -> System.getenv().getOrDefault("TEST_DB_PASSWORD", "workchat"));
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OutboxEventJpaRepository outboxEventJpaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getConversationByIdReturns200ForOwner() {
        String token = loginToken("alice@workchat.test", "secret123");
        HttpHeaders headers = authHeaders(token);
        UUID conversationId = createConversation("alice@workchat.test");

        ResponseEntity<Map> getResponse = restTemplate.exchange(
                "/api/v1/conversations/" + conversationId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).containsEntry("id", conversationId.toString());
        assertThat(getResponse.getBody()).containsEntry("status", "OPEN");
        assertThat(getResponse.getBody()).containsEntry("accountId", ACCOUNT_ALPHA_ID.toString());
    }

    @Test
    void listAndGetMessageReturns200() {
        UUID conversationId = createConversation("alice@workchat.test");
        HttpHeaders headers = authHeaders(loginToken("alice@workchat.test", "secret123"));

        ResponseEntity<Map> createResponse = restTemplate.exchange(
                "/api/v1/conversations/" + conversationId + "/messages",
                HttpMethod.POST,
                new HttpEntity<>(Map.of("content", "list-me"), headers),
                Map.class);
        UUID messageId = UUID.fromString((String) createResponse.getBody().get("id"));

        ResponseEntity<List> listResponse = restTemplate.exchange(
                "/api/v1/conversations/" + conversationId + "/messages",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                List.class);
        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResponse.getBody()).isNotEmpty();

        ResponseEntity<Map> getResponse = restTemplate.exchange(
                "/api/v1/conversations/" + conversationId + "/messages/" + messageId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).containsEntry("content", "list-me");
        assertThat(getResponse.getBody()).containsEntry("status", "DRAFT");
        assertThat(getResponse.getBody()).containsEntry("conversationId", conversationId.toString());
    }

    @Test
    void createConversationAppearsInListForSameAccount() {
        String token = loginToken("alice@workchat.test", "secret123");
        HttpHeaders headers = authHeaders(token);

        ResponseEntity<Map> createResponse = restTemplate.exchange(
                "/api/v1/conversations",
                HttpMethod.POST,
                new HttpEntity<>(headers),
                Map.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String conversationId = (String) createResponse.getBody().get("id");

        ResponseEntity<Map> listResponse = restTemplate.exchange(
                "/api/v1/conversations?size=50",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class);
        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Map<String, Object>> content = (List<Map<String, Object>>) listResponse.getBody().get("content");
        assertThat(content.stream().anyMatch(row -> conversationId.equals(row.get("id")))).isTrue();
    }

    @Test
    void crossAccountConversationAccessReturns404() {
        String aliceToken = loginToken("alice@workchat.test", "secret123");
        HttpHeaders aliceHeaders = authHeaders(aliceToken);
        ResponseEntity<Map> createResponse = restTemplate.exchange(
                "/api/v1/conversations",
                HttpMethod.POST,
                new HttpEntity<>(aliceHeaders),
                Map.class);
        UUID conversationId = UUID.fromString((String) createResponse.getBody().get("id"));

        String bobToken = loginToken("bob@workchat.test", "secret123");
        HttpHeaders bobHeaders = authHeaders(bobToken);
        ResponseEntity<Map> getResponse = restTemplate.exchange(
                "/api/v1/conversations/" + conversationId,
                HttpMethod.GET,
                new HttpEntity<>(bobHeaders),
                Map.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createMessagePersistsDraftAndOutboxEvent() throws Exception {
        UUID conversationId = createConversation("alice@workchat.test");
        HttpHeaders headers = authHeaders(loginToken("alice@workchat.test", "secret123"));

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/v1/conversations/" + conversationId + "/messages",
                HttpMethod.POST,
                new HttpEntity<>(Map.of("content", "Olá paciente"), headers),
                Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).containsEntry("status", "DRAFT");
        UUID messageId = UUID.fromString((String) response.getBody().get("id"));

        OutboxEventEntity event = outboxEventJpaRepository.findAll().stream()
                .filter(e -> e.getPayload().contains(messageId.toString()))
                .findFirst()
                .orElseThrow();
        assertThat(event.getEventType()).isEqualTo("MessageCreated");
        assertThat(event.getVersion()).isEqualTo(1);
        assertThat(event.getId()).isNotNull();

        JsonNode payload = objectMapper.readTree(event.getPayload());
        assertThat(payload.get("eventId").asText()).isEqualTo(event.getId().toString());
        assertThat(payload.get("eventType").asText()).isEqualTo("MessageCreated");
        assertThat(payload.get("version").asInt()).isEqualTo(1);
        assertThat(UUID.fromString(payload.get("accountId").asText())).isEqualTo(ACCOUNT_ALPHA_ID);
        assertThat(UUID.fromString(payload.get("conversationId").asText())).isEqualTo(conversationId);
        assertThat(UUID.fromString(payload.get("messageId").asText())).isEqualTo(messageId);
        assertThat(UUID.fromString(payload.get("authorId").asText())).isEqualTo(ALICE_USER_ID);
        assertThat(payload.get("messageType").asText()).isEqualTo("TEXT");
    }

    @Test
    void editMessageByNonAuthorReturns403() {
        UUID conversationId = createConversation("alice@workchat.test");
        HttpHeaders aliceHeaders = authHeaders(loginToken("alice@workchat.test", "secret123"));

        ResponseEntity<Map> createResponse = restTemplate.exchange(
                "/api/v1/conversations/" + conversationId + "/messages",
                HttpMethod.POST,
                new HttpEntity<>(Map.of("content", "owned by alice"), aliceHeaders),
                Map.class);
        UUID messageId = UUID.fromString((String) createResponse.getBody().get("id"));

        HttpHeaders carolHeaders = authHeaders(loginToken("carol@workchat.test", "secret123"));
        ResponseEntity<Map> editResponse = restTemplate.exchange(
                "/api/v1/conversations/" + conversationId + "/messages/" + messageId,
                HttpMethod.PATCH,
                new HttpEntity<>(Map.of("content", "stolen"), carolHeaders),
                Map.class);

        assertThat(editResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(editResponse.getBody()).containsEntry("error", "message_edit_forbidden");
    }

    @Test
    void closedConversationRejectsNewMessage() {
        UUID conversationId = createConversation("alice@workchat.test");
        HttpHeaders headers = authHeaders(loginToken("alice@workchat.test", "secret123"));

        restTemplate.exchange(
                "/api/v1/conversations/" + conversationId,
                HttpMethod.PATCH,
                new HttpEntity<>(Map.of("action", "close"), headers),
                Map.class);

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/v1/conversations/" + conversationId + "/messages",
                HttpMethod.POST,
                new HttpEntity<>(Map.of("content", "blocked"), headers),
                Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void editOwnDraftMessageReturns200() {
        UUID conversationId = createConversation("alice@workchat.test");
        HttpHeaders headers = authHeaders(loginToken("alice@workchat.test", "secret123"));

        ResponseEntity<Map> createResponse = restTemplate.exchange(
                "/api/v1/conversations/" + conversationId + "/messages",
                HttpMethod.POST,
                new HttpEntity<>(Map.of("content", "v1"), headers),
                Map.class);
        UUID messageId = UUID.fromString((String) createResponse.getBody().get("id"));

        ResponseEntity<Map> editResponse = restTemplate.exchange(
                "/api/v1/conversations/" + conversationId + "/messages/" + messageId,
                HttpMethod.PATCH,
                new HttpEntity<>(Map.of("content", "v2"), headers),
                Map.class);

        assertThat(editResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(editResponse.getBody()).containsEntry("content", "v2");
    }

    @Test
    void createMessageWithoutPermissionReturns403() {
        UUID conversationId = createConversation("alice@workchat.test");
        HttpHeaders viewerHeaders = authHeaders(loginToken("viewer@workchat.test", "secret123"));

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/v1/conversations/" + conversationId + "/messages",
                HttpMethod.POST,
                new HttpEntity<>(Map.of("content", "nope"), viewerHeaders),
                Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void crossAccountMessageListReturns404() {
        UUID conversationId = createConversation("alice@workchat.test");
        HttpHeaders bobHeaders = authHeaders(loginToken("bob@workchat.test", "secret123"));

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/v1/conversations/" + conversationId + "/messages",
                HttpMethod.GET,
                new HttpEntity<>(bobHeaders),
                Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private UUID createConversation(String email) {
        HttpHeaders headers = authHeaders(loginToken(email, "secret123"));
        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/v1/conversations", HttpMethod.POST, new HttpEntity<>(headers), Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return UUID.fromString((String) response.getBody().get("id"));
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
