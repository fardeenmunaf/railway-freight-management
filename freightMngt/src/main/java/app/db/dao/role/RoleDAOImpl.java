package app.db.dao.role;

import app.db.dao.BaseDAOImpl;
import app.model.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação DAO para gestão de Role na base de dados Oracle.
 * Implementa operações CRUD para a entidade Role.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class RoleDAOImpl extends BaseDAOImpl implements RoleDAO {

    public RoleDAOImpl() {
        super();
    }

    @Override
    public void create(Role role) throws SQLException {
        String sql = "INSERT INTO " + getTableName("ROLE") + " (ROLE_ID, NAME) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (role.getRoleId() == null) {
                role.setRoleId(getNextId());
            }

            pstmt.setInt(1, role.getRoleId());
            pstmt.setString(2, role.getRoleName());

            pstmt.executeUpdate();
        }
    }

    @Override
    public Role get(int id) throws SQLException {
        String sql = "SELECT ROLE_ID, NAME FROM " + getTableName("ROLE") + " WHERE ROLE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRole(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Role> getAll() throws SQLException {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT ROLE_ID, NAME FROM " + getTableName("ROLE") + " ORDER BY ROLE_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                roles.add(mapResultSetToRole(rs));
            }
        }

        return roles;
    }

    @Override
    public void update(Role role) throws SQLException {
        String sql = "UPDATE " + getTableName("ROLE") + " SET NAME = ? WHERE ROLE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, role.getRoleName());
            pstmt.setInt(2, role.getRoleId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM " + getTableName("ROLE") + " WHERE ROLE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public Role getByName(String name) throws SQLException {
        String sql = "SELECT ROLE_ID, NAME FROM " + getTableName("ROLE") + " WHERE NAME = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRole(rs);
                }
            }
        }

        return null;
    }

    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM " + getTableName("ROLE") + " WHERE ROLE_ID = ?";

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
        String sql = "SELECT NVL(MAX(ROLE_ID), 0) + 1 AS NEXT_ID FROM " + getTableName("ROLE");

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }

        return 1;
    }

    private Role mapResultSetToRole(ResultSet rs) throws SQLException {
        return new Role(
                rs.getInt("ROLE_ID"),
                rs.getString("NAME")
        );
    }
}


