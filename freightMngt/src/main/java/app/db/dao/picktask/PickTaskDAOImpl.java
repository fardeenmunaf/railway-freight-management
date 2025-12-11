package app.db.dao.picktask;
import app.db.DatabaseConnection;
import app.model.PickTask;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PickTaskDAOImpl implements PickTaskDAO {
    private final Connection connection;
    public PickTaskDAOImpl() { this.connection = DatabaseConnection.getInstance().getConnection(); }

    @Override
    public void create(PickTask pickTask) throws SQLException {
        String sql = "INSERT INTO PICK_TASK (PICK_ID, WAVE_ID, ORDER_LINE_ID, BALANCE_ID, QTY, POS_SEQ) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (pickTask.getPickId() == null) pickTask.setPickId(getNextId());
            pstmt.setInt(1, pickTask.getPickId());
            pstmt.setInt(2, pickTask.getWave() != null ? pickTask.getWave().getWaveId() : null);
            pstmt.setInt(3, pickTask.getOrderLine() != null ? pickTask.getOrderLine().getOrderLineId() : null);
            pstmt.setInt(4, pickTask.getBalance() != null ? pickTask.getBalance().getBalanceId() : null);
            pstmt.setInt(5, pickTask.getQty());
            pstmt.setInt(6, pickTask.getPosSeq());
            pstmt.executeUpdate();
        }
    }

    @Override public PickTask get(int id) throws SQLException { return null; }
    @Override public List<PickTask> getAll() throws SQLException { return new ArrayList<>(); }
    @Override public void update(PickTask pickTask) throws SQLException { }
    @Override public void delete(int id) throws SQLException { }

    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(PICK_ID), 0) + 1 AS NEXT_ID FROM PICK_TASK";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("NEXT_ID");
        }
        return 1;
    }
}

