SELECT
    w.WAGON_ID,
    c.NAME AS operator,
    wt.NAME AS wagon_type,
    g.NAME AS gauge,
    g.SIZE_MM AS gauge_size,
    wm.PAYLOAD_T AS payload,
    wm.VOLUME_M3 AS volume,
    wm.TARE_T AS tare,
    wm.LENGTH_M || ' x ' || wm.WIDTH_M || ' x ' || wm.HEIGHT_M AS dimensions
FROM
    WAGON w
        JOIN WAGON_MODEL wm ON w.WAGON_MODEL = wm.WAGON_MODEL_ID
        JOIN WAGON_TYPE wt ON wm.WAGON_TYPE = wt.WAGON_TYPE_ID
        JOIN WAGON_GAUGE wg ON wm.WAGON_MODEL_ID = wg.WAGON_ID
        JOIN GAUGE g ON wg.GAUGE_ID = g.GAUGE_ID
        JOIN "USER" u ON w.OPERATOR_ID = u.USER_ID
        JOIN COMPANY c ON u.COMPANY_ID = c.COMPANY_ID
WHERE
    w.OPERATOR_ID = :operator_id
  AND wg.GAUGE_ID = :gauge_id
  AND wm.WAGON_TYPE = :wagon_type_id
ORDER BY
    wt.NAME, g.NAME, w.WAGON_ID;
