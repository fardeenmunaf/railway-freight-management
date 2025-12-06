--------------------------------------------------------------------------------
-- USBD44 – Test 1: Valid Segment Without Siding
--------------------------------------------------------------------------------

SET SERVEROUTPUT ON;

DECLARE
    v_segment_id NUMBER;
BEGIN
    v_segment_id := add_segment_with_siding(
            p_line_id = > 1,
            p_length_km = > 3000,
            p_electrif_type = > 1,
            p_gauge_id = > 1,
            p_max_weight_kg_m = > 8000,
            p_speed_limit_kmh = > 100,
            p_track_number = > 1,
            p_has_siding = > 0,
            p_siding_facility_id = > NULL,
            p_siding_name = > NULL,
            p_siding_capacity_wagons = > NULL
                    );

    DBMS_OUTPUT.PUT_LINE('TEST 1 OK → Segment created without siding. ID = ' || v_segment_id);

    COMMIT;
END;
/
--------------------------------------------------------------------------------
-- USBD44 – Test 2: Valid Segment With Siding
--------------------------------------------------------------------------------

DECLARE
    v_segment_id NUMBER;
BEGIN
    v_segment_id := add_segment_with_siding(
            p_line_id = > 1,
            p_length_km = > 3500,
            p_electrif_type = > 1,
            p_gauge_id = > 1,
            p_max_weight_kg_m = > 8000,
            p_speed_limit_kmh = > 120,
            p_track_number = > 2,
            p_has_siding = > 1,
            p_siding_facility_id = > 5,
            p_siding_name = > 'SIDING_1',
            p_siding_capacity_wagons = > 12
                    );

    DBMS_OUTPUT.PUT_LINE('TEST 2 OK → Segment and siding created. Segment ID = ' || v_segment_id);

    COMMIT;
END;
/
--------------------------------------------------------------------------------
-- USBD44 – Test 3: Missing Siding When Required
--------------------------------------------------------------------------------

BEGIN
    DECLARE
        v_segment_id NUMBER;
    BEGIN
        v_segment_id := add_segment_with_siding(
                p_line_id = > 1,
                p_length_km = > 4000,
                p_electrif_type = > 1,
                p_gauge_id = > 1,
                p_max_weight_kg_m = > 8000,
                p_speed_limit_kmh = > 100,
                p_track_number = > 3,
                p_has_siding = > 1,
                p_siding_facility_id = > NULL,
                p_siding_name = > NULL,
                p_siding_capacity_wagons = > NULL
                        );
    END;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('TEST 3 OK → Expected error for missing siding data: ' || SQLERRM);
END;
/
--------------------------------------------------------------------------------
-- USBD44 – Test 4: Invalid Line Reference
--------------------------------------------------------------------------------

BEGIN
    DECLARE
        v_segment_id NUMBER;
    BEGIN
        v_segment_id := add_segment_with_siding(
                p_line_id = > 9999,
                p_length_km = > 3000,
                p_electrif_type = > 1,
                p_gauge_id = > 1,
                p_max_weight_kg_m = > 8000,
                p_speed_limit_kmh = > 100,
                p_track_number = > 1,
                p_has_siding = > 0
                        );
    END;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('TEST 4 OK → Expected error for invalid line: ' || SQLERRM);
END;
/
--------------------------------------------------------------------------------
-- USBD44 – Test 5: Duplicate Siding Name
--------------------------------------------------------------------------------

BEGIN
    DECLARE
        v_segment_id NUMBER;
    BEGIN
        v_segment_id := add_segment_with_siding(
                p_line_id = > 1,
                p_length_km = > 3600,
                p_electrif_type = > 1,
                p_gauge_id = > 1,
                p_max_weight_kg_m = > 8000,
                p_speed_limit_kmh = > 100,
                p_track_number = > 4,
                p_has_siding = > 1,
                p_siding_facility_id = > 5,
                p_siding_name = > 'SIDING_1',
                p_siding_capacity_wagons = > 10
                        );
    END;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('TEST 5 OK → Expected duplicate siding name error: ' || SQLERRM);
END;
/
--------------------------------------------------------------------------------
-- USBD44 – Test 6: Boundary Attribute Test
--------------------------------------------------------------------------------

DECLARE
    v_seg_min NUMBER;
    v_seg_max NUMBER;
BEGIN
    v_seg_min := add_segment_with_siding(
            p_line_id = > 1,
            p_length_km = > 1,
            p_electrif_type = > 1,
            p_gauge_id = > 1,
            p_max_weight_kg_m = > 6400,
            p_speed_limit_kmh = > 40,
            p_track_number = > 5,
            p_has_siding = > 0
                 );

    v_seg_max := add_segment_with_siding(
            p_line_id = > 1,
            p_length_km = > 50000,
            p_electrif_type = > 1,
            p_gauge_id = > 1,
            p_max_weight_kg_m = > 10000,
            p_speed_limit_kmh = > 200,
            p_track_number = > 6,
            p_has_siding = > 0
                 );

    DBMS_OUTPUT.PUT_LINE(
            'TEST 6 OK → Boundary segments inserted: ' ||
            v_seg_min || ', ' || v_seg_max
    );

    COMMIT;
END;
/
--------------------------------------------------------------------------------
-- USBD44 – Test 7: Data Dictionary Check
--------------------------------------------------------------------------------

PROMPT Checking inserted segments in LINE_SEGMENT:

SELECT segment_id, line_id, position, length_km, track_number
FROM LINE_SEGMENT
WHERE line_id = 1
ORDER BY position;

PROMPT
Checking sidings in PARKING_TRACK:

SELECT *
FROM PARKING_TRACK
WHERE line_id = 1
ORDER BY name;
/
