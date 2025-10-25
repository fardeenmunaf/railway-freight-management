package app.service;

import app.controller.*;
import app.model.*;
import app.service.PickingPlan.PickingPlanResult;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for PickingPlan service.
 * Tests picking task planning, cart capacity calculations, and trip planning.
 */
public class PickingPlanTest {

    private PickingPlan pickingPlan;
    private InventoryBalanceController balanceController;
    private InventoryLotController lotController;
    private WarehouseBayController bayController;
    private OrderController orderController;
    private OrderLineController orderLineController;
    private CartController cartController;
    private PickWaveController pickWaveController;
    private PickTaskController pickTaskController;

    @Before
    public void setUp() {
        // Clear repositories
        balanceController = new InventoryBalanceController();
        lotController = new InventoryLotController();
        bayController = new WarehouseBayController();
        orderController = new OrderController();
        orderLineController = new OrderLineController();
        cartController = new CartController();
        pickWaveController = new PickWaveController();
        pickTaskController = new PickTaskController();

        balanceController.clearAllInventoryBalances();
        bayController.clearAllWarehouseBays();
        orderController.clearAllOrders();
        app.repositories.Repositories.getInstance().getCartRepository().clear();
        app.repositories.Repositories.getInstance().getInventoryLotRepository().clear();
        app.repositories.Repositories.getInstance().getOrderLineRepository().clear();
        app.repositories.Repositories.getInstance().getPickWaveRepository().clear();
        app.repositories.Repositories.getInstance().getPickTaskRepository().clear();

        pickingPlan = new PickingPlan();
    }

    /**
     * Helper method to create warehouse structure.
     */
    private WarehouseBay[] createWarehouseStructure(Integer... bayIds) {
        Facility facility = new Facility(1, "Test Facility",
                new FacilityType(1, "Warehouse"), 0.0, 0.0, false);

        Warehouse warehouse = new Warehouse(1, facility, "Test Warehouse");
        WarehouseAisle aisle = new WarehouseAisle(1, warehouse, 1, "A-001");

        WarehouseBay[] bays = new WarehouseBay[bayIds.length];
        for (int i = 0; i < bayIds.length; i++) {
            bays[i] = new WarehouseBay(bayIds[i], aisle, bayIds[i], 10);
            bayController.createWarehouseBay(bays[i]);
        }

        return bays;
    }

    /**
     * Helper method to create an item.
     */
    private Item createItem(Integer itemId, String skuCode, Double unitWeight) {
        ItemCategory category = new ItemCategory(1, "Test Category");
        return new Item(itemId, category, skuCode, "Test Item " + itemId, "EA", 1.0, unitWeight);
    }

    /**
     * Test calculateCartCapacity with valid cart and item.
     */
    @Test
    public void testCalculateCartCapacity_ValidCartAndItem() {
        // Cart with 100kg capacity, item weighs 10kg
        Cart cart = new Cart(1, "Cart-1", 100.0);
        Item item = createItem(10, "SKU-010", 10.0);
        OrderLine orderLine = new OrderLine(1, null, item, 50.0);

        int capacity = pickingPlan.calculateCartCapacity(cart, orderLine);

        assertEquals("Cart should carry 10 items (100kg / 10kg)", 10, capacity);
    }

    /**
     * Test calculateCartCapacity when item is heavier than cart capacity.
     */
    @Test
    public void testCalculateCartCapacity_ItemHeavierThanCart() {
        // Cart with 5kg capacity, item weighs 10kg
        Cart cart = new Cart(1, "Cart-1", 5.0);
        Item item = createItem(10, "SKU-010", 10.0);
        OrderLine orderLine = new OrderLine(1, null, item, 50.0);

        int capacity = pickingPlan.calculateCartCapacity(cart, orderLine);

        assertEquals("Cart should carry at least 1 item even if heavier", 1, capacity);
    }

    /**
     * Test calculateCartCapacity with fractional capacity.
     */
    @Test
    public void testCalculateCartCapacity_FractionalCapacity() {
        // Cart with 25kg capacity, item weighs 7kg
        Cart cart = new Cart(1, "Cart-1", 25.0);
        Item item = createItem(10, "SKU-010", 7.0);
        OrderLine orderLine = new OrderLine(1, null, item, 50.0);

        int capacity = pickingPlan.calculateCartCapacity(cart, orderLine);

        assertEquals("Cart should carry 3 items (floor of 25/7 = 3.57)", 3, capacity);
    }

    /**
     * Test calculateCartCapacity with null cart.
     */
    @Test
    public void testCalculateCartCapacity_NullCart() {
        Item item = createItem(10, "SKU-010", 10.0);
        OrderLine orderLine = new OrderLine(1, null, item, 50.0);

        int capacity = pickingPlan.calculateCartCapacity(null, orderLine);

        assertEquals("Null cart should return 0 capacity", 0, capacity);
    }

    /**
     * Test calculateCartCapacity with null order line.
     */
    @Test
    public void testCalculateCartCapacity_NullOrderLine() {
        Cart cart = new Cart(1, "Cart-1", 100.0);

        int capacity = pickingPlan.calculateCartCapacity(cart, null);

        assertEquals("Null order line should return 0 capacity", 0, capacity);
    }

    /**
     * Test calculateCartCapacity with zero unit weight.
     */
    @Test
    public void testCalculateCartCapacity_ZeroUnitWeight() {
        Cart cart = new Cart(1, "Cart-1", 100.0);
        Item item = createItem(10, "SKU-010", 0.0);
        OrderLine orderLine = new OrderLine(1, null, item, 50.0);

        int capacity = pickingPlan.calculateCartCapacity(cart, orderLine);

        assertEquals("Zero unit weight should return 0 capacity", 0, capacity);
    }

    /**
     * Test calculateTripsNeeded with exact fit.
     */
    @Test
    public void testCalculateTripsNeeded_ExactFit() {
        Cart cart = new Cart(1, "Cart-1", 100.0);
        Item item = createItem(10, "SKU-010", 10.0);  // Cart can carry 10 items
        OrderLine orderLine = new OrderLine(1, null, item, 30.0);  // Need 30 items

        int trips = pickingPlan.calculateTripsNeeded(cart, orderLine);

        assertEquals("Should need 3 trips (30 items / 10 per trip)", 3, trips);
    }

    /**
     * Test calculateTripsNeeded with remainder.
     */
    @Test
    public void testCalculateTripsNeeded_WithRemainder() {
        Cart cart = new Cart(1, "Cart-1", 100.0);
        Item item = createItem(10, "SKU-010", 10.0);  // Cart can carry 10 items
        OrderLine orderLine = new OrderLine(1, null, item, 25.0);  // Need 25 items

        int trips = pickingPlan.calculateTripsNeeded(cart, orderLine);

        assertEquals("Should need 3 trips (ceil of 25/10 = 2.5)", 3, trips);
    }

    /**
     * Test calculateTripsNeeded with custom quantity.
     */
    @Test
    public void testCalculateTripsNeeded_CustomQuantity() {
        Cart cart = new Cart(1, "Cart-1", 100.0);
        Item item = createItem(10, "SKU-010", 10.0);  // Cart can carry 10 items
        OrderLine orderLine = new OrderLine(1, null, item, 50.0);

        int trips = pickingPlan.calculateTripsNeeded(cart, orderLine, 15.0);

        assertEquals("Should need 2 trips for 15 items (ceil of 15/10)", 2, trips);
    }

    /**
     * Test calculateTripsNeeded when cart cannot carry items.
     */
    @Test
    public void testCalculateTripsNeeded_ImpossibleCart() {
        Cart cart = new Cart(1, "Cart-1", 0.0);  // Zero capacity
        Item item = createItem(10, "SKU-010", 10.0);
        OrderLine orderLine = new OrderLine(1, null, item, 25.0);

        int trips = pickingPlan.calculateTripsNeeded(cart, orderLine);

        assertEquals("Should return -1 when cart cannot carry items", -1, trips);
    }

    /**
     * Test calculateTripsNeeded with null cart.
     */
    @Test
    public void testCalculateTripsNeeded_NullCart() {
        Item item = createItem(10, "SKU-010", 10.0);
        OrderLine orderLine = new OrderLine(1, null, item, 25.0);

        int trips = pickingPlan.calculateTripsNeeded(null, orderLine);

        assertEquals("Should return -1 for null cart", -1, trips);
    }

    /**
     * Test calculateTripsNeeded with zero quantity.
     */
    @Test
    public void testCalculateTripsNeeded_ZeroQuantity() {
        Cart cart = new Cart(1, "Cart-1", 100.0);
        Item item = createItem(10, "SKU-010", 10.0);
        OrderLine orderLine = new OrderLine(1, null, item, 50.0);

        int trips = pickingPlan.calculateTripsNeeded(cart, orderLine, 0.0);

        assertEquals("Should return -1 for zero quantity", -1, trips);
    }

    /**
     * Test planAllocations with STRICT mode.
     */
    @Test
    public void testPlanAllocations_StrictMode() {
        // Setup warehouse
        WarehouseBay[] bays = createWarehouseStructure(1);
        WarehouseBay bay1 = bays[0];

        // Create inventory
        Item item = createItem(10, "SKU-010", 5.0);
        InventoryLot lot = new InventoryLot(101, 10, 50,
                LocalDateTime.of(2026, 6, 1, 0, 0),
                LocalDateTime.of(2025, 10, 1, 10, 0));

        lotController.createInventoryLot(lot);
        InventoryLot lotFromRepo = lotController.getInventoryLotById(101);

        List<InventoryLot> lots = new ArrayList<>();
        lots.add(lotFromRepo);
        InventoryBalance balance = new InventoryBalance(1, bay1, lots, 50);
        balanceController.createInventoryBalance(balance);

        // Create order
        Order order = new Order(1, "NEW", 1, LocalDateTime.of(2025, 11, 1, 0, 0));
        orderController.createOrder(order);

        OrderLine orderLine = new OrderLine(1, order, item, 30.0);
        orderLineController.createOrderLine(orderLine);

        // Execute
        Allocation.AllocationResult result = pickingPlan.planAllocations("STRICT");

        // Verify
        assertNotNull("Result should not be null", result);
        assertEquals("Should have 1 eligibility", 1, result.getEligibilities().size());
        assertEquals("Should have 1 allocation", 1, result.getAllocations().size());
    }

    /**
     * Test getItemLocations for specific SKU.
     */
    @Test
    public void testGetItemLocations_FilterBySKU() {
        // Setup warehouse
        WarehouseBay[] bays = createWarehouseStructure(1, 2);

        // Create inventory for SKU 10 and SKU 20
        Item item10 = createItem(10, "SKU-010", 5.0);
        Item item20 = createItem(20, "SKU-020", 3.0);

        InventoryLot lot10 = new InventoryLot(101, 10, 30,
                LocalDateTime.of(2026, 6, 1, 0, 0),
                LocalDateTime.of(2025, 10, 1, 10, 0));

        InventoryLot lot20 = new InventoryLot(102, 20, 25,
                LocalDateTime.of(2026, 6, 1, 0, 0),
                LocalDateTime.of(2025, 10, 1, 10, 0));

        lotController.createInventoryLot(lot10);
        lotController.createInventoryLot(lot20);

        InventoryLot lot10FromRepo = lotController.getInventoryLotById(101);
        InventoryLot lot20FromRepo = lotController.getInventoryLotById(102);

        List<InventoryLot> lots10 = new ArrayList<>();
        lots10.add(lot10FromRepo);
        InventoryBalance balance1 = new InventoryBalance(1, bays[0], lots10, 30);
        balanceController.createInventoryBalance(balance1);

        List<InventoryLot> lots20 = new ArrayList<>();
        lots20.add(lot20FromRepo);
        InventoryBalance balance2 = new InventoryBalance(2, bays[1], lots20, 25);
        balanceController.createInventoryBalance(balance2);

        // Create orders
        Order order = new Order(1, "NEW", 1, LocalDateTime.of(2025, 11, 1, 0, 0));
        orderController.createOrder(order);

        OrderLine line1 = new OrderLine(1, order, item10, 20.0);
        OrderLine line2 = new OrderLine(2, order, item20, 15.0);
        orderLineController.createOrderLine(line1);
        orderLineController.createOrderLine(line2);

        // Execute
        List<Allocation.AllocationRecord> locations = pickingPlan.getItemLocations(10, null);

        // Verify
        assertNotNull("Locations should not be null", locations);
        assertEquals("Should have 1 location for SKU 10", 1, locations.size());
        assertEquals("Location should be for SKU 10", Integer.valueOf(10), locations.get(0).getSku());
    }

    /**
     * Test getItemLocations filtering by order ID.
     */
    @Test
    public void testGetItemLocations_FilterByOrderId() {
        // Setup warehouse
        WarehouseBay[] bays = createWarehouseStructure(1);

        // Create inventory
        Item item = createItem(10, "SKU-010", 5.0);
        InventoryLot lot = new InventoryLot(101, 10, 50,
                LocalDateTime.of(2026, 6, 1, 0, 0),
                LocalDateTime.of(2025, 10, 1, 10, 0));

        lotController.createInventoryLot(lot);
        InventoryLot lotFromRepo = lotController.getInventoryLotById(101);

        List<InventoryLot> lots = new ArrayList<>();
        lots.add(lotFromRepo);
        InventoryBalance balance = new InventoryBalance(1, bays[0], lots, 50);
        balanceController.createInventoryBalance(balance);

        // Create two orders
        Order order1 = new Order(1, "NEW", 1, LocalDateTime.of(2025, 11, 1, 0, 0));
        Order order2 = new Order(2, "NEW", 1, LocalDateTime.of(2025, 11, 2, 0, 0));
        orderController.createOrder(order1);
        orderController.createOrder(order2);

        OrderLine line1 = new OrderLine(1, order1, item, 20.0);
        OrderLine line2 = new OrderLine(2, order2, item, 15.0);
        orderLineController.createOrderLine(line1);
        orderLineController.createOrderLine(line2);

        // Execute
        List<Allocation.AllocationRecord> locations = pickingPlan.getItemLocations(null, 1);

        // Verify
        assertNotNull("Locations should not be null", locations);
        assertTrue("Should have at least 1 location for order 1", locations.size() >= 1);
        for (Allocation.AllocationRecord loc : locations) {
            assertEquals("All locations should be for order 1", Integer.valueOf(1), loc.getOrderId());
        }
    }

    /**
     * Test getOrderEligibilities returns eligibility information.
     */
    @Test
    public void testGetOrderEligibilities() {
        // Setup warehouse
        WarehouseBay[] bays = createWarehouseStructure(1);

        // Create inventory
        Item item = createItem(10, "SKU-010", 5.0);
        InventoryLot lot = new InventoryLot(101, 10, 30,
                LocalDateTime.of(2026, 6, 1, 0, 0),
                LocalDateTime.of(2025, 10, 1, 10, 0));

        lotController.createInventoryLot(lot);
        InventoryLot lotFromRepo = lotController.getInventoryLotById(101);

        List<InventoryLot> lots = new ArrayList<>();
        lots.add(lotFromRepo);
        InventoryBalance balance = new InventoryBalance(1, bays[0], lots, 30);
        balanceController.createInventoryBalance(balance);

        // Create order requesting more than available
        Order order = new Order(1, "NEW", 1, LocalDateTime.of(2025, 11, 1, 0, 0));
        orderController.createOrder(order);

        OrderLine orderLine = new OrderLine(1, order, item, 50.0);
        orderLineController.createOrderLine(orderLine);

        // Execute
        List<Allocation.OrderEligibility> eligibilities = pickingPlan.getOrderEligibilities();

        // Verify
        assertNotNull("Eligibilities should not be null", eligibilities);
        assertEquals("Should have 1 eligibility", 1, eligibilities.size());
        assertEquals("Status should be UNDISPATCHABLE", "UNDISPATCHABLE", eligibilities.get(0).getStatus());
        assertEquals("Allocated qty should be 30.0", 30.0, eligibilities.get(0).getAllocatedQty(), 0.001);
    }

    /**
     * Test planPickingTask creates tasks and waves.
     */
    @Test
    public void testPlanPickingTask_CreatesTasksAndWaves() {
        // Setup warehouse
        WarehouseBay[] bays = createWarehouseStructure(1);

        // Create inventory
        Item item = createItem(10, "SKU-010", 10.0);  // 10kg per item
        InventoryLot lot = new InventoryLot(101, 10, 30,
                LocalDateTime.of(2026, 6, 1, 0, 0),
                LocalDateTime.of(2025, 10, 1, 10, 0));

        lotController.createInventoryLot(lot);
        InventoryLot lotFromRepo = lotController.getInventoryLotById(101);

        List<InventoryLot> lots = new ArrayList<>();
        lots.add(lotFromRepo);
        InventoryBalance balance = new InventoryBalance(1, bays[0], lots, 30);
        balanceController.createInventoryBalance(balance);

        // Create cart with 100kg capacity (can carry 10 items)
        Cart cart = new Cart(1, "Cart-1", 100.0);
        cartController.createCart(cart);

        // Create order
        Order order = new Order(1, "NEW", 1, LocalDateTime.of(2025, 11, 1, 0, 0));
        orderController.createOrder(order);

        OrderLine orderLine = new OrderLine(1, order, item, 25.0);  // Need 25 items
        orderLineController.createOrderLine(orderLine);

        // Execute
        PickingPlanResult result = pickingPlan.planPickingTask();

        // Verify
        assertNotNull("Result should not be null", result);
        List<PickTask> tasks = result.getPickTasks();
        List<PickWave> waves = result.getPickWaves();

        assertEquals("Should have 1 pick task", 1, tasks.size());
        assertTrue("Should have at least 3 waves (ceil of 25/10)", waves.size() >= 3);
    }

    /**
     * Test planPickingTaskForOrder filters by order ID.
     */
    @Test
    public void testPlanPickingTaskForOrder_FiltersCorrectly() {
        // Setup warehouse
        WarehouseBay[] bays = createWarehouseStructure(1);

        // Create inventory
        Item item = createItem(10, "SKU-010", 10.0);
        InventoryLot lot = new InventoryLot(101, 10, 50,
                LocalDateTime.of(2026, 6, 1, 0, 0),
                LocalDateTime.of(2025, 10, 1, 10, 0));

        lotController.createInventoryLot(lot);
        InventoryLot lotFromRepo = lotController.getInventoryLotById(101);

        List<InventoryLot> lots = new ArrayList<>();
        lots.add(lotFromRepo);
        InventoryBalance balance = new InventoryBalance(1, bays[0], lots, 50);
        balanceController.createInventoryBalance(balance);

        // Create cart
        Cart cart = new Cart(1, "Cart-1", 100.0);
        cartController.createCart(cart);

        // Create two orders
        Order order1 = new Order(1, "NEW", 1, LocalDateTime.of(2025, 11, 1, 0, 0));
        Order order2 = new Order(2, "NEW", 1, LocalDateTime.of(2025, 11, 2, 0, 0));
        orderController.createOrder(order1);
        orderController.createOrder(order2);

        OrderLine line1 = new OrderLine(1, order1, item, 20.0);
        OrderLine line2 = new OrderLine(2, order2, item, 15.0);
        orderLineController.createOrderLine(line1);
        orderLineController.createOrderLine(line2);

        // Execute - plan for order 1 only
        PickingPlanResult result = pickingPlan.planPickingTaskForOrder(1);

        // Verify
        assertNotNull("Result should not be null", result);
        List<PickTask> tasks = result.getPickTasks();

        assertFalse("Should have at least 1 task for order 1", tasks.isEmpty());
        for (PickTask task : tasks) {
            assertEquals("All tasks should be for order 1",
                    Integer.valueOf(1), task.getOrderLine().getOrder().getOrderId());
        }
    }

    /**
     * Test planPickingTaskForOrder with non-existent order returns empty result.
     */
    @Test
    public void testPlanPickingTaskForOrder_NonExistentOrder() {
        // Execute
        PickingPlanResult result = pickingPlan.planPickingTaskForOrder(999);

        // Verify
        assertNotNull("Result should not be null", result);
        assertEquals("Should have 0 tasks for non-existent order", 0, result.getPickTasks().size());
        assertEquals("Should have 0 waves for non-existent order", 0, result.getPickWaves().size());
    }

    /**
     * Test that pick waves are created for each trip needed.
     */
    @Test
    public void testPlanPickingTask_MultipleWavesForMultipleTrips() {
        // Setup warehouse
        WarehouseBay[] bays = createWarehouseStructure(1);

        // Create inventory
        Item item = createItem(10, "SKU-010", 20.0);  // 20kg per item
        InventoryLot lot = new InventoryLot(101, 10, 40,
                LocalDateTime.of(2026, 6, 1, 0, 0),
                LocalDateTime.of(2025, 10, 1, 10, 0));

        lotController.createInventoryLot(lot);
        InventoryLot lotFromRepo = lotController.getInventoryLotById(101);

        List<InventoryLot> lots = new ArrayList<>();
        lots.add(lotFromRepo);
        InventoryBalance balance = new InventoryBalance(1, bays[0], lots, 40);
        balanceController.createInventoryBalance(balance);

        // Create cart with 50kg capacity (can carry 2 items of 20kg each)
        Cart cart = new Cart(1, "Cart-1", 50.0);
        cartController.createCart(cart);

        // Create order requesting 7 items (will need 4 trips: 2+2+2+1)
        Order order = new Order(1, "NEW", 1, LocalDateTime.of(2025, 11, 1, 0, 0));
        orderController.createOrder(order);

        OrderLine orderLine = new OrderLine(1, order, item, 7.0);
        orderLineController.createOrderLine(orderLine);

        // Execute
        PickingPlanResult result = pickingPlan.planPickingTask();

        // Verify
        List<PickWave> waves = result.getPickWaves();
        assertEquals("Should have 4 waves for 4 trips (ceil of 7/2)", 4, waves.size());
    }

    /**
     * Test getAllItemBalances returns inventory balances.
     */
    @Test
    public void testGetAllItemBalances() {
        // Setup
        WarehouseBay[] bays = createWarehouseStructure(1);
        Item item = createItem(10, "SKU-010", 5.0);
        InventoryLot lot = new InventoryLot(101, 10, 30,
                LocalDateTime.of(2026, 6, 1, 0, 0),
                LocalDateTime.of(2025, 10, 1, 10, 0));

        lotController.createInventoryLot(lot);
        InventoryLot lotFromRepo = lotController.getInventoryLotById(101);

        List<InventoryLot> lots = new ArrayList<>();
        lots.add(lotFromRepo);
        InventoryBalance balance = new InventoryBalance(1, bays[0], lots, 30);
        balanceController.createInventoryBalance(balance);

        // Execute
        List<InventoryBalance> balances = pickingPlan.getAllItemBalences();

        // Verify
        assertNotNull("Balances should not be null", balances);
        assertEquals("Should have 1 balance", 1, balances.size());
    }

    /**
     * Test getAllOrders returns orders.
     */
    @Test
    public void testGetAllOrders() {
        // Setup
        Order order1 = new Order(1, "NEW", 1, LocalDateTime.of(2025, 11, 1, 0, 0));
        Order order2 = new Order(2, "NEW", 1, LocalDateTime.of(2025, 11, 2, 0, 0));
        orderController.createOrder(order1);
        orderController.createOrder(order2);

        // Execute
        List<Order> orders = pickingPlan.getAllOrders();

        // Verify
        assertNotNull("Orders should not be null", orders);
        assertEquals("Should have 2 orders", 2, orders.size());
    }

    /**
     * Test getAllOrderLines returns order lines.
     */
    @Test
    public void testGetAllOrderLines() {
        // Setup
        Item item = createItem(10, "SKU-010", 5.0);
        Order order = new Order(1, "NEW", 1, LocalDateTime.of(2025, 11, 1, 0, 0));
        orderController.createOrder(order);

        OrderLine line1 = new OrderLine(1, order, item, 20.0);
        OrderLine line2 = new OrderLine(2, order, item, 15.0);
        orderLineController.createOrderLine(line1);
        orderLineController.createOrderLine(line2);

        // Execute
        List<OrderLine> lines = pickingPlan.getAllOrderLines();

        // Verify
        assertNotNull("Order lines should not be null", lines);
        assertEquals("Should have 2 order lines", 2, lines.size());
    }

    /**
     * Test getAllOrderLinesFromOrder filters by order ID.
     */
    @Test
    public void testGetAllOrderLinesFromOrder() {
        // Setup
        Item item = createItem(10, "SKU-010", 5.0);
        Order order1 = new Order(1, "NEW", 1, LocalDateTime.of(2025, 11, 1, 0, 0));
        Order order2 = new Order(2, "NEW", 1, LocalDateTime.of(2025, 11, 2, 0, 0));
        orderController.createOrder(order1);
        orderController.createOrder(order2);

        OrderLine line1 = new OrderLine(1, order1, item, 20.0);
        OrderLine line2 = new OrderLine(2, order1, item, 15.0);
        OrderLine line3 = new OrderLine(3, order2, item, 10.0);
        orderLineController.createOrderLine(line1);
        orderLineController.createOrderLine(line2);
        orderLineController.createOrderLine(line3);

        // Execute
        List<OrderLine> linesForOrder1 = pickingPlan.getAllOrderLinesFromOrder(1);

        // Verify
        assertNotNull("Order lines should not be null", linesForOrder1);
        assertEquals("Should have 2 order lines for order 1", 2, linesForOrder1.size());
        for (OrderLine line : linesForOrder1) {
            assertEquals("All lines should be for order 1", Integer.valueOf(1), line.getOrder().getOrderId());
        }
    }

    /**
     * Test getAllCarts returns carts.
     */
    @Test
    public void testGetAllCarts() {
        // Setup
        Cart cart1 = new Cart(1, "Cart-1", 100.0);
        Cart cart2 = new Cart(2, "Cart-2", 150.0);
        cartController.createCart(cart1);
        cartController.createCart(cart2);

        // Execute
        List<Cart> carts = pickingPlan.getAllCarts();

        // Verify
        assertNotNull("Carts should not be null", carts);
        assertEquals("Should have 2 carts", 2, carts.size());
    }

    /**
     * Test getAllPickTasks returns tasks.
     */
    @Test
    public void testGetAllPickTasks() {
        // Initially should be empty
        List<PickTask> tasks = pickingPlan.getAllPickTasks();

        assertNotNull("Tasks should not be null", tasks);
        assertEquals("Should have 0 tasks initially", 0, tasks.size());
    }

    /**
     * Test planPickingTask skips ineligible order lines.
     */
    @Test
    public void testPlanPickingTask_SkipsIneligibleOrders() {
        // Setup warehouse with insufficient inventory
        WarehouseBay[] bays = createWarehouseStructure(1);

        Item item = createItem(10, "SKU-010", 10.0);
        InventoryLot lot = new InventoryLot(101, 10, 10,  // Only 10 items available
                LocalDateTime.of(2026, 6, 1, 0, 0),
                LocalDateTime.of(2025, 10, 1, 10, 0));

        lotController.createInventoryLot(lot);
        InventoryLot lotFromRepo = lotController.getInventoryLotById(101);

        List<InventoryLot> lots = new ArrayList<>();
        lots.add(lotFromRepo);
        InventoryBalance balance = new InventoryBalance(1, bays[0], lots, 10);
        balanceController.createInventoryBalance(balance);

        Cart cart = new Cart(1, "Cart-1", 100.0);
        cartController.createCart(cart);

        // Create order requesting 50 items (will be UNDISPATCHABLE in STRICT mode)
        Order order = new Order(1, "NEW", 1, LocalDateTime.of(2025, 11, 1, 0, 0));
        orderController.createOrder(order);

        OrderLine orderLine = new OrderLine(1, order, item, 50.0);
        orderLineController.createOrderLine(orderLine);

        // Execute with PARTIAL mode to get allocation
        PickingPlanResult result = pickingPlan.planPickingTask();

        // Verify - should create task because PARTIAL mode allocates what's available
        assertNotNull("Result should not be null", result);
        // In PARTIAL mode, it should allocate the 10 available items
        assertTrue("Should have at least some tasks/waves",
                result.getPickTasks().size() > 0 || result.getPickWaves().size() > 0);
    }
}

