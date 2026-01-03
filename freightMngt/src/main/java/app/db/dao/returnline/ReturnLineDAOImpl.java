package app.db.dao.returnline;

import app.db.dao.BaseDAOImpl;
import app.model.ReturnLine;
import app.model.ReturnOrder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for ReturnLine management in the Oracle database.
 * Implements CRUD operations for the ReturnLine entity.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class ReturnLineDAOImpl extends BaseDAOImpl implements ReturnLineDAO {

    /**
     * Constructor that initializes the database connection.
     */
    public ReturnLineDAOImpl() {
        super();
    }

    @Override
    public void create(ReturnLine returnLine) throws SQLException {
        String sql = "INSERT INTO " + getTableName("RETURN_LINE") +
                     " (RETURN_LINE_ID, RETURN_ORDER_ID, SKU, QTY, CONDITION) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (returnLine.getReturnLineId() == null) {
                returnLine.setReturnLineId(getNextId());
            }

            pstmt.setInt(1, returnLine.getReturnLineId());

            if (returnLine.getReturnOrder() != null && returnLine.getReturnOrder().getReturnId() != null) {
                pstmt.setInt(2, returnLine.getReturnOrder().getReturnId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            pstmt.setString(3, returnLine.getSku());

            if (returnLine.getQty() != null) {
                pstmt.setInt(4, returnLine.getQty());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            pstmt.setString(5, returnLine.getCondition());

            pstmt.executeUpdate();
        }
    }

    @Override
    public ReturnLine get(int id) throws SQLException {
        String sql = "SELECT * FROM " + getTableName("RETURN_LINE") + " WHERE RETURN_LINE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReturnLine(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<ReturnLine> getAll() throws SQLException {
        List<ReturnLine> returnLines = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("RETURN_LINE") + " ORDER BY RETURN_LINE_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                returnLines.add(mapResultSetToReturnLine(rs));
            }
        }
        return returnLines;
    }

    @Override
    public void update(ReturnLine returnLine) throws SQLException {
        String sql = "UPDATE " + getTableName("RETURN_LINE") +
                     " SET RETURN_ORDER_ID = ?, SKU = ?, QTY = ?, CONDITION = ? WHERE RETURN_LINE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (returnLine.getReturnOrder() != null && returnLine.getReturnOrder().getReturnId() != null) {
                pstmt.setInt(1, returnLine.getReturnOrder().getReturnId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            pstmt.setString(2, returnLine.getSku());

            if (returnLine.getQty() != null) {
                pstmt.setInt(3, returnLine.getQty());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.setString(4, returnLine.getCondition());
            pstmt.setInt(5, returnLine.getReturnLineId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM " + getTableName("RETURN_LINE") + " WHERE RETURN_LINE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<ReturnLine> getByReturnOrder(int returnOrderId) throws SQLException {
        List<ReturnLine> returnLines = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("RETURN_LINE") +
                     " WHERE RETURN_ORDER_ID = ? ORDER BY RETURN_LINE_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, returnOrderId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    returnLines.add(mapResultSetToReturnLine(rs));
                }
            }
        }
        return returnLines;
    }

    @Override
    public List<ReturnLine> getBySku(String sku) throws SQLException {
        List<ReturnLine> returnLines = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("RETURN_LINE") +
                     " WHERE SKU = ? ORDER BY RETURN_LINE_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, sku);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    returnLines.add(mapResultSetToReturnLine(rs));
                }
            }
        }
        return returnLines;
    }

    @Override
    public List<ReturnLine> getByCondition(String condition) throws SQLException {
        List<ReturnLine> returnLines = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("RETURN_LINE") +
                     " WHERE CONDITION = ? ORDER BY RETURN_LINE_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, condition);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    returnLines.add(mapResultSetToReturnLine(rs));
                }
            }
        }
        return returnLines;
    }

    @Override
    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM " + getTableName("RETURN_LINE") + " WHERE RETURN_LINE_ID = ?";

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
     * Gets the next available ID for a new ReturnLine.
     *
     * @return the next available ID
     * @throws SQLException if an error occurs during the operation
     */
    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(RETURN_LINE_ID), 0) + 1 AS NEXT_ID FROM " + getTableName("RETURN_LINE");

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }
        return 1;
    }

    /**
     * Maps a ResultSet row to a ReturnLine object.
     *
     * @param rs the ResultSet containing the data
     * @return the ReturnLine object
     * @throws SQLException if an error occurs during the operation
     */
    private ReturnLine mapResultSetToReturnLine(ResultSet rs) throws SQLException {
        ReturnLine returnLine = new ReturnLine();

        returnLine.setReturnLineId(rs.getInt("RETURN_LINE_ID"));

        // Set ReturnOrder if available
        if (rs.getObject("RETURN_ORDER_ID") != null) {
            ReturnOrder returnOrder = new ReturnOrder();
            returnOrder.setReturnId(rs.getInt("RETURN_ORDER_ID"));
            returnLine.setReturnOrder(returnOrder);
        }

        returnLine.setSku(rs.getString("SKU"));

        if (rs.getObject("QTY") != null) {
            returnLine.setQty(rs.getInt("QTY"));
        }

        returnLine.setCondition(rs.getString("CONDITION"));

        return returnLine;
    }
}

