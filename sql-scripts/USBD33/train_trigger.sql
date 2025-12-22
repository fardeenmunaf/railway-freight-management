alter session set current_schema = APPUSER;

-- Trigger para validar comprimento máximo do comboio ao adicionar freight
CREATE OR REPLACE TRIGGER trg_check_train_service_length
BEFORE INSERT OR UPDATE ON TRAIN_SERVICE_FREIGHT
FOR EACH ROW
DECLARE
    v_total_length NUMBER := 0;
    v_max_length CONSTANT NUMBER := 1000; -- máximo 1000 metros
    v_locomotives_length NUMBER := 0;
    v_wagons_length NUMBER := 0;
BEGIN
    -- Calcular comprimento total das locomotivas no serviço
    SELECT NVL(SUM(l.length_m), 0)
    INTO v_locomotives_length
    FROM SERVICE_LOCOMOTIVE sl
    JOIN LOCOMOTIVE l ON sl.locomotive_id = l.locomotive_id
    WHERE sl.service_id = :NEW.service_id;
    
    -- Calcular comprimento total dos vagões já existentes no serviço (excluindo o freight atual se for UPDATE)
    SELECT NVL(SUM(wm.length_m), 0)
    INTO v_wagons_length
    FROM TRAIN_SERVICE_FREIGHT tsf
    JOIN FREIGHT_COMPOSITION fc ON tsf.freight_id = fc.freight_id
    JOIN WAGON w ON fc.wagon_id = w.wagon_id
    JOIN WAGON_MODEL wm ON w.wagon_model = wm.wagon_model_id
    WHERE tsf.service_id = :NEW.service_id
    AND (UPDATING OR tsf.freight_id != :NEW.freight_id);
    
    -- Adicionar comprimento dos vagões do novo freight
    SELECT NVL(SUM(wm.length_m), 0)
    INTO v_total_length
    FROM FREIGHT_COMPOSITION fc
    JOIN WAGON w ON fc.wagon_id = w.wagon_id
    JOIN WAGON_MODEL wm ON w.wagon_model = wm.wagon_model_id
    WHERE fc.freight_id = :NEW.freight_id;
    
    -- Calcular comprimento total
    v_total_length := v_locomotives_length + v_wagons_length + v_total_length;
    
    -- Validar se excede o limite
    IF v_total_length > v_max_length THEN
        RAISE_APPLICATION_ERROR(-20001, 
            'Train service exceeds maximum length. ' ||
            'Current total: ' || ROUND(v_total_length, 2) || 'm, ' ||
            'Maximum allowed: ' || v_max_length || 'm ' ||
            '(Locomotives: ' || ROUND(v_locomotives_length, 2) || 'm, ' ||
            'Wagons: ' || ROUND(v_wagons_length + v_total_length - v_locomotives_length, 2) || 'm)');
    END IF;
END;
/

-- Procedure auxiliar para verificar comprimento de um serviço
CREATE OR REPLACE FUNCTION fn_get_train_service_length(p_service_id NUMBER)
RETURN NUMBER
IS
    v_total_length NUMBER := 0;
    v_locomotives_length NUMBER := 0;
    v_wagons_length NUMBER := 0;
BEGIN
    -- Comprimento das locomotivas
    SELECT NVL(SUM(l.length_m), 0)
    INTO v_locomotives_length
    FROM SERVICE_LOCOMOTIVE sl
    JOIN LOCOMOTIVE l ON sl.locomotive_id = l.locomotive_id
    WHERE sl.service_id = p_service_id;
    
    -- Comprimento dos vagões
    SELECT NVL(SUM(wm.length_m), 0)
    INTO v_wagons_length
    FROM TRAIN_SERVICE_FREIGHT tsf
    JOIN FREIGHT_COMPOSITION fc ON tsf.freight_id = fc.freight_id
    JOIN WAGON w ON fc.wagon_id = w.wagon_id
    JOIN WAGON_MODEL wm ON w.wagon_model = wm.wagon_model_id
    WHERE tsf.service_id = p_service_id;
    
    v_total_length := v_locomotives_length + v_wagons_length;
    
    RETURN v_total_length;
END;
/

-- View para consultar comprimento dos serviços
CREATE OR REPLACE VIEW v_train_service_lengths AS
SELECT 
    ts.service_id,
    ts.train_id,
    t.code as train_code,
    ts.departure_date,
    ts.status,
    fn_get_train_service_length(ts.service_id) as total_length_m,
    1000 - fn_get_train_service_length(ts.service_id) as remaining_capacity_m
FROM TRAIN_SERVICE ts
JOIN TRAIN t ON ts.train_id = t.train_id;







-- Consultar comprimentos atuais
SELECT * FROM v_train_service_lengths;

-- Verificar comprimento de um serviço específico
SELECT fn_get_train_service_length(1) FROM DUAL;