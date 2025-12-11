package app.db.dao.cart;
}
    void delete(int id) throws SQLException;
    void update(Cart cart) throws SQLException;
    List<Cart> getAll() throws SQLException;
    Cart get(int id) throws SQLException;
    void create(Cart cart) throws SQLException;
public interface CartDAO {

import java.util.List;
import java.sql.SQLException;
import app.model.Cart;

