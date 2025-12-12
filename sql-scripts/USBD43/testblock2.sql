--============================================================
-- ANONYMOUS BLOCK TEST 2 - MISSING REQUIRED ATTRIBUTE
-- ============================================================
-- Test Case: Insert without voltage (should fail with error)
-- Expected: ORA-20001 error raised by function validation
-- ============================================================


DECLARE
    v_id NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('========================================');
    DBMS_OUTPUT.PUT_LINE('TEST 2: MISSING REQUIRED ATTRIBUTE');
    DBMS_OUTPUT.PUT_LINE('========================================');
    DBMS_OUTPUT.PUT_LINE('Attempting insert with p_voltage => NULL');
    DBMS_OUTPUT.PUT_LINE('Expected: Application error (ORA-20001)');
    DBMS_OUTPUT.PUT_LINE('');

    v_id := INSERT_ELECTRIC_LOCOMOTIVE(
        P_OPERATOR_ID       => 2,
        P_LOCOMOTIVE_TYPE   => 1,
        P_NAME              => 'E-Loco Missing Voltage',
        P_BRAND             => 'Siemens',
        P_MODEL             => 'Vectron-Test',
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
        P_VOLTAGE           => NULL,     -- REQUIRED - should trigger error
        P_FREQUENCY_HZ      => 50
    );
END;
/

-- Verify NO row was inserted (transaction rolled back)
SELECT COUNT(*) AS inserted_count
FROM LOCOMOTIVE
WHERE name = 'E-Loco Missing Voltage';

COMMIT;