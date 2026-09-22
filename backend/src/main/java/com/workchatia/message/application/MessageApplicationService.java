package com.workchatia.message.application;

import com.workchatia.conversation.domain.Conversation;
import com.workchatia.conversation.domain.ConversationRepository;
import com.workchatia.identity.application.AuthorizationService;
import com.workchatia.identity.application.ForbiddenException;
import com.workchatia.identity.domain.AuthenticatedUser;
import com.workchatia.identity.domain.PermissionCode;
import com.workchatia.message.domain.Message;
import com.workchatia.message.domain.MessageRepository;
import com.workchatia.message.domain.MessageType;
import com.workchatia.shared.application.DomainConflictException;
import com.workchatia.shared.application.ResourceNotFoundException;
import com.workchatia.shared.domain.event.MessageCreatedEvent;
import com.workchatia.shared.domain.event.OutboxRepository;
import java.time.Clock;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageApplicationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final OutboxRepository outboxRepository;
    private final AuthorizationService authorizationService;
    private final Clock clock;

    public MessageApplicationService(
            ConversationRepository conversationRepository,
            MessageRepository messageRepository,
            OutboxRepository outboxRepository,
            AuthorizationService authorizationService,
            Clock clock) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.outboxRepository = outboxRepository;
        this.authorizationService = authorizationService;
        this.clock = clock;
    }

    @Transactional
    public Message createMessage(AuthenticatedUser user, UUID conversationId, String content, MessageType type) {
        requireUser(user);
        authorizationService.requirePermission(user, PermissionCode.SEND_MESSAGE);
        Conversation conversation = loadConversation(user, conversationId);
        if (!conversation.isOpen()) {
            throw new DomainConflictException("conversation_closed");
        }
        Message message =
                Message.create(UUID.randomUUID(), conversationId, user.userId(), content, type, clock.instant());
        Message saved = messageRepository.save(message);
        outboxRepository.append(
                MessageCreatedEvent.create(
                        UUID.randomUUID(),
                        clock.instant(),
                        conversation.accountId(),
                        conversationId,
                        saved.id(),
                        saved.authorId(),
                        saved.type()));
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Message> listMessages(AuthenticatedUser user, UUID conversationId) {
        requireUser(user);
        loadConversation(user, conversationId);
        return messageRepository.findByConversationId(conversationId);
    }

    @Transactional(readOnly = true)
    public Message getMessage(AuthenticatedUser user, UUID conversationId, UUID messageId) {
        requireUser(user);
        loadConversation(user, conversationId);
        Message message = messageRepository
                .findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("message_not_found"));
        if (!message.conversationId().equals(conversationId)) {
            throw new ResourceNotFoundException("message_not_found");
        }
        return message;
    }

    @Transactional
    public Message editMessage(AuthenticatedUser user, UUID conversationId, UUID messageId, String content) {
        requireUser(user);
        authorizationService.requirePermission(user, PermissionCode.SEND_MESSAGE);
        loadConversation(user, conversationId);
        Message message = messageRepository
                .findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("message_not_found"));
        if (!message.conversationId().equals(conversationId)) {
            throw new ResourceNotFoundException("message_not_found");
        }
        if (!message.authorId().equals(user.userId())) {
            throw new ForbiddenException("message_edit_forbidden");
        }
        message.edit(content, clock.instant());
        return messageRepository.save(message);
    }

    private Conversation loadConversation(AuthenticatedUser user, UUID conversationId) {
        return conversationRepository
                .findByIdAndAccount(conversationId, user.accountId())
                .orElseThrow(() -> new ResourceNotFoundException("conversation_not_found"));
    }

    private static void requireUser(AuthenticatedUser user) {
        if (user == null) {
            throw new ResourceNotFoundException("conversation_not_found");
        }
    }
}
