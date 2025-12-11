package app.db.dao.station;
}
    List<Station> getMainStations() throws SQLException;
    List<Station> getByCountry(String country) throws SQLException;
    void delete(int id) throws SQLException;
    void update(Station station) throws SQLException;
    List<Station> getAll() throws SQLException;
    Station get(int id) throws SQLException;
    void create(Station station) throws SQLException;
public interface StationDAO {

import java.util.List;
import java.sql.SQLException;
import app.model.Station;


