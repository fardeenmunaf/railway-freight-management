-- ============================================================
-- ANONYMOUS BLOCK - TEST INSERT_ELECTRIC_LOCOMOTIVE
-- ============================================================
-- Assumptions:
--   - USER/OPERATOR already exists (id = 1 or 2)
--   - LOCOMOTIVE_TYPE already exists (id = 1)
-- ============================================================

DECLARE
    v_id NUMBER;
    v_operator_id NUMBER := 2;              -- Adjust if needed
    v_locomotive_type NUMBER := 1;          -- Adjust if needed
BEGIN
    DBMS_OUTPUT.PUT_LINE('========================================');
    DBMS_OUTPUT.PUT_LINE('Testing: insert_electric_locomotive()');
    DBMS_OUTPUT.PUT_LINE('========================================');
    
    v_id := insert_electric_locomotive(
        p_operator_id       => v_operator_id,
        p_locomotive_type   => v_locomotive_type,
        p_name              => 'E-Loco Valid',
        p_brand             => 'Siemens',
        p_model             => 'Vectron-V1',
        p_year              => 2025,
        p_power_kw          => 6000,
        p_acc_ms2           => 5,
        p_length_m          => 19.0,
        p_width_m           => 3.0,
        p_height_m          => 4.2,
        p_max_weight_t      => 90,
        p_total_weight_t    => 88,
        p_max_speed_kmh     => 200,
        p_oper_speed_kmh    => 160,
        p_num_bogies        => 2,
        p_bogie_type        => 'Bo-Bo',
        p_traction_kn       => '300 kN',
        p_voltage           => 25,
        p_frequency_hz      => 50,
        p_start_facility    => 50
    );
    
    DBMS_OUTPUT.PUT_LINE('   SUCCESS - Locomotive created!');
    DBMS_OUTPUT.PUT_LINE('   Locomotive ID: ' || v_id);
    DBMS_OUTPUT.PUT_LINE('   Name: E-Loco Valid');
    DBMS_OUTPUT.PUT_LINE('   Brand: Siemens');
    DBMS_OUTPUT.PUT_LINE('   Model: Vectron-V1');
    DBMS_OUTPUT.PUT_LINE('   Voltage: 25 kV');
    DBMS_OUTPUT.PUT_LINE('   Frequency: 50 Hz');
    DBMS_OUTPUT.PUT_LINE('   Start Facility ID: 50');
    DBMS_OUTPUT.PUT_LINE('========================================');
    
EXCEPTION 
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('   ERROR OCCURRED:');
        DBMS_OUTPUT.PUT_LINE('   Error Code: ' || SQLCODE);
        DBMS_OUTPUT.PUT_LINE('   Error Message: ' || SQLERRM);
        DBMS_OUTPUT.PUT_LINE('========================================');
        ROLLBACK;
END;
/

-- Verify insertion
SELECT locomotive_id, name, brand, model,start_facility, created_at
FROM LOCOMOTIVE 
WHERE name = 'E-Loco Valid' 
ORDER BY locomotive_id DESC 
FETCH FIRST 5 ROW ONLY;

-- Verify electric details
SELECT le.locomotive_id, le.voltage, le.frequency_hz
FROM LOCOMOTIVE_ELECTRIC le
JOIN LOCOMOTIVE l ON le.locomotive_id = l.locomotive_id
WHERE l.name = 'E-Loco Valid'
ORDER BY le.locomotive_id DESC
FETCH FIRST 5 ROW ONLY;

COMMIT;