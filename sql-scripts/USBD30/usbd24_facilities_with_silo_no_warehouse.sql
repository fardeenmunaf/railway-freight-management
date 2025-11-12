CREATE OR REPLACE FUNCTION fn_facilities_with_silo_no_warehouse
RETURN SYS_REFCURSOR
AS
  rc SYS_REFCURSOR;
BEGIN
OPEN rc FOR
SELECT
    f.facility_id,
    f.name          AS facility_name,
    ft.name         AS facility_type,
    fft.name        AS silo_feature
FROM FACILITY f
         JOIN FACILITY_TYPE ft
              ON ft.facility_type_id = f.facility_type_id
         JOIN FACILITY_FEATURE ff
              ON ff.facility_id = f.facility_id
         JOIN FACILITY_FEATURE_TYPE fft
              ON fft.feature_id = ff.feature_id
         LEFT JOIN WAREHOUSE w
                   ON w.facility_id = f.facility_id
WHERE UPPER(fft.name) LIKE '%SILO%'
  AND w.warehouse_id IS NULL
ORDER BY f.facility_id;
RETURN rc;
END;
