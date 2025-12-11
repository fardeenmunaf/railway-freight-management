------------------------------------------------------------
-- TESTE 1: Inserir segmento sem siding
------------------------------------------------------------
DECLARE
    v_id NUMBER;
BEGIN
    v_id := add_segment_with_siding(
            p_line_id         => 999,
            p_length_km       => 3000,
            p_electrif_type   => 1,
            p_gauge_id        => 999,
            p_max_weight_kg_m => 8000,
            p_speed_limit_kmh => 100,
            p_track_number    => 1,
            p_has_siding      => 0
            );

    DBMS_OUTPUT.PUT_LINE('TESTE 1 OK → Segmento sem siding criado. ID = ' || v_id);
END;
/

------------------------------------------------------------
-- TESTE 2: Inserir segmento com siding
------------------------------------------------------------
DECLARE
    v_id NUMBER;
BEGIN
    v_id := add_segment_with_siding(
            p_line_id                => 999,
            p_length_km              => 3500,
            p_electrif_type          => 1,
            p_gauge_id               => 999,
            p_max_weight_kg_m        => 8000,
            p_speed_limit_kmh        => 120,
            p_track_number           => 2,
            p_has_siding             => 1,
            p_siding_facility_id     => 999,
            p_siding_name            => 'SIDING_TEST',
            p_siding_capacity_wagons => 10
            );

    DBMS_OUTPUT.PUT_LINE('TESTE 2 OK → Segmento + siding criado. ID = ' || v_id);
END;
/

------------------------------------------------------------
-- TESTE 3: Erro esperado (siding obrigatório mas faltam dados)
------------------------------------------------------------
BEGIN
    DECLARE
        v_id NUMBER;
    BEGIN
        v_id := add_segment_with_siding(
                p_line_id         => 999,
                p_length_km       => 4000,
                p_electrif_type   => 1,
                p_gauge_id        => 999,
                p_max_weight_kg_m => 8000,
                p_speed_limit_kmh => 100,
                p_track_number    => 3,
                p_has_siding      => 1,
                p_siding_facility_id => NULL,
                p_siding_name        => NULL,
                p_siding_capacity_wagons => NULL
                );
    END;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('TESTE 3 OK → Erro esperado: ' || SQLERRM);
END;
/

------------------------------------------------------------
-- TESTE 4: Erro esperado (line_id inexistente)
------------------------------------------------------------
BEGIN
    DECLARE
        v_id NUMBER;
    BEGIN
        v_id := add_segment_with_siding(
                p_line_id         => 123456,
                p_length_km       => 3000,
                p_electrif_type   => 1,
                p_gauge_id        => 999,
                p_max_weight_kg_m => 8000,
                p_speed_limit_kmh => 100,
                p_track_number    => 1,
                p_has_siding      => 0
                );
    END;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('TESTE 4 OK → Erro esperado: ' || SQLERRM);
END;
/

------------------------------------------------------------
-- TESTE 5: Erro de nome duplicado no siding
------------------------------------------------------------
BEGIN
    DECLARE
        v_id NUMBER;
    BEGIN
        v_id := add_segment_with_siding(
                p_line_id                => 999,
                p_length_km              => 3600,
                p_electrif_type          => 1,
                p_gauge_id               => 999,
                p_max_weight_kg_m        => 8000,
                p_speed_limit_kmh        => 100,
                p_track_number           => 4,
                p_has_siding             => 1,
                p_siding_facility_id     => 999,
                p_siding_name            => 'SIDING_TEST',  -- já existe
                p_siding_capacity_wagons => 10
                );
    END;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('TESTE 5 OK → Erro duplicado: ' || SQLERRM);
END;
/
