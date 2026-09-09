# Estados

**Não misturar** estado da mensagem e estado da revisão em um único enum.

## MessageStatus

Ciclo de vida da `Message`:

```text
DRAFT
PENDING_REVIEW
APPROVED
SENDING
SENT
DELIVERED
FAILED
BLOCKED
```

| Estado | Descrição |
|--------|-----------|
| `DRAFT` | Rascunho, ainda não submetida |
| `PENDING_REVIEW` | Aguardando ou em processo de revisão |
| `APPROVED` | Aprovada pelo profissional, pronta para envio |
| `SENDING` | Envio em andamento |
| `SENT` | Enviada ao gateway |
| `DELIVERED` | Confirmada pelo canal |
| `FAILED` | Falha no envio |
| `BLOCKED` | Bloqueada por política |

## ReviewStatus

Ciclo de vida do `MessageReview`:

```text
PENDING
IN_PROGRESS
COMPLETED
FAILED
```

| Estado | Descrição |
|--------|-----------|
| `PENDING` | Revisão enfileirada |
| `IN_PROGRESS` | LLM ou políticas em execução |
| `COMPLETED` | Revisão finalizada com resultado |
| `FAILED` | Erro na revisão (retry possível) |

## Por que separar?

```text
Message.status = SENT
MessageReview.status = COMPLETED
```

São estados de **agregados diferentes**. Uma mensagem pode estar `SENT` enquanto a revisão já está `COMPLETED`, ou `PENDING_REVIEW` com revisão `IN_PROGRESS` no fluxo assíncrono.
