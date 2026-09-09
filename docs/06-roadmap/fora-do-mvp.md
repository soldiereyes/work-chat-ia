# Fora do MVP (evolução)

Implementar **somente depois** de validar o núcleo de revisão.

## Integrações e canais

```text
ChannelGateway
Webhook outbound
WhatsApp
Teams
Instagram
Email
Integração Chatwoot (como camada de inbox)
```

## Infraestrutura

```text
RabbitMQ / Kafka (substituir ou complementar Redis)
OpenSearch (busca avançada de mensagens)
Object storage (anexos e áudio)
Keycloak (Identity Provider externo)
```

## Produto

```text
Multi-tenant avançado (isolamento rigoroso por Account)
Dashboards e analytics de revisão
Configuração de políticas por Account (UI admin)
SLA e filas de atendimento
Bots e automações
Campanhas
```

## Lições do Chatwoot a considerar na evolução

- Webhooks assíncronos com `delivery_id` e retry
- Idempotência em integrações externas
- Escala horizontal (web + workers)
- Busca: PostgreSQL básico → OpenSearch avançado

## O que evitar copiar do Chatwoot

- God model em `Message` (validação + entrega + webhook + LLM no mesmo lugar)
- Autorização centrada apenas em `Inbox`
- Dependência implícita de contexto global (`Current.account`) em serviços críticos
