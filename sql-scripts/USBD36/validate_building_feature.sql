alter session set current_schema = APPUSER;

-- ============================================================
-- USBD36: A building can be inserted only if it references 
--         a valid existing facility with feature type 'Building' (feature_id = 6)
-- ============================================================

-- Function to insert a building (facility_feature with feature_id = 6)
-- Only inserts if the facility exists
CREATE OR REPLACE FUNCTION insert_building(p_facility_id IN NUMBER)
RETURN NUMBER
IS
    v_facility_exists NUMBER;
    v_already_has_building NUMBER;
BEGIN
    -- Check if the facility exists
    SELECT COUNT(*)
    INTO v_facility_exists
    FROM FACILITY
    WHERE facility_id = p_facility_id;
    
    -- If facility does not exist, raise an error
    IF v_facility_exists = 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 
            'A building can be inserted only if it references a valid existing facility.');
    END IF;
    
    -- Check if the facility already has a building feature
    SELECT COUNT(*)
    INTO v_already_has_building
    FROM FACILITY_FEATURE
    WHERE facility_id = p_facility_id
      AND feature_id = 6;
    
    -- If already has building feature, return the facility_id without inserting
    IF v_already_has_building > 0 THEN
        RETURN p_facility_id;
    END IF;
    
    -- Insert the building feature (feature_id = 6)
    INSERT INTO FACILITY_FEATURE (facility_id, feature_id)
    VALUES (p_facility_id, 6);
    
    COMMIT;
    
    -- Return the facility_id
    RETURN p_facility_id;
    
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- Display confirmation message
BEGIN
    DBMS_OUTPUT.PUT_LINE('USBD36: Building insertion function created successfully.');
    DBMS_OUTPUT.PUT_LINE('- Function: insert_building');
    DBMS_OUTPUT.PUT_LINE('- Usage: insert_building(facility_id)');
    DBMS_OUTPUT.PUT_LINE('- Validates and inserts a building feature only if facility exists.');
END;
/
