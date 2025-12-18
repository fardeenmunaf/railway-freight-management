SET SERVEROUTPUT ON;

DECLARE
    v_result NUMBER;
    v_count  NUMBER;
BEGIN
    -- limpar apenas os movimentos de teste do service 2001, posições >= 10
    DELETE FROM TRAIN_STOP
    WHERE service_id = 2001
      AND position >= 10;
    COMMIT;

    -----------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('--- 1. VALID PASSING MOVEMENT TEST ---');
    -----------------------------------------------------------------
    BEGIN
        v_result := fn_register_train_movement(
                p_service_id    => 2001,
                p_facility_id   => 5,
                p_parking_track => 1,
                p_position      => 10,
                p_sched_arr     => TO_TIMESTAMP('2025-11-01 08:00:00','YYYY-MM-DD HH24:MI:SS'),
                p_sched_dep     => TO_TIMESTAMP('2025-11-01 08:00:00','YYYY-MM-DD HH24:MI:SS'),
                p_is_stop       => 0,
                p_act_arr       => TO_TIMESTAMP('2025-11-01 08:01:00','YYYY-MM-DD HH24:MI:SS'),
                p_act_dep       => NULL
                    );
        DBMS_OUTPUT.PUT_LINE('SUCCESS: Passing movement ID = ' || v_result);
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('ERROR (should NOT fail): ' || SQLERRM);
    END;

    -----------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('--- 2. VALID STOP MOVEMENT TEST ---');
    -----------------------------------------------------------------
    BEGIN
        v_result := fn_register_train_movement(
                p_service_id    => 2001,
                p_facility_id   => 5,
                p_parking_track => 1,
                p_position      => 11,
                p_sched_arr     => TO_TIMESTAMP('2025-11-01 09:00:00','YYYY-MM-DD HH24:MI:SS'),
                p_sched_dep     => TO_TIMESTAMP('2025-11-01 09:15:00','YYYY-MM-DD HH24:MI:SS'),
                p_is_stop       => 1,
                p_act_arr       => TO_TIMESTAMP('2025-11-01 09:02:00','YYYY-MM-DD HH24:MI:SS'),
                p_act_dep       => TO_TIMESTAMP('2025-11-01 09:10:00','YYYY-MM-DD HH24:MI:SS')
                    );
        DBMS_OUTPUT.PUT_LINE('SUCCESS: Stop movement ID = ' || v_result);
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('ERROR (should NOT fail): ' || SQLERRM);
    END;

    -----------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('--- 3. MISSING DEPARTURE TIME TEST ---');
    -----------------------------------------------------------------
    BEGIN
        v_result := fn_register_train_movement(
                p_service_id    => 2001,
                p_facility_id   => 5,
                p_parking_track => 1,
                p_position      => 12,
                p_sched_arr     => TO_TIMESTAMP('2025-11-01 10:00:00','YYYY-MM-DD HH24:MI:SS'),
                p_sched_dep     => TO_TIMESTAMP('2025-11-01 10:15:00','YYYY-MM-DD HH24:MI:SS'),
                p_is_stop       => 1,
                p_act_arr       => TO_TIMESTAMP('2025-11-01 10:05:00','YYYY-MM-DD HH24:MI:SS'),
                p_act_dep       => NULL
                    );
        DBMS_OUTPUT.PUT_LINE('ERROR: should have failed but returned ' || v_result);
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('EXPECTED ERROR (missing dep): ' || SQLERRM);
    END;

    -----------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('--- 4. INVALID DEPARTURE TIME TEST ---');
    -----------------------------------------------------------------
    BEGIN
        v_result := fn_register_train_movement(
                p_service_id    => 2001,
                p_facility_id   => 5,
                p_parking_track => 1,
                p_position      => 13,
                p_sched_arr     => TO_TIMESTAMP('2025-11-01 11:00:00','YYYY-MM-DD HH24:MI:SS'),
                p_sched_dep     => TO_TIMESTAMP('2025-11-01 11:15:00','YYYY-MM-DD HH24:MI:SS'),
                p_is_stop       => 1,
                p_act_arr       => TO_TIMESTAMP('2025-11-01 11:05:00','YYYY-MM-DD HH24:MI:SS'),
                p_act_dep       => TO_TIMESTAMP('2025-11-01 11:01:00','YYYY-MM-DD HH24:MI:SS')
                    );
        DBMS_OUTPUT.PUT_LINE('ERROR: should have failed but returned ' || v_result);
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('EXPECTED ERROR (dep < arr): ' || SQLERRM);
    END;

    -----------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('--- 5. INVALID TRAIN/FACILITY REFERENCE TEST ---');
    -----------------------------------------------------------------
    BEGIN
        v_result := fn_register_train_movement(
                p_service_id    => 9999,
                p_facility_id   => 9999,
                p_parking_track => 1,
                p_position      => 14,
                p_sched_arr     => TO_TIMESTAMP('2025-11-01 12:00:00','YYYY-MM-DD HH24:MI:SS'),
                p_sched_dep     => TO_TIMESTAMP('2025-11-01 12:15:00','YYYY-MM-DD HH24:MI:SS'),
                p_is_stop       => 0,
                p_act_arr       => TO_TIMESTAMP('2025-11-01 12:05:00','YYYY-MM-DD HH24:MI:SS'),
                p_act_dep       => NULL
                    );
        DBMS_OUTPUT.PUT_LINE('ERROR: should have failed but returned ' || v_result);
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('EXPECTED FK ERROR: ' || SQLERRM);
    END;

    -----------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('--- 6. DUPLICATE MOVEMENT RECORD TEST ---');
    -----------------------------------------------------------------
    DECLARE
        v_dup_id NUMBER;
    BEGIN
        v_dup_id := fn_register_train_movement(
                p_service_id    => 2001,
                p_facility_id   => 5,
                p_parking_track => 1,
                p_position      => 15,   -- livre
                p_sched_arr     => TO_TIMESTAMP('2025-11-01 13:00:00','YYYY-MM-DD HH24:MI:SS'),
                p_sched_dep     => TO_TIMESTAMP('2025-11-01 13:15:00','YYYY-MM-DD HH24:MI:SS'),
                p_is_stop       => 0,
                p_act_arr       => TO_TIMESTAMP('2025-11-01 13:05:00','YYYY-MM-DD HH24:MI:SS'),
                p_act_dep       => NULL
                    );
        DBMS_OUTPUT.PUT_LINE('Base movement created, ID = ' || v_dup_id);

        BEGIN
            v_result := fn_register_train_movement(
                    p_service_id    => 2001,
                    p_facility_id   => 5,
                    p_parking_track => 1,
                    p_position      => 15,   -- mesma (service_id, position) => UNIQUE do índice train_stop_index_16[file:1]
                    p_sched_arr     => TO_TIMESTAMP('2025-11-01 13:30:00','YYYY-MM-DD HH24:MI:SS'),
                    p_sched_dep     => TO_TIMESTAMP('2025-11-01 13:45:00','YYYY-MM-DD HH24:MI:SS'),
                    p_is_stop       => 0,
                    p_act_arr       => TO_TIMESTAMP('2025-11-01 13:35:00','YYYY-MM-DD HH24:MI:SS'),
                    p_act_dep       => NULL
                        );
            DBMS_OUTPUT.PUT_LINE('ERROR: duplicate should have failed but returned ' || v_result);
        EXCEPTION
            WHEN OTHERS THEN
                DBMS_OUTPUT.PUT_LINE('EXPECTED UNIQUE ERROR: ' || SQLERRM);
        END;
    END;

    -----------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('--- 7. DATA DICTIONARY CHECK ---');
    -----------------------------------------------------------------
    SELECT COUNT(*)
    INTO v_count
    FROM TRAIN_STOP
    WHERE service_id = 2001;

    DBMS_OUTPUT.PUT_LINE('TRAIN_STOP rows for service 2001 = ' || v_count);
END;
/
