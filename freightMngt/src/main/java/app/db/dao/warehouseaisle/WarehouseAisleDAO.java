package app.db.dao.warehouseaisle;
}
    List<WarehouseAisle> getByWarehouse(int warehouseId) throws SQLException;
    void delete(int id) throws SQLException;
    void update(WarehouseAisle aisle) throws SQLException;
    List<WarehouseAisle> getAll() throws SQLException;
    WarehouseAisle get(int id) throws SQLException;
    void create(WarehouseAisle aisle) throws SQLException;
public interface WarehouseAisleDAO {

import java.util.List;
import java.sql.SQLException;
import app.model.WarehouseAisle;

