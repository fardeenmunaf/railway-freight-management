package app.db.dao.itemcategory;
}
    ItemCategory getByName(String name) throws SQLException;
     */
     * @throws SQLException se ocorrer erro na operação
     * @return o ItemCategory encontrado ou null se não existir
     * @param name o nome do ItemCategory
     *
     * Recupera um ItemCategory pelo nome.
    /**

    void delete(int id) throws SQLException;
     */
     * @throws SQLException se ocorrer erro na operação
     * @param id o identificador do ItemCategory a remover
     *
     * Remove um ItemCategory da base de dados com base no seu identificador único.
    /**

    void update(ItemCategory itemCategory) throws SQLException;
     */
     * @throws SQLException se ocorrer erro na operação
     * @param itemCategory o objeto ItemCategory com os dados atualizados
     *
     * Atualiza os dados de um ItemCategory existente na base de dados.
    /**

    List<ItemCategory> getAll() throws SQLException;
     */
     * @throws SQLException se ocorrer erro na operação
     * @return lista com todos os ItemCategories
     *
     * Recupera todos os ItemCategories armazenados na base de dados.
    /**

    ItemCategory get(int id) throws SQLException;
     */
     * @throws SQLException se ocorrer erro na operação
     * @return o ItemCategory encontrado ou null se não existir
     * @param id o identificador do ItemCategory
     *
     * Recupera um ItemCategory com base no seu identificador único.
    /**

    void create(ItemCategory itemCategory) throws SQLException;
     */
     * @throws SQLException se ocorrer erro na operação
     * @param itemCategory o objeto ItemCategory a ser criado
     *
     * Cria um novo registo de ItemCategory na base de dados.
    /**

public interface ItemCategoryDAO {
 */
 * @version 1.0
 * @author G113 2DK
 *
 * Define os métodos para manipulação de dados de ItemCategory na base de dados.
 * Interface DAO para operações CRUD de ItemCategory.
/**

import java.util.List;
import java.sql.SQLException;
import app.model.ItemCategory;


