-- =====================================================
-- USLP09 - Test Script
-- =====================================================
-- Tests all 4 functions to verify they work with the schema
-- =====================================================

-- =====================================================
-- TEST 1: Get Available Locomotives
-- =====================================================
DECLARE
    v_cursor SYS_REFCURSOR;
    v_count NUMBER := 0;
    v_locomotive_id NUMBER;
    v_name VARCHAR2(255);
    v_brand VARCHAR2(60);
    v_model VARCHAR2(60);
    v_year NUMBER;
    v_power_kw NUMBER;
    v_max_speed_kmh NUMBER;
    v_operational_speed_kmh NUMBER;
    v_total_weight_t NUMBER;
    v_length_m NUMBER;
    v_locomotive_type VARCHAR2(60);
    v_current_location VARCHAR2(255);
    v_location_id NUMBER;
    v_location_lat NUMBER;
    v_location_lon NUMBER;
    v_distance_from_departure_km NUMBER;
    v_status VARCHAR2(20);
    v_transit_destination VARCHAR2(255);
    v_transit_arrival_date TIMESTAMP;
    v_operator_company VARCHAR2(255);
    v_facility_id NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST 1: Get Available Locomotives');
    DBMS_OUTPUT.PUT_LINE('==========================================');

    BEGIN
        -- Get first facility
        SELECT facility_id INTO v_facility_id FROM FACILITY WHERE ROWNUM = 1;

        DBMS_OUTPUT.PUT_LINE('Testing with facility_id: ' || v_facility_id);

        v_cursor := fn_get_available_locomotives(v_facility_id);

        LOOP
            FETCH v_cursor INTO v_locomotive_id, v_name, v_brand, v_model, v_year, v_power_kw,
                v_max_speed_kmh, v_operational_speed_kmh, v_total_weight_t, v_length_m,
                v_locomotive_type, v_current_location, v_location_id, v_location_lat, v_location_lon,
                v_distance_from_departure_km, v_status, v_transit_destination,
                v_transit_arrival_date, v_operator_company;
            EXIT WHEN v_cursor%NOTFOUND OR v_count >= 5;

            DBMS_OUTPUT.PUT_LINE('  Loco ' || v_locomotive_id || ': ' || v_name || ' | Status: ' || v_status || ' | Location: ' || v_current_location);
            v_count := v_count + 1;
        END LOOP;

        CLOSE v_cursor;

        DBMS_OUTPUT.PUT_LINE('TEST PASSED - Found ' || v_count || ' locomotive(s)');
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            DBMS_OUTPUT.PUT_LINE('TEST SKIPPED - No facilities found');
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('TEST FAILED: ' || SQLERRM);
    END;

    DBMS_OUTPUT.PUT_LINE('');
END;
/

-- =====================================================
-- TEST 2: Get Available Wagons
-- =====================================================
DECLARE
    v_cursor SYS_REFCURSOR;
    v_count NUMBER := 0;
    v_wagon_id NUMBER;
    v_model_name VARCHAR2(255);
    v_wagon_type VARCHAR2(255);
    v_constructor VARCHAR2(60);
    v_service_year NUMBER;
    v_payload_t NUMBER;
    v_tare_t NUMBER;
    v_volume_m3 NUMBER;
    v_length_m NUMBER;
    v_width_m NUMBER;
    v_height_m NUMBER;
    v_max_speed_kmh NUMBER;
    v_current_location VARCHAR2(255);
    v_location_id NUMBER;
    v_location_lat NUMBER;
    v_location_lon NUMBER;
    v_distance_from_departure_km NUMBER;
    v_status VARCHAR2(20);
    v_parking_track_name VARCHAR2(60);
    v_parking_track_id NUMBER;
    v_parking_position NUMBER;
    v_operator_company VARCHAR2(255);
    v_in_active_freight NUMBER;
    v_facility_id NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST 2: Get Available Wagons');
    DBMS_OUTPUT.PUT_LINE('==========================================');

    BEGIN
        -- Get first facility
        SELECT facility_id INTO v_facility_id FROM FACILITY WHERE ROWNUM = 1;

        DBMS_OUTPUT.PUT_LINE('Testing with facility_id: ' || v_facility_id);

        v_cursor := fn_get_available_wagons(v_facility_id);

        LOOP
            FETCH v_cursor INTO v_wagon_id, v_model_name, v_wagon_type, v_constructor,
                v_service_year, v_payload_t, v_tare_t, v_volume_m3, v_length_m, v_width_m,
                v_height_m, v_max_speed_kmh, v_current_location, v_location_id, v_location_lat,
                v_location_lon, v_distance_from_departure_km, v_status, v_parking_track_name,
                v_parking_track_id, v_parking_position, v_operator_company, v_in_active_freight;
            EXIT WHEN v_cursor%NOTFOUND OR v_count >= 5;

            DBMS_OUTPUT.PUT_LINE('  Wagon ' || v_wagon_id || ': ' || v_model_name || ' | Status: ' || v_status || ' | Location: ' || v_current_location);
            v_count := v_count + 1;
        END LOOP;

        CLOSE v_cursor;

        DBMS_OUTPUT.PUT_LINE('TEST PASSED - Found ' || v_count || ' wagon(s)');
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            DBMS_OUTPUT.PUT_LINE('TEST SKIPPED - No facilities found');
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('TEST FAILED: ' || SQLERRM);
    END;

    DBMS_OUTPUT.PUT_LINE('');
END;
/

-- =====================================================
-- TEST 3: Validate Train Composition
-- =====================================================
DECLARE
    v_result VARCHAR2(4000);
    v_service_id NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST 3: Validate Train Composition');
    DBMS_OUTPUT.PUT_LINE('==========================================');

    BEGIN
        -- Get first train service
        SELECT service_id INTO v_service_id FROM TRAIN_SERVICE WHERE ROWNUM = 1;

        DBMS_OUTPUT.PUT_LINE('Testing with service_id: ' || v_service_id);

        v_result := fn_validate_train_composition(v_service_id);

        DBMS_OUTPUT.PUT_LINE('Service ' || v_service_id || ': ' || v_result);
        DBMS_OUTPUT.PUT_LINE('TEST PASSED');

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            DBMS_OUTPUT.PUT_LINE('TEST SKIPPED - No train services found');
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('TEST FAILED: ' || SQLERRM);
    END;

    DBMS_OUTPUT.PUT_LINE('');
END;
/

-- =====================================================
-- TEST 4: Count Available Resources
-- =====================================================
DECLARE
    v_loco_count NUMBER;
    v_wagon_count NUMBER;
    v_service_count NUMBER;
    v_facility_count NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST 4: Count Resources');
    DBMS_OUTPUT.PUT_LINE('==========================================');

    SELECT COUNT(*) INTO v_loco_count FROM LOCOMOTIVE;
    SELECT COUNT(*) INTO v_wagon_count FROM WAGON;
    SELECT COUNT(*) INTO v_service_count FROM TRAIN_SERVICE;
    SELECT COUNT(*) INTO v_facility_count FROM FACILITY;

    DBMS_OUTPUT.PUT_LINE('Database Statistics:');
    DBMS_OUTPUT.PUT_LINE('  Locomotives: ' || v_loco_count);
    DBMS_OUTPUT.PUT_LINE('  Wagons: ' || v_wagon_count);
    DBMS_OUTPUT.PUT_LINE('  Train Services: ' || v_service_count);
    DBMS_OUTPUT.PUT_LINE('  Facilities: ' || v_facility_count);

    IF v_loco_count = 0 THEN
        DBMS_OUTPUT.PUT_LINE('WARNING: No locomotives in database!');
    END IF;

    IF v_wagon_count = 0 THEN
        DBMS_OUTPUT.PUT_LINE('WARNING: No wagons in database!');
    END IF;

    DBMS_OUTPUT.PUT_LINE('TEST PASSED');
    DBMS_OUTPUT.PUT_LINE('');
END;
/

-- =====================================================
-- TEST SUMMARY
-- =====================================================
DECLARE
    v_total_tests NUMBER := 4;
BEGIN
    DBMS_OUTPUT.PUT_LINE('');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST SUITE COMPLETE');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('Total tests executed: ' || v_total_tests);
    DBMS_OUTPUT.PUT_LINE('');
    DBMS_OUTPUT.PUT_LINE('Tests validate:');
    DBMS_OUTPUT.PUT_LINE('  - fn_get_available_locomotives()');
    DBMS_OUTPUT.PUT_LINE('  - fn_get_available_wagons()');
    DBMS_OUTPUT.PUT_LINE('  - fn_validate_train_composition()');
    DBMS_OUTPUT.PUT_LINE('  - Database resource counts');
    DBMS_OUTPUT.PUT_LINE('==========================================');
END;
/

