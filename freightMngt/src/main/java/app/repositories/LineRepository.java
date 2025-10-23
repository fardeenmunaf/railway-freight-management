package app.repositories;

import app.model.Line;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing lines in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class LineRepository {

    private final List<Line> lines = new ArrayList<>();

    public void addLine(Line line) {
        if (line != null && line.getLineId() != null) {
            if (!containsLine(line.getLineId())) {
                lines.add(line);
            }
        }
    }

    public List<Line> getAllLines() {
        return new ArrayList<>(lines);
    }

    public Line getLineById(Integer lineId) {
        for (Line line : lines) {
            if (lineId.equals(line.getLineId())) {
                return line;
            }
        }
        return null;
    }

    public boolean updateLine(Integer lineId, Line updatedLine) {
        Line existingLine = getLineById(lineId);
        if (existingLine != null) {
            int index = lines.indexOf(existingLine);
            lines.set(index, updatedLine);
            return true;
        }
        return false;
    }

    public boolean deleteLine(Integer lineId) {
        return lines.removeIf(line -> lineId.equals(line.getLineId()));
    }

    public Integer getNextLineId() {
        int maxId = 0;
        for (Line line : lines) {
            if (line.getLineId() != null && line.getLineId() > maxId) {
                maxId = line.getLineId();
            }
        }
        return maxId + 1;
    }

    private boolean containsLine(Integer lineId) {
        return getLineById(lineId) != null;
    }

    public void clear() {
        lines.clear();
    }
}

