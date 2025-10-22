package app.repositories;

import app.model.OrderLine;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing order lines in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class OrderLineRepository {

    private final List<OrderLine> orderLines = new ArrayList<>();

    public void addOrderLine(OrderLine orderLine) {
        if (orderLine != null && orderLine.getOrderLineId() != null) {
            if (!containsOrderLine(orderLine.getOrderLineId())) {
                orderLines.add(orderLine);
            }
        }
    }

    public List<OrderLine> getAllOrderLines() {
        return new ArrayList<>(orderLines);
    }

    public OrderLine getOrderLineById(Integer orderLineId) {
        for (OrderLine orderLine : orderLines) {
            if (orderLineId.equals(orderLine.getOrderLineId())) {
                return orderLine;
            }
        }
        return null;
    }

    public List<OrderLine> getOrderLinesByOrderId(Integer orderId) {
        List<OrderLine> result = new ArrayList<>();
        for (OrderLine orderLine : orderLines) {
            if (orderId != null && orderLine.getOrder() != null && orderId.equals(orderLine.getOrder().getOrderId())) {
                result.add(orderLine);
            }
        }
        return result;
    }

    public boolean updateOrderLine(Integer orderLineId, OrderLine updatedOrderLine) {
        OrderLine existingOrderLine = getOrderLineById(orderLineId);
        if (existingOrderLine != null) {
            int index = orderLines.indexOf(existingOrderLine);
            orderLines.set(index, updatedOrderLine);
            return true;
        }
        return false;
    }

    public boolean deleteOrderLine(Integer orderLineId) {
        return orderLines.removeIf(orderLine -> orderLineId.equals(orderLine.getOrderLineId()));
    }

    public Integer getNextOrderLineId() {
        int maxId = 0;
        for (OrderLine orderLine : orderLines) {
            if (orderLine.getOrderLineId() != null && orderLine.getOrderLineId() > maxId) {
                maxId = orderLine.getOrderLineId();
            }
        }
        return maxId + 1;
    }

    private boolean containsOrderLine(Integer orderLineId) {
        return getOrderLineById(orderLineId) != null;
    }

    public void clear() {
        orderLines.clear();
    }
}
