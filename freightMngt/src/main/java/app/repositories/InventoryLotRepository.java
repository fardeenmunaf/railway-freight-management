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

    public InventoryLot getInventoryLotById(String lotId) {
        for (InventoryLot lot : inventoryLots) {
            if (lotId != null && lotId.equals(lot.getLotId())) {
                return lot;
            }
        }
        return null;
    }

    public boolean updateInventoryLot(String lotId, InventoryLot updatedLot) {
        InventoryLot existingLot = getInventoryLotById(lotId);
        if (existingLot != null) {
            int index = inventoryLots.indexOf(existingLot);
            inventoryLots.set(index, updatedLot);
            return true;
        }
        return false;
    }

    public boolean deleteInventoryLot(String lotId) {
        return inventoryLots.removeIf(lot -> lotId != null && lotId.equals(lot.getLotId()));
    }

    public String getNextLotId() {
        int maxNumericId = 0;
        for (InventoryLot lot : inventoryLots) {
            if (lot.getLotId() != null) {
                try {
                    int numericId = Integer.parseInt(lot.getLotId());
                    if (numericId > maxNumericId) {
                        maxNumericId = numericId;
                    }
                } catch (NumberFormatException e) {
                    // Skip non-numeric IDs
                }
            }
        }
        return String.valueOf(maxNumericId + 1);
    }

    private boolean containsInventoryLot(String lotId) {
        return getInventoryLotById(lotId) != null;
    }

    public void clear() {
        inventoryLots.clear();
    }
}
