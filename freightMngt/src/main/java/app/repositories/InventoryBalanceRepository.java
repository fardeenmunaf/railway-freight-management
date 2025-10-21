package app.repositories;

import app.model.InventoryBalance;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing inventory balances in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class InventoryBalanceRepository {

    private final List<InventoryBalance> inventoryBalances = new ArrayList<>();

    public void addInventoryBalance(InventoryBalance inventoryBalance) {
        if (inventoryBalance != null && inventoryBalance.getBalanceId() != null) {
            if (!containsInventoryBalance(inventoryBalance.getBalanceId())) {
                inventoryBalances.add(inventoryBalance);
            }
        }
    }

    public List<InventoryBalance> getAllInventoryBalances() {
        return new ArrayList<>(inventoryBalances);
    }

    public InventoryBalance getInventoryBalanceById(Integer balanceId) {
        for (InventoryBalance balance : inventoryBalances) {
            if (balanceId.equals(balance.getBalanceId())) {
                return balance;
            }
        }
        return null;
    }

    public boolean updateInventoryBalance(Integer balanceId, InventoryBalance updatedBalance) {
        InventoryBalance existingBalance = getInventoryBalanceById(balanceId);
        if (existingBalance != null) {
            int index = inventoryBalances.indexOf(existingBalance);
            inventoryBalances.set(index, updatedBalance);
            return true;
        }
        return false;
    }

    public boolean deleteInventoryBalance(Integer balanceId) {
        return inventoryBalances.removeIf(balance -> balanceId.equals(balance.getBalanceId()));
    }

    public Integer getNextBalanceId() {
        int maxId = 0;
        for (InventoryBalance balance : inventoryBalances) {
            if (balance.getBalanceId() != null && balance.getBalanceId() > maxId) {
                maxId = balance.getBalanceId();
            }
        }
        return maxId + 1;
    }

    private boolean containsInventoryBalance(Integer balanceId) {
        return getInventoryBalanceById(balanceId) != null;
    }

    public void clear() {
        inventoryBalances.clear();
    }
}

