-- =====================================================
-- USBD38: Test Register New Gauge Function
-- =====================================================
-- Purpose: Test all scenarios for register_gauge function
-- Author: Diogo Remelgado
-- Date: 2026-01-01
-- =====================================================
-- Test Strategy: All tests are independent and demonstrate
-- both success and failure scenarios without using SYSDATE
-- =====================================================


-- =====================================================
-- TEST 1: Valid Gauge Registration (SUCCESS CASE)
-- =====================================================
-- Description: Insert a new gauge with valid attributes
-- Expected: Insert succeeds with success message
-- =====================================================
DECLARE
    v_result VARCHAR2(500);
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST 1: Valid Gauge Registration');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('Input: ID=100, Name=''Generic Standard'', Size=1435mm');

    v_result := register_gauge(
        p_gauge_id => 100,
        p_name => 'Generic Standard',
        p_size_mm => 1435
    );

    DBMS_OUTPUT.PUT_LINE('Result: ' || v_result);

    -- Verify insertion
    IF v_result LIKE 'SUCCESS%' THEN
        DBMS_OUTPUT.PUT_LINE('✓ TEST PASSED: Gauge registered successfully');

        -- Display the inserted data
        FOR rec IN (SELECT * FROM GAUGE WHERE gauge_id = 100) LOOP
            DBMS_OUTPUT.PUT_LINE('  - Gauge ID: ' || rec.gauge_id);
            DBMS_OUTPUT.PUT_LINE('  - Name: ' || rec.name);
            DBMS_OUTPUT.PUT_LINE('  - Size: ' || rec.size_mm || 'mm');
        END LOOP;
    ELSE
        DBMS_OUTPUT.PUT_LINE('✗ TEST FAILED: Expected SUCCESS but got error');
    END IF;

    DBMS_OUTPUT.PUT_LINE('');
END;
/

-- =====================================================
-- TEST 2: Duplicate Gauge ID (PRIMARY KEY VIOLATION)
-- =====================================================
-- Description: Attempt to insert a gauge with existing primary key
-- Expected: Error message about duplicate primary key
-- =====================================================
DECLARE
    v_result VARCHAR2(500);
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST 2: Duplicate Gauge ID Test');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('Input: ID=100 (already exists), Name=''Duplicate Test'', Size=1520mm');

    v_result := register_gauge(
        p_gauge_id => 100,
        p_name => 'Duplicate Test',
        p_size_mm => 1520
    );

    DBMS_OUTPUT.PUT_LINE('Result: ' || v_result);

    IF v_result LIKE 'ERROR%' AND (v_result LIKE '%already exists%' OR v_result LIKE '%Duplicate%') THEN
        DBMS_OUTPUT.PUT_LINE('✓ TEST PASSED: Duplicate key correctly rejected');
    ELSE
        DBMS_OUTPUT.PUT_LINE('✗ TEST FAILED: Expected duplicate key error');
    END IF;

    DBMS_OUTPUT.PUT_LINE('');
END;
/

-- =====================================================
-- TEST 3: Duplicate Gauge Name and Size
-- =====================================================
-- Description: Attempt to insert a gauge with same name and size
-- Expected: Error message about duplicate gauge
-- =====================================================
DECLARE
    v_result VARCHAR2(500);
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST 3: Duplicate Gauge Name and Size');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('Input: ID=101, Name=''Generic Standard'', Size=1435mm (name+size exists)');

    v_result := register_gauge(
        p_gauge_id => 101,
        p_name => 'Generic Standard',
        p_size_mm => 1435
    );

    DBMS_OUTPUT.PUT_LINE('Result: ' || v_result);

    IF v_result LIKE 'ERROR%' AND v_result LIKE '%already exists%' THEN
        DBMS_OUTPUT.PUT_LINE('✓ TEST PASSED: Duplicate gauge correctly rejected');
    ELSE
        DBMS_OUTPUT.PUT_LINE('✗ TEST FAILED: Expected duplicate gauge error');
    END IF;

    DBMS_OUTPUT.PUT_LINE('');
END;
/

-- =====================================================
-- TEST 4: NULL Gauge ID (MISSING REQUIRED FIELD)
-- =====================================================
-- Description: Attempt to insert without gauge_id
-- Expected: Error message about NULL gauge_id
-- =====================================================
DECLARE
    v_result VARCHAR2(500);
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST 4: NULL Gauge ID Test');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('Input: ID=NULL, Name=''Test Gauge'', Size=1000mm');

    v_result := register_gauge(
        p_gauge_id => NULL,
        p_name => 'Test Gauge',
        p_size_mm => 1000
    );

    DBMS_OUTPUT.PUT_LINE('Result: ' || v_result);

    IF v_result LIKE 'ERROR%' AND v_result LIKE '%Gauge ID%' AND v_result LIKE '%NULL%' THEN
        DBMS_OUTPUT.PUT_LINE('✓ TEST PASSED: NULL gauge_id correctly rejected');
    ELSE
        DBMS_OUTPUT.PUT_LINE('✗ TEST FAILED: Expected NULL gauge_id error');
    END IF;

    DBMS_OUTPUT.PUT_LINE('');
END;
/

-- =====================================================
-- TEST 5: NULL Gauge Name (MISSING REQUIRED FIELD)
-- =====================================================
-- Description: Attempt to insert without gauge name
-- Expected: Error message about NULL name
-- =====================================================
DECLARE
    v_result VARCHAR2(500);
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST 5: NULL Gauge Name Test');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('Input: ID=102, Name=NULL, Size=1435mm');

    v_result := register_gauge(
        p_gauge_id => 102,
        p_name => NULL,
        p_size_mm => 1435
    );

    DBMS_OUTPUT.PUT_LINE('Result: ' || v_result);

    IF v_result LIKE 'ERROR%' AND v_result LIKE '%name%' AND v_result LIKE '%NULL%' THEN
        DBMS_OUTPUT.PUT_LINE('✓ TEST PASSED: NULL name correctly rejected');
    ELSE
        DBMS_OUTPUT.PUT_LINE('✗ TEST FAILED: Expected NULL name error');
    END IF;

    DBMS_OUTPUT.PUT_LINE('');
END;
/

-- =====================================================
-- TEST 6: Empty Gauge Name (MISSING REQUIRED FIELD)
-- =====================================================
-- Description: Attempt to insert with empty string name
-- Expected: Error message about empty name
-- =====================================================
DECLARE
    v_result VARCHAR2(500);
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST 6: Empty Gauge Name Test');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('Input: ID=103, Name='' '' (empty), Size=1435mm');

    v_result := register_gauge(
        p_gauge_id => 103,
        p_name => '   ',
        p_size_mm => 1435
    );

    DBMS_OUTPUT.PUT_LINE('Result: ' || v_result);

    IF v_result LIKE 'ERROR%' AND v_result LIKE '%name%' AND (v_result LIKE '%NULL%' OR v_result LIKE '%empty%') THEN
        DBMS_OUTPUT.PUT_LINE('✓ TEST PASSED: Empty name correctly rejected');
    ELSE
        DBMS_OUTPUT.PUT_LINE('✗ TEST FAILED: Expected empty name error');
    END IF;

    DBMS_OUTPUT.PUT_LINE('');
END;
/

-- =====================================================
-- TEST 7: NULL Gauge Size (MISSING REQUIRED FIELD)
-- =====================================================
-- Description: Attempt to insert without gauge size
-- Expected: Error message about NULL size
-- =====================================================
DECLARE
    v_result VARCHAR2(500);
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST 7: NULL Gauge Size Test');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('Input: ID=104, Name=''Test Gauge'', Size=NULL');

    v_result := register_gauge(
        p_gauge_id => 104,
        p_name => 'Test Gauge',
        p_size_mm => NULL
    );

    DBMS_OUTPUT.PUT_LINE('Result: ' || v_result);

    IF v_result LIKE 'ERROR%' AND v_result LIKE '%size%' AND v_result LIKE '%NULL%' THEN
        DBMS_OUTPUT.PUT_LINE('✓ TEST PASSED: NULL size correctly rejected');
    ELSE
        DBMS_OUTPUT.PUT_LINE('✗ TEST FAILED: Expected NULL size error');
    END IF;

    DBMS_OUTPUT.PUT_LINE('');
END;
/

-- =====================================================
-- TEST 8: Negative Gauge Size (INVALID DATA)
-- =====================================================
-- Description: Attempt to insert with negative width
-- Expected: Error message about invalid size
-- =====================================================
DECLARE
    v_result VARCHAR2(500);
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST 8: Negative Gauge Size Test');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('Input: ID=105, Name=''Negative Test'', Size=-100mm');

    v_result := register_gauge(
        p_gauge_id => 105,
        p_name => 'Negative Test',
        p_size_mm => -100
    );

    DBMS_OUTPUT.PUT_LINE('Result: ' || v_result);

    IF v_result LIKE 'ERROR%' AND v_result LIKE '%positive%' THEN
        DBMS_OUTPUT.PUT_LINE('✓ TEST PASSED: Negative size correctly rejected');
    ELSE
        DBMS_OUTPUT.PUT_LINE('✗ TEST FAILED: Expected negative size error');
    END IF;

    DBMS_OUTPUT.PUT_LINE('');
END;
/

-- =====================================================
-- TEST 9: Zero Gauge Size (INVALID DATA)
-- =====================================================
-- Description: Attempt to insert with zero width
-- Expected: Error message about invalid size
-- =====================================================
DECLARE
    v_result VARCHAR2(500);
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST 9: Zero Gauge Size Test');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('Input: ID=106, Name=''Zero Test'', Size=0mm');

    v_result := register_gauge(
        p_gauge_id => 106,
        p_name => 'Zero Test',
        p_size_mm => 0
    );

    DBMS_OUTPUT.PUT_LINE('Result: ' || v_result);

    IF v_result LIKE 'ERROR%' AND v_result LIKE '%positive%' THEN
        DBMS_OUTPUT.PUT_LINE('✓ TEST PASSED: Zero size correctly rejected');
    ELSE
        DBMS_OUTPUT.PUT_LINE('✗ TEST FAILED: Expected zero size error');
    END IF;

    DBMS_OUTPUT.PUT_LINE('');
END;
/

-- =====================================================
-- TEST 10: Unreasonably Large Gauge Size (INVALID DATA)
-- =====================================================
-- Description: Attempt to insert with unrealistic width
-- Expected: Error message about size exceeding maximum
-- =====================================================
DECLARE
    v_result VARCHAR2(500);
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST 10: Unreasonably Large Size Test');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('Input: ID=107, Name=''Too Large'', Size=5000mm');

    v_result := register_gauge(
        p_gauge_id => 107,
        p_name => 'Too Large',
        p_size_mm => 5000
    );

    DBMS_OUTPUT.PUT_LINE('Result: ' || v_result);

    IF v_result LIKE 'ERROR%' AND v_result LIKE '%maximum%' THEN
        DBMS_OUTPUT.PUT_LINE('✓ TEST PASSED: Excessive size correctly rejected');
    ELSE
        DBMS_OUTPUT.PUT_LINE('✗ TEST FAILED: Expected excessive size error');
    END IF;

    DBMS_OUTPUT.PUT_LINE('');
END;
/

-- =====================================================
-- TEST 11: Another Valid Gauge Registration (SUCCESS)
-- =====================================================
-- Description: Insert another valid gauge to ensure function still works
-- Expected: Insert succeeds
-- =====================================================
DECLARE
    v_result VARCHAR2(500);
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST 11: Second Valid Gauge Registration');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('Input: ID=108, Name=''Cape Gauge'', Size=1067mm');

    v_result := register_gauge(
        p_gauge_id => 108,
        p_name => 'Cape Gauge',
        p_size_mm => 1067
    );

    DBMS_OUTPUT.PUT_LINE('Result: ' || v_result);

    IF v_result LIKE 'SUCCESS%' THEN
        DBMS_OUTPUT.PUT_LINE('✓ TEST PASSED: Second gauge registered successfully');

        -- Display the inserted data
        FOR rec IN (SELECT * FROM GAUGE WHERE gauge_id = 108) LOOP
            DBMS_OUTPUT.PUT_LINE('  - Gauge ID: ' || rec.gauge_id);
            DBMS_OUTPUT.PUT_LINE('  - Name: ' || rec.name);
            DBMS_OUTPUT.PUT_LINE('  - Size: ' || rec.size_mm || 'mm');
        END LOOP;
    ELSE
        DBMS_OUTPUT.PUT_LINE('✗ TEST FAILED: Expected SUCCESS but got error');
    END IF;

    DBMS_OUTPUT.PUT_LINE('');
END;
/

-- =====================================================
-- TEST 12: Existing Gauge from Initial Data (DUPLICATE)
-- =====================================================
-- Description: Attempt to insert a gauge that exists in initial data
-- Expected: Error message about duplicate
-- =====================================================
DECLARE
    v_result VARCHAR2(500);
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST 12: Duplicate Existing Gauge Test');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('Input: ID=1 (Iberian Gauge from initial data)');

    v_result := register_gauge(
        p_gauge_id => 1,
        p_name => 'Test Duplicate',
        p_size_mm => 1600
    );

    DBMS_OUTPUT.PUT_LINE('Result: ' || v_result);

    IF v_result LIKE 'ERROR%' AND (v_result LIKE '%already exists%' OR v_result LIKE '%Duplicate%') THEN
        DBMS_OUTPUT.PUT_LINE('✓ TEST PASSED: Existing gauge ID correctly rejected');
    ELSE
        DBMS_OUTPUT.PUT_LINE('✗ TEST FAILED: Expected duplicate key error');
    END IF;

    DBMS_OUTPUT.PUT_LINE('');
END;
/

-- =====================================================
-- DATA DICTIONARY CHECK
-- =====================================================
-- Display all gauges in the database after tests
-- =====================================================
DECLARE
    v_count NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('DATA DICTIONARY CHECK: All Gauges');
    DBMS_OUTPUT.PUT_LINE('==========================================');

    SELECT COUNT(*) INTO v_count FROM GAUGE;
    DBMS_OUTPUT.PUT_LINE('Total gauges in database: ' || v_count);
    DBMS_OUTPUT.PUT_LINE('');
    DBMS_OUTPUT.PUT_LINE('Gauge Details:');
    DBMS_OUTPUT.PUT_LINE('------------------------------------------');

    FOR rec IN (SELECT * FROM GAUGE ORDER BY gauge_id) LOOP
        DBMS_OUTPUT.PUT_LINE('ID: ' || RPAD(rec.gauge_id, 5) ||
                            ' | Name: ' || RPAD(rec.name, 25) ||
                            ' | Size: ' || rec.size_mm || 'mm');
    END LOOP;

    DBMS_OUTPUT.PUT_LINE('==========================================');
END;
/

-- =====================================================
-- TEST SUMMARY
-- =====================================================
DECLARE
    v_total_tests NUMBER := 12;
BEGIN
    DBMS_OUTPUT.PUT_LINE('');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('TEST SUITE COMPLETE');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('Total tests executed: ' || v_total_tests);
    DBMS_OUTPUT.PUT_LINE('Please review the results above.');
    DBMS_OUTPUT.PUT_LINE('==========================================');
END;
/

