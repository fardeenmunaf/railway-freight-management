package app.db.dao.locomotivetype;

import app.model.LocomotiveType;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface DAO para operações CRUD de LocomotiveType.
 * Define os métodos para manipulação de dados de LocomotiveType na base de dados.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface LocomotiveTypeDAO {

    /**
     * Cria um novo registo de LocomotiveType na base de dados.
     *
     * @param locomotiveType o objeto LocomotiveType a ser criado
     * @throws SQLException se ocorrer erro na operação
     */
    void create(LocomotiveType locomotiveType) throws SQLException;

    /**
     * Recupera um LocomotiveType com base no seu identificador único.
     *
     * @param id o identificador do LocomotiveType
     * @return o LocomotiveType encontrado ou null se não existir
     * @throws SQLException se ocorrer erro na operação
     */
    LocomotiveType get(int id) throws SQLException;

    /**
     * Recupera todos os LocomotiveTypes armazenados na base de dados.
     *
     * @return lista com todos os LocomotiveTypes
     * @throws SQLException se ocorrer erro na operação
     */
    List<LocomotiveType> getAll() throws SQLException;

    /**
     * Atualiza os dados de um LocomotiveType existente na base de dados.
     *
     * @param locomotiveType o objeto LocomotiveType com os dados atualizados
     * @throws SQLException se ocorrer erro na operação
     */
    void update(LocomotiveType locomotiveType) throws SQLException;

    /**
     * Remove um LocomotiveType da base de dados com base no seu identificador único.
     *
     * @param id o identificador do LocomotiveType a remover
     * @throws SQLException se ocorrer erro na operação
     */
    void delete(int id) throws SQLException;

    /**
     * Recupera um LocomotiveType pelo nome.
     *
     * @param name o nome do LocomotiveType
     * @return o LocomotiveType encontrado ou null se não existir
     * @throws SQLException se ocorrer erro na operação
     */
    LocomotiveType getByName(String name) throws SQLException;
}

