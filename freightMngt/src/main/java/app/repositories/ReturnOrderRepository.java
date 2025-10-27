package app.repositories;

import app.model.ReturnOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing return orders in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class ReturnOrderRepository {

    private final List<ReturnOrder> returnOrders = new ArrayList<>();

    public void addReturnOrder(ReturnOrder returnOrder) {
        if (returnOrder != null && returnOrder.getReturnId() != null) {
            if (!containsReturnOrder(returnOrder.getReturnId())) {
                returnOrders.add(returnOrder);
            }
        }
    }

    public List<ReturnOrder> getAllReturnOrders() {
        return new ArrayList<>(returnOrders);
    }

    public ReturnOrder getReturnOrderById(Integer returnId) {
        for (ReturnOrder returnOrder : returnOrders) {
            if (returnId.equals(returnOrder.getReturnId())) {
                return returnOrder;
            }
        }
        return null;
    }

    public boolean updateReturnOrder(Integer returnId, ReturnOrder updatedReturnOrder) {
        ReturnOrder existingReturnOrder = getReturnOrderById(returnId);
        if (existingReturnOrder != null) {
            int index = returnOrders.indexOf(existingReturnOrder);
            returnOrders.set(index, updatedReturnOrder);
            return true;
        }
        return false;
    }

    public boolean deleteReturnOrder(Integer returnId) {
        return returnOrders.removeIf(returnOrder -> returnId.equals(returnOrder.getReturnId()));
    }

    public Integer getNextReturnOrderId() {
        int maxId = 0;
        for (ReturnOrder returnOrder : returnOrders) {
            if (returnOrder.getReturnId() != null && returnOrder.getReturnId() > maxId) {
                maxId = returnOrder.getReturnId();
            }
        }
        return maxId + 1;
    }

    private boolean containsReturnOrder(Integer returnId) {
        return getReturnOrderById(returnId) != null;
    }

    public void clear() {
        returnOrders.clear();
    }
}
