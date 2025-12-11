package app.db.dao.linesegment;
}
    List<LineSegment> getByLine(int lineId) throws SQLException;
    void delete(int id) throws SQLException;
    void update(LineSegment segment) throws SQLException;
    List<LineSegment> getAll() throws SQLException;
    LineSegment get(int id) throws SQLException;
    void create(LineSegment segment) throws SQLException;
public interface LineSegmentDAO {

import java.util.List;
import java.sql.SQLException;
import app.model.LineSegment;

