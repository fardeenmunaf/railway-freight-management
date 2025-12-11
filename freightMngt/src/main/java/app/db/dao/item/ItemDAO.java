package app.db.dao.item;

import app.model.Item;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface DAO para operações CRUD de Item.
 * Define os métodos para manipulação de dados de Item na base de dados.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface ItemDAO {

    /**
     * Cria um novo registo de Item na base de dados.
     *
     * @param item o objeto Item a ser criado
     * @throws SQLException se ocorrer erro na operação
     */
    void create(Item item) throws SQLException;

    /**
     * Recupera um Item com base no seu identificador único.
     *
     * @param id o identificador do Item
     * @return o Item encontrado ou null se não existir
     * @throws SQLException se ocorrer erro na operação
     */
    Item get(int id) throws SQLException;

    /**
     * Recupera todos os Items armazenados na base de dados.
     *
     * @return lista com todos os Items
     * @throws SQLException se ocorrer erro na operação
     */
    List<Item> getAll() throws SQLException;

    /**
     * Atualiza os dados de um Item existente na base de dados.
     *
     * @param item o objeto Item com os dados atualizados
     * @throws SQLException se ocorrer erro na operação
     */
    void update(Item item) throws SQLException;

    /**
     * Remove um Item da base de dados com base no seu identificador único.
     *
     * @param id o identificador do Item a remover
     * @throws SQLException se ocorrer erro na operação
     */
    void delete(int id) throws SQLException;

    /**
     * Recupera um Item pelo SKU.
     *
     * @param sku o SKU do Item
     * @return o Item encontrado ou null se não existir
     * @throws SQLException se ocorrer erro na operação
     */
    Item getBySku(String sku) throws SQLException;

    /**
     * Recupera Items por categoria.
     *
     * @param categoryId o identificador da categoria
     * @return lista com os Items da categoria
     * @throws SQLException se ocorrer erro na operação
     */
    List<Item> getByCategory(int categoryId) throws SQLException;
}


