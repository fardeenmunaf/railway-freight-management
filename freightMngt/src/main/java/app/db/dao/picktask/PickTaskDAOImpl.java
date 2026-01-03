package app.db.dao.picktask;

import app.db.dao.BaseDAOImpl;
import app.model.PickTask;
import app.model.PickWave;
import app.model.OrderLine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for PickTask management in the Oracle database.
 * Implements CRUD operations for the PickTask entity.
 * Note: This implementation assumes a PICK_TASK_WAVE junction table for many-to-many relationship.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class PickTaskDAOImpl extends BaseDAOImpl implements PickTaskDAO {

    /**
     * Constructor that initializes the database connection.
     */
    public PickTaskDAOImpl() {
        super();
    }

    @Override
    public void create(PickTask pickTask) throws SQLException {
        String sql = "INSERT INTO " + getTableName("PICK_TASK") +
                     " (TASK_ID, WAVE_ID, ORDER_LINE_ID, LOT_ID, FROM_BAY_ID, " +
                     "QTY_REQUIRED, QTY_PICKED, STATUS, ASSIGNED_TO, PRIORITY, CREATED_AT) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (pickTask.getPickTaskId() == null) {
                pickTask.setPickTaskId(getNextId());
            }

            pstmt.setInt(1, pickTask.getPickTaskId());

            if (pickTask.getOrderLine() != null && pickTask.getOrderLine().getOrderLineId() != null) {
                pstmt.setInt(2, pickTask.getOrderLine().getOrderLineId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            if (pickTask.getBalance() != null) {
                pstmt.setInt(3, pickTask.getBalance());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            if (pickTask.getQty() != null) {
                pstmt.setDouble(4, pickTask.getQty());
            } else {
                pstmt.setNull(4, Types.DOUBLE);
            }

            if (pickTask.getPositionSequence() != null) {
                pstmt.setInt(5, pickTask.getPositionSequence());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }

            pstmt.executeUpdate();

            // Create associations with PickWaves if any
            if (pickTask.getPickWaves() != null && !pickTask.getPickWaves().isEmpty()) {
                createPickTaskWaveAssociations(pickTask.getPickTaskId(), pickTask.getPickWaves());
            }
        }
    }

    @Override
    public PickTask get(int id) throws SQLException {
        String sql = "SELECT * FROM " + getTableName("PICK_TASK") + " WHERE TASK_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    PickTask pickTask = mapResultSetToPickTask(rs);
                    // Load associated PickWaves
                    pickTask.setPickWaves(getPickWavesForTask(id));
                    return pickTask;
                }
            }
        }
        return null;
    }

    @Override
    public List<PickTask> getAll() throws SQLException {
        List<PickTask> tasks = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("PICK_TASK") + " ORDER BY PRIORITY DESC, CREATED_AT ASC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                PickTask pickTask = mapResultSetToPickTask(rs);
                // Load associated PickWaves for each task
                pickTask.setPickWaves(getPickWavesForTask(pickTask.getPickTaskId()));
                tasks.add(pickTask);
            }
        }
        return tasks;
    }

    @Override
    public void update(PickTask pickTask) throws SQLException {
        String sql = "UPDATE " + getTableName("PICK_TASK") +
                     " SET WAVE_ID = ?, ORDER_LINE_ID = ?, LOT_ID = ?, FROM_BAY_ID = ?, " +
                     "QTY_REQUIRED = ?, QTY_PICKED = ?, STATUS = ?, ASSIGNED_TO = ?, PRIORITY = ? " +
                     "WHERE TASK_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (pickTask.getOrderLine() != null && pickTask.getOrderLine().getOrderLineId() != null) {
                pstmt.setInt(1, pickTask.getOrderLine().getOrderLineId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            if (pickTask.getBalance() != null) {
                pstmt.setInt(2, pickTask.getBalance());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            if (pickTask.getQty() != null) {
                pstmt.setDouble(3, pickTask.getQty());
            } else {
                pstmt.setNull(3, Types.DOUBLE);
            }

            if (pickTask.getPositionSequence() != null) {
                pstmt.setInt(4, pickTask.getPositionSequence());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            pstmt.setInt(5, pickTask.getPickTaskId());

            pstmt.executeUpdate();

            // Update PickWave associations
            deletePickTaskWaveAssociations(pickTask.getPickTaskId());
            if (pickTask.getPickWaves() != null && !pickTask.getPickWaves().isEmpty()) {
                createPickTaskWaveAssociations(pickTask.getPickTaskId(), pickTask.getPickWaves());
            }
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM " + getTableName("PICK_TASK") + " WHERE TASK_ID = ?";

        // First delete associations
        deletePickTaskWaveAssociations(id);

        // Then delete the pick task
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<PickTask> getByWave(int waveId) throws SQLException {
        List<PickTask> tasks = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("PICK_TASK") +
                     " WHERE WAVE_ID = ? ORDER BY PRIORITY DESC, CREATED_AT ASC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, waveId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PickTask pickTask = mapResultSetToPickTask(rs);
                    pickTask.setPickWaves(getPickWavesForTask(pickTask.getPickTaskId()));
                    tasks.add(pickTask);
                }
            }
        }
        return tasks;
    }

    public List<PickTask> getByStatus(String status) throws SQLException {
        List<PickTask> tasks = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("PICK_TASK") +
                     " WHERE STATUS = ? ORDER BY PRIORITY DESC, CREATED_AT ASC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PickTask pickTask = mapResultSetToPickTask(rs);
                    pickTask.setPickWaves(getPickWavesForTask(pickTask.getPickTaskId()));
                    tasks.add(pickTask);
                }
            }
        }
        return tasks;
    }

    public List<PickTask> getByUser(int userId) throws SQLException {
        List<PickTask> tasks = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("PICK_TASK") +
                     " WHERE ASSIGNED_TO = ? ORDER BY PRIORITY DESC, CREATED_AT ASC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PickTask pickTask = mapResultSetToPickTask(rs);
                    pickTask.setPickWaves(getPickWavesForTask(pickTask.getPickTaskId()));
                    tasks.add(pickTask);
                }
            }
        }
        return tasks;
    }

    public List<PickTask> getByOrderLine(int orderLineId) throws SQLException {
        List<PickTask> tasks = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("PICK_TASK") +
                     " WHERE ORDER_LINE_ID = ? ORDER BY CREATED_AT ASC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, orderLineId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PickTask pickTask = mapResultSetToPickTask(rs);
                    pickTask.setPickWaves(getPickWavesForTask(pickTask.getPickTaskId()));
                    tasks.add(pickTask);
                }
            }
        }
        return tasks;
    }

    @Override
    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM " + getTableName("PICK_TASK") + " WHERE TASK_ID = ?";

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
     * Gets the next available ID for a new PickTask.
     *
     * @return the next available ID
     * @throws SQLException if an error occurs during the operation
     */
    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(TASK_ID), 0) + 1 AS NEXT_ID FROM " + getTableName("PICK_TASK");

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }
        return 1;
    }

    /**
     * Creates associations between a PickTask and its PickWaves.
     *
     * @param pickTaskId the ID of the PickTask
     * @param pickWaves the list of PickWaves to associate
     * @throws SQLException if an error occurs during the operation
     */
    private void createPickTaskWaveAssociations(int pickTaskId, List<PickWave> pickWaves) throws SQLException {
        String sql = "INSERT INTO PICK_TASK_WAVE (PICK_ID, WAVE_ID) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (PickWave wave : pickWaves) {
                if (wave != null && wave.getPickWaveId() != null) {
                    pstmt.setInt(1, pickTaskId);
                    pstmt.setInt(2, wave.getPickWaveId());
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
        }
    }

    /**
     * Deletes all associations between a PickTask and its PickWaves.
     *
     * @param pickTaskId the ID of the PickTask
     * @throws SQLException if an error occurs during the operation
     */
    private void deletePickTaskWaveAssociations(int pickTaskId) throws SQLException {
        String sql = "DELETE FROM PICK_TASK_WAVE WHERE PICK_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, pickTaskId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Retrieves all PickWaves associated with a specific PickTask.
     *
     * @param pickTaskId the ID of the PickTask
     * @return list of associated PickWaves
     * @throws SQLException if an error occurs during the operation
     */
    private List<PickWave> getPickWavesForTask(int pickTaskId) throws SQLException {
        List<PickWave> pickWaves = new ArrayList<>();
        String sql = "SELECT pw.* FROM PICK_WAVE pw " +
                     "JOIN PICK_TASK_WAVE ptw ON pw.WAVE_ID = ptw.WAVE_ID " +
                     "WHERE ptw.PICK_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, pickTaskId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PickWave pickWave = new PickWave();
                    pickWave.setPickWaveId(rs.getInt("WAVE_ID"));
                    pickWave.setStrategy(rs.getString("STRATEGY"));
                    pickWave.setStatus(rs.getString("STATUS"));
                    pickWaves.add(pickWave);
                }
            }
        }
        return pickWaves;
    }

    /**
     * Maps a ResultSet row to a PickTask object.
     *
     * @param rs the ResultSet containing the data
     * @return the PickTask object
     * @throws SQLException if an error occurs during the operation
     */
    private PickTask mapResultSetToPickTask(ResultSet rs) throws SQLException {
        PickTask pickTask = new PickTask();

        pickTask.setPickTaskId(rs.getInt("PICK_ID"));

        // Set OrderLine if available
        if (rs.getObject("ORDER_LINE_ID") != null) {
            OrderLine orderLine = new OrderLine();
            orderLine.setOrderLineId(rs.getInt("ORDER_LINE_ID"));
            pickTask.setOrderLine(orderLine);
        }

        if (rs.getObject("BALANCE_ID") != null) {
            pickTask.setBalance(rs.getInt("BALANCE_ID"));
        }

        if (rs.getObject("QTY") != null) {
            pickTask.setQty(rs.getDouble("QTY"));
        }

        if (rs.getObject("POS_SEQ") != null) {
            pickTask.setPositionSequence(rs.getInt("POS_SEQ"));
        }

        return pickTask;
    }
}

