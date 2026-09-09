# Visão geral da arquitetura

Arquitetura inspirada no Chatwoot, adaptada ao domínio de **revisão de mensagens**.

## Diagrama de componentes (MVP)

```text
                     ┌─────────────────────┐
                     │       React         │
                     │   Chat Interface    │
                     └──────────┬──────────┘
                                │
                         REST + WebSocket
                                │
                                ▼
                 ┌──────────────────────────┐
                 │      Spring Boot         │
                 │   (monólito modular)     │
                 │                          │
                 │  Conversation            │
                 │  Message                 │
                 │  Review                  │
                 │  Identity                │
                 │  Delivery                │
                 └──────┬───────────┬───────┘
                        │           │
                 PostgreSQL        Redis
                        │           │
                        │        Workers
                        │           │
                        │           ▼
                        │       AI Worker
                        │           │
                        │           ▼
                        │       LLM Provider
                        │
                        ▼
                  Source of Truth
```

## Princípios

1. **Monólito modular** — não começar com microsserviços.
2. **PostgreSQL** como source of truth.
3. **Processamento assíncrono** para revisão por LLM (não bloquear HTTP).
4. **Domain events** para desacoplar revisão de criação de mensagem.
5. **Interfaces** (`LLMProvider`, `ChannelGateway`) para integrações futuras.

## Fluxo de mensagem aprovada

```text
             USUÁRIO
                │
                ▼
           POST MESSAGE
                │
                ▼
           MessageService
                │
        ┌───────┴────────┐
        │                │
        ▼                ▼
     Persist          Event (MessageCreated)
        │                │
        │                ▼
        │          Review Worker
        │                │
        │                ▼
        │             AI Review
        │                │
        │        ┌───────┴────────┐
        │        │                │
        │        ▼                ▼
        │      APPROVE          SUGGEST
        │        │                │
        │        │                ▼
        │        │          User adjusts
        │        │                │
        └────────┴────────────────┘
                         │
                         ▼
                       SEND
```
