package app.db.dao.picktask;
import app.model.PickTask;
import java.sql.SQLException;
import java.util.List;

public interface PickTaskDAO {
    void create(PickTask pickTask) throws SQLException;
    PickTask get(int id) throws SQLException;
    List<PickTask> getAll() throws SQLException;
    void update(PickTask pickTask) throws SQLException;
    void delete(int id) throws SQLException;
}

