package app.db.dao.locomotivetype;
}
    }
        );
                rs.getString("NAME")
                rs.getInt("LOCOMOTIVE_TYPE_ID"),
        return new LocomotiveType(
    private LocomotiveType mapResultSetToLocomotiveType(ResultSet rs) throws SQLException {

    }
        return 1;

        }
            }
                return rs.getInt("NEXT_ID");
            if (rs.next()) {

             ResultSet rs = stmt.executeQuery(sql)) {
        try (Statement stmt = connection.createStatement();

        String sql = "SELECT NVL(MAX(LOCOMOTIVE_TYPE_ID), 0) + 1 AS NEXT_ID FROM LOCOMOTIVE_TYPE";
    private int getNextId() throws SQLException {

    }
        return null;

        }
            }
                }
                    return mapResultSetToLocomotiveType(rs);
                if (rs.next()) {
            try (ResultSet rs = pstmt.executeQuery()) {

            pstmt.setString(1, name);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

        String sql = "SELECT LOCOMOTIVE_TYPE_ID, NAME FROM LOCOMOTIVE_TYPE WHERE NAME = ?";
    public LocomotiveType getByName(String name) throws SQLException {
    @Override

    }
        }
            pstmt.executeUpdate();
            pstmt.setInt(1, id);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

        String sql = "DELETE FROM LOCOMOTIVE_TYPE WHERE LOCOMOTIVE_TYPE_ID = ?";
    public void delete(int id) throws SQLException {
    @Override

    }
        }
            pstmt.executeUpdate();

            pstmt.setInt(2, locomotiveType.getLocomotiveTypeId());
            pstmt.setString(1, locomotiveType.getName());
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

        String sql = "UPDATE LOCOMOTIVE_TYPE SET NAME = ?, UPDATED_AT = CURRENT_TIMESTAMP WHERE LOCOMOTIVE_TYPE_ID = ?";
    public void update(LocomotiveType locomotiveType) throws SQLException {
    @Override

    }
        return types;

        }
            }
                types.add(mapResultSetToLocomotiveType(rs));
            while (rs.next()) {

             ResultSet rs = stmt.executeQuery(sql)) {
        try (Statement stmt = connection.createStatement();

        String sql = "SELECT LOCOMOTIVE_TYPE_ID, NAME FROM LOCOMOTIVE_TYPE ORDER BY LOCOMOTIVE_TYPE_ID";
        List<LocomotiveType> types = new ArrayList<>();
    public List<LocomotiveType> getAll() throws SQLException {
    @Override

    }
        return null;

        }
            }
                }
                    return mapResultSetToLocomotiveType(rs);
                if (rs.next()) {
            try (ResultSet rs = pstmt.executeQuery()) {

            pstmt.setInt(1, id);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

        String sql = "SELECT LOCOMOTIVE_TYPE_ID, NAME FROM LOCOMOTIVE_TYPE WHERE LOCOMOTIVE_TYPE_ID = ?";
    public LocomotiveType get(int id) throws SQLException {
    @Override

    }
        }
            pstmt.executeUpdate();

            pstmt.setString(2, locomotiveType.getName());
            pstmt.setInt(1, locomotiveType.getLocomotiveTypeId());

            }
                locomotiveType.setLocomotiveTypeId(getNextId());
            if (locomotiveType.getLocomotiveTypeId() == null) {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

        String sql = "INSERT INTO LOCOMOTIVE_TYPE (LOCOMOTIVE_TYPE_ID, NAME, CREATED_AT) VALUES (?, ?, CURRENT_TIMESTAMP)";
    public void create(LocomotiveType locomotiveType) throws SQLException {
    @Override

    }
        this.connection = DatabaseConnection.getInstance().getConnection();
    public LocomotiveTypeDAOImpl() {

    private final Connection connection;

public class LocomotiveTypeDAOImpl implements LocomotiveTypeDAO {
 */
 * @version 1.0
 * @author G113 2DK
 *
 * Implementa operações CRUD para a entidade LocomotiveType.
 * Implementação DAO para gestão de LocomotiveType na base de dados Oracle.
/**

import java.util.List;
import java.util.ArrayList;
import java.sql.*;

import app.model.LocomotiveType;
import app.db.DatabaseConnection;


