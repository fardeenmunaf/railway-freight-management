-- =====================================================
-- USBD38: Register New Gauge Function
-- =====================================================
-- Purpose: Add a new gauge to the database with validation
-- Author: Diogo Remelgado
-- Date: 2026-01-01
-- =====================================================

-- Function to register a new gauge
CREATE OR REPLACE FUNCTION register_gauge(
    p_gauge_id IN NUMBER,
    p_name IN VARCHAR2,
    p_size_mm IN NUMBER
) RETURN VARCHAR2 IS
    v_count NUMBER;
BEGIN
    -- Validation 1: Check if gauge_id is NULL
    IF p_gauge_id IS NULL THEN
        RETURN 'ERROR: Gauge ID cannot be NULL';
    END IF;

    -- Validation 2: Check if name is NULL or empty
    IF p_name IS NULL OR TRIM(p_name) IS NULL THEN
        RETURN 'ERROR: Gauge name cannot be NULL or empty';
    END IF;

    -- Validation 3: Check if size_mm is NULL
    IF p_size_mm IS NULL THEN
        RETURN 'ERROR: Gauge size (mm) cannot be NULL';
    END IF;

    -- Validation 4: Check if size_mm is negative or zero
    IF p_size_mm <= 0 THEN
        RETURN 'ERROR: Gauge size must be a positive value (greater than 0 mm)';
    END IF;

    -- Validation 5: Check if size_mm is unreasonably large (e.g., > 3000mm)
    IF p_size_mm > 3000 THEN
        RETURN 'ERROR: Gauge size exceeds maximum realistic value (3000 mm)';
    END IF;

    -- Validation 6: Check if gauge_id already exists (duplicate primary key)
    SELECT COUNT(*)
    INTO v_count
    FROM GAUGE
    WHERE gauge_id = p_gauge_id;

    IF v_count > 0 THEN
        RETURN 'ERROR: Gauge ID ' || p_gauge_id || ' already exists (Duplicate Primary Key)';
    END IF;

    -- Validation 7: Check if a gauge with the same name and size already exists
    SELECT COUNT(*)
    INTO v_count
    FROM GAUGE
    WHERE UPPER(TRIM(name)) = UPPER(TRIM(p_name))
      AND size_mm = p_size_mm;

    IF v_count > 0 THEN
        RETURN 'ERROR: A gauge with name "' || p_name || '" and size ' || p_size_mm || 'mm already exists';
    END IF;

    -- All validations passed, insert the new gauge
    INSERT INTO GAUGE (gauge_id, name, size_mm)
    VALUES (p_gauge_id, TRIM(p_name), p_size_mm);

    COMMIT;

    RETURN 'SUCCESS: Gauge "' || p_name || '" (ID: ' || p_gauge_id || ', Size: ' || p_size_mm || 'mm) registered successfully';

EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN
        ROLLBACK;
        RETURN 'ERROR: Duplicate key violation - Gauge ID ' || p_gauge_id || ' already exists';
    WHEN OTHERS THEN
        ROLLBACK;
        RETURN 'ERROR: ' || SQLERRM;
END register_gauge;
/

