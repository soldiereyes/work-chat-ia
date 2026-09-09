# Fluxo 6 — Mensagem de áudio

Regra do MVP: **somente usuários autorizados podem enviar áudio**.

Modelar como **política de autorização no backend**, não como `if` no frontend.

```mermaid
sequenceDiagram
    autonumber

    actor Professional as Profissional
    participant ChatUI as Chat Interface
    participant MessageController
    participant MessageApplicationService
    participant AudioMessagePolicy
    participant AuthorizationService
    participant MessageRepository
    participant MessageDeliveryService

    Professional->>ChatUI: Solicita envio de áudio

    ChatUI->>MessageController: POST /messages/audio

    MessageController->>MessageApplicationService: sendAudio(command)

    MessageApplicationService->>AuthorizationService: checkPermission(professional)

    AuthorizationService-->>MessageApplicationService: AuthorizationResult

    MessageApplicationService->>AudioMessagePolicy: evaluate(professional)

    alt Profissional autorizado
        AudioMessagePolicy-->>MessageApplicationService: ALLOWED

        MessageApplicationService->>MessageRepository: save(audioMessage)

        MessageApplicationService->>MessageDeliveryService: send(audioMessage)

        MessageDeliveryService-->>MessageApplicationService: Delivered

        MessageApplicationService-->>MessageController: Success

        MessageController-->>ChatUI: Áudio enviado

    else Profissional não autorizado
        AudioMessagePolicy-->>MessageApplicationService: DENIED

        MessageApplicationService-->>MessageController: Forbidden

        MessageController-->>ChatUI: 403 Forbidden

        ChatUI-->>Professional: Envio de áudio não autorizado
    end
```

## Distinção importante

| Componente | Pergunta |
|------------|----------|
| `AuthorizationService` | Esse profissional possui a permissão? |
| `AudioMessagePolicy` | Esse tipo de mensagem pode ser enviado por ele? |

Não misture os dois conceitos.
