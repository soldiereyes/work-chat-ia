# Entidades e agregados

## Modelo conceitual

```text
Account
   │
   ├── User / Professional
   │
   └── Conversation
            │
            ├── Message
            │      │
            │      └── MessageReview
            │               │
            │               └── ReviewSuggestion
            │
            └── (assignee, status, ...)
```

## Conversation

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | UUID | Identificador |
| `accountId` | UUID | Tenant |
| `professionalId` | UUID | Profissional responsável |
| `status` | `ConversationStatus` | Estado da conversa |

**Comportamentos:** `create()`, `close()`

## Message

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | UUID | Identificador |
| `conversationId` | UUID | Conversa pai |
| `authorId` | UUID | Autor (profissional) |
| `content` | String | Conteúdo textual |
| `status` | `MessageStatus` | Estado do ciclo de vida |
| `type` | `MessageType` | Texto, áudio, etc. |

**Comportamentos:** `create()`, `approve()`, `block()`

> **Atenção:** não concentrar validação, entrega, webhook, busca e LLM dentro do model `Message` (lição do Chatwoot). Extrair para services dedicados.

## MessageReview

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | UUID | Identificador |
| `messageId` | UUID | Mensagem avaliada |
| `status` | `ReviewStatus` | Estado da revisão |
| `summary` | String | Resumo da avaliação |
| `suggestions` | `List<ReviewSuggestion>` | Sugestões detalhadas |

## ReviewSuggestion

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | UUID | Identificador |
| `type` | `SuggestionType` | Tipo da sugestão |
| `originalText` | String | Trecho original |
| `suggestedText` | String | Texto sugerido |
| `explanation` | String | Justificativa para o profissional |

## ReviewResult (valor / DTO de aplicação)

Resultado agregado retornado ao frontend:

| Valor | Significado |
|-------|-------------|
| `ALLOWED` | Pode enviar sem alterações |
| `SUGGESTION` | Recomenda ajustes antes do envio |
| `BLOCKED` | Não pode ser enviada |
