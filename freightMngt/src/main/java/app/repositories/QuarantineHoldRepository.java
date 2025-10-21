package app.repositories;

import app.model.QuarantineHold;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing quarantine holds in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class QuarantineHoldRepository {

    private final List<QuarantineHold> quarantineHolds = new ArrayList<>();

    public void addQuarantineHold(QuarantineHold quarantineHold) {
        if (quarantineHold != null && quarantineHold.getQholdId() != null) {
            if (!containsQuarantineHold(quarantineHold.getQholdId())) {
                quarantineHolds.add(quarantineHold);
            }
        }
    }

    public List<QuarantineHold> getAllQuarantineHolds() {
        return new ArrayList<>(quarantineHolds);
    }

    public QuarantineHold getQuarantineHoldById(Integer holdId) {
        for (QuarantineHold hold : quarantineHolds) {
            if (holdId.equals(hold.getQholdId())) {
                return hold;
            }
        }
        return null;
    }

    public boolean updateQuarantineHold(Integer holdId, QuarantineHold updatedHold) {
        QuarantineHold existingHold = getQuarantineHoldById(holdId);
        if (existingHold != null) {
            int index = quarantineHolds.indexOf(existingHold);
            quarantineHolds.set(index, updatedHold);
            return true;
        }
        return false;
    }

    public boolean deleteQuarantineHold(Integer holdId) {
        return quarantineHolds.removeIf(hold -> holdId.equals(hold.getQholdId()));
    }

    public Integer getNextHoldId() {
        int maxId = 0;
        for (QuarantineHold hold : quarantineHolds) {
            if (hold.getQholdId() != null && hold.getQholdId() > maxId) {
                maxId = hold.getQholdId();
            }
        }
        return maxId + 1;
    }

    private boolean containsQuarantineHold(Integer holdId) {
        return getQuarantineHoldById(holdId) != null;
    }

    public void clear() {
        quarantineHolds.clear();
    }
}
