package app.db.dao.warehousebay;
import app.model.WarehouseBay;
import java.sql.SQLException;
import java.util.List;

public interface WarehouseBayDAO {
    void create(WarehouseBay bay) throws SQLException;
    WarehouseBay get(int id) throws SQLException;
    List<WarehouseBay> getAll() throws SQLException;
    void update(WarehouseBay bay) throws SQLException;
    void delete(int id) throws SQLException;
    List<WarehouseBay> getByAisle(int aisleId) throws SQLException;
}

