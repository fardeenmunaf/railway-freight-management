alter session set current_schema = APPUSER;

-- Trigger para validar comprimento máximo do comboio ao adicionar freight.
-- Compound trigger: as linhas guardam o service_id afetado e a validação corre no fim
-- da instrução (AFTER STATEMENT), quando a tabela já tem todas as linhas novas.
-- Isto evita o erro ORA-04091 (mutating table) de um trigger por linha que lê a própria tabela.
CREATE OR REPLACE TRIGGER trg_check_train_service_length
FOR INSERT OR UPDATE ON TRAIN_SERVICE_FREIGHT
COMPOUND TRIGGER

    TYPE t_service_ids IS TABLE OF NUMBER INDEX BY PLS_INTEGER;
    g_service_ids t_service_ids;
    c_max_length CONSTANT NUMBER := 1000; -- máximo 1000 metros

    AFTER EACH ROW IS
    BEGIN
        g_service_ids(g_service_ids.COUNT + 1) := :NEW.service_id;
    END AFTER EACH ROW;

    AFTER STATEMENT IS
        v_locomotives_length NUMBER;
        v_wagons_length      NUMBER;
        v_total_length       NUMBER;
    BEGIN
        FOR i IN 1 .. g_service_ids.COUNT LOOP
            -- Comprimento das locomotivas do serviço
            SELECT NVL(SUM(l.length_m), 0)
            INTO v_locomotives_length
            FROM SERVICE_LOCOMOTIVE sl
            JOIN LOCOMOTIVE l ON sl.locomotive_id = l.locomotive_id
            WHERE sl.service_id = g_service_ids(i);

            -- Comprimento de todos os vagões do serviço (já inclui as linhas novas)
            SELECT NVL(SUM(wm.length_m), 0)
            INTO v_wagons_length
            FROM TRAIN_SERVICE_FREIGHT tsf
            JOIN FREIGHT_COMPOSITION fc ON tsf.freight_id = fc.freight_id
            JOIN WAGON w ON fc.wagon_id = w.wagon_id
            JOIN WAGON_MODEL wm ON w.wagon_model = wm.wagon_model_id
            WHERE tsf.service_id = g_service_ids(i);

            v_total_length := v_locomotives_length + v_wagons_length;

            IF v_total_length > c_max_length THEN
                g_service_ids.DELETE;
                RAISE_APPLICATION_ERROR(-20001,
                    'Train service exceeds maximum length. ' ||
                    'Current total: ' || ROUND(v_total_length, 2) || 'm, ' ||
                    'Maximum allowed: ' || c_max_length || 'm ' ||
                    '(Locomotives: ' || ROUND(v_locomotives_length, 2) || 'm, ' ||
                    'Wagons: ' || ROUND(v_wagons_length, 2) || 'm)');
            END IF;
        END LOOP;
        g_service_ids.DELETE;
    END AFTER STATEMENT;

END trg_check_train_service_length;
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