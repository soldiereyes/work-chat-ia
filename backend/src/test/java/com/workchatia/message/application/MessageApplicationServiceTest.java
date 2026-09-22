package com.workchatia.message.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.workchatia.conversation.domain.Conversation;
import com.workchatia.conversation.domain.ConversationRepository;
import com.workchatia.conversation.domain.ConversationStatus;
import com.workchatia.identity.application.AuthorizationService;
import com.workchatia.identity.application.ForbiddenException;
import com.workchatia.identity.domain.AuthenticatedUser;
import com.workchatia.identity.domain.PermissionCode;
import com.workchatia.message.domain.Message;
import com.workchatia.message.domain.MessageRepository;
import com.workchatia.message.domain.MessageType;
import com.workchatia.shared.application.DomainConflictException;
import com.workchatia.shared.application.ResourceNotFoundException;
import com.workchatia.shared.domain.event.OutboxRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MessageApplicationServiceTest {

    private static final Instant NOW = Instant.parse("2026-09-22T12:00:00Z");
    private static final UUID ACCOUNT_ID = UUID.fromString("11111111-1111-1111-1111-111111111101");
    private static final UUID ALICE_ID = UUID.fromString("22222222-2222-2222-2222-222222222201");
    private static final UUID CAROL_ID = UUID.fromString("22222222-2222-2222-2222-222222222204");
    private static final UUID CONVERSATION_ID = UUID.randomUUID();
    private static final UUID MESSAGE_ID = UUID.randomUUID();

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private AuthorizationService authorizationService;

    private MessageApplicationService service;

    private AuthenticatedUser alice;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
        service = new MessageApplicationService(
                conversationRepository, messageRepository, outboxRepository, authorizationService, clock);
        alice = new AuthenticatedUser(ALICE_ID, ACCOUNT_ID, "alice@workchat.test", Set.of(PermissionCode.SEND_MESSAGE));
    }

    @Test
    void createMessageOnClosedConversationThrows() {
        Conversation closed = Conversation.restore(
                CONVERSATION_ID, ACCOUNT_ID, ALICE_ID, ConversationStatus.CLOSED, NOW, NOW);
        when(conversationRepository.findByIdAndAccount(CONVERSATION_ID, ACCOUNT_ID))
                .thenReturn(Optional.of(closed));

        assertThatThrownBy(() -> service.createMessage(alice, CONVERSATION_ID, "hi", MessageType.TEXT))
                .isInstanceOf(DomainConflictException.class)
                .hasMessageContaining("conversation_closed");

        verify(messageRepository, never()).save(any());
        verify(outboxRepository, never()).append(any());
    }

    @Test
    void createMessageWhenConversationNotFoundThrows() {
        when(conversationRepository.findByIdAndAccount(CONVERSATION_ID, ACCOUNT_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createMessage(alice, CONVERSATION_ID, "hi", MessageType.TEXT))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void editMessageByNonAuthorThrowsForbidden() {
        Conversation open = Conversation.restore(
                CONVERSATION_ID, ACCOUNT_ID, ALICE_ID, ConversationStatus.OPEN, NOW, NOW);
        Message message = Message.create(MESSAGE_ID, CONVERSATION_ID, ALICE_ID, "text", MessageType.TEXT, NOW);
        when(conversationRepository.findByIdAndAccount(CONVERSATION_ID, ACCOUNT_ID))
                .thenReturn(Optional.of(open));
        when(messageRepository.findById(MESSAGE_ID)).thenReturn(Optional.of(message));

        AuthenticatedUser carol =
                new AuthenticatedUser(CAROL_ID, ACCOUNT_ID, "carol@workchat.test", Set.of(PermissionCode.SEND_MESSAGE));

        assertThatThrownBy(() -> service.editMessage(carol, CONVERSATION_ID, MESSAGE_ID, "hack"))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("message_edit_forbidden");
    }

    @Test
    void createMessagePersistsAndAppendsOutbox() {
        Conversation open = Conversation.restore(
                CONVERSATION_ID, ACCOUNT_ID, ALICE_ID, ConversationStatus.OPEN, NOW, NOW);
        when(conversationRepository.findByIdAndAccount(CONVERSATION_ID, ACCOUNT_ID))
                .thenReturn(Optional.of(open));
        when(messageRepository.save(any(Message.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Message result = service.createMessage(alice, CONVERSATION_ID, "hello", MessageType.TEXT);

        assertThat(result.content()).isEqualTo("hello");
        verify(messageRepository).save(any(Message.class));
        verify(outboxRepository).append(any());
    }
}
