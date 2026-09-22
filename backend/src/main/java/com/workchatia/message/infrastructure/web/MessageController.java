package com.workchatia.message.infrastructure.web;

import com.workchatia.identity.domain.AuthenticatedUser;
import com.workchatia.identity.infrastructure.web.SecuritySupport;
import com.workchatia.message.application.MessageApplicationService;
import com.workchatia.message.domain.Message;
import com.workchatia.message.domain.MessageStatus;
import com.workchatia.message.domain.MessageType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/conversations/{conversationId}/messages")
public class MessageController {

    private final MessageApplicationService messageApplicationService;

    public MessageController(MessageApplicationService messageApplicationService) {
        this.messageApplicationService = messageApplicationService;
    }

    @PostMapping
    public ResponseEntity<MessageResponse> create(
            @PathVariable UUID conversationId, @Valid @RequestBody CreateMessageRequest request) {
        AuthenticatedUser user = SecuritySupport.currentUser();
        MessageType type = request.type() != null ? request.type() : MessageType.TEXT;
        Message message = messageApplicationService.createMessage(user, conversationId, request.content(), type);
        return ResponseEntity.status(HttpStatus.CREATED).body(MessageResponse.from(message));
    }

    @GetMapping
    public List<MessageResponse> list(@PathVariable UUID conversationId) {
        AuthenticatedUser user = SecuritySupport.currentUser();
        return messageApplicationService.listMessages(user, conversationId).stream()
                .map(MessageResponse::from)
                .toList();
    }

    @GetMapping("/{messageId}")
    public MessageResponse get(@PathVariable UUID conversationId, @PathVariable UUID messageId) {
        AuthenticatedUser user = SecuritySupport.currentUser();
        return MessageResponse.from(messageApplicationService.getMessage(user, conversationId, messageId));
    }

    @PatchMapping("/{messageId}")
    public MessageResponse edit(
            @PathVariable UUID conversationId,
            @PathVariable UUID messageId,
            @Valid @RequestBody EditMessageRequest request) {
        AuthenticatedUser user = SecuritySupport.currentUser();
        return MessageResponse.from(
                messageApplicationService.editMessage(user, conversationId, messageId, request.content()));
    }

    public record CreateMessageRequest(@NotBlank String content, MessageType type) {}

    public record EditMessageRequest(@NotBlank String content) {}

    public record MessageResponse(
            UUID id,
            UUID conversationId,
            UUID authorId,
            String content,
            MessageType type,
            MessageStatus status,
            Instant createdAt,
            Instant updatedAt) {

        static MessageResponse from(Message message) {
            return new MessageResponse(
                    message.id(),
                    message.conversationId(),
                    message.authorId(),
                    message.content(),
                    message.type(),
                    message.status(),
                    message.createdAt(),
                    message.updatedAt());
        }
    }
}
