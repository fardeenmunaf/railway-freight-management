package app.db.dao.line;

import app.db.DatabaseConnection;
import app.model.Company;
import app.model.Line;
import app.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação DAO para gestão de Line na base de dados Oracle.
 * Implementa operações CRUD para a entidade Line.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class LineDAOImpl implements LineDAO {

    private final Connection connection;

    public LineDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(Line line) throws SQLException {
        String sql = "INSERT INTO LINE (LINE_ID, NAME, OWNER_ID) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (line.getLineId() == null) {
                line.setLineId(getNextId());
            }

            pstmt.setInt(1, line.getLineId());
            pstmt.setString(2, line.getName());

            if (line.getOwner() != null && line.getOwner().getCompanyId() != null) {
                pstmt.setInt(3, line.getOwner().getCompanyId().getCompanyId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.executeUpdate();
        }
    }

    @Override
    public Line get(int id) throws SQLException {
        String sql = "SELECT l.LINE_ID, l.OWNER_ID, " +
                     "c.COMPANY_ID, c.NAME as COMPANY_NAME, c.SHORT_NAME, c.VAT " +
                     "FROM LINE l " +
                     "LEFT JOIN COMPANY c ON l.OWNER_ID = c.COMPANY_ID " +
                     "WHERE l.LINE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLine(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Line> getAll() throws SQLException {
        List<Line> lines = new ArrayList<>();
        String sql = "SELECT l.LINE_ID, l.NAME, l.OWNER_ID, " +
                     "c.COMPANY_ID, c.NAME as COMPANY_NAME, c.SHORT_NAME, c.VAT " +
                     "FROM LINE l " +
                     "LEFT JOIN COMPANY c ON l.OWNER_ID = c.COMPANY_ID " +
                     "ORDER BY l.LINE_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lines.add(mapResultSetToLine(rs));
            }
        }

        return lines;
    }

    @Override
    public void update(Line line) throws SQLException {
        String sql = "UPDATE LINE SET OWNER_ID = ? WHERE LINE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (line.getOwner() != null && line.getOwner().getCompanyId() != null) {
                pstmt.setInt(1, line.getOwner().getCompanyId().getCompanyId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            pstmt.setInt(2, line.getLineId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM LINE WHERE LINE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Line> getByOwner(int ownerId) throws SQLException {
        List<Line> lines = new ArrayList<>();
        String sql = "SELECT l.LINE_ID, l.NAME, l.OWNER_ID, " +
                     "c.COMPANY_ID, c.NAME as COMPANY_NAME, c.SHORT_NAME, c.VAT " +
                     "FROM LINE l " +
                     "LEFT JOIN COMPANY c ON l.OWNER_ID = c.COMPANY_ID " +
                     "WHERE l.OWNER_ID = ? " +
                     "ORDER BY l.LINE_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, ownerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lines.add(mapResultSetToLine(rs));
                }
            }
        }

        return lines;
    }

    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(LINE_ID), 0) + 1 AS NEXT_ID FROM LINE";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }

        return 1;
    }

    private Line mapResultSetToLine(ResultSet rs) throws SQLException {
        User owner = null;
        if (rs.getObject("OWNER_ID") != null) {
            Company company = new Company(
                    rs.getInt("COMPANY_ID"),
                    rs.getString("COMPANY_NAME"),
                    rs.getString("SHORT_NAME"),
                    rs.getString("VAT")
            );
            owner = new User();
            owner.setUserId(rs.getInt("OWNER_ID"));
            owner.setCompanyId(company);
        }

        return new Line(
                rs.getInt("LINE_ID"),
                owner
        );
    }
}

