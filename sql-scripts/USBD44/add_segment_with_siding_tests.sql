-- Enable DBMS_OUTPUT for DataGrip/SQL Developer
BEGIN
    DBMS_OUTPUT.ENABLE(1000000);
END;
/

------------------------------------------------------------
-- Setup: Ensure required test data exists
------------------------------------------------------------
DECLARE
    v_count NUMBER;
    v_line_id NUMBER := 999;
    v_gauge_id NUMBER := 1;
    v_facility_id NUMBER := 999;
    v_owner_id NUMBER := 1;
BEGIN
    DBMS_OUTPUT.PUT_LINE('========================================');
    DBMS_OUTPUT.PUT_LINE('USBD44 - Test Data Setup');
    DBMS_OUTPUT.PUT_LINE('========================================');

    -- Check and ensure GAUGE exists
    SELECT COUNT(*) INTO v_count FROM GAUGE WHERE gauge_id = v_gauge_id;
    IF v_count = 0 THEN
        DBMS_OUTPUT.PUT_LINE('Creating test GAUGE...');
        INSERT INTO GAUGE (gauge_id, name, size_mm) VALUES (v_gauge_id, 'Iberian Gauge', 1668);
        COMMIT;
    END IF;
    DBMS_OUTPUT.PUT_LINE('✓ GAUGE ID ' || v_gauge_id || ' exists');

    -- Check and ensure COMPANY exists for owner
    SELECT COUNT(*) INTO v_count FROM COMPANY WHERE company_id = v_owner_id;
    IF v_count = 0 THEN
        DBMS_OUTPUT.PUT_LINE('Creating test COMPANY...');
        INSERT INTO COMPANY (company_id, name, short_name, vat)
        VALUES (v_owner_id, 'Infraestruturas de Portugal, SA', 'IP', 'PT503933813');
        COMMIT;
    END IF;
    DBMS_OUTPUT.PUT_LINE('✓ COMPANY ID ' || v_owner_id || ' exists');

    -- Check and ensure LINE exists
    SELECT COUNT(*) INTO v_count FROM LINE WHERE line_id = v_line_id;
    IF v_count = 0 THEN
        DBMS_OUTPUT.PUT_LINE('Creating test LINE...');
        INSERT INTO LINE (line_id, name, owner_id)
        VALUES (v_line_id, 'TEST LINE FOR USBD44', v_owner_id);
        COMMIT;
    END IF;
    DBMS_OUTPUT.PUT_LINE('✓ LINE ID ' || v_line_id || ' exists');

    -- Check and ensure FACILITY_TYPE exists
    SELECT COUNT(*) INTO v_count FROM FACILITY_TYPE WHERE facility_type_id = 1;
    IF v_count = 0 THEN
        DBMS_OUTPUT.PUT_LINE('Creating test FACILITY_TYPE...');
        INSERT INTO FACILITY_TYPE (facility_type_id, name) VALUES (1, 'Warehouse');
        COMMIT;
    END IF;

    -- Check and ensure FACILITY exists
    SELECT COUNT(*) INTO v_count FROM FACILITY WHERE facility_id = v_facility_id;
    IF v_count = 0 THEN
        DBMS_OUTPUT.PUT_LINE('Creating test FACILITY...');
        INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal)
        VALUES (v_facility_id, 'TEST FACILITY FOR USBD44', 1, 41.0, -8.0, 0);
        COMMIT;
    END IF;
    DBMS_OUTPUT.PUT_LINE('✓ FACILITY ID ' || v_facility_id || ' exists');

    DBMS_OUTPUT.PUT_LINE('========================================');
    DBMS_OUTPUT.PUT_LINE('Setup completed successfully!');
    DBMS_OUTPUT.PUT_LINE('========================================');
    DBMS_OUTPUT.PUT_LINE('');
END;
/

------------------------------------------------------------
-- Cleanup: Remove any previous test data
------------------------------------------------------------
DECLARE
    v_count NUMBER;
BEGIN
    -- Delete test sidings
    DELETE FROM PARKING_TRACK WHERE name LIKE 'SIDING\_TEST%' ESCAPE '\';
    v_count := SQL%ROWCOUNT;
    IF v_count > 0 THEN
        DBMS_OUTPUT.PUT_LINE('Cleaned up ' || v_count || ' test siding(s)');
    END IF;

    -- Delete test segments (use line_id 999 and specific track numbers used in tests)
    DELETE FROM LINE_SEGMENT
    WHERE line_id = 999
    AND track_number IN (1, 2, 3, 4);
    v_count := SQL%ROWCOUNT;
    IF v_count > 0 THEN
        DBMS_OUTPUT.PUT_LINE('Cleaned up ' || v_count || ' test segment(s)');
    END IF;

    COMMIT;
    DBMS_OUTPUT.PUT_LINE('');
END;
/

------------------------------------------------------------
-- TESTE 1: Inserir segmento sem siding
------------------------------------------------------------
DECLARE
    v_id NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('============================================================');
    DBMS_OUTPUT.PUT_LINE('TESTE 1: Inserir segmento sem siding');
    DBMS_OUTPUT.PUT_LINE('============================================================');

    v_id := add_segment_with_siding(
            p_line_id         => 999,
            p_length_km       => 3000,
            p_electrif_type   => 1,
            p_gauge_id        => 1,
            p_max_weight_kg_m => 8000,
            p_speed_limit_kmh => 100,
            p_track_number    => 1,
            p_has_siding      => 0
            );

    DBMS_OUTPUT.PUT_LINE('✓ TESTE 1 PASSED - Segmento sem siding criado. ID = ' || v_id);
    DBMS_OUTPUT.PUT_LINE('  Line ID: 999');
    DBMS_OUTPUT.PUT_LINE('  Track Number: 1');
    DBMS_OUTPUT.PUT_LINE('  Length: 3000 km');
    DBMS_OUTPUT.PUT_LINE('');
END;
/

------------------------------------------------------------
-- TESTE 2: Inserir segmento com siding
------------------------------------------------------------
DECLARE
    v_id NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('============================================================');
    DBMS_OUTPUT.PUT_LINE('TESTE 2: Inserir segmento com siding');
    DBMS_OUTPUT.PUT_LINE('============================================================');

    v_id := add_segment_with_siding(
            p_line_id                => 999,
            p_length_km              => 3500,
            p_electrif_type          => 1,
            p_gauge_id               => 1,
            p_max_weight_kg_m        => 8000,
            p_speed_limit_kmh        => 120,
            p_track_number           => 2,
            p_has_siding             => 1,
            p_siding_facility_id     => 999,
            p_siding_name            => 'SIDING_TEST',
            p_siding_capacity_wagons => 10
            );

    DBMS_OUTPUT.PUT_LINE('✓ TESTE 2 PASSED - Segmento + siding criado. ID = ' || v_id);
    DBMS_OUTPUT.PUT_LINE('  Line ID: 999');
    DBMS_OUTPUT.PUT_LINE('  Track Number: 2');
    DBMS_OUTPUT.PUT_LINE('  Siding: SIDING_TEST (Capacity: 10 wagons)');
    DBMS_OUTPUT.PUT_LINE('');
END;
/

------------------------------------------------------------
-- TESTE 3: Erro esperado (siding obrigatório mas faltam dados)
------------------------------------------------------------
DECLARE
    v_id NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('============================================================');
    DBMS_OUTPUT.PUT_LINE('TESTE 3: Erro esperado (siding obrigatório mas faltam dados)');
    DBMS_OUTPUT.PUT_LINE('============================================================');

    v_id := add_segment_with_siding(
                p_line_id         => 999,
                p_length_km       => 4000,
                p_electrif_type   => 1,
                p_gauge_id        => 1,
                p_max_weight_kg_m => 8000,
                p_speed_limit_kmh => 100,
                p_track_number    => 3,
                p_has_siding      => 1,
                p_siding_facility_id => NULL,
                p_siding_name        => NULL,
                p_siding_capacity_wagons => NULL
                );

    DBMS_OUTPUT.PUT_LINE('✗ TESTE 3 FAILED - Should have raised an error');
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('✓ TESTE 3 PASSED - Erro esperado: ' || SQLERRM);
END;
/

------------------------------------------------------------
-- TESTE 4: Erro esperado (line_id inexistente)
------------------------------------------------------------
DECLARE
    v_id NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('============================================================');
    DBMS_OUTPUT.PUT_LINE('TESTE 4: Erro esperado (line_id inexistente)');
    DBMS_OUTPUT.PUT_LINE('============================================================');

    v_id := add_segment_with_siding(
                p_line_id         => 123456,
                p_length_km       => 3000,
                p_electrif_type   => 1,
                p_gauge_id        => 1,
                p_max_weight_kg_m => 8000,
                p_speed_limit_kmh => 100,
                p_track_number    => 1,
                p_has_siding      => 0
                );

    DBMS_OUTPUT.PUT_LINE('✗ TESTE 4 FAILED - Should have raised an error');
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('✓ TESTE 4 PASSED - Erro esperado: ' || SQLERRM);
END;
/

------------------------------------------------------------
-- TESTE 5: Erro de nome duplicado no siding
------------------------------------------------------------
DECLARE
    v_id NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('============================================================');
    DBMS_OUTPUT.PUT_LINE('TESTE 5: Erro de nome duplicado no siding');
    DBMS_OUTPUT.PUT_LINE('============================================================');

    v_id := add_segment_with_siding(
                p_line_id                => 999,
                p_length_km              => 3600,
                p_electrif_type          => 1,
                p_gauge_id               => 1,
                p_max_weight_kg_m        => 8000,
                p_speed_limit_kmh        => 100,
                p_track_number           => 4,
                p_has_siding             => 1,
                p_siding_facility_id     => 999,
                p_siding_name            => 'SIDING_TEST',  -- já existe
                p_siding_capacity_wagons => 10
                );

    DBMS_OUTPUT.PUT_LINE('✗ TESTE 5 FAILED - Should have raised duplicate name error');
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('✓ TESTE 5 PASSED - Erro esperado (nome duplicado): ' || SQLERRM);
END;
/

------------------------------------------------------------
-- Final Cleanup
------------------------------------------------------------
DECLARE
    v_count NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('');
    DBMS_OUTPUT.PUT_LINE('========================================');
    DBMS_OUTPUT.PUT_LINE('Final Cleanup');
    DBMS_OUTPUT.PUT_LINE('========================================');

    -- Delete test sidings
    DELETE FROM PARKING_TRACK WHERE name LIKE 'SIDING\_TEST%' ESCAPE '\';
    v_count := SQL%ROWCOUNT;
    IF v_count > 0 THEN
        DBMS_OUTPUT.PUT_LINE('Cleaned up ' || v_count || ' test siding(s)');
    END IF;

    -- Delete test segments
    DELETE FROM LINE_SEGMENT
    WHERE line_id = 999
    AND track_number IN (1, 2, 3, 4);
    v_count := SQL%ROWCOUNT;
    IF v_count > 0 THEN
        DBMS_OUTPUT.PUT_LINE('Cleaned up ' || v_count || ' test segment(s)');
    END IF;

    COMMIT;

    DBMS_OUTPUT.PUT_LINE('========================================');
    DBMS_OUTPUT.PUT_LINE('All tests completed!');
    DBMS_OUTPUT.PUT_LINE('========================================');
END;
/
