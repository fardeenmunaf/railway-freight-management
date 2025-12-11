DECLARE
    v_id NUMBER;
BEGIN
    v_id := REGISTER_WAGON_FULL(
        'Model_Valid',
        1,
        'CP-123456',
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
END;
/
