package app.db.dao.user;

import app.db.dao.BaseDAOImpl;
import app.model.Company;
import app.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação DAO para gestão de User na base de dados Oracle.
 * Implementa operações CRUD para a entidade User.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class UserDAOImpl extends BaseDAOImpl implements UserDAO {

    public UserDAOImpl() {
        super();
    }

    @Override
    public void create(User user) throws SQLException {
        String sql = "INSERT INTO " + getTableName("\"USER\"") + " (USER_ID, USERNAME, EMAIL, PASSWORD, COMPANY_ID, IS_ACTIVE, CREATED_AT) " +
                     "VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (user.getUserId() == null) {
                user.setUserId(getNextId());
            }

            pstmt.setInt(1, user.getUserId());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPassword());

            if (user.getCompanyId() != null) {
                pstmt.setInt(5, user.getCompanyId().getCompanyId());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }

            pstmt.setInt(6, user.getIsActive() != null && user.getIsActive() ? 1 : 0);

            pstmt.executeUpdate();
        }
    }

    @Override
    public User get(int id) throws SQLException {
        String sql = "SELECT * FROM " + getTableName("\"USER\"") + " WHERE USER_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<User> getAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("\"USER\"") + " ORDER BY USER_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }

        return users;
    }

    @Override
    public void update(User user) throws SQLException {
        String sql = "UPDATE " + getTableName("\"USER\"") + " SET USERNAME = ?, EMAIL = ?, PASSWORD = ?, COMPANY_ID = ?, IS_ACTIVE = ? WHERE USER_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());

            if (user.getCompanyId() != null) {
                pstmt.setInt(4, user.getCompanyId().getCompanyId());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            pstmt.setInt(5, user.getIsActive() != null && user.getIsActive() ? 1 : 0);
            pstmt.setInt(6, user.getUserId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM " + getTableName("\"USER\"") + " WHERE USER_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public User getByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM " + getTableName("\"USER\"") + " WHERE USERNAME = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }

        return null;
    }

    @Override
    public User getByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM " + getTableName("\"USER\"") + " WHERE EMAIL = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<User> getByCompany(int companyId) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("\"USER\"") + " WHERE COMPANY_ID = ? ORDER BY USER_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, companyId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        }

        return users;
    }

    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM " + getTableName("\"USER\"") + " WHERE USER_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("COUNT") > 0;
                }
            }
        }

        return false;
    }

    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(USER_ID), 0) + 1 AS NEXT_ID FROM " + getTableName("\"USER\"");

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }

        return 1;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        Company company = null;
        if (rs.getObject("COMPANY_ID") != null) {
            company = new Company(
                    rs.getInt("COMPANY_ID"),
                    rs.getString("NAME"),
                    rs.getString("SHORT_NAME"),
                    rs.getString("VAT")
            );
        }

        return new User(
                rs.getInt("USER_ID"),
                rs.getString("USERNAME"),
                rs.getString("EMAIL"),
                rs.getString("PASSWORD"),
                company,
                rs.getInt("IS_ACTIVE") == 1
        );
    }
}

