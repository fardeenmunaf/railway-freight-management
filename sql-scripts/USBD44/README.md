# USBD44

Função: `add_segment_with_siding` — adiciona um segmento à `LINE` e, opcionalmente, cria um `PARKING_TRACK` (siding). A função devolve o id do segmento.

Validações principais (resumido):

- Campos obrigatórios do segmento (linha, comprimento, tipo de electrificação, gauge, peso máximo, velocidade, número da via).
- A `LINE` e o `GAUGE` têm de existir.
- Se `p_has_siding = 1`, os dados do siding (facility_id, nome, capacidade) têm de estar presentes e válidos; o nome do siding tem de ser único na linha.
- Alguns erros possíveis: -20000..-20006 (cada código indica um tipo de validação falhada).

O que cada teste em `add_segment_with_siding_tests.sql` faz (resumo):

- Setup: prepara dados necessários (GAUGE, COMPANY, LINE, FACILITY_TYPE, FACILITY) e mostra mensagens.
- Cleanup inicial: apaga dados de teste antigos da `LINE 9999` para não haver conflito.

- TESTE 1 — Inserir segmento sem siding:
  Insere um segmento simples (p_has_siding = 0) e espera sucesso; confirma ID criado.

- TESTE 3 — Erro: siding obrigatório mas faltam dados:
  Chama a função com p_has_siding = 1 mas deixa os dados do siding NULL; espera erro.

- TESTE 4 — Erro: line_id inexistente:
  Tenta inserir segmento numa linha que não existe; espera erro.

- TESTE 5 — Erro: nome duplicado no siding:
  Tenta criar um siding com nome que já existe na mesma linha; espera erro de duplicado.

- Final Cleanup: apaga as entradas de teste (PARKING_TRACK e LINE_SEGMENT da linha 9999) e faz COMMIT.
- 
O script usa `DBMS_OUTPUT` para mostrar progresso e PASS/FAIL por teste.
