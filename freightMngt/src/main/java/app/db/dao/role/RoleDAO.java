package app.db.dao.role;

import app.model.Role;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface DAO para operações CRUD de Role.
 * Define os métodos para manipulação de dados de Role na base de dados.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface RoleDAO {

    /**
     * Cria um novo registo de Role na base de dados.
     *
     * @param role o objeto Role a ser criado
     * @throws SQLException se ocorrer erro na operação
     */
    void create(Role role) throws SQLException;

    /**
     * Recupera um Role com base no seu identificador único.
     *
     * @param id o identificador do Role
     * @return o Role encontrado ou null se não existir
     * @throws SQLException se ocorrer erro na operação
     */
    Role get(int id) throws SQLException;

    /**
     * Recupera todos os Roles armazenados na base de dados.
     *
     * @return lista com todos os Roles
     * @throws SQLException se ocorrer erro na operação
     */
    List<Role> getAll() throws SQLException;

    /**
     * Atualiza os dados de um Role existente na base de dados.
     *
     * @param role o objeto Role com os dados atualizados
     * @throws SQLException se ocorrer erro na operação
     */
    void update(Role role) throws SQLException;

    /**
     * Remove um Role da base de dados com base no seu identificador único.
     *
     * @param id o identificador do Role a remover
     * @throws SQLException se ocorrer erro na operação
     */
    void delete(int id) throws SQLException;

    /**
     * Recupera um Role pelo nome.
     *
     * @param name o nome do Role
     * @return o Role encontrado ou null se não existir
     * @throws SQLException se ocorrer erro na operação
     */
    Role getByName(String name) throws SQLException;
}

