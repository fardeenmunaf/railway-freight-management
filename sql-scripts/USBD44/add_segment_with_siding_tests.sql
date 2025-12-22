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
    v_line_id NUMBER := 9999;  -- Changed from 999 to 9999 to avoid conflicts
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
END;
/

------------------------------------------------------------
-- Cleanup: Remove any previous test data - AGGRESSIVE MODE
------------------------------------------------------------
ROLLBACK;  -- Rollback any uncommitted transactions first

DECLARE
    v_count NUMBER;
    v_total NUMBER := 0;
    v_segment_count NUMBER;
    v_siding_count NUMBER;
    v_max_segment_id NUMBER;
    v_max_position NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('');
    DBMS_OUTPUT.PUT_LINE('Cleaning up previous test data - AGGRESSIVE MODE...');

    -- Check what exists before cleanup
    SELECT COUNT(*), NVL(MAX(segment_id), 0), NVL(MAX(position), 0)
    INTO v_segment_count, v_max_segment_id, v_max_position
    FROM LINE_SEGMENT WHERE line_id = 9999;

    SELECT COUNT(*) INTO v_siding_count FROM PARKING_TRACK WHERE line_id = 9999;

    DBMS_OUTPUT.PUT_LINE('  Before cleanup: ' || v_segment_count || ' segment(s) (max id: ' || v_max_segment_id || ', max pos: ' || v_max_position || '), ' || v_siding_count || ' siding(s)');

    -- Delete in correct order (FK dependency)
    DELETE FROM PARKING_TRACK WHERE line_id = 9999;
    v_count := SQL%ROWCOUNT;
    v_total := v_total + v_count;
    IF v_count > 0 THEN
        DBMS_OUTPUT.PUT_LINE('  Deleted ' || v_count || ' siding(s)');
    END IF;

    DELETE FROM LINE_SEGMENT WHERE line_id = 9999;
    v_count := SQL%ROWCOUNT;
    v_total := v_total + v_count;
    IF v_count > 0 THEN
        DBMS_OUTPUT.PUT_LINE('  Deleted ' || v_count || ' segment(s)');
    END IF;

    -- Force commit to make changes visible
    COMMIT;

    -- Wait a moment for Oracle to process (workaround for DataGrip caching)
    DBMS_LOCK.SLEEP(1);

    IF v_total = 0 THEN
        DBMS_OUTPUT.PUT_LINE('  No previous test data found');
    ELSE
        DBMS_OUTPUT.PUT_LINE('  Total: ' || v_total || ' record(s) cleaned up');
    END IF;

    -- Verify cleanup after commit
    SELECT COUNT(*) INTO v_segment_count FROM LINE_SEGMENT WHERE line_id = 9999;
    SELECT COUNT(*) INTO v_siding_count FROM PARKING_TRACK WHERE line_id = 9999;

    IF v_segment_count > 0 OR v_siding_count > 0 THEN
        DBMS_OUTPUT.PUT_LINE('  ⚠ WARNING: After cleanup: ' || v_segment_count || ' segment(s), ' || v_siding_count || ' siding(s) STILL EXIST!');
        RAISE_APPLICATION_ERROR(-20999, 'CLEANUP FAILED - Data still exists after delete and commit!');
    ELSE
        DBMS_OUTPUT.PUT_LINE('  ✓ Cleanup verified: Line 9999 is completely clean');
    END IF;

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
            p_line_id         => 9999,
            p_length_km       => 3000,
            p_electrif_type   => 1,
            p_gauge_id        => 1,
            p_max_weight_kg_m => 8000,
            p_speed_limit_kmh => 100,
            p_track_number    => 1,
            p_has_siding      => 0
            );

    DBMS_OUTPUT.PUT_LINE('✓ TESTE 1 PASSED - Segmento sem siding criado. ID = ' || v_id);
    DBMS_OUTPUT.PUT_LINE('  Line ID: 9999');
    DBMS_OUTPUT.PUT_LINE('  Track Number: 1');
    DBMS_OUTPUT.PUT_LINE('  Length: 3000 km');
    DBMS_OUTPUT.PUT_LINE('');
    COMMIT;  -- Commit test data immediately
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
                p_line_id         => 9999,
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
                p_line_id                => 9999,
                p_length_km              => 3600,
                p_electrif_type          => 1,
                p_gauge_id               => 1,
                p_max_weight_kg_m        => 8000,
                p_speed_limit_kmh        => 100,
                p_track_number           => 4,
                p_has_siding             => 1,
                p_siding_facility_id     => 999,
                p_siding_name            => 'TEST_USBD44_SIDING_001',  -- mesmo nome do TEST 2
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
    v_total NUMBER := 0;
BEGIN
    DBMS_OUTPUT.PUT_LINE('');
    DBMS_OUTPUT.PUT_LINE('========================================');
    DBMS_OUTPUT.PUT_LINE('Final Cleanup');
    DBMS_OUTPUT.PUT_LINE('========================================');

    -- Delete ALL sidings from line 9999 first (FK dependency)
    DELETE FROM PARKING_TRACK WHERE line_id = 9999;
    v_count := SQL%ROWCOUNT;
    v_total := v_total + v_count;
    IF v_count > 0 THEN
        DBMS_OUTPUT.PUT_LINE('  Deleted ' || v_count || ' siding(s) from line 9999');
    END IF;

    -- Delete ALL segments from line 9999
    DELETE FROM LINE_SEGMENT WHERE line_id = 9999;
    v_count := SQL%ROWCOUNT;
    v_total := v_total + v_count;
    IF v_count > 0 THEN
        DBMS_OUTPUT.PUT_LINE('  Deleted ' || v_count || ' segment(s) from line 9999');
    END IF;

    IF v_total = 0 THEN
        DBMS_OUTPUT.PUT_LINE('  No test data to clean up');
    ELSE
        DBMS_OUTPUT.PUT_LINE('  Total: ' || v_total || ' record(s) cleaned up');
    END IF;

    COMMIT;

    DBMS_OUTPUT.PUT_LINE('========================================');
    DBMS_OUTPUT.PUT_LINE('All tests completed!');
    DBMS_OUTPUT.PUT_LINE('========================================');
END;
/
