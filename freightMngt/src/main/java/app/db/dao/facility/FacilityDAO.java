package app.db.dao.facility;

import app.model.Facility;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface DAO para operações CRUD de Facility.
 * Define os métodos para manipulação de dados de Facility na base de dados.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface FacilityDAO {

    /**
     * Cria um novo registo de Facility na base de dados.
     *
     * @param facility o objeto Facility a ser criado
     * @throws SQLException se ocorrer erro na operação
     */
    void create(Facility facility) throws SQLException;

    /**
     * Recupera uma Facility com base no seu identificador único.
     *
     * @param id o identificador da Facility
     * @return a Facility encontrada ou null se não existir
     * @throws SQLException se ocorrer erro na operação
     */
    Facility get(int id) throws SQLException;

    /**
     * Recupera todas as Facilities armazenadas na base de dados.
     *
     * @return lista com todas as Facilities
     * @throws SQLException se ocorrer erro na operação
     */
    List<Facility> getAll() throws SQLException;

    /**
     * Atualiza os dados de uma Facility existente na base de dados.
     *
     * @param facility o objeto Facility com os dados atualizados
     * @throws SQLException se ocorrer erro na operação
     */
    void update(Facility facility) throws SQLException;

    /**
     * Remove uma Facility da base de dados com base no seu identificador único.
     *
     * @param id o identificador da Facility a remover
     * @throws SQLException se ocorrer erro na operação
     */
    void delete(int id) throws SQLException;

    /**
     * Recupera Facilities intermodais.
     *
     * @param isIntermodal true para intermodais, false caso contrário
     * @return lista com as Facilities
     * @throws SQLException se ocorrer erro na operação
     */
    List<Facility> getByIntermodal(boolean isIntermodal) throws SQLException;
}


