# Glossário — Linguagem ubíqua

Termos do domínio em **inglês** (código) com definição em português.

| Termo | Definição |
|-------|-----------|
| `Professional` | Profissional que atende e envia mensagens |
| `Client` | Cliente final que recebe a mensagem |
| `Account` | Fronteira de multi-tenancy (empresa/organização) |
| `User` | Identidade autenticada no sistema |
| `Conversation` | Contexto de atendimento entre profissional e cliente |
| `Message` | Unidade de comunicação enviada ou em preparação |
| `MessageReview` | Resultado da avaliação de uma mensagem |
| `ReviewSuggestion` | Sugestão específica de melhoria em trecho da mensagem |
| `ReviewResult` | Resultado agregado da revisão (`ALLOWED`, `SUGGESTION`, `BLOCKED`) |
| `CommunicationPolicy` | Regras determinísticas de comunicação |
| `PolicyViolation` | Violação identificada por uma política |
| `MessageReviewService` | Serviço de aplicação que orquestra a revisão |
| `AIReviewService` | Capacidade de revisão semântica via LLM |
| `LLMProvider` | Interface para provedor de modelo de linguagem |
| `MessageDeliveryService` | Serviço que envia mensagem aprovada ao canal |
| `ChannelGateway` | Interface de integração com canal externo |
| `RealtimeGateway` | Canal WebSocket para notificações em tempo real |
| `AudioMessagePolicy` | Política específica para envio de mensagens de áudio |

## Convenções de nomenclatura

- Use **Review**, não `AIValidator` — a IA é implementação, não o conceito de negócio.
- Use **Domain Events** com nomes do domínio (`MessageCreated`), não nomes técnicos (`SendMessageToAI`).
- Separe **Authorization** (permissão do usuário) de **Policy** (regra de negócio do tipo de mensagem).
