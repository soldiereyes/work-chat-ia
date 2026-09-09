# Fluxo 1 — Envio de mensagem para revisão

Fluxo **mais importante do MVP**.

```mermaid
sequenceDiagram
    autonumber

    actor Professional as Profissional
    participant ChatUI as Chat Interface
    participant MessageController
    participant MessageApplicationService
    participant ConversationRepository
    participant MessageRepository
    participant MessageReviewService
    participant CommunicationPolicy
    participant AIReviewService
    participant LLMProvider

    Professional->>ChatUI: Escreve mensagem
    Professional->>ChatUI: Solicita envio

    ChatUI->>MessageController: POST /conversations/{id}/messages

    MessageController->>MessageApplicationService: sendMessage(command)

    MessageApplicationService->>ConversationRepository: findById(conversationId)
    ConversationRepository-->>MessageApplicationService: Conversation

    MessageApplicationService->>MessageReviewService: review(messageContent, conversation)

    MessageReviewService->>CommunicationPolicy: validate(messageContent)
    CommunicationPolicy-->>MessageReviewService: PolicyViolations

    alt Regras determinísticas identificam problemas
        MessageReviewService-->>MessageApplicationService: ReviewResult
    else Necessário analisar contexto/linguagem
        MessageReviewService->>AIReviewService: review(messageContent, conversation)
        AIReviewService->>LLMProvider: analyze(context, messageContent)
        LLMProvider-->>AIReviewService: AIReviewResponse
        AIReviewService-->>MessageReviewService: AIReviewResult
        MessageReviewService-->>MessageApplicationService: ReviewResult
    end

    MessageApplicationService->>MessageRepository: save(PendingMessage)
    MessageRepository-->>MessageApplicationService: Message

    MessageApplicationService-->>MessageController: MessageReviewResponse
    MessageController-->>ChatUI: 200 OK + ReviewResult

    ChatUI-->>Professional: Exibe sugestões de melhoria
```

## Termos introduzidos

- `Professional`, `Conversation`, `Message`, `MessageReview`
- `CommunicationPolicy`, `PolicyViolation`, `ReviewResult`
- `AIReviewService`, `LLMProvider`

> Use **Review**, não `AIValidator`. A IA é capacidade da revisão, não o conceito principal do negócio.
