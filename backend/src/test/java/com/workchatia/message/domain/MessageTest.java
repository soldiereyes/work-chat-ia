package com.workchatia.message.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MessageTest {

    private static final Instant NOW = Instant.parse("2026-09-22T12:00:00Z");
    private static final UUID CONVERSATION_ID = UUID.randomUUID();
    private static final UUID AUTHOR_ID = UUID.randomUUID();

    @Test
    void createStartsDraft() {
        Message message = Message.create(UUID.randomUUID(), CONVERSATION_ID, AUTHOR_ID, "hello", MessageType.TEXT, NOW);

        assertThat(message.status()).isEqualTo(MessageStatus.DRAFT);
        assertThat(message.content()).isEqualTo("hello");
    }

    @Test
    void editInDraftUpdatesContent() {
        Message message = Message.create(UUID.randomUUID(), CONVERSATION_ID, AUTHOR_ID, "hello", MessageType.TEXT, NOW);

        message.edit("updated", NOW.plusSeconds(1));

        assertThat(message.content()).isEqualTo("updated");
    }

    @Test
    void submitForReviewFromDraft() {
        Message message = Message.create(UUID.randomUUID(), CONVERSATION_ID, AUTHOR_ID, "hello", MessageType.TEXT, NOW);

        message.submitForReview(NOW.plusSeconds(1));

        assertThat(message.status()).isEqualTo(MessageStatus.PENDING_REVIEW);
    }

    @Test
    void editAfterSubmitFails() {
        Message message = Message.create(UUID.randomUUID(), CONVERSATION_ID, AUTHOR_ID, "hello", MessageType.TEXT, NOW);
        message.submitForReview(NOW.plusSeconds(1));

        assertThatThrownBy(() -> message.edit("x", NOW.plusSeconds(2))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void blankContentRejected() {
        assertThatThrownBy(() -> Message.create(UUID.randomUUID(), CONVERSATION_ID, AUTHOR_ID, "  ", MessageType.TEXT, NOW))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void submitFromWrongStateFails() {
        Message message = Message.create(UUID.randomUUID(), CONVERSATION_ID, AUTHOR_ID, "hello", MessageType.TEXT, NOW);
        message.submitForReview(NOW.plusSeconds(1));

        assertThatThrownBy(() -> message.submitForReview(NOW.plusSeconds(2)))
                .isInstanceOf(IllegalStateException.class);
    }
}
