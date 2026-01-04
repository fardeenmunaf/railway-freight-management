package app.db.dao.warehousebay;

import app.db.DatabaseConnection;
import app.db.dao.BaseDAOImpl;
import app.model.WarehouseBay;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarehouseBayDAOImpl extends BaseDAOImpl implements WarehouseBayDAO {

    public WarehouseBayDAOImpl() {
        super();
    }

    @Override
    public void create(WarehouseBay bay) throws SQLException {
        String sql = "INSERT INTO WAREHOUSE_BAY (BAY_ID, AISLE_ID, BAY_NO, CAPACITY_BOX) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (bay.getBayId() == null) bay.setBayId(getNextId());
            pstmt.setInt(1, bay.getBayId());
            pstmt.setInt(2, bay.getAisle() != null ? bay.getAisle().getAisleId() : null);
            pstmt.setInt(3, bay.getBayNo());
            pstmt.setInt(4, bay.getCapacityBox());
            pstmt.executeUpdate();
        }
    }

    @Override public WarehouseBay get(int id) throws SQLException { return null; }
    @Override public List<WarehouseBay> getAll() throws SQLException { return new ArrayList<>(); }
    @Override public void update(WarehouseBay bay) throws SQLException { }
    @Override public void delete(int id) throws SQLException { }
    @Override public List<WarehouseBay> getByAisle(int aisleId) throws SQLException { return new ArrayList<>(); }

    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(BAY_ID), 0) + 1 AS NEXT_ID FROM WAREHOUSE_BAY";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("NEXT_ID");
        }
        return 1;
    }
}

