package app.db.dao.locomotive;
}
    List<Locomotive> getByType(int locomotiveTypeId) throws SQLException;

    List<Locomotive> getByOperator(int operatorId) throws SQLException;

    void delete(int id) throws SQLException;

    void update(Locomotive locomotive) throws SQLException;

    List<Locomotive> getAll() throws SQLException;

    Locomotive get(int id) throws SQLException;

    void create(Locomotive locomotive) throws SQLException;

public interface LocomotiveDAO {
 */
 * @version 1.0
 * @author G113 2DK
 *
 * Define os métodos para manipulação de dados de Locomotive na base de dados.
 * Interface DAO para operações CRUD de Locomotive.
/**

import java.util.List;
import java.sql.SQLException;
import app.model.Locomotive;


