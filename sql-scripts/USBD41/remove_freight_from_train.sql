alter session set current_schema = APPUSER;

-- ============================================================
-- USBD41: Remove a Freight From a Planned Train
-- ============================================================
-- As a Freight Manager, I want to remove a freight from a planned train.
-- A freight can only be removed if it references a valid existing train service.
-- ============================================================

CREATE OR REPLACE FUNCTION remove_freight_from_train(
    p_service_id NUMBER,
    p_freight_id NUMBER
) RETURN NUMBER IS
    v_facility_count NUMBER;
    v_deleted_count NUMBER := 0;
BEGIN
    -- Validate that the train service exists
    SELECT COUNT(*)
    INTO v_facility_count
    FROM TRAIN_SERVICE
    WHERE service_id = p_service_id;
    
    IF v_facility_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'A freight can only be removed if it references a valid existing train service');
    END IF;
    
    -- Check if the freight is actually assigned to this train service
    SELECT COUNT(*)
    INTO v_facility_count
    FROM TRAIN_SERVICE_FREIGHT
    WHERE service_id = p_service_id AND freight_id = p_freight_id;
    
    IF v_facility_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'The specified freight is not assigned to this train service');
    END IF;
    
    -- Remove the freight from the train service
    DELETE FROM TRAIN_SERVICE_FREIGHT
    WHERE service_id = p_service_id AND freight_id = p_freight_id;
    
    v_deleted_count := SQL%ROWCOUNT;
    
    COMMIT;
    
    RETURN v_deleted_count;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END remove_freight_from_train;
/
