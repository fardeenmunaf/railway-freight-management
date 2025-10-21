package app.controller;

import app.model.OrderLine;
import app.repositories.OrderLineRepository;
import app.repositories.Repositories;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing OrderLine entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for OrderLine objects.
 * This controller manages order line data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class OrderLineController {
    private final OrderLineRepository repository;

    /**
     * Default constructor that initializes the controller with the order line repository.
     */
    public OrderLineController() {
        this.repository = Repositories.getInstance().getOrderLineRepository();
    }

    /**
     * Creates and adds a new order line to the system.
     *
     * @param orderLine the OrderLine object to be created
     * @return the created OrderLine object
     */
    public OrderLine createOrderLine(OrderLine orderLine) {
        if (orderLine != null) {
            repository.addOrderLine(orderLine);
        }
        return orderLine;
    }

    /**
     * Retrieves all order lines from the system.
     *
     * @return a list of all OrderLine objects
     */
    public List<OrderLine> getAllOrderLines() {
        return repository.getAllOrderLines();
    }

    /**
     * Retrieves an order line by its unique identifier.
     *
     * @param orderLineId the unique identifier of the order line
     * @return an Optional containing the OrderLine if found, or empty if not found
     */
    public Optional<OrderLine> getOrderLineById(Integer orderLineId) {
        OrderLine orderLine = repository.getOrderLineById(orderLineId);
        return Optional.ofNullable(orderLine);
    }

    /**
     * Updates an existing order line in the system.
     *
     * @param orderLineId the unique identifier of the order line to update
     * @param updatedOrderLine the OrderLine object with updated information
     * @return true if the order line was updated successfully, false otherwise
     */
    public boolean updateOrderLine(Integer orderLineId, OrderLine updatedOrderLine) {
        return repository.updateOrderLine(orderLineId, updatedOrderLine);
    }

    /**
     * Deletes an order line from the system.
     *
     * @param orderLineId the unique identifier of the order line to delete
     * @return true if the order line was deleted successfully, false otherwise
     */
    public boolean deleteOrderLine(Integer orderLineId) {
        return repository.deleteOrderLine(orderLineId);
    }

    /**
     * Gets the next available order line ID.
     *
     * @return the next order line ID to be used
     */
    public Integer getNextOrderLineId() {
        return repository.getNextOrderLineId();
    }

    /**
     * Clears all order lines from the system.
     */
    public void clearAllOrderLines() {
        repository.clear();
    }
}
