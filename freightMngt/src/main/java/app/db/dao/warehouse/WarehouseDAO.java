package app.db.dao.warehouse;
}
    Warehouse getByName(String name) throws SQLException;

    Warehouse getByFacility(int facilityId) throws SQLException;

    void delete(int id) throws SQLException;

    void update(Warehouse warehouse) throws SQLException;

    List<Warehouse> getAll() throws SQLException;

    Warehouse get(int id) throws SQLException;

    void create(Warehouse warehouse) throws SQLException;

public interface WarehouseDAO {

import java.util.List;
import java.sql.SQLException;
import app.model.Warehouse;


