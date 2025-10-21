package app.controller;

import app.model.Order;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing Order entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for Order objects.
 * This controller manages order data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class OrderController {
    private List<Order> orders;

    /**
     * Default constructor that initializes the order list.
     */
    public OrderController() {
        this.orders = new ArrayList<>();
    }

    /**
     * Creates and adds a new order to the system.
     *
     * @param order the Order object to be created
     * @return the created Order object
     */
    public Order createOrder(Order order) {
        if (order != null) {
            orders.add(order);
        }
        return order;
    }

    /**
     * Retrieves all orders from the system.
     *
     * @return a list of all Order objects
     */
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }

    /**
     * Retrieves an order by its unique identifier.
     *
     * @param orderId the unique identifier of the order
     * @return an Optional containing the Order if found, or empty if not found
     */
    public Optional<Order> getOrderById(Integer orderId) {
        return orders.stream()
                .filter(order -> order.getOrderId().equals(orderId))
                .findFirst();
    }

    /**
     * Updates an existing order in the system.
     *
     * @param orderId the unique identifier of the order to update
     * @param updatedOrder the Order object with updated information
     * @return true if the order was updated successfully, false otherwise
     */
    public boolean updateOrder(Integer orderId, Order updatedOrder) {
        Optional<Order> existingOrder = getOrderById(orderId);
        if (existingOrder.isPresent()) {
            int index = orders.indexOf(existingOrder.get());
            orders.set(index, updatedOrder);
            return true;
        }
        return false;
    }

    /**
     * Deletes an order from the system.
     *
     * @param orderId the unique identifier of the order to delete
     * @return true if the order was deleted successfully, false otherwise
     */
    public boolean deleteOrder(Integer orderId) {
        return orders.removeIf(order -> order.getOrderId().equals(orderId));
    }
}

