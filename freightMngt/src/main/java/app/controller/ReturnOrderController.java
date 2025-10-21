package app.controller;

import app.model.ReturnOrder;
import app.repositories.ReturnOrderRepository;
import app.repositories.Repositories;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing ReturnOrder entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for ReturnOrder objects.
 * This controller manages return order data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class ReturnOrderController {
    private final ReturnOrderRepository repository;

    /**
     * Default constructor that initializes the controller with the return order repository.
     */
    public ReturnOrderController() {
        this.repository = Repositories.getInstance().getReturnOrderRepository();
    }

    /**
     * Creates and adds a new return order to the system.
     *
     * @param returnOrder the ReturnOrder object to be created
     * @return the created ReturnOrder object
     */
    public ReturnOrder createReturnOrder(ReturnOrder returnOrder) {
        if (returnOrder != null) {
            repository.addReturnOrder(returnOrder);
        }
        return returnOrder;
    }

    /**
     * Retrieves all return orders from the system.
     *
     * @return a list of all ReturnOrder objects
     */
    public List<ReturnOrder> getAllReturnOrders() {
        return repository.getAllReturnOrders();
    }

    /**
     * Retrieves a return order by its unique identifier.
     *
     * @param returnOrderId the unique identifier of the return order
     * @return an Optional containing the ReturnOrder if found, or empty if not found
     */
    public Optional<ReturnOrder> getReturnOrderById(Integer returnOrderId) {
        ReturnOrder returnOrder = repository.getReturnOrderById(returnOrderId);
        return Optional.ofNullable(returnOrder);
    }

    /**
     * Updates an existing return order in the system.
     *
     * @param returnOrderId the unique identifier of the return order to update
     * @param updatedReturnOrder the ReturnOrder object with updated information
     * @return true if the return order was updated successfully, false otherwise
     */
    public boolean updateReturnOrder(Integer returnOrderId, ReturnOrder updatedReturnOrder) {
        return repository.updateReturnOrder(returnOrderId, updatedReturnOrder);
    }

    /**
     * Deletes a return order from the system.
     *
     * @param returnOrderId the unique identifier of the return order to delete
     * @return true if the return order was deleted successfully, false otherwise
     */
    public boolean deleteReturnOrder(Integer returnOrderId) {
        return repository.deleteReturnOrder(returnOrderId);
    }

    /**
     * Gets the next available return order ID.
     *
     * @return the next return order ID to be used
     */
    public Integer getNextReturnOrderId() {
        return repository.getNextReturnOrderId();
    }

    /**
     * Clears all return orders from the system.
     */
    public void clearAllReturnOrders() {
        repository.clear();
    }
}
