CREATE TABLE `GAUGE` (
  `gauge_id` INT PRIMARY KEY,
  `name` VARCHAR(255) NOT NULL,
  `size_mm` INT NOT NULL
);

CREATE TABLE `LINE` (
  `line_id` INT PRIMARY KEY AUTO_INCREMENT,
  `owner_id` INT NOT NULL
);

CREATE TABLE `LINE_SEGMENT` (
  `segment_id` INT PRIMARY KEY AUTO_INCREMENT,
  `line_id` INT NOT NULL,
  `length_km` DECIMAL(7,3) NOT NULL,
  `track_type_id` INT NOT NULL,
  `electrif_type_id` INT NOT NULL,
  `gauge_id` INT NOT NULL,
  `max_weight_kg_m` INT NOT NULL,
  `speed_limit_kmh` INT NOT NULL,
  `single_track` BOOLEAN NOT NULL DEFAULT true,
  `position` INT NOT NULL
);

CREATE TABLE `LINE_SEGMENT_FACILITY` (
  `segment_facility_id` INT PRIMARY KEY AUTO_INCREMENT,
  `line_segment_id` INT NOT NULL,
  `facility_id` INT NOT NULL
);

CREATE TABLE `TRACK_TYPE` (
  `track_type_id` INT PRIMARY KEY,
  `name` VARCHAR(10) NOT NULL
);

CREATE TABLE `PARKING_TRACK` (
  `parking_track_id` INT PRIMARY KEY AUTO_INCREMENT,
  `line_id` INT NOT NULL,
  `facility_id` INT NOT NULL,
  `name` VARCHAR(60) NOT NULL,
  `capacity_wagons` INT NOT NULL
);

CREATE TABLE `PARKING_QUEUE` (
  `parking_queue_id` INT PRIMARY KEY AUTO_INCREMENT,
  `parking_track_id` INT NOT NULL,
  `wagon_id` INT NOT NULL,
  `position_no` INT NOT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'WAITING'
);

CREATE TABLE `FACILITY` (
  `facility_id` INT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(120) NOT NULL,
  `facility_type_id` INT NOT NULL,
  `lat` DECIMAL(9,6) NOT NULL,
  `lon` DECIMAL(9,6) NOT NULL,
  `is_intermodal` BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE `FACILITY_TYPE` (
  `facility_type_id` INT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(120) NOT NULL
);

CREATE TABLE `FACILITY_FEATURE` (
  `facility_id` INT NOT NULL,
  `feature_id` INT NOT NULL,
  PRIMARY KEY (`facility_id`, `feature_id`)
);

CREATE TABLE `FACILITY_FEATURE_TYPE` (
  `feature_id` INT PRIMARY KEY NOT NULL,
  `name` VARCHAR(30) NOT NULL
);

CREATE TABLE `ELECTRIFICATION_TYPE` (
  `electrif_type_id` INT PRIMARY KEY,
  `name` VARCHAR(255) NOT NULL
);

CREATE TABLE `LOCOMOTIVE` (
  `locomotive_id` INT PRIMARY KEY AUTO_INCREMENT,
  `operator_id` INT NOT NULL,
  `locomotive_type` INT NOT NULL,
  `brand` VARCHAR(60) NOT NULL,
  `model` VARCHAR(60) NOT NULL,
  `year` INT NOT NULL,
  `power_kw` INT NOT NULL,
  `acceleration_ms2` DECIMAL(5,2) NOT NULL,
  `length_m` DECIMAL(5,2) NOT NULL,
  `width_m` DECIMAL(4,2) NOT NULL,
  `height_m` DECIMAL(4,2) NOT NULL,
  `max_weight_t` DECIMAL(6,2) NOT NULL,
  `total_weight_t` DECIMAL(6,2) NOT NULL,
  `num_bogies` INT NOT NULL,
  `created_at` TIMESTAMP(6) NOT NULL DEFAULT (CURRENT_TIMESTAMP(6)),
  `updated_at` TIMESTAMP(6) DEFAULT null
);

CREATE TABLE `LOCOMOTIVE_TYPE` (
  `locomotive_type_id` INT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(60) NOT NULL,
  `created_at` TIMESTAMP(6) NOT NULL DEFAULT (CURRENT_TIMESTAMP(6)),
  `updated_at` TIMESTAMP(6) DEFAULT null
);

CREATE TABLE `LOCOMOTIVE_DIESEL` (
  `locomotive_id` INT PRIMARY KEY,
  `fuel_capacity_l` DECIMAL(7,1) NOT NULL
);

CREATE TABLE `LOCOMOTIVE_GAUGE` (
  `locomotive_id` INT NOT NULL,
  `gauge_id` INT NOT NULL,
  PRIMARY KEY (`locomotive_id`, `gauge_id`)
);

CREATE TABLE `WAGON` (
  `wagon_id` INT PRIMARY KEY AUTO_INCREMENT,
  `operator_id` INT NOT NULL,
  `wagon_type` INT NOT NULL,
  `tare_t` DECIMAL(6,2) NOT NULL,
  `payload_t` DECIMAL(6,2) NOT NULL,
  `volume_m3` DECIMAL(7,2) NOT NULL,
  `length_m` DECIMAL(5,2) NOT NULL,
  `width_m` DECIMAL(4,2) NOT NULL,
  `height_m` DECIMAL(4,2) NOT NULL,
  `created_at` TIMESTAMP(6) NOT NULL DEFAULT (CURRENT_TIMESTAMP(6)),
  `updated_at` TIMESTAMP(6) DEFAULT null
);

CREATE TABLE `WAGON_TYPE` (
  `wagon_type_id` INT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(20) NOT NULL,
  `created_at` TIMESTAMP(6) NOT NULL DEFAULT (CURRENT_TIMESTAMP(6)),
  `updated_at` TIMESTAMP(6) DEFAULT null
);

CREATE TABLE `WAGON_GAUGE` (
  `wagon_id` INT NOT NULL,
  `gauge_id` INT NOT NULL,
  PRIMARY KEY (`wagon_id`, `gauge_id`)
);

CREATE TABLE `COMPANY` (
  `company_id` INT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(120) UNIQUE NOT NULL
);

CREATE TABLE `USER` (
  `user_id` INT PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR(60) UNIQUE NOT NULL,
  `email` VARCHAR(120) UNIQUE NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `company_id` INT NOT NULL,
  `is_active` BOOLEAN NOT NULL DEFAULT true,
  `created_at` TIMESTAMP(6) NOT NULL DEFAULT (CURRENT_TIMESTAMP(6))
);

CREATE TABLE `ROLE` (
  `role_id` INT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(30) UNIQUE NOT NULL
);

CREATE TABLE `USER_ROLE` (
  `user_id` INT NOT NULL,
  `role_id` INT NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`)
);

CREATE TABLE `ITEM` (
  `item_id` INT PRIMARY KEY AUTO_INCREMENT,
  `item_category_id` INT,
  `sku` VARCHAR(40) UNIQUE NOT NULL,
  `name` VARCHAR(160) NOT NULL,
  `uom` VARCHAR(10) NOT NULL,
  `volume` DECIMAL(6,2) NOT NULL,
  `unit_weight` DECIMAL(6,2) NOT NULL
);

CREATE TABLE `ITEM_CATEGORY` (
  `category_id` INT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL
);

CREATE TABLE `INVENTORY_LOT` (
  `lot_id` INT PRIMARY KEY AUTO_INCREMENT,
  `sku` VARCHAR(40) NOT NULL,
  `box_code` VARCHAR(40) NOT NULL,
  `exp_date` DATE,
  `received_date` DATE
);

CREATE TABLE `INVENTORY_BALANCE` (
  `balance_id` INT PRIMARY KEY AUTO_INCREMENT,
  `bay_id` INT NOT NULL,
  `lot_id` INT NOT NULL,
  `qty` INT NOT NULL DEFAULT 0
);

CREATE TABLE `WAREHOUSE` (
  `warehouse_id` INT PRIMARY KEY AUTO_INCREMENT,
  `facility_id` INT UNIQUE NOT NULL,
  `name` VARCHAR(120) NOT NULL
);

CREATE TABLE `WAREHOUSE_AISLE` (
  `aisle_id` INT PRIMARY KEY AUTO_INCREMENT,
  `warehouse_id` INT NOT NULL,
  `aisle_no` INT NOT NULL,
  `unique_code` VARCHAR(60) UNIQUE NOT NULL
);

CREATE TABLE `WAREHOUSE_BAY` (
  `bay_id` INT PRIMARY KEY AUTO_INCREMENT,
  `aisle_id` INT NOT NULL,
  `bay_no` INT NOT NULL,
  `capacity_box` INT NOT NULL
);

CREATE TABLE `ORDER` (
  `order_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `status` VARCHAR(20) NOT NULL,
  `priority` INT NOT NULL DEFAULT 0,
  `due_date` DATE,
  `created_at` TIMESTAMP(6) NOT NULL DEFAULT (CURRENT_TIMESTAMP(6))
);

CREATE TABLE `ORDER_LINE` (
  `order_line_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL,
  `sku` VARCHAR(40) NOT NULL,
  `qty` INT NOT NULL
);

CREATE TABLE `RETURN_ORDER` (
  `return_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `orig_order_id` BIGINT,
  `reason` VARCHAR(255),
  `status` VARCHAR(20) NOT NULL,
  `received_at` TIMESTAMP(6) NOT NULL DEFAULT (CURRENT_TIMESTAMP(6))
);

CREATE TABLE `RETURN_LINE` (
  `return_line_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `return_id` BIGINT NOT NULL,
  `sku` VARCHAR(40) NOT NULL,
  `qty` INT NOT NULL,
  `condition` VARCHAR(50)
);

CREATE TABLE `INVENTORY_MOVE` (
  `move_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `lot_id` INT,
  `from_bay_id` INT,
  `to_bay_id` INT,
  `qty` INT NOT NULL,
  `reason` VARCHAR(40),
  `created_at` TIMESTAMP(6) NOT NULL DEFAULT (CURRENT_TIMESTAMP(6))
);

CREATE TABLE `QUARANTINE_HOLD` (
  `qhold_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `return_id` BIGINT NOT NULL,
  `status` VARCHAR(20) NOT NULL,
  `created_at` TIMESTAMP(6) NOT NULL DEFAULT (CURRENT_TIMESTAMP(6)),
  `released_at` TIMESTAMP(6) DEFAULT null
);

CREATE TABLE `QA_INSPECTION` (
  `qa_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `return_line_id` BIGINT NOT NULL,
  `status` VARCHAR(20) NOT NULL,
  `inspected_at` TIMESTAMP(6) NOT NULL DEFAULT (CURRENT_TIMESTAMP(6))
);

CREATE TABLE `CART` (
  `cart_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `nome` VARCHAR(255) NOT NULL,
  `max_weight` DECIMAL(10,3) NOT NULL
);

CREATE TABLE `PICK_WAVE` (
  `wave_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `cart_id` BIGINT NOT NULL,
  `strategy` VARCHAR(20) NOT NULL,
  `status` VARCHAR(20) NOT NULL,
  `created_at` TIMESTAMP(6) NOT NULL DEFAULT (CURRENT_TIMESTAMP(6))
);

CREATE TABLE `PICK_TASK` (
  `pick_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `wave_id` BIGINT NOT NULL,
  `order_line_id` BIGINT NOT NULL,
  `balance_id` INT NOT NULL,
  `qty` INT NOT NULL,
  `pos_seq` INT NOT NULL
);

CREATE TABLE `TRAIN` (
  `train_id` INT PRIMARY KEY AUTO_INCREMENT,
  `code` varchar(150) NOT NULL,
  `departure` INT NOT NULL,
  `destination` INT NOT NULL,
  `operator_id` INT NOT NULL
);

CREATE TABLE `TRAIN_SERVICE_COMPOSITION` (
  `train_service_id` INT PRIMARY KEY AUTO_INCREMENT,
  `service_locomotive_id` INT NOT NULL,
  `wagon_id` INT NOT NULL
);

CREATE TABLE `SERVICE_LOCOMOTIVE` (
  `service_locomotive_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `service_id` INT NOT NULL,
  `locomotive_id` INT NOT NULL,
  `position_no` INT NOT NULL
);

CREATE TABLE `TRAIN_SERVICE` (
  `service_id` INT PRIMARY KEY AUTO_INCREMENT,
  `train_id` INT NOT NULL,
  `departure_date` timestamp NOT NULL,
  `arrival_date` timestamp NOT NULL,
  `train_driver_id` INT,
  `status` VARCHAR(20) NOT NULL COMMENT 'SCHEDULED, DEPARTED, ARRIVED, CANCELLED'
);

CREATE TABLE `TRAIN_STOP` (
  `train_stop_id` INT PRIMARY KEY AUTO_INCREMENT,
  `service_id` INT NOT NULL,
  `facility_id` INT NOT NULL,
  `parking_track` INT NOT NULL,
  `position` INT NOT NULL,
  `sched_arr` TIMESTAMP(6) NOT NULL,
  `sched_dep` TIMESTAMP(6) NOT NULL,
  `planned_stop_time` INT,
  `act_arr` TIMESTAMP(6),
  `act_dep` TIMESTAMP(6),
  `act_stop_time` INT
);

CREATE UNIQUE INDEX `uq_lsg_line_position` ON `LINE_SEGMENT` (`line_id`, `position`);

CREATE UNIQUE INDEX `uq_seg_fac` ON `LINE_SEGMENT_FACILITY` (`line_segment_id`, `facility_id`);

CREATE UNIQUE INDEX `uq_ptrk_line_name` ON `PARKING_TRACK` (`line_id`, `name`);

CREATE UNIQUE INDEX `uq_pq_track_pos` ON `PARKING_QUEUE` (`parking_track_id`, `position_no`);

CREATE UNIQUE INDEX `uq_pq_track_wagon` ON `PARKING_QUEUE` (`parking_track_id`, `wagon_id`);

CREATE UNIQUE INDEX `uq_lot_box_per_sku` ON `INVENTORY_LOT` (`sku`, `box_code`);

CREATE INDEX `ix_lot_fefo` ON `INVENTORY_LOT` (`sku`, `exp_date`, `received_date`, `box_code`);

CREATE UNIQUE INDEX `uq_balance_bay_lot` ON `INVENTORY_BALANCE` (`bay_id`, `lot_id`);

CREATE INDEX `INVENTORY_BALANCE_index_8` ON `INVENTORY_BALANCE` (`bay_id`);

CREATE INDEX `INVENTORY_BALANCE_index_9` ON `INVENTORY_BALANCE` (`lot_id`);

CREATE UNIQUE INDEX `uq_wh_aisle_no` ON `WAREHOUSE_AISLE` (`warehouse_id`, `aisle_no`);

CREATE UNIQUE INDEX `uq_aisle_bay` ON `WAREHOUSE_BAY` (`aisle_id`, `bay_no`);

CREATE INDEX `ix_wave_seq` ON `PICK_TASK` (`wave_id`, `pos_seq`);

CREATE UNIQUE INDEX `uq_train_composition` ON `TRAIN_SERVICE_COMPOSITION` (`service_locomotive_id`, `wagon_id`);

CREATE UNIQUE INDEX `uq_serv_loco_pos` ON `SERVICE_LOCOMOTIVE` (`service_id`, `position_no`);

CREATE INDEX `ix_train_run` ON `TRAIN_SERVICE` (`train_id`, `departure_date`);

CREATE UNIQUE INDEX `TRAIN_STOP_index_16` ON `TRAIN_STOP` (`service_id`, `position`);

ALTER TABLE `LINE` ADD FOREIGN KEY (`owner_id`) REFERENCES `COMPANY` (`company_id`);

ALTER TABLE `LINE_SEGMENT` ADD FOREIGN KEY (`line_id`) REFERENCES `LINE` (`line_id`);

ALTER TABLE `LINE_SEGMENT` ADD FOREIGN KEY (`track_type_id`) REFERENCES `TRACK_TYPE` (`track_type_id`);

ALTER TABLE `LINE_SEGMENT` ADD FOREIGN KEY (`electrif_type_id`) REFERENCES `ELECTRIFICATION_TYPE` (`electrif_type_id`);

ALTER TABLE `LINE_SEGMENT` ADD FOREIGN KEY (`gauge_id`) REFERENCES `GAUGE` (`gauge_id`);

ALTER TABLE `PARKING_TRACK` ADD FOREIGN KEY (`line_id`) REFERENCES `LINE` (`line_id`);

ALTER TABLE `PARKING_TRACK` ADD FOREIGN KEY (`facility_id`) REFERENCES `FACILITY` (`facility_id`);

ALTER TABLE `PARKING_QUEUE` ADD FOREIGN KEY (`parking_track_id`) REFERENCES `PARKING_TRACK` (`parking_track_id`);

ALTER TABLE `LOCOMOTIVE_GAUGE` ADD FOREIGN KEY (`locomotive_id`) REFERENCES `LOCOMOTIVE` (`locomotive_id`);

ALTER TABLE `LOCOMOTIVE_GAUGE` ADD FOREIGN KEY (`gauge_id`) REFERENCES `GAUGE` (`gauge_id`);

ALTER TABLE `WAGON_GAUGE` ADD FOREIGN KEY (`wagon_id`) REFERENCES `WAGON` (`wagon_id`);

ALTER TABLE `WAGON_GAUGE` ADD FOREIGN KEY (`gauge_id`) REFERENCES `GAUGE` (`gauge_id`);

ALTER TABLE `PARKING_QUEUE` ADD FOREIGN KEY (`wagon_id`) REFERENCES `WAGON` (`wagon_id`);

ALTER TABLE `FACILITY` ADD FOREIGN KEY (`facility_type_id`) REFERENCES `FACILITY_TYPE` (`facility_type_id`);

ALTER TABLE `FACILITY_FEATURE` ADD FOREIGN KEY (`facility_id`) REFERENCES `FACILITY` (`facility_id`);

ALTER TABLE `FACILITY_FEATURE` ADD FOREIGN KEY (`feature_id`) REFERENCES `FACILITY_FEATURE_TYPE` (`feature_id`);

ALTER TABLE `LOCOMOTIVE` ADD FOREIGN KEY (`locomotive_type`) REFERENCES `LOCOMOTIVE_TYPE` (`locomotive_type_id`);

ALTER TABLE `LOCOMOTIVE` ADD FOREIGN KEY (`operator_id`) REFERENCES `USER` (`user_id`);

ALTER TABLE `WAGON` ADD FOREIGN KEY (`operator_id`) REFERENCES `USER` (`user_id`);

ALTER TABLE `WAGON` ADD FOREIGN KEY (`wagon_type`) REFERENCES `WAGON_TYPE` (`wagon_type_id`);

ALTER TABLE `USER_ROLE` ADD FOREIGN KEY (`role_id`) REFERENCES `ROLE` (`role_id`);

ALTER TABLE `USER_ROLE` ADD FOREIGN KEY (`user_id`) REFERENCES `USER` (`user_id`);

ALTER TABLE `USER` ADD FOREIGN KEY (`company_id`) REFERENCES `COMPANY` (`company_id`);

ALTER TABLE `WAREHOUSE` ADD FOREIGN KEY (`facility_id`) REFERENCES `FACILITY` (`facility_id`);

ALTER TABLE `WAREHOUSE_AISLE` ADD FOREIGN KEY (`warehouse_id`) REFERENCES `WAREHOUSE` (`warehouse_id`);

ALTER TABLE `WAREHOUSE_BAY` ADD FOREIGN KEY (`aisle_id`) REFERENCES `WAREHOUSE_AISLE` (`aisle_id`);

ALTER TABLE `INVENTORY_LOT` ADD FOREIGN KEY (`sku`) REFERENCES `ITEM` (`sku`);

ALTER TABLE `INVENTORY_BALANCE` ADD FOREIGN KEY (`bay_id`) REFERENCES `WAREHOUSE_BAY` (`bay_id`);

ALTER TABLE `INVENTORY_BALANCE` ADD FOREIGN KEY (`lot_id`) REFERENCES `INVENTORY_LOT` (`lot_id`);

ALTER TABLE `ORDER_LINE` ADD FOREIGN KEY (`sku`) REFERENCES `ITEM` (`sku`);

ALTER TABLE `RETURN_LINE` ADD FOREIGN KEY (`sku`) REFERENCES `ITEM` (`sku`);

ALTER TABLE `INVENTORY_MOVE` ADD FOREIGN KEY (`lot_id`) REFERENCES `INVENTORY_LOT` (`lot_id`);

ALTER TABLE `INVENTORY_MOVE` ADD FOREIGN KEY (`from_bay_id`) REFERENCES `WAREHOUSE_BAY` (`bay_id`);

ALTER TABLE `INVENTORY_MOVE` ADD FOREIGN KEY (`to_bay_id`) REFERENCES `WAREHOUSE_BAY` (`bay_id`);

ALTER TABLE `ORDER_LINE` ADD FOREIGN KEY (`order_id`) REFERENCES `ORDER` (`order_id`);

ALTER TABLE `RETURN_LINE` ADD FOREIGN KEY (`return_id`) REFERENCES `RETURN_ORDER` (`return_id`);

ALTER TABLE `QA_INSPECTION` ADD FOREIGN KEY (`return_line_id`) REFERENCES `RETURN_LINE` (`return_line_id`);

ALTER TABLE `QUARANTINE_HOLD` ADD FOREIGN KEY (`return_id`) REFERENCES `RETURN_ORDER` (`return_id`);

ALTER TABLE `PICK_WAVE` ADD FOREIGN KEY (`cart_id`) REFERENCES `CART` (`cart_id`);

ALTER TABLE `PICK_TASK` ADD FOREIGN KEY (`wave_id`) REFERENCES `PICK_WAVE` (`wave_id`);

ALTER TABLE `PICK_TASK` ADD FOREIGN KEY (`order_line_id`) REFERENCES `ORDER_LINE` (`order_line_id`);

ALTER TABLE `PICK_TASK` ADD FOREIGN KEY (`balance_id`) REFERENCES `INVENTORY_BALANCE` (`balance_id`);

ALTER TABLE `ITEM` ADD FOREIGN KEY (`item_category_id`) REFERENCES `ITEM_CATEGORY` (`category_id`);

ALTER TABLE `LOCOMOTIVE_DIESEL` ADD FOREIGN KEY (`locomotive_id`) REFERENCES `LOCOMOTIVE` (`locomotive_id`);

ALTER TABLE `LINE_SEGMENT_FACILITY` ADD FOREIGN KEY (`line_segment_id`) REFERENCES `LINE_SEGMENT` (`segment_id`);

ALTER TABLE `LINE_SEGMENT_FACILITY` ADD FOREIGN KEY (`facility_id`) REFERENCES `FACILITY` (`facility_id`);

ALTER TABLE `TRAIN` ADD FOREIGN KEY (`operator_id`) REFERENCES `USER` (`user_id`);

ALTER TABLE `TRAIN_SERVICE` ADD FOREIGN KEY (`train_driver_id`) REFERENCES `USER` (`user_id`);

ALTER TABLE `TRAIN_SERVICE` ADD FOREIGN KEY (`train_id`) REFERENCES `TRAIN` (`train_id`);

ALTER TABLE `SERVICE_LOCOMOTIVE` ADD FOREIGN KEY (`service_id`) REFERENCES `TRAIN_SERVICE` (`service_id`);

ALTER TABLE `TRAIN_SERVICE_COMPOSITION` ADD FOREIGN KEY (`service_locomotive_id`) REFERENCES `SERVICE_LOCOMOTIVE` (`service_locomotive_id`);

ALTER TABLE `TRAIN_STOP` ADD FOREIGN KEY (`service_id`) REFERENCES `TRAIN_SERVICE` (`service_id`);

ALTER TABLE `TRAIN_STOP` ADD FOREIGN KEY (`facility_id`) REFERENCES `FACILITY` (`facility_id`);

ALTER TABLE `TRAIN` ADD FOREIGN KEY (`departure`) REFERENCES `FACILITY` (`facility_id`);

ALTER TABLE `TRAIN` ADD FOREIGN KEY (`destination`) REFERENCES `FACILITY` (`facility_id`);

ALTER TABLE `TRAIN_SERVICE_COMPOSITION` ADD FOREIGN KEY (`wagon_id`) REFERENCES `WAGON` (`wagon_id`);

ALTER TABLE `SERVICE_LOCOMOTIVE` ADD FOREIGN KEY (`locomotive_id`) REFERENCES `LOCOMOTIVE` (`locomotive_id`);

ALTER TABLE `TRAIN_STOP` ADD FOREIGN KEY (`parking_track`) REFERENCES `PARKING_TRACK` (`parking_track_id`);
