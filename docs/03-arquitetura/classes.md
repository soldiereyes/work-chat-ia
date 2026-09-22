# Diagrama de classes (MVP)

```mermaid
classDiagram

    class Conversation {
        +UUID id
        +UUID accountId
        +UUID professionalId
        +ConversationStatus status
        +create()
        +close()
    }

    class Message {
        +UUID id
        +UUID conversationId
        +UUID authorId
        +String content
        +MessageStatus status
        +MessageType type
        +create()
        +approve()
        +block()
    }

    class MessageReview {
        +UUID id
        +UUID messageId
        +ReviewStatus status
        +String summary
        +List~ReviewSuggestion~ suggestions
        +create()
    }

    class ReviewSuggestion {
        +UUID id
        +SuggestionType type
        +String originalText
        +String suggestedText
        +String explanation
    }

    class CommunicationPolicy {
        +evaluate(String content)
    }

    class MessageReviewService {
        +review(Message message)
    }

    class AIReviewService {
        +review(ReviewContext context)
    }

    class LLMProvider {
        <<interface>>
        +review(ReviewContext context)
    }

    class MessageDeliveryService {
        +send(Message message)
    }

    class ChannelGateway {
        <<interface>>
        +deliver(Message message)
    }

    Conversation "1" --> "*" Message
    Message "1" --> "0..1" MessageReview
    MessageReview "1" --> "*" ReviewSuggestion

    MessageReviewService --> CommunicationPolicy
    MessageReviewService --> AIReviewService

    AIReviewService --> LLMProvider

    MessageDeliveryService --> ChannelGateway
```

## Identity & Access (MVP-01)

| Classe | Responsabilidade |
|--------|------------------|
| `AuthenticationService` | Login e emissão de JWT |
| `AuthorizationService` | Verificação de `PermissionCode` |
| `AccountScopedAccess` | Isolamento por `accountId` |
| `IdentityProvider` / `DbIdentityProvider` | Autenticação contra PostgreSQL |
| `JwtService` | Geração e parsing de token |

## Serviços de aplicação principais

| Classe | Responsabilidade |
|--------|------------------|
| `MessageApplicationService` | Orquestra criação, edição, aprovação de mensagens |
| `MessageReviewService` | Orquestra políticas + IA, produz `ReviewResult` |
| `AIReviewService` | Adapta contexto para `LLMProvider` |
| `MessageDeliveryService` | Envia mensagem aprovada via `ChannelGateway` |
| `ConversationApplicationService` | CRUD e ciclo de vida de conversas |
