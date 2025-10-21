package app.controller;

import app.model.OrderLine;
import java.util.ArrayList;
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
    private List<OrderLine> orderLines;

    /**
     * Default constructor that initializes the order line list.
     */
    public OrderLineController() {
        this.orderLines = new ArrayList<>();
    }

    /**
     * Creates and adds a new order line to the system.
     *
     * @param orderLine the OrderLine object to be created
     * @return the created OrderLine object
     */
    public OrderLine createOrderLine(OrderLine orderLine) {
        if (orderLine != null) {
            orderLines.add(orderLine);
        }
        return orderLine;
    }

    /**
     * Retrieves all order lines from the system.
     *
     * @return a list of all OrderLine objects
     */
    public List<OrderLine> getAllOrderLines() {
        return new ArrayList<>(orderLines);
    }

    /**
     * Retrieves an order line by its unique identifier.
     *
     * @param orderLineId the unique identifier of the order line
     * @return an Optional containing the OrderLine if found, or empty if not found
     */
    public Optional<OrderLine> getOrderLineById(Integer orderLineId) {
        return orderLines.stream()
                .filter(orderLine -> orderLine.getOrderLineId().equals(orderLineId))
                .findFirst();
    }

    /**
     * Updates an existing order line in the system.
     *
     * @param orderLineId the unique identifier of the order line to update
     * @param updatedOrderLine the OrderLine object with updated information
     * @return true if the order line was updated successfully, false otherwise
     */
    public boolean updateOrderLine(Integer orderLineId, OrderLine updatedOrderLine) {
        Optional<OrderLine> existingOrderLine = getOrderLineById(orderLineId);
        if (existingOrderLine.isPresent()) {
            int index = orderLines.indexOf(existingOrderLine.get());
            orderLines.set(index, updatedOrderLine);
            return true;
        }
        return false;
    }

    /**
     * Deletes an order line from the system.
     *
     * @param orderLineId the unique identifier of the order line to delete
     * @return true if the order line was deleted successfully, false otherwise
     */
    public boolean deleteOrderLine(Integer orderLineId) {
        return orderLines.removeIf(orderLine -> orderLine.getOrderLineId().equals(orderLineId));
    }
}

