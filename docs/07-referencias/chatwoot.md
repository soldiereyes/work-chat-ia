# Chatwoot — análise técnica (referência)

Documento de referência baseado na análise do código e arquitetura do [Chatwoot](https://github.com/chatwoot/chatwoot).

## Avaliação geral

| Área | Nota |
|------|-----|
| Arquitetura geral | 9/10 |
| Escalabilidade | 8,5/10 |
| API / Integrações | 9,5/10 |
| Processamento assíncrono | 9/10 |
| Multi-tenant | 9/10 |
| Extensibilidade | 9/10 |
| Manutenibilidade do código | 7,5/10 |

**Conclusão:** produto tecnicamente sério — monólito Rails bem evoluído, com excelentes decisões de integração e processamento assíncrono, mas com dívida arquitetural de produto grande e antigo.

## Arquitetura

```text
Vue.js App (Dashboard / Widget)
        │
 HTTP / WebSocket
        │
Rails Application (Controllers, Services, Models, Policies, Listeners)
        │
   PostgreSQL          Redis → Sidekiq → Background Jobs
        │
External Channels (WhatsApp, Email, API, Telegram, ...)
```

## Domínio central (Chatwoot)

```text
Account
   ├── Users
   ├── Inboxes → Channel
   ├── Contacts → ContactInbox
   └── Conversations → Messages, Assignee, Team, Labels, Attachments
```

Modelagem correta para omnichannel: `Contact → ContactInbox → Conversation → Message`.

## Pontos fortes

1. **Monólito modular** escalável horizontalmente
2. **Redis + Sidekiq** — jobs não bloqueiam HTTP
3. **APIs + Webhooks** — plataforma de integração, não só UI
4. **PostgreSQL** como source of truth
5. **ActionCable** para realtime
6. **Webhooks assíncronos** com `delivery_id` (retry, deduplication)
7. **Service objects** (`app/services`) para lógica complexa
8. **Evolução incremental** (open source / enterprise / custom)

## Pontos de atenção

1. **`Message` como god model** (>460 linhas) — validação, entrega, webhook, search, LLM no mesmo lugar
2. **Multi-tenancy** — risco em lookups sem escopo de `Account`
3. **Autorização centrada em `Inbox`** — complexidade crescente
4. **`Current.account`** — dependência implícita difícil de testar
5. **Frontend heterogêneo** — Vuex legado + Composition API
6. **Redis crítico** — jobs + realtime na mesma dependência
7. **Integrações externas** — consistência local vs remota é frágil

## Relevância para Work Chat IA

O Chatwoot é referência para **infraestrutura de conversação** (inbox, agentes, canais, histórico, webhooks).

Nosso produto adiciona a camada **`MessageReview`** que o Chatwoot não resolve nativamente como proposta de valor central.

Possível integração futura:

```text
Chatwoot (inbox + canais) → Webhook → Work Chat IA (revisão) → retorno → Chatwoot
```
