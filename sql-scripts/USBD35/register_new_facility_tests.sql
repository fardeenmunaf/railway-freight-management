ALTER SESSION SET CURRENT_SCHEMA = APPUSER;

BEGIN
  DBMS_OUTPUT.ENABLE(1000000);
END;
/

--------------------------------------------------------------------------------
-- Preparação: garantir FACILITY_TYPE mínimo para os testes
--------------------------------------------------------------------------------
DECLARE
v_count NUMBER;
BEGIN
SELECT COUNT(*) INTO v_count FROM FACILITY_TYPE;

IF v_count = 0 THEN
    DBMS_OUTPUT.PUT_LINE('Inserting required FACILITY_TYPE data...');
INSERT INTO FACILITY_TYPE (facility_type_id, name) VALUES (1, 'Warehouse');
INSERT INTO FACILITY_TYPE (facility_type_id, name) VALUES (2, 'Train Station');
INSERT INTO FACILITY_TYPE (facility_type_id, name) VALUES (3, 'Depot');
INSERT INTO FACILITY_TYPE (facility_type_id, name) VALUES (4, 'Maintenance Yard');
INSERT INTO FACILITY_TYPE (facility_type_id, name) VALUES (5, 'Intermodal Terminal');
COMMIT;
DBMS_OUTPUT.PUT_LINE('FACILITY_TYPE data inserted successfully.');
ELSE
    DBMS_OUTPUT.PUT_LINE('FACILITY_TYPE data already exists (' || v_count || ' records).');
END IF;

  DBMS_OUTPUT.PUT_LINE('');
END;
/

--------------------------------------------------------------------------------
-- Limpeza antes dos testes: apagar facilities de testes antigos
--------------------------------------------------------------------------------
DECLARE
v_count NUMBER;
BEGIN
DELETE FROM FACILITY
WHERE UPPER(name) LIKE 'TEST\_USBD35\_%' ESCAPE '\';

v_count := SQL%ROWCOUNT;

  IF v_count > 0 THEN
    DBMS_OUTPUT.PUT_LINE('Cleaned up ' || v_count || ' existing test facility/facilities');
ELSE
    DBMS_OUTPUT.PUT_LINE('No existing test facilities found to clean up');
END IF;

COMMIT;
DBMS_OUTPUT.PUT_LINE('');
END;
/

--------------------------------------------------------------------------------
-- TEST 1: Missing Required Attribute (NULL name) -> ORA-20001
--------------------------------------------------------------------------------
DECLARE
v_id NUMBER;
BEGIN
  DBMS_OUTPUT.PUT_LINE('============================================================');
  DBMS_OUTPUT.PUT_LINE('TEST 1: NULL name (expect ORA-20001)');
  DBMS_OUTPUT.PUT_LINE('============================================================');

  v_id := usbd35_register_facility(
    p_name             => NULL,
    p_facility_type_id => 1,
    p_lat              => 40.0,
    p_lon              => -8.0,
    p_is_intermodal    => 0
  );

  DBMS_OUTPUT.PUT_LINE('FAIL: function returned id ' || v_id || ' but should have raised ORA-20001');
EXCEPTION
  WHEN OTHERS THEN
    IF SQLCODE = -20001 THEN
      DBMS_OUTPUT.PUT_LINE('PASS: expected error caught: ' || SQLERRM);
ELSE
      DBMS_OUTPUT.PUT_LINE('FAIL: unexpected error: ' || SQLERRM);
END IF;
END;
/

--------------------------------------------------------------------------------
-- TEST 2: Invalid facility_type_id -> ORA-20002
--------------------------------------------------------------------------------
DECLARE
v_id NUMBER;
BEGIN
  DBMS_OUTPUT.PUT_LINE('============================================================');
  DBMS_OUTPUT.PUT_LINE('TEST 2: invalid facility_type_id (expect ORA-20002)');
  DBMS_OUTPUT.PUT_LINE('============================================================');

  v_id := usbd35_register_facility(
    p_name             => 'TEST_USBD35_BadType_001',
    p_facility_type_id => 9999,
    p_lat              => 40.0,
    p_lon              => -8.0,
    p_is_intermodal    => 0
  );

  DBMS_OUTPUT.PUT_LINE('FAIL: function returned id ' || v_id || ' but should have raised ORA-20002');
EXCEPTION
  WHEN OTHERS THEN
    IF SQLCODE = -20002 THEN
      DBMS_OUTPUT.PUT_LINE('PASS: expected error caught: ' || SQLERRM);
ELSE
      DBMS_OUTPUT.PUT_LINE('FAIL: unexpected error: ' || SQLERRM);
END IF;
END;
/

--------------------------------------------------------------------------------
-- TEST 3: Duplicate facility name (case-insensitive) -> ORA-20003
--------------------------------------------------------------------------------
DECLARE
v_id1 NUMBER;
  v_id2 NUMBER;
BEGIN
  DBMS_OUTPUT.PUT_LINE('============================================================');
  DBMS_OUTPUT.PUT_LINE('TEST 3: duplicate name (expect ORA-20003)');
  DBMS_OUTPUT.PUT_LINE('============================================================');

  v_id1 := usbd35_register_facility(
    p_name             => 'TEST_USBD35_Duplicate_001',
    p_facility_type_id => 2,
    p_lat              => 41.0,
    p_lon              => -8.5,
    p_is_intermodal    => 0
  );

  DBMS_OUTPUT.PUT_LINE('First insert OK. ID=' || v_id1);

  -- mesma string mas com casing diferente para testar UPPER(...)
  v_id2 := usbd35_register_facility(
    p_name             => 'test_usbd35_duplicate_001',
    p_facility_type_id => 2,
    p_lat              => 41.1,
    p_lon              => -8.6,
    p_is_intermodal    => 1
  );

  DBMS_OUTPUT.PUT_LINE('FAIL: function returned id ' || v_id2 || ' but should have raised ORA-20003');
EXCEPTION
  WHEN OTHERS THEN
    IF SQLCODE = -20003 THEN
      DBMS_OUTPUT.PUT_LINE('PASS: expected error caught: ' || SQLERRM);
ELSE
      DBMS_OUTPUT.PUT_LINE('FAIL: unexpected error: ' || SQLERRM);
END IF;
END;
/

--------------------------------------------------------------------------------
-- TEST 4: Invalid is_intermodal -> ORA-20004
--------------------------------------------------------------------------------
DECLARE
v_id NUMBER;
BEGIN
  DBMS_OUTPUT.PUT_LINE('============================================================');
  DBMS_OUTPUT.PUT_LINE('TEST 4: invalid is_intermodal (expect ORA-20004)');
  DBMS_OUTPUT.PUT_LINE('============================================================');

  v_id := usbd35_register_facility(
    p_name             => 'TEST_USBD35_InvalidIntermodal_001',
    p_facility_type_id => 1,
    p_lat              => 40.5,
    p_lon              => -8.5,
    p_is_intermodal    => 5
  );

  DBMS_OUTPUT.PUT_LINE('FAIL: function returned id ' || v_id || ' but should have raised ORA-20004');
EXCEPTION
  WHEN OTHERS THEN
    IF SQLCODE = -20004 THEN
      DBMS_OUTPUT.PUT_LINE('PASS: expected error caught: ' || SQLERRM);
ELSE
      DBMS_OUTPUT.PUT_LINE('FAIL: unexpected error: ' || SQLERRM);
END IF;
END;
/

--------------------------------------------------------------------------------
-- TEST 5: NULL facility_type_id -> ORA-20001
--------------------------------------------------------------------------------
DECLARE
v_id NUMBER;
BEGIN
  DBMS_OUTPUT.PUT_LINE('============================================================');
  DBMS_OUTPUT.PUT_LINE('TEST 5: NULL facility_type_id (expect ORA-20001)');
  DBMS_OUTPUT.PUT_LINE('============================================================');

  v_id := usbd35_register_facility(
    p_name             => 'TEST_USBD35_NoType_001',
    p_facility_type_id => NULL,
    p_lat              => 40.0,
    p_lon              => -8.0,
    p_is_intermodal    => 0
  );

  DBMS_OUTPUT.PUT_LINE('FAIL: function returned id ' || v_id || ' but should have raised ORA-20001');
EXCEPTION
  WHEN OTHERS THEN
    IF SQLCODE = -20001 THEN
      DBMS_OUTPUT.PUT_LINE('PASS: expected error caught: ' || SQLERRM);
ELSE
      DBMS_OUTPUT.PUT_LINE('FAIL: unexpected error: ' || SQLERRM);
END IF;
END;
/

--------------------------------------------------------------------------------
-- TEST 6: NULL lat -> ORA-20001
--------------------------------------------------------------------------------
DECLARE
v_id NUMBER;
BEGIN
  DBMS_OUTPUT.PUT_LINE('============================================================');
  DBMS_OUTPUT.PUT_LINE('TEST 6: NULL lat (expect ORA-20001)');
  DBMS_OUTPUT.PUT_LINE('============================================================');

  v_id := usbd35_register_facility(
    p_name             => 'TEST_USBD35_NoLat_001',
    p_facility_type_id => 1,
    p_lat              => NULL,
    p_lon              => -8.0,
    p_is_intermodal    => 0
  );

  DBMS_OUTPUT.PUT_LINE('FAIL: function returned id ' || v_id || ' but should have raised ORA-20001');
EXCEPTION
  WHEN OTHERS THEN
    IF SQLCODE = -20001 THEN
      DBMS_OUTPUT.PUT_LINE('PASS: expected error caught: ' || SQLERRM);
ELSE
      DBMS_OUTPUT.PUT_LINE('FAIL: unexpected error: ' || SQLERRM);
END IF;
END;
/

--------------------------------------------------------------------------------
-- TEST 7: NULL lon -> ORA-20001
--------------------------------------------------------------------------------
DECLARE
v_id NUMBER;
BEGIN
  DBMS_OUTPUT.PUT_LINE('============================================================');
  DBMS_OUTPUT.PUT_LINE('TEST 7: NULL lon (expect ORA-20001)');
  DBMS_OUTPUT.PUT_LINE('============================================================');

  v_id := usbd35_register_facility(
    p_name             => 'TEST_USBD35_NoLon_001',
    p_facility_type_id => 1,
    p_lat              => 40.0,
    p_lon              => NULL,
    p_is_intermodal    => 0
  );

  DBMS_OUTPUT.PUT_LINE('FAIL: function returned id ' || v_id || ' but should have raised ORA-20001');
EXCEPTION
  WHEN OTHERS THEN
    IF SQLCODE = -20001 THEN
      DBMS_OUTPUT.PUT_LINE('PASS: expected error caught: ' || SQLERRM);
ELSE
      DBMS_OUTPUT.PUT_LINE('FAIL: unexpected error: ' || SQLERRM);
END IF;
END;
/

--------------------------------------------------------------------------------
-- TEST 8: Happy path (insert ok) + validar que ficou na tabela
--------------------------------------------------------------------------------
DECLARE
v_id NUMBER;
  v_count NUMBER;
BEGIN
  DBMS_OUTPUT.PUT_LINE('============================================================');
  DBMS_OUTPUT.PUT_LINE('TEST 8: valid insert (expect SUCCESS)');
  DBMS_OUTPUT.PUT_LINE('============================================================');

  v_id := usbd35_register_facility(
    p_name             => 'TEST_USBD35_OK_001',
    p_facility_type_id => 1,
    p_lat              => 38.7167,
    p_lon              => -9.1333,
    p_is_intermodal    => 1
  );

SELECT COUNT(*)
INTO v_count
FROM FACILITY
WHERE facility_id = v_id
  AND UPPER(name) = 'TEST_USBD35_OK_001'
  AND facility_type_id = 1
  AND lat = 38.7167
  AND lon = -9.1333
  AND is_intermodal = 1;

IF v_count = 1 THEN
    DBMS_OUTPUT.PUT_LINE('PASS: inserted facility with ID=' || v_id || ' and validated in table');
ELSE
    DBMS_OUTPUT.PUT_LINE('FAIL: insert returned ID=' || v_id || ' but validation query did not match');
END IF;

EXCEPTION
  WHEN OTHERS THEN
    DBMS_OUTPUT.PUT_LINE('FAIL: unexpected error: ' || SQLERRM);
END;
/

--------------------------------------------------------------------------------
-- Limpeza final: apagar facilities de testes
--------------------------------------------------------------------------------
DECLARE
v_count NUMBER;
BEGIN
  DBMS_OUTPUT.PUT_LINE('');
  DBMS_OUTPUT.PUT_LINE('========================================');
  DBMS_OUTPUT.PUT_LINE('Final Cleanup');
  DBMS_OUTPUT.PUT_LINE('========================================');

DELETE FROM FACILITY
WHERE UPPER(name) LIKE 'TEST\_USBD35\_%' ESCAPE '\';

v_count := SQL%ROWCOUNT;

  IF v_count > 0 THEN
    DBMS_OUTPUT.PUT_LINE('Cleaned up ' || v_count || ' test facility/facilities');
ELSE
    DBMS_OUTPUT.PUT_LINE('No test facilities to clean up');
END IF;

COMMIT;

DBMS_OUTPUT.PUT_LINE('========================================');
  DBMS_OUTPUT.PUT_LINE('All tests completed!');
  DBMS_OUTPUT.PUT_LINE('========================================');
END;
/
