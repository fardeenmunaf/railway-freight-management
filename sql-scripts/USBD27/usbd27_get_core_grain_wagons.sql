CREATE OR REPLACE FUNCTION get_core_grain_wagons(
    p_start_date IN TIMESTAMP,
    p_end_date   IN TIMESTAMP
) RETURN SYS_REFCURSOR
AS
  rc SYS_REFCURSOR;
BEGIN
OPEN rc FOR
  WITH grain_services AS (
    SELECT DISTINCT ts.service_id
    FROM train_service ts
    JOIN service_locomotive sl
      ON sl.service_id = ts.service_id
    JOIN train_service_composition tsc
      ON tsc.service_locomotive_id = sl.service_locomotive_id
    JOIN wagon w
      ON w.wagon_id = tsc.wagon_id
    JOIN wagon_model wm
      ON wm.wagon_model_id = w.wagon_model
    JOIN wagon_type wt
      ON wt.wagon_type_id = wm.wagon_type
    WHERE ts.departure_date BETWEEN p_start_date AND p_end_date
      AND wt.name = 'Cereal wagon'
  ),
  svc_count AS (
    SELECT COUNT(*) AS n FROM grain_services
  )
SELECT
    w.wagon_id,
    wt.name AS wagon_type
FROM grain_services gs
         JOIN service_locomotive sl
              ON sl.service_id = gs.service_id
         JOIN train_service_composition tsc
              ON tsc.service_locomotive_id = sl.service_locomotive_id
         JOIN wagon w
              ON w.wagon_id = tsc.wagon_id
         JOIN wagon_model wm
              ON wm.wagon_model_id = w.wagon_model
         JOIN wagon_type wt
              ON wt.wagon_type_id = wm.wagon_type
WHERE wt.name = 'Cereal wagon'
GROUP BY w.wagon_id, wt.name
HAVING COUNT(DISTINCT gs.service_id) = (SELECT n FROM svc_count);

RETURN rc;
END;