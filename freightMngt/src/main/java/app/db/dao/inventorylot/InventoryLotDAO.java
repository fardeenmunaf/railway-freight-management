package app.db.dao.inventorylot;

import app.model.InventoryLot;
import java.sql.SQLException;
import java.util.List;

public interface InventoryLotDAO {
    void create(InventoryLot lot) throws SQLException;
    InventoryLot get(int id) throws SQLException;
    List<InventoryLot> getAll() throws SQLException;
    void update(InventoryLot lot) throws SQLException;
    void delete(int id) throws SQLException;
    List<InventoryLot> getBySku(String sku) throws SQLException;
}

