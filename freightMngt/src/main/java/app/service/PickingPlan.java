package app.service;

import java.util.ArrayList;
import java.util.List;

import app.controller.InventoryBalanceController;
import app.controller.OrderController;
import app.controller.OrderLineController;
import app.model.InventoryBalance;
import app.model.Order;
import app.model.OrderLine;
import app.model.PickTask;

public class PickingPlan {
    List<PickTask> tasks;

    InventoryBalanceController inventoryBalanceController = new InventoryBalanceController();
    OrderController orderController = new OrderController();
    OrderLineController orderLineController = new OrderLineController();

    public PickingPlan() {
        this.tasks = new ArrayList<>();
    }

    public List<InventoryBalance> getAllItemBalences() {
        return inventoryBalanceController.getAllInventoryBalances();
    }

    public List<Order> getAllOrders() {
        return orderController.getAllOrders();
    }

    public List<OrderLine> getAllOrderLines() {
        return orderLineController.getAllOrderLines();
    }

    public List<OrderLine> getAllOrderLinesFromOrder(int orderId) {
        return orderLineController.getOrderLinesByOrderId(orderId);
    }

    public List<PickTask> getAllPickTasks() {
        return tasks;
    }

    /**
     * Plans allocations by invoking the AllocationService.
     * @param mode allocation mode: "STRICT" (default) or "PARTIAL"
     * @return AllocationResult with eligibilities and allocation rows
     */
    public AllocationService.AllocationResult planAllocations(String mode) {
        if (mode == null || mode.isBlank()) mode = "STRICT";
        AllocationService allocator = new AllocationService();
        List<Order> orders = getAllOrders();
        List<InventoryBalance> balances = getAllItemBalences();
        return allocator.allocateOrders(orders, balances, mode);
    }
}
