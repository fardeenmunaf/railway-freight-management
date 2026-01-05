package app.db.dao.itemcategory;

import app.db.dao.BaseDAOImpl;
import app.model.ItemCategory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for ItemCategory management in the Oracle database.
 * Implements CRUD operations for the ItemCategory entity.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class ItemCategoryDAOImpl extends BaseDAOImpl implements ItemCategoryDAO {

    /**
     * Constructor that initializes the database connection.
     */
    public ItemCategoryDAOImpl() {
        super();
    }

    @Override
    public void create(ItemCategory itemCategory) throws SQLException {
        String sql = "INSERT INTO " + getTableName("ITEM_CATEGORY") + " (CATEGORY_ID, NAME) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (itemCategory.getCategoryId() == null) {
                itemCategory.setCategoryId(getNextId());
            }

            pstmt.setInt(1, itemCategory.getCategoryId());
            pstmt.setString(2, itemCategory.getName());

            pstmt.executeUpdate();
        }
    }

    @Override
    public ItemCategory get(int id) throws SQLException {
        String sql = "SELECT * FROM " + getTableName("ITEM_CATEGORY") + " WHERE CATEGORY_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToItemCategory(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<ItemCategory> getAll() throws SQLException {
        List<ItemCategory> categories = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("ITEM_CATEGORY") + " ORDER BY CATEGORY_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categories.add(mapResultSetToItemCategory(rs));
            }
        }

        return categories;
    }

    @Override
    public void update(ItemCategory itemCategory) throws SQLException {
        String sql = "UPDATE " + getTableName("ITEM_CATEGORY") + " SET NAME = ? WHERE CATEGORY_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, itemCategory.getName());
            pstmt.setInt(2, itemCategory.getCategoryId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM " + getTableName("ITEM_CATEGORY") + " WHERE CATEGORY_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public ItemCategory getByName(String name) throws SQLException {
        String sql = "SELECT * FROM " + getTableName("ITEM_CATEGORY") + " WHERE NAME = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToItemCategory(rs);
                }
            }
        }

        return null;
    }

    @Override
    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM " + getTableName("ITEM_CATEGORY") + " WHERE CATEGORY_ID = ?";

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

    /**
     * Gets the next available ID for a new ItemCategory.
     *
     * @return the next available ID
     * @throws SQLException if an error occurs during the operation
     */
    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(CATEGORY_ID), 0) + 1 AS NEXT_ID FROM " + getTableName("ITEM_CATEGORY");

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }

        return 1;
    }

    /**
     * Maps a ResultSet row to an ItemCategory object.
     *
     * @param rs the ResultSet containing the data
     * @return the ItemCategory object
     * @throws SQLException if an error occurs during the operation
     */
    private ItemCategory mapResultSetToItemCategory(ResultSet rs) throws SQLException {
        return new ItemCategory(
                rs.getInt("CATEGORY_ID"),
                rs.getString("NAME")
        );
    }
}

