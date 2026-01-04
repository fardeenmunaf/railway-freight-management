# Função PL/SQL: `fn_register_train_movement`

## Descrição

Função para registar movimentos de comboios na tabela `TRAIN_STOP`, distinguindo entre movimentos com paragem (stop) e movimentos de passagem.
Garante regras de negócio sobre tempos de chegada/partida e delega a validação de referências de serviço, instalação e via de parque para as constraints do schema.

***

## Parâmetros

| Parâmetro | Tipo | Descrição |
| :-- | :-- | :-- |
| `p_service_id` | `TRAIN_STOP.service_id%TYPE` | ID do serviço de comboio (`TRAIN_SERVICE`) |
| `p_facility_id` | `TRAIN_STOP.facility_id%TYPE` | ID da instalação / terminal (`FACILITY`) |
| `p_parking_track` | `TRAIN_STOP.parking_track%TYPE` | ID da via de parque (`PARKING_TRACK`) |
| `p_position` | `TRAIN_STOP.position%TYPE` | Sequência do movimento dentro do serviço |
| `p_sched_arr` | `TRAIN_STOP.sched_arr%TYPE` | Hora de chegada planeada |
| `p_sched_dep` | `TRAIN_STOP.sched_dep%TYPE` | Hora de partida planeada |
| `p_is_stop` | `NUMBER` (1 ou 0) | Tipo de movimento: `1` = com paragem, `0` = passagem |
| `p_act_arr` | `TRAIN_STOP.act_arr%TYPE` | Hora de chegada real |
| `p_act_dep` | `TRAIN_STOP.act_dep%TYPE` | Hora de partida real (apenas para movimentos com paragem) |


***

## Retorno

| Valor | Significado |
| :-- | :-- |
| `train_stop_id` (NUMBER) | ID gerado para o novo registo em `TRAIN_STOP` |


***

## Códigos de Erro

| Código | Mensagem | Condição |
| :-- | :-- | :-- |
| -20001 | `Para movimentos com paragem, act_arr e act_dep são obrigatórios.` | `p_is_stop = 1` e `p_act_arr` ou `p_act_dep` a `NULL` |
| -20002 | `act_dep deve ser posterior a act_arr para movimentos com paragem.` | `p_is_stop = 1` e `p_act_dep <= p_act_arr` |
| -20003 | `Para movimentos sem paragem, act_dep não é permitido.` | `p_is_stop = 0` e `p_act_dep` não é `NULL` |

Erros de FK (por exemplo, serviço ou instalação inexistentes) são lançados automaticamente pelas constraints da base de dados.

***

## Código da Função

```sql
CREATE OR REPLACE FUNCTION fn_register_train_movement (
    p_service_id    IN TRAIN_STOP.service_id%TYPE,
    p_facility_id   IN TRAIN_STOP.facility_id%TYPE,
    p_parking_track IN TRAIN_STOP.parking_track%TYPE,
    p_position      IN TRAIN_STOP.position%TYPE,
    p_sched_arr     IN TRAIN_STOP.sched_arr%TYPE,
    p_sched_dep     IN TRAIN_STOP.sched_dep%TYPE,
    p_is_stop       IN NUMBER,  -- 1 = pára, 0 = passagem
    p_act_arr       IN TRAIN_STOP.act_arr%TYPE,
    p_act_dep       IN TRAIN_STOP.act_dep%TYPE
) RETURN NUMBER
IS
    v_train_stop_id TRAIN_STOP.train_stop_id%TYPE;
BEGIN
    -- Se é stop, act_arr e act_dep obrigatórios e act_dep > act_arr
    IF p_is_stop = 1 THEN
        IF p_act_arr IS NULL OR p_act_dep IS NULL THEN
            RAISE_APPLICATION_ERROR(
                -20001,
                'Para movimentos com paragem, act_arr e act_dep são obrigatórios.'
            );
        END IF;

        IF p_act_dep <= p_act_arr THEN
            RAISE_APPLICATION_ERROR(
                -20002,
                'act_dep deve ser posterior a act_arr para movimentos com paragem.'
            );
        END IF;
    ELSE
        -- Se não é stop, act_dep não é permitido
        IF p_act_dep IS NOT NULL THEN
            RAISE_APPLICATION_ERROR(
                -20003,
                'Para movimentos sem paragem, act_dep não é permitido.'
            );
        END IF;
    END IF;

    -- FKs (service_id, facility_id, parking_track) validadas pelas constraints do schema

    INSERT INTO TRAIN_STOP (
        service_id,
        facility_id,
        parking_track,
        position,
        sched_arr,
        sched_dep,
        act_arr,
        act_dep,
        act_stop_time
    )
    VALUES (
        p_service_id,
        p_facility_id,
        p_parking_track,
        p_position,
        p_sched_arr,
        p_sched_dep,
        p_act_arr,
        p_act_dep,
        CASE
            WHEN p_is_stop = 1 THEN
                ROUND(
                    (CAST(p_act_dep AS DATE) - CAST(p_act_arr AS DATE)) * 24 * 60
                )  -- minutos
            ELSE
                NULL
        END
    )
    RETURNING train_stop_id INTO v_train_stop_id;

    RETURN v_train_stop_id;
END;
/
```


***

## Exemplo de Uso e Testes

```sql
SET SERVEROUTPUT ON;

DECLARE
    v_result NUMBER;
    v_count  NUMBER;
BEGIN
    -- limpar apenas os movimentos de teste do service 2001, posições >= 10
    DELETE FROM TRAIN_STOP
    WHERE service_id = 2001
      AND position >= 10;
    COMMIT;

    -----------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('--- 1. VALID PASSING MOVEMENT TEST ---');
    -----------------------------------------------------------------
    BEGIN
        v_result := fn_register_train_movement(
            p_service_id    => 2001,
            p_facility_id   => 5,
            p_parking_track => 1,
            p_position      => 10,
            p_sched_arr     => TO_TIMESTAMP('2025-11-01 08:00:00','YYYY-MM-DD HH24:MI:SS'),
            p_sched_dep     => TO_TIMESTAMP('2025-11-01 08:00:00','YYYY-MM-DD HH24:MI:SS'),
            p_is_stop       => 0,
            p_act_arr       => TO_TIMESTAMP('2025-11-01 08:01:00','YYYY-MM-DD HH24:MI:SS'),
            p_act_dep       => NULL
        );
        DBMS_OUTPUT.PUT_LINE('SUCCESS: Passing movement ID = ' || v_result);
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('ERROR (should NOT fail): ' || SQLERRM);
    END;

    -----------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('--- 2. VALID STOP MOVEMENT TEST ---');
    -----------------------------------------------------------------
    BEGIN
        v_result := fn_register_train_movement(
            p_service_id    => 2001,
            p_facility_id   => 5,
            p_parking_track => 1,
            p_position      => 11,
            p_sched_arr     => TO_TIMESTAMP('2025-11-01 09:00:00','YYYY-MM-DD HH24:MI:SS'),
            p_sched_dep     => TO_TIMESTAMP('2025-11-01 09:15:00','YYYY-MM-DD HH24:MI:SS'),
            p_is_stop       => 1,
            p_act_arr       => TO_TIMESTAMP('2025-11-01 09:02:00','YYYY-MM-DD HH24:MI:SS'),
            p_act_dep       => TO_TIMESTAMP('2025-11-01 09:10:00','YYYY-MM-DD HH24:MI:SS')
        );
        DBMS_OUTPUT.PUT_LINE('SUCCESS: Stop movement ID = ' || v_result);
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('ERROR (should NOT fail): ' || SQLERRM);
    END;

    -----------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('--- 3. MISSING DEPARTURE TIME TEST ---');
    -----------------------------------------------------------------
    BEGIN
        v_result := fn_register_train_movement(
            p_service_id    => 2001,
            p_facility_id   => 5,
            p_parking_track => 1,
            p_position      => 12,
            p_sched_arr     => TO_TIMESTAMP('2025-11-01 10:00:00','YYYY-MM-DD HH24:MI:SS'),
            p_sched_dep     => TO_TIMESTAMP('2025-11-01 10:15:00','YYYY-MM-DD HH24:MI:SS'),
            p_is_stop       => 1,
            p_act_arr       => TO_TIMESTAMP('2025-11-01 10:05:00','YYYY-MM-DD HH24:MI:SS'),
            p_act_dep       => NULL
        );
        DBMS_OUTPUT.PUT_LINE('ERROR: should have failed but returned ' || v_result);
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('EXPECTED ERROR (missing dep): ' || SQLERRM);
    END;

    -----------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('--- 4. INVALID DEPARTURE TIME TEST ---');
    -----------------------------------------------------------------
    BEGIN
        v_result := fn_register_train_movement(
            p_service_id    => 2001,
            p_facility_id   => 5,
            p_parking_track => 1,
            p_position      => 13,
            p_sched_arr     => TO_TIMESTAMP('2025-11-01 11:00:00','YYYY-MM-DD HH24:MI:SS'),
            p_sched_dep     => TO_TIMESTAMP('2025-11-01 11:15:00','YYYY-MM-DD HH24:MI:SS'),
            p_is_stop       => 1,
            p_act_arr       => TO_TIMESTAMP('2025-11-01 11:05:00','YYYY-MM-DD HH24:MI:SS'),
            p_act_dep       => TO_TIMESTAMP('2025-11-01 11:01:00','YYYY-MM-DD HH24:MI:SS')
        );
        DBMS_OUTPUT.PUT_LINE('ERROR: should have failed but returned ' || v_result);
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('EXPECTED ERROR (dep < arr): ' || SQLERRM);
    END;

    -----------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('--- 5. INVALID TRAIN/FACILITY REFERENCE TEST ---');
    -----------------------------------------------------------------
    BEGIN
        v_result := fn_register_train_movement(
            p_service_id    => 9999,
            p_facility_id   => 9999,
            p_parking_track => 1,
            p_position      => 14,
            p_sched_arr     => TO_TIMESTAMP('2025-11-01 12:00:00','YYYY-MM-DD HH24:MI:SS'),
            p_sched_dep     => TO_TIMESTAMP('2025-11-01 12:15:00','YYYY-MM-DD HH24:MI:SS'),
            p_is_stop       => 0,
            p_act_arr       => TO_TIMESTAMP('2025-11-01 12:05:00','YYYY-MM-DD HH24:MI:SS'),
            p_act_dep       => NULL
        );
        DBMS_OUTPUT.PUT_LINE('ERROR: should have failed but returned ' || v_result);
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('EXPECTED FK ERROR: ' || SQLERRM);
    END;

    -----------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('--- 6. DUPLICATE MOVEMENT RECORD TEST ---');
    -----------------------------------------------------------------
    DECLARE
        v_dup_id NUMBER;
    BEGIN
        v_dup_id := fn_register_train_movement(
            p_service_id    => 2001,
            p_facility_id   => 5,
            p_parking_track => 1,
            p_position      => 15,   -- livre
            p_sched_arr     => TO_TIMESTAMP('2025-11-01 13:00:00','YYYY-MM-DD HH24:MI:SS'),
            p_sched_dep     => TO_TIMESTAMP('2025-11-01 13:15:00','YYYY-MM-DD HH24:MI:SS'),
            p_is_stop       => 0,
            p_act_arr       => TO_TIMESTAMP('2025-11-01 13:05:00','YYYY-MM-DD HH24:MI:SS'),
            p_act_dep       => NULL
        );
        DBMS_OUTPUT.PUT_LINE('Base movement created, ID = ' || v_dup_id);

        BEGIN
            v_result := fn_register_train_movement(
                p_service_id    => 2001,
                p_facility_id   => 5,
                p_parking_track => 1,
                p_position      => 15,   -- mesma (service_id, position)
                p_sched_arr     => TO_TIMESTAMP('2025-11-01 13:30:00','YYYY-MM-DD HH24:MI:SS'),
                p_sched_dep     => TO_TIMESTAMP('2025-11-01 13:45:00','YYYY-MM-DD HH24:MI:SS'),
                p_is_stop       => 0,
                p_act_arr       => TO_TIMESTAMP('2025-11-01 13:35:00','YYYY-MM-DD HH24:MI:SS'),
                p_act_dep       => NULL
            );
            DBMS_OUTPUT.PUT_LINE('ERROR: duplicate should have failed but returned ' || v_result);
        EXCEPTION
            WHEN OTHERS THEN
                DBMS_OUTPUT.PUT_LINE('EXPECTED UNIQUE ERROR: ' || SQLERRM);
        END;
    END;

    -----------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('--- 7. DATA DICTIONARY CHECK ---');
    -----------------------------------------------------------------
    SELECT COUNT(*)
      INTO v_count
      FROM TRAIN_STOP
     WHERE service_id = 2001;

    DBMS_OUTPUT.PUT_LINE('TRAIN_STOP rows for service 2001 = ' || v_count);
END;
/
```


***

## Saída Esperada (Resumo)

- Teste 1 e 2: mensagens `SUCCESS` com IDs gerados para movimento de passagem e de paragem.
- Teste 3: mensagem `EXPECTED ERROR (missing dep): ORA-20001 ...`.
- Teste 4: mensagem `EXPECTED ERROR (dep < arr): ORA-20002 ...`.
- Teste 5: mensagem `EXPECTED FK ERROR: ORA-02291 ...` (ou equivalente de FK).
- Teste 6: primeira inserção com sucesso, segunda com `EXPECTED UNIQUE ERROR: ORA-00001 ...` devido ao índice único em `(service_id, position)`.

***

## Observações

- A função não usa `SYSDATE`, sendo totalmente determinística em relação aos parâmetros, o que simplifica os testes automatizados.
- A lógica de negócio concentra-se na coerência entre `p_is_stop`, `act_arr` e `act_dep`; integridade referencial e unicidade de movimentos por serviço/posição são garantidas pelas constraints existentes.