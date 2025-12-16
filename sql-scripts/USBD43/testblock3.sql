-- ============================================================
-- TEST 3: MISSING FREQUENCY - Should fail with ORA-20001
-- ============================================================
DECLARE
    v_id NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('');
    DBMS_OUTPUT.PUT_LINE('========================================');
    DBMS_OUTPUT.PUT_LINE('TEST 3: MISSING FREQUENCY (NULL)');
    DBMS_OUTPUT.PUT_LINE('========================================');
    DBMS_OUTPUT.PUT_LINE('Expected: ORA-20001 validation error');

    v_id := INSERT_ELECTRIC_LOCOMOTIVE(
        P_OPERATOR_ID       => 2,
        P_LOCOMOTIVE_TYPE   => 1,
        P_NAME              => 'E-Loco-No-Frequency',
        P_BRAND             => 'Siemens',
        P_MODEL             => 'Test-Model',
        P_YEAR              => 2024,
        P_POWER_KW          => 3000,
        P_ACC_MS2           => 0.5,
        P_LENGTH_M          => 18.0,
        P_WIDTH_M           => 3.0,
        P_HEIGHT_M          => 4.0,
        P_MAX_WEIGHT_T      => 85,
        P_TOTAL_WEIGHT_T    => 80,
        P_MAX_SPEED_KMH     => 160,
        P_OPER_SPEED_KMH    => 140,
        P_NUM_BOGIES        => 2,
        P_BOGIE_TYPE        => 'B-B',
        P_TRACTION_KN       => '250 kN',
        P_VOLTAGE           => 25,
        P_FREQUENCY_HZ      => NULL,      -- MISSING!
        p_START_FACILITY    => 50
    );
END;
/