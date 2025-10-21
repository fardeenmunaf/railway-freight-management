package app.repositories;

import app.model.Order;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing orders in the freight management system.
 * This class provides methods to add, retrieve, and manage order entities.
 * It maintains a collection of all orders and allows searching by order ID or status.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class OrderRepository {

    private final List<Order> orders = new ArrayList<>();

    /**
     * Adds a new order to the repository if it doesn't already exist.
     *
     * @param order the order to add
     */
    public void addOrder(Order order) {
        if (order != null && order.getOrderId() != null) {
            if (!containsOrder(order.getOrderId())) {
                orders.add(order);
            }
        }
    }

    /**
     * Retrieves all orders in the repository.
     *
     * @return a new list containing all orders
     */
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }

    /**
     * Retrieves a specific order by its ID.
     *
     * @param orderId the order identifier
     * @return the matching order, or null if not found
     */
    public Order getOrderById(Integer orderId) {
        for (Order order : orders) {
            if (orderId.equals(order.getOrderId())) {
                return order;
            }
        }
        return null;
    }

    /**
     * Retrieves all orders with a specific status.
     *
     * @param status the order status to filter by
     * @return a list of orders with the specified status
     */
    public List<Order> getOrdersByStatus(String status) {
        List<Order> result = new ArrayList<>();
        for (Order order : orders) {
            if (status != null && status.equals(order.getStatus())) {
                result.add(order);
            }
        }
        return result;
    }

    /**
     * Retrieves all orders with a specific priority.
     *
     * @param priority the priority level to filter by
     * @return a list of orders with the specified priority
     */
    public List<Order> getOrdersByPriority(Integer priority) {
        List<Order> result = new ArrayList<>();
        for (Order order : orders) {
            if (priority != null && priority.equals(order.getPriority())) {
                result.add(order);
            }
        }
        return result;
    }

    /**
     * Gets the next available order ID.
     * Finds the maximum order ID currently in use and returns the next sequential ID.
     *
     * @return the next available order ID (max current ID + 1)
     */
    public Integer getNextOrderId() {
        int maxId = 0;
        for (Order order : orders) {
            if (order.getOrderId() != null && order.getOrderId() > maxId) {
                maxId = order.getOrderId();
            }
        }
        return maxId + 1;
    }

    /**
     * Checks if an order exists in the repository by its ID.
     *
     * @param orderId the order identifier
     * @return true if the order exists, false otherwise
     */
    private boolean containsOrder(Integer orderId) {
        return getOrderById(orderId) != null;
    }

    /**
     * Clears all orders from the repository.
     */
    public void clear() {
        orders.clear();
    }
}

