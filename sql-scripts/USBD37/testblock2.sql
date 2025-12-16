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
            999999,  -- gauge inexistente
            50
        );
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20001, 'Gauge inválido na criação de wagon.');
    END;

    DBMS_OUTPUT.put_line('Wagon id = ' || v_id);
    COMMIT;
END;
/
