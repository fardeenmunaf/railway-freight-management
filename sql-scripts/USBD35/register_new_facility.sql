CREATE OR REPLACE FUNCTION usbd35_register_facility( p_name             IN FACILITY.name%TYPE,
  p_facility_type_id IN FACILITY.facility_type_id%TYPE,
  p_lat              IN FACILITY.lat%TYPE,
  p_lon              IN FACILITY.lon%TYPE,
  p_is_intermodal    IN FACILITY.is_intermodal%TYPE DEFAULT 0
) RETURN SYS_REFCURSOR
AS
  v_id FACILITY.facility_id%TYPE;
rc
SYS_REFCURSOR;
BEGIN
    IF p_name IS NULL
        OR p_facility_type_id IS NULL
        OR p_lat IS NULL
        OR p_lon IS NULL THEN
        RAISE_APPLICATION_ERROR(-20001, 'Required field is missing');
    END IF;

    DECLARE
        v_facility_type_count NUMBER;
    BEGIN
        SELECT COUNT(*)
        INTO v_facility_type_count
        FROM FACILITY_TYPE ft
        WHERE ft.facility_type_id = p_facility_type_id;

        IF v_facility_type_count = 0 THEN
            RAISE_APPLICATION_ERROR(-20002, 'Facility type not found');
        END IF;
    END;

    IF NVL(p_is_intermodal, 0) NOT IN (0, 1) THEN
        RAISE_APPLICATION_ERROR(-20004, 'is_intermodal must be 0 or 1');
    END IF;

    DECLARE
        v_duplicate_count NUMBER;
    BEGIN
        SELECT COUNT(*)
        INTO v_duplicate_count
        FROM FACILITY f
        WHERE UPPER(f.name) = UPPER(p_name);

        IF v_duplicate_count > 0 THEN
            RAISE_APPLICATION_ERROR(-20003, 'Duplicate facility name');
        END IF;
    END;

    INSERT INTO FACILITY (name,
                          facility_type_id,
                          lat,
                          lon,
                          is_intermodal)
    VALUES (p_name,
            p_facility_type_id,
            p_lat,
            p_lon,
            NVL(p_is_intermodal, 0))
    RETURNING facility_id
    INTO v_id;

    OPEN rc FOR
    SELECT f.facility_id,
           f.name,
           f.facility_type_id,
           f.lat,
           f.lon,
           f.is_intermodal
    FROM FACILITY f
    WHERE f.facility_id = v_id;

    RETURN rc;

EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN
        RAISE_APPLICATION_ERROR(-20003, 'Duplicate facility name');
END usbd35_register_facility;
/
