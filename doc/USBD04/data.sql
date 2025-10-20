alter session set current_schema = APPUSER;

-- ============================================================
-- NÍVEL 0: Tabelas sem dependências (sem FK)
-- ============================================================

-- Inserir 5 GAUGE (sem FK)
INSERT INTO GAUGE (gauge_id, name, size_mm) VALUES (1, 'Iberian Gauge', 1668);
INSERT INTO GAUGE (gauge_id, name, size_mm) VALUES (2, 'Standard Gauge', 1435);
INSERT INTO GAUGE (gauge_id, name, size_mm) VALUES (3, 'Meter Gauge', 1000);
INSERT INTO GAUGE (gauge_id, name, size_mm) VALUES (4, 'Russian Gauge', 1520);
INSERT INTO GAUGE (gauge_id, name, size_mm) VALUES (5, 'Narrow Gauge', 900);
COMMIT;

-- Inserir 2 COMPANY (sem FK)
INSERT INTO COMPANY (company_id, name, short_name, vat) VALUES (1, 'Infraestruturas de Portugal, SA', 'IP', 'PT503933813');
INSERT INTO COMPANY (company_id, name, short_name, vat) VALUES (2, 'Medway - Operador Ferroviário de Mercadorias, S.A', 'Medway', 'PT509017800');
COMMIT;
-- Inserir 5 ROLE (sem FK)
INSERT INTO "ROLE" (role_id, name) VALUES (1, 'ADMIN');
INSERT INTO "ROLE" (role_id, name) VALUES (2, 'OPERATOR');
INSERT INTO "ROLE" (role_id, name) VALUES (3, 'DRIVER');
INSERT INTO "ROLE" (role_id, name) VALUES (4, 'MECHANIC');
INSERT INTO "ROLE" (role_id, name) VALUES (5, 'SUPERVISOR');
COMMIT;
-- Inserir 5 ITEM_CATEGORY (sem FK)
INSERT INTO ITEM_CATEGORY (category_id, name) VALUES (1, 'Electronics');
INSERT INTO ITEM_CATEGORY (category_id, name) VALUES (2, 'Food');
INSERT INTO ITEM_CATEGORY (category_id, name) VALUES (3, 'Clothing');
INSERT INTO ITEM_CATEGORY (category_id, name) VALUES (4, 'Tools');
INSERT INTO ITEM_CATEGORY (category_id, name) VALUES (5, 'Furniture');
COMMIT;
-- Inserir 5 FACILITY_TYPE (sem FK)
INSERT INTO FACILITY_TYPE (facility_type_id, name) VALUES (1, 'Warehouse');
INSERT INTO FACILITY_TYPE (facility_type_id, name) VALUES (2, 'Train Station');
INSERT INTO FACILITY_TYPE (facility_type_id, name) VALUES (3, 'Depot');
INSERT INTO FACILITY_TYPE (facility_type_id, name) VALUES (4, 'Maintenance Yard');
INSERT INTO FACILITY_TYPE (facility_type_id, name) VALUES (5, 'Intermodal Terminal');
COMMIT;
-- Inserir 5 LOCOMOTIVE_TYPE (sem FK)
INSERT INTO LOCOMOTIVE_TYPE (locomotive_type_id, name) VALUES (1, 'Electric');
INSERT INTO LOCOMOTIVE_TYPE (locomotive_type_id, name) VALUES (2, 'Diesel');
INSERT INTO LOCOMOTIVE_TYPE (locomotive_type_id, name) VALUES (3, 'Steam');
INSERT INTO LOCOMOTIVE_TYPE (locomotive_type_id, name) VALUES (4, 'Hybrid');
INSERT INTO LOCOMOTIVE_TYPE (locomotive_type_id, name) VALUES (5, 'Battery');
COMMIT;
-- Inserir 3 WAGON_TYPE (sem FK)
INSERT INTO WAGON_TYPE (wagon_type_id, name) VALUES (1, 'Cereal wagon');
INSERT INTO WAGON_TYPE (wagon_type_id, name) VALUES (2, 'Covered wagon with sliding door');
INSERT INTO WAGON_TYPE (wagon_type_id, name) VALUES (3, 'Container wagon (max 40" HC');

COMMIT;
-- Inserir FACILITY_FEATURE_TYPE (sem FK)
INSERT INTO FACILITY_FEATURE_TYPE (feature_id, name) VALUES (1, 'Fuel Station');
INSERT INTO FACILITY_FEATURE_TYPE (feature_id, name) VALUES (2, 'Repair Shop');
INSERT INTO FACILITY_FEATURE_TYPE (feature_id, name) VALUES (3, 'Cafeteria');
INSERT INTO FACILITY_FEATURE_TYPE (feature_id, name) VALUES (4, 'Restrooms');
INSERT INTO FACILITY_FEATURE_TYPE (feature_id, name) VALUES (5, 'Parking Lot');
COMMIT;
-- Inserir ORDER (sem FK)
INSERT INTO "ORDER" (order_id, status, priority, due_date) VALUES (1, 'NEW', 5, TO_DATE('2025-11-15','YYYY-MM-DD'));
INSERT INTO "ORDER" (order_id, status, priority, due_date) VALUES (2, 'PROCESSING', 3, TO_DATE('2025-11-20','YYYY-MM-DD'));
INSERT INTO "ORDER" (order_id, status, priority, due_date) VALUES (3, 'COMPLETED', 1, TO_DATE('2025-11-01','YYYY-MM-DD'));
INSERT INTO "ORDER" (order_id, status, priority, due_date) VALUES (4, 'CANCELLED', 0, NULL);
INSERT INTO "ORDER" (order_id, status, priority, due_date) VALUES (5, 'NEW', 4, TO_DATE('2025-10-25','YYYY-MM-DD'));
COMMIT;
-- Inserir CART (sem FK)
INSERT INTO CART (cart_id, nome, max_weight) VALUES (1, 'Cart 1', 1000);
INSERT INTO CART (cart_id, nome, max_weight) VALUES (2, 'Cart 2', 1500);
INSERT INTO CART (cart_id, nome, max_weight) VALUES (3, 'Cart 3', 1200);
INSERT INTO CART (cart_id, nome, max_weight) VALUES (4, 'Cart 4', 1300);
INSERT INTO CART (cart_id, nome, max_weight) VALUES (5, 'Cart 5', 1100);
COMMIT;
-- ============================================================
-- NÍVEL 1: Tabelas que dependem apenas do NÍVEL 0
-- ============================================================

-- USER depende de COMPANY
INSERT INTO "USER" (user_id, username, email, password, company_id, is_active) VALUES (1, 'alice', 'alice@ip.pt', 'password1', 1, 1);
INSERT INTO "USER" (user_id, username, email, password, company_id, is_active) VALUES (2, 'bob', 'bob@medway.pt', 'password2', 2, 1);
INSERT INTO "USER" (user_id, username, email, password, company_id, is_active) VALUES (3, 'charlie', 'charlie@cp.pt', 'password3', 1, 1);
INSERT INTO "USER" (user_id, username, email, password, company_id, is_active) VALUES (4, 'diana', 'diana@renfe.pt', 'password4', 2, 1);
INSERT INTO "USER" (user_id, username, email, password, company_id, is_active) VALUES (5, 'ed', 'ed@rexp.pt', 'password5', 1, 1);
COMMIT;
-- USER_ROLE depende de USER e ROLE
INSERT INTO USER_ROLE (user_id, role_id) VALUES (1, 1);
INSERT INTO USER_ROLE (user_id, role_id) VALUES (2, 2);
INSERT INTO USER_ROLE (user_id, role_id) VALUES (3, 3);
INSERT INTO USER_ROLE (user_id, role_id) VALUES (4, 4);
INSERT INTO USER_ROLE (user_id, role_id) VALUES (5, 5);
COMMIT;
-- ITEM depende de ITEM_CATEGORY
INSERT INTO ITEM (item_id, item_category_id, sku, name, uom, volume, unit_weight) VALUES (1, 1, 'ELEC001', 'Laptop', 'pcs', 0.5, 2.5);
INSERT INTO ITEM (item_id, item_category_id, sku, name, uom, volume, unit_weight) VALUES (2, 2, 'FOOD002', 'Canned Beans', 'box', 1.2, 3.0);
INSERT INTO ITEM (item_id, item_category_id, sku, name, uom, volume, unit_weight) VALUES (3, 3, 'CLOT003', 'T-Shirt', 'pcs', 0.3, 0.2);
INSERT INTO ITEM (item_id, item_category_id, sku, name, uom, volume, unit_weight) VALUES (4, 4, 'TOOL004', 'Hammer', 'pcs', 0.8, 1.5);
INSERT INTO ITEM (item_id, item_category_id, sku, name, uom, volume, unit_weight) VALUES (5, 5, 'FURN005', 'Chair', 'pcs', 2.0, 7.0);
COMMIT;
-- FACILITY depende de FACILITY_TYPE
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (1, 'São Romão', 2, 41.2782, -8.5534, 0);
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (2, 'Tamel', 2, 41.6089, -8.6349, 0);
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (3, 'Senhora das Dores', 2, 41.3320, -8.5577, 0);
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (4, 'Lousado', 2, 41.3509, -8.5265, 0);
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (5, 'Porto Campanhã', 5, 41.1488, -8.5854, 1);
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (6, 'Leandro', 2, 41.2433, -8.5568, 0);
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (7, 'Porto São Bento', 5, 41.1456, -8.6106, 1);
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (8, 'Barcelos', 2, 41.5366, -8.6091, 0);
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (9, 'Vila Nova da Cerveira', 2, 41.9463, -8.7484, 0);
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (10, 'Midões', 2, 41.5082, -8.5819, 0);
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (11, 'Valença', 2, 42.0248, -8.6394, 0);
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (12, 'Darque', 2, 41.6667, -8.8167, 0);
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (13, 'Contumil', 3, 41.1660, -8.5780, 0);
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (14, 'Ermesinde', 5, 41.2167, -8.5542, 1);
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (15, 'São Frutuoso', 2, 41.2590, -8.5582, 0);
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (16, 'São Pedro da Torre', 2, 41.9903, -8.6672, 0);
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (17, 'Viana do Castelo', 2, 41.6952, -8.8313, 0);
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (18, 'Famalicão', 2, 41.4051, -8.5297, 0);
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (19, 'Barroselas', 2, 41.6463, -8.7011, 0);
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (20, 'Nine', 2, 41.4551, -8.5443, 0);
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (21, 'Caminha', 2, 41.8733, -8.8397, 0);
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (22, 'Carvalha', 2, 41.9660, -8.7470, 0);
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (23, 'Carreço', 2, 41.7490, -8.8650, 0);
COMMIT;
-- LINE depende de COMPANY (owner_id)
INSERT INTO LINE (line_id, name, owner_id) VALUES (1, 'Ramal São Bento - Campanhã', 1);
INSERT INTO LINE (line_id, name, owner_id) VALUES (2, 'Ramal Camapanhã - Nine', 1);
INSERT INTO LINE (line_id, name, owner_id) VALUES (3, 'Ramal Nine - Barcelos', 1);
INSERT INTO LINE (line_id, name, owner_id) VALUES (4, 'Ramal Barcelos - Viana', 1);
INSERT INTO LINE (line_id, name, owner_id) VALUES (5, 'Ramal viana - Caminha', 1);
INSERT INTO LINE (line_id, name, owner_id) VALUES (6, 'Ramal Caminha - Torre', 1);
INSERT INTO LINE (line_id, name, owner_id) VALUES (7, 'Ramal Torre - Valença', 1);
COMMIT;
-- WAGON_MODEL depende de WAGON_TYPE
INSERT INTO WAGON_MODEL (wagon_model_id, name, wagon_type, constructor, number_bogies, bogies, tare_t, payload_t, volume_m3, length_m, width_m, height_m, max_speed_kmh) VALUES
    (1245, 'Tadgs 32 94 082 3', 1, 'Metalsines', 2, 'Duplo', 24.0, 56.0, 75.0, 17.24, 3.07, 4.27, 120);
INSERT INTO WAGON_MODEL (wagon_model_id, name, wagon_type, constructor, number_bogies, bogies, tare_t, payload_t, volume_m3, length_m, width_m, height_m, max_speed_kmh) VALUES
    (1278, 'Tdgs 41 94 074 1', 1, 'Equimetal', 2, 'Duplo', 13.8, 26.2, 38.0, 9.64, 3.12, 4.17, 100);
INSERT INTO WAGON_MODEL (wagon_model_id, name, wagon_type, constructor, number_bogies, bogies, tare_t, payload_t, volume_m3, length_m, width_m, height_m, max_speed_kmh) VALUES
    (1325, 'Gabs 81 94 181 1', 2, 'Sepsa Cometna', 2, 'Duplo', 29.8, 50.2, 110.0, 21.70, 3.18, 4.17, 100);
INSERT INTO WAGON_MODEL (wagon_model_id, name, wagon_type, constructor, number_bogies, bogies, tare_t, payload_t, volume_m3, length_m, width_m, height_m, max_speed_kmh) VALUES
    (1104, 'Regmms 32 94 356 3', 3, 'Metalsines', 2, 'Duplo', 21.2, 60.6, 76.3, 14.04, 3.10, 2.54, 120);
INSERT INTO WAGON_MODEL (wagon_model_id, name, wagon_type, constructor, number_bogies, bogies, tare_t, payload_t, volume_m3, length_m, width_m, height_m, max_speed_kmh) VALUES
    (985, 'Lgs 22 94 441 6', 3, 'Metalsines', 1, 'Simples', 11.9, 28.1, 76.3, 13.86, 2.85, 1.06, 120);
INSERT INTO WAGON_MODEL (wagon_model_id, name, wagon_type, constructor, number_bogies, bogies, tare_t, payload_t, volume_m3, length_m, width_m, height_m, max_speed_kmh) VALUES
    (987, 'Sgnss 12 94 455 2', 3, 'Emef', 2, 'Duplo', 21.6, 68.4, 76.3, 18.12, 2.95, 1.03, 120);
INSERT INTO WAGON_MODEL (wagon_model_id, name, wagon_type, constructor, number_bogies, bogies, tare_t, payload_t, volume_m3, length_m, width_m, height_m, max_speed_kmh) VALUES
    (988, 'Sgnss 12 94 455 2', 3, 'Emef', 2, 'Duplo', 21.6, 68.4, 76.3, 18.12, 2.95, 1.03, 120);
COMMIT;
-- LOCOMOTIVE depende de USER (operator_id) e LOCOMOTIVE_TYPE
INSERT INTO LOCOMOTIVE (locomotive_id, operator_id, locomotive_type, name, brand, model, year, power_kw, acceleration_ms2, length_m, width_m, height_m, max_weight_t, max_speed_kmh, operational_speed_kmh, total_weight_t, num_bogies, bogie_type, traction_kn) VALUES
    (5621, 2, 1, 'Inês', 'Siemens', 'Eurosprinter', 1995, 5600, 0.5, 19.2, 3.0, 4.375, 87, 220, 70, 87, 2, 'Bo-Bo', 300);
INSERT INTO LOCOMOTIVE (locomotive_id, operator_id, locomotive_type, name, brand, model, year, power_kw, acceleration_ms2, length_m, width_m, height_m, max_weight_t, max_speed_kmh, operational_speed_kmh, total_weight_t, num_bogies, bogie_type, traction_kn) VALUES
    (5623, 2, 1, 'Paz', 'Siemens', 'Eurosprinter', 1995, 5600, 0.4, 19.2, 3.0, 4.375, 87, 220, 70, 87, 2, 'Bo-Bo', 300);
INSERT INTO LOCOMOTIVE (locomotive_id, operator_id, locomotive_type, name, brand, model, year, power_kw, acceleration_ms2, length_m, width_m, height_m, max_weight_t, max_speed_kmh, operational_speed_kmh, total_weight_t, num_bogies, bogie_type, traction_kn) VALUES
    (5630, 2, 1, 'Helena', 'Siemens', 'Eurosprinter', 1996, 5600, 0.45, 19.2, 3.0, 4.375, 87, 220, 70, 87, 2, 'Bo-Bo', 300);
INSERT INTO LOCOMOTIVE (locomotive_id, operator_id, locomotive_type, name, brand, model, year, power_kw, acceleration_ms2, length_m, width_m, height_m, max_weight_t, max_speed_kmh, operational_speed_kmh, total_weight_t, num_bogies, bogie_type, traction_kn) VALUES
    (1903, 2, 2, 'Eva', 'Sorefame - Alsthom', 'CP 1900', 1981, 1623, 0.3, 19.084, 3.062, 4.31, 117, 100, 42.5, 117, 2, 'Co-Co', 396);
COMMIT;
-- ORDER_LINE depende de ORDER e ITEM (sku)
INSERT INTO ORDER_LINE (order_line_id, order_id, sku, qty) VALUES (1, 1, 'ELEC001', 10);
INSERT INTO ORDER_LINE (order_line_id, order_id, sku, qty) VALUES (2, 2, 'FOOD002', 50);
INSERT INTO ORDER_LINE (order_line_id, order_id, sku, qty) VALUES (3, 3, 'CLOT003', 100);
INSERT INTO ORDER_LINE (order_line_id, order_id, sku, qty) VALUES (4, 4, 'TOOL004', 25);
INSERT INTO ORDER_LINE (order_line_id, order_id, sku, qty) VALUES (5, 5, 'FURN005', 5);
COMMIT;
-- RETURN_ORDER depende de ORDER (orig_order_id - nullable)
INSERT INTO RETURN_ORDER (return_id, orig_order_id, reason, status) VALUES (1, 1, 'Damaged Item', 'RECEIVED');
INSERT INTO RETURN_ORDER (return_id, orig_order_id, reason, status) VALUES (2, 2, 'Wrong Item', 'PENDING');
INSERT INTO RETURN_ORDER (return_id, orig_order_id, reason, status) VALUES (3, 3, 'Late Delivery', 'PROCESSED');
INSERT INTO RETURN_ORDER (return_id, orig_order_id, reason, status) VALUES (4, 4, 'Customer Cancelled', 'CANCELLED');
INSERT INTO RETURN_ORDER (return_id, orig_order_id, reason, status) VALUES (5, 5, 'Quality Issue', 'RECEIVED');
COMMIT;
-- WAREHOUSE depende de FACILITY
INSERT INTO WAREHOUSE(warehouse_id, facility_id, name) VALUES (1, 5, 'Porto Campanhã Warehouse');
INSERT INTO WAREHOUSE(warehouse_id, facility_id, name) VALUES (2, 7, 'Sao Bento Warehouse');
INSERT INTO WAREHOUSE(warehouse_id, facility_id, name) VALUES (3, 8, 'Barcelos Warehouse');
INSERT INTO WAREHOUSE(warehouse_id, facility_id, name) VALUES (4, 11, 'Valença Warehouse');
INSERT INTO WAREHOUSE(warehouse_id, facility_id, name) VALUES (5, 16, 'São Pedro da Torre Warehouse');
COMMIT;
-- FACILITY_FEATURE depende de FACILITY e FACILITY_FEATURE_TYPE
INSERT INTO FACILITY_FEATURE (facility_id, feature_id) VALUES (5, 1);
INSERT INTO FACILITY_FEATURE (facility_id, feature_id) VALUES (7, 2);
INSERT INTO FACILITY_FEATURE (facility_id, feature_id) VALUES (8, 3);
INSERT INTO FACILITY_FEATURE (facility_id, feature_id) VALUES (16, 4);
INSERT INTO FACILITY_FEATURE (facility_id, feature_id) VALUES (11, 5);
COMMIT;
-- ============================================================
-- NÍVEL 2: Tabelas que dependem do NÍVEL 1
-- ============================================================

-- LINE_SEGMENT depende de LINE e GAUGE
INSERT INTO LINE_SEGMENT (segment_id, line_id, length_km, electrif_type, gauge_id, max_weight_kg_m, speed_limit_kmh, track_number, position) VALUES (1, 1, 2618, 1, 1, 8000, 100, 4, 1);
INSERT INTO LINE_SEGMENT (segment_id, line_id, length_km, electrif_type, gauge_id, max_weight_kg_m, speed_limit_kmh, track_number, position) VALUES (10, 2, 29003, 1, 1, 8000, 90, 2, 1);
INSERT INTO LINE_SEGMENT (segment_id, line_id, length_km, electrif_type, gauge_id, max_weight_kg_m, speed_limit_kmh, track_number, position) VALUES (11, 2, 10000, 1, 1, 8000, 80, 2, 2);
INSERT INTO LINE_SEGMENT (segment_id, line_id, length_km, electrif_type, gauge_id, max_weight_kg_m, speed_limit_kmh, track_number, position) VALUES (15, 3, 5286, 1, 1, 8000, 110, 2, 1);
INSERT INTO LINE_SEGMENT (segment_id, line_id, length_km, electrif_type, gauge_id, max_weight_kg_m, speed_limit_kmh, track_number, position) VALUES (16, 3, 6000, 1, 1, 8000, 100, 2, 2);
INSERT INTO LINE_SEGMENT (segment_id, line_id, length_km, electrif_type, gauge_id, max_weight_kg_m, speed_limit_kmh, track_number, position) VALUES (14, 4, 10387, 1, 1, 8000, 100, 2, 1);
INSERT INTO LINE_SEGMENT (segment_id, line_id, length_km, electrif_type, gauge_id, max_weight_kg_m, speed_limit_kmh, track_number, position) VALUES (12, 4, 12000, 1, 1, 8000, 100, 2, 2);
INSERT INTO LINE_SEGMENT (segment_id, line_id, length_km, electrif_type, gauge_id, max_weight_kg_m, speed_limit_kmh, track_number, position) VALUES (13, 4, 8000, 1, 1, 6400, 100, 2, 3);
INSERT INTO LINE_SEGMENT (segment_id, line_id, length_km, electrif_type, gauge_id, max_weight_kg_m, speed_limit_kmh, track_number, position) VALUES (20, 5, 6000, 1, 1, 8000, 100, 2, 1);
INSERT INTO LINE_SEGMENT (segment_id, line_id, length_km, electrif_type, gauge_id, max_weight_kg_m, speed_limit_kmh, track_number, position) VALUES (21, 5, 3000, 1, 1, 8000, 100, 2, 2);
INSERT INTO LINE_SEGMENT (segment_id, line_id, length_km, electrif_type, gauge_id, max_weight_kg_m, speed_limit_kmh, track_number, position) VALUES (22, 5, 15000, 1, 1, 8000, 100, 2, 3);
INSERT INTO LINE_SEGMENT (segment_id, line_id, length_km, electrif_type, gauge_id, max_weight_kg_m, speed_limit_kmh, track_number, position) VALUES (25, 6, 20829, 1, 1, 8000, 100, 2, 1);
INSERT INTO LINE_SEGMENT (segment_id, line_id, length_km, electrif_type, gauge_id, max_weight_kg_m, speed_limit_kmh, track_number, position) VALUES (26, 7, 4264, 1, 1, 8000, 100, 2, 1);
COMMIT;
-- WAGON_GAUGE depende de WAGON_MODEL e GAUGE
INSERT INTO WAGON_GAUGE (wagon_id, gauge_id) VALUES (1245, 1);
INSERT INTO WAGON_GAUGE (wagon_id, gauge_id) VALUES (1278, 1);
INSERT INTO WAGON_GAUGE (wagon_id, gauge_id) VALUES (1325, 1);
INSERT INTO WAGON_GAUGE (wagon_id, gauge_id) VALUES (1104, 1);
INSERT INTO WAGON_GAUGE (wagon_id, gauge_id) VALUES (985, 1);
INSERT INTO WAGON_GAUGE (wagon_id, gauge_id) VALUES (987, 1);
INSERT INTO WAGON_GAUGE (wagon_id, gauge_id) VALUES (988, 2);
COMMIT;
-- WAGON depende de WAGON_MODEL e USER (operator_id)
-- ATENÇÃO: Devido a um erro no schema, WAGON.wagon_id deve corresponder a WAGON_MODEL.wagon_model_id
-- A FK correta deveria ser WAGON.wagon_model -> WAGON_MODEL.wagon_model_id
-- Mas o schema define WAGON.wagon_id -> WAGON_MODEL.wagon_model_id
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year) VALUES (3563077, 1104, 2, 1987);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year) VALUES (3563078, 1104, 2, 1987);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year) VALUES (3563079, 1104, 2, 1987);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year) VALUES (3563080, 1104, 2, 1987);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year) VALUES (3563081, 1104, 2, 1987);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year) VALUES (3563082, 1104, 2, 1987);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year) VALUES (3563083, 1104, 2, 1987);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year) VALUES (3563084, 1104, 2, 1987);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year) VALUES (3563085, 1104, 2, 1987);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year) VALUES (3563086, 1104, 2, 1987);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year) VALUES (3563087, 1104, 2, 1987);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year) VALUES (3563088, 1104, 2, 1987);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year) VALUES (3563089, 1104, 2, 1987);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year) VALUES (3563090, 1104, 2, 1987);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year) VALUES (3563091, 1104, 2, 1987);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year) VALUES (3563092, 1104, 2, 1987);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year) VALUES (823045, 1245, 2, 1990);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year) VALUES (823046, 1245, 2, 1990);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year) VALUES (823047, 1245, 2, 1990);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year) VALUES (823048, 1245, 2, 1990);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year) VALUES (741001, 1278, 2, 1977);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year) VALUES (741002, 1278, 2, 1977);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year) VALUES (741003, 1278, 2, 1977);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year) VALUES (741004, 1278, 2, 1977);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year) VALUES (741005, 1278, 2, 1977);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year) VALUES (741006, 1278, 2, 1977);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year) VALUES (1811010, 1325, 2, 1977);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year) VALUES (1811011, 1325, 2, 1977);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year) VALUES (1811012, 1325, 2, 1977);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year) VALUES (1811013, 1325, 2, 1977);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year) VALUES (1811014, 1325, 2, 1977);
COMMIT;
-- LOCOMOTIVE_DIESEL depende de LOCOMOTIVE
INSERT INTO LOCOMOTIVE_DIESEL (locomotive_id, fuel_capacity_l) VALUES (1903, 4882);
COMMIT;
--LOCOMOTIVE_ELECTRIC depende de LOCOMOTIVE
INSERT INTO LOCOMOTIVE_ELECTRIC (locomotive_id, voltage, frequency_hz) VALUES (5621, 25, 50);
INSERT INTO LOCOMOTIVE_ELECTRIC (locomotive_id, voltage, frequency_hz) VALUES (5623, 25, 50);
INSERT INTO LOCOMOTIVE_ELECTRIC (locomotive_id, voltage, frequency_hz) VALUES (5630, 25, 50);
COMMIT;
-- LOCOMOTIVE_GAUGE depende de LOCOMOTIVE e GAUGE
INSERT INTO LOCOMOTIVE_GAUGE (locomotive_id, gauge_id) VALUES (5621, 1);
INSERT INTO LOCOMOTIVE_GAUGE (locomotive_id, gauge_id) VALUES (5623, 1);
INSERT INTO LOCOMOTIVE_GAUGE (locomotive_id, gauge_id) VALUES (5630, 1);
INSERT INTO LOCOMOTIVE_GAUGE (locomotive_id, gauge_id) VALUES (1903, 1);
COMMIT;
-- WAREHOUSE_AISLE depende de WAREHOUSE
INSERT INTO WAREHOUSE_AISLE (aisle_id, warehouse_id, aisle_no, unique_code) VALUES (1, 1, 1, 'A1');
INSERT INTO WAREHOUSE_AISLE (aisle_id, warehouse_id, aisle_no, unique_code) VALUES (2, 2, 2, 'A2');
INSERT INTO WAREHOUSE_AISLE (aisle_id, warehouse_id, aisle_no, unique_code) VALUES (3, 3, 3, 'A3');
INSERT INTO WAREHOUSE_AISLE (aisle_id, warehouse_id, aisle_no, unique_code) VALUES (4, 4, 4, 'A4');
INSERT INTO WAREHOUSE_AISLE (aisle_id, warehouse_id, aisle_no, unique_code) VALUES (5, 5, 5, 'A5');
COMMIT;
-- RETURN_LINE depende de RETURN_ORDER e ITEM (sku)
INSERT INTO RETURN_LINE (return_line_id, return_id, sku, qty, condition) VALUES (1, 1, 'ELEC001', 2, 'Damaged');
INSERT INTO RETURN_LINE (return_line_id, return_id, sku, qty, condition) VALUES (2, 2, 'FOOD002', 5, 'Wrong Item');
INSERT INTO RETURN_LINE (return_line_id, return_id, sku, qty, condition) VALUES (3, 3, 'CLOT003', 10, 'Late Arrival');
INSERT INTO RETURN_LINE (return_line_id, return_id, sku, qty, condition) VALUES (4, 4, 'TOOL004', 1, 'Cancelled');
INSERT INTO RETURN_LINE (return_line_id, return_id, sku, qty, condition) VALUES (5, 5, 'FURN005', 3, 'Defective');
COMMIT;
-- TRAIN depende de USER (operator_id) e FACILITY (departure, destination)
INSERT INTO TRAIN (train_id, code, departure, destination, operator_id) VALUES (1, 'T001', 7, 5, 2);
INSERT INTO TRAIN (train_id, code, departure, destination, operator_id) VALUES (2, 'T002', 5, 20, 2);
INSERT INTO TRAIN (train_id, code, departure, destination, operator_id) VALUES (3, 'T003', 20, 8, 2);
INSERT INTO TRAIN (train_id, code, departure, destination, operator_id) VALUES (4, 'T004', 8, 17, 2);
INSERT INTO TRAIN (train_id, code, departure, destination, operator_id) VALUES (5, 'T005', 17, 21, 2);
INSERT INTO TRAIN (train_id, code, departure, destination, operator_id) VALUES (6, 'T006', 21, 16, 2);
INSERT INTO TRAIN (train_id, code, departure, destination, operator_id) VALUES (7, 'T007', 16, 11, 2);
COMMIT;
-- PARKING_TRACK depende de LINE e FACILITY
INSERT INTO PARKING_TRACK (parking_track_id, line_id, facility_id, name, capacity_wagons) VALUES (1, 1, 5, 'Track A', 10);
INSERT INTO PARKING_TRACK (parking_track_id, line_id, facility_id, name, capacity_wagons) VALUES (2, 2, 7, 'Track B', 20);
INSERT INTO PARKING_TRACK (parking_track_id, line_id, facility_id, name, capacity_wagons) VALUES (3, 3, 8, 'Track C', 15);
INSERT INTO PARKING_TRACK (parking_track_id, line_id, facility_id, name, capacity_wagons) VALUES (4, 4, 11, 'Track D', 18);
INSERT INTO PARKING_TRACK (parking_track_id, line_id, facility_id, name, capacity_wagons) VALUES (5, 5, 16, 'Track E', 12);
COMMIT;
-- ============================================================
-- NÍVEL 3: Tabelas que dependem do NÍVEL 2
-- ============================================================

-- Ramal 1: São Bento - Campanhã
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (1, 7);
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (1, 5);

-- Ramal 2: Campanhã - Nine
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (10, 5);
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (11, 20);

-- Ramal 3: Nine - Barcelos
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (15, 20);
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (16, 8);

-- Ramal 4: Barcelos - Viana
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (14, 8);
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (13, 17);

-- Ramal 5: Viana - Caminha
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (20, 17);
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (22, 21);

-- Ramal 6: Caminha - Torre
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (25, 21);
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (25, 16);

-- Ramal 7: Torre - Valença
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (26, 16);
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (26, 11);

COMMIT;
-- WAREHOUSE_BAY depende de WAREHOUSE_AISLE
INSERT INTO WAREHOUSE_BAY (bay_id, aisle_id, bay_no, capacity_box) VALUES (1, 1, 1, 500);
INSERT INTO WAREHOUSE_BAY (bay_id, aisle_id, bay_no, capacity_box) VALUES (2, 2, 2, 600);
INSERT INTO WAREHOUSE_BAY (bay_id, aisle_id, bay_no, capacity_box) VALUES (3, 3, 3, 700);
INSERT INTO WAREHOUSE_BAY (bay_id, aisle_id, bay_no, capacity_box) VALUES (4, 4, 4, 800);
INSERT INTO WAREHOUSE_BAY (bay_id, aisle_id, bay_no, capacity_box) VALUES (5, 5, 5, 900);
COMMIT;
-- QA_INSPECTION depende de RETURN_LINE
INSERT INTO QA_INSPECTION (qa_id, return_line_id, status) VALUES (1, 1, 'PASSED');
INSERT INTO QA_INSPECTION (qa_id, return_line_id, status) VALUES (2, 2, 'FAILED');
INSERT INTO QA_INSPECTION (qa_id, return_line_id, status) VALUES (3, 3, 'PASSED');
INSERT INTO QA_INSPECTION (qa_id, return_line_id, status) VALUES (4, 4, 'FAILED');
INSERT INTO QA_INSPECTION (qa_id, return_line_id, status) VALUES (5, 5, 'PASSED');
COMMIT;
-- QUARANTINE_HOLD depende de RETURN_ORDER
INSERT INTO QUARANTINE_HOLD (qhold_id, return_id, status) VALUES (1, 1, 'ACTIVE');
INSERT INTO QUARANTINE_HOLD (qhold_id, return_id, status) VALUES (2, 2, 'RELEASED');
INSERT INTO QUARANTINE_HOLD (qhold_id, return_id, status) VALUES (3, 3, 'ACTIVE');
INSERT INTO QUARANTINE_HOLD (qhold_id, return_id, status) VALUES (4, 4, 'RELEASED');
INSERT INTO QUARANTINE_HOLD (qhold_id, return_id, status) VALUES (5, 5, 'ACTIVE');
COMMIT;
-- TRAIN_SERVICE depende de TRAIN e USER (train_driver_id)
INSERT INTO TRAIN_SERVICE (service_id, train_id, departure_date, arrival_date, train_driver_id, status) VALUES
    (1, 1, TO_TIMESTAMP('2025-11-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-11-01 10:00:00', 'YYYY-MM-DD HH24:MI:SS'), 3, 'SCHEDULED');
INSERT INTO TRAIN_SERVICE (service_id, train_id, departure_date, arrival_date, train_driver_id, status) VALUES
    (2, 2, TO_TIMESTAMP('2025-11-02 09:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-11-02 12:00:00', 'YYYY-MM-DD HH24:MI:SS'), 2, 'DEPARTED');
INSERT INTO TRAIN_SERVICE (service_id, train_id, departure_date, arrival_date, train_driver_id, status) VALUES
    (3, 3, TO_TIMESTAMP('2025-11-03 07:30:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-11-03 11:00:00', 'YYYY-MM-DD HH24:MI:SS'), 5, 'ARRIVED');
INSERT INTO TRAIN_SERVICE (service_id, train_id, departure_date, arrival_date, train_driver_id, status) VALUES
    (4, 4, TO_TIMESTAMP('2025-11-04 06:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-11-04 08:30:00', 'YYYY-MM-DD HH24:MI:SS'), 4, 'CANCELLED');
INSERT INTO TRAIN_SERVICE (service_id, train_id, departure_date, arrival_date, train_driver_id, status) VALUES
    (5, 5, TO_TIMESTAMP('2025-11-05 05:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-11-05 07:00:00', 'YYYY-MM-DD HH24:MI:SS'), 1, 'SCHEDULED');
COMMIT;
-- PARKING_QUEUE depende de PARKING_TRACK e WAGON
INSERT INTO PARKING_QUEUE (parking_queue_id, parking_track_id, wagon_id, position_no, status) VALUES (1, 1, 823045, 1, 'WAITING');
INSERT INTO PARKING_QUEUE (parking_queue_id, parking_track_id, wagon_id, position_no, status) VALUES (2, 2, 741001, 1, 'WAITING');
INSERT INTO PARKING_QUEUE (parking_queue_id, parking_track_id, wagon_id, position_no, status) VALUES (3, 3, 1811010, 1, 'WAITING');
INSERT INTO PARKING_QUEUE (parking_queue_id, parking_track_id, wagon_id, position_no, status) VALUES (4, 4, 3563078, 1, 'WAITING');
INSERT INTO PARKING_QUEUE (parking_queue_id, parking_track_id, wagon_id, position_no, status) VALUES (5, 5, 3563090, 1, 'WAITING');

-- INVENTORY_LOT depende de ITEM (sku)
INSERT INTO INVENTORY_LOT (lot_id, sku, box_code, exp_date, received_date) VALUES (1, 'ELEC001', 'BX1001', TO_DATE('2026-05-01','YYYY-MM-DD'), TO_DATE('2025-09-01','YYYY-MM-DD'));
INSERT INTO INVENTORY_LOT (lot_id, sku, box_code, exp_date, received_date) VALUES (2, 'FOOD002', 'BX1002', TO_DATE('2025-12-15','YYYY-MM-DD'), TO_DATE('2025-09-15','YYYY-MM-DD'));
INSERT INTO INVENTORY_LOT (lot_id, sku, box_code, exp_date, received_date) VALUES (3, 'CLOT003', 'BX1003', NULL, TO_DATE('2025-10-01','YYYY-MM-DD'));
INSERT INTO INVENTORY_LOT (lot_id, sku, box_code, exp_date, received_date) VALUES (4, 'TOOL004', 'BX1004', NULL, TO_DATE('2025-08-20','YYYY-MM-DD'));
INSERT INTO INVENTORY_LOT (lot_id, sku, box_code, exp_date, received_date) VALUES (5, 'FURN005', 'BX1005', NULL, TO_DATE('2025-07-30','YYYY-MM-DD'));
COMMIT;
-- ============================================================
-- NÍVEL 4: Tabelas que dependem do NÍVEL 3
-- ============================================================

-- INVENTORY_BALANCE depende de WAREHOUSE_BAY e INVENTORY_LOT
INSERT INTO INVENTORY_BALANCE (balance_id, bay_id, lot_id, qty) VALUES (1, 1, 1, 100);
INSERT INTO INVENTORY_BALANCE (balance_id, bay_id, lot_id, qty) VALUES (2, 2, 2, 200);
INSERT INTO INVENTORY_BALANCE (balance_id, bay_id, lot_id, qty) VALUES (3, 3, 3, 150);
INSERT INTO INVENTORY_BALANCE (balance_id, bay_id, lot_id, qty) VALUES (4, 4, 4, 80);
INSERT INTO INVENTORY_BALANCE (balance_id, bay_id, lot_id, qty) VALUES (5, 5, 5, 60);
COMMIT;
-- INVENTORY_MOVE depende de INVENTORY_LOT e WAREHOUSE_BAY (from_bay_id, to_bay_id)
INSERT INTO INVENTORY_MOVE (move_id, lot_id, from_bay_id, to_bay_id, qty, reason) VALUES (1, 1, 1, 2, 50, 'Stock Reposition');
INSERT INTO INVENTORY_MOVE (move_id, lot_id, from_bay_id, to_bay_id, qty, reason) VALUES (2, 2, 2, 3, 100, 'Order Fulfillment');
INSERT INTO INVENTORY_MOVE (move_id, lot_id, from_bay_id, to_bay_id, qty, reason) VALUES (3, 3, 3, 4, 30, 'Stock Transfer');
INSERT INTO INVENTORY_MOVE (move_id, lot_id, from_bay_id, to_bay_id, qty, reason) VALUES (4, 4, 4, 5, 40, 'Return Handling');
INSERT INTO INVENTORY_MOVE (move_id, lot_id, from_bay_id, to_bay_id, qty, reason) VALUES (5, 5, 5, 1, 20, 'Inventory Adjustment');
COMMIT;
-- SERVICE_LOCOMOTIVE depende de TRAIN_SERVICE e LOCOMOTIVE
INSERT INTO SERVICE_LOCOMOTIVE (service_locomotive_id, service_id, locomotive_id, position_no) VALUES (1, 1, 5621, 1);
INSERT INTO SERVICE_LOCOMOTIVE (service_locomotive_id, service_id, locomotive_id, position_no) VALUES (2, 2, 5623, 1);
INSERT INTO SERVICE_LOCOMOTIVE (service_locomotive_id, service_id, locomotive_id, position_no) VALUES (3, 3, 5630, 1);
INSERT INTO SERVICE_LOCOMOTIVE (service_locomotive_id, service_id, locomotive_id, position_no) VALUES (4, 4, 1903, 1);
COMMIT;
-- TRAIN_STOP depende de TRAIN_SERVICE, FACILITY e PARKING_TRACK
INSERT INTO TRAIN_STOP (train_stop_id, service_id, facility_id, parking_track, position, sched_arr, sched_dep, planned_stop_time) VALUES
    (1, 1, 5, 1, 1, TO_TIMESTAMP('2025-11-01 08:30:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-11-01 08:45:00', 'YYYY-MM-DD HH24:MI:SS'), 15);
INSERT INTO TRAIN_STOP (train_stop_id, service_id, facility_id, parking_track, position, sched_arr, sched_dep, planned_stop_time) VALUES
    (2, 2, 7, 2, 1, TO_TIMESTAMP('2025-11-02 09:30:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-11-02 09:50:00', 'YYYY-MM-DD HH24:MI:SS'), 20);
INSERT INTO TRAIN_STOP (train_stop_id, service_id, facility_id, parking_track, position, sched_arr, sched_dep, planned_stop_time) VALUES
    (3, 3, 8, 3, 1, TO_TIMESTAMP('2025-11-03 08:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-11-03 08:15:00', 'YYYY-MM-DD HH24:MI:SS'), 15);
INSERT INTO TRAIN_STOP (train_stop_id, service_id, facility_id, parking_track, position, sched_arr, sched_dep, planned_stop_time) VALUES
    (4, 4, 16, 4, 1, TO_TIMESTAMP('2025-11-04 06:30:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-11-04 06:45:00', 'YYYY-MM-DD HH24:MI:SS'), 15);
INSERT INTO TRAIN_STOP (train_stop_id, service_id, facility_id, parking_track, position, sched_arr, sched_dep, planned_stop_time) VALUES
    (5, 5, 11, 5, 1, TO_TIMESTAMP('2025-11-05 05:30:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-11-05 05:45:00', 'YYYY-MM-DD HH24:MI:SS'), 15);
COMMIT;
-- PICK_WAVE depende de CART
INSERT INTO PICK_WAVE (wave_id, cart_id, strategy, status) VALUES (1, 1, 'FIFO', 'OPEN');
INSERT INTO PICK_WAVE (wave_id, cart_id, strategy, status) VALUES (2, 2, 'LIFO', 'IN_PROGRESS');
INSERT INTO PICK_WAVE (wave_id, cart_id, strategy, status) VALUES (3, 3, 'ZONE', 'COMPLETED');
INSERT INTO PICK_WAVE (wave_id, cart_id, strategy, status) VALUES (4, 4, 'FIFO', 'OPEN');
INSERT INTO PICK_WAVE (wave_id, cart_id, strategy, status) VALUES (5, 5, 'LIFO', 'CANCELLED');
COMMIT;
-- ============================================================
-- NÍVEL 5: Tabelas que dependem do NÍVEL 4
-- ============================================================APPUSER

-- TRAIN_SERVICE_COMPOSITION depende de SERVICE_LOCOMOTIVE e WAGON
INSERT INTO TRAIN_SERVICE_COMPOSITION (train_service_id, service_locomotive_id, wagon_id) VALUES (1, 1, 823045);
INSERT INTO TRAIN_SERVICE_COMPOSITION (train_service_id, service_locomotive_id, wagon_id) VALUES (2, 2, 741001);
INSERT INTO TRAIN_SERVICE_COMPOSITION (train_service_id, service_locomotive_id, wagon_id) VALUES (3, 3, 1811010);
INSERT INTO TRAIN_SERVICE_COMPOSITION (train_service_id, service_locomotive_id, wagon_id) VALUES (4, 4, 3563078);
INSERT INTO TRAIN_SERVICE_COMPOSITION (train_service_id, service_locomotive_id, wagon_id) VALUES (5, 4, 3563091);
COMMIT;
-- PICK_TASK depende de PICK_WAVE, ORDER_LINE e INVENTORY_BALANCE
INSERT INTO PICK_TASK (pick_id, wave_id, order_line_id, balance_id, qty, pos_seq) VALUES (1, 1, 1, 1, 5, 1);
INSERT INTO PICK_TASK (pick_id, wave_id, order_line_id, balance_id, qty, pos_seq) VALUES (2, 2, 2, 2, 10, 1);
INSERT INTO PICK_TASK (pick_id, wave_id, order_line_id, balance_id, qty, pos_seq) VALUES (3, 3, 3, 3, 15, 1);
INSERT INTO PICK_TASK (pick_id, wave_id, order_line_id, balance_id, qty, pos_seq) VALUES (4, 4, 4, 4, 5, 1);
INSERT INTO PICK_TASK (pick_id, wave_id, order_line_id, balance_id, qty, pos_seq) VALUES (5, 5, 5, 5, 3, 1);
COMMIT;