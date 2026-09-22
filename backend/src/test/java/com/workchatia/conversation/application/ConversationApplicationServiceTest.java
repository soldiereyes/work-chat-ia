package com.workchatia.conversation.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.workchatia.conversation.domain.Conversation;
import com.workchatia.conversation.domain.ConversationRepository;
import com.workchatia.conversation.domain.ConversationStatus;
import com.workchatia.identity.domain.AuthenticatedUser;
import com.workchatia.shared.application.ResourceNotFoundException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConversationApplicationServiceTest {

    private static final Instant NOW = Instant.parse("2026-09-22T12:00:00Z");
    private static final UUID ACCOUNT_ID = UUID.fromString("11111111-1111-1111-1111-111111111101");
    private static final UUID USER_ID = UUID.fromString("22222222-2222-2222-2222-222222222201");
    private static final UUID CONVERSATION_ID = UUID.randomUUID();

    @Mock
    private ConversationRepository conversationRepository;

    private ConversationApplicationService service;

    private AuthenticatedUser user;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
        service = new ConversationApplicationService(conversationRepository, clock);
        user = new AuthenticatedUser(USER_ID, ACCOUNT_ID, "alice@workchat.test", Set.of());
    }

    @Test
    void getWhenNotFoundThrows() {
        when(conversationRepository.findByIdAndAccount(CONVERSATION_ID, ACCOUNT_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(user, CONVERSATION_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void closePersistsClosedStatus() {
        Conversation open = Conversation.restore(
                CONVERSATION_ID, ACCOUNT_ID, USER_ID, ConversationStatus.OPEN, NOW, NOW);
        when(conversationRepository.findByIdAndAccount(CONVERSATION_ID, ACCOUNT_ID))
                .thenReturn(Optional.of(open));
        when(conversationRepository.save(any(Conversation.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Conversation result = service.close(user, CONVERSATION_ID);

        assertThat(result.status()).isEqualTo(ConversationStatus.CLOSED);
        ArgumentCaptor<Conversation> captor = ArgumentCaptor.forClass(Conversation.class);
        verify(conversationRepository).save(captor.capture());
        assertThat(captor.getValue().status()).isEqualTo(ConversationStatus.CLOSED);
    }
}
