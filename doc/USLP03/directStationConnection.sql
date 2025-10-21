SELECT
    ls.segment_id,
    ls.line_id,
    l.name AS line_name,
    LEAST(fa.facility_id, fb.facility_id) AS station_a_id,
    GREATEST(fa.facility_id, fb.facility_id) AS station_b_id,
    CASE WHEN fa.facility_id < fb.facility_id THEN fa.name ELSE fb.name END AS station_a,
    CASE WHEN fa.facility_id < fb.facility_id THEN fb.name ELSE fa.name END AS station_b,
    ls.length_km
FROM LINE_SEGMENT ls
         JOIN LINE l ON l.line_id = ls.line_id
         JOIN (
    SELECT line_segment_id
    FROM LINE_SEGMENT_FACILITY
    GROUP BY line_segment_id
    HAVING COUNT(DISTINCT facility_id) = 2
) p ON p.line_segment_id = ls.segment_id
         JOIN LINE_SEGMENT_FACILITY a ON a.line_segment_id = ls.segment_id
         JOIN LINE_SEGMENT_FACILITY b ON b.line_segment_id = ls.segment_id AND b.facility_id > a.facility_id
         JOIN FACILITY fa ON fa.facility_id = a.facility_id
         JOIN FACILITY fb ON fb.facility_id = b.facility_id
         JOIN FACILITY_TYPE fta ON fta.facility_type_id = fa.facility_type_id
         JOIN FACILITY_TYPE ftb ON ftb.facility_type_id = fb.facility_type_id
WHERE ls.length_km IS NOT NULL
  AND fta.name = 'Train Station'
  AND ftb.name = 'Train Station'
ORDER BY ls.line_id, ls.position, station_a, station_b;