package com.workchatia.conversation.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ConversationTest {

    private static final UUID ACCOUNT_ID = UUID.randomUUID();
    private static final UUID PROFESSIONAL_ID = UUID.randomUUID();
    private static final Instant NOW = Instant.parse("2026-09-22T12:00:00Z");

    @Test
    void createStartsOpen() {
        UUID id = UUID.randomUUID();
        Conversation conversation = Conversation.create(id, ACCOUNT_ID, PROFESSIONAL_ID, NOW);

        assertThat(conversation.id()).isEqualTo(id);
        assertThat(conversation.accountId()).isEqualTo(ACCOUNT_ID);
        assertThat(conversation.professionalId()).isEqualTo(PROFESSIONAL_ID);
        assertThat(conversation.status()).isEqualTo(ConversationStatus.OPEN);
        assertThat(conversation.isOpen()).isTrue();
    }

    @Test
    void closeTransitionsToClosed() {
        Conversation conversation = Conversation.create(UUID.randomUUID(), ACCOUNT_ID, PROFESSIONAL_ID, NOW);

        conversation.close(NOW.plusSeconds(1));

        assertThat(conversation.status()).isEqualTo(ConversationStatus.CLOSED);
        assertThat(conversation.isOpen()).isFalse();
    }

    @Test
    void closeTwiceThrows() {
        Conversation conversation = Conversation.create(UUID.randomUUID(), ACCOUNT_ID, PROFESSIONAL_ID, NOW);
        conversation.close(NOW);

        assertThatThrownBy(() -> conversation.close(NOW.plusSeconds(1)))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void createRequiresAccountAndProfessional() {
        assertThatThrownBy(() -> Conversation.create(UUID.randomUUID(), null, PROFESSIONAL_ID, NOW))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
