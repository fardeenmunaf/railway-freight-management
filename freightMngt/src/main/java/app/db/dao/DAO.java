package app.db.dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface genérica para operações CRUD na base de dados.
 * Define os métodos básicos para manipulação de entidades.
 *
 * @param <T> o tipo da entidade
 * @author G113 2DK
 * @version 1.0
 */
public interface DAO<T> {

    /**
     * Cria um novo registo na base de dados.
     *
     * @param t o objeto a ser criado
     * @throws SQLException se ocorrer erro na operação
     */
    void create(T t) throws SQLException;

    /**
     * Recupera um elemento com base no seu identificador único.
     *
     * @param id o identificador do elemento
     * @return o elemento encontrado ou null se não existir
     * @throws SQLException se ocorrer erro na operação
     */
    T get(int id) throws SQLException;

    /**
     * Recupera todos os elementos armazenados na base de dados.
     *
     * @return lista com todos os elementos
     * @throws SQLException se ocorrer erro na operação
     */
    List<T> getAll() throws SQLException;

    /**
     * Atualiza os dados de um elemento existente na base de dados.
     *
     * @param t o objeto com os dados atualizados
     * @throws SQLException se ocorrer erro na operação
     */
    void update(T t) throws SQLException;

    /**
     * Remove um elemento da base de dados com base no seu identificador único.
     *
     * @param id o identificador do elemento a remover
     * @throws SQLException se ocorrer erro na operação
     */
    void delete(int id) throws SQLException;
}

