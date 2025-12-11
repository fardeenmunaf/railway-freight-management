package app.db.dao.facilitytype;
}
    FacilityType getByName(String name) throws SQLException;
     */
     * @throws SQLException se ocorrer erro na operação
     * @return o FacilityType encontrado ou null se não existir
     * @param name o nome do FacilityType
     *
     * Recupera um FacilityType pelo nome.
    /**

    void delete(int id) throws SQLException;
     */
     * @throws SQLException se ocorrer erro na operação
     * @param id o identificador do FacilityType a remover
     *
     * Remove um FacilityType da base de dados com base no seu identificador único.
    /**

    void update(FacilityType facilityType) throws SQLException;
     */
     * @throws SQLException se ocorrer erro na operação
     * @param facilityType o objeto FacilityType com os dados atualizados
     *
     * Atualiza os dados de um FacilityType existente na base de dados.
    /**

    List<FacilityType> getAll() throws SQLException;
     */
     * @throws SQLException se ocorrer erro na operação
     * @return lista com todos os FacilityTypes
     *
     * Recupera todos os FacilityTypes armazenados na base de dados.
    /**

    FacilityType get(int id) throws SQLException;
     */
     * @throws SQLException se ocorrer erro na operação
     * @return o FacilityType encontrado ou null se não existir
     * @param id o identificador do FacilityType
     *
     * Recupera um FacilityType com base no seu identificador único.
    /**

    void create(FacilityType facilityType) throws SQLException;
     */
     * @throws SQLException se ocorrer erro na operação
     * @param facilityType o objeto FacilityType a ser criado
     *
     * Cria um novo registo de FacilityType na base de dados.
    /**

public interface FacilityTypeDAO {
 */
 * @version 1.0
 * @author G113 2DK
 *
 * Define os métodos para manipulação de dados de FacilityType na base de dados.
 * Interface DAO para operações CRUD de FacilityType.
/**

import java.util.List;
import java.sql.SQLException;
import app.model.FacilityType;


