-- =====================================================
-- USBD38: Cleanup Test Data for Register Gauge Tests
-- =====================================================
-- Purpose: Remove test data and reset database to initial state
-- Author: Diogo Remelgado
-- Date: 2026-01-01
-- =====================================================
-- IMPORTANT: This script removes ALL test gauges (IDs 100-108)
-- Run this AFTER testing to clean up test data
-- =====================================================

DECLARE
    v_count NUMBER;
BEGIN
    -- Display initial state
    SELECT COUNT(*) INTO v_count FROM GAUGE WHERE gauge_id BETWEEN 100 AND 108;

    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('CLEANING UP TEST DATA');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('Test gauges to delete: ' || v_count);
    DBMS_OUTPUT.PUT_LINE('');

    -- Delete test gauges (IDs 100-108 used in tests)
    DELETE FROM GAUGE WHERE gauge_id BETWEEN 100 AND 108;

    DBMS_OUTPUT.PUT_LINE('Deleted test gauges (IDs 100-108)');

    -- Delete test results table if it exists
    BEGIN
        EXECUTE IMMEDIATE 'DROP TABLE TEST_RESULTS';
        DBMS_OUTPUT.PUT_LINE('Deleted TEST_RESULTS table');
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('TEST_RESULTS table not found (OK)');
    END;

    -- Commit changes
    COMMIT;

    DBMS_OUTPUT.PUT_LINE('');
    DBMS_OUTPUT.PUT_LINE('Changes committed');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('CLEANUP COMPLETE');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('');

    -- Show remaining gauges
    SELECT COUNT(*) INTO v_count FROM GAUGE;
    DBMS_OUTPUT.PUT_LINE('Remaining gauges in database: ' || v_count);
    DBMS_OUTPUT.PUT_LINE('');
    DBMS_OUTPUT.PUT_LINE('Current gauges in database:');
    DBMS_OUTPUT.PUT_LINE('------------------------------------------');

    FOR rec IN (SELECT gauge_id, name, size_mm FROM GAUGE ORDER BY gauge_id) LOOP
        DBMS_OUTPUT.PUT_LINE('ID: ' || RPAD(rec.gauge_id, 5) ||
                            ' | Name: ' || RPAD(rec.name, 20) ||
                            ' | Size: ' || rec.size_mm || 'mm');
    END LOOP;

    DBMS_OUTPUT.PUT_LINE('==========================================');
END;
/

-- Verify cleanup by displaying all remaining gauges
SELECT gauge_id AS "ID",
       name AS "Name",
       size_mm AS "Size (mm)"
FROM GAUGE
ORDER BY gauge_id;

