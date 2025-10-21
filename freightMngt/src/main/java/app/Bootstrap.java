package app;

import app.model.*;
import app.repositories.Repositories;
import app.repositories.WarehouseAisleRepository;
import app.utils.CsvReader;

import java.util.List;
import java.util.Map;

/**
 * Bootstrap class to initialize the application with sample data
 */
public class Bootstrap implements Runnable {

    private static final String BAYS_CSV = "src/main/java/app/sampleDataset/bays.csv";
    private static final String ITEMS_CSV = "src/main/java/app/sampleDataset/items.csv";
    private static final String ORDERS_CSV = "src/main/java/app/sampleDataset/orders.csv";
    private static final String ORDER_LINES_CSV = "src/main/java/app/sampleDataset/order_lines.csv";
    private static final String RETURNS_CSV = "src/main/java/app/sampleDataset/returns.csv";
    private static final String WAGONS_CSV = "src/main/java/app/sampleDataset/wagons.csv";

    private int aisleIdCounter = 1;

    @Override
    public void run() {
        Repositories repositories = Repositories.getInstance();
        loadBays(repositories);
        loadItems(repositories);
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


}