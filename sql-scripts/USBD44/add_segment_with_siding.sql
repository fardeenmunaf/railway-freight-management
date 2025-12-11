CREATE OR REPLACE FUNCTION add_segment_with_siding (
    p_line_id                IN LINE.line_id%TYPE,
    p_length_km              IN LINE_SEGMENT.length_km%TYPE,
    p_electrif_type          IN LINE_SEGMENT.electrif_type%TYPE,
    p_gauge_id               IN LINE_SEGMENT.gauge_id%TYPE,
    p_max_weight_kg_m        IN LINE_SEGMENT.max_weight_kg_m%TYPE,
    p_speed_limit_kmh        IN LINE_SEGMENT.speed_limit_kmh%TYPE,
    p_track_number           IN LINE_SEGMENT.track_number%TYPE,
    p_has_siding             IN NUMBER,
    p_siding_facility_id     IN PARKING_TRACK.facility_id%TYPE     DEFAULT NULL,
    p_siding_name            IN PARKING_TRACK.name%TYPE            DEFAULT NULL,
    p_siding_capacity_wagons IN PARKING_TRACK.capacity_wagons%TYPE DEFAULT NULL
) RETURN NUMBER
IS
    v_segment_id    LINE_SEGMENT.segment_id%TYPE;
    v_next_position LINE_SEGMENT.position%TYPE;
    v_dummy         NUMBER;
BEGIN
    -- validar campos obrigatórios do segmento
    IF p_line_id IS NULL
       OR p_length_km IS NULL
       OR p_electrif_type IS NULL
       OR p_gauge_id IS NULL
       OR p_max_weight_kg_m IS NULL
       OR p_speed_limit_kmh IS NULL
       OR p_track_number IS NULL THEN
        RAISE_APPLICATION_ERROR(-20000, 'Required segment attribute is null');
END IF;

    -- validar existência da linha
BEGIN
SELECT 1
INTO v_dummy
FROM LINE
WHERE line_id = p_line_id;
EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20001, 'Line does not exist');
END;

    -- validar existência do gauge
BEGIN
SELECT 1
INTO v_dummy
FROM GAUGE
WHERE gauge_id = p_gauge_id;
EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20002, 'Gauge does not exist');
END;

    -- posição seguinte na linha
SELECT NVL(MAX(position), 0) + 1
INTO v_next_position
FROM LINE_SEGMENT
WHERE line_id = p_line_id;

-- se tiver siding, validar dados do siding
IF p_has_siding = 1 THEN
        IF p_siding_facility_id IS NULL
           OR p_siding_name IS NULL
           OR p_siding_capacity_wagons IS NULL THEN
            RAISE_APPLICATION_ERROR(
                -20003,
                'Segment indicates siding but siding data is incomplete'
            );
END IF;

        -- facility do siding tem de existir
BEGIN
SELECT 1
INTO v_dummy
FROM FACILITY
WHERE facility_id = p_siding_facility_id;
EXCEPTION
            WHEN NO_DATA_FOUND THEN
                RAISE_APPLICATION_ERROR(-20004, 'Siding facility does not exist');
END;

        -- nome do siding único por linha
BEGIN
SELECT 1
INTO v_dummy
FROM PARKING_TRACK
WHERE line_id = p_line_id
  AND UPPER(name) = UPPER(p_siding_name);

RAISE_APPLICATION_ERROR(
                -20005,
                'Siding name already exists for this line'
            );
EXCEPTION
            WHEN NO_DATA_FOUND THEN
                NULL;
END;
END IF;

    -- inserir segmento
INSERT INTO LINE_SEGMENT (
    line_id,
    length_km,
    electrif_type,
    gauge_id,
    max_weight_kg_m,
    speed_limit_kmh,
    track_number,
    position
) VALUES (
             p_line_id,
             p_length_km,
             p_electrif_type,
             p_gauge_id,
             p_max_weight_kg_m,
             p_speed_limit_kmh,
             p_track_number,
             v_next_position
         )
    RETURNING segment_id INTO v_segment_id;

-- se tiver siding, inserir PARKING_TRACK
IF p_has_siding = 1 THEN
        INSERT INTO PARKING_TRACK (
            line_id,
            facility_id,
            name,
            capacity_wagons
        ) VALUES (
            p_line_id,
            p_siding_facility_id,
            p_siding_name,
            p_siding_capacity_wagons
        );
END IF;

RETURN v_segment_id;

EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN
        RAISE_APPLICATION_ERROR(-20006, 'Duplicate segment or siding detected');
END add_segment_with_siding;
/
