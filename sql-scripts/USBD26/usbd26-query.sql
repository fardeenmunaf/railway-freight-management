/*
    USBD26 - List of wagons that were not used in any train in a given period.

    Parameters:
      :start_date (Format: YYYY-MM-DD, e.g., '2025-10-01')
      :end_date   (Format: YYYY-MM-DD, e.g., '2025-10-05')
*/

SELECT
    w.wagon_id,
    wm.name AS wagon_model_name,
    wt.name AS wagon_type_name,
    w.service_year,
    c.short_name AS operator_code
FROM
    WAGON w
    JOIN WAGON_MODEL wm ON w.wagon_model = wm.wagon_model_id
    JOIN WAGON_TYPE wt ON wm.wagon_type = wt.wagon_type_id
    JOIN "USER" u ON w.operator_id = u.user_id
    JOIN COMPANY c ON u.company_id = c.company_id
WHERE
    w.wagon_id NOT IN ( -- NOT IN: Exceto Vagões que foram usados em serviços nesse intervalo
        -- Subquery: Encontrar vagões usados em serviços nesse intervalo
        SELECT
            tsc.wagon_id
        FROM
            TRAIN_SERVICE ts
            -- O caminho para chegar ao vagão no esquema é:
            -- Service -> Service_Locomotive -> Composition -> Wagon
            JOIN SERVICE_LOCOMOTIVE sl ON ts.service_id = sl.service_id
            JOIN TRAIN_SERVICE_COMPOSITION tsc ON sl.service_locomotive_id = tsc.service_locomotive_id
        WHERE
            -- Logica de datas simplificada:
            -- >= Data Inicio (00:00:00)
            -- <  Data Fim + 1 dia (garante que apanha o dia 'end_date' completo)
            ts.departure_date >= TO_DATE(:start_date, 'YYYY-MM-DD')
            AND ts.departure_date < TO_DATE(:end_date, 'YYYY-MM-DD') + 1
    )
ORDER BY
    w.wagon_id;