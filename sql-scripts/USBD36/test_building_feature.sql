alter session set current_schema = APPUSER;

-- Enable DBMS_OUTPUT for DataGrip
BEGIN
    DBMS_OUTPUT.ENABLE(1000000);
END;
/

-- ============================================================
-- USBD36: Test Cases for Building Feature Insertion
-- ============================================================

DECLARE
    v_result NUMBER;
    v_test_passed NUMBER := 0;
    v_test_failed NUMBER := 0;
    
    PROCEDURE log_test(p_test_name VARCHAR2, p_passed BOOLEAN) IS
    BEGIN
        IF p_passed THEN
            DBMS_OUTPUT.PUT_LINE('PASS: ' || p_test_name);
            v_test_passed := v_test_passed + 1;
        ELSE
            DBMS_OUTPUT.PUT_LINE('FAIL: ' || p_test_name);
            v_test_failed := v_test_failed + 1;
        END IF;
    END;
    
BEGIN
    DBMS_OUTPUT.PUT_LINE('========================================');
    DBMS_OUTPUT.PUT_LINE('USBD36 - Building Feature Test Cases');
    DBMS_OUTPUT.PUT_LINE('========================================');
    DBMS_OUTPUT.PUT_LINE('');
    -- TEST 1: Insert building for valid existing facility
    -- ========================================
    BEGIN
        DBMS_OUTPUT.PUT_LINE('Test 1: Insert building for valid existing facility (facility_id = 5)');
        v_result := insert_building(5);
        log_test('Building inserted successfully for facility_id = 5', v_result = 5);
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('ERROR: ' || SQLERRM);
            log_test('Building inserted successfully for facility_id = 5', FALSE);
    END;
    
    DBMS_OUTPUT.PUT_LINE('');
    
    -- ========================================
    -- TEST 2: Try to insert building for the same facility again (should not duplicate)
    -- ========================================
    BEGIN
        DBMS_OUTPUT.PUT_LINE('Test 2: Insert building for same facility again (should not duplicate)');
        v_result := insert_building(5);
        
        -- Verify it doesn't create duplicate
        DECLARE
            v_count NUMBER;
        BEGIN
            SELECT COUNT(*)
            INTO v_count
            FROM FACILITY_FEATURE
            WHERE facility_id = 5 AND feature_id = 6;
            
            log_test('No duplicate building feature created', v_count = 1);
        END;
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('ERROR: ' || SQLERRM);
            log_test('No duplicate building feature created', FALSE);
    END;
    
    DBMS_OUTPUT.PUT_LINE('');
    
    -- ========================================
    -- TEST 3: Insert building for another valid facility
    -- ========================================
    BEGIN
        DBMS_OUTPUT.PUT_LINE('Test 3: Insert building for another valid facility (facility_id = 7)');
        v_result := insert_building(7);
        log_test('Building inserted successfully for facility_id = 7', v_result = 7);
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('ERROR: ' || SQLERRM);
            log_test('Building inserted successfully for facility_id = 7', FALSE);
    END;
    
    DBMS_OUTPUT.PUT_LINE('');
    
    -- ========================================
    -- TEST 4: Try to insert building for non-existent facility (should fail)
    -- ========================================
    BEGIN
        DBMS_OUTPUT.PUT_LINE('Test 4: Insert building for non-existent facility (facility_id = 9999)');
        v_result := insert_building(9999);
        log_test('Error raised for non-existent facility', FALSE);
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -20001 THEN
                DBMS_OUTPUT.PUT_LINE('Expected error: ' || SQLERRM);
                log_test('Error raised for non-existent facility', TRUE);
            ELSE
                DBMS_OUTPUT.PUT_LINE('Unexpected error: ' || SQLERRM);
                log_test('Error raised for non-existent facility', FALSE);
            END IF;
    END;
    
    DBMS_OUTPUT.PUT_LINE('');
    
    -- ========================================
    -- TEST 5: Verify building features exist in FACILITY_FEATURE
    -- ========================================
    BEGIN
        DBMS_OUTPUT.PUT_LINE('Test 5: Verify building features exist in FACILITY_FEATURE');
        DECLARE
            v_count NUMBER;
        BEGIN
            SELECT COUNT(*)
            INTO v_count
            FROM FACILITY_FEATURE ff
            JOIN FACILITY f ON ff.facility_id = f.facility_id
            WHERE ff.feature_id = 6;
            
            DBMS_OUTPUT.PUT_LINE('Total buildings in database: ' || v_count);
            log_test('Buildings exist in FACILITY_FEATURE', v_count >= 2);
        END;
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('ERROR: ' || SQLERRM);
            log_test('Buildings exist in FACILITY_FEATURE', FALSE);
    END;
    
    DBMS_OUTPUT.PUT_LINE('');
    
    -- ========================================
    -- TEST 6: Verify feature_id = 6 is 'Building' in FACILITY_FEATURE_TYPE
    -- ========================================
    BEGIN
        DBMS_OUTPUT.PUT_LINE('Test 6: Verify feature_id = 6 is "Building"');
        DECLARE
            v_name VARCHAR2(30);
        BEGIN
            SELECT name
            INTO v_name
            FROM FACILITY_FEATURE_TYPE
            WHERE feature_id = 6;
            
            DBMS_OUTPUT.PUT_LINE('Feature name: ' || v_name);
            log_test('Feature_id = 6 is "Building"', v_name = 'Building');
        END;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            DBMS_OUTPUT.PUT_LINE('WARNING: feature_id = 6 not found in FACILITY_FEATURE_TYPE');
            log_test('Feature_id = 6 is "Building"', FALSE);
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('ERROR: ' || SQLERRM);
            log_test('Feature_id = 6 is "Building"', FALSE);
    END;
    
    -- ========================================
    -- CLEANUP: Remove test data
    -- ========================================
    DBMS_OUTPUT.PUT_LINE('');
    DBMS_OUTPUT.PUT_LINE('Cleaning up test data...');
    DELETE FROM FACILITY_FEATURE WHERE facility_id IN (5, 7) AND feature_id = 6;
    COMMIT;
    DBMS_OUTPUT.PUT_LINE('Test data cleaned up.');
    
    -- ========================================
    -- SUMMARY
    -- ========================================
    DBMS_OUTPUT.PUT_LINE('');
    DBMS_OUTPUT.PUT_LINE('========================================');
    DBMS_OUTPUT.PUT_LINE('Test Summary');
    DBMS_OUTPUT.PUT_LINE('========================================');
    DBMS_OUTPUT.PUT_LINE('Tests Passed: ' || v_test_passed);
    DBMS_OUTPUT.PUT_LINE('Tests Failed: ' || v_test_failed);
    DBMS_OUTPUT.PUT_LINE('Total Tests:  ' || (v_test_passed + v_test_failed));
    DBMS_OUTPUT.PUT_LINE('========================================');
    
    IF v_test_failed = 0 THEN
        DBMS_OUTPUT.PUT_LINE('ALL TESTS PASSED!');
    ELSE
        DBMS_OUTPUT.PUT_LINE('SOME TESTS FAILED!');
    END IF;
    DBMS_OUTPUT.PUT_LINE('========================================');
END;
/
