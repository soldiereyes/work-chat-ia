package com.workchatia.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import com.workchatia.conversation.application.ConversationApplicationService;
import com.workchatia.identity.domain.AuthenticatedUser;
import com.workchatia.identity.domain.PermissionCode;
import com.workchatia.message.application.MessageApplicationService;
import com.workchatia.message.domain.MessageType;
import com.workchatia.message.infrastructure.persistence.MessageJpaRepository;
import com.workchatia.shared.domain.event.OutboxRepository;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class MessageOutboxAtomicityIntegrationTest {

    private static final String DEFAULT_JDBC_URL = "jdbc:postgresql://localhost:55432/workchat";
    private static final UUID ALICE_ID = UUID.fromString("22222222-2222-2222-2222-222222222201");
    private static final UUID ACCOUNT_ALPHA = UUID.fromString("11111111-1111-1111-1111-111111111101");

    @DynamicPropertySource
    static void datasourceProps(DynamicPropertyRegistry registry) {
        String fromEnv = System.getenv("TEST_JDBC_URL");
        final String jdbcUrl = (fromEnv == null || fromEnv.isBlank()) ? DEFAULT_JDBC_URL : fromEnv;
        registry.add("spring.datasource.url", () -> jdbcUrl);
        registry.add("spring.datasource.username", () -> System.getenv().getOrDefault("TEST_DB_USER", "workchat"));
        registry.add("spring.datasource.password", () -> System.getenv().getOrDefault("TEST_DB_PASSWORD", "workchat"));
    }

    @MockitoBean
    private OutboxRepository outboxRepository;

    @Autowired
    private ConversationApplicationService conversationApplicationService;

    @Autowired
    private MessageApplicationService messageApplicationService;

    @Autowired
    private MessageJpaRepository messageJpaRepository;

    @Test
    void outboxFailureRollsBackMessageInsert() {
        doThrow(new RuntimeException("outbox_unavailable")).when(outboxRepository).append(any());

        AuthenticatedUser alice = new AuthenticatedUser(
                ALICE_ID, ACCOUNT_ALPHA, "alice@workchat.test", Set.of(PermissionCode.SEND_MESSAGE));
        var conversation = conversationApplicationService.create(alice);
        long messagesBefore = messageJpaRepository.count();

        assertThatThrownBy(() -> messageApplicationService.createMessage(
                        alice, conversation.id(), "should not persist", MessageType.TEXT))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("outbox_unavailable");

        assertThat(messageJpaRepository.count()).isEqualTo(messagesBefore);
    }
}
