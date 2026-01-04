package app.db.dao.inventorybalance;

import app.model.InventoryBalance;
import java.sql.SQLException;
import java.util.List;

public interface InventoryBalanceDAO {
    void create(InventoryBalance balance) throws SQLException;
    InventoryBalance get(int id) throws SQLException;
    List<InventoryBalance> getAll() throws SQLException;
    void update(InventoryBalance balance) throws SQLException;
    void delete(int id) throws SQLException;
    List<InventoryBalance> getByBay(int bayId) throws SQLException;
    List<InventoryBalance> getByLot(int lotId) throws SQLException;
}

