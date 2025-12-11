DECLARE
    v_id NUMBER;
BEGIN
    v_id := REGISTER_WAGON_FULL(
        'Model_MissingType',
        NULL,          -- wagon_type em falta (NOT NULL na tabela)
        'CP-NOTNULL',
        2,
        'Duplo',
        20,
        50,
        80,
        12,
        3,
        4,
        120,
        2,
        2020,
        1
    );

    DBMS_OUTPUT.put_line('Wagon id = ' || v_id);
    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        -- Traduz o erro de NOT NULL para um erro de aplicação explícito
        RAISE_APPLICATION_ERROR(
            -20002,
            'Erro esperado: campo obrigatório em falta (NOT NULL). Detalhe: ' || SQLERRM
        );
END;
/
