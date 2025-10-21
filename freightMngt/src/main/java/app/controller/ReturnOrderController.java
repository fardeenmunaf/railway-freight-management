package app.controller;

import app.model.ReturnOrder;
import java.util.ArrayList;
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
    private List<ReturnOrder> returnOrders;

    /**
     * Default constructor that initializes the return order list.
     */
    public ReturnOrderController() {
        this.returnOrders = new ArrayList<>();
    }

    /**
     * Creates and adds a new return order to the system.
     *
     * @param returnOrder the ReturnOrder object to be created
     * @return the created ReturnOrder object
     */
    public ReturnOrder createReturnOrder(ReturnOrder returnOrder) {
        if (returnOrder != null) {
            returnOrders.add(returnOrder);
        }
        return returnOrder;
    }

    /**
     * Retrieves all return orders from the system.
     *
     * @return a list of all ReturnOrder objects
     */
    public List<ReturnOrder> getAllReturnOrders() {
        return new ArrayList<>(returnOrders);
    }

    /**
     * Retrieves a return order by its unique identifier.
     *
     * @param returnOrderId the unique identifier of the return order
     * @return an Optional containing the ReturnOrder if found, or empty if not found
     */
    public Optional<ReturnOrder> getReturnOrderById(Integer returnOrderId) {
        return returnOrders.stream()
                .filter(returnOrder -> returnOrder.getReturnOrderId().equals(returnOrderId))
                .findFirst();
    }

    /**
     * Updates an existing return order in the system.
     *
     * @param returnOrderId the unique identifier of the return order to update
     * @param updatedReturnOrder the ReturnOrder object with updated information
     * @return true if the return order was updated successfully, false otherwise
     */
    public boolean updateReturnOrder(Integer returnOrderId, ReturnOrder updatedReturnOrder) {
        Optional<ReturnOrder> existingReturnOrder = getReturnOrderById(returnOrderId);
        if (existingReturnOrder.isPresent()) {
            int index = returnOrders.indexOf(existingReturnOrder.get());
            returnOrders.set(index, updatedReturnOrder);
            return true;
        }
        return false;
    }

    /**
     * Deletes a return order from the system.
     *
     * @param returnOrderId the unique identifier of the return order to delete
     * @return true if the return order was deleted successfully, false otherwise
     */
    public boolean deleteReturnOrder(Integer returnOrderId) {
        return returnOrders.removeIf(returnOrder -> returnOrder.getReturnOrderId().equals(returnOrderId));
    }
}

