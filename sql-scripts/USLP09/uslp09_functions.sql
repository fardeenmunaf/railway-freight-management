-- ============================================================================
-- USLP09 - Train Assembly - PL/SQL Functions
-- ============================================================================
-- As Traffic Manager, I want to be able to assemble and assign a train to a route.
-- List locomotives and wagons distinguishing between:
-- 1. In transit (show final destination)
-- 2. Parked (show station, ordered by distance from route start)
-- ============================================================================

-- Function 1: Get Available Locomotives
-- Returns locomotives with their status (PARKED or IN_TRANSIT) and location
CREATE OR REPLACE FUNCTION fn_get_available_locomotives(
    p_departure_facility_id IN FACILITY.facility_id%TYPE
) RETURN SYS_REFCURSOR
IS
    v_cursor SYS_REFCURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT
            l.locomotive_id,
            l.brand || ' ' || l.model AS name,
            l.brand,
            l.model,
            l.year,
            l.power_kw,
            l.max_speed_kmh,
            l.operational_speed_kmh,
            l.total_weight_t,
            l.length_m,
            lt.name AS locomotive_type,
            f.name AS current_location,
            f.facility_id AS location_id,
            f.lat AS location_lat,
            f.lon AS location_lon,
            -- Calculate approximate distance from departure point (in km)
            ROUND(
                SQRT(
                    POWER((f.lat - dep.lat) * 111, 2) +
                    POWER((f.lon - dep.lon) * 111 * COS(f.lat * 3.14159 / 180), 2)
                ), 2
            ) AS distance_from_departure_km,
            -- Determine status: PARKED or IN_TRANSIT
            CASE
                WHEN EXISTS (
                    SELECT 1
                    FROM SERVICE_LOCOMOTIVE sl
                    INNER JOIN TRAIN_SERVICE ts ON sl.service_id = ts.service_id
                    WHERE sl.locomotive_id = l.locomotive_id
                    AND ts.status IN ('SCHEDULED', 'DEPARTED')
                    AND ts.departure_date <= CURRENT_TIMESTAMP
                    AND ts.arrival_date >= CURRENT_TIMESTAMP
                ) THEN 'IN_TRANSIT'
                ELSE 'PARKED'
            END AS status,
            -- Get destination facility if in transit
            (SELECT fd.name
             FROM SERVICE_LOCOMOTIVE sl
             INNER JOIN TRAIN_SERVICE ts ON sl.service_id = ts.service_id
             INNER JOIN TRAIN t ON ts.train_id = t.train_id
             INNER JOIN FACILITY fd ON t.destination = fd.facility_id
             WHERE sl.locomotive_id = l.locomotive_id
             AND ts.status IN ('SCHEDULED', 'DEPARTED')
             AND ts.departure_date <= CURRENT_TIMESTAMP
             AND ts.arrival_date >= CURRENT_TIMESTAMP
             AND ROWNUM = 1) AS transit_destination,
            -- Get arrival date if in transit
            (SELECT ts.arrival_date
             FROM SERVICE_LOCOMOTIVE sl
             INNER JOIN TRAIN_SERVICE ts ON sl.service_id = ts.service_id
             WHERE sl.locomotive_id = l.locomotive_id
             AND ts.status IN ('SCHEDULED', 'DEPARTED')
             AND ts.departure_date <= CURRENT_TIMESTAMP
             AND ts.arrival_date >= CURRENT_TIMESTAMP
             AND ROWNUM = 1) AS transit_arrival_date,
            -- Get operator/company name
            c.name AS operator_company
        FROM LOCOMOTIVE l
        INNER JOIN LOCOMOTIVE_TYPE lt ON l.locomotive_type = lt.locomotive_type_id
        INNER JOIN FACILITY f ON l.start_facility = f.facility_id
        INNER JOIN "USER" u ON l.operator_id = u.user_id
        INNER JOIN COMPANY c ON u.company_id = c.company_id
        CROSS JOIN FACILITY dep
        WHERE dep.facility_id = p_departure_facility_id
        ORDER BY
            -- First show PARKED locomotives, then IN_TRANSIT
            CASE
                WHEN EXISTS (
                    SELECT 1
                    FROM SERVICE_LOCOMOTIVE sl
                    INNER JOIN TRAIN_SERVICE ts ON sl.service_id = ts.service_id
                    WHERE sl.locomotive_id = l.locomotive_id
                    AND ts.status IN ('SCHEDULED', 'DEPARTED')
                ) THEN 1
                ELSE 0
            END,
            -- Then order by distance (closer first)
            distance_from_departure_km;

    RETURN v_cursor;
END fn_get_available_locomotives;
/

-- Function 2: Get Available Wagons
-- Returns wagons with their status and location
CREATE OR REPLACE FUNCTION fn_get_available_wagons(
    p_departure_facility_id IN FACILITY.facility_id%TYPE
) RETURN SYS_REFCURSOR
    IS
    v_cursor SYS_REFCURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT
            w.wagon_id,
            wm.name AS model_name,
            wt.name AS wagon_type,
            wm.constructor,
            w.service_year,
            wm.payload_t,
            wm.tare_t,
            wm.volume_m3,
            wm.length_m,
            wm.width_m,
            wm.height_m,
            wm.max_speed_kmh,
            f.name AS current_location,
            f.facility_id AS location_id,
            f.lat AS location_lat,
            f.lon AS location_lon,
            -- Calculate distance from departure
            ROUND(
                    SQRT(
                            POWER((f.lat - dep.lat) * 111, 2) +
                            POWER((f.lon - dep.lon) * 111 * COS(f.lat * 3.14159 / 180), 2)
                    ), 2
            ) AS distance_from_departure_km,
            -- Determine status
            CASE
                WHEN pq.status = 'IN_TRANSIT' THEN 'IN_TRANSIT'
                WHEN pq.status = 'WAITING' THEN 'PARKED'
                WHEN pq.status IS NULL THEN 'PARKED'
                ELSE pq.status
                END AS status,
            -- Parking track information
            pt.name AS parking_track_name,
            pt.parking_track_id,
            pq.position_no AS parking_position,
            -- Get operator company
            c.name AS operator_company,
            -- Check if wagon is part of a freight in transit
            (SELECT COUNT(*)
             FROM FREIGHT_COMPOSITION fc
                      INNER JOIN TRAIN_SERVICE_FREIGHT tsf ON fc.freight_id = tsf.freight_id
                      INNER JOIN TRAIN_SERVICE ts ON tsf.service_id = ts.service_id
             WHERE fc.wagon_id = w.wagon_id
               AND ts.status IN ('SCHEDULED', 'DEPARTED')
            ) AS in_active_freight
        FROM WAGON w
                 INNER JOIN WAGON_MODEL wm ON w.wagon_model = wm.wagon_model_id
                 INNER JOIN WAGON_TYPE wt ON wm.wagon_type = wt.wagon_type_id
                 INNER JOIN FACILITY f ON w.start_facility = f.facility_id
                 INNER JOIN "USER" u ON w.operator_id = u.user_id
                 INNER JOIN COMPANY c ON u.company_id = c.company_id
                 LEFT JOIN PARKING_QUEUE pq ON w.wagon_id = pq.wagon_id
                 LEFT JOIN PARKING_TRACK pt ON pq.parking_track_id = pt.parking_track_id
                 CROSS JOIN FACILITY dep
        WHERE dep.facility_id = p_departure_facility_id
          -- Only show wagons that are available (not in active freight)
          AND NOT EXISTS (
            SELECT 1
            FROM FREIGHT_COMPOSITION fc
                     INNER JOIN TRAIN_SERVICE_FREIGHT tsf ON fc.freight_id = tsf.freight_id
                     INNER JOIN TRAIN_SERVICE ts ON tsf.service_id = ts.service_id
            WHERE fc.wagon_id = w.wagon_id
              AND ts.status IN ('SCHEDULED', 'DEPARTED')
        )
        ORDER BY
            -- First PARKED, then IN_TRANSIT
            CASE
                WHEN pq.status = 'IN_TRANSIT' THEN 1
                ELSE 0
                END,
            -- Then by distance
            distance_from_departure_km;

    RETURN v_cursor;
END fn_get_available_wagons;
/

-- Function 3: Assemble Train Composition
-- Adds locomotives to a train service
-- Note: Wagons are assigned through FREIGHT_COMPOSITION when freight is added
CREATE OR REPLACE FUNCTION fn_assemble_train_composition(
    p_service_id IN TRAIN_SERVICE.service_id%TYPE,
    p_locomotive_ids IN VARCHAR2 -- Comma-separated IDs: '1,2,3'
) RETURN NUMBER
    IS
    v_count NUMBER;
    v_position NUMBER := 1;
    v_locomotive_count NUMBER := 0;
BEGIN
    -- Validate train service exists and is in appropriate status
    SELECT COUNT(*) INTO v_count
    FROM TRAIN_SERVICE
    WHERE service_id = p_service_id
      AND status IN ('SCHEDULED', 'DEPARTED');

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Train service does not exist or has invalid status');
    END IF;

    -- Check if service already has locomotives
    SELECT COUNT(*) INTO v_count
    FROM SERVICE_LOCOMOTIVE
    WHERE service_id = p_service_id;

    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'Train service already has locomotives assigned');
    END IF;

    -- Add locomotives (they go at the front)
    IF p_locomotive_ids IS NOT NULL AND LENGTH(TRIM(p_locomotive_ids)) > 0 THEN
        FOR loco_rec IN (
            SELECT TO_NUMBER(TRIM(REGEXP_SUBSTR(p_locomotive_ids, '[^,]+', 1, LEVEL))) AS loco_id
            FROM DUAL
            CONNECT BY LEVEL <= REGEXP_COUNT(p_locomotive_ids, ',') + 1
            ) LOOP
                -- Validate locomotive exists
                SELECT COUNT(*) INTO v_count
                FROM LOCOMOTIVE
                WHERE locomotive_id = loco_rec.loco_id;

                IF v_count = 0 THEN
                    RAISE_APPLICATION_ERROR(-20003, 'Locomotive ' || loco_rec.loco_id || ' does not exist');
                END IF;

                -- Check if locomotive has a schedule conflict (temporal overlap)
                -- A locomotive can be in multiple services as long as schedules don't overlap
                DECLARE
                    v_new_departure TIMESTAMP;
                    v_new_arrival TIMESTAMP;
                BEGIN
                    -- Get the schedule of the new service
                    SELECT departure_date, arrival_date
                    INTO v_new_departure, v_new_arrival
                    FROM TRAIN_SERVICE
                    WHERE service_id = p_service_id;

                    -- Check for temporal overlap with other active services
                    -- Two services overlap if: new_start < existing_end AND new_end > existing_start
                    SELECT COUNT(*) INTO v_count
                    FROM SERVICE_LOCOMOTIVE sl
                    INNER JOIN TRAIN_SERVICE ts ON sl.service_id = ts.service_id
                    WHERE sl.locomotive_id = loco_rec.loco_id
                      AND ts.status IN ('SCHEDULED', 'DEPARTED')
                      AND sl.service_id != p_service_id
                      AND v_new_departure < ts.arrival_date
                      AND v_new_arrival > ts.departure_date;

                    IF v_count > 0 THEN
                        RAISE_APPLICATION_ERROR(-20004, 'Locomotive ' || loco_rec.loco_id ||
                            ' has a schedule conflict with another service (overlapping times)');
                    END IF;
                END;

                -- Insert locomotive
                INSERT INTO SERVICE_LOCOMOTIVE (service_id, locomotive_id, position_no)
                VALUES (p_service_id, loco_rec.loco_id, v_position);

                v_position := v_position + 1;
                v_locomotive_count := v_locomotive_count + 1;
            END LOOP;
    END IF;

    -- Validate that at least one locomotive was added
    IF v_locomotive_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20005, 'At least one locomotive must be assigned');
    END IF;

    -- Note: Wagons are typically added through FREIGHT_COMPOSITION, not directly
    -- This function focuses on locomotive assignment
    -- Wagon assignment happens when freight is assigned to the service

    -- No COMMIT here: the caller owns the transaction, so the whole train assembly
    -- (train, service, stops, locomotives, wagons) is saved or discarded together.
    -- If this function raises an error, Oracle undoes the work it did.
    RETURN v_locomotive_count; -- Return number of locomotives added
END fn_assemble_train_composition;
/

-- Function 4: Validate Train Composition
-- Checks if the train composition is valid and returns diagnostic information
CREATE OR REPLACE FUNCTION fn_validate_train_composition(
    p_service_id IN TRAIN_SERVICE.service_id%TYPE
) RETURN VARCHAR2
    IS
    v_total_power_kw NUMBER := 0;
    v_total_weight_t NUMBER := 0;
    v_max_speed_kmh NUMBER := 9999;
    v_min_loco_speed NUMBER := 9999;
    v_num_locomotives NUMBER := 0;
    v_num_wagons NUMBER := 0;
    v_power_to_weight_ratio NUMBER;
    v_result VARCHAR2(4000);
    v_warnings VARCHAR2(2000) := '';
BEGIN
    -- Check if service exists
    BEGIN
        SELECT 1 INTO v_num_locomotives
        FROM TRAIN_SERVICE
        WHERE service_id = p_service_id;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN 'ERROR: Train service does not exist';
    END;

    -- Count and calculate locomotive power and weight
    SELECT
        NVL(COUNT(*), 0),
        NVL(SUM(l.power_kw), 0),
        NVL(SUM(l.total_weight_t), 0),
        NVL(MIN(l.max_speed_kmh), 9999)
    INTO
        v_num_locomotives,
        v_total_power_kw,
        v_total_weight_t,
        v_min_loco_speed
    FROM SERVICE_LOCOMOTIVE sl
             INNER JOIN LOCOMOTIVE l ON sl.locomotive_id = l.locomotive_id
    WHERE sl.service_id = p_service_id;

    -- Check if locomotives are assigned
    IF v_num_locomotives = 0 THEN
        RETURN 'ERROR: No locomotives assigned to this service';
    END IF;

    -- Calculate total weight including wagons/freight
    DECLARE
        v_freight_weight NUMBER := 0;
    BEGIN
        SELECT NVL(SUM(fc.weight_kg), 0) / 1000 -- Convert kg to tonnes
        INTO v_freight_weight
        FROM TRAIN_SERVICE_FREIGHT tsf
                 INNER JOIN FREIGHT_COMPOSITION fc ON tsf.freight_id = fc.freight_id
        WHERE tsf.service_id = p_service_id;

        v_total_weight_t := v_total_weight_t + v_freight_weight;

        -- Count wagons
        SELECT NVL(COUNT(DISTINCT fc.wagon_id), 0)
        INTO v_num_wagons
        FROM TRAIN_SERVICE_FREIGHT tsf
                 INNER JOIN FREIGHT_COMPOSITION fc ON tsf.freight_id = fc.freight_id
        WHERE tsf.service_id = p_service_id;
    END;

    -- Calculate minimum speed (limited by slowest component)
    BEGIN
        SELECT MIN(wm.max_speed_kmh)
        INTO v_max_speed_kmh
        FROM TRAIN_SERVICE_FREIGHT tsf
                 INNER JOIN FREIGHT_COMPOSITION fc ON tsf.freight_id = fc.freight_id
                 INNER JOIN WAGON w ON fc.wagon_id = w.wagon_id
                 INNER JOIN WAGON_MODEL wm ON w.wagon_model = wm.wagon_model_id
        WHERE tsf.service_id = p_service_id;

        v_max_speed_kmh := LEAST(v_max_speed_kmh, v_min_loco_speed);
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            v_max_speed_kmh := v_min_loco_speed;
    END;

    -- Calculate power-to-weight ratio (kW per tonne)
    IF v_total_weight_t > 0 THEN
        v_power_to_weight_ratio := ROUND(v_total_power_kw / v_total_weight_t, 2);
    ELSE
        v_power_to_weight_ratio := v_total_power_kw;
    END IF;

    -- Validation checks
    IF v_total_power_kw < 1000 THEN
        v_warnings := v_warnings || 'WARNING: Low total power (< 1000 kW). ';
    END IF;

    IF v_total_weight_t > 0 AND v_power_to_weight_ratio < 15 THEN
        v_warnings := v_warnings || 'WARNING: Low power-to-weight ratio (< 15 kW/t). ';
    END IF;

    IF v_num_locomotives > 3 THEN
        v_warnings := v_warnings || 'INFO: Multiple locomotives may require special handling. ';
    END IF;

    IF v_num_wagons = 0 THEN
        v_warnings := v_warnings || 'INFO: No freight/wagons assigned yet. ';
    END IF;

    IF v_max_speed_kmh < 50 THEN
        v_warnings := v_warnings || 'WARNING: Maximum speed very low (< 50 km/h). ';
    END IF;

    -- Build result string
    IF LENGTH(v_warnings) = 0 THEN
        v_result := 'OK: Composition is valid';
    ELSE
        v_result := v_warnings;
    END IF;

    v_result := v_result ||
                ' | Locomotives: ' || v_num_locomotives ||
                ' | Wagons: ' || v_num_wagons ||
                ' | Power: ' || ROUND(v_total_power_kw, 0) || ' kW' ||
                ' | Weight: ' || ROUND(v_total_weight_t, 1) || ' t' ||
                ' | P/W Ratio: ' || v_power_to_weight_ratio || ' kW/t' ||
                ' | Max Speed: ' || ROUND(v_max_speed_kmh, 0) || ' km/h';

    RETURN v_result;

EXCEPTION
    WHEN OTHERS THEN
        RETURN 'ERROR: ' || SQLERRM;
END fn_validate_train_composition;
/

-- ============================================================================
-- Success message
-- ============================================================================
BEGIN
    DBMS_OUTPUT.PUT_LINE('========================================');
    DBMS_OUTPUT.PUT_LINE('USLP09 Functions created successfully!');
    DBMS_OUTPUT.PUT_LINE('========================================');
    DBMS_OUTPUT.PUT_LINE('1. fn_get_available_locomotives');
    DBMS_OUTPUT.PUT_LINE('2. fn_get_available_wagons');
    DBMS_OUTPUT.PUT_LINE('3. fn_assemble_train_composition');
    DBMS_OUTPUT.PUT_LINE('4. fn_validate_train_composition');
    DBMS_OUTPUT.PUT_LINE('========================================');
END;
/

