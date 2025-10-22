package app;

import app.repositories.*;
import app.service.TravelTimeFeature;
import app.view.MainMenu;

public class Main {
    public static void main(String[] args) {
        AppInitializer appInitializer = new AppInitializer();
        Repositories repositories = Repositories.getInstance();
        ItemCategoryRepository itemCategoryRepository = repositories.getItemCategoryRepository();
        ItemRepository itemRepository = repositories.getItemRepository();
        OrderRepository orderRepository = repositories.getOrderRepository();
        WarehouseRepository warehouseRepository = repositories.getWarehouseRepository();
        WarehouseAisleRepository warehouseAisleRepository = repositories.getWarehouseAisleRepository();
        WarehouseBayRepository warehouseBayRepository = repositories.getWarehouseBayRepository();
        OrderLineRepository orderLineRepository = repositories.getOrderLineRepository();
        ReturnOrderRepository returnOrderRepository = repositories.getReturnOrderRepository();

        TravelTimeFeature.TravelTimeCalculator travelTime = TravelTimeFeature.create(
                repositories.getFacilityRepository(),
                repositories.getLineSegmentRepository(),
                repositories.getLineSegmentFacilityRepository(),
                repositories.getLocomotiveRepository()
        );

        System.out.println("Initializing dataset...");
        appInitializer.run();

        System.out.println(itemCategoryRepository.getAllCategories().stream().limit(3).toList());
        System.out.println(itemRepository.getAllItems().stream().limit(3).toList());
        System.out.println(orderRepository.getAllOrders().stream().limit(3).toList());
        System.out.println(warehouseRepository.getAllWarehouses().stream().limit(3).toList());
        System.out.println(warehouseAisleRepository.getAllAisles().stream().limit(3).toList());
        System.out.println(warehouseBayRepository.getAllWarehouseBays().stream().limit(3).toList());
        System.out.println(orderLineRepository.getAllOrderLines().stream().limit(3).toList());
        System.out.println(returnOrderRepository.getAllReturnOrders().stream().limit(3).toList());
        System.out.println("Dataset initialized successfully.\n\n");
        MainMenu.display();
    }
}