CREATE OR REPLACE FUNCTION usbd35_register_facility(
    p_name             IN FACILITY.name%TYPE,
    p_facility_type_id IN FACILITY.facility_type_id%TYPE,
    p_lat              IN FACILITY.lat%TYPE,
    p_lon              IN FACILITY.lon%TYPE,
    p_is_intermodal    IN FACILITY.is_intermodal%TYPE DEFAULT 0
) RETURN FACILITY.facility_id%TYPE
AS
    v_id FACILITY.facility_id%TYPE;
    v_dummy NUMBER;
BEGIN
    IF p_name IS NULL
        OR p_facility_type_id IS NULL
        OR p_lat IS NULL
        OR p_lon IS NULL THEN
        RAISE_APPLICATION_ERROR(-20001, 'Required field is missing');
    END IF;

    IF NVL(p_is_intermodal, 0) NOT IN (0, 1) THEN
        RAISE_APPLICATION_ERROR(-20004, 'is_intermodal must be 0 or 1');
    END IF;

    BEGIN
        SELECT 1 INTO v_dummy
        FROM FACILITY_TYPE
        WHERE facility_type_id = p_facility_type_id;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20002, 'Facility type not found');
    END;

    BEGIN
        SELECT 1 INTO v_dummy
        FROM FACILITY
        WHERE UPPER(name) = UPPER(p_name);
        RAISE_APPLICATION_ERROR(-20003, 'Duplicate facility name');
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            NULL;
    END;

    INSERT INTO FACILITY(name, facility_type_id, lat, lon, is_intermodal)
    VALUES (p_name, p_facility_type_id, p_lat, p_lon, NVL(p_is_intermodal, 0))
    RETURNING facility_id INTO v_id;

    RETURN v_id;
END;
/
