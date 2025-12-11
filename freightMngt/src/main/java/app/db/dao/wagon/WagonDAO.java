package app.db.dao.wagon;

import app.model.Wagon;
import java.sql.SQLException;
import java.util.List;

public interface WagonDAO {

    void create(Wagon wagon) throws SQLException;

    Wagon get(int id) throws SQLException;

    List<Wagon> getAll() throws SQLException;

    void update(Wagon wagon) throws SQLException;

    void delete(int id) throws SQLException;

    List<Wagon> getByOperator(int operatorId) throws SQLException;

    List<Wagon> getByModel(int modelId) throws SQLException;
}

