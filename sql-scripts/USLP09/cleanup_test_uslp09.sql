-- =====================================================
-- USLP09: Cleanup Test Data
-- =====================================================
-- Purpose: Clean up any test data created during testing
-- Author: Diogo Remelgado
-- Date: 2026-01-02
-- =====================================================
-- NOTE: The USLP09 test suite (test_uslp09.sql) is READ-ONLY
-- It only queries existing data and does not insert test data
--
-- However, if you manually tested fn_assemble_train_composition()
-- or created test train services, this script will help clean them up
-- =====================================================

DECLARE
    v_test_service_count NUMBER := 0;
    v_test_assignment_count NUMBER := 0;
    v_deleted_services NUMBER := 0;
    v_deleted_assignments NUMBER := 0;
    v_initial_loco_count NUMBER;
    v_initial_wagon_count NUMBER;
    v_initial_service_count NUMBER;
    v_initial_assignment_count NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('USLP09 - CLEANUP TEST DATA');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('');

    -- Get initial counts
    SELECT COUNT(*) INTO v_initial_loco_count FROM LOCOMOTIVE;
    SELECT COUNT(*) INTO v_initial_wagon_count FROM WAGON;
    SELECT COUNT(*) INTO v_initial_service_count FROM TRAIN_SERVICE;
    SELECT COUNT(*) INTO v_initial_assignment_count FROM SERVICE_LOCOMOTIVE;

    DBMS_OUTPUT.PUT_LINE('Initial Database State:');
    DBMS_OUTPUT.PUT_LINE('  Locomotives: ' || v_initial_loco_count);
    DBMS_OUTPUT.PUT_LINE('  Wagons: ' || v_initial_wagon_count);
    DBMS_OUTPUT.PUT_LINE('  Train Services: ' || v_initial_service_count);
    DBMS_OUTPUT.PUT_LINE('  Locomotive Assignments: ' || v_initial_assignment_count);
    DBMS_OUTPUT.PUT_LINE('');

    -- =====================================================
    -- STEP 1: Check for test service locomotive assignments
    -- =====================================================
    DBMS_OUTPUT.PUT_LINE('------------------------------------------');
    DBMS_OUTPUT.PUT_LINE('STEP 1: Checking for test assignments...');
    DBMS_OUTPUT.PUT_LINE('------------------------------------------');

    BEGIN
        -- Check for test services (IDs >= 9000 typically used for testing)
        SELECT COUNT(DISTINCT sl.service_id)
        INTO v_test_service_count
        FROM SERVICE_LOCOMOTIVE sl
        INNER JOIN TRAIN_SERVICE ts ON sl.service_id = ts.service_id
        WHERE ts.service_id >= 9000;

        SELECT COUNT(*)
        INTO v_test_assignment_count
        FROM SERVICE_LOCOMOTIVE sl
        INNER JOIN TRAIN_SERVICE ts ON sl.service_id = ts.service_id
        WHERE ts.service_id >= 9000;

        IF v_test_assignment_count > 0 THEN
            DBMS_OUTPUT.PUT_LINE('Found ' || v_test_assignment_count || ' test locomotive assignment(s)');
            DBMS_OUTPUT.PUT_LINE('in ' || v_test_service_count || ' test service(s)');
            DBMS_OUTPUT.PUT_LINE('');

            -- Display test assignments before deletion
            DBMS_OUTPUT.PUT_LINE('Test assignments to be deleted:');
            FOR rec IN (
                SELECT sl.service_id, sl.locomotive_id, sl.position_no, ts.status
                FROM SERVICE_LOCOMOTIVE sl
                INNER JOIN TRAIN_SERVICE ts ON sl.service_id = ts.service_id
                WHERE ts.service_id >= 9000
                ORDER BY sl.service_id, sl.position_no
            ) LOOP
                DBMS_OUTPUT.PUT_LINE('  Service ' || rec.service_id ||
                                   ' -> Locomotive ' || rec.locomotive_id ||
                                   ' (Position ' || rec.position_no ||
                                   ', Status: ' || rec.status || ')');
            END LOOP;

            -- Delete test assignments
            DELETE FROM SERVICE_LOCOMOTIVE
            WHERE service_id IN (
                SELECT service_id FROM TRAIN_SERVICE WHERE service_id >= 9000
            );

            v_deleted_assignments := SQL%ROWCOUNT;
            DBMS_OUTPUT.PUT_LINE('');
            DBMS_OUTPUT.PUT_LINE('Deleted ' || v_deleted_assignments || ' test assignment(s)');
        ELSE
            DBMS_OUTPUT.PUT_LINE('No test locomotive assignments found');
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Could not check/delete test assignments: ' || SQLERRM);
    END;

    DBMS_OUTPUT.PUT_LINE('');

    -- =====================================================
    -- STEP 2: Check for test train services
    -- =====================================================
    DBMS_OUTPUT.PUT_LINE('------------------------------------------');
    DBMS_OUTPUT.PUT_LINE('STEP 2: Checking for test train services...');
    DBMS_OUTPUT.PUT_LINE('------------------------------------------');

    BEGIN
        -- Check for test services without assignments (orphaned)
        SELECT COUNT(*)
        INTO v_test_service_count
        FROM TRAIN_SERVICE
        WHERE service_id >= 9000;

        IF v_test_service_count > 0 THEN
            DBMS_OUTPUT.PUT_LINE('Found ' || v_test_service_count || ' test train service(s)');
            DBMS_OUTPUT.PUT_LINE('');

            -- Display test services before deletion
            DBMS_OUTPUT.PUT_LINE('Test services to be deleted:');
            FOR rec IN (
                SELECT service_id, train_id, status, departure_date, arrival_date
                FROM TRAIN_SERVICE
                WHERE service_id >= 9000
                ORDER BY service_id
            ) LOOP
                DBMS_OUTPUT.PUT_LINE('  Service ID ' || rec.service_id ||
                                   ' | Train ' || rec.train_id ||
                                   ' | Status: ' || rec.status);
            END LOOP;

            -- Note: Only delete if you actually created test services
            -- Uncomment the following lines if needed:
            /*
            DELETE FROM TRAIN_SERVICE WHERE service_id >= 9000;
            v_deleted_services := SQL%ROWCOUNT;
            DBMS_OUTPUT.PUT_LINE('');
            DBMS_OUTPUT.PUT_LINE('Deleted ' || v_deleted_services || ' test service(s)');
            */

            DBMS_OUTPUT.PUT_LINE('');
            DBMS_OUTPUT.PUT_LINE('Test services found but NOT deleted (uncomment delete if needed)');
        ELSE
            DBMS_OUTPUT.PUT_LINE('No test train services found');
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Could not check test services: ' || SQLERRM);
    END;

    DBMS_OUTPUT.PUT_LINE('');

    -- =====================================================
    -- STEP 3: Clean up test trains (if created)
    -- =====================================================
    DBMS_OUTPUT.PUT_LINE('------------------------------------------');
    DBMS_OUTPUT.PUT_LINE('STEP 3: Checking for test trains...');
    DBMS_OUTPUT.PUT_LINE('------------------------------------------');

    BEGIN
        DECLARE
            v_test_train_count NUMBER := 0;
        BEGIN
            -- Check for test trains (IDs >= 9000)
            SELECT COUNT(*)
            INTO v_test_train_count
            FROM TRAIN
            WHERE train_id >= 9000;

            IF v_test_train_count > 0 THEN
                DBMS_OUTPUT.PUT_LINE('Found ' || v_test_train_count || ' test train(s)');
                DBMS_OUTPUT.PUT_LINE('Not deleted (uncomment delete statement if needed)');

                -- Uncomment if you need to delete test trains:
                /*
                DELETE FROM TRAIN WHERE train_id >= 9000;
                DBMS_OUTPUT.PUT_LINE('Deleted ' || SQL%ROWCOUNT || ' test train(s)');
                */
            ELSE
                DBMS_OUTPUT.PUT_LINE('No test trains found');
            END IF;
        END;
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Could not check test trains: ' || SQLERRM);
    END;

    DBMS_OUTPUT.PUT_LINE('');

    -- =====================================================
    -- STEP 4: Commit changes
    -- =====================================================
    IF v_deleted_assignments > 0 OR v_deleted_services > 0 THEN
        COMMIT;
        DBMS_OUTPUT.PUT_LINE('------------------------------------------');
        DBMS_OUTPUT.PUT_LINE('Changes committed to database');
        DBMS_OUTPUT.PUT_LINE('------------------------------------------');
    ELSE
        DBMS_OUTPUT.PUT_LINE('------------------------------------------');
        DBMS_OUTPUT.PUT_LINE('No changes to commit (database unchanged)');
        DBMS_OUTPUT.PUT_LINE('------------------------------------------');
    END IF;

    DBMS_OUTPUT.PUT_LINE('');

    -- =====================================================
    -- FINAL STATE: Display current database state
    -- =====================================================
    DECLARE
        v_final_loco_count NUMBER;
        v_final_wagon_count NUMBER;
        v_final_service_count NUMBER;
        v_final_assignment_count NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_final_loco_count FROM LOCOMOTIVE;
        SELECT COUNT(*) INTO v_final_wagon_count FROM WAGON;
        SELECT COUNT(*) INTO v_final_service_count FROM TRAIN_SERVICE;
        SELECT COUNT(*) INTO v_final_assignment_count FROM SERVICE_LOCOMOTIVE;

        DBMS_OUTPUT.PUT_LINE('==========================================');
        DBMS_OUTPUT.PUT_LINE('FINAL DATABASE STATE');
        DBMS_OUTPUT.PUT_LINE('==========================================');
        DBMS_OUTPUT.PUT_LINE('  Locomotives: ' || v_final_loco_count ||
                            ' (was ' || v_initial_loco_count || ')');
        DBMS_OUTPUT.PUT_LINE('  Wagons: ' || v_final_wagon_count ||
                            ' (was ' || v_initial_wagon_count || ')');
        DBMS_OUTPUT.PUT_LINE('  Train Services: ' || v_final_service_count ||
                            ' (was ' || v_initial_service_count || ')');
        DBMS_OUTPUT.PUT_LINE('  Locomotive Assignments: ' || v_final_assignment_count ||
                            ' (was ' || v_initial_assignment_count || ')');
        DBMS_OUTPUT.PUT_LINE('');

        IF v_deleted_assignments > 0 THEN
            DBMS_OUTPUT.PUT_LINE('Summary:');
            DBMS_OUTPUT.PUT_LINE('  Removed ' || v_deleted_assignments || ' test assignment(s)');
            IF v_deleted_services > 0 THEN
                DBMS_OUTPUT.PUT_LINE('  Removed ' || v_deleted_services || ' test service(s)');
            END IF;
        ELSE
            DBMS_OUTPUT.PUT_LINE('No test data was found or deleted.');
            DBMS_OUTPUT.PUT_LINE('The USLP09 test suite is read-only.');
        END IF;

        DBMS_OUTPUT.PUT_LINE('==========================================');
    END;

    DBMS_OUTPUT.PUT_LINE('');
    DBMS_OUTPUT.PUT_LINE('==========================================');
    DBMS_OUTPUT.PUT_LINE('CLEANUP COMPLETE');
    DBMS_OUTPUT.PUT_LINE('==========================================');

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('');
        DBMS_OUTPUT.PUT_LINE('==========================================');
        DBMS_OUTPUT.PUT_LINE('ERROR DURING CLEANUP');
        DBMS_OUTPUT.PUT_LINE('==========================================');
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
        DBMS_OUTPUT.PUT_LINE('Changes rolled back');
        DBMS_OUTPUT.PUT_LINE('==========================================');
        RAISE;
END;
/

-- =====================================================
-- VERIFICATION QUERY
-- =====================================================
-- Display current locomotive assignments for verification
-- =====================================================
DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count FROM SERVICE_LOCOMOTIVE;

    IF v_count > 0 THEN
        DBMS_OUTPUT.PUT_LINE('');
        DBMS_OUTPUT.PUT_LINE('Current Locomotive Assignments (first 10):');
        DBMS_OUTPUT.PUT_LINE('------------------------------------------');

        FOR rec IN (
            SELECT sl.service_id, sl.locomotive_id, sl.position_no, ts.status
            FROM SERVICE_LOCOMOTIVE sl
            INNER JOIN TRAIN_SERVICE ts ON sl.service_id = ts.service_id
            WHERE ROWNUM <= 10
            ORDER BY sl.service_id, sl.position_no
        ) LOOP
            DBMS_OUTPUT.PUT_LINE('Service ' || rec.service_id ||
                               ' -> Loco ' || rec.locomotive_id ||
                               ' (Pos ' || rec.position_no ||
                               ', ' || rec.status || ')');
        END LOOP;

        IF v_count > 10 THEN
            DBMS_OUTPUT.PUT_LINE('... (' || (v_count - 10) || ' more)');
        END IF;
    ELSE
        DBMS_OUTPUT.PUT_LINE('');
        DBMS_OUTPUT.PUT_LINE('No locomotive assignments in database');
    END IF;

    DBMS_OUTPUT.PUT_LINE('');
END;
/

