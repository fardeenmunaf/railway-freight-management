package app.repositories;

import app.model.InventoryLot;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing inventory lots in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class InventoryLotRepository {

    private final List<InventoryLot> inventoryLots = new ArrayList<>();

    public void addInventoryLot(InventoryLot inventoryLot) {
        if (inventoryLot != null && inventoryLot.getLotId() != null) {
            if (!containsInventoryLot(inventoryLot.getLotId())) {
                inventoryLots.add(inventoryLot);
            }
        }
    }

    public List<InventoryLot> getAllInventoryLots() {
        return new ArrayList<>(inventoryLots);
    }

    public InventoryLot getInventoryLotById(Integer lotId) {
        for (InventoryLot lot : inventoryLots) {
            if (lotId != null && lotId.equals(lot.getLotId())) {
                return lot;
            }
        }
        return null;
    }

    public boolean updateInventoryLot(Integer lotId, InventoryLot updatedLot) {
        InventoryLot existingLot = getInventoryLotById(lotId);
        if (existingLot != null) {
            int index = inventoryLots.indexOf(existingLot);
            inventoryLots.set(index, updatedLot);
            return true;
        }
        return false;
    }

    public boolean deleteInventoryLot(Integer lotId) {
        return inventoryLots.removeIf(lot -> lotId != null && lotId.equals(lot.getLotId()));
    }

    public Integer getNextLotId() {
        int maxNumericId = 0;
        for (InventoryLot lot : inventoryLots) {
            if (lot.getLotId() != null) {
                int numericId = lot.getLotId();
                if (numericId > maxNumericId) {
                    maxNumericId = numericId;
                }
            }
        }
        return maxNumericId + 1;
    }

    private boolean containsInventoryLot(Integer lotId) {
        return getInventoryLotById(lotId) != null;
    }

    public void clear() {
        inventoryLots.clear();
    }
}
