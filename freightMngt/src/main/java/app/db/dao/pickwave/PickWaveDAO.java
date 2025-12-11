package app.db.dao.pickwave;
import app.model.PickWave;
import java.sql.SQLException;
import java.util.List;

public interface PickWaveDAO {
    void create(PickWave pickWave) throws SQLException;
    PickWave get(int id) throws SQLException;
    List<PickWave> getAll() throws SQLException;
    void update(PickWave pickWave) throws SQLException;
    void delete(int id) throws SQLException;
}

