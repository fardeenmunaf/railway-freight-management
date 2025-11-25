package app;

import app.controller.InventoryBalanceController;
import app.model.*;
import app.repositories.CartRepository;
import app.repositories.Repositories;
import app.repositories.WarehouseAisleRepository;
import app.utils.CsvReader;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        Facility lisboa = new Facility();
        lisboa.setFacilityId(1);
        lisboa.setName("Lisboa Santa Apolónia");
        lisboa.setFacilityType(stationType);
        repositories.getFacilityRepository().addFacility(lisboa);

        Facility porto = new Facility();
        porto.setFacilityId(2);
        porto.setName("Porto Campanhã");
        porto.setFacilityType(stationType);
        repositories.getFacilityRepository().addFacility(porto);

        Facility coimbra = new Facility();
        coimbra.setFacilityId(3);
        coimbra.setName("Coimbra-B");
        coimbra.setFacilityType(stationType);
        repositories.getFacilityRepository().addFacility(coimbra);

        // 5-6. Criar segmentos
        LineSegment seg1 = new LineSegment();
        seg1.setSegmentId(1);
        seg1.setLengthKm(200.0);
        seg1.setSpeedLimitKmh(120.0);
        repositories.getLineSegmentRepository().addLineSegment(seg1);

        LineSegment seg2 = new LineSegment();
        seg2.setSegmentId(2);
        seg2.setLengthKm(120.0);
        seg2.setSpeedLimitKmh(100.0);
        repositories.getLineSegmentRepository().addLineSegment(seg2);

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

        // 11. Criar locomotivas
        Locomotive loco1 = new Locomotive();
        loco1.setLocomotiveId(1);
        loco1.setModel("Class E10");
        loco1.setBrand("DB");
        loco1.setMaxSpeed(160.0);
        repositories.getLocomotiveRepository().addLocomotive(loco1);

        Locomotive loco2 = new Locomotive();
        loco2.setLocomotiveId(2);
        loco2.setModel("Euro Sprinter");
        loco2.setBrand("Siemens");
        loco2.setMaxSpeed(220.0);
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
            TrainDispatch dispatch = new TrainDispatch();
            dispatch.setTrainService(trainService);
            dispatch.setPlannedDepartureTime(departureTime);
            dispatch.setRouteType("MANUAL");
            dispatch.setStatus(status);

            // Criar e adicionar o dispatch
            dispatch = repositories.getTrainDispatchRepository().addTrainDispatch(dispatch);

            // Criar route points
            List<RoutePoint> routePoints = new ArrayList<>();
            double cumulativeDistance = 0.0;
            double cumulativeTime = 0.0;
            LocalDateTime currentTime = departureTime;

            for (int i = 0; i < facilityIds.size(); i++) {
                Facility facility = repositories.getFacilityRepository().getFacilityById(facilityIds.get(i));
                if (facility == null) continue;

                RoutePoint point = new RoutePoint();
                point.setTrainDispatch(dispatch);
                point.setSequenceOrder(i + 1);
                point.setFacility(facility);
                point.setDistanceFromStartKm(cumulativeDistance);
                point.setCumulativeTravelTimeMinutes(cumulativeTime);

                if (i == 0) {
                    // Primeiro ponto - partida
                    point.setEstimatedArrivalTime(departureTime);
                    point.setEstimatedDepartureTime(departureTime);
                    point.setCrossingOperation("NONE");
                    point.setExpectedWaitTimeMinutes(0);
                } else {
                    // Pontos subsequentes - simular viagem
                    double segmentDistance = 50.0 + (i * 30.0); // Distância variável
                    double segmentSpeed = 80.0; // Velocidade média
                    double segmentTime = (segmentDistance / segmentSpeed) * 60.0; // Minutos

                    cumulativeDistance += segmentDistance;
                    cumulativeTime += segmentTime;
                    currentTime = currentTime.plusMinutes((long) segmentTime);

                    point.setEstimatedArrivalTime(currentTime);
                    point.setDistanceFromStartKm(cumulativeDistance);
                    point.setCumulativeTravelTimeMinutes(cumulativeTime);

                    // Simular operação de cruzamento em alguns pontos
                    if (i == 1 && facilityIds.size() > 2) {
                        point.setCrossingOperation("WAIT_FOR_CROSSING");
                        point.setExpectedWaitTimeMinutes(10);
                        point.setIsSingleLineSegment(true);
                        point.setNotes("Single-line segment - crossing operation required");
                        currentTime = currentTime.plusMinutes(10);
                        cumulativeTime += 10;
                    } else {
                        point.setCrossingOperation("NONE");
                        point.setExpectedWaitTimeMinutes(0);
                        point.setIsSingleLineSegment(false);
                    }

                    // Tempo de paragem na estação (exceto última)
                    int stopTime = (i < facilityIds.size() - 1) ? 5 : 0;
                    currentTime = currentTime.plusMinutes(stopTime);
                    cumulativeTime += stopTime;
                    point.setEstimatedDepartureTime(currentTime);
                }

                routePoints.add(repositories.getRoutePointRepository().addRoutePoint(point));
            }

            // Atualizar dispatch com totais
            if (!routePoints.isEmpty()) {
                RoutePoint lastPoint = routePoints.get(routePoints.size() - 1);
                dispatch.setTotalDistanceKm(lastPoint.getDistanceFromStartKm());
                dispatch.setEstimatedTravelTimeMinutes(lastPoint.getCumulativeTravelTimeMinutes());
                dispatch.setRoute(routePoints);
                repositories.getTrainDispatchRepository().updateTrainDispatch(dispatch.getDispatchId(), dispatch);
            }

            return dispatch;

        } catch (Exception e) {
            System.out.println("⚠️  Error creating dispatch: " + e.getMessage());
            return null;
        }
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
    }
}

