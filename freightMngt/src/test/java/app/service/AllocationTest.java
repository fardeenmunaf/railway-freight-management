package app.service;

import app.controller.InventoryBalanceController;
import app.controller.InventoryLotController;
import app.controller.OrderController;
import app.controller.OrderLineController;
import app.controller.WarehouseBayController;
import app.model.*;
import app.service.Allocation.AllocationResult;
import app.service.Allocation.OrderEligibility;
import app.service.Allocation.AllocationRecord;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for Allocation service.
 * Tests the allocation of inventory to orders using FEFO strategy in STRICT and PARTIAL modes.
 */
public class AllocationTest {

    private Allocation allocation;
    private InventoryBalanceController balanceController;
    private InventoryLotController lotController;
    private WarehouseBayController bayController;
    private OrderController orderController;
    private OrderLineController orderLineController;

    @Before
    public void setUp() {
        balanceController = new InventoryBalanceController();
        lotController = new InventoryLotController();
        bayController = new WarehouseBayController();
        orderController = new OrderController();
        orderLineController = new OrderLineController();

        balanceController.clearAllInventoryBalances();
        bayController.clearAllWarehouseBays();
        orderController.clearAllOrders();
        app.repositories.Repositories.getInstance().getInventoryLotRepository().clear();
        app.repositories.Repositories.getInstance().getOrderLineRepository().clear();

        allocation = new Allocation();
    }

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

    private Item createItem(Integer itemId, String skuCode) {
        ItemCategory category = new ItemCategory(1, "Test Category");
        return new Item(itemId, category, skuCode, "Test Item " + itemId, "EA", 1.0, 1.0);
    }

    @Test
    public void testStrictMode_FullAllocation() {
        WarehouseBay[] bays = createWarehouseStructure(1);
        Item item = createItem(10, "SKU-010");
        InventoryLot lot = new InventoryLot(101, 10, 50,
            LocalDateTime.of(2026, 6, 1, 0, 0),
            LocalDateTime.of(2025, 10, 1, 10, 0));

        lotController.createInventoryLot(lot);
        List<InventoryLot> lots = new ArrayList<>();
        lots.add(lotController.getInventoryLotById(101));
        balanceController.createInventoryBalance(new InventoryBalance(1, bays[0], lots, 50));

        Order order = new Order(1, "NEW", 1, LocalDateTime.of(2025, 11, 1, 0, 0));
        orderController.createOrder(order);
        orderLineController.createOrderLine(new OrderLine(1, order, item, 30.0));

        List<Order> orders = new ArrayList<>();
        orders.add(order);
        AllocationResult result = allocation.allocateOrders(orders, balanceController.getAllInventoryBalances(), "STRICT");

        assertEquals(1, result.getEligibilities().size());
        assertEquals("ELIGIBLE", result.getEligibilities().get(0).getStatus());
        assertEquals(1, result.getAllocations().size());
    }

    @Test
    public void testStrictMode_InsufficientStock() {
        WarehouseBay[] bays = createWarehouseStructure(1);
        Item item = createItem(10, "SKU-010");
        InventoryLot lot = new InventoryLot(102, 10, 20,
            LocalDateTime.of(2026, 6, 1, 0, 0),
            LocalDateTime.of(2025, 10, 1, 10, 0));

        lotController.createInventoryLot(lot);
        List<InventoryLot> lots = new ArrayList<>();
        lots.add(lotController.getInventoryLotById(102));
        balanceController.createInventoryBalance(new InventoryBalance(1, bays[0], lots, 20));

        Order order = new Order(1, "NEW", 1, LocalDateTime.of(2025, 11, 1, 0, 0));
        orderController.createOrder(order);
        orderLineController.createOrderLine(new OrderLine(1, order, item, 30.0));

        List<Order> orders = new ArrayList<>();
        orders.add(order);
        AllocationResult result = allocation.allocateOrders(orders, balanceController.getAllInventoryBalances(), "STRICT");

        assertEquals("UNDISPATCHABLE", result.getEligibilities().get(0).getStatus());
        assertEquals(0, result.getAllocations().size());
    }

    @Test
    public void testPartialMode_PartialAllocation() {
        WarehouseBay[] bays = createWarehouseStructure(1);
        Item item = createItem(10, "SKU-010");
        InventoryLot lot = new InventoryLot(103, 10, 20,
            LocalDateTime.of(2026, 6, 1, 0, 0),
            LocalDateTime.of(2025, 10, 1, 10, 0));

        lotController.createInventoryLot(lot);
        List<InventoryLot> lots = new ArrayList<>();
        lots.add(lotController.getInventoryLotById(103));
        balanceController.createInventoryBalance(new InventoryBalance(1, bays[0], lots, 20));

        Order order = new Order(1, "NEW", 1, LocalDateTime.of(2025, 11, 1, 0, 0));
        orderController.createOrder(order);
        orderLineController.createOrderLine(new OrderLine(1, order, item, 30.0));

        List<Order> orders = new ArrayList<>();
        orders.add(order);
        AllocationResult result = allocation.allocateOrders(orders, balanceController.getAllInventoryBalances(), "PARTIAL");

        assertEquals("PARTIAL", result.getEligibilities().get(0).getStatus());
        assertEquals(20.0, result.getEligibilities().get(0).getAllocatedQty(), 0.001);
        assertEquals(1, result.getAllocations().size());
    }

    @Test
    public void testFEFO_AllocatesEarliestExpiryFirst() {
        WarehouseBay[] bays = createWarehouseStructure(1, 2);
        Item item = createItem(20, "SKU-020");

        InventoryLot lotEarly = new InventoryLot(201, 20, 30,
            LocalDateTime.of(2026, 2, 1, 0, 0),
            LocalDateTime.of(2025, 10, 1, 10, 0));

        InventoryLot lotLate = new InventoryLot(202, 20, 40,
            LocalDateTime.of(2026, 6, 1, 0, 0),
            LocalDateTime.of(2025, 10, 2, 10, 0));

        lotController.createInventoryLot(lotEarly);
        lotController.createInventoryLot(lotLate);

        List<InventoryLot> lotsEarly = new ArrayList<>();
        lotsEarly.add(lotController.getInventoryLotById(201));
        balanceController.createInventoryBalance(new InventoryBalance(1, bays[0], lotsEarly, 30));

        List<InventoryLot> lotsLate = new ArrayList<>();
        lotsLate.add(lotController.getInventoryLotById(202));
        balanceController.createInventoryBalance(new InventoryBalance(2, bays[1], lotsLate, 40));

        Order order = new Order(1, "NEW", 1, LocalDateTime.of(2025, 11, 1, 0, 0));
        orderController.createOrder(order);
        orderLineController.createOrderLine(new OrderLine(1, order, item, 25.0));

        List<Order> orders = new ArrayList<>();
        orders.add(order);
        AllocationResult result = allocation.allocateOrders(orders, balanceController.getAllInventoryBalances(), "STRICT");

        assertEquals(1, result.getAllocations().size());
        assertEquals(Integer.valueOf(201), result.getAllocations().get(0).getBoxId());
    }

    @Test
    public void testFEFO_AcrossMultipleLots() {
        WarehouseBay[] bays = createWarehouseStructure(1, 2);
        Item item = createItem(30, "SKU-030");

        InventoryLot lot1 = new InventoryLot(301, 30, 20,
            LocalDateTime.of(2026, 1, 1, 0, 0),
            LocalDateTime.of(2025, 10, 1, 10, 0));

        InventoryLot lot2 = new InventoryLot(302, 30, 25,
            LocalDateTime.of(2026, 3, 1, 0, 0),
            LocalDateTime.of(2025, 10, 2, 10, 0));

        lotController.createInventoryLot(lot1);
        lotController.createInventoryLot(lot2);

        List<InventoryLot> lots1 = new ArrayList<>();
        lots1.add(lotController.getInventoryLotById(301));
        balanceController.createInventoryBalance(new InventoryBalance(1, bays[0], lots1, 20));

        List<InventoryLot> lots2 = new ArrayList<>();
        lots2.add(lotController.getInventoryLotById(302));
        balanceController.createInventoryBalance(new InventoryBalance(2, bays[1], lots2, 25));

        Order order = new Order(1, "NEW", 1, LocalDateTime.of(2025, 11, 1, 0, 0));
        orderController.createOrder(order);
        orderLineController.createOrderLine(new OrderLine(1, order, item, 35.0));

        List<Order> orders = new ArrayList<>();
        orders.add(order);
        AllocationResult result = allocation.allocateOrders(orders, balanceController.getAllInventoryBalances(), "STRICT");

        assertEquals(2, result.getAllocations().size());
        assertEquals(Integer.valueOf(301), result.getAllocations().get(0).getBoxId());
        assertEquals(20.0, result.getAllocations().get(0).getQty(), 0.001);
        assertEquals(Integer.valueOf(302), result.getAllocations().get(1).getBoxId());
        assertEquals(15.0, result.getAllocations().get(1).getQty(), 0.001);
    }

    @Test
    public void testOrderPriority_HighPriorityFirst() {
        WarehouseBay[] bays = createWarehouseStructure(1);
        Item item = createItem(40, "SKU-040");

        InventoryLot lot = new InventoryLot(401, 40, 30,
            LocalDateTime.of(2026, 6, 1, 0, 0),
            LocalDateTime.of(2025, 10, 1, 10, 0));

        lotController.createInventoryLot(lot);
        List<InventoryLot> lots = new ArrayList<>();
        lots.add(lotController.getInventoryLotById(401));
        balanceController.createInventoryBalance(new InventoryBalance(1, bays[0], lots, 30));

        Order orderLowPriority = new Order(1, "NEW", 3, LocalDateTime.of(2025, 11, 1, 0, 0));
        orderController.createOrder(orderLowPriority);
        orderLineController.createOrderLine(new OrderLine(1, orderLowPriority, item, 20.0));

        Order orderHighPriority = new Order(2, "NEW", 1, LocalDateTime.of(2025, 11, 1, 0, 0));
        orderController.createOrder(orderHighPriority);
        orderLineController.createOrderLine(new OrderLine(2, orderHighPriority, item, 20.0));

        List<Order> orders = new ArrayList<>();
        orders.add(orderLowPriority);
        orders.add(orderHighPriority);

        AllocationResult result = allocation.allocateOrders(orders, balanceController.getAllInventoryBalances(), "STRICT");

        assertEquals(1, result.getAllocations().size());
        assertEquals(Integer.valueOf(2), result.getAllocations().get(0).getOrderId());
    }

    @Test
    public void testMultipleOrderLines() {
        WarehouseBay[] bays = createWarehouseStructure(1, 2);
        Item item1 = createItem(50, "SKU-050");
        Item item2 = createItem(51, "SKU-051");

        InventoryLot lot1 = new InventoryLot(501, 50, 40,
            LocalDateTime.of(2026, 6, 1, 0, 0),
            LocalDateTime.of(2025, 10, 1, 10, 0));

        InventoryLot lot2 = new InventoryLot(502, 51, 30,
            LocalDateTime.of(2026, 6, 1, 0, 0),
            LocalDateTime.of(2025, 10, 1, 10, 0));

        lotController.createInventoryLot(lot1);
        lotController.createInventoryLot(lot2);

        List<InventoryLot> lots1 = new ArrayList<>();
        lots1.add(lotController.getInventoryLotById(501));
        balanceController.createInventoryBalance(new InventoryBalance(1, bays[0], lots1, 40));

        List<InventoryLot> lots2 = new ArrayList<>();
        lots2.add(lotController.getInventoryLotById(502));
        balanceController.createInventoryBalance(new InventoryBalance(2, bays[1], lots2, 30));

        Order order = new Order(1, "NEW", 1, LocalDateTime.of(2025, 11, 1, 0, 0));
        orderController.createOrder(order);
        orderLineController.createOrderLine(new OrderLine(1, order, item1, 20.0));
        orderLineController.createOrderLine(new OrderLine(2, order, item2, 15.0));

        List<Order> orders = new ArrayList<>();
        orders.add(order);

        AllocationResult result = allocation.allocateOrders(orders, balanceController.getAllInventoryBalances(), "STRICT");

        assertEquals(2, result.getEligibilities().size());
        assertEquals(2, result.getAllocations().size());
    }

    @Test
    public void testAllocationRecord_GettersReturnCorrectValues() {
        AllocationRecord record = new AllocationRecord(1, 2, 10, 25.5, 101, "A-001", 5);

        assertEquals(Integer.valueOf(1), record.getOrderId());
        assertEquals(Integer.valueOf(2), record.getLineNo());
        assertEquals(Integer.valueOf(10), record.getSku());
        assertEquals(25.5, record.getQty(), 0.001);
        assertEquals(Integer.valueOf(101), record.getBoxId());
        assertEquals("A-001", record.getAisle());
        assertEquals(Integer.valueOf(5), record.getBay());
    }

    @Test
    public void testOrderEligibility_GettersReturnCorrectValues() {
        OrderEligibility eligibility = new OrderEligibility(1, 2, 10, 30.0, 25.0, "PARTIAL");

        assertEquals(Integer.valueOf(1), eligibility.getOrderId());
        assertEquals(Integer.valueOf(2), eligibility.getLineNo());
        assertEquals(Integer.valueOf(10), eligibility.getSku());
        assertEquals(30.0, eligibility.getRequestedQty(), 0.001);
        assertEquals(25.0, eligibility.getAllocatedQty(), 0.001);
        assertEquals("PARTIAL", eligibility.getStatus());
    }

    @Test
    public void testAllocationResult_GettersReturnCorrectLists() {
        List<OrderEligibility> eligibilities = new ArrayList<>();
        eligibilities.add(new OrderEligibility(1, 1, 10, 50.0, 50.0, "ELIGIBLE"));

        List<AllocationRecord> allocations = new ArrayList<>();
        allocations.add(new AllocationRecord(1, 1, 10, 50.0, 101, "A-001", 1));

        AllocationResult result = new AllocationResult(eligibilities, allocations);

        assertNotNull(result.getEligibilities());
        assertNotNull(result.getAllocations());
        assertEquals(1, result.getEligibilities().size());
        assertEquals(1, result.getAllocations().size());
    }
}
