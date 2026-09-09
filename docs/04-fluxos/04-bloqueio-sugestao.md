# Fluxo 4 — Bloqueio e sugestão

Separe **sugestão** de **bloqueio** — são conceitos diferentes.

```text
Suggestion → "Seria melhor escrever..."
Block      → "Essa mensagem não pode ser enviada."
```

```mermaid
sequenceDiagram
    autonumber

    actor Professional as Profissional
    participant ChatUI as Chat Interface
    participant MessageController
    participant MessageApplicationService
    participant MessageReviewService
    participant CommunicationPolicy
    participant MessageRepository

    Professional->>ChatUI: Solicita envio

    ChatUI->>MessageController: POST /messages

    MessageController->>MessageApplicationService: sendMessage(command)

    MessageApplicationService->>MessageReviewService: review(message)

    MessageReviewService->>CommunicationPolicy: evaluate(message)

    CommunicationPolicy-->>MessageReviewService: PolicyEvaluation

    alt Mensagem permitida
        MessageReviewService-->>MessageApplicationService: ReviewResult(ALLOWED)

        MessageApplicationService->>MessageRepository: save(message)

        MessageApplicationService-->>MessageController: ReviewResult(ALLOWED)

        MessageController-->>ChatUI: Mensagem aprovada
    else Mensagem necessita ajuste
        MessageReviewService-->>MessageApplicationService: ReviewResult(SUGGESTION)

        MessageApplicationService-->>MessageController: ReviewResult(SUGGESTION)

        MessageController-->>ChatUI: Sugestões

        ChatUI-->>Professional: Solicita ajuste
    else Mensagem bloqueada
        MessageReviewService-->>MessageApplicationService: ReviewResult(BLOCKED)

        MessageApplicationService-->>MessageController: ReviewResult(BLOCKED)

        MessageController-->>ChatUI: Mensagem bloqueada

        ChatUI-->>Professional: Exibe motivo do bloqueio
    end
```
