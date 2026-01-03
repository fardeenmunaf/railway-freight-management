-- =====================================================
-- USBD45: Add a new Line with Initial Segment
-- Author: Diogo Remelgado
-- Date: 2026-01-01
-- Description: Function to register a new line with its first segment.
--              A line MUST have at least one segment (Business Rule).
--              The operation is atomic: both Line and Segment are
--              inserted in the same transaction.
-- =====================================================

CREATE OR REPLACE FUNCTION register_line_with_segment(
    p_line_id IN NUMBER,
    p_line_name IN VARCHAR2,
    p_owner_id IN NUMBER,
    p_segment_id IN NUMBER,
    p_length_km IN NUMBER,
    p_electrif_type IN NUMBER,
    p_gauge_id IN NUMBER,
    p_max_weight_kg_m IN NUMBER,
    p_speed_limit_kmh IN NUMBER,
    p_track_number IN NUMBER,
    p_position IN NUMBER
) RETURN VARCHAR2 IS
    v_count NUMBER;
    v_owner_exists NUMBER;
    v_gauge_exists NUMBER;
BEGIN
    -- ==========================================
    -- VALIDATION: LINE DATA
    -- ==========================================

    -- Validation: line_id cannot be NULL
    IF p_line_id IS NULL THEN
        RETURN 'ERROR: Line ID cannot be NULL';
    END IF;

    -- Validation: line_name cannot be NULL or empty
    IF p_line_name IS NULL OR TRIM(p_line_name) IS NULL THEN
        RETURN 'ERROR: Line name cannot be NULL or empty';
    END IF;

    -- Validation: owner_id cannot be NULL
    IF p_owner_id IS NULL THEN
        RETURN 'ERROR: Owner ID cannot be NULL';
    END IF;

    -- Validation: owner_id must be positive
    IF p_owner_id <= 0 THEN
        RETURN 'ERROR: Owner ID must be a positive number';
    END IF;

    -- Check if owner exists in COMPANY table
    SELECT COUNT(*) INTO v_owner_exists
    FROM COMPANY
    WHERE company_id = p_owner_id;

    IF v_owner_exists = 0 THEN
        RETURN 'ERROR: Owner ID ' || p_owner_id || ' does not exist';
    END IF;

    -- Check if line_id already exists
    SELECT COUNT(*) INTO v_count
    FROM LINE
    WHERE line_id = p_line_id;

    IF v_count > 0 THEN
        RETURN 'ERROR: Line ID ' || p_line_id || ' already exists';
    END IF;

    -- Check if a line with the same name and owner already exists
    SELECT COUNT(*) INTO v_count
    FROM LINE
    WHERE UPPER(TRIM(name)) = UPPER(TRIM(p_line_name))
      AND owner_id = p_owner_id;

    IF v_count > 0 THEN
        RETURN 'ERROR: A line with name "' || p_line_name || '" already exists for this owner';
    END IF;

    -- ==========================================
    -- VALIDATION: SEGMENT DATA (MANDATORY)
    -- ==========================================

    -- Validation: segment_id cannot be NULL
    IF p_segment_id IS NULL THEN
        RETURN 'ERROR: Segment ID cannot be NULL (Line must have at least one segment)';
    END IF;

    -- Validation: length_km cannot be NULL
    IF p_length_km IS NULL THEN
        RETURN 'ERROR: Segment length cannot be NULL';
    END IF;

    -- Validation: length_km must be positive
    IF p_length_km <= 0 THEN
        RETURN 'ERROR: Segment length must be positive';
    END IF;

    -- Validation: electrif_type cannot be NULL
    IF p_electrif_type IS NULL THEN
        RETURN 'ERROR: Electrification type cannot be NULL';
    END IF;

    -- Validation: gauge_id cannot be NULL
    IF p_gauge_id IS NULL THEN
        RETURN 'ERROR: Gauge ID cannot be NULL';
    END IF;

    -- Check if gauge exists in GAUGE table
    SELECT COUNT(*) INTO v_gauge_exists
    FROM GAUGE
    WHERE gauge_id = p_gauge_id;

    IF v_gauge_exists = 0 THEN
        RETURN 'ERROR: Gauge ID ' || p_gauge_id || ' does not exist';
    END IF;

    -- Validation: max_weight_kg_m cannot be NULL
    IF p_max_weight_kg_m IS NULL THEN
        RETURN 'ERROR: Maximum weight per meter cannot be NULL';
    END IF;

    -- Validation: max_weight_kg_m must be positive
    IF p_max_weight_kg_m <= 0 THEN
        RETURN 'ERROR: Maximum weight per meter must be positive';
    END IF;

    -- Validation: speed_limit_kmh cannot be NULL
    IF p_speed_limit_kmh IS NULL THEN
        RETURN 'ERROR: Speed limit cannot be NULL';
    END IF;

    -- Validation: speed_limit_kmh must be positive
    IF p_speed_limit_kmh <= 0 THEN
        RETURN 'ERROR: Speed limit must be positive';
    END IF;

    -- Validation: track_number cannot be NULL
    IF p_track_number IS NULL THEN
        RETURN 'ERROR: Track number cannot be NULL';
    END IF;

    -- Validation: track_number must be positive
    IF p_track_number <= 0 THEN
        RETURN 'ERROR: Track number must be positive';
    END IF;

    -- Validation: position cannot be NULL
    IF p_position IS NULL THEN
        RETURN 'ERROR: Segment position cannot be NULL';
    END IF;

    -- Validation: position must be positive (usually starts at 1)
    IF p_position <= 0 THEN
        RETURN 'ERROR: Segment position must be positive';
    END IF;

    -- Check if segment_id already exists
    SELECT COUNT(*) INTO v_count
    FROM LINE_SEGMENT
    WHERE segment_id = p_segment_id;

    IF v_count > 0 THEN
        RETURN 'ERROR: Segment ID ' || p_segment_id || ' already exists';
    END IF;

    -- ==========================================
    -- ATOMIC INSERTION: LINE + SEGMENT
    -- ==========================================

    -- Insert the new line
    INSERT INTO LINE (line_id, name, owner_id)
    VALUES (p_line_id, TRIM(p_line_name), p_owner_id);

    -- Insert the initial segment (linked to the line)
    INSERT INTO LINE_SEGMENT (
        segment_id, line_id, length_km, electrif_type, gauge_id,
        max_weight_kg_m, speed_limit_kmh, track_number, position
    ) VALUES (
        p_segment_id, p_line_id, p_length_km, p_electrif_type, p_gauge_id,
        p_max_weight_kg_m, p_speed_limit_kmh, p_track_number, p_position
    );

    COMMIT;
    RETURN 'SUCCESS: Line "' || p_line_name || '" (ID ' || p_line_id ||
           ') registered with initial segment (ID ' || p_segment_id || ')';

EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN
        ROLLBACK;
        RETURN 'ERROR: Duplicate key violation - Line or Segment ID already exists';
    WHEN OTHERS THEN
        ROLLBACK;
        RETURN 'ERROR: ' || SQLERRM;
END register_line_with_segment;
/

