# Agent Router — Work Chat IA

Este arquivo orienta uma IA sobre qual skill utilizar em cada tipo de tarefa.

## Roteamento

| Intenção detectada | Skill |
|---|---|
| requisito, história, persona, aceite, regra de negócio, prioridade | Produto |
| arquitetura, classes, pacote, fluxo, API, evento, decomposição, ADR | Implementação |
| Java, Spring, React, TypeScript, código, refactor, teste unitário, code review | Desenvolvedores |
| teste, regressão, aceite, segurança, qualidade, validação | Qualidade |
| PostgreSQL, tabela, coluna, índice, query, migration, transação, lock, backup | DBA |
| Docker, CI/CD, pipeline, deploy, ambiente, secret, log, métrica, tracing, rollback | DevOps |

## Roteamento composto

Quando a tarefa atravessar áreas, não escolher uma única skill arbitrariamente.

### Nova funcionalidade

```text
Produto
→ Implementação
→ Desenvolvedores + DBA
→ Qualidade
→ DevOps
```

### Correção de bug

```text
Qualidade/Diagnóstico
→ Desenvolvedores
→ DBA (se dados)
→ Qualidade
→ DevOps (se release)
```

### Alteração de banco

```text
Implementação
→ DBA
→ Desenvolvedores
→ Qualidade
→ DevOps
```

### Alteração arquitetural

```text
Implementação
→ Produto (se comportamento mudar)
→ Desenvolvedores + DBA
→ Qualidade
→ DevOps
```

### Incidente de produção

```text
DevOps
→ Desenvolvedores / DBA
→ Qualidade
→ Implementação
→ Produto (se impacto funcional)
```

## Regra de precedência

1. Requisito de negócio indefinido → Produto.
2. Requisito definido, solução indefinida → Implementação.
3. Solução definida, código pendente → Desenvolvedores/DBA.
4. Código pronto → Qualidade.
5. Qualidade aprovada → DevOps.
6. Problema em produção → DevOps coordena diagnóstico.

## Regra contra improvisação

Se uma tarefa revelar uma decisão pertencente a uma etapa anterior, parar a implementação daquela decisão e registrar o handoff de retorno.

Não usar código para decidir silenciosamente uma regra de produto ou arquitetura.
