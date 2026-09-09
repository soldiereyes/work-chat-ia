# Visão geral

## O que é

**Work Chat IA** é uma plataforma de comunicação profissional com **revisão assistida por IA** antes do envio de mensagens ao cliente.

O produto não substitui o profissional na decisão final. Ele garante que cada mensagem passe por **regras de comunicação** (determinísticas e semânticas) e receba **sugestões de melhoria** quando necessário.

## Fluxo resumido

```text
Profissional escreve mensagem
        ↓
Sistema avalia (CommunicationPolicy + AIReviewService)
        ↓
IA sugere ajustes (quando aplicável)
        ↓
Profissional decide (aprovar, editar ou desistir)
        ↓
Mensagem é enviada ao canal externo
```

## Inspiração arquitetural

O [Chatwoot](../07-referencias/chatwoot.md) serve como **referência** para decisões de infraestrutura (monólito modular, PostgreSQL, Redis, workers, webhooks, WebSocket), mas **não** como produto a ser copiado literalmente.

Nosso diferencial é o **domínio de revisão de comunicação** (`MessageReview`), não a infraestrutura omnichannel completa.

## Núcleo do MVP

```text
                 ┌──────────────┐
                 │ Professional │
                 └──────┬───────┘
                        │
                        ▼
                 ┌──────────────┐
                 │   Message    │
                 └──────┬───────┘
                        │
                        ▼
              ┌──────────────────┐
              │  MessageReview   │
              └────────┬─────────┘
                       │
             ┌─────────┴─────────┐
             ▼                   ▼
   CommunicationPolicy    AIReviewService
             │                   │
             └─────────┬─────────┘
                       ▼
                 ReviewResult
                       │
                       ▼
                Professional
                       │
                       ▼
                    Approve
                       │
                       ▼
              MessageDelivery
                       │
                       ▼
                   Cliente
```
