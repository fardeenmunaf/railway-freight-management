DECLARE
    v_id NUMBER;
BEGIN
    BEGIN
        v_id := REGISTER_WAGON_FULL(
            'Model_InvalidGauge',
            1,
            'CP-INVALID',
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
            999999  -- gauge inexistente
        );
    EXCEPTION
        WHEN OTHERS THEN
            -- aqui assumes que o erro é FK de gauge; em produção convém testar SQLCODE
            RAISE_APPLICATION_ERROR(-20001, 'Gauge inválido na criação de wagon.');
    END;

    DBMS_OUTPUT.put_line('Wagon id = ' || v_id);
    COMMIT;
END;
/
