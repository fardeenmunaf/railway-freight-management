package app.db.dao.wagonmodel;

import app.model.WagonModel;
import java.sql.SQLException;
import java.util.List;

public interface WagonModelDAO {

    void create(WagonModel wagonModel) throws SQLException;

    WagonModel get(int id) throws SQLException;

    List<WagonModel> getAll() throws SQLException;

    void update(WagonModel wagonModel) throws SQLException;

    void delete(int id) throws SQLException;

    List<WagonModel> getByType(int wagonTypeId) throws SQLException;

    WagonModel getByName(String name) throws SQLException;
}

