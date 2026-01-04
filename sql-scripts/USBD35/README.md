# USBD35 — Documentação técnica: usbd35_register_facility e testes

Este documento descreve a função PL/SQL `usbd35_register_facility` e o conjunto de testes em `register_new_facility_tests.sql` (ambos na pasta `USBD35`).

## Objectivo

Permitir a criação de um registo na tabela `FACILITY` garantindo validações e mensagens de erro explícitas (códigos ORA-2000x) usadas pelos testes. Os testes verificam cenários de erro (validações) e a inserção bem sucedida e validação do registo inserido.

## Ficheiros relevantes

- `register_new_facility.sql` — implementa a função `usbd35_register_facility`.
- `register_new_facility_tests.sql` — script PL/SQL que prepara dados, executa vários testes e faz limpeza.

## Função (inputs / outputs / erros)

- Inputs:
  - `p_name` (FACILITY.name%TYPE) — nome da instalação (obrigatório)
  - `p_facility_type_id` (FACILITY.facility_type_id%TYPE) — FK para `FACILITY_TYPE` (obrigatório)
  - `p_lat` (FACILITY.lat%TYPE) — latitude (obrigatório)
  - `p_lon` (FACILITY.lon%TYPE) — longitude (obrigatório)
  - `p_is_intermodal` (FACILITY.is_intermodal%TYPE) — sinalizador 0/1 (opcional, default 0)
- Output: retorna o `facility_id` (FACILITY.facility_id%TYPE) do registo inserido.
- Erros controlados (RAISE_APPLICATION_ERROR):
  - ORA-20001 — falta um campo obrigatório (NULL em p_name, p_facility_type_id, p_lat ou p_lon)
  - ORA-20002 — `p_facility_type_id` não existe na tabela `FACILITY_TYPE`
  - ORA-20003 — nome duplicado (comparação case-insensitive)
  - ORA-20004 — `p_is_intermodal` não é 0 nem 1

Observação: ao usar códigos de erro dedicados evita-se ambiguidade nas camadas superiores da aplicação e facilita a escrita de testes que validam erros específicos.

## Lógica e motivos técnicos

1. Verificação de campos obrigatórios (NULL check)

   - A função valida se `p_name`, `p_facility_type_id`, `p_lat` e `p_lon` são NOT NULL; se algum for NULL, a função dispara ORA-20001.
   - Racional: evita inserir registos incompletos e permite mensagens de erro coerentes para a aplicação.

2. Validação de `p_is_intermodal`

   - Usa `NVL(p_is_intermodal, 0)` e verifica se o resultado está em (0,1). Se não, lança ORA-20004.
   - Racional: aceita omissão do parâmetro (default explícito) e protege contra valores inválidos (ex.: 5, NULL string, etc.). Garante comportamento previsível quando NULL é passado.

3. Verificar existência de `FACILITY_TYPE`

   - Executa um SELECT para procurar o `facility_type_id` passado; em caso de `NO_DATA_FOUND` lança ORA-20002.
   - Racional: garante integridade referencial lógica (mesmo que exista FK na BD, essa validação gera mensagens de erro mais amigáveis e testáveis antes de uma violação de FK).

4. Verificar duplicação de nome (case-insensitive)

   - Executa SELECT ... WHERE UPPER(name) = UPPER(p_name). Se um resultado existir, RAISE_APPLICATION_ERROR(-20003).
   - Racional: a comparação em UPPER() assegura que nomes com diferenças apenas de casing são considerados iguais. Isto evita duplicação lógica e discrepâncias numa camada que possa não normalizar o casing.

5. Inserção com `RETURNING` para obter `facility_id`

   - O INSERT usa `RETURNING facility_id INTO v_id` para retornar imediatamente o identificador gerado.
   - Racional: evita fazer SELECT adicional depois do INSERT; é eficiente e transacional.

6. Tratamento de exceções e mensagens
   - Usa `RAISE_APPLICATION_ERROR` para devolver erros com códigos 20001..20004. Estes são reutilizados pelos testes para assert.
   - Racional: mensagens e códigos previsíveis tornam os testes determinísticos e a integração com camadas de aplicação mais simples.

## Pontos de implementação importantes / trade-offs

- Uso de checks manuais vs constraints da BD:

  - Algumas validações (p.ex. `facility_type_id` existente, unicidade de nome) podem ser delegadas a constraints (FK, unique index). Aqui optou-se por validar manualmente para controlar mensagens de erro (e códigos) e para permitir os testes que esperam erros específicos.
  - Trade-off: duplicação de lógica que a BD poderia impor; vantagem: mensagens de erro testáveis e usáveis pela aplicação.

- Case-insensitive duplicate check com `UPPER()`:

  - Ae a BD usar collation case-insensitive, a verificação permanece correta, mas UPPER() pode ser desnecessário. Alternativa: criar índice funcional sobre UPPER(name).

- `p_is_intermodal` validado como 0/1:

  - Assumimos que a coluna é NUMÉRICA (tinyint/number). Se a coluna for booleana de outro tipo, ajustar a validação.

- Atomicidade/transacções:
  - A função não faz COMMIT nem ROLLBACK; a gestão da transacção é delegada ao chamador (bom para testes e consistência transaccional em aplicações).

## Descrição dos testes em `register_new_facility_tests.sql`

O script organiza-se em blocos: preparação de dados, limpeza/remoção de testes antigos, vários testes unitários PL/SQL e limpeza final.

Preparação:

- Garante que existem registos em `FACILITY_TYPE` (insere alguns tipos caso a tabela esteja vazia). Isso torna o script idempotente para execução em bases de teste vazias.
- Remove instalações com nome que começam por `TEST_USBD35_` (caso existam) para não haver interferência entre execuções.

Cada teste usa uma chamada a `usbd35_register_facility` dentro de um bloco `DECLARE ... BEGIN ... EXCEPTION ... END;` para capturar a exceção e verificar o `SQLCODE` esperado.

# USBD35 — resumo simples

Função: `usbd35_register_facility` — valida os dados e insere um registo em `FACILITY`; devolve o `facility_id`.

O que valida (resumido):

- Campos obrigatórios: nome, tipo, latitude, longitude. Se faltar, ORA-20001.
- O `facility_type_id` tem de existir em `FACILITY_TYPE`. Se não existir, ORA-20002.
- Nome não pode ser duplicado (ignora maiúsculas/minúsculas). Se duplicado, ORA-20003.
- `is_intermodal` só aceita 0 ou 1. Se inválido, ORA-20004.

Descrição rápida dos testes em `register_new_facility_tests.sql`:

- TEST 1 — NULL name: garante que falta de nome retorna ORA-20001.
- TEST 2 — tipo inválido (9999): garante ORA-20002.
- TEST 3 — duplicado (mesmo nome com casing diferente): garante ORA-20003.
- TEST 4 — is_intermodal inválido (5): garante ORA-20004.
- TEST 5 — NULL facility_type_id: garante ORA-20001.
- TEST 6 — NULL lat: garante ORA-20001.
- TEST 7 — NULL lon: garante ORA-20001.
- TEST 8 — insere `TEST_USBD35_OK_001` e verifica que o registo ficou na tabela (espera sucesso).
