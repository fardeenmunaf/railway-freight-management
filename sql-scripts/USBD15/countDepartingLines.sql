--COUNT DEPARTING LINES
WITH endpoints AS (
    SELECT
        ls.line_id,
        lsf.facility_id
    FROM line_segment ls
             JOIN (
        SELECT line_id, MIN(position) AS min_pos, MAX(position) AS max_pos
        FROM line_segment
        GROUP BY line_id
    ) mm
                  ON mm.line_id = ls.line_id
                      AND (ls.position = mm.min_pos OR ls.position = mm.max_pos)
             JOIN line_segment_facility lsf
                  ON lsf.line_segment_id = ls.segment_id
)
SELECT
    f.name AS endpoint_name,
    COUNT(DISTINCT e.line_id) AS lines_departing_count
FROM facility f
         LEFT JOIN endpoints e
                   ON e.facility_id = f.facility_id
WHERE f.facility_id = :endpoint_id
GROUP BY f.name;

-- Velocidade máxima da locomotiva
CREATE OR REPLACE FUNCTION get_locomotive_max_speed(p_locomotive_id IN NUMBER)
    RETURN NUMBER
    IS
    v_speed NUMBER;
BEGIN
    SELECT max_speed_kmh
    INTO   v_speed
    FROM   LOCOMOTIVE
    WHERE  locomotive_id = p_locomotive_id;

    RETURN v_speed;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END;

-- Velocidade permitida no segmento
CREATE OR REPLACE FUNCTION get_permitted_speed(
    p_locomotive_id IN NUMBER,
    p_segment_id    IN NUMBER DEFAULT NULL
) RETURN NUMBER
    IS
    v_loco NUMBER;
    v_seg  NUMBER;
BEGIN
    SELECT max_speed_kmh INTO v_loco
    FROM   LOCOMOTIVE
    WHERE  locomotive_id = p_locomotive_id;

    IF p_segment_id IS NULL THEN
        RETURN v_loco;
    END IF;

    BEGIN
        SELECT speed_limit_kmh INTO v_seg
        FROM   LINE_SEGMENT
        WHERE  segment_id = p_segment_id;

        RETURN LEAST(v_loco, v_seg);
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN v_loco;
    END;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END;

-- Teste 1, locomotiva válida
SELECT get_locomotive_max_speed(5621) AS loco_max FROM dual;

-- Teste 2, loc + segmento, usa o menor entre ambos
SELECT get_permitted_speed(5621, 1) AS permitted_speed FROM dual;

-- Teste 3, segmento inexistente, volta à velocidade da loco
SELECT get_permitted_speed(5621, 999999) AS permitted_speed FROM dual;

-- Teste 4, locomotiva inexistente, devolve NULL
SELECT get_permitted_speed(999999, 1) AS permitted_speed FROM dual;
