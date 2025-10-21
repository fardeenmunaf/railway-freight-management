WITH endpoints AS (
    SELECT line_segment_id, facility_id
    FROM LINE_SEGMENT_FACILITY
),
     pairs AS (
         SELECT
             ls.segment_id,
             ls.line_id,
             ls.position,
             ls.length_km,
             ls.speed_limit_kmh,
             LEAST(a.facility_id, b.facility_id) AS station_a_id,
             GREATEST(a.facility_id, b.facility_id) AS station_b_id
         FROM LINE_SEGMENT ls
                  JOIN (
             SELECT line_segment_id
             FROM endpoints
             GROUP BY line_segment_id
             HAVING COUNT(DISTINCT facility_id) = 2
         ) ok ON ok.line_segment_id = ls.segment_id
                  JOIN endpoints a ON a.line_segment_id = ls.segment_id
                  JOIN endpoints b ON b.line_segment_id = ls.segment_id AND b.facility_id > a.facility_id
     ),
     chosen AS (
         SELECT LEAST(:origin_id, :dest_id) AS station_a_id,
                GREATEST(:origin_id, :dest_id) AS station_b_id
         FROM dual
     ),
     filtered AS (
         SELECT
             p.segment_id,
             p.line_id,
             p.position,
             p.length_km,
             p.speed_limit_kmh,
             fa.name AS origin_station,
             fb.name AS destination_station
         FROM pairs p
                  JOIN chosen c
                       ON c.station_a_id = p.station_a_id
                           AND c.station_b_id = p.station_b_id
                  JOIN FACILITY fa ON fa.facility_id = :origin_id
                  JOIN FACILITY fb ON fb.facility_id = :dest_id
                  JOIN FACILITY_TYPE fta ON fta.facility_type_id = fa.facility_type_id
                  JOIN FACILITY_TYPE ftb ON ftb.facility_type_id = fb.facility_type_id
         WHERE fta.name = 'Train Station'
           AND ftb.name = 'Train Station'
     ),
     loco AS (
         SELECT max_speed_kmh
         FROM LOCOMOTIVE
         WHERE locomotive_id = :loco_id
     )
SELECT
    f.origin_station,
    f.destination_station,
    f.length_km AS distance_km,
    CASE
        WHEN l.max_speed_kmh IS NULL OR l.max_speed_kmh = 0 THEN NULL
        ELSE LEAST(l.max_speed_kmh, f.speed_limit_kmh)
        END AS locomotive_speed_kmh,
    CASE
        WHEN f.length_km = 0 THEN 0
        WHEN l.max_speed_kmh IS NULL OR l.max_speed_kmh = 0 THEN NULL
        ELSE f.length_km / LEAST(l.max_speed_kmh, f.speed_limit_kmh)
        END AS travel_time_hours
FROM filtered f
         LEFT JOIN loco l ON 1 = 1
ORDER BY f.position;