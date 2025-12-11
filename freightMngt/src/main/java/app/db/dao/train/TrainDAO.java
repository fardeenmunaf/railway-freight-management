package app.db.dao.train;

import java.sql.SQLException;
import java.util.List;
import app.model.Train;

/**
 * Interface DAO para operações CRUD de Train.
 * Define os métodos para manipulação de dados de Train na base de dados.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface TrainDAO {

    /**
     * Cria um novo registo de Train na base de dados.
     *
     * @param train o objeto Train a ser criado
     * @throws SQLException se ocorrer erro na operação
     */
    void create(Train train) throws SQLException;

    /**
     * Recupera um Train com base no seu identificador único.
     *
     * @param id o identificador do Train
     * @return o Train encontrado ou null se não existir
     * @throws SQLException se ocorrer erro na operação
     */
    Train get(int id) throws SQLException;

    /**
     * Recupera todos os Trains armazenados na base de dados.
     *
     * @return lista com todos os Trains
     * @throws SQLException se ocorrer erro na operação
     */
    List<Train> getAll() throws SQLException;

    /**
     * Atualiza os dados de um Train existente na base de dados.
     *
     * @param train o objeto Train com os dados atualizados
     * @throws SQLException se ocorrer erro na operação
     */
    void update(Train train) throws SQLException;

    /**
     * Remove um Train da base de dados com base no seu identificador único.
     *
     * @param id o identificador do Train a remover
     * @throws SQLException se ocorrer erro na operação
     */
    void delete(int id) throws SQLException;

    /**
     * Recupera Trains por operador (Company).
     *
     * @param companyId o identificador da Company operadora
     * @return lista com os Trains da Company
     * @throws SQLException se ocorrer erro na operação
     */
    List<Train> getByOperator(int companyId) throws SQLException;
}

