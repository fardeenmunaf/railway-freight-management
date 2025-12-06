--------------------------------------------------------------------------------
-- USBD35 – TEST 1: Valid Facility Registration Test
--------------------------------------------------------------------------------

VAR rc REFCURSOR
BEGIN
  :
rc := register_new_facility(
    p_name             => 'New Logistics Hub',
    p_facility_type_id => 1,
    p_lat              => 40.0000,
    p_lon              => -8.0000,
    p_is_intermodal    => 1
  );
END;
/
PRINT rc;


--------------------------------------------------------------------------------
-- USBD35 – TEST 2: Missing Required Attribute Test
--------------------------------------------------------------------------------

BEGIN
    DECLARE rc SYS_REFCURSOR;
    BEGIN
        rc := register_new_facility(
                p_name = > NULL,
                p_facility_type_id = > 1,
                p_lat = > 40.0,
                p_lon = > -8.0,
                p_is_intermodal = > 0
              );
    END;
END;
/

--------------------------------------------------------------------------------
-- USBD35 – TEST 3: Invalid Lookup Reference Test
--------------------------------------------------------------------------------

BEGIN
    DECLARE rc SYS_REFCURSOR;
    BEGIN
        rc := register_new_facility(
                p_name = > 'Bad Type Facility',
                p_facility_type_id = > 9999,
                p_lat = > 40.0,
                p_lon = > -8.0,
                p_is_intermodal = > 0
              );
    END;
END;
/


--------------------------------------------------------------------------------
-- USBD35 – TEST 4: Duplicate Facility Test
--------------------------------------------------------------------------------

VAR rc1 REFCURSOR
BEGIN
  :
rc1 := register_new_facility(
    p_name             => 'Duplicate Name Test',
    p_facility_type_id => 2,
    p_lat              => 41.0,
    p_lon              => -8.5,
    p_is_intermodal    => 0
  );
END;
/
PRINT rc1;

BEGIN
    DECLARE rc SYS_REFCURSOR;
    BEGIN
        rc := register_new_facility(
                p_name = > 'Duplicate Name Test',
                p_facility_type_id = > 2,
                p_lat = > 41.1,
                p_lon = > -8.6,
                p_is_intermodal = > 1
              );
    END;
END;

--------------------------------------------------------------------------------
-- USBD35 – TEST 5: Boundary Data Test
--------------------------------------------------------------------------------

VAR rc2 REFCURSOR
BEGIN
  :
rc2 := register_new_facility(
    p_name             => 'Boundary Coords Facility',
    p_facility_type_id => 3,
    p_lat              => -90,
    p_lon              => 180,
    p_is_intermodal    => 0
  );
END;
/
PRINT rc2;
