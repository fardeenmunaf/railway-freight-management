CREATE OR REPLACE FUNCTION register_wagon_full (
    p_model_name      IN WAGON_MODEL.name%TYPE,
    p_wagon_type      IN WAGON_MODEL.wagon_type%TYPE,
    p_constructor     IN WAGON_MODEL.constructor%TYPE,
    p_number_bogies   IN WAGON_MODEL.number_bogies%TYPE,
    p_bogies          IN WAGON_MODEL.bogies%TYPE,
    p_tare_t          IN WAGON_MODEL.tare_t%TYPE,
    p_payload_t       IN WAGON_MODEL.payload_t%TYPE,
    p_volume_m3       IN WAGON_MODEL.volume_m3%TYPE,
    p_length_m        IN WAGON_MODEL.length_m%TYPE,
    p_width_m         IN WAGON_MODEL.width_m%TYPE,
    p_height_m        IN WAGON_MODEL.height_m%TYPE,
    p_max_speed_kmh   IN WAGON_MODEL.max_speed_kmh%TYPE,
    p_operator_id     IN WAGON.operator_id%TYPE,
    p_service_year    IN WAGON.service_year%TYPE,
    p_gauges          IN GAUGE.gauge_id%TYPE,
    p_start_facility_id IN WAGON.START_FACILITY%TYPE
)
RETURN NUMBER
IS
    v_model_id  WAGON_MODEL.wagon_model_id%TYPE;
    v_wagon_id  WAGON.wagon_id%TYPE;
BEGIN
    ------------------------------------------------------------------
    -- 1. Obter ou criar o WAGON_MODEL
    ------------------------------------------------------------------
    BEGIN
        SELECT wagon_model_id
        INTO   v_model_id
        FROM   WAGON_MODEL
        WHERE  name          = p_model_name
        AND    wagon_type    = p_wagon_type
        AND    constructor   = p_constructor
        AND    number_bogies = p_number_bogies
        AND    bogies        = p_bogies
        AND    tare_t        = p_tare_t
        AND    payload_t     = p_payload_t
        AND    volume_m3     = p_volume_m3
        AND    length_m      = p_length_m
        AND    width_m       = p_width_m
        AND    height_m      = p_height_m
        AND    max_speed_kmh = p_max_speed_kmh;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            INSERT INTO WAGON_MODEL (
                name, wagon_type, constructor, number_bogies, bogies,
                tare_t, payload_t, volume_m3, length_m, width_m, height_m, max_speed_kmh
            )
            VALUES (
                p_model_name, p_wagon_type, p_constructor, p_number_bogies, p_bogies,
                p_tare_t, p_payload_t, p_volume_m3, p_length_m, p_width_m, p_height_m, p_max_speed_kmh
            )
            RETURNING wagon_model_id INTO v_model_id;
    END;

    ------------------------------------------------------------------
    -- 2. Criar o WAGON físico para esse modelo
    ------------------------------------------------------------------
    INSERT INTO WAGON (wagon_model, operator_id, service_year,start_facility)
    VALUES (v_model_id, p_operator_id, p_service_year , p_start_facility_id)
    RETURNING wagon_id INTO v_wagon_id;

    ------------------------------------------------------------------
    -- 3. Associar o MODEL ao GAUGE em WAGON_GAUGE
    --    (usa wagon_model_id na coluna wagon_id, como pediste)
    ------------------------------------------------------------------
    BEGIN
        INSERT INTO WAGON_GAUGE (wagon_id, gauge_id)
        VALUES (v_model_id, p_gauges);
    EXCEPTION
        WHEN DUP_VAL_ON_INDEX THEN
            -- Já existe esta combinação wagon_model_id + gauge_id, ignora
            NULL;
    END;

    ------------------------------------------------------------------
    -- 4. Devolver o ID do wagon físico
    ------------------------------------------------------------------
    RETURN v_wagon_id;

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('ERROR in register_wagon_full_nodup: ' || SQLERRM);
        RAISE;
END;
/
