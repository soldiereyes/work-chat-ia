# Definition of Done — Work Chat IA

Uma entrega só é considerada concluída quando todos os gates aplicáveis foram satisfeitos.

## Produto

- [ ] requisito está claro;
- [ ] critérios de aceite são testáveis;
- [ ] regras de negócio estão documentadas;
- [ ] exceções relevantes estão definidas.

## Implementação

- [ ] bounded context correto;
- [ ] responsabilidades de classes definidas;
- [ ] dependências justificadas;
- [ ] contratos definidos;
- [ ] fluxo assíncrono/síncrono explícito;
- [ ] ADR criado quando necessário;
- [ ] documentação arquitetural atualizada.

## Desenvolvimento

- [ ] código implementado;
- [ ] domínio preserva invariantes;
- [ ] testes adequados adicionados;
- [ ] tratamento de erro;
- [ ] idempotência/concurrency avaliadas;
- [ ] logs e correlation id quando necessários;
- [ ] revisão técnica concluída.

## DBA

Aplicável quando houver dados:

- [ ] migration versionada;
- [ ] constraints avaliadas;
- [ ] índices avaliados;
- [ ] queries críticas verificadas;
- [ ] concorrência avaliada;
- [ ] compatibilidade com deploy verificada.

## Qualidade

- [ ] critérios de aceite validados;
- [ ] testes de regressão relevantes;
- [ ] segurança validada;
- [ ] cenários assíncronos avaliados;
- [ ] isolamento entre contas/tenants validado;
- [ ] nenhum defeito crítico aberto.

## DevOps

- [ ] build reproduzível;
- [ ] CI verde;
- [ ] artefato identificável;
- [ ] configuração/secrets seguros;
- [ ] migration/deploy ordenados;
- [ ] health checks;
- [ ] observabilidade;
- [ ] smoke test;
- [ ] rollback conhecido.

## Resultado

A mudança pode ser marcada como `DONE` somente quando os gates aplicáveis estiverem completos e as evidências estiverem disponíveis.
