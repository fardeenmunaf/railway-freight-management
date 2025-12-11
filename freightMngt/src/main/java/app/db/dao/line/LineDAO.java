package app.db.dao.line;

import app.model.Line;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface DAO para operações CRUD de Line.
 * Define os métodos para manipulação de dados de Line na base de dados.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface LineDAO {

    /**
     * Cria um novo registo de Line na base de dados.
     *
     * @param line o objeto Line a ser criado
     * @throws SQLException se ocorrer erro na operação
     */
    void create(Line line) throws SQLException;

    /**
     * Recupera uma Line com base no seu identificador único.
     *
     * @param id o identificador da Line
     * @return a Line encontrada ou null se não existir
     * @throws SQLException se ocorrer erro na operação
     */
    Line get(int id) throws SQLException;

    /**
     * Recupera todas as Lines armazenadas na base de dados.
     *
     * @return lista com todas as Lines
     * @throws SQLException se ocorrer erro na operação
     */
    List<Line> getAll() throws SQLException;

    /**
     * Atualiza os dados de uma Line existente na base de dados.
     *
     * @param line o objeto Line com os dados atualizados
     * @throws SQLException se ocorrer erro na operação
     */
    void update(Line line) throws SQLException;

    /**
     * Remove uma Line da base de dados com base no seu identificador único.
     *
     * @param id o identificador da Line a remover
     * @throws SQLException se ocorrer erro na operação
     */
    void delete(int id) throws SQLException;

    /**
     * Recupera Lines por proprietário (Company).
     *
     * @param ownerId o identificador do proprietário (Company)
     * @return lista com as Lines do proprietário
     * @throws SQLException se ocorrer erro na operação
     */
    List<Line> getByOwner(int ownerId) throws SQLException;
}

