package app.db.dao.user;

import app.model.User;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface DAO para operações CRUD de User.
 * Define os métodos para manipulação de dados de User na base de dados.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface UserDAO {

    /**
     * Cria um novo registo de User na base de dados.
     *
     * @param user o objeto User a ser criado
     * @throws SQLException se ocorrer erro na operação
     */
    void create(User user) throws SQLException;

    /**
     * Recupera um User com base no seu identificador único.
     *
     * @param id o identificador do User
     * @return o User encontrado ou null se não existir
     * @throws SQLException se ocorrer erro na operação
     */
    User get(int id) throws SQLException;

    /**
     * Recupera todos os Users armazenados na base de dados.
     *
     * @return lista com todos os Users
     * @throws SQLException se ocorrer erro na operação
     */
    List<User> getAll() throws SQLException;

    /**
     * Atualiza os dados de um User existente na base de dados.
     *
     * @param user o objeto User com os dados atualizados
     * @throws SQLException se ocorrer erro na operação
     */
    void update(User user) throws SQLException;

    /**
     * Remove um User da base de dados com base no seu identificador único.
     *
     * @param id o identificador do User a remover
     * @throws SQLException se ocorrer erro na operação
     */
    void delete(int id) throws SQLException;

    /**
     * Recupera um User pelo username.
     *
     * @param username o username do User
     * @return o User encontrado ou null se não existir
     * @throws SQLException se ocorrer erro na operação
     */
    User getByUsername(String username) throws SQLException;

    /**
     * Recupera um User pelo email.
     *
     * @param email o email do User
     * @return o User encontrado ou null se não existir
     * @throws SQLException se ocorrer erro na operação
     */
    User getByEmail(String email) throws SQLException;

    /**
     * Recupera Users por empresa.
     *
     * @param companyId o identificador da empresa
     * @return lista com os Users da empresa
     * @throws SQLException se ocorrer erro na operação
     */
    List<User> getByCompany(int companyId) throws SQLException;
}

