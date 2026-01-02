package app.db.dao.facilitytype;

import app.model.FacilityType;
import java.sql.SQLException;
import java.util.List;

public interface FacilityTypeDAO {
    void create(FacilityType facilityType) throws SQLException;
    FacilityType get(int id) throws SQLException;
    List<FacilityType> getAll() throws SQLException;
    void update(FacilityType facilityType) throws SQLException;
    void delete(int id) throws SQLException;
    FacilityType getByName(String name) throws SQLException;
}

