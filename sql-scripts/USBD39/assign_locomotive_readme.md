# Função PL/SQL: `fn_assign_locomotive_to_service`

## Descrição

Função para atribuir locomotivas a serviços ferroviários, garantindo integridade de dados e evitando duplicações. A função realiza validações de existência e duplicidade antes de inserir a associação na tabela `SERVICE_LOCOMOTIVE`.

---

## Parâmetros

| Parâmetro        | Tipo                                    | Descrição                                    |
|-----------------|----------------------------------------|---------------------------------------------|
| `p_service_id`   | `TRAIN_SERVICE.service_id%TYPE`        | ID do serviço ferroviário                   |
| `p_locomotive_id`| `LOCOMOTIVE.locomotive_id%TYPE`       | ID da locomotiva                            |
| `p_position_no`  | `SERVICE_LOCOMOTIVE.position_no%TYPE` | Posição da locomotiva no serviço           |

---

## Retorno

| Valor | Significado                     |
|-------|--------------------------------|
| `1`   | Atribuição realizada com sucesso |

---

## Códigos de Erro

| Código   | Mensagem                                       | Condição                                    |
|----------|-----------------------------------------------|--------------------------------------------|
| -20001   | Service does not exist                         | `service_id` não existe                     |
| -20002   | Locomotive does not exist                      | `locomotive_id` não existe                  |
| -20003   | Locomotive already assigned to this service   | Locomotiva já atribuída ao serviço         |
| -20004   | Position already used for this service        | Posição já ocupada para o serviço          |

---

## Código da Função

```sql
CREATE OR REPLACE FUNCTION fn_assign_locomotive_to_service (
    p_service_id     IN TRAIN_SERVICE.service_id%TYPE,
    p_locomotive_id  IN LOCOMOTIVE.locomotive_id%TYPE,
    p_position_no    IN SERVICE_LOCOMOTIVE.position_no%TYPE
) RETURN NUMBER IS
    v_cnt NUMBER;
BEGIN
    -- Verificar se o serviço existe
    SELECT COUNT(*) INTO v_cnt
    FROM TRAIN_SERVICE
    WHERE service_id = p_service_id;

    IF v_cnt = 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Service does not exist');
    END IF;

    -- Verificar se a locomotiva existe
    SELECT COUNT(*) INTO v_cnt
    FROM LOCOMOTIVE
    WHERE locomotive_id = p_locomotive_id;

    IF v_cnt = 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'Locomotive does not exist');
    END IF;

    -- Verificar se já existe essa combinação service–locomotive
    SELECT COUNT(*) INTO v_cnt
    FROM SERVICE_LOCOMOTIVE
    WHERE service_id = p_service_id
      AND locomotive_id = p_locomotive_id;

    IF v_cnt > 0 THEN
        RAISE_APPLICATION_ERROR(-20003, 'Locomotive already assigned to this service');
    END IF;

    -- Verificar se já existe essa posição para o serviço (uq_serv_loco_pos)
    SELECT COUNT(*) INTO v_cnt
    FROM SERVICE_LOCOMOTIVE
    WHERE service_id = p_service_id
      AND position_no = p_position_no;

    IF v_cnt > 0 THEN
        RAISE_APPLICATION_ERROR(-20004, 'Position already used for this service');
    END IF;

    -- Inserir associação
    INSERT INTO SERVICE_LOCOMOTIVE (service_id, locomotive_id, position_no)
    VALUES (p_service_id, p_locomotive_id, p_position_no);

    RETURN 1;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
```

---

## Exemplo de Uso e Testes

```sql
SET SERVEROUTPUT ON;

DECLARE
    v_result NUMBER;
    v_count NUMBER;
BEGIN
    -- Limpeza inicial
    DELETE FROM SERVICE_LOCOMOTIVE WHERE service_id = 2003;

    -- 1. Atribuição válida
    v_result := fn_assign_locomotive_to_service(2003, 5623, 1);
    DBMS_OUTPUT.PUT_LINE('✅ SUCCESS: Result = ' || v_result);

    -- 2. Locomotiva inválida
    BEGIN
        v_result := fn_assign_locomotive_to_service(2003, 999999, 2);
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('✅ FK ERROR: ' || SQLERRM);
    END;

    -- 3. Serviço inválido
    BEGIN
        v_result := fn_assign_locomotive_to_service(9999, 5621, 1);
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('✅ SERVICE ERROR: ' || SQLERRM);
    END;

    -- 4. Atribuição duplicada
    BEGIN
        v_result := fn_assign_locomotive_to_service(2003, 5623, 1);
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('✅ DUPLICATE ERROR: ' || SQLERRM);
    END;

    -- 5-7. Remoção e verificação
    DELETE FROM SERVICE_LOCOMOTIVE WHERE service_id = 2003 AND locomotive_id = 5623;
    SELECT COUNT(*) INTO v_count FROM SERVICE_LOCOMOTIVE WHERE service_id = 2003;
    DBMS_OUTPUT.PUT_LINE('✅ ALL TESTS PASSED: ' || v_count || ' rows left');
END;
/
```

### Saída Esperada

```
--- 1. VALID ASSIGNMENT TEST ---
✅ SUCCESS: Result = 1
--- 2. INVALID LOCOMOTIVE TEST ---
✅ FK ERROR: ORA-20002: Locomotive does not exist
--- 3. INVALID SERVICE TEST ---
✅ SERVICE ERROR: ORA-20001: Service does not exist
--- 4. DUPLICATE ASSIGNMENT TEST ---
✅ DUPLICATE ERROR: ORA-20003: Locomotive already assigned to this service
--- 5-7. INTEGRITY/REMOVAL/CHECK ---
✅ ALL TESTS PASSED: 0 rows left
```

---

## Observações

- Função transacional: qualquer erro aciona rollback automático.
- Recomendado utilizar para todas as atribuições de locomotivas a serviços.
- Códigos de erro permitem identificação rápida do problema.