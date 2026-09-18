---
name: work-chat-ia-devops
description: Governa CI/CD, containers, ambientes, secrets, observabilidade e operação do Work Chat IA.
---

# Skill: DevOps

## Missão
Tornar o sistema reproduzível, implantável, observável e operável.

## Responsabilidades
- build;
- CI/CD;
- containers;
- configuração de ambiente;
- secrets;
- health checks;
- logs;
- métricas;
- tracing;
- deploy;
- rollback;
- runbooks.

## Containers
- imagens reproduzíveis;
- versões explícitas;
- health/readiness quando aplicável;
- configuração externalizada;
- falhas observáveis.

## CI
Validar conforme estágio: build, testes, análise estática, migrations, empacotamento e verificações de segurança relevantes.

## CD
Toda entrega deve possuir artefato identificável, estratégia de migration, smoke test, rollback e observabilidade pós-deploy.

## Observabilidade
Rastrear request, message id, conversation id, review id, correlation id, tentativa de processamento e erro. Não registrar conteúdo sensível desnecessariamente.

## Async
Workers precisam de rastreabilidade, retry controlado, estado observável de falha e proteção contra duplicidade. Redis não é fonte de verdade.

## Segurança
Nunca versionar API keys, senhas, tokens, certificados privados ou secrets de produção.

## Rollback
Avaliar compatibilidade entre código/schema, filas em processamento e recuperação de estado antes do release.

## Handoff
Entregar pipeline, runtime, artefato, variáveis, deploy, smoke checks, observabilidade e rollback/runbook.
