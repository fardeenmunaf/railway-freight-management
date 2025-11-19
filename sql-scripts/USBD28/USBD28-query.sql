-- =======================================================================
-- QUERY: Multi-Gauge Locomotives by Operator (COMPANY-centric version)
-- =======================================================================

SELECT
    l.locomotive_id,                         -- Unique identifier
    l.name AS locomotive_name,               -- Name
    l.brand,                                 -- Brand
    l.model,                                 -- Model
    lt.name AS locomotive_type,              -- Type (freight, passenger, etc)
    c.name AS operator_company_name,         -- Company name
    COUNT(lg.gauge_id) AS num_gauges,        -- Number of gauges
    LISTAGG(g.name, ', ')
        WITHIN GROUP (ORDER BY g.size_mm) AS supported_gauges,
    LISTAGG(g.size_mm, ', ')
        WITHIN GROUP (ORDER BY g.size_mm) AS gauge_sizes_mm
FROM
    LOCOMOTIVE l
    INNER JOIN LOCOMOTIVE_GAUGE lg ON l.locomotive_id = lg.locomotive_id
    INNER JOIN GAUGE g ON lg.gauge_id = g.gauge_id
    INNER JOIN LOCOMOTIVE_TYPE lt ON l.locomotive_type = lt.locomotive_type_id
    INNER JOIN "USER" u ON l.operator_id = u.user_id
    INNER JOIN COMPANY c ON u.company_id = c.company_id
WHERE
    c.company_id = 2     -- <-- 'Medway - Operador Ferroviário de Mercadorias, S.A' --->
GROUP BY
    l.locomotive_id,
    l.name,
    l.brand,
    l.model,
    lt.name,
    c.name
HAVING
    COUNT(lg.gauge_id) > 1
ORDER BY
    l.locomotive_id;
