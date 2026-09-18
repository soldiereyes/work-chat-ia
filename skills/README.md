# Skills do Work Chat IA

As skills deste diretório definem contratos operacionais para agentes de IA e colaboradores humanos.

## Skills
- `produto/SKILL.md`
- `implementacao/SKILL.md`
- `desenvolvedores/SKILL.md`
- `qualidade/SKILL.md`
- `dba/SKILL.md`
- `devops/SKILL.md`

## Fluxo
Produto → Implementação → Desenvolvedores + DBA → Qualidade → DevOps.

As skills podem atuar em paralelo quando as dependências estiverem claras.

## Responsabilidade decisória
| Área | Responsabilidade |
|---|---|
| Produto | o que construir, regras e critérios de aceite |
| Implementação | como organizar a solução e decompor o trabalho |
| Desenvolvedores | código e testes de implementação |
| Qualidade | evidência de corretude, regressão e segurança |
| DBA | dados, PostgreSQL, migrations e performance |
| DevOps | build, entrega, operação e observabilidade |

## Invariantes
1. O profissional mantém a decisão final sobre a mensagem.
2. A IA revisa e sugere; revisão não envia mensagens.
3. Bounded contexts possuem responsabilidades explícitas.
4. `Message` e `MessageReview` mantêm estados independentes.
5. PostgreSQL é fonte de verdade.
6. Redis é infraestrutura auxiliar.
7. Eventos representam fatos de domínio.
8. Não criar dependências circulares entre módulos.
9. Alterações de regras, contratos ou arquitetura atualizam a documentação.
10. Nenhuma skill deve assumir silenciosamente a responsabilidade de outra.

## Definition of Done comum
- requisito e aceite claros;
- bounded context identificado;
- arquitetura respeitada;
- testes adequados passando;
- migrations versionadas quando houver alteração de banco;
- tratamento de erro e observabilidade adequados;
- documentação impactada atualizada;
- pendências críticas resolvidas.

## Saída mínima
Toda skill deve informar: contexto, decisão/diagnóstico, artefatos, riscos/pendências e próximo handoff.
