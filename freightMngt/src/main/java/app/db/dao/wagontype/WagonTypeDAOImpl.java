package app.db.dao.wagontype;

import app.db.DatabaseConnection;
import app.model.WagonType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação DAO para gestão de WagonType na base de dados Oracle.
 * Implementa operações CRUD para a entidade WagonType.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class WagonTypeDAOImpl implements WagonTypeDAO {

    private final Connection connection;

    public WagonTypeDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(WagonType wagonType) throws SQLException {
        String sql = "INSERT INTO WAGON_TYPE (WAGON_TYPE_ID, NAME, CREATED_AT) VALUES (?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (wagonType.getWagonTypeId() == null) {
                wagonType.setWagonTypeId(getNextId());
            }

            pstmt.setInt(1, wagonType.getWagonTypeId());
            pstmt.setString(2, wagonType.getName());

            pstmt.executeUpdate();
        }
    }

    @Override
    public WagonType get(int id) throws SQLException {
        String sql = "SELECT WAGON_TYPE_ID, NAME FROM WAGON_TYPE WHERE WAGON_TYPE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToWagonType(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<WagonType> getAll() throws SQLException {
        List<WagonType> types = new ArrayList<>();
        String sql = "SELECT WAGON_TYPE_ID, NAME FROM WAGON_TYPE ORDER BY WAGON_TYPE_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                types.add(mapResultSetToWagonType(rs));
            }
        }

        return types;
    }

    @Override
    public void update(WagonType wagonType) throws SQLException {
        String sql = "UPDATE WAGON_TYPE SET NAME = ?, UPDATED_AT = CURRENT_TIMESTAMP WHERE WAGON_TYPE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, wagonType.getName());
            pstmt.setInt(2, wagonType.getWagonTypeId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM WAGON_TYPE WHERE WAGON_TYPE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public WagonType getByName(String name) throws SQLException {
        String sql = "SELECT WAGON_TYPE_ID, NAME FROM WAGON_TYPE WHERE NAME = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToWagonType(rs);
                }
            }
        }

        return null;
    }

    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(WAGON_TYPE_ID), 0) + 1 AS NEXT_ID FROM WAGON_TYPE";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }

        return 1;
    }

    private WagonType mapResultSetToWagonType(ResultSet rs) throws SQLException {
        return new WagonType(
                rs.getInt("WAGON_TYPE_ID"),
                rs.getString("NAME")
        );
    }
}

