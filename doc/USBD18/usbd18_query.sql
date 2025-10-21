SELECT
    l.line_id,
    l.name AS line_name,
    CASE ls.track_number
        WHEN 1 THEN 'Single Track'
        WHEN 2 THEN 'Double Track'
        WHEN 3 THEN 'Triple track'
        WHEN 4 THEN 'Quadruple Track'
        ELSE 'Unknown (' || ls.track_number || ' tracks)'
    END AS track_type,
    COUNT(ls.segment_id) AS segment_count
FROM LINE l
INNER JOIN LINE_SEGMENT ls ON l.line_id = ls.line_id
WHERE l.line_id = :line_id
GROUP BY l.line_id, l.name, ls.track_number
ORDER BY l.line_id, ls.track_number;
