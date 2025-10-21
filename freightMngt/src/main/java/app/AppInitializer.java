package app;

import app.model.*;
import app.repositories.Repositories;
import app.repositories.WarehouseAisleRepository;
import app.utils.CsvReader;

import java.time.LocalDateTime;
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
        int balanceIdCounter = 1;

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

            // Create InventoryBalance with bay = null (not yet assigned to any bay)
            InventoryBalance balance = new InventoryBalance(balanceIdCounter, null, lot, qty);
            repositories.getInventoryBalanceRepository().addInventoryBalance(balance);
            balanceIdCounter++;
        }
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
        // This method is used to create all repositories with mocked data for testing purposes.

    }
}