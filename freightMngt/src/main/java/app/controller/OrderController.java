package app.controller;

import app.model.Order;
import app.repositories.OrderRepository;
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
    private final OrderRepository repository;

    /**
     * Default constructor that initializes the controller with a new repository.
     */
    public OrderController() {
        this.repository = new OrderRepository();
    }

    /**
     * Constructor that accepts a repository instance.
     *
     * @param repository the OrderRepository to use
     */
    public OrderController(OrderRepository repository) {
        this.repository = repository;
    }

    /**
     * Creates and adds a new order to the system.
     *
     * @param order the Order object to be created
     * @return the created Order object
     */
    public Order createOrder(Order order) {
        if (order != null) {
            repository.addOrder(order);
        }
        return order;
    }

    /**
     * Retrieves all orders from the system.
     *
     * @return a list of all Order objects
     */
    public List<Order> getAllOrders() {
        return repository.getAllOrders();
    }

    /**
     * Retrieves an order by its unique identifier.
     *
     * @param orderId the unique identifier of the order
     * @return an Optional containing the Order if found, or empty if not found
     */
    public Optional<Order> getOrderById(Integer orderId) {
        Order order = repository.getOrderById(orderId);
        return Optional.ofNullable(order);
    }

    /**
     * Retrieves all orders with a specific status.
     *
     * @param status the order status to filter by
     * @return a list of orders with the specified status
     */
    public List<Order> getOrdersByStatus(String status) {
        return repository.getOrdersByStatus(status);
    }

    /**
     * Retrieves all orders with a specific priority.
     *
     * @param priority the priority level to filter by
     * @return a list of orders with the specified priority
     */
    public List<Order> getOrdersByPriority(Integer priority) {
        return repository.getOrdersByPriority(priority);
    }

    /**
     * Updates an existing order in the system.
     *
     * @param orderId the unique identifier of the order to update
     * @param updatedOrder the Order object with updated information
     * @return true if the order was updated successfully, false otherwise
     */
    public boolean updateOrder(Integer orderId, Order updatedOrder) {
        return repository.updateOrder(orderId, updatedOrder);
    }

    /**
     * Deletes an order from the system.
     *
     * @param orderId the unique identifier of the order to delete
     * @return true if the order was deleted successfully, false otherwise
     */
    public boolean deleteOrder(Integer orderId) {
        return repository.deleteOrder(orderId);
    }

    /**
     * Gets the next available order ID.
     *
     * @return the next available order ID
     */
    public Integer getNextOrderId() {
        return repository.getNextOrderId();
    }
}
