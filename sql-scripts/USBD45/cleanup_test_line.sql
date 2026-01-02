-- =====================================================
-- USBD45: Cleanup Test Data for Register Line Tests
-- =====================================================
-- Purpose: Remove test data and reset database to initial state
-- Author: Diogo Remelgado
-- Date: 2026-01-01
-- =====================================================
-- IMPORTANT: This script removes ALL test lines and segments (IDs >= 100)
-- Run this AFTER testing to clean up test data
-- =====================================================

DECLARE
    v_line_count NUMBER;
    v_segment_count NUMBER;
BEGIN
    -- Display initial state
    SELECT COUNT(*) INTO v_line_count FROM LINE WHERE line_id >= 100;
    SELECT COUNT(*) INTO v_segment_count FROM LINE_SEGMENT WHERE segment_id >= 1000;

    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('CLEANING UP TEST DATA');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('Test lines to delete: ' || v_line_count);
    DBMS_OUTPUT.PUT_LINE('Test segments to delete: ' || v_segment_count);
    DBMS_OUTPUT.PUT_LINE('');

    -- Delete test segments first (FK constraint)
    DELETE FROM LINE_SEGMENT WHERE segment_id >= 1000;
    DBMS_OUTPUT.PUT_LINE('Deleted test segments (IDs >= 1000)');

    -- Delete test lines (IDs >= 100 used in tests)
    DELETE FROM LINE WHERE line_id >= 100;
    DBMS_OUTPUT.PUT_LINE('Deleted test lines (IDs >= 100)');

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

    -- Show remaining lines and segments
    SELECT COUNT(*) INTO v_line_count FROM LINE;
    SELECT COUNT(*) INTO v_segment_count FROM LINE_SEGMENT;

    DBMS_OUTPUT.PUT_LINE('Remaining lines in database: ' || v_line_count);
    DBMS_OUTPUT.PUT_LINE('Remaining segments in database: ' || v_segment_count);
    DBMS_OUTPUT.PUT_LINE('');
    DBMS_OUTPUT.PUT_LINE('Current lines in database:');
    DBMS_OUTPUT.PUT_LINE('------------------------------------------');

    FOR rec IN (SELECT line_id, name, owner_id FROM LINE ORDER BY line_id) LOOP
        DBMS_OUTPUT.PUT_LINE('ID: ' || RPAD(rec.line_id, 5) ||
                            ' | Name: ' || RPAD(rec.name, 30) ||
                            ' | Owner: ' || rec.owner_id);
    END LOOP;

    DBMS_OUTPUT.PUT_LINE('==========================================');
END;
/

-- Verify cleanup by displaying all remaining lines
SELECT line_id AS "ID",
       name AS "Name",
       owner_id AS "Owner ID"
FROM LINE
ORDER BY line_id;


