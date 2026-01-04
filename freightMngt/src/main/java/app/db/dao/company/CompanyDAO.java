package app.db.dao.company;

import java.sql.SQLException;
import java.util.List;
import app.model.Company;

/**
 * Interface DAO para operacoes CRUD de Company.
 * Define os metodos para manipulacao de dados de Company na base de dados.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface CompanyDAO {

    /**
     * Cria um novo registo de Company na base de dados.
     *
     * @param company o objeto Company a ser criado
     * @throws SQLException se ocorrer erro na operacao
     */
    void create(Company company) throws SQLException;

    /**
     * Recupera uma Company com base no seu identificador unico.
     *
     * @param id o identificador da Company
     * @return a Company encontrada ou null se nao existir
     * @throws SQLException se ocorrer erro na operacao
     */
    Company get(int id) throws SQLException;

    /**
     * Recupera todas as Companies armazenadas na base de dados.
     *
     * @return lista com todas as Companies
     * @throws SQLException se ocorrer erro na operacao
     */
    List<Company> getAll() throws SQLException;

    /**
     * Atualiza os dados de uma Company existente na base de dados.
     *
     * @param company o objeto Company com os dados atualizados
     * @throws SQLException se ocorrer erro na operacao
     */
    void update(Company company) throws SQLException;

    /**
     * Remove uma Company da base de dados com base no seu identificador unico.
     *
     * @param id o identificador da Company a remover
     * @throws SQLException se ocorrer erro na operacao
     */
    void delete(int id) throws SQLException;

    /**
     * Recupera uma Company pelo nome.
     *
     * @param name o nome da Company
     * @return a Company encontrada ou null se nao existir
     * @throws SQLException se ocorrer erro na operacao
     */
    Company getByName(String name) throws SQLException;

    /**
     * Verifica se existe uma Company com o ID especificado.
     *
     * @param id o identificador da Company
     * @return true se existir, false caso contrario
     * @throws SQLException se ocorrer erro na operacao
     */
    boolean exists(int id) throws SQLException;
}


