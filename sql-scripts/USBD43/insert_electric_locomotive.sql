CREATE OR REPLACE FUNCTION insert_electric_locomotive(
    p_operator_id IN NUMBER,
    p_locomotive_type IN NUMBER,
    p_name IN VARCHAR2,
    p_brand IN VARCHAR2,
    p_model IN VARCHAR2,
    p_year IN NUMBER,
    p_power_kw IN NUMBER,
    p_acc_ms2 IN NUMBER,
    p_length_m IN NUMBER,
    p_width_m IN NUMBER,
    p_height_m IN NUMBER,
    p_max_weight_t IN NUMBER,
    p_total_weight_t IN NUMBER,
    p_max_speed_kmh IN NUMBER,
    p_oper_speed_kmh IN NUMBER,
    p_num_bogies IN NUMBER,
    p_bogie_type IN VARCHAR2,
    p_traction_kn IN VARCHAR2,
    p_voltage IN NUMBER,
    p_frequency_hz IN NUMBER,
    p_start_facility IN NUMBER
)
RETURN NUMBER
IS
    v_loco_id NUMBER;
BEGIN
    ------------------------------------------------------------------
    -- VALIDATION (Electric locomotives MUST have voltage + freq)
    ------------------------------------------------------------------
    IF p_voltage IS NULL OR p_frequency_hz IS NULL THEN
        RAISE_APPLICATION_ERROR(-20001,
            'Electric locomotive requires voltage and frequency.');
    END IF;

    ------------------------------------------------------------------
    -- INSERT MAIN LOCOMOTIVE ROW USING EXPLICIT COLUMN LIST
    ------------------------------------------------------------------
    INSERT INTO LOCOMOTIVE(
        operator_id,
        locomotive_type,
        name,
        brand,
        model,
        year,
        power_kw,
        acceleration_ms2,
        length_m,
        width_m,
        height_m,
        max_weight_t,
        total_weight_t,
        max_speed_kmh,
        operational_speed_kmh,
        num_bogies,
        bogie_type,
        traction_kn,
        start_facility,
        created_at
    )
    VALUES(
        p_operator_id,
        p_locomotive_type,
        p_name,
        p_brand,
        p_model,
        p_year,
        p_power_kw,
        p_acc_ms2,
        p_length_m,
        p_width_m,
        p_height_m,
        p_max_weight_t,
        p_total_weight_t,
        p_max_speed_kmh,
        p_oper_speed_kmh,
        p_num_bogies,
        p_bogie_type,
        p_traction_kn,
        p_start_facility,
        CURRENT_TIMESTAMP
    )
    RETURNING locomotive_id INTO v_loco_id;

    ------------------------------------------------------------------
    -- INSERT ELECTRIC SPECIFIC DATA
    ------------------------------------------------------------------
    INSERT INTO LOCOMOTIVE_ELECTRIC(
        locomotive_id,
        voltage,
        frequency_hz
    )
    VALUES(
        v_loco_id,
        p_voltage,
        p_frequency_hz
    );

    RETURN v_loco_id;
END insert_electric_locomotive;
/