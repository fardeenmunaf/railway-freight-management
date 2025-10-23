package app.repositories;

import app.model.ReturnLine;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing return lines in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class ReturnLineRepository {

    private final List<ReturnLine> returnLines = new ArrayList<>();

    public void addReturnLine(ReturnLine returnLine) {
        if (returnLine != null && returnLine.getReturnLineId() != null) {
            if (!containsReturnLine(returnLine.getReturnLineId())) {
                returnLines.add(returnLine);
            }
        }
    }

    public List<ReturnLine> getAllReturnLines() {
        return new ArrayList<>(returnLines);
    }

    public ReturnLine getReturnLineById(Integer returnLineId) {
        for (ReturnLine returnLine : returnLines) {
            if (returnLineId.equals(returnLine.getReturnLineId())) {
                return returnLine;
            }
        }
        return null;
    }

    public boolean updateReturnLine(Integer returnLineId, ReturnLine updatedReturnLine) {
        ReturnLine existingReturnLine = getReturnLineById(returnLineId);
        if (existingReturnLine != null) {
            int index = returnLines.indexOf(existingReturnLine);
            returnLines.set(index, updatedReturnLine);
            return true;
        }
        return false;
    }

    public boolean deleteReturnLine(Integer returnLineId) {
        return returnLines.removeIf(returnLine -> returnLineId.equals(returnLine.getReturnLineId()));
    }

    public Integer getNextReturnLineId() {
        int maxId = 0;
        for (ReturnLine returnLine : returnLines) {
            if (returnLine.getReturnLineId() != null && returnLine.getReturnLineId() > maxId) {
                maxId = returnLine.getReturnLineId();
            }
        }
        return maxId + 1;
    }

    private boolean containsReturnLine(Integer returnLineId) {
        return getReturnLineById(returnLineId) != null;
    }

    public void clear() {
        returnLines.clear();
    }
}

