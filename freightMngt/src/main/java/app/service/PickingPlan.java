package app.service;

import java.util.ArrayList;
import java.util.List;

import app.controller.CartController;
import app.controller.InventoryBalanceController;
import app.controller.OrderController;
import app.controller.OrderLineController;
import app.model.*;

public class PickingPlan {
    List<PickTask> tasks;

    InventoryBalanceController inventoryBalanceController = new InventoryBalanceController();
    OrderController orderController = new OrderController();
    OrderLineController orderLineController = new OrderLineController();
    CartController cartController = new CartController();

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

    public List<Cart> getAllCarts() {
        return cartController.getAllCarts();
    }

    /**
     * Calculates how many items from an order line a cart can carry based on weight capacity.
     *
     * @param cart the cart to check capacity
     * @param orderLine the order line containing the item information
     * @return the maximum number of items the cart can carry, or 0 if cart/orderLine is null
     */
    public int calculateCartCapacity(Cart cart, OrderLine orderLine) {
        if (cart == null || orderLine == null || orderLine.getSku() == null) {
            return 0;
        }

        Double maxWeight = cart.getMaxWeight();
        Double unitWeight = orderLine.getSku().getUnitWeight();

        if (maxWeight == null || unitWeight == null || unitWeight <= 0) {
            return 0;
        }

        // Calculate how many items fit based on weight
        return (int) Math.floor(maxWeight / unitWeight);
    }

    /**
     * Calculates how many trips are needed to transport all items from an order line.
     * If the cart cannot carry any items (capacity is 0), returns -1 to indicate impossibility.
     *
     * @param cart the cart to use for transport
     * @param orderLine the order line containing the items to transport
     * @return the number of trips needed, or -1 if impossible to transport
     */
    public int calculateTripsNeeded(Cart cart, OrderLine orderLine) {
        if (cart == null || orderLine == null || orderLine.getQty() == null) {
            return -1;
        }

        int cartCapacity = calculateCartCapacity(cart, orderLine);

        // If cart cannot carry any items, it's impossible
        if (cartCapacity == 0) {
            return -1;
        }

        double totalQuantity = orderLine.getQty();

        // Calculate trips needed (round up to ensure all items are transported)
        return (int) Math.ceil(totalQuantity / cartCapacity);
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
