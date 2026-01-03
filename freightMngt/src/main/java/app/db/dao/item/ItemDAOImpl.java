package app.db.dao.item;

import app.db.DatabaseConnection;
import app.model.Item;
import app.model.ItemCategory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for Item management in the Oracle database.
 * Implements CRUD operations for the Item entity.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class ItemDAOImpl implements ItemDAO {

    private final Connection connection;

    /**
     * Constructor that initializes the database connection.
     */
    public ItemDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(Item item) throws SQLException {
        String sql = "INSERT INTO ITEM (ITEM_ID, ITEM_CATEGORY_ID, SKU, NAME, UOM, VOLUME, UNIT_WEIGHT) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (item.getItemId() == null) {
                item.setItemId(getNextId());
            }

            pstmt.setInt(1, item.getItemId());

            if (item.getItemCategory() != null) {
                pstmt.setInt(2, item.getItemCategory().getCategoryId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            pstmt.setString(3, item.getSku());
            pstmt.setString(4, item.getName());
            pstmt.setString(5, item.getUom());
            pstmt.setDouble(6, item.getVolume());
            pstmt.setDouble(7, item.getUnitWeight());

            pstmt.executeUpdate();
        }
    }

    @Override
    public Item get(int id) throws SQLException {
        String sql = "SELECT i.ITEM_ID, i.SKU, i.NAME, i.UOM, i.VOLUME, i.UNIT_WEIGHT, " +
                     "ic.CATEGORY_ID, ic.NAME as CATEGORY_NAME " +
                     "FROM ITEM i " +
                     "LEFT JOIN ITEM_CATEGORY ic ON i.ITEM_CATEGORY_ID = ic.CATEGORY_ID " +
                     "WHERE i.ITEM_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToItem(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Item> getAll() throws SQLException {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT i.ITEM_ID, i.SKU, i.NAME, i.UOM, i.VOLUME, i.UNIT_WEIGHT, " +
                     "ic.CATEGORY_ID, ic.NAME as CATEGORY_NAME " +
                     "FROM ITEM i " +
                     "LEFT JOIN ITEM_CATEGORY ic ON i.ITEM_CATEGORY_ID = ic.CATEGORY_ID " +
                     "ORDER BY i.ITEM_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        }

        return items;
    }

    @Override
    public void update(Item item) throws SQLException {
        String sql = "UPDATE ITEM SET ITEM_CATEGORY_ID = ?, SKU = ?, NAME = ?, UOM = ?, VOLUME = ?, UNIT_WEIGHT = ? WHERE ITEM_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (item.getItemCategory() != null) {
                pstmt.setInt(1, item.getItemCategory().getCategoryId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            pstmt.setString(2, item.getSku());
            pstmt.setString(3, item.getName());
            pstmt.setString(4, item.getUom());
            pstmt.setDouble(5, item.getVolume());
            pstmt.setDouble(6, item.getUnitWeight());
            pstmt.setInt(7, item.getItemId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM ITEM WHERE ITEM_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public Item getBySku(String sku) throws SQLException {
        String sql = "SELECT i.ITEM_ID, i.SKU, i.NAME, i.UOM, i.VOLUME, i.UNIT_WEIGHT, " +
                     "ic.CATEGORY_ID, ic.NAME as CATEGORY_NAME " +
                     "FROM ITEM i " +
                     "LEFT JOIN ITEM_CATEGORY ic ON i.ITEM_CATEGORY_ID = ic.CATEGORY_ID " +
                     "WHERE i.SKU = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, sku);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToItem(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Item> getByCategory(int categoryId) throws SQLException {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT i.ITEM_ID, i.SKU, i.NAME, i.UOM, i.VOLUME, i.UNIT_WEIGHT, " +
                     "ic.CATEGORY_ID, ic.NAME as CATEGORY_NAME " +
                     "FROM ITEM i " +
                     "LEFT JOIN ITEM_CATEGORY ic ON i.ITEM_CATEGORY_ID = ic.CATEGORY_ID " +
                     "WHERE i.ITEM_CATEGORY_ID = ? " +
                     "ORDER BY i.ITEM_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, categoryId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToItem(rs));
                }
            }
        }

        return items;
    }

    @Override
    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM ITEM WHERE ITEM_ID = ?";

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
     * Gets the next available ID for a new Item.
     *
     * @return the next available ID
     * @throws SQLException if an error occurs during the operation
     */
    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(ITEM_ID), 0) + 1 AS NEXT_ID FROM ITEM";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }

        return 1;
    }

    /**
     * Maps a ResultSet row to an Item object.
     *
     * @param rs the ResultSet containing the data
     * @return the Item object
     * @throws SQLException if an error occurs during the operation
     */
    private Item mapResultSetToItem(ResultSet rs) throws SQLException {
        ItemCategory category = null;
        if (rs.getObject("CATEGORY_ID") != null) {
            category = new ItemCategory(
                    rs.getInt("CATEGORY_ID"),
                    rs.getString("CATEGORY_NAME")
            );
        }

        return new Item(
                rs.getInt("ITEM_ID"),
                category,
                rs.getString("SKU"),
                rs.getString("NAME"),
                rs.getString("UOM"),
                rs.getDouble("VOLUME"),
                rs.getDouble("UNIT_WEIGHT")
        );
    }
}

