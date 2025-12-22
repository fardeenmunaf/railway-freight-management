CREATE OR REPLACE FUNCTION fn_assign_locomotive_to_service (
    p_service_id     IN TRAIN_SERVICE.service_id%TYPE,
    p_locomotive_id  IN LOCOMOTIVE.locomotive_id%TYPE,
    p_position_no    IN SERVICE_LOCOMOTIVE.position_no%TYPE
) RETURN NUMBER IS
    v_cnt NUMBER;
BEGIN
    -- Verificar se o serviço existe
    SELECT COUNT(*) INTO v_cnt
    FROM TRAIN_SERVICE
    WHERE service_id = p_service_id;

    IF v_cnt = 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Service does not exist');
    END IF;

    -- Verificar se a locomotiva existe
    SELECT COUNT(*) INTO v_cnt
    FROM LOCOMOTIVE
    WHERE locomotive_id = p_locomotive_id;

    IF v_cnt = 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'Locomotive does not exist');
    END IF;

    -- Verificar se já existe essa combinação service–locomotive
    SELECT COUNT(*) INTO v_cnt
    FROM SERVICE_LOCOMOTIVE
    WHERE service_id = p_service_id
      AND locomotive_id = p_locomotive_id;

    IF v_cnt > 0 THEN
        RAISE_APPLICATION_ERROR(-20003, 'Locomotive already assigned to this service');
    END IF;

    -- Verificar se já existe essa posição para o serviço (uq_serv_loco_pos)
    SELECT COUNT(*) INTO v_cnt
    FROM SERVICE_LOCOMOTIVE
    WHERE service_id = p_service_id
      AND position_no = p_position_no;

    IF v_cnt > 0 THEN
        RAISE_APPLICATION_ERROR(-20004, 'Position already used for this service');
    END IF;

    -- Inserir associação
    INSERT INTO SERVICE_LOCOMOTIVE (service_id, locomotive_id, position_no)
    VALUES (p_service_id, p_locomotive_id, p_position_no);

    RETURN 1;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
