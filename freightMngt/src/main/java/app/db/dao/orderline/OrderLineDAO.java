package app.db.dao.orderline;

import app.model.OrderLine;
import java.sql.SQLException;
import java.util.List;

public interface OrderLineDAO {

    void create(OrderLine orderLine) throws SQLException;

    OrderLine get(int id) throws SQLException;

    List<OrderLine> getAll() throws SQLException;

    void update(OrderLine orderLine) throws SQLException;

    void delete(int id) throws SQLException;

    List<OrderLine> getByOrder(int orderId) throws SQLException;

    List<OrderLine> getBySku(String sku) throws SQLException;
}

