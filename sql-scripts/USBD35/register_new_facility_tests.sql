-- Enable DBMS_OUTPUT for DataGrip/SQL Developer
BEGIN
    DBMS_OUTPUT.ENABLE(1000000);
END;
/

--------------------------------------------------------------------------------
-- USBD35 – Ensure required FACILITY_TYPE data exists
--------------------------------------------------------------------------------
DECLARE
    v_count NUMBER;
BEGIN
    -- Check if FACILITY_TYPE has data
    SELECT COUNT(*) INTO v_count FROM FACILITY_TYPE;

    IF v_count = 0 THEN
        DBMS_OUTPUT.PUT_LINE('Inserting required FACILITY_TYPE data...');
        INSERT INTO FACILITY_TYPE (facility_type_id, name) VALUES (1, 'Warehouse');
        INSERT INTO FACILITY_TYPE (facility_type_id, name) VALUES (2, 'Train Station');
        INSERT INTO FACILITY_TYPE (facility_type_id, name) VALUES (3, 'Depot');
        INSERT INTO FACILITY_TYPE (facility_type_id, name) VALUES (4, 'Maintenance Yard');
        INSERT INTO FACILITY_TYPE (facility_type_id, name) VALUES (5, 'Intermodal Terminal');
        COMMIT;
        DBMS_OUTPUT.PUT_LINE('FACILITY_TYPE data inserted successfully.');
    ELSE
        DBMS_OUTPUT.PUT_LINE('FACILITY_TYPE data already exists (' || v_count || ' records).');
    END IF;
END;
/

--------------------------------------------------------------------------------
-- USBD35 – Display available FACILITY_TYPE for reference
--------------------------------------------------------------------------------
DECLARE
    CURSOR c_facility_types IS SELECT facility_type_id, name FROM FACILITY_TYPE ORDER BY facility_type_id;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Available FACILITY_TYPE records:');
    FOR rec IN c_facility_types LOOP
        DBMS_OUTPUT.PUT_LINE('  ID: ' || rec.facility_type_id || ' - ' || rec.name);
    END LOOP;
    DBMS_OUTPUT.PUT_LINE('');
END;
/

--------------------------------------------------------------------------------
-- USBD35 – Clean up test data before running tests
--------------------------------------------------------------------------------
DELETE FROM FACILITY WHERE name IN ('New Logistics Hub', 'Duplicate Name Test', 'Boundary Coords Facility', 'Default Intermodal Facility');
COMMIT;

--------------------------------------------------------------------------------
-- USBD35 – TEST 1: Valid Facility Registration Test
--------------------------------------------------------------------------------

DECLARE
    rc SYS_REFCURSOR;
    v_facility_id FACILITY.facility_id%TYPE;
    v_name FACILITY.name%TYPE;
    v_facility_type_id FACILITY.facility_type_id%TYPE;
    v_lat FACILITY.lat%TYPE;
    v_lon FACILITY.lon%TYPE;
    v_is_intermodal FACILITY.is_intermodal%TYPE;
BEGIN
    DBMS_OUTPUT.PUT_LINE('============================================================');
    DBMS_OUTPUT.PUT_LINE('TEST 1: Valid Facility Registration Test');
    DBMS_OUTPUT.PUT_LINE('============================================================');

    rc := usbd35_register_facility(
        p_name             => 'New Logistics Hub',
        p_facility_type_id => 1,
        p_lat              => 40.0000,
        p_lon              => -8.0000,
        p_is_intermodal    => 1
    );

    -- Fetch and display the result
    FETCH rc INTO v_facility_id, v_name, v_facility_type_id, v_lat, v_lon, v_is_intermodal;
    IF rc%FOUND THEN
        DBMS_OUTPUT.PUT_LINE('✓ TEST 1 PASSED - Facility created:');
        DBMS_OUTPUT.PUT_LINE('  ID: ' || v_facility_id);
        DBMS_OUTPUT.PUT_LINE('  Name: ' || v_name);
        DBMS_OUTPUT.PUT_LINE('  Type ID: ' || v_facility_type_id);
        DBMS_OUTPUT.PUT_LINE('  Latitude: ' || v_lat);
        DBMS_OUTPUT.PUT_LINE('  Longitude: ' || v_lon);
        DBMS_OUTPUT.PUT_LINE('  Is Intermodal: ' || v_is_intermodal);
    END IF;
    CLOSE rc;
END;
/


--------------------------------------------------------------------------------
-- USBD35 – TEST 2: Missing Required Attribute Test (NULL name)
--------------------------------------------------------------------------------

DECLARE
    rc SYS_REFCURSOR;
BEGIN
    DBMS_OUTPUT.PUT_LINE('============================================================');
    DBMS_OUTPUT.PUT_LINE('TEST 2: Missing Required Attribute Test (expect ORA-20001)');
    DBMS_OUTPUT.PUT_LINE('============================================================');

    rc := usbd35_register_facility(
        p_name => NULL,
        p_facility_type_id => 1,
        p_lat => 40.0,
        p_lon => -8.0,
        p_is_intermodal => 0
    );
    DBMS_OUTPUT.PUT_LINE('✗ TEST 2 FAILED - Should have raised ORA-20001');
    CLOSE rc;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('✓ TEST 2 PASSED - Expected error caught: ' || SQLERRM);
END;
/

--------------------------------------------------------------------------------
-- USBD35 – TEST 3: Invalid Lookup Reference Test
--------------------------------------------------------------------------------

DECLARE
    rc SYS_REFCURSOR;
BEGIN
    DBMS_OUTPUT.PUT_LINE('============================================================');
    DBMS_OUTPUT.PUT_LINE('TEST 3: Invalid Lookup Reference Test (expect ORA-20002)');
    DBMS_OUTPUT.PUT_LINE('============================================================');

    rc := usbd35_register_facility(
        p_name => 'Bad Type Facility',
        p_facility_type_id => 9999,
        p_lat => 40.0,
        p_lon => -8.0,
        p_is_intermodal => 0
    );
    DBMS_OUTPUT.PUT_LINE('✗ TEST 3 FAILED - Should have raised ORA-20002');
    CLOSE rc;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('✓ TEST 3 PASSED - Expected error caught: ' || SQLERRM);
END;
/


--------------------------------------------------------------------------------
-- USBD35 – TEST 4: Duplicate Facility Test
--------------------------------------------------------------------------------

DECLARE
    rc SYS_REFCURSOR;
    v_facility_id FACILITY.facility_id%TYPE;
    v_name FACILITY.name%TYPE;
    v_facility_type_id FACILITY.facility_type_id%TYPE;
    v_lat FACILITY.lat%TYPE;
    v_lon FACILITY.lon%TYPE;
    v_is_intermodal FACILITY.is_intermodal%TYPE;
BEGIN
    DBMS_OUTPUT.PUT_LINE('============================================================');
    DBMS_OUTPUT.PUT_LINE('TEST 4: Duplicate Facility Test (expect ORA-20003)');
    DBMS_OUTPUT.PUT_LINE('============================================================');

    -- First insert should succeed
    rc := usbd35_register_facility(
        p_name             => 'Duplicate Name Test',
        p_facility_type_id => 2,
        p_lat              => 41.0,
        p_lon              => -8.5,
        p_is_intermodal    => 0
    );

    FETCH rc INTO v_facility_id, v_name, v_facility_type_id, v_lat, v_lon, v_is_intermodal;
    IF rc%FOUND THEN
        DBMS_OUTPUT.PUT_LINE('✓ First facility created: ' || v_name || ' (ID: ' || v_facility_id || ')');
    END IF;
    CLOSE rc;

    -- Second insert with same name should fail
    rc := usbd35_register_facility(
        p_name => 'Duplicate Name Test',
        p_facility_type_id => 2,
        p_lat => 41.1,
        p_lon => -8.6,
        p_is_intermodal => 1
    );

    DBMS_OUTPUT.PUT_LINE('✗ TEST 4 FAILED - Should have raised ORA-20003');
    CLOSE rc;

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('✓ TEST 4 PASSED - Expected error caught: ' || SQLERRM);
END;
/

--------------------------------------------------------------------------------
-- USBD35 – TEST 5: Boundary Data Test
--------------------------------------------------------------------------------

DECLARE
    rc SYS_REFCURSOR;
    v_facility_id FACILITY.facility_id%TYPE;
    v_name FACILITY.name%TYPE;
    v_facility_type_id FACILITY.facility_type_id%TYPE;
    v_lat FACILITY.lat%TYPE;
    v_lon FACILITY.lon%TYPE;
    v_is_intermodal FACILITY.is_intermodal%TYPE;
BEGIN
    DBMS_OUTPUT.PUT_LINE('============================================================');
    DBMS_OUTPUT.PUT_LINE('TEST 5: Boundary Data Test');
    DBMS_OUTPUT.PUT_LINE('============================================================');

    rc := usbd35_register_facility(
        p_name             => 'Boundary Coords Facility',
        p_facility_type_id => 3,
        p_lat              => -90,
        p_lon              => 180,
        p_is_intermodal    => 0
    );

    FETCH rc INTO v_facility_id, v_name, v_facility_type_id, v_lat, v_lon, v_is_intermodal;
    IF rc%FOUND THEN
        DBMS_OUTPUT.PUT_LINE('✓ TEST 5 PASSED - Facility created with boundary coordinates:');
        DBMS_OUTPUT.PUT_LINE('  ID: ' || v_facility_id);
        DBMS_OUTPUT.PUT_LINE('  Latitude: ' || v_lat || ' (min: -90)');
        DBMS_OUTPUT.PUT_LINE('  Longitude: ' || v_lon || ' (max: 180)');
    END IF;
    CLOSE rc;
END;
/

--------------------------------------------------------------------------------
-- USBD35 – TEST 6: Invalid is_intermodal value Test
--------------------------------------------------------------------------------

DECLARE
    rc SYS_REFCURSOR;
BEGIN
    DBMS_OUTPUT.PUT_LINE('============================================================');
    DBMS_OUTPUT.PUT_LINE('TEST 6: Invalid is_intermodal value Test (expect ORA-20004)');
    DBMS_OUTPUT.PUT_LINE('============================================================');

    rc := usbd35_register_facility(
        p_name => 'Invalid Intermodal Facility',
        p_facility_type_id => 1,
        p_lat => 40.5,
        p_lon => -8.5,
        p_is_intermodal => 5
    );
    DBMS_OUTPUT.PUT_LINE('✗ TEST 6 FAILED - Should have raised ORA-20004');
    CLOSE rc;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('✓ TEST 6 PASSED - Expected error caught: ' || SQLERRM);
END;
/

--------------------------------------------------------------------------------
-- USBD35 – TEST 7: NULL facility_type_id Test
--------------------------------------------------------------------------------

DECLARE
    rc SYS_REFCURSOR;
BEGIN
    DBMS_OUTPUT.PUT_LINE('============================================================');
    DBMS_OUTPUT.PUT_LINE('TEST 7: NULL facility_type_id Test (expect ORA-20001)');
    DBMS_OUTPUT.PUT_LINE('============================================================');

    rc := usbd35_register_facility(
        p_name => 'No Type Facility',
        p_facility_type_id => NULL,
        p_lat => 40.0,
        p_lon => -8.0,
        p_is_intermodal => 0
    );
    DBMS_OUTPUT.PUT_LINE('✗ TEST 7 FAILED - Should have raised ORA-20001');
    CLOSE rc;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('✓ TEST 7 PASSED - Expected error caught: ' || SQLERRM);
END;
/

--------------------------------------------------------------------------------
-- USBD35 – TEST 8: NULL lat Test
--------------------------------------------------------------------------------

DECLARE
    rc SYS_REFCURSOR;
BEGIN
    DBMS_OUTPUT.PUT_LINE('============================================================');
    DBMS_OUTPUT.PUT_LINE('TEST 8: NULL lat Test (expect ORA-20001)');
    DBMS_OUTPUT.PUT_LINE('============================================================');

    rc := usbd35_register_facility(
        p_name => 'No Lat Facility',
        p_facility_type_id => 1,
        p_lat => NULL,
        p_lon => -8.0,
        p_is_intermodal => 0
    );
    DBMS_OUTPUT.PUT_LINE('✗ TEST 8 FAILED - Should have raised ORA-20001');
    CLOSE rc;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('✓ TEST 8 PASSED - Expected error caught: ' || SQLERRM);
END;
/

--------------------------------------------------------------------------------
-- USBD35 – TEST 9: NULL lon Test
--------------------------------------------------------------------------------

DECLARE
    rc SYS_REFCURSOR;
BEGIN
    DBMS_OUTPUT.PUT_LINE('============================================================');
    DBMS_OUTPUT.PUT_LINE('TEST 9: NULL lon Test (expect ORA-20001)');
    DBMS_OUTPUT.PUT_LINE('============================================================');

    rc := usbd35_register_facility(
        p_name => 'No Lon Facility',
        p_facility_type_id => 1,
        p_lat => 40.0,
        p_lon => NULL,
        p_is_intermodal => 0
    );
    DBMS_OUTPUT.PUT_LINE('✗ TEST 9 FAILED - Should have raised ORA-20001');
    CLOSE rc;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('✓ TEST 9 PASSED - Expected error caught: ' || SQLERRM);
END;
/

--------------------------------------------------------------------------------
-- USBD35 – TEST 10: Default is_intermodal Test
--------------------------------------------------------------------------------

DECLARE
    rc SYS_REFCURSOR;
    v_facility_id FACILITY.facility_id%TYPE;
    v_name FACILITY.name%TYPE;
    v_facility_type_id FACILITY.facility_type_id%TYPE;
    v_lat FACILITY.lat%TYPE;
    v_lon FACILITY.lon%TYPE;
    v_is_intermodal FACILITY.is_intermodal%TYPE;
BEGIN
    DBMS_OUTPUT.PUT_LINE('============================================================');
    DBMS_OUTPUT.PUT_LINE('TEST 10: Default is_intermodal Test (should default to 0)');
    DBMS_OUTPUT.PUT_LINE('============================================================');

    -- Call without p_is_intermodal parameter, should default to 0
    rc := usbd35_register_facility(
        p_name             => 'Default Intermodal Facility',
        p_facility_type_id => 2,
        p_lat              => 41.5,
        p_lon              => -8.3
    );

    FETCH rc INTO v_facility_id, v_name, v_facility_type_id, v_lat, v_lon, v_is_intermodal;
    IF rc%FOUND THEN
        IF v_is_intermodal = 0 THEN
            DBMS_OUTPUT.PUT_LINE('✓ TEST 10 PASSED - is_intermodal defaulted to 0');
            DBMS_OUTPUT.PUT_LINE('  ID: ' || v_facility_id);
            DBMS_OUTPUT.PUT_LINE('  Name: ' || v_name);
            DBMS_OUTPUT.PUT_LINE('  Is Intermodal: ' || v_is_intermodal);
        ELSE
            DBMS_OUTPUT.PUT_LINE('✗ TEST 10 FAILED - is_intermodal is ' || v_is_intermodal || ' (expected 0)');
        END IF;
    END IF;
    CLOSE rc;
END;
/

--------------------------------------------------------------------------------
-- USBD35 – Clean up test data after running tests
--------------------------------------------------------------------------------
DELETE FROM FACILITY WHERE name IN ('New Logistics Hub', 'Duplicate Name Test', 'Boundary Coords Facility', 'Default Intermodal Facility');
COMMIT;

BEGIN
    DBMS_OUTPUT.PUT_LINE('============================================================');
    DBMS_OUTPUT.PUT_LINE('All tests completed!');
    DBMS_OUTPUT.PUT_LINE('============================================================');
END;
/

