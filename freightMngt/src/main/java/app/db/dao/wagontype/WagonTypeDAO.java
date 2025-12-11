package app.db.dao.wagontype;

import app.model.WagonType;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface DAO para operações CRUD de WagonType.
 * Define os métodos para manipulação de dados de WagonType na base de dados.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface WagonTypeDAO {

    /**
     * Cria um novo registo de WagonType na base de dados.
     *
     * @param wagonType o objeto WagonType a ser criado
     * @throws SQLException se ocorrer erro na operação
     */
    void create(WagonType wagonType) throws SQLException;

    /**
     * Recupera um WagonType com base no seu identificador único.
     *
     * @param id o identificador do WagonType
     * @return o WagonType encontrado ou null se não existir
     * @throws SQLException se ocorrer erro na operação
     */
    WagonType get(int id) throws SQLException;

    /**
     * Recupera todos os WagonTypes armazenados na base de dados.
     *
     * @return lista com todos os WagonTypes
     * @throws SQLException se ocorrer erro na operação
     */
    List<WagonType> getAll() throws SQLException;

    /**
     * Atualiza os dados de um WagonType existente na base de dados.
     *
     * @param wagonType o objeto WagonType com os dados atualizados
     * @throws SQLException se ocorrer erro na operação
     */
    void update(WagonType wagonType) throws SQLException;

    /**
     * Remove um WagonType da base de dados com base no seu identificador único.
     *
     * @param id o identificador do WagonType a remover
     * @throws SQLException se ocorrer erro na operação
     */
    void delete(int id) throws SQLException;

    /**
     * Recupera um WagonType pelo nome.
     *
     * @param name o nome do WagonType
     * @return o WagonType encontrado ou null se não existir
     * @throws SQLException se ocorrer erro na operação
     */
    WagonType getByName(String name) throws SQLException;
}

