CREATE OR REPLACE FUNCTION fn_register_train_movement (
    p_service_id    IN TRAIN_STOP.service_id%TYPE,
    p_facility_id   IN TRAIN_STOP.facility_id%TYPE,
    p_parking_track IN TRAIN_STOP.parking_track%TYPE,
    p_position      IN TRAIN_STOP.position%TYPE,
    p_sched_arr     IN TRAIN_STOP.sched_arr%TYPE,
    p_sched_dep     IN TRAIN_STOP.sched_dep%TYPE,
    p_is_stop       IN NUMBER,  -- 1 = pára, 0 = passagem
    p_act_arr       IN TRAIN_STOP.act_arr%TYPE,
    p_act_dep       IN TRAIN_STOP.act_dep%TYPE
) RETURN NUMBER
    IS
    v_train_stop_id TRAIN_STOP.train_stop_id%TYPE;
BEGIN
    -- Se é stop, act_arr e act_dep obrigatórios e act_dep > act_arr
    IF p_is_stop = 1 THEN
        IF p_act_arr IS NULL OR p_act_dep IS NULL THEN
            RAISE_APPLICATION_ERROR(
                    -20001,
                    'Para movimentos com paragem, act_arr e act_dep são obrigatórios.'
            );
        END IF;

        IF p_act_dep <= p_act_arr THEN
            RAISE_APPLICATION_ERROR(
                    -20002,
                    'act_dep deve ser posterior a act_arr para movimentos com paragem.'
            );
        END IF;
    ELSE
        -- Se não é stop, act_dep não é permitido
        IF p_act_dep IS NOT NULL THEN
            RAISE_APPLICATION_ERROR(
                    -20003,
                    'Para movimentos sem paragem, act_dep não é permitido.'
            );
        END IF;
    END IF;

    -- FKs (service_id, facility_id, parking_track) validadas pelas constraints do schema.[file:1]

    INSERT INTO TRAIN_STOP (
        service_id,
        facility_id,
        parking_track,
        position,
        sched_arr,
        sched_dep,
        act_arr,
        act_dep,
        act_stop_time
    )
    VALUES (
               p_service_id,
               p_facility_id,
               p_parking_track,
               p_position,
               p_sched_arr,
               p_sched_dep,
               p_act_arr,
               p_act_dep,
               CASE
                   WHEN p_is_stop = 1 THEN
                       ROUND(
                               (CAST(p_act_dep AS DATE) - CAST(p_act_arr AS DATE)) * 24 * 60
                       )  -- minutos
                   ELSE
                       NULL
                   END
           )
    RETURNING train_stop_id INTO v_train_stop_id;

    RETURN v_train_stop_id;
END;
/
