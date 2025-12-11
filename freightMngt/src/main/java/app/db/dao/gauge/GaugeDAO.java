package app.db.dao.gauge;
}
    Gauge getByName(String name) throws SQLException;
     */
     * @throws SQLException se ocorrer erro na operação
     * @return o Gauge encontrado ou null se não existir
     * @param name o nome do Gauge
     *
     * Recupera um Gauge pelo nome.
    /**

    void delete(int id) throws SQLException;
     */
     * @throws SQLException se ocorrer erro na operação
     * @param id o identificador do Gauge a remover
     *
     * Remove um Gauge da base de dados com base no seu identificador único.
    /**

    void update(Gauge gauge) throws SQLException;
     */
     * @throws SQLException se ocorrer erro na operação
     * @param gauge o objeto Gauge com os dados atualizados
     *
     * Atualiza os dados de um Gauge existente na base de dados.
    /**

    List<Gauge> getAll() throws SQLException;
     */
     * @throws SQLException se ocorrer erro na operação
     * @return lista com todos os Gauges
     *
     * Recupera todos os Gauges armazenados na base de dados.
    /**

    Gauge get(int id) throws SQLException;
     */
     * @throws SQLException se ocorrer erro na operação
     * @return o Gauge encontrado ou null se não existir
     * @param id o identificador do Gauge
     *
     * Recupera um Gauge com base no seu identificador único.
    /**

    void create(Gauge gauge) throws SQLException;
     */
     * @throws SQLException se ocorrer erro na operação
     * @param gauge o objeto Gauge a ser criado
     *
     * Cria um novo registo de Gauge na base de dados.
    /**

public interface GaugeDAO {
 */
 * @version 1.0
 * @author G113 2DK
 *
 * Define os métodos para manipulação de dados de Gauge na base de dados.
 * Interface DAO para operações CRUD de Gauge.
/**

import app.model.Gauge;
import java.util.List;
import java.sql.SQLException;


