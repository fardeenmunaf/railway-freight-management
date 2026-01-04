package app.db.dao.cart;

import app.model.Cart;
import java.sql.SQLException;
import java.util.List;

public interface CartDAO {
    void create(Cart cart) throws SQLException;
    Cart get(int id) throws SQLException;
    List<Cart> getAll() throws SQLException;
    void update(Cart cart) throws SQLException;
    void delete(int id) throws SQLException;
    Cart getByName(String name) throws SQLException;
}
