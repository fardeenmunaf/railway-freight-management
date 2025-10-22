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
