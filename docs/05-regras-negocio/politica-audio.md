# Política de áudio

## Regra de negócio

> Somente profissionais **autorizados** podem enviar mensagens de áudio.

## Componentes

| Componente | Pergunta que responde |
|------------|----------------------|
| `AuthorizationService` | O usuário tem permissão `SEND_AUDIO_MESSAGE`? |
| `AudioMessagePolicy` | Este profissional pode enviar áudio neste contexto? |

## Por que não no frontend?

Um `if` na UI pode ser contornado e não serve para auditoria. A validação deve ocorrer em `MessageApplicationService.sendAudio()`.

## Respostas HTTP

| Situação | Código |
|----------|--------|
| Autorizado e enviado | `200 OK` |
| Sem permissão | `403 Forbidden` |

## Fluxo detalhado

Ver [Fluxo 6 — Mensagem de áudio](../04-fluxos/06-audio.md).
