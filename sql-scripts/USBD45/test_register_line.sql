-- =====================================================
-- USBD45: Test Register New Line with Segment Function
-- =====================================================
-- Purpose: Test all scenarios for register_line_with_segment function
-- Author: Diogo Remelgado
-- Date: 2026-01-01

-- =====================================================
-- TEST 1: Valid Line and Segment Registration (SUCCESS CASE)
-- =====================================================
-- Description: Insert a new line with a valid initial segment
-- Expected: Insert succeeds for both tables atomically
-- =====================================================
DECLARE
    v_result VARCHAR2(500);
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST 1: Valid Line and Segment Registration');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('Input: Line ID=100, Name=''Test North Line'', Owner=1');
    DBMS_OUTPUT.PUT_LINE('       Segment ID=1000, Length=15.5km, Gauge=1');

    v_result := register_line_with_segment(
        p_line_id => 100,
        p_line_name => 'Test North Line',
        p_owner_id => 1,
        p_segment_id => 1000,
        p_length_km => 15.5,
        p_electrif_type => 1,
        p_gauge_id => 1,
        p_max_weight_kg_m => 8000,
        p_speed_limit_kmh => 120,
        p_track_number => 2,
        p_position => 1
    );

    DBMS_OUTPUT.PUT_LINE('Result: ' || v_result);

    -- Verify insertion
    IF v_result LIKE 'SUCCESS%' THEN
        DBMS_OUTPUT.PUT_LINE('TEST PASSED: Line and segment registered successfully');

        -- Display the inserted line
        FOR rec IN (SELECT * FROM LINE WHERE line_id = 100) LOOP
            DBMS_OUTPUT.PUT_LINE('  Line - ID: ' || rec.line_id || ', Name: ' || rec.name || ', Owner: ' || rec.owner_id);
        END LOOP;

        -- Display the inserted segment
        FOR rec IN (SELECT * FROM LINE_SEGMENT WHERE segment_id = 1000) LOOP
            DBMS_OUTPUT.PUT_LINE('  Segment - ID: ' || rec.segment_id || ', Line: ' || rec.line_id || ', Length: ' || rec.length_km || 'km');
        END LOOP;
    ELSE
        DBMS_OUTPUT.PUT_LINE('TEST FAILED: Expected SUCCESS but got error');
    END IF;

    DBMS_OUTPUT.PUT_LINE('');
END;
/

-- =====================================================
-- TEST 2: Missing Segment Data (BUSINESS RULE VIOLATION)
-- =====================================================
-- Description: Attempt to register a line without segment_id
-- Expected: Error about missing segment (enforcing business rule)
-- =====================================================
DECLARE
    v_result VARCHAR2(500);
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST 2: Missing Segment Data Test');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('Input: Line ID=101, Segment ID=NULL');

    v_result := register_line_with_segment(
        p_line_id => 101,
        p_line_name => 'Test Line',
        p_owner_id => 1,
        p_segment_id => NULL,  -- Missing segment data!
        p_length_km => 10.0,
        p_electrif_type => 1,
        p_gauge_id => 1,
        p_max_weight_kg_m => 8000,
        p_speed_limit_kmh => 100,
        p_track_number => 2,
        p_position => 1
    );

    DBMS_OUTPUT.PUT_LINE('Result: ' || v_result);

    IF v_result LIKE 'ERROR%' AND v_result LIKE '%Segment%' AND v_result LIKE '%at least one%' THEN
        DBMS_OUTPUT.PUT_LINE('TEST PASSED: Missing segment correctly rejected');
    ELSE
        DBMS_OUTPUT.PUT_LINE('TEST FAILED: Expected segment mandatory error');
    END IF;

    DBMS_OUTPUT.PUT_LINE('');
END;
/

-- =====================================================
-- TEST 3: Invalid Gauge ID (FOREIGN KEY VIOLATION)
-- =====================================================
-- Description: Insert a valid line but reference a Gauge that does not exist
-- Expected: FK error, Line is not inserted (atomicity preserved)
-- =====================================================
DECLARE
    v_result VARCHAR2(500);
    v_line_count NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST 3: Invalid Gauge ID Test');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('Input: Line ID=102, Gauge ID=9999 (does not exist)');

    v_result := register_line_with_segment(
        p_line_id => 102,
        p_line_name => 'Test Line Invalid Gauge',
        p_owner_id => 1,
        p_segment_id => 1002,
        p_length_km => 10.0,
        p_electrif_type => 1,
        p_gauge_id => 9999,  -- Non-existent gauge!
        p_max_weight_kg_m => 8000,
        p_speed_limit_kmh => 100,
        p_track_number => 2,
        p_position => 1
    );

    DBMS_OUTPUT.PUT_LINE('Result: ' || v_result);

    -- Verify line was NOT inserted (atomicity test)
    SELECT COUNT(*) INTO v_line_count FROM LINE WHERE line_id = 102;

    IF v_result LIKE 'ERROR%' AND v_result LIKE '%Gauge%' AND v_line_count = 0 THEN
        DBMS_OUTPUT.PUT_LINE('TEST PASSED: Invalid gauge rejected, Line not inserted (atomic rollback)');
    ELSE
        DBMS_OUTPUT.PUT_LINE('TEST FAILED: Expected gauge FK error and rollback');
    END IF;

    DBMS_OUTPUT.PUT_LINE('');
END;
/

-- =====================================================
-- TEST 4: Duplicate Line ID (PRIMARY KEY VIOLATION)
-- =====================================================
-- Description: Attempt to insert a line with existing primary key
-- Expected: Error message about duplicate line ID
-- =====================================================
DECLARE
    v_result VARCHAR2(500);
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST 4: Duplicate Line ID Test');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('Input: Line ID=100 (already exists from TEST 1)');

    v_result := register_line_with_segment(
        p_line_id => 100,  -- Duplicate!
        p_line_name => 'Duplicate Line',
        p_owner_id => 1,
        p_segment_id => 1004,
        p_length_km => 10.0,
        p_electrif_type => 1,
        p_gauge_id => 1,
        p_max_weight_kg_m => 8000,
        p_speed_limit_kmh => 100,
        p_track_number => 2,
        p_position => 1
    );

    DBMS_OUTPUT.PUT_LINE('Result: ' || v_result);

    IF v_result LIKE 'ERROR%' AND v_result LIKE '%already exists%' THEN
        DBMS_OUTPUT.PUT_LINE('TEST PASSED: Duplicate line ID correctly rejected');
    ELSE
        DBMS_OUTPUT.PUT_LINE('TEST FAILED: Expected duplicate key error');
    END IF;

    DBMS_OUTPUT.PUT_LINE('');
END;
/

-- =====================================================
-- TEST 5: Atomicity/Rollback Test (NEGATIVE LENGTH)
-- =====================================================
-- Description: Trigger error during segment insertion (negative length)
-- Expected: Verify that Line was not created (clean rollback)
-- =====================================================
DECLARE
    v_result VARCHAR2(500);
    v_line_count NUMBER;
    v_segment_count NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST 5: Atomicity/Rollback Test');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('Input: Line ID=105, Segment length=-10 (invalid)');

    v_result := register_line_with_segment(
        p_line_id => 105,
        p_line_name => 'Test Rollback Line',
        p_owner_id => 1,
        p_segment_id => 1005,
        p_length_km => -10,  -- Invalid negative length!
        p_electrif_type => 1,
        p_gauge_id => 1,
        p_max_weight_kg_m => 8000,
        p_speed_limit_kmh => 100,
        p_track_number => 2,
        p_position => 1
    );

    DBMS_OUTPUT.PUT_LINE('Result: ' || v_result);

    -- Verify atomicity: neither Line nor Segment should exist
    SELECT COUNT(*) INTO v_line_count FROM LINE WHERE line_id = 105;
    SELECT COUNT(*) INTO v_segment_count FROM LINE_SEGMENT WHERE segment_id = 1005;

    IF v_result LIKE 'ERROR%' AND v_line_count = 0 AND v_segment_count = 0 THEN
        DBMS_OUTPUT.PUT_LINE('TEST PASSED: Transaction rolled back atomically (Line not created)');
    ELSE
        DBMS_OUTPUT.PUT_LINE('TEST FAILED: Expected rollback - Line count: ' || v_line_count || ', Segment count: ' || v_segment_count);
    END IF;

    DBMS_OUTPUT.PUT_LINE('');
END;
/

-- =====================================================
-- TEST 6: NULL Line ID (VALIDATION ERROR)
-- =====================================================
-- Description: Attempt to insert a line with NULL line_id
-- Expected: Error message about NULL line_id
-- =====================================================
DECLARE
    v_result VARCHAR2(500);
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST 6: NULL Line ID Test');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('Input: Line ID=NULL');

    v_result := register_line_with_segment(
        p_line_id => NULL,
        p_line_name => 'Test Line',
        p_owner_id => 1,
        p_segment_id => 1006,
        p_length_km => 10.0,
        p_electrif_type => 1,
        p_gauge_id => 1,
        p_max_weight_kg_m => 8000,
        p_speed_limit_kmh => 100,
        p_track_number => 2,
        p_position => 1
    );

    DBMS_OUTPUT.PUT_LINE('Result: ' || v_result);

    IF v_result LIKE 'ERROR%' AND v_result LIKE '%Line ID%' AND v_result LIKE '%NULL%' THEN
        DBMS_OUTPUT.PUT_LINE('TEST PASSED: NULL line_id correctly rejected');
    ELSE
        DBMS_OUTPUT.PUT_LINE('TEST FAILED: Expected NULL line_id error');
    END IF;

    DBMS_OUTPUT.PUT_LINE('');
END;
/

-- =====================================================
-- TEST 7: NULL Line Name (VALIDATION ERROR)
-- =====================================================
-- Description: Attempt to insert a line with NULL name
-- Expected: Error message about NULL name
-- =====================================================
DECLARE
    v_result VARCHAR2(500);
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST 7: NULL Line Name Test');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('Input: Line Name=NULL');

    v_result := register_line_with_segment(
        p_line_id => 107,
        p_line_name => NULL,
        p_owner_id => 1,
        p_segment_id => 1007,
        p_length_km => 10.0,
        p_electrif_type => 1,
        p_gauge_id => 1,
        p_max_weight_kg_m => 8000,
        p_speed_limit_kmh => 100,
        p_track_number => 2,
        p_position => 1
    );

    DBMS_OUTPUT.PUT_LINE('Result: ' || v_result);

    IF v_result LIKE 'ERROR%' AND v_result LIKE '%name%' AND v_result LIKE '%NULL%' THEN
        DBMS_OUTPUT.PUT_LINE('TEST PASSED: NULL line name correctly rejected');
    ELSE
        DBMS_OUTPUT.PUT_LINE('TEST FAILED: Expected NULL name error');
    END IF;

    DBMS_OUTPUT.PUT_LINE('');
END;
/

-- =====================================================
-- TEST 8: NULL Owner ID (VALIDATION ERROR)
-- =====================================================
-- Description: Attempt to insert a line with NULL owner_id
-- Expected: Error message about NULL owner_id
-- =====================================================
DECLARE
    v_result VARCHAR2(500);
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST 8: NULL Owner ID Test');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('Input: Owner ID=NULL');

    v_result := register_line_with_segment(
        p_line_id => 108,
        p_line_name => 'Test Line',
        p_owner_id => NULL,
        p_segment_id => 1008,
        p_length_km => 10.0,
        p_electrif_type => 1,
        p_gauge_id => 1,
        p_max_weight_kg_m => 8000,
        p_speed_limit_kmh => 100,
        p_track_number => 2,
        p_position => 1
    );

    DBMS_OUTPUT.PUT_LINE('Result: ' || v_result);

    IF v_result LIKE 'ERROR%' AND v_result LIKE '%Owner ID%' AND v_result LIKE '%NULL%' THEN
        DBMS_OUTPUT.PUT_LINE('TEST PASSED: NULL owner_id correctly rejected');
    ELSE
        DBMS_OUTPUT.PUT_LINE('TEST FAILED: Expected NULL owner_id error');
    END IF;

    DBMS_OUTPUT.PUT_LINE('');
END;
/

-- =====================================================
-- TEST 9: Non-existent Owner ID (FOREIGN KEY VALIDATION)
-- =====================================================
-- Description: Attempt to insert a line with non-existent owner_id
-- Expected: Error message about owner not existing
-- =====================================================
DECLARE
    v_result VARCHAR2(500);
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST 9: Non-existent Owner ID Test');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('Input: Owner ID=9999 (does not exist)');

    v_result := register_line_with_segment(
        p_line_id => 109,
        p_line_name => 'Test Line',
        p_owner_id => 9999,
        p_segment_id => 1009,
        p_length_km => 10.0,
        p_electrif_type => 1,
        p_gauge_id => 1,
        p_max_weight_kg_m => 8000,
        p_speed_limit_kmh => 100,
        p_track_number => 2,
        p_position => 1
    );

    DBMS_OUTPUT.PUT_LINE('Result: ' || v_result);

    IF v_result LIKE 'ERROR%' AND v_result LIKE '%does not exist%' THEN
        DBMS_OUTPUT.PUT_LINE('TEST PASSED: Non-existent owner correctly rejected');
    ELSE
        DBMS_OUTPUT.PUT_LINE('TEST FAILED: Expected non-existent owner error');
    END IF;

    DBMS_OUTPUT.PUT_LINE('');
END;
/

-- =====================================================
-- TEST 10: NULL Segment Length (VALIDATION ERROR)
-- =====================================================
-- Description: Attempt to insert segment with NULL length
-- Expected: Error message about NULL length
-- =====================================================
DECLARE
    v_result VARCHAR2(500);
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST 10: NULL Segment Length Test');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('Input: Segment length=NULL');

    v_result := register_line_with_segment(
        p_line_id => 110,
        p_line_name => 'Test Line',
        p_owner_id => 1,
        p_segment_id => 1010,
        p_length_km => NULL,
        p_electrif_type => 1,
        p_gauge_id => 1,
        p_max_weight_kg_m => 8000,
        p_speed_limit_kmh => 100,
        p_track_number => 2,
        p_position => 1
    );

    DBMS_OUTPUT.PUT_LINE('Result: ' || v_result);

    IF v_result LIKE 'ERROR%' AND v_result LIKE '%length%' AND v_result LIKE '%NULL%' THEN
        DBMS_OUTPUT.PUT_LINE('TEST PASSED: NULL segment length correctly rejected');
    ELSE
        DBMS_OUTPUT.PUT_LINE('TEST FAILED: Expected NULL length error');
    END IF;

    DBMS_OUTPUT.PUT_LINE('');
END;
/

-- =====================================================
-- TEST 11: Zero Segment Length (VALIDATION ERROR)
-- =====================================================
-- Description: Attempt to insert segment with zero length
-- Expected: Error message about positive length required
-- =====================================================
DECLARE
    v_result VARCHAR2(500);
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST 11: Zero Segment Length Test');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('Input: Segment length=0');

    v_result := register_line_with_segment(
        p_line_id => 111,
        p_line_name => 'Test Line',
        p_owner_id => 1,
        p_segment_id => 1011,
        p_length_km => 0,
        p_electrif_type => 1,
        p_gauge_id => 1,
        p_max_weight_kg_m => 8000,
        p_speed_limit_kmh => 100,
        p_track_number => 2,
        p_position => 1
    );

    DBMS_OUTPUT.PUT_LINE('Result: ' || v_result);

    IF v_result LIKE 'ERROR%' AND v_result LIKE '%positive%' THEN
        DBMS_OUTPUT.PUT_LINE('TEST PASSED: Zero segment length correctly rejected');
    ELSE
        DBMS_OUTPUT.PUT_LINE('TEST FAILED: Expected positive length error');
    END IF;

    DBMS_OUTPUT.PUT_LINE('');
END;
/

-- =====================================================
-- TEST 12: Second Valid Line and Segment (SUCCESS CASE)
-- =====================================================
-- Description: Insert a second valid line with segment
-- Expected: Insert succeeds for both tables
-- =====================================================
DECLARE
    v_result VARCHAR2(500);
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST 12: Second Valid Line and Segment Registration');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('Input: Line ID=112, Name=''Test South Line'', Owner=2');

    v_result := register_line_with_segment(
        p_line_id => 112,
        p_line_name => 'Test South Line',
        p_owner_id => 2,
        p_segment_id => 1012,
        p_length_km => 25.3,
        p_electrif_type => 1,
        p_gauge_id => 2,
        p_max_weight_kg_m => 7000,
        p_speed_limit_kmh => 100,
        p_track_number => 1,
        p_position => 1
    );

    DBMS_OUTPUT.PUT_LINE('Result: ' || v_result);

    IF v_result LIKE 'SUCCESS%' THEN
        DBMS_OUTPUT.PUT_LINE('TEST PASSED: Second line and segment registered successfully');

        FOR rec IN (SELECT * FROM LINE WHERE line_id = 112) LOOP
            DBMS_OUTPUT.PUT_LINE('  Line - ID: ' || rec.line_id || ', Name: ' || rec.name);
        END LOOP;

        FOR rec IN (SELECT * FROM LINE_SEGMENT WHERE segment_id = 1012) LOOP
            DBMS_OUTPUT.PUT_LINE('  Segment - ID: ' || rec.segment_id || ', Length: ' || rec.length_km || 'km');
        END LOOP;
    ELSE
        DBMS_OUTPUT.PUT_LINE('TEST FAILED: Expected SUCCESS but got error');
    END IF;

    DBMS_OUTPUT.PUT_LINE('');
END;
/

-- =====================================================
-- TEST 13: Duplicate Segment ID (PRIMARY KEY VIOLATION)
-- =====================================================
-- Description: Attempt to insert segment with existing segment_id
-- Expected: Error message about duplicate segment ID
-- =====================================================
DECLARE
    v_result VARCHAR2(500);
    v_line_count NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST 13: Duplicate Segment ID Test');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('Input: Line ID=113, Segment ID=1000 (already exists)');

    v_result := register_line_with_segment(
        p_line_id => 113,
        p_line_name => 'Test Line Duplicate Segment',
        p_owner_id => 1,
        p_segment_id => 1000,  -- Duplicate segment ID!
        p_length_km => 10.0,
        p_electrif_type => 1,
        p_gauge_id => 1,
        p_max_weight_kg_m => 8000,
        p_speed_limit_kmh => 100,
        p_track_number => 2,
        p_position => 1
    );

    DBMS_OUTPUT.PUT_LINE('Result: ' || v_result);

    -- Verify line was NOT inserted (atomicity)
    SELECT COUNT(*) INTO v_line_count FROM LINE WHERE line_id = 113;

    IF v_result LIKE 'ERROR%' AND v_line_count = 0 THEN
        DBMS_OUTPUT.PUT_LINE('TEST PASSED: Duplicate segment ID rejected, Line not inserted');
    ELSE
        DBMS_OUTPUT.PUT_LINE('TEST FAILED: Expected duplicate segment error');
    END IF;

    DBMS_OUTPUT.PUT_LINE('');
END;
/

-- =====================================================
-- DATA DICTIONARY CHECK
-- =====================================================
-- Display all test lines and segments in the database after tests
-- =====================================================
DECLARE
    v_line_count NUMBER;
    v_segment_count NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('DATA DICTIONARY CHECK: Test Data');
    DBMS_OUTPUT.PUT_LINE('==========================================');

    SELECT COUNT(*) INTO v_line_count FROM LINE WHERE line_id >= 100;
    SELECT COUNT(*) INTO v_segment_count FROM LINE_SEGMENT WHERE segment_id >= 1000;

    DBMS_OUTPUT.PUT_LINE('Total test lines: ' || v_line_count);
    DBMS_OUTPUT.PUT_LINE('Total test segments: ' || v_segment_count);
    DBMS_OUTPUT.PUT_LINE('');
    DBMS_OUTPUT.PUT_LINE('Test Lines:');
    DBMS_OUTPUT.PUT_LINE('------------------------------------------');

    FOR rec IN (SELECT * FROM LINE WHERE line_id >= 100 ORDER BY line_id) LOOP
        DBMS_OUTPUT.PUT_LINE('ID: ' || RPAD(rec.line_id, 5) ||
                            ' | Name: ' || RPAD(rec.name, 25) ||
                            ' | Owner: ' || rec.owner_id);
    END LOOP;

    DBMS_OUTPUT.PUT_LINE('');
    DBMS_OUTPUT.PUT_LINE('Test Segments:');
    DBMS_OUTPUT.PUT_LINE('------------------------------------------');

    FOR rec IN (SELECT * FROM LINE_SEGMENT WHERE segment_id >= 1000 ORDER BY segment_id) LOOP
        DBMS_OUTPUT.PUT_LINE('ID: ' || RPAD(rec.segment_id, 5) ||
                            ' | Line: ' || RPAD(rec.line_id, 5) ||
                            ' | Length: ' || rec.length_km || 'km' ||
                            ' | Gauge: ' || rec.gauge_id);
    END LOOP;

    DBMS_OUTPUT.PUT_LINE('==========================================');
END;
/

-- =====================================================
-- TEST SUMMARY
-- =====================================================
DECLARE
    v_total_tests NUMBER := 13;
BEGIN
    DBMS_OUTPUT.PUT_LINE('');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST SUITE COMPLETE');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('Total tests executed: ' || v_total_tests);
    DBMS_OUTPUT.PUT_LINE('');
    DBMS_OUTPUT.PUT_LINE('Tests validate:');
    DBMS_OUTPUT.PUT_LINE('  - Atomic insertion of Line + Segment');
    DBMS_OUTPUT.PUT_LINE('  - Business rule: Line must have at least one segment');
    DBMS_OUTPUT.PUT_LINE('  - Foreign key validations (Owner, Gauge)');
    DBMS_OUTPUT.PUT_LINE('  - Rollback on error (atomicity)');
    DBMS_OUTPUT.PUT_LINE('  - All input validations');
    DBMS_OUTPUT.PUT_LINE('==========================================');
END;
/

