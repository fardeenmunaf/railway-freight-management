package app.db.dao.order;

import app.model.Order;
import java.sql.SQLException;
import java.util.List;

public interface OrderDAO {

    void create(Order order) throws SQLException;

    Order get(int id) throws SQLException;

    List<Order> getAll() throws SQLException;

    void update(Order order) throws SQLException;

    void delete(int id) throws SQLException;

    List<Order> getByStatus(String status) throws SQLException;

    List<Order> getByPriority(int priority) throws SQLException;
}

