package app;

import app.controller.InventoryBalanceController;
import app.model.*;
import app.repositories.CartRepository;
import app.repositories.Repositories;
import app.repositories.WarehouseAisleRepository;
import app.service.sprint3.TrainSchedulingService;
import app.utils.CsvReader;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * AppInitializer class to initialize the application with sample data
 */
public class AppInitializer implements Runnable {

    private static final String BAYS_CSV = "src/main/java/app/sampleDataset/bays.csv";
    private static final String ITEMS_CSV = "src/main/java/app/sampleDataset/items.csv";
    private static final String ORDERS_CSV = "src/main/java/app/sampleDataset/orders.csv";
    private static final String WAGONS_CSV = "src/main/java/app/sampleDataset/wagons.csv";
    private static final String ORDER_LINES_CSV = "src/main/java/app/sampleDataset/order_lines.csv";
    private static final String RETURNS_CSV = "src/main/java/app/sampleDataset/returns.csv";
    private static final String STATIONS_CSV = "src/main/java/app/sampleDataset/train_stations_europe.csv";

    private int aisleIdCounter = 1;

    @Override
    public void run() {
        Repositories repositories = Repositories.getInstance();
        loadBays(repositories);
        loadItems(repositories);
        loadOrders(repositories);
        loadOrderLines(repositories);
        loadWagons(repositories);
        loadReturns(repositories);
        loadTravelTimeData(repositories);
        loadMockedData();
        loadStations(repositories);
        loadSampleDispatches(repositories); // Carregar dispatches de exemplo
    }

    private void loadBays(Repositories repositories) {
        List<Map<String, String>> data = CsvReader.readCsv(BAYS_CSV, ";");
        int bayIdCounter = 1;
        for (Map<String, String> row : data) {
            String warehouseId = row.get("warehouseId");
            Integer aisleNo = Integer.parseInt(row.get("aisle"));
            Integer bayNo = Integer.parseInt(row.get("bay"));
            Integer capacityBox = Integer.parseInt(row.get("capacityBoxes"));

            Warehouse warehouse = getOrCreateWarehouse(warehouseId, repositories);
            WarehouseAisle aisle = getOrCreateAisle(warehouse, aisleNo, repositories);
            WarehouseBay bay = new WarehouseBay(bayIdCounter, aisle, bayNo, capacityBox);
            bayIdCounter++;

            repositories.getWarehouseBayRepository().addWarehouseBay(bay);
        }
    }

    private Warehouse getOrCreateWarehouse(String warehouseIdStr, Repositories repositories) {
        Integer warehouseId = Integer.parseInt(warehouseIdStr.substring(1));
        Warehouse warehouse = repositories.getWarehouseRepository().getWarehouseById(warehouseId);
        if (warehouse == null) {
            FacilityType facilityType = new FacilityType(1, "Warehouse");
            Facility facility = new Facility(warehouseId, warehouseIdStr, facilityType, null, null, null);
            warehouse = new Warehouse(warehouseId, facility, warehouseIdStr);
            repositories.getWarehouseRepository().addWarehouse(warehouse);
        }
        return warehouse;
    }

    private WarehouseAisle getOrCreateAisle(Warehouse warehouse, Integer aisleNo, Repositories repositories) {
        WarehouseAisleRepository aisleRepo = repositories.getWarehouseAisleRepository();
        WarehouseAisle aisle = aisleRepo.getAisle(warehouse.getWarehouseId(), aisleNo);
        if (aisle == null) {
            String uniqueCode = warehouse.getName() + "-A" + aisleNo;
            aisle = new WarehouseAisle(aisleIdCounter, warehouse, aisleNo, uniqueCode);
            aisleIdCounter++;
            repositories.getWarehouseAisleRepository().addWarehouseAisle(aisle);
        }
        return aisle;
    }

    private void loadItems(Repositories repositories) {
        List<Map<String, String>> data = CsvReader.readCsv(ITEMS_CSV, ",");
        int itemIdCounter = 1;
        for (Map<String, String> row : data) {
            String sku = row.get("sku");
            String name = row.get("name");
            String categoryName = row.get("category");
            String unit = row.get("unit");
            Double volume = Double.parseDouble(row.get("volume"));
            Double unitWeight = Double.parseDouble(row.get("unitWeight"));

            // Get or create item category
            ItemCategory category = getOrCreateCategory(categoryName, repositories);

            // Create and add item
            Item item = new Item(itemIdCounter, category, sku, name, unit, volume, unitWeight);
            repositories.getItemRepository().addItem(item);
            itemIdCounter++;
        }
    }

    private ItemCategory getOrCreateCategory(String categoryName, Repositories repositories) {
        ItemCategory category = repositories.getItemCategoryRepository().getCategoryByName(categoryName);
        if (category == null) {
            Integer categoryId = repositories.getItemCategoryRepository().getNextCategoryId();
            category = new ItemCategory(categoryId, categoryName);
            repositories.getItemCategoryRepository().addItemCategory(category);
        }
        return category;
    }

    private void loadOrders(Repositories repositories) {
        List<Map<String, String>> data = CsvReader.readCsv(ORDERS_CSV, ",");
        for (Map<String, String> row : data) {
            String orderIdStr = row.get("orderId");
            String dueDateStr = row.get("dueDate");
            Integer priority = Integer.parseInt(row.get("priority"));

            // Extract numeric part from orderId (e.g., "ORD00001" -> 1)
            Integer orderId = Integer.parseInt(orderIdStr.substring(3));

            // Parse dueDate from ISO format
            LocalDateTime dueDate = LocalDateTime.parse(dueDateStr);

            // Create order with default status "pending"
            Order order = new Order(orderId, "pending", priority, dueDate);
            repositories.getOrderRepository().addOrder(order);
        }
    }

    private void loadOrderLines(Repositories repositories) {
        List<Map<String, String>> data = CsvReader.readCsv(ORDER_LINES_CSV, ",");
        int orderLineIdCounter = 1;

        for (Map<String, String> row : data) {
            String orderIdStr = row.get("orderId");
            String skuString = row.get("sku");
            Double qty = Double.parseDouble(row.get("qty"));

            // Extract numeric part from orderId (e.g., "ORD00001" -> 1)
            Integer orderId = Integer.parseInt(orderIdStr.substring(3));

            // Get the order from repository
            Order order = repositories.getOrderRepository().getOrderById(orderId);

            // Get the item by SKU
            Item item = repositories.getItemRepository().getItemBySku(skuString);

            if (order != null && item != null) {
                // Create OrderLine
                OrderLine orderLine = new OrderLine(orderLineIdCounter, order, item, qty);
                repositories.getOrderLineRepository().addOrderLine(orderLine);
                orderLineIdCounter++;
            }
        }
    }

    private void loadWagons(Repositories repositories) {
        List<Map<String, String>> data = CsvReader.readCsv(WAGONS_CSV, ",");
        InventoryBalanceController balanceController = new InventoryBalanceController();
        int balanceIdCounter = -1;

        for (Map<String, String> row : data) {
            String boxId = row.get("boxId");
            String skuString = row.get("sku");
            Integer qty = Integer.parseInt(row.get("qty"));
            String expiryDateStr = row.get("expiryDate");
            String receivedAtStr = row.get("receivedAt");

            // Extract numeric part from boxId (e.g., "BOX00001" -> 1)
            Integer lotId = Integer.parseInt(boxId.substring(3));

            // Parse sku as Integer (e.g., "SKU00001" -> 1)
            Integer sku = Integer.parseInt(skuString.substring(3));

            // Parse expiryDate (optional - can be null/empty)
            LocalDateTime expiryDate = null;
            if (expiryDateStr != null && !expiryDateStr.trim().isEmpty()) {
                // Handle both date-only format (2026-01-05) and datetime format
                if (expiryDateStr.length() == 10) {
                    // Date only format - add default time (start of day)
                    expiryDate = LocalDateTime.parse(expiryDateStr + "T00:00:00");
                } else {
                    // Full datetime format
                    expiryDate = LocalDateTime.parse(expiryDateStr);
                }
            }

            // Parse receivedAt as LocalDateTime (ISO format: 2025-07-05T15:42:00)
            LocalDateTime receivedAt = LocalDateTime.parse(receivedAtStr);

            // Create InventoryLot (box) - lotId is the numeric part of boxId
            InventoryLot lot = new InventoryLot(lotId, sku, qty, expiryDate, receivedAt);
            repositories.getInventoryLotRepository().addInventoryLot(lot);

            InventoryBalance existingBalance = findBalanceBySku(balanceController, sku);

            if (existingBalance != null) {
                existingBalance.addLot(lot);
                balanceController.updateInventoryBalance(existingBalance.getBalanceId(), existingBalance);
            } else {
                List<InventoryLot> lotsList = new ArrayList<>();
                lotsList.add(lot);
                InventoryBalance balance = new InventoryBalance(balanceIdCounter, null, lotsList, qty);
                balanceController.createInventoryBalance(balance);
                balanceIdCounter--;
            }
        }
    }

    /**
     * Helper method to find an existing InventoryBalance by SKU.
     *
     * @param balanceController the inventory balance controller
     * @param sku               the SKU to search for
     * @return the existing InventoryBalance, or null if not found
     */
    private InventoryBalance findBalanceBySku(InventoryBalanceController balanceController, Integer sku) {
        List<InventoryBalance> allBalances = balanceController.getAllInventoryBalances();
        for (InventoryBalance balance : allBalances) {
            if (!balance.getLots().isEmpty() && sku.equals(balance.getLots().get(0).getSku())) {
                return balance;
            }
        }
        return null;
    }

    private void loadReturns(Repositories repositories) {
        List<Map<String, String>> data = CsvReader.readCsv(RETURNS_CSV, ",");
        int returnLineIdCounter = 1;

        for (Map<String, String> row : data) {
            String returnIdStr = row.get("returnId");
            String skuString = row.get("sku");
            Integer qty = Integer.parseInt(row.get("qty"));
            String reason = row.get("reason");
            String timestampStr = row.get("timestamp");
            String expiryDateStr = row.get("expiryDate");

            // Extract numeric part from returnId (e.g., "RET00001" -> 1)
            Integer returnId = Integer.parseInt(returnIdStr.substring(3));

            // Parse timestamp as LocalDateTime (ISO format: 2025-09-13T12:00:00)
            LocalDateTime timestamp = LocalDateTime.parse(timestampStr);

            // Parse expiryDate (optional - can be null/empty)
            LocalDateTime expiryDate = null;
            if (expiryDateStr != null && !expiryDateStr.trim().isEmpty()) {
                // Handle both date-only format (2025-07-30) and datetime format
                if (expiryDateStr.length() == 10) {
                    // Date only format - add default time (start of day)
                    expiryDate = LocalDateTime.parse(expiryDateStr + "T00:00:00");
                } else {
                    // Full datetime format
                    expiryDate = LocalDateTime.parse(expiryDateStr);
                }
            }

            // Create ReturnOrder with status "received"
            ReturnOrder returnOrder = new ReturnOrder(returnId, null, reason, "received", timestamp);
            repositories.getReturnOrderRepository().addReturnOrder(returnOrder);

            // Create ReturnLine with condition based on reason
            String condition = getConditionFromReason(reason);
            ReturnLine returnLine = new ReturnLine(returnLineIdCounter, returnOrder, skuString, qty, condition);
            repositories.getReturnLineRepository().addReturnLine(returnLine);
            returnLineIdCounter++;
        }
    }

    private String getConditionFromReason(String reason) {
        // Map reason to condition
        switch (reason) {
            case "damaged":
                return "damaged";
            case "expired":
                return "expired";
            case "customer-remorse":
                return "good";
            case "cycle-count":
                return "good";
            default:
                return "unknown";
        }
    }

    private void loadMockedData() {
        CartRepository cartRepository = Repositories.getInstance().getCartRepository();
        cartRepository.initializeDefaultCarts();
    }

    private void loadTravelTimeData(Repositories repositories) {
        // 1. Criar tipo "Station"
        FacilityType stationType = new FacilityType();
        stationType.setFacilityTypeId(2);
        stationType.setName("Station");
        repositories.getFacilityTypeRepository().addFacilityType(stationType);

        // 2-4. Criar estações
        // Try to reuse facilities if already loaded (e.g., from CSV); otherwise create defaults.
        // Use a flexible finder to match names that may differ in accents or small variants.
        Facility lisboa = findFacilityByNameFlexible(repositories, "Lisboa Santa Apolónia");
        if (lisboa == null) {
            lisboa = new Facility();
            lisboa.setFacilityId(repositories.getFacilityRepository().getNextFacilityId());
            lisboa.setName("Lisboa Santa Apolónia");
            lisboa.setFacilityType(stationType);
            lisboa.setLat(38.7223);
            lisboa.setLon(-9.1393);
            lisboa.setIsIntermodal(false);
            repositories.getFacilityRepository().addFacility(lisboa);
        }

        Facility porto = findFacilityByNameFlexible(repositories, "Porto Campanhã");
        if (porto == null) {
            porto = new Facility();
            porto.setFacilityId(repositories.getFacilityRepository().getNextFacilityId());
            porto.setName("Porto Campanhã");
            porto.setFacilityType(stationType);
            porto.setLat(41.1496);
            porto.setLon(-8.5850);
            porto.setIsIntermodal(false);
            repositories.getFacilityRepository().addFacility(porto);
        }

        Facility coimbra = findFacilityByNameFlexible(repositories, "Coimbra-B");
        if (coimbra == null) {
            coimbra = new Facility();
            coimbra.setFacilityId(repositories.getFacilityRepository().getNextFacilityId());
            coimbra.setName("Coimbra-B");
            coimbra.setFacilityType(stationType);
            coimbra.setLat(40.2085);
            coimbra.setLon(-8.4295);
            coimbra.setIsIntermodal(false);
            repositories.getFacilityRepository().addFacility(coimbra);
        }

        // 5-6. Criar segmentos
        LineSegment seg1 = new LineSegment();
        seg1.setSegmentId(1);
        seg1.setLengthKm(200.0);
        seg1.setSpeedLimitKmh(120.0);
        seg1.setTrackNumber(2); // double track
        repositories.getLineSegmentRepository().addLineSegment(seg1);

        LineSegment seg2 = new LineSegment();
        seg2.setSegmentId(2);
        seg2.setLengthKm(120.0);
        seg2.setSpeedLimitKmh(100.0);
        seg2.setTrackNumber(1); // single track (for crossing tests)
        repositories.getLineSegmentRepository().addLineSegment(seg2);

        // Additional direct segment Lisbon<->Coimbra to provide alternative route
        LineSegment seg3 = new LineSegment();
        seg3.setSegmentId(3);
        seg3.setLengthKm(230.0);
        seg3.setSpeedLimitKmh(110.0);
        seg3.setTrackNumber(1); // single track
        repositories.getLineSegmentRepository().addLineSegment(seg3);

        // 7-10. Criar ligações estação-segmento
        LineSegmentFacility lsf1 = new LineSegmentFacility();
        lsf1.setSegmentFacilityId(1);
        lsf1.setLineSegment(seg1);
        lsf1.setFacility(lisboa);
        repositories.getLineSegmentFacilityRepository().addLineSegmentFacility(lsf1);

        LineSegmentFacility lsf2 = new LineSegmentFacility();
        lsf2.setSegmentFacilityId(2);
        lsf2.setLineSegment(seg1);
        lsf2.setFacility(porto);
        repositories.getLineSegmentFacilityRepository().addLineSegmentFacility(lsf2);

        LineSegmentFacility lsf3 = new LineSegmentFacility();
        lsf3.setSegmentFacilityId(3);
        lsf3.setLineSegment(seg2);
        lsf3.setFacility(porto);
        repositories.getLineSegmentFacilityRepository().addLineSegmentFacility(lsf3);

        LineSegmentFacility lsf4 = new LineSegmentFacility();
        lsf4.setSegmentFacilityId(4);
        lsf4.setLineSegment(seg2);
        lsf4.setFacility(coimbra);
        repositories.getLineSegmentFacilityRepository().addLineSegmentFacility(lsf4);

        // link seg3 (Lisboa <-> Coimbra direct)
        LineSegmentFacility lsf5 = new LineSegmentFacility();
        lsf5.setSegmentFacilityId(5);
        lsf5.setLineSegment(seg3);
        lsf5.setFacility(lisboa);
        repositories.getLineSegmentFacilityRepository().addLineSegmentFacility(lsf5);

        LineSegmentFacility lsf6 = new LineSegmentFacility();
        lsf6.setSegmentFacilityId(6);
        lsf6.setLineSegment(seg3);
        lsf6.setFacility(coimbra);
        repositories.getLineSegmentFacilityRepository().addLineSegmentFacility(lsf6);

        // Create a sample freight route (Lisboa -> Porto -> Coimbra)
        FreightRoute fr = new FreightRoute();
        fr.setRouteId(1);
        fr.setRouteName("Lisboa-Porto-Coimbra");
        fr.setRouteType(FreightRoute.RouteType.COMPLEX);
        fr.setStartFacility(lisboa);
        fr.setEndFacility(coimbra);

        FreightRouteSegment frs1 = new FreightRouteSegment();
        frs1.setSegmentId(1);
        frs1.setFreightRoute(fr);
        frs1.setSequenceOrder(1);
        frs1.setFromFacility(lisboa);
        frs1.setToFacility(porto);
        frs1.setLineSegment(seg1);

        FreightRouteSegment frs2 = new FreightRouteSegment();
        frs2.setSegmentId(2);
        frs2.setFreightRoute(fr);
        frs2.setSequenceOrder(2);
        frs2.setFromFacility(porto);
        frs2.setToFacility(coimbra);
        frs2.setLineSegment(seg2);

        fr.addRouteSegment(frs1);
        fr.addRouteSegment(frs2);
        // Only add route if not already present by name
        if (repositories.getFreightRouteRepository().getFreightRouteByName(fr.getRouteName()) == null) {
            repositories.getFreightRouteRepository().addFreightRoute(fr);
        }

        // Create an alternative direct freight route (Lisboa -> Coimbra)
        FreightRoute fr2 = new FreightRoute();
        fr2.setRouteId(2);
        fr2.setRouteName("Lisboa-Coimbra-Direct");
        fr2.setRouteType(FreightRoute.RouteType.SIMPLE);
        fr2.setStartFacility(lisboa);
        fr2.setEndFacility(coimbra);

        FreightRouteSegment frs3 = new FreightRouteSegment();
        frs3.setSegmentId(3);
        frs3.setFreightRoute(fr2);
        frs3.setSequenceOrder(1);
        frs3.setFromFacility(lisboa);
        frs3.setToFacility(coimbra);
        frs3.setLineSegment(seg3);

        fr2.addRouteSegment(frs3);
        if (repositories.getFreightRouteRepository().getFreightRouteByName(fr2.getRouteName()) == null) {
            repositories.getFreightRouteRepository().addFreightRoute(fr2);
        }

        // 11. Criar locomotivas
        Locomotive loco1 = new Locomotive();
        loco1.setLocomotiveId(1);
        loco1.setModel("Class E10");
        loco1.setBrand("DB");
        loco1.setMaxSpeed(160.0);
        loco1.setPower(3000); // kW
        loco1.setTotalWeight(90000.0); // kg
        repositories.getLocomotiveRepository().addLocomotive(loco1);

        Locomotive loco2 = new Locomotive();
        loco2.setLocomotiveId(2);
        loco2.setModel("Euro Sprinter");
        loco2.setBrand("Siemens");
        loco2.setMaxSpeed(220.0);
        loco2.setPower(4000); // kW
        loco2.setTotalWeight(95000.0); // kg
        repositories.getLocomotiveRepository().addLocomotive(loco2);
    }

    private void loadStations(Repositories repositories) {
        List<Map<String, String>> data = CsvReader.readCsv(STATIONS_CSV, ",");
        int stationIdCounter = 100;
        int rejectedCount = 0;

        FacilityType stationType = repositories.getFacilityTypeRepository()
                .getFacilityTypeByName("Station");
        if (stationType == null) {
            stationType = new FacilityType(2, "Station");
            repositories.getFacilityTypeRepository().addFacilityType(stationType);
        }

        for (Map<String, String> row : data) {
            String country = row.get("country");
            String timeZone = sanitizeTimeZone(row.get("time_zone"));
            String timeZoneGroup = row.get("time_zone_group");
            String name = row.get("station");
            Double latitude = parseDouble(row.get("latitude"));
            Double longitude = parseDouble(row.get("longitude"));
            Boolean isCity = parseBoolean(row.get("is_city"));
            Boolean isMainStation = parseBoolean(row.get("is_main_station"));
            Boolean isAirport = parseBoolean(row.get("is_airport"));

            if (!isValidStation(name, country, timeZoneGroup, latitude, longitude)) {
                System.err.printf("REJECTED: Station='%s', Country='%s', TZ='%s', Lat=%s, Lon=%s%n",
                        name, country, timeZoneGroup, latitude, longitude);
                rejectedCount++;
                continue;
            }

            Station station = new Station(
                    stationIdCounter,
                    name,
                    stationType,
                    latitude,
                    longitude,
                    false,
                    country,
                    timeZone,
                    timeZoneGroup,
                    isCity,
                    isMainStation,
                    isAirport
            );

            repositories.getFacilityRepository().addFacility(station);
            stationIdCounter++;
        }

        System.out.printf("✅ Stations loaded: %d | ❌ Rejected: %d%n",
                stationIdCounter - 100, rejectedCount);
    }

    /**
     * Find a facility by name with flexible matching: exact, normalized (no accents), or contains.
     */
    private Facility findFacilityByNameFlexible(Repositories repositories, String name) {
        if (name == null) return null;
        var fr = repositories.getFacilityRepository();
        // 1) exact
        Facility f = fr.getFacilityByName(name);
        if (f != null) return f;

        // 2) case-insensitive exact
        for (Facility fac : fr.getAllFacilities()) {
            if (fac.getName() != null && fac.getName().equalsIgnoreCase(name)) return fac;
        }

        // 3) normalized (remove diacritics) equals
        String nName = normalize(name);
        for (Facility fac : fr.getAllFacilities()) {
            if (fac.getName() != null && normalize(fac.getName()).equals(nName)) return fac;
        }

        // 4) contains normalized
        for (Facility fac : fr.getAllFacilities()) {
            if (fac.getName() != null && normalize(fac.getName()).contains(nName)) return fac;
        }

        // 5) contains case-insensitive
        for (Facility fac : fr.getAllFacilities()) {
            if (fac.getName() != null && fac.getName().toLowerCase().contains(name.toLowerCase())) return fac;
        }

        return null;
    }

    private String normalize(String s) {
        if (s == null) return "";
        String temp = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD);
        return temp.replaceAll("\\p{M}", "").toLowerCase();
    }

    private boolean isValidStation(String name, String country, String timeZoneGroup,
                                   Double lat, Double lon) {
        if (name == null || name.trim().isEmpty()) return false;
        if (country == null || country.trim().isEmpty()) return false;
        if (timeZoneGroup == null || timeZoneGroup.trim().isEmpty()) return false;
        if (lat == null || lat < -90.0 || lat > 90.0) return false;
        if (lon == null || lon < -180.0 || lon > 180.0) return false;
        return true;
    }

    private static Double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Boolean parseBoolean(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        String v = value.trim();
        return "True".equals(v) || "true".equalsIgnoreCase(v) || "1".equals(v);
    }

    private String sanitizeTimeZone(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }

        String cleaned = raw.replaceAll("[()'\",]", "").trim();
        String[] parts = cleaned.split("\\s+");
        return parts[0];
    }

    /**
     * Loads sample train dispatches for testing the Train Dispatch Scheduler.
     * Creates example dispatches with complete routes, crossing operations, and timing information.
     */
    private void loadSampleDispatches(Repositories repositories) {
        System.out.println("\n🚂 Loading sample train dispatches...");

        try {
            // Verificar se já existem facilities e train services
            List<Facility> facilities = repositories.getFacilityRepository().getAllFacilities();
            List<TrainService> trainServices = repositories.getTrainServiceRepository().getAllTrainServices();

            if (facilities.isEmpty()) {
                System.out.println("⚠️  No facilities found. Creating sample facilities first...");
                createSampleFacilities(repositories);
                facilities = repositories.getFacilityRepository().getAllFacilities();
            }

            if (trainServices.isEmpty()) {
                System.out.println("⚠️  No train services found. Creating sample train services first...");
                createSampleTrainServices(repositories);
                trainServices = repositories.getTrainServiceRepository().getAllTrainServices();
            }

            // Criar dispatches de exemplo
            int dispatchCount = 0;
            LocalDateTime baseTime = LocalDateTime.now().plusHours(2);

            // Dispatch 1: Lisboa -> Porto -> Coimbra (PLANNED)
            if (facilities.size() >= 3 && !trainServices.isEmpty()) {
                TrainDispatch dispatch1 = createSampleDispatch(
                    repositories,
                    trainServices.get(0),
                    baseTime,
                    List.of(facilities.get(0).getFacilityId(), facilities.get(1).getFacilityId(), facilities.get(2).getFacilityId()),
                    "PLANNED"
                );
                if (dispatch1 != null) dispatchCount++;
            }

            // Dispatch 2: Porto -> Braga (DISPATCHED)
            if (facilities.size() >= 4 && trainServices.size() >= 2) {
                TrainDispatch dispatch2 = createSampleDispatch(
                    repositories,
                    trainServices.size() > 1 ? trainServices.get(1) : trainServices.get(0),
                    baseTime.plusHours(1),
                    List.of(facilities.get(1).getFacilityId(), facilities.get(3).getFacilityId()),
                    "DISPATCHED"
                );
                if (dispatch2 != null) dispatchCount++;
            }

            // Dispatch 3: Coimbra -> Lisboa -> Porto (IN_TRANSIT)
            if (facilities.size() >= 3 && trainServices.size() >= 3) {
                TrainDispatch dispatch3 = createSampleDispatch(
                    repositories,
                    trainServices.size() > 2 ? trainServices.get(2) : trainServices.get(0),
                    baseTime.minusHours(1),
                    List.of(facilities.get(2).getFacilityId(), facilities.get(0).getFacilityId(), facilities.get(1).getFacilityId()),
                    "IN_TRANSIT"
                );
                if (dispatch3 != null) dispatchCount++;
            }

            System.out.println("✅ Sample dispatches loaded: " + dispatchCount);

        } catch (Exception e) {
            System.out.println("⚠️  Error loading sample dispatches: " + e.getMessage());
        }
    }

    private TrainDispatch createSampleDispatch(Repositories repositories, TrainService trainService,
                                              LocalDateTime departureTime, List<Integer> facilityIds,
                                              String status) {
        try {
            // Build manual RoutePoints (without persistence yet)
            List<RoutePoint> manual = new ArrayList<>();
            for (int i = 0; i < facilityIds.size(); i++) {
                Facility facility = repositories.getFacilityRepository().getFacilityById(facilityIds.get(i));
                if (facility == null) continue;
                RoutePoint rp = new RoutePoint();
                rp.setFacility(facility);
                rp.setSequenceOrder(i + 1);
                manual.add(rp);
            }

            // Try find a FreightRoute matching the facility sequence so normalizeRoutePoints can set segmentToNext
            FreightRoute matchedRoute = findMatchingFreightRoute(repositories, facilityIds);

            // Prepare existing dispatches for crossing analysis
            List<TrainDispatch> existing = repositories.getTrainDispatchRepository().getAllTrainDispatches();

            // Use TrainSchedulingService to compute schedule using real segments if possible
            TrainSchedulingService scheduler = new TrainSchedulingService();
            int dispatchId = new java.util.Random().nextInt(10000) + 1;
            TrainDispatch scheduled = scheduler.scheduleDispatch(dispatchId, trainService, matchedRoute, departureTime, "MANUAL", manual, existing);

            // Persist dispatch and its route points
            TrainDispatch saved = repositories.getTrainDispatchRepository().addTrainDispatch(scheduled);
            List<RoutePoint> persisted = new ArrayList<>();
            if (saved.getRoute() != null) {
                for (RoutePoint rp : saved.getRoute()) {
                    rp.setTrainDispatch(saved);
                    RoutePoint pr = repositories.getRoutePointRepository().addRoutePoint(rp);
                    persisted.add(pr);
                }
            }
            saved.setRoute(persisted);
            repositories.getTrainDispatchRepository().updateTrainDispatch(saved.getDispatchId(), saved);

            return saved;

        } catch (Exception e) {
            System.out.println("⚠️  Error creating dispatch: " + e.getMessage());
            return null;
        }
    }

    /**
     * Try to find a FreightRoute whose facility sequence contains the given facilityIds as a contiguous subsequence.
     */
    private FreightRoute findMatchingFreightRoute(Repositories repositories, List<Integer> facilityIds) {
        if (facilityIds == null || facilityIds.size() < 2) return null;
        List<FreightRoute> all = repositories.getFreightRouteRepository().getAllFreightRoutes();
        for (FreightRoute fr : all) {
            List<FreightRouteSegment> segs = new ArrayList<>(fr.getRouteSegments());
            segs.sort(Comparator.comparing(FreightRouteSegment::getSequenceOrder));
            // build sequence of facility ids from route segments
            List<Integer> seq = new ArrayList<>();
            if (!segs.isEmpty()) {
                seq.add(segs.get(0).getFromFacility().getFacilityId());
                for (FreightRouteSegment s : segs) {
                    seq.add(s.getToFacility().getFacilityId());
                }
            }
            // check if facilityIds is contiguous subsequence of seq
            for (int i = 0; i + facilityIds.size() <= seq.size(); i++) {
                boolean ok = true;
                for (int j = 0; j < facilityIds.size(); j++) {
                    if (!seq.get(i + j).equals(facilityIds.get(j))) { ok = false; break; }
                }
                if (ok) return fr;
            }
        }
        return null;
    }

    private void createSampleFacilities(Repositories repositories) {
        FacilityType stationType = new FacilityType(1, "Station");

        Facility f1 = new Facility(1, "Lisboa Santa Apolónia", stationType, 38.7223, -9.1393, false);
        Facility f2 = new Facility(2, "Porto Campanhã", stationType, 41.1496, -8.5850, false);
        Facility f3 = new Facility(3, "Coimbra-B", stationType, 40.2085, -8.4295, false);
        Facility f4 = new Facility(4, "Braga", stationType, 41.5454, -8.4265, false);

        repositories.getFacilityRepository().addFacility(f1);
        repositories.getFacilityRepository().addFacility(f2);
        repositories.getFacilityRepository().addFacility(f3);
        repositories.getFacilityRepository().addFacility(f4);

        System.out.println("✅ Sample facilities created: 4");
    }

    private void createSampleTrainServices(Repositories repositories) {
        // Criar company
        Company operator = new Company(1, "CP - Comboios de Portugal", "CP", "PT123456789");
        repositories.getCompanyRepository().addCompany(operator);

        // Criar trains
        Train train1 = new Train(1, 101, "Lisboa Santa Apolónia", "Porto Campanhã", operator);
        Train train2 = new Train(2, 202, "Porto Campanhã", "Braga", operator);
        Train train3 = new Train(3, 303, "Coimbra-B", "Lisboa Santa Apolónia", operator);

        repositories.getTrainRepository().addTrain(train1);
        repositories.getTrainRepository().addTrain(train2);
        repositories.getTrainRepository().addTrain(train3);

        // Criar train services
        LocalDateTime now = LocalDateTime.now();

        TrainService service1 = new TrainService(1, train1, now, now.plusHours(3), null, "scheduled");
        TrainService service2 = new TrainService(2, train2, now.plusHours(1), now.plusHours(2), null, "scheduled");
        TrainService service3 = new TrainService(3, train3, now.minusHours(1), now.plusHours(2), null, "in_transit");

        repositories.getTrainServiceRepository().addTrainService(service1);
        repositories.getTrainServiceRepository().addTrainService(service2);
        repositories.getTrainServiceRepository().addTrainService(service3);

        System.out.println("✅ Sample train services created: 3");

        // Associate locomotives to services so scheduler can compute power
        ServiceLocomotive sl1 = new ServiceLocomotive();
        sl1.setServiceLocomotiveId(1);
        sl1.setService(service1);
        sl1.setLocomotive(Repositories.getInstance().getLocomotiveRepository().getLocomotiveById(1));
        sl1.setPositionNo(1);
        Repositories.getInstance().getServiceLocomotiveRepository().addServiceLocomotive(sl1);

        TrainServiceComposition comp1 = new TrainServiceComposition();
        comp1.setTrainService(service1);
        comp1.setServiceLocomotive(sl1);
        Repositories.getInstance().getTrainServiceCompositionRepository().addTrainServiceComposition(comp1);

        ServiceLocomotive sl2 = new ServiceLocomotive();
        sl2.setServiceLocomotiveId(2);
        sl2.setService(service2);
        sl2.setLocomotive(Repositories.getInstance().getLocomotiveRepository().getLocomotiveById(2));
        sl2.setPositionNo(1);
        Repositories.getInstance().getServiceLocomotiveRepository().addServiceLocomotive(sl2);

        TrainServiceComposition comp2 = new TrainServiceComposition();
        comp2.setTrainService(service2);
        comp2.setServiceLocomotive(sl2);
        Repositories.getInstance().getTrainServiceCompositionRepository().addTrainServiceComposition(comp2);
    }
}

