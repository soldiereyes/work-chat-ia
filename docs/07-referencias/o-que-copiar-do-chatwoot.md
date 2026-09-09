# O que copiar (e não copiar) do Chatwoot

## ✅ Copiar

### 1. Monólito modular

```text
Spring Boot
   ├── Conversations
   ├── Messages
   ├── Review
   ├── Identity
   └── Delivery
```

### 2. PostgreSQL como source of truth

### 3. Redis + workers

```text
API → DB transaction → Queue → Worker
```

### 4. Eventos internos

```text
MessageCreated, ReviewCompleted, ConversationUpdated, ...
```

### 5. Webhooks assíncronos (evolução)

```text
Domain Event → Listener → Webhook Job → External system
```

### 6. WebSocket para realtime

```text
Domain Event → RealtimeGateway → Frontend
```

---

## ❌ Não copiar

### 1. God model `Message`

**Em vez de:**

```text
Message
 ├── validation
 ├── events
 ├── delivery
 ├── webhook
 ├── search
 └── LLM
```

**Preferir:**

```text
Message
   ├── MessageDomainService
   ├── MessageDeliveryService
   ├── MessageEventPublisher
   └── MessageLLMAdapter (no contexto Review)
```

### 2. Autorização centrada em Inbox

**Preferir:**

```text
AccountPolicy
ConversationPolicy
MessagePolicy
InboxPolicy (se houver inbox no futuro)
AccessControlService
```

### 3. Contexto implícito global

Evitar `Current.account` em serviços críticos — preferir dependências explícitas nos construtores.

---

## Arquitetura alvo Work Chat IA

```text
React (Chat Interface)
        │
 REST + WebSocket
        │
Spring Boot
   Conversation | Message | Review | Identity | Delivery
        │
 PostgreSQL + Redis + AI Worker + LLM Provider
```

A **IA revisora** é capacidade do domínio de mensagens — **não** o sistema de chat inteiro.
