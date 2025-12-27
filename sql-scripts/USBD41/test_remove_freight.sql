alter session set current_schema = APPUSER;

-- Enable DBMS_OUTPUT for DataGrip
BEGIN
    DBMS_OUTPUT.ENABLE(1000000);
END;
/

-- ============================================================
-- USBD41: Test Cases for Remove Freight From Train
-- ============================================================

DECLARE
    v_result NUMBER;
    v_test_passed NUMBER := 0;
    v_test_failed NUMBER := 0;
    v_initial_count NUMBER;
    v_final_count NUMBER;
    
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
    DBMS_OUTPUT.PUT_LINE('USBD41 - Remove Freight From Train Test Cases');
    DBMS_OUTPUT.PUT_LINE('========================================');
    DBMS_OUTPUT.PUT_LINE('');
    
    -- ========================================
    -- TEST 1: Remove valid freight from valid train service
    -- ========================================
    BEGIN
        DBMS_OUTPUT.PUT_LINE('Test 1: Remove valid freight from valid train service (service_id = 2001, freight_id = 1001)');
        
        -- Get initial count
        SELECT COUNT(*) INTO v_initial_count
        FROM TRAIN_SERVICE_FREIGHT
        WHERE service_id = 2001 AND freight_id = 1001;
        
        v_result := remove_freight_from_train(2001, 1001);
        
        -- Get final count
        SELECT COUNT(*) INTO v_final_count
        FROM TRAIN_SERVICE_FREIGHT
        WHERE service_id = 2001 AND freight_id = 1001;
        
        log_test('Freight removed successfully', v_result = 1 AND v_final_count = 0);
        
        -- Restore data for next tests
        INSERT INTO TRAIN_SERVICE_FREIGHT (service_id, freight_id, position_no)
        VALUES (2001, 1001, 1);
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('ERROR: ' || SQLERRM);
            log_test('Freight removed successfully', FALSE);
    END;
    
    DBMS_OUTPUT.PUT_LINE('');
    
    -- ========================================
    -- TEST 2: Try to remove freight from non-existent train service (should fail)
    -- ========================================
    BEGIN
        DBMS_OUTPUT.PUT_LINE('Test 2: Remove freight from non-existent train service (service_id = 9999)');
        v_result := remove_freight_from_train(9999, 1);
        log_test('Error raised for non-existent train service', FALSE);
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -20001 THEN
                DBMS_OUTPUT.PUT_LINE('Expected error: ' || SQLERRM);
                log_test('Error raised for non-existent train service', TRUE);
            ELSE
                DBMS_OUTPUT.PUT_LINE('Unexpected error: ' || SQLERRM);
                log_test('Error raised for non-existent train service', FALSE);
            END IF;
    END;
    
    DBMS_OUTPUT.PUT_LINE('');
    
    -- ========================================
    -- TEST 3: Try to remove freight not assigned to train service (should fail)
    -- ========================================
    BEGIN
        DBMS_OUTPUT.PUT_LINE('Test 3: Remove freight not assigned to train service (service_id = 2001, freight_id = 9999)');
        v_result := remove_freight_from_train(2001, 9999);
        log_test('Error raised for unassigned freight', FALSE);
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -20002 THEN
                DBMS_OUTPUT.PUT_LINE('Expected error: ' || SQLERRM);
                log_test('Error raised for unassigned freight', TRUE);
            ELSE
                DBMS_OUTPUT.PUT_LINE('Unexpected error: ' || SQLERRM);
                log_test('Error raised for unassigned freight', FALSE);
            END IF;
    END;
    
    DBMS_OUTPUT.PUT_LINE('');
    
    -- ========================================
    -- TEST 4: Verify freight removal doesn't affect other train services
    -- ========================================
    BEGIN
        DBMS_OUTPUT.PUT_LINE('Test 4: Verify freight removal does not affect other train services');
        
        -- Get count of all train service freights before
        SELECT COUNT(*) INTO v_initial_count
        FROM TRAIN_SERVICE_FREIGHT;
        
        -- Remove one freight
        v_result := remove_freight_from_train(2002, 1002);
        
        -- Get count after
        SELECT COUNT(*) INTO v_final_count
        FROM TRAIN_SERVICE_FREIGHT;
        
        log_test('Only one freight removed', (v_initial_count - v_final_count) = 1);
        
        -- Restore data
        INSERT INTO TRAIN_SERVICE_FREIGHT (service_id, freight_id, position_no)
        VALUES (2002, 1002, 1);
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('ERROR: ' || SQLERRM);
            log_test('Only one freight removed', FALSE);
    END;
    
    DBMS_OUTPUT.PUT_LINE('');
    
    -- ========================================
    -- TEST 5: Verify data consistency after removal
    -- ========================================
    BEGIN
        DBMS_OUTPUT.PUT_LINE('Test 5: Verify data consistency after removal and restoration');
        
        -- Count freights for service_id = 2003 before
        SELECT COUNT(*) INTO v_initial_count
        FROM TRAIN_SERVICE_FREIGHT
        WHERE service_id = 2003;
        
        -- Remove freight
        v_result := remove_freight_from_train(2003, 1003);
        
        -- Verify removal
        SELECT COUNT(*) INTO v_final_count
        FROM TRAIN_SERVICE_FREIGHT
        WHERE service_id = 2003;
        
        DBMS_OUTPUT.PUT_LINE('Before: ' || v_initial_count || ', After removal: ' || v_final_count);
        
        -- Restore
        INSERT INTO TRAIN_SERVICE_FREIGHT (service_id, freight_id, position_no)
        VALUES (2003, 1003, 1);
        COMMIT;
        
        -- Verify restoration
        SELECT COUNT(*) INTO v_final_count
        FROM TRAIN_SERVICE_FREIGHT
        WHERE service_id = 2003;
        
        log_test('Data consistency maintained', v_initial_count = v_final_count);
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('ERROR: ' || SQLERRM);
            log_test('Data consistency maintained', FALSE);
    END;
    
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
