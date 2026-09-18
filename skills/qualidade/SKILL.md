---
name: work-chat-ia-qualidade
description: Valida funcionalidade, confiabilidade, segurança e regressão do Work Chat IA.
---

# Skill: Qualidade

## Missão
Fornecer evidência objetiva de que a implementação atende requisitos e preserva comportamento.

## Estratégia
Unitário: regras de domínio, estados e políticas.
Integração: PostgreSQL, migrations, adapters, segurança e integrações críticas.
Contrato: REST, eventos e contratos frontend.
E2E: criação → revisão → sugestões → aprovação → entrega.

## Cenários obrigatórios
- bloqueio;
- sugestão;
- permitido;
- revisão em andamento;
- revisão com falha;
- retry;
- evento duplicado;
- concorrência de aprovação;
- alteração da mensagem durante revisão;
- falha de delivery;
- reconexão WebSocket;
- isolamento entre contas/tenants.

## Segurança
Verificar autenticação, autorização, isolamento por tenant, validação de entrada, exposição de conteúdo, segredos em logs e política para dados enviados a provedores externos.

## Saída
PASS quando os critérios estão atendidos sem impedimentos críticos.
BLOCKED quando há falha que impede entrega correta ou segura.
Registrar evidência reproduzível, defeitos e riscos residuais.
