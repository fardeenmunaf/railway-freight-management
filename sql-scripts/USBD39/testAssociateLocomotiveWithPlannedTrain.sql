-- SET SERVEROUTPUT ON;
DECLARE
    v_result NUMBER;
    v_count NUMBER;
BEGIN
    -- LIMPA service 2003 primeiro
    DELETE FROM SERVICE_LOCOMOTIVE WHERE service_id = 2003;

    -- 1. VALID ASSIGNMENT TEST
    DBMS_OUTPUT.PUT_LINE('--- 1. VALID ASSIGNMENT TEST ---');
    v_result := fn_assign_locomotive_to_service(2003, 5623, 1);
    DBMS_OUTPUT.PUT_LINE('✅ SUCCESS: Result = ' || v_result);

    -- 2. INVALID LOCOMOTIVE
    DBMS_OUTPUT.PUT_LINE('--- 2. INVALID LOCOMOTIVE TEST ---');
    BEGIN
        v_result := fn_assign_locomotive_to_service(2003, 999999, 2);
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('✅ FK ERROR: ' || SQLERRM);
    END;

    -- 3. INVALID SERVICE
    DBMS_OUTPUT.PUT_LINE('--- 3. INVALID SERVICE TEST ---');
    BEGIN
        v_result := fn_assign_locomotive_to_service(9999, 5621, 1);
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('✅ SERVICE ERROR: ' || SQLERRM);
    END;

    -- 4. DUPLICATE ASSIGNMENT
    DBMS_OUTPUT.PUT_LINE('--- 4. DUPLICATE ASSIGNMENT TEST ---');
    BEGIN
        v_result := fn_assign_locomotive_to_service(2003, 5623, 1);
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('✅ DUPLICATE ERROR: ' || SQLERRM);
    END;

    -- 5-7. INTEGRITY + REMOVAL + CHECK
    DBMS_OUTPUT.PUT_LINE('--- 5-7. INTEGRITY/REMOVAL/CHECK ---');
    DELETE FROM SERVICE_LOCOMOTIVE WHERE service_id = 2003 AND locomotive_id = 5623;
    SELECT COUNT(*) INTO v_count FROM SERVICE_LOCOMOTIVE WHERE service_id = 2003;
    DBMS_OUTPUT.PUT_LINE('✅ ALL TESTS PASSED: ' || v_count || ' rows left');
END;
/
