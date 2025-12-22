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

-- Inserir 3 COMPANY (sem FK)
INSERT INTO COMPANY (company_id, name, short_name, vat) VALUES (1, 'Infraestruturas de Portugal, SA', 'IP', 'PT503933813');
INSERT INTO COMPANY (company_id, name, short_name, vat) VALUES (2, 'Medway - Operador Ferroviário de Mercadorias, S.A', 'Medway', 'PT509017800');
INSERT INTO COMPANY (company_id, name, short_name, vat) VALUES (3, 'Captrain Portugal S.A.', 'Captrain', 'PT507832388');
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
INSERT INTO WAGON_TYPE (wagon_type_id, name) VALUES (4, 'Biodiesel wagaon');
INSERT INTO WAGON_TYPE (wagon_type_id, name) VALUES (5, 'Biodiesel wagaon');
INSERT INTO WAGON_TYPE (wagon_type_id, name) VALUES (6, 'Wood wagon');
COMMIT;

-- Inserir FACILITY_FEATURE_TYPE (sem FK)
INSERT INTO FACILITY_FEATURE_TYPE (feature_id, name) VALUES (1, 'Fuel Station');
INSERT INTO FACILITY_FEATURE_TYPE (feature_id, name) VALUES (2, 'Repair Shop');
INSERT INTO FACILITY_FEATURE_TYPE (feature_id, name) VALUES (3, 'Cafeteria');
INSERT INTO FACILITY_FEATURE_TYPE (feature_id, name) VALUES (4, 'Restrooms');
INSERT INTO FACILITY_FEATURE_TYPE (feature_id, name) VALUES (5, 'Parking Lot');
INSERT INTO FACILITY_FEATURE_TYPE (feature_id, name) VALUES (6, 'Building');
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
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (30, 'Braga', 2, 41.548672, -8.434175, 0);
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (31, 'Manzagão', 2, 41.52699, -8.45769, 0);
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (32, 'Cerqueiral', 1, 41.816277, -8.400176, 0);
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (33, 'Gemieira', 1, 41.778056, -8.523611, 0);
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (35, 'Paredes de Coura', 1, 41.900000, -8.550000, 0);
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (43, 'São Gemil', 2, 41.19714, -8.57921, 0);
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (45, 'São Mamede de Infesta', 2, 41.1903, -8.6133, 0);
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (48, 'Leça do Balio', 2, 41.20845, -8.62974, 0);
INSERT INTO FACILITY (facility_id, name, facility_type_id, lat, lon, is_intermodal) VALUES (50, 'Leixões', 5, 41.1833, -8.7020, 1);
COMMIT;
-- LINE depende de COMPANY (owner_id)
INSERT INTO LINE (line_id, name, owner_id) VALUES (1, 'Ramal São Bento - Campanhã', 1);
INSERT INTO LINE (line_id, name, owner_id) VALUES (2, 'Ramal Camapanhã - Nine', 1);
INSERT INTO LINE (line_id, name, owner_id) VALUES (3, 'Ramal Nine - Barcelos', 1);
INSERT INTO LINE (line_id, name, owner_id) VALUES (4, 'Ramal Barcelos - Viana', 1);
INSERT INTO LINE (line_id, name, owner_id) VALUES (5, 'Ramal viana - Caminha', 1);
INSERT INTO LINE (line_id, name, owner_id) VALUES (6, 'Ramal Caminha - Torre', 1);
INSERT INTO LINE (line_id, name, owner_id) VALUES (7, 'Ramal Torre - Valença', 1);
INSERT INTO LINE (line_id, name, owner_id) VALUES (8, 'Ramal Caminha - Torre', 1);
INSERT INTO LINE (line_id, name, owner_id) VALUES (9, 'Ramal Torre - Valença', 1);
INSERT INTO LINE (line_id, name, owner_id) VALUES (21, 'Ramal Contumil - São Gemil', 1);
INSERT INTO LINE (line_id, name, owner_id) VALUES (22, 'Ramal São Gemil - São Mamede de Infesta', 1);
INSERT INTO LINE (line_id, name, owner_id) VALUES (23, 'Ramal São Mamede de Infesta - Leça do Balio', 1);
INSERT INTO LINE (line_id, name, owner_id) VALUES (24, 'Ramal Leça do Balio - Leixões', 1);
INSERT INTO LINE (line_id, name, owner_id) VALUES (30, 'Ramal Braga', 1);
INSERT INTO LINE (line_id, name, owner_id) VALUES (31, 'Ramal Nine - Manzagão', 1);
INSERT INTO LINE (line_id, name, owner_id) VALUES (32, 'Ramal Manzagão - Cerqueiral', 1);
INSERT INTO LINE (line_id, name, owner_id) VALUES (35, 'Ramal Cerqueiral - Gemieira', 1);
INSERT INTO LINE (line_id, name, owner_id) VALUES (36, 'Ramal Gemieira - Paredes de Coura', 1);
INSERT INTO LINE (line_id, name, owner_id) VALUES (37, 'Ramal Paredes de Coura - Valença', 1);
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
    (987, 'Sgnss 12 94 455 2', 3, 'Emef', 2, 'Duplo', 21.6, 68.4, 76.3, 18.116, 2.95, 1.03, 120);
INSERT INTO WAGON_MODEL (wagon_model_id, name, wagon_type, constructor, number_bogies, bogies, tare_t, payload_t, volume_m3, length_m, width_m, height_m, max_speed_kmh) VALUES
    (988, 'Sgnss 12 94 455 2', 3, 'Emef', 2, 'Duplo', 21.6, 68.4, 76.3, 18.116, 2.95, 1.03, 120);
INSERT INTO WAGON_MODEL (wagon_model_id, name, wagon_type, constructor, number_bogies, bogies, tare_t, payload_t, volume_m3, length_m, width_m, height_m, max_speed_kmh) VALUES
    (1525, 'Zaes 81 94 788', 4, 'Equimetal', 2, 'Duplo', 22.9, 57.1, 64.6, 13.8, 2.95, 4.226, 120);
INSERT INTO WAGON_MODEL (wagon_model_id, name, wagon_type, constructor, number_bogies, bogies, tare_t, payload_t, volume_m3, length_m, width_m, height_m, max_speed_kmh) VALUES
    (1523, 'Zaes 81 94 788', 4, 'Equimetal', 2, 'Duplo', 22.9, 57.1, 64.6, 13.8, 2.95, 4.226, 120);
INSERT INTO WAGON_MODEL (wagon_model_id, name, wagon_type, constructor, number_bogies, bogies, tare_t, payload_t, volume_m3, length_m, width_m, height_m, max_speed_kmh) VALUES
    (1212, 'Kbs 41 94 333', 5, 'Equimetal', 2, 'Simples', 14.2, 25.8, 62.7, 14.02, 2.842, 3.3, 120);
COMMIT;

-- LOCOMOTIVE depende de USER (operator_id) e LOCOMOTIVE_TYPE
INSERT INTO LOCOMOTIVE (locomotive_id, operator_id, locomotive_type, name, brand, model, year, power_kw, acceleration_ms2, length_m, width_m, height_m, max_weight_t, max_speed_kmh, operational_speed_kmh, total_weight_t, num_bogies, bogie_type, traction_kn, start_facility) VALUES
    (5621, 2, 1, 'Inês', 'Siemens', 'Eurosprinter', 1995, 5600, 0.5, 19.2, 3.0, 4.375, 87, 220, 70, 87, 2, 'Bo-Bo', 300, 50);
INSERT INTO LOCOMOTIVE (locomotive_id, operator_id, locomotive_type, name, brand, model, year, power_kw, acceleration_ms2, length_m, width_m, height_m, max_weight_t, max_speed_kmh, operational_speed_kmh, total_weight_t, num_bogies, bogie_type, traction_kn, start_facility) VALUES
    (5623, 2, 1, 'Paz', 'Siemens', 'Eurosprinter', 1995, 5600, 0.4, 19.2, 3.0, 4.375, 87, 220, 70, 87, 2, 'Bo-Bo', 300, 50);
INSERT INTO LOCOMOTIVE (locomotive_id, operator_id, locomotive_type, name, brand, model, year, power_kw, acceleration_ms2, length_m, width_m, height_m, max_weight_t, max_speed_kmh, operational_speed_kmh, total_weight_t, num_bogies, bogie_type, traction_kn, start_facility) VALUES
    (5630, 2, 1, 'Helena', 'Siemens', 'Eurosprinter', 1996, 5600, 0.45, 19.2, 3.0, 4.375, 87, 220, 70, 87, 2, 'Bo-Bo', 300, 50);
INSERT INTO LOCOMOTIVE (locomotive_id, operator_id, locomotive_type, name, brand, model, year, power_kw, acceleration_ms2, length_m, width_m, height_m, max_weight_t, max_speed_kmh, operational_speed_kmh, total_weight_t, num_bogies, bogie_type, traction_kn, start_facility) VALUES
    (1903, 2, 2, 'Eva', 'Sorefame - Alsthom', 'CP 1900', 1981, 1623, 0.3, 19.084, 3.062, 4.31, 117, 100, 42.5, 117, 2, 'Co-Co', 396, 50);
INSERT INTO LOCOMOTIVE (locomotive_id, operator_id, locomotive_type, name, brand, model, year, power_kw, acceleration_ms2, length_m, width_m, height_m, max_weight_t, max_speed_kmh, operational_speed_kmh, total_weight_t, num_bogies, bogie_type, traction_kn, start_facility) VALUES
    (5034, 2, 2, 'Adriana', 'Stadler', 'E4000', 2017, 3178, 0.3, 23.02, 3000, 4.264, 124, 120, 100, 124, 3, 'Co-Co', 400, 50);
INSERT INTO LOCOMOTIVE (locomotive_id, operator_id, locomotive_type, name, brand, model, year, power_kw, acceleration_ms2, length_m, width_m, height_m, max_weight_t, max_speed_kmh, operational_speed_kmh, total_weight_t, num_bogies, bogie_type, traction_kn, start_facility) VALUES
    (5036, 2, 2, 'Marina', 'Stadler', 'E4000', 2017, 3178, 0.3, 23.02, 3000, 4.264, 124, 120, 100, 124, 3, 'Co-Co', 400, 50);
INSERT INTO LOCOMOTIVE (locomotive_id, operator_id, locomotive_type, name, brand, model, year, power_kw, acceleration_ms2, length_m, width_m, height_m, max_weight_t, max_speed_kmh, operational_speed_kmh, total_weight_t, num_bogies, bogie_type, traction_kn, start_facility) VALUES
    (335.001, 3, 2, 'No_Name', 'Stadler', 'E4000', 2019, 3178, 0.3, 23.02, 3000, 4.264, 124, 120, 100, 124, 3, 'Co-Co', 400, 5);
INSERT INTO LOCOMOTIVE (locomotive_id, operator_id, locomotive_type, name, brand, model, year, power_kw, acceleration_ms2, length_m, width_m, height_m, max_weight_t, max_speed_kmh, operational_speed_kmh, total_weight_t, num_bogies, bogie_type, traction_kn, start_facility) VALUES
    (335.003, 3, 2, 'No_Name', 'Stadler', 'E4000', 2019, 3178, 0.3, 23.02, 3000, 4.264, 124, 120, 100, 124, 3, 'Co-Co', 400, 5);
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
INSERT INTO LINE_SEGMENT (segment_id, line_id, length_km, electrif_type, gauge_id, max_weight_kg_m, speed_limit_kmh, track_number, position)
VALUES (1, 1, 2618, 1, 1, 8000, 100, 4, 1);

INSERT INTO LINE_SEGMENT VALUES (3, 2, 2443, 1, 1, 8000, 100, 4, 1);

INSERT INTO LINE_SEGMENT VALUES (10, 3, 26560, 1, 1, 8000, 100, 2, 1);

INSERT INTO LINE_SEGMENT VALUES (11, 3, 10000, 1, 1, 8000, 100, 2, 2);

INSERT INTO LINE_SEGMENT VALUES (15, 4, 5286, 1, 1, 8000, 100, 2, 1);

INSERT INTO LINE_SEGMENT VALUES (16, 4, 6000, 1, 1, 8000, 100, 2, 2);

INSERT INTO LINE_SEGMENT VALUES (14, 5, 10387, 1, 1, 8000, 100, 2, 1);

INSERT INTO LINE_SEGMENT VALUES (12, 5, 12000, 1, 1, 8000, 100, 2, 2);

INSERT INTO LINE_SEGMENT VALUES (13, 5, 3100, 1, 1, 8000, 100, 2, 3);

INSERT INTO LINE_SEGMENT VALUES (20, 6, 4890, 1, 1, 6400, 100, 2, 1);

INSERT INTO LINE_SEGMENT VALUES (18, 7, 6000, 1, 1, 8000, 100, 1, 1);

INSERT INTO LINE_SEGMENT VALUES (21, 7, 5000, 1, 1, 8000, 100, 1, 2);

INSERT INTO LINE_SEGMENT VALUES (22, 7, 12000, 1, 1, 8000, 100, 1, 3);

INSERT INTO LINE_SEGMENT VALUES (25, 8, 20829, 1, 1, 8000, 100, 1, 1);

INSERT INTO LINE_SEGMENT VALUES (26, 9, 4264, 1, 1, 8000, 100, 1, 1);

INSERT INTO LINE_SEGMENT VALUES (30, 21, 3883, 1, 1, 8000, 100, 2, 1);

INSERT INTO LINE_SEGMENT VALUES (31, 22, 1174, 1, 1, 8400, 100, 2, 1);

INSERT INTO LINE_SEGMENT VALUES (32, 22, 2534, 1, 1, 8000, 100, 2, 2);

INSERT INTO LINE_SEGMENT VALUES (33, 23, 1566, 1, 1, 8000, 100, 2, 1);

INSERT INTO LINE_SEGMENT VALUES (34, 23, 1453, 1, 1, 8000, 100, 2, 2);

INSERT INTO LINE_SEGMENT VALUES (35, 24, 3597, 1, 1, 8100, 100, 2, 1);

INSERT INTO LINE_SEGMENT VALUES (36, 24, 4334, 1, 1, 8000, 100, 2, 2);

INSERT INTO LINE_SEGMENT VALUES (50, 30, 3555, 1, 1, 8000, 100, 2, 1);

INSERT INTO LINE_SEGMENT VALUES (51, 31, 1222, 1, 1, 8000, 100, 2, 5);

INSERT INTO LINE_SEGMENT VALUES (52, 31, 1760, 1, 1, 8000, 100, 2, 4);

INSERT INTO LINE_SEGMENT VALUES (53, 31, 1720, 1, 1, 8000, 100, 2, 3);

INSERT INTO LINE_SEGMENT VALUES (54, 31, 3350, 1, 1, 8000, 100, 2, 2);

INSERT INTO LINE_SEGMENT VALUES (55, 31, 3470, 1, 1, 8000, 100, 2, 1);

INSERT INTO LINE_SEGMENT VALUES (58, 32, 8050, 1, 1, 8000, 100, 2, 1);

INSERT INTO LINE_SEGMENT VALUES (59, 35, 22320, 1, 1, 8000, 100, 2, 1);

INSERT INTO LINE_SEGMENT VALUES (60, 36, 16310, 1, 1, 8000, 100, 2, 1);

INSERT INTO LINE_SEGMENT VALUES (61, 37, 15200, 1, 1, 8000, 100, 2, 1);
COMMIT;


-- WAGON_GAUGE depende de WAGON_MODEL e GAUGE
INSERT INTO WAGON_GAUGE (wagon_id, gauge_id) VALUES (1245, 1);
INSERT INTO WAGON_GAUGE (wagon_id, gauge_id) VALUES (1278, 1);
INSERT INTO WAGON_GAUGE (wagon_id, gauge_id) VALUES (1325, 1);
INSERT INTO WAGON_GAUGE (wagon_id, gauge_id) VALUES (1104, 1);
INSERT INTO WAGON_GAUGE (wagon_id, gauge_id) VALUES (985, 1);
INSERT INTO WAGON_GAUGE (wagon_id, gauge_id) VALUES (987, 1);
INSERT INTO WAGON_GAUGE (wagon_id, gauge_id) VALUES (988, 2);
INSERT INTO WAGON_GAUGE (wagon_id, gauge_id) VALUES (1525, 1);
INSERT INTO WAGON_GAUGE (wagon_id, gauge_id) VALUES (1523, 2);
INSERT INTO WAGON_GAUGE (wagon_id, gauge_id) VALUES (1212, 1);
COMMIT;
-- WAGON depende de WAGON_MODEL e USER (operator_id)
-- ATENÇÃO: Devido a um erro no schema, WAGON.wagon_id deve corresponder a WAGON_MODEL.wagon_model_id
-- A FK correta deveria ser WAGON.wagon_model -> WAGON_MODEL.wagon_model_id
-- Mas o schema define WAGON.wagon_id -> WAGON_MODEL.wagon_model_id
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (3563077, 1104, 2, 1987, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (3563078, 1104, 2, 1987, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (3563079, 1104, 2, 1987, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (3563080, 1104, 2, 1987, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (3563081, 1104, 2, 1987, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (3563082, 1104, 2, 1987, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (3563083, 1104, 2, 1987, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (3563084, 1104, 2, 1987, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (3563085, 1104, 2, 1987, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (3563086, 1104, 2, 1987, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (3563087, 1104, 2, 1987, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (3563088, 1104, 2, 1987, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (3563089, 1104, 2, 1987, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (3563090, 1104, 2, 1987, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (3563091, 1104, 2, 1987, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (3563092, 1104, 2, 1987, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (823045, 1245, 2, 1990, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (823046, 1245, 2, 1990, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (823047, 1245, 2, 1990, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (823048, 1245, 2, 1990, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (741001, 1278, 2, 1977, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (741002, 1278, 2, 1977, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (741003, 1278, 2, 1977, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (741004, 1278, 2, 1977, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (741005, 1278, 2, 1977, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (741006, 1278, 2, 1977, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (1811010, 1325, 2, 1977, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (1811011, 1325, 2, 1977, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (1811012, 1325, 2, 1977, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (1811013, 1325, 2, 1977, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (1811014, 1325, 2, 1977, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (3330001, 1212, 2, 2005, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (3330002, 1212, 2, 2005, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (3330003, 1212, 2, 2005, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (3330004, 1212, 2, 2005, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (3330005, 1212, 2, 2005, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (3330006, 1212, 2, 2005, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (3330007, 1212, 2, 2005, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (3330008, 1212, 2, 2005, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (3330009, 1212, 2, 2005, 50);
INSERT INTO WAGON (wagon_id, wagon_model, operator_id, service_year, start_facility) VALUES (3330010, 1212, 2, 2005, 50);
COMMIT;

-- LOCOMOTIVE_DIESEL depende de LOCOMOTIVE
INSERT INTO LOCOMOTIVE_DIESEL (locomotive_id, fuel_capacity_l) VALUES (1903, 4882);
INSERT INTO LOCOMOTIVE_DIESEL (locomotive_id, fuel_capacity_l) VALUES (5034, 6700);
INSERT INTO LOCOMOTIVE_DIESEL (locomotive_id, fuel_capacity_l) VALUES (5036, 6700);
INSERT INTO LOCOMOTIVE_DIESEL (locomotive_id, fuel_capacity_l) VALUES (335.001, 6700);
INSERT INTO LOCOMOTIVE_DIESEL (locomotive_id, fuel_capacity_l) VALUES (335.003, 6700);
COMMIT;
--LOCOMOTIVE_ELECTRIC depende de LOCOMOTIVE
INSERT INTO LOCOMOTIVE_ELECTRIC (locomotive_id, voltage, frequency_hz) VALUES (5621, 25, 50);
INSERT INTO LOCOMOTIVE_ELECTRIC (locomotive_id, voltage, frequency_hz) VALUES (5623, 25, 50);
INSERT INTO LOCOMOTIVE_ELECTRIC (locomotive_id, voltage, frequency_hz) VALUES (5630, 25, 50);
COMMIT;
-- LOCOMOTIVE_GAUGE depende de LOCOMOTIVE e GAUGE
INSERT INTO LOCOMOTIVE_GAUGE (locomotive_id, gauge_id) VALUES (5621, 1);
INSERT INTO LOCOMOTIVE_GAUGE (locomotive_id, gauge_id) VALUES (5623, 1);
INSERT INTO LOCOMOTIVE_GAUGE (locomotive_id, gauge_id) VALUES (5623, 2);
INSERT INTO LOCOMOTIVE_GAUGE (locomotive_id, gauge_id) VALUES (5630, 1);
INSERT INTO LOCOMOTIVE_GAUGE (locomotive_id, gauge_id) VALUES (1903, 1);
INSERT INTO LOCOMOTIVE_GAUGE (locomotive_id, gauge_id) VALUES (5034, 1);
INSERT INTO LOCOMOTIVE_GAUGE (locomotive_id, gauge_id) VALUES (5036, 1);
INSERT INTO LOCOMOTIVE_GAUGE (locomotive_id, gauge_id) VALUES (335.001, 1);
INSERT INTO LOCOMOTIVE_GAUGE (locomotive_id, gauge_id) VALUES (335.003, 1);
INSERT INTO LOCOMOTIVE_GAUGE (locomotive_id, gauge_id) VALUES (335.003, 2);
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
INSERT INTO TRAIN (train_id, code, departure, destination, operator_id) VALUES (5421, 'Medway_5421', 50, 11, 2);
INSERT INTO TRAIN (train_id, code, departure, destination, operator_id) VALUES (5435, 'Medway_5435', 11, 50, 2);
INSERT INTO TRAIN (train_id, code, departure, destination, operator_id) VALUES (5437, 'Medway_5437', 11, 50, 2);

COMMIT;

COMMIT;
-- PARKING_TRACK depende de LINE e FACILITY
INSERT INTO PARKING_TRACK (parking_track_id, line_id, facility_id, name, capacity_wagons) VALUES (1, 1, 5, 'Track A', 10);
INSERT INTO PARKING_TRACK (parking_track_id, line_id, facility_id, name, capacity_wagons) VALUES (2, 2, 7, 'Track B', 20);
INSERT INTO PARKING_TRACK (parking_track_id, line_id, facility_id, name, capacity_wagons) VALUES (3, 3, 8, 'Track C', 15);
INSERT INTO PARKING_TRACK (parking_track_id, line_id, facility_id, name, capacity_wagons) VALUES (4, 4, 11, 'Track D', 18);
INSERT INTO PARKING_TRACK (parking_track_id, line_id, facility_id, name, capacity_wagons) VALUES (5, 5, 16, 'Track E', 12);
INSERT INTO PARKING_TRACK (parking_track_id, line_id, facility_id, name, capacity_wagons) VALUES (6, 6, 48, 'Track F', 13);
INSERT INTO PARKING_TRACK (parking_track_id, line_id, facility_id, name, capacity_wagons) VALUES (7, 7, 45, 'Track G', 14);
INSERT INTO PARKING_TRACK (parking_track_id, line_id, facility_id, name, capacity_wagons) VALUES (8, 8, 43, 'Track H', 20);
COMMIT;
-- ============================================================
-- NÍVEL 3: Tabelas que dependem do NÍVEL 2
-- ============================================================

-- ==========================================
-- Ramal 1: São Bento (7) → Campanhã (5)
-- ==========================================
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (1, 7);
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (1, 5);

-- ==========================================
-- Ramal 2: Campanhã (5) → Contumil (13)
-- ==========================================
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (3, 5);
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (3, 13);

-- ==========================================
-- Ramal 3: Contumil (13) → Nine (20)
-- ==========================================
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (10, 13);
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (10, 20);

-- ==========================================
-- Ramal 4: Nine (20) → Barcelos (8)
-- ==========================================
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (15, 20);
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (15, 8);

-- ==========================================
-- Ramal 5: Barcelos (8) → Darque (12)
-- ==========================================
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (14, 8);
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (14, 12);

-- ==========================================
-- Ramal 6: Darque (12) → Viana do Castelo (17)
-- ==========================================
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (12, 12);
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (12, 17);

-- ==========================================
-- Ramal 7: Viana (17) → Caminha (21)
-- ==========================================
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (20, 17);
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (20, 21);

-- ==========================================
-- Ramal 8: Caminha (21) → S. Pedro da Torre (16)
-- ==========================================
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (25, 21);
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (25, 16);

-- ==========================================
-- Ramal 9: Torre (16) → Valença (11)
-- ==========================================
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (26, 16);
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (26, 11);

-- ==========================================
-- Ramal 21: Contumil (13) → São Gemil (43)
-- ==========================================
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (30, 13);
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (30, 43);

-- ==========================================
-- Ramal 22: São Gemil (43) → São Mamede (45)
-- ==========================================
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (31, 43);
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (31, 45);

-- ==========================================
-- Ramal 23: São Mamede (45) → Leça do Balio (48)
-- ==========================================
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (33, 45);
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (33, 48);

-- ==========================================
-- Ramal 24: Leça do Balio (48) → Leixões (50)
-- ==========================================
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (35, 48);
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (35, 50);

-- ==========================================
-- RAMAL 30: Manzagão (31) → Braga (30)
-- LINE_SEGMENT.segment_id = 50
-- ==========================================
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (50, 31);
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (50, 30);

-- ==========================================
-- RAMAL 31: Nine (20) → Manzagão (31)
-- LINE_SEGMENT.segment_id = 55
-- ==========================================
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (55, 20);
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (55, 31);

-- ==========================================
-- RAMAL 32: Manzagão (31) → Cerqueiral (32)
-- LINE_SEGMENT.segment_id = 58
-- ==========================================
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (58, 31);
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (58, 32);

-- ==========================================
-- RAMAL 35: Cerqueiral (32) → Gemieira (33)
-- LINE_SEGMENT.segment_id = 59
-- ==========================================
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (59, 32);
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (59, 33);

-- ==========================================
-- RAMAL 36: Gemieira (33) → Paredes de Coura (35)
-- LINE_SEGMENT.segment_id = 60
-- ==========================================
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (60, 33);
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (60, 35);

-- ==========================================
-- RAMAL 37: Paredes de Coura (35) → Valença (11)
-- LINE_SEGMENT.segment_id = 61
-- ==========================================
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (61, 35);
INSERT INTO LINE_SEGMENT_FACILITY (line_segment_id, facility_id) VALUES (61, 11);

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
    (2001, 1, TO_TIMESTAMP('2025-10-03 08:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-03 18:00:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, 'SCHEDULED');
INSERT INTO TRAIN_SERVICE (service_id, train_id, departure_date, arrival_date, train_driver_id, status) VALUES
    (2002, 2, TO_TIMESTAMP('2025-10-03 08:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-03 18:00:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, 'SCHEDULED');
INSERT INTO TRAIN_SERVICE (service_id, train_id, departure_date, arrival_date, train_driver_id, status) VALUES
    (2003, 3, TO_TIMESTAMP('2025-10-03 08:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-03 18:00:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, 'SCHEDULED');
INSERT INTO TRAIN_SERVICE (service_id, train_id, departure_date, arrival_date, train_driver_id, status) VALUES
    (2004, 4, TO_TIMESTAMP('2025-10-03 08:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-03 18:00:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, 'SCHEDULED');
INSERT INTO TRAIN_SERVICE (service_id, train_id, departure_date, arrival_date, train_driver_id, status) VALUES
    (2005, 5, TO_TIMESTAMP('2025-10-03 08:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-03 18:00:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, 'SCHEDULED');
INSERT INTO TRAIN_SERVICE (service_id, train_id, departure_date, arrival_date, train_driver_id, status) VALUES
    (2006, 6, TO_TIMESTAMP('2025-10-03 08:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-03 18:00:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, 'SCHEDULED');
INSERT INTO TRAIN_SERVICE (service_id, train_id, departure_date, arrival_date, train_driver_id, status) VALUES
    (2007, 7, TO_TIMESTAMP('2025-10-03 08:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-03 18:00:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, 'SCHEDULED');
INSERT INTO TRAIN_SERVICE (service_id, train_id, departure_date, arrival_date, train_driver_id, status) VALUES
    (2050, 5421, TO_TIMESTAMP('2025-10-06 08:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-06 18:00:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, 'SCHEDULED');
INSERT INTO TRAIN_SERVICE (service_id, train_id, departure_date, arrival_date, train_driver_id, status) VALUES
    (2051, 5435, TO_TIMESTAMP('2025-10-06 08:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-06 18:00:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, 'SCHEDULED');

-- FREIGHT depende de FACILITY (origin_id, destination_id) e USER (operator_id)
INSERT INTO FREIGHT (freight_id, code, origin_id, destination_id, operator_id)
VALUES (1001, 'FRG-2001-001', 50, 12, 2);

INSERT INTO FREIGHT (freight_id, code, origin_id, destination_id, operator_id)
VALUES (1002, 'FRG-2002-001', 13, 11, 2);

INSERT INTO FREIGHT (freight_id, code, origin_id, destination_id, operator_id)
VALUES (1003, 'FRG-2003-001', 50, 11, 2);

INSERT INTO FREIGHT (freight_id, code, origin_id, destination_id, operator_id)
VALUES (1004, 'FRG-2004-001', 50, 21, 2);

INSERT INTO FREIGHT (freight_id, code, origin_id, destination_id, operator_id)
VALUES (1005, 'FRG-2005-001', 20, 11, 2);

INSERT INTO FREIGHT (freight_id, code, origin_id, destination_id, operator_id)
VALUES (1006, 'FRG-2006-001', 12, 50, 2);

INSERT INTO FREIGHT (freight_id, code, origin_id, destination_id, operator_id)
VALUES (1007, 'FRG-2007-001', 50, 5, 2);

INSERT INTO FREIGHT (freight_id, code, origin_id, destination_id, operator_id)
VALUES (1050, 'FRG-2050-001', 12, 50, 2);

INSERT INTO FREIGHT (freight_id, code, origin_id, destination_id, operator_id)
VALUES (1051, 'FRG-2051-001', 11, 50, 2);

COMMIT;

-- FREIGHT_COMPOSITION depende de FREIGHT e WAGON
-- Freight 1001 (para service 2001) - 5 vagões
INSERT INTO FREIGHT_COMPOSITION (freight_id, wagon_id, order_line_id, position_no, weight_kg)
VALUES (1001, 3330001, 1, 1, 45000);

INSERT INTO FREIGHT_COMPOSITION (freight_id, wagon_id, order_line_id, position_no, weight_kg)
VALUES (1001, 3330002, 2, 2, 47000);

INSERT INTO FREIGHT_COMPOSITION (freight_id, wagon_id, order_line_id, position_no, weight_kg)
VALUES (1001, 3330004, 3, 3, 46500);

INSERT INTO FREIGHT_COMPOSITION (freight_id, wagon_id, order_line_id, position_no, weight_kg)
VALUES (1001, 3330005, 4, 4, 48000);

INSERT INTO FREIGHT_COMPOSITION (freight_id, wagon_id, order_line_id, position_no, weight_kg)
VALUES (1001, 3330006, 5, 5, 44500);

-- Freight 1002 (para service 2002) - 1 vagão
INSERT INTO FREIGHT_COMPOSITION (freight_id, wagon_id, order_line_id, position_no, weight_kg)
VALUES (1002, 3563089, 1, 1, 42000);

-- Freight 1003 (para service 2003) - 2 vagões
INSERT INTO FREIGHT_COMPOSITION (freight_id, wagon_id, order_line_id, position_no, weight_kg)
VALUES (1003, 1811011, 2, 1, 50000);

INSERT INTO FREIGHT_COMPOSITION (freight_id, wagon_id, order_line_id, position_no, weight_kg)
VALUES (1003, 1811012, 3, 2, 51000);

-- Freight 1004 (para service 2004) - 1 vagão
INSERT INTO FREIGHT_COMPOSITION (freight_id, wagon_id, order_line_id, position_no, weight_kg)
VALUES (1004, 1811013, 4, 1, 49500);

-- Freight 1005 (para service 2005) - 4 vagões
INSERT INTO FREIGHT_COMPOSITION (freight_id, wagon_id, order_line_id, position_no, weight_kg)
VALUES (1005, 3563077, 5, 1, 38000);

INSERT INTO FREIGHT_COMPOSITION (freight_id, wagon_id, order_line_id, position_no, weight_kg)
VALUES (1005, 3563078, 1, 2, 39000);

INSERT INTO FREIGHT_COMPOSITION (freight_id, wagon_id, order_line_id, position_no, weight_kg)
VALUES (1005, 3563079, 2, 3, 37500);

INSERT INTO FREIGHT_COMPOSITION (freight_id, wagon_id, order_line_id, position_no, weight_kg)
VALUES (1005, 3563080, 3, 4, 40000);

-- Freight 1006 (para service 2006) - 2 vagões
INSERT INTO FREIGHT_COMPOSITION (freight_id, wagon_id, order_line_id, position_no, weight_kg)
VALUES (1006, 3330003, 4, 1, 46000);

INSERT INTO FREIGHT_COMPOSITION (freight_id, wagon_id, order_line_id, position_no, weight_kg)
VALUES (1006, 3330007, 5, 2, 45500);

-- Freight 1007 (para service 2007) - 3 vagões
INSERT INTO FREIGHT_COMPOSITION (freight_id, wagon_id, order_line_id, position_no, weight_kg)
VALUES (1007, 3563090, 1, 1, 39500);

INSERT INTO FREIGHT_COMPOSITION (freight_id, wagon_id, order_line_id, position_no, weight_kg)
VALUES (1007, 3563091, 2, 2, 38500);

INSERT INTO FREIGHT_COMPOSITION (freight_id, wagon_id, order_line_id, position_no, weight_kg)
VALUES (1007, 3563092, 3, 3, 41000);

-- Freight 1050 (para service 2050) - 5 vagões
INSERT INTO FREIGHT_COMPOSITION (freight_id, wagon_id, order_line_id, position_no, weight_kg)
VALUES (1050, 3330001, 4, 1, 45500);

INSERT INTO FREIGHT_COMPOSITION (freight_id, wagon_id, order_line_id, position_no, weight_kg)
VALUES (1050, 3330002, 5, 2, 47500);

INSERT INTO FREIGHT_COMPOSITION (freight_id, wagon_id, order_line_id, position_no, weight_kg)
VALUES (1050, 3330004, 1, 3, 46000);

INSERT INTO FREIGHT_COMPOSITION (freight_id, wagon_id, order_line_id, position_no, weight_kg)
VALUES (1050, 3330005, 2, 4, 48500);

INSERT INTO FREIGHT_COMPOSITION (freight_id, wagon_id, order_line_id, position_no, weight_kg)
VALUES (1050, 3330006, 3, 5, 44000);

-- Freight 1051 (para service 2051) - 6 vagões
INSERT INTO FREIGHT_COMPOSITION (freight_id, wagon_id, order_line_id, position_no, weight_kg)
VALUES (1051, 1811011, 4, 1, 50500);

INSERT INTO FREIGHT_COMPOSITION (freight_id, wagon_id, order_line_id, position_no, weight_kg)
VALUES (1051, 1811012, 5, 2, 51500);

INSERT INTO FREIGHT_COMPOSITION (freight_id, wagon_id, order_line_id, position_no, weight_kg)
VALUES (1051, 3563077, 1, 3, 38500);

INSERT INTO FREIGHT_COMPOSITION (freight_id, wagon_id, order_line_id, position_no, weight_kg)
VALUES (1051, 3563078, 2, 4, 39500);

INSERT INTO FREIGHT_COMPOSITION (freight_id, wagon_id, order_line_id, position_no, weight_kg)
VALUES (1051, 3563079, 3, 5, 37000);

INSERT INTO FREIGHT_COMPOSITION (freight_id, wagon_id, order_line_id, position_no, weight_kg)
VALUES (1051, 3563080, 4, 6, 40500);

COMMIT;

-- TRAIN_SERVICE_FREIGHT depende de TRAIN_SERVICE e FREIGHT
INSERT INTO TRAIN_SERVICE_FREIGHT (service_id, freight_id, position_no)
VALUES (2001, 1001, 1);

INSERT INTO TRAIN_SERVICE_FREIGHT (service_id, freight_id, position_no)
VALUES (2002, 1002, 1);

INSERT INTO TRAIN_SERVICE_FREIGHT (service_id, freight_id, position_no)
VALUES (2003, 1003, 1);

INSERT INTO TRAIN_SERVICE_FREIGHT (service_id, freight_id, position_no)
VALUES (2004, 1004, 1);

INSERT INTO TRAIN_SERVICE_FREIGHT (service_id, freight_id, position_no)
VALUES (2005, 1005, 1);

INSERT INTO TRAIN_SERVICE_FREIGHT (service_id, freight_id, position_no)
VALUES (2006, 1006, 1);

INSERT INTO TRAIN_SERVICE_FREIGHT (service_id, freight_id, position_no)
VALUES (2007, 1007, 1);

INSERT INTO TRAIN_SERVICE_FREIGHT (service_id, freight_id, position_no)
VALUES (2050, 1050, 1);

INSERT INTO TRAIN_SERVICE_FREIGHT (service_id, freight_id, position_no)
VALUES (2051, 1051, 1);

COMMIT;


-- PARKING_QUEUE depende de PARKING_TRACK e WAGON
INSERT INTO PARKING_QUEUE (parking_queue_id, parking_track_id, wagon_id, position_no, length_no, status) VALUES (1, 1, 823045, 1, 1,  'WAITING');
INSERT INTO PARKING_QUEUE (parking_queue_id, parking_track_id, wagon_id, position_no, length_no, status) VALUES (2, 2, 741001, 1, 1, 'WAITING');
INSERT INTO PARKING_QUEUE (parking_queue_id, parking_track_id, wagon_id, position_no, length_no, status) VALUES (3, 3, 1811010, 1, 1, 'WAITING');
INSERT INTO PARKING_QUEUE (parking_queue_id, parking_track_id, wagon_id, position_no, length_no, status) VALUES (4, 4, 3563078, 1, 1, 'WAITING');
INSERT INTO PARKING_QUEUE (parking_queue_id, parking_track_id, wagon_id, position_no, length_no, status) VALUES (5, 5, 3563090, 1, 1, 'WAITING');
INSERT INTO PARKING_QUEUE (parking_queue_id, parking_track_id, wagon_id, position_no, length_no, status) VALUES (6, 6, 3563090, 1, 1, 'WAITING');
INSERT INTO PARKING_QUEUE (parking_queue_id, parking_track_id, wagon_id, position_no, length_no, status) VALUES (7, 7, 3563090, 2000, 864, 'WAITING');
INSERT INTO PARKING_QUEUE (parking_queue_id, parking_track_id, wagon_id, position_no, length_no, status) VALUES (8, 8, 3563090, 11000, 266, 'WAITING');

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
-- 2001
INSERT INTO SERVICE_LOCOMOTIVE (service_id, locomotive_id, position_no)
VALUES (2001, 5621, 1);

-- 2002
INSERT INTO SERVICE_LOCOMOTIVE (service_id, locomotive_id, position_no)
VALUES (2002, 5623, 1);

-- 2003
INSERT INTO SERVICE_LOCOMOTIVE (service_id, locomotive_id, position_no)
VALUES (2003, 5630, 1);

-- 2004
INSERT INTO SERVICE_LOCOMOTIVE (service_id, locomotive_id, position_no)
VALUES (2004, 1903, 1);

-- 2005
INSERT INTO SERVICE_LOCOMOTIVE (service_id, locomotive_id, position_no)
VALUES (2005, 5621, 1);

-- 2006
INSERT INTO SERVICE_LOCOMOTIVE (service_id, locomotive_id, position_no)
VALUES (2006, 5623, 1);

-- 2007
INSERT INTO SERVICE_LOCOMOTIVE (service_id, locomotive_id, position_no)
VALUES (2007, 5630, 1);

-- 2050
INSERT INTO SERVICE_LOCOMOTIVE (service_id, locomotive_id, position_no)
VALUES (2050, 5621, 1);

-- 2051
INSERT INTO SERVICE_LOCOMOTIVE (service_id, locomotive_id, position_no)
VALUES (2051, 5630, 1);

COMMIT;

-- TRAIN_STOP depende de TRAIN_SERVICE, FACILITY e PARKING_TRACK
INSERT INTO TRAIN_STOP (train_stop_id, service_id, facility_id, parking_track, position, sched_arr, sched_dep, planned_stop_time) VALUES
    (1, 2001, 5, 1, 1, TO_TIMESTAMP('2025-11-01 08:30:00', 'YYYY-MM-DD HH24:MI:SS'),
     TO_TIMESTAMP('2025-11-01 08:45:00', 'YYYY-MM-DD HH24:MI:SS'), 15);

INSERT INTO TRAIN_STOP (train_stop_id, service_id, facility_id, parking_track, position, sched_arr, sched_dep, planned_stop_time) VALUES
    (2, 2002, 7, 2, 1, TO_TIMESTAMP('2025-11-02 09:30:00', 'YYYY-MM-DD HH24:MI:SS'),
     TO_TIMESTAMP('2025-11-02 09:50:00', 'YYYY-MM-DD HH24:MI:SS'), 20);

INSERT INTO TRAIN_STOP (train_stop_id, service_id, facility_id, parking_track, position, sched_arr, sched_dep, planned_stop_time) VALUES
    (3, 2003, 8, 3, 1, TO_TIMESTAMP('2025-11-03 08:00:00', 'YYYY-MM-DD HH24:MI:SS'),
     TO_TIMESTAMP('2025-11-03 08:15:00', 'YYYY-MM-DD HH24:MI:SS'), 15);

INSERT INTO TRAIN_STOP (train_stop_id, service_id, facility_id, parking_track, position, sched_arr, sched_dep, planned_stop_time) VALUES
    (4, 2004, 16, 4, 1, TO_TIMESTAMP('2025-11-04 06:30:00', 'YYYY-MM-DD HH24:MI:SS'),
     TO_TIMESTAMP('2025-11-04 06:45:00', 'YYYY-MM-DD HH24:MI:SS'), 15);

INSERT INTO TRAIN_STOP (train_stop_id, service_id, facility_id, parking_track, position, sched_arr, sched_dep, planned_stop_time) VALUES
    (5, 2005, 11, 5, 1, TO_TIMESTAMP('2025-11-05 05:30:00', 'YYYY-MM-DD HH24:MI:SS'),
     TO_TIMESTAMP('2025-11-05 05:45:00', 'YYYY-MM-DD HH24:MI:SS'), 15);

COMMIT;

-- PICK_WAVE depende de CART
INSERT INTO PICK_WAVE (wave_id, cart_id, strategy, status) VALUES (1, 1, 'FIFO', 'OPEN');
INSERT INTO PICK_WAVE (wave_id, cart_id, strategy, status) VALUES (2, 2, 'LIFO', 'IN_PROGRESS');
INSERT INTO PICK_WAVE (wave_id, cart_id, strategy, status) VALUES (3, 3, 'ZONE', 'COMPLETED');
INSERT INTO PICK_WAVE (wave_id, cart_id, strategy, status) VALUES (4, 4, 'FIFO', 'OPEN');
INSERT INTO PICK_WAVE (wave_id, cart_id, strategy, status) VALUES (5, 5, 'LIFO', 'CANCELLED');
COMMIT;

-- PICK_TASK depende de PICK_WAVE, ORDER_LINE e INVENTORY_BALANCE
INSERT INTO PICK_TASK (pick_id, wave_id, order_line_id, balance_id, qty, pos_seq) VALUES (1, 1, 1, 1, 5, 1);
INSERT INTO PICK_TASK (pick_id, wave_id, order_line_id, balance_id, qty, pos_seq) VALUES (2, 2, 2, 2, 10, 1);
INSERT INTO PICK_TASK (pick_id, wave_id, order_line_id, balance_id, qty, pos_seq) VALUES (3, 3, 3, 3, 15, 1);
INSERT INTO PICK_TASK (pick_id, wave_id, order_line_id, balance_id, qty, pos_seq) VALUES (4, 4, 4, 4, 5, 1);
INSERT INTO PICK_TASK (pick_id, wave_id, order_line_id, balance_id, qty, pos_seq) VALUES (5, 5, 5, 5, 3, 1);
COMMIT;