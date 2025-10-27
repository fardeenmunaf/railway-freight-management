package app.service;

import app.controller.InventoryBalanceController;
import app.controller.WarehouseBayController;
import app.model.*;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class WagonsUnloadingTest {

    private WagonsUnloading wagonsUnloading;
    private InventoryBalanceController balanceController;
    private WarehouseBayController bayController;

    @Before
    public void setUp() {
        // Clear repositories
        balanceController = new InventoryBalanceController();
        bayController = new WarehouseBayController();
        balanceController.clearAllInventoryBalances();
        bayController.clearAllWarehouseBays();

        wagonsUnloading = new WagonsUnloading();
    }

    /**
     * Helper method to create a WarehouseBay with proper object hierarchy
     */
    private WarehouseBay createWarehouseBay(Integer bayId, Integer warehouseId, Integer aisleNo, Integer bayNo, Integer capacity) {
        FacilityType facilityType = new FacilityType(1, "Warehouse");
        Facility facility = new Facility(1, "Main Facility", facilityType, 40.0, -8.0, false);
        Warehouse warehouse = new Warehouse(warehouseId, facility, "WH" + warehouseId);
        WarehouseAisle aisle = new WarehouseAisle(aisleNo, warehouse, aisleNo, "A" + aisleNo);
        return new WarehouseBay(bayId, aisle, bayNo, capacity);
    }

    @Test
    public void testConstructor() {
        assertNotNull(wagonsUnloading);
    }

    @Test
    public void testGetUnassignedBalancesWithNoBalances() {
        List<InventoryBalance> result = wagonsUnloading.getUnassignedBalances();
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testGetUnassignedBalancesWithUnassignedBalance() {
        // Create an unassigned balance (bay = null)
        List<InventoryLot> lots = new ArrayList<>();
        lots.add(new InventoryLot(1, 100, 10, null, LocalDateTime.now()));
        InventoryBalance unassigned = new InventoryBalance(-1, null, lots, 10);
        balanceController.createInventoryBalance(unassigned);

        List<InventoryBalance> result = wagonsUnloading.getUnassignedBalances();
        assertEquals(1, result.size());
        assertNull(result.get(0).getBay());
    }

    @Test
    public void testGetUnassignedBalancesWithAssignedBalance() {
        // Create a warehouse and bay
        WarehouseBay bay = createWarehouseBay(1, 1, 1, 1, 50);
        bayController.createWarehouseBay(bay);

        // Create an assigned balance (bay != null)
        List<InventoryLot> lots = new ArrayList<>();
        lots.add(new InventoryLot(1, 100, 10, null, LocalDateTime.now()));
        InventoryBalance assigned = new InventoryBalance(1, bay, lots, 10);
        balanceController.createInventoryBalance(assigned);

        List<InventoryBalance> result = wagonsUnloading.getUnassignedBalances();
        assertEquals(0, result.size());
    }

    @Test
    public void testGetUnassignedBalancesMixedBalances() {
        // Create warehouse and bay
        WarehouseBay bay = createWarehouseBay(1, 1, 1, 1, 50);
        bayController.createWarehouseBay(bay);

        // Create one assigned and one unassigned
        List<InventoryLot> lots1 = new ArrayList<>();
        lots1.add(new InventoryLot(1, 100, 10, null, LocalDateTime.now()));
        InventoryBalance assigned = new InventoryBalance(1, bay, lots1, 10);
        balanceController.createInventoryBalance(assigned);

        List<InventoryLot> lots2 = new ArrayList<>();
        lots2.add(new InventoryLot(2, 200, 20, null, LocalDateTime.now()));
        InventoryBalance unassigned = new InventoryBalance(-2, null, lots2, 20);
        balanceController.createInventoryBalance(unassigned);

        List<InventoryBalance> result = wagonsUnloading.getUnassignedBalances();
        assertEquals(1, result.size());
        assertNull(result.get(0).getBay());
    }

    @Test
    public void testGetAllBays() {
        WarehouseBay bay1 = createWarehouseBay(1, 1, 1, 1, 50);
        WarehouseBay bay2 = createWarehouseBay(2, 1, 1, 2, 30);
        bayController.createWarehouseBay(bay1);
        bayController.createWarehouseBay(bay2);

        List<WarehouseBay> result = wagonsUnloading.getAllBays();
        assertEquals(2, result.size());
    }

    @Test
    public void testGetAllBaysEmpty() {
        List<WarehouseBay> result = wagonsUnloading.getAllBays();
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testGetAvailableCapacityEmptyBay() {
        WarehouseBay bay = createWarehouseBay(1, 1, 1, 1, 50);
        bayController.createWarehouseBay(bay);

        Integer capacity = wagonsUnloading.getAvailableCapacity(bay);
        assertEquals(Integer.valueOf(50), capacity);
    }

    @Test
    public void testGetAvailableCapacityPartiallyOccupiedBay() {
        WarehouseBay bay = createWarehouseBay(1, 1, 1, 1, 50);
        bayController.createWarehouseBay(bay);

        // Add 3 lots to the bay
        List<InventoryLot> lots = new ArrayList<>();
        lots.add(new InventoryLot(1, 100, 10, null, LocalDateTime.now()));
        lots.add(new InventoryLot(2, 100, 15, null, LocalDateTime.now()));
        lots.add(new InventoryLot(3, 100, 20, null, LocalDateTime.now()));
        InventoryBalance balance = new InventoryBalance(1, bay, lots, 45);
        balanceController.createInventoryBalance(balance);

        Integer capacity = wagonsUnloading.getAvailableCapacity(bay);
        assertEquals(Integer.valueOf(47), capacity); // 50 - 3 lots
    }

    @Test
    public void testGetAvailableCapacityFullBay() {
        WarehouseBay bay = createWarehouseBay(1, 1, 1, 1, 3);
        bayController.createWarehouseBay(bay);

        // Fill bay with 3 lots
        List<InventoryLot> lots = new ArrayList<>();
        lots.add(new InventoryLot(1, 100, 10, null, LocalDateTime.now()));
        lots.add(new InventoryLot(2, 100, 15, null, LocalDateTime.now()));
        lots.add(new InventoryLot(3, 100, 20, null, LocalDateTime.now()));
        InventoryBalance balance = new InventoryBalance(1, bay, lots, 45);
        balanceController.createInventoryBalance(balance);

        Integer capacity = wagonsUnloading.getAvailableCapacity(bay);
        assertEquals(Integer.valueOf(0), capacity);
    }

    @Test
    public void testIsBaySuitableEmptyBayWithCapacity() {
        WarehouseBay bay = createWarehouseBay(1, 1, 1, 1, 50);
        bayController.createWarehouseBay(bay);

        boolean result = wagonsUnloading.isBaySuitable(bay, 100);
        assertTrue(result);
    }

    @Test
    public void testIsBaySuitableEmptyBayNoCapacity() {
        WarehouseBay bay = createWarehouseBay(1, 1, 1, 1, 0);
        bayController.createWarehouseBay(bay);

        boolean result = wagonsUnloading.isBaySuitable(bay, 100);
        assertFalse(result);
    }

    @Test
    public void testIsBaySuitableSameSkuWithCapacity() {
        WarehouseBay bay = createWarehouseBay(1, 1, 1, 1, 50);
        bayController.createWarehouseBay(bay);

        // Add SKU 100 to bay
        List<InventoryLot> lots = new ArrayList<>();
        lots.add(new InventoryLot(1, 100, 10, null, LocalDateTime.now()));
        InventoryBalance balance = new InventoryBalance(1, bay, lots, 10);
        balanceController.createInventoryBalance(balance);

        boolean result = wagonsUnloading.isBaySuitable(bay, 100);
        assertTrue(result);
    }

    @Test
    public void testIsBaySuitableDifferentSku() {
        WarehouseBay bay = createWarehouseBay(1, 1, 1, 1, 50);
        bayController.createWarehouseBay(bay);

        // Add SKU 100 to bay
        List<InventoryLot> lots = new ArrayList<>();
        lots.add(new InventoryLot(1, 100, 10, null, LocalDateTime.now()));
        InventoryBalance balance = new InventoryBalance(1, bay, lots, 10);
        balanceController.createInventoryBalance(balance);

        // Try to add different SKU 200
        boolean result = wagonsUnloading.isBaySuitable(bay, 200);
        assertFalse(result);
    }

    @Test
    public void testIsBaySuitableSameSkuNoCapacity() {
        WarehouseBay bay = createWarehouseBay(1, 1, 1, 1, 1);
        bayController.createWarehouseBay(bay);

        // Fill bay with SKU 100
        List<InventoryLot> lots = new ArrayList<>();
        lots.add(new InventoryLot(1, 100, 10, null, LocalDateTime.now()));
        InventoryBalance balance = new InventoryBalance(1, bay, lots, 10);
        balanceController.createInventoryBalance(balance);

        boolean result = wagonsUnloading.isBaySuitable(bay, 100);
        assertFalse(result);
    }

    @Test
    public void testFindSuitableBayFound() {
        WarehouseBay bay1 = createWarehouseBay(1, 1, 1, 1, 50);
        WarehouseBay bay2 = createWarehouseBay(2, 1, 1, 2, 30);
        bayController.createWarehouseBay(bay1);
        bayController.createWarehouseBay(bay2);

        WarehouseBay result = wagonsUnloading.findSuitableBay(100);
        assertNotNull(result);
    }

    @Test
    public void testFindSuitableBayNotFound() {
        WarehouseBay bay = createWarehouseBay(1, 1, 1, 1, 1);
        bayController.createWarehouseBay(bay);

        // Fill bay with different SKU
        List<InventoryLot> lots = new ArrayList<>();
        lots.add(new InventoryLot(1, 200, 10, null, LocalDateTime.now()));
        InventoryBalance balance = new InventoryBalance(1, bay, lots, 10);
        balanceController.createInventoryBalance(balance);

        // Try to find bay for SKU 100 - should not find (different SKU)
        WarehouseBay result = wagonsUnloading.findSuitableBay(100);
        assertNull(result);
    }

    @Test
    public void testFindSuitableBayNoBaysAvailable() {
        WarehouseBay result = wagonsUnloading.findSuitableBay(100);
        assertNull(result);
    }

    @Test
    public void testAllocateToBay() {
        WarehouseBay bay = createWarehouseBay(1, 1, 1, 1, 50);
        bayController.createWarehouseBay(bay);

        List<InventoryLot> lots = new ArrayList<>();
        lots.add(new InventoryLot(1, 100, 10, null, LocalDateTime.now()));
        lots.add(new InventoryLot(2, 100, 20, null, LocalDateTime.now()));

        int initialSize = balanceController.getAllInventoryBalances().size();
        wagonsUnloading.allocateToBay(lots, bay, 30);

        List<InventoryBalance> balances = balanceController.getAllInventoryBalances();
        assertEquals(initialSize + 1, balances.size());

        InventoryBalance newBalance = balances.get(balances.size() - 1);
        assertEquals(bay, newBalance.getBay());
        assertEquals(2, newBalance.getLots().size());
        assertEquals(Integer.valueOf(30), newBalance.getQty());
    }

    @Test
    public void testProcessBalanceEmptyLots() {
        List<InventoryLot> emptyLots = new ArrayList<>();
        InventoryBalance balance = new InventoryBalance(-1, null, emptyLots, 0);

        wagonsUnloading.processBalance(balance);

        // Should do nothing, no balances created
        List<InventoryBalance> balances = balanceController.getAllInventoryBalances();
        assertEquals(0, balances.size());
    }

    @Test
    public void testProcessBalanceWithSuitableBay() {
        WarehouseBay bay = createWarehouseBay(1, 1, 1, 1, 50);
        bayController.createWarehouseBay(bay);

        List<InventoryLot> lots = new ArrayList<>();
        lots.add(new InventoryLot(1, 100, 10, null, LocalDateTime.now()));
        lots.add(new InventoryLot(2, 100, 20, null, LocalDateTime.now()));
        InventoryBalance unassignedBalance = new InventoryBalance(-1, null, lots, 30);
        balanceController.createInventoryBalance(unassignedBalance);

        wagonsUnloading.processBalance(unassignedBalance);

        // Original unassigned balance should be deleted
        boolean foundUnassigned = false;
        for (InventoryBalance balance : balanceController.getAllInventoryBalances()) {
            if (balance.getBalanceId() == -1) {
                foundUnassigned = true;
                break;
            }
        }
        assertFalse(foundUnassigned);

        // New assigned balance should exist
        List<InventoryBalance> allBalances = balanceController.getAllInventoryBalances();
        assertFalse(allBalances.isEmpty());
        
        boolean foundAssignedBalance = false;
        for (InventoryBalance balance : allBalances) {
            if (balance.getBay() != null) {
                foundAssignedBalance = true;
                break;
            }
        }
        assertTrue(foundAssignedBalance);
    }

    @Test
    public void testProcessBalancePartialAllocation() {
        WarehouseBay bay = createWarehouseBay(1, 1, 1, 1, 2); // Only 2 box capacity
        bayController.createWarehouseBay(bay);

        List<InventoryLot> lots = new ArrayList<>();
        lots.add(new InventoryLot(1, 100, 10, null, LocalDateTime.now()));
        lots.add(new InventoryLot(2, 100, 20, null, LocalDateTime.now()));
        lots.add(new InventoryLot(3, 100, 30, null, LocalDateTime.now()));
        InventoryBalance unassignedBalance = new InventoryBalance(-1, null, lots, 60);
        balanceController.createInventoryBalance(unassignedBalance);

        wagonsUnloading.processBalance(unassignedBalance);

        // Should have created one assigned balance with 2 lots
        List<InventoryBalance> allBalances = balanceController.getAllInventoryBalances();
        
        InventoryBalance assignedBalance = null;
        InventoryBalance remainingBalance = null;
        
        for (InventoryBalance balance : allBalances) {
            if (balance.getBay() != null) {
                assignedBalance = balance;
            } else if (balance.getBalanceId() == -1) {
                remainingBalance = balance;
            }
        }

        assertNotNull(assignedBalance);
        assertEquals(2, assignedBalance.getLots().size());

        // Remaining unassigned balance should have 1 lot
        assertNotNull(remainingBalance);
        assertEquals(1, remainingBalance.getLots().size());
    }

    @Test
    public void testProcessBalanceNoSuitableBay() {
        // No bays created
        List<InventoryLot> lots = new ArrayList<>();
        lots.add(new InventoryLot(1, 100, 10, null, LocalDateTime.now()));
        InventoryBalance unassignedBalance = new InventoryBalance(-1, null, lots, 10);
        balanceController.createInventoryBalance(unassignedBalance);

        wagonsUnloading.processBalance(unassignedBalance);

        // Balance should remain unassigned
        boolean foundUnassigned = false;
        for (InventoryBalance balance : balanceController.getAllInventoryBalances()) {
            if (balance.getBalanceId() == -1) {
                foundUnassigned = true;
                assertNull(balance.getBay());
                break;
            }
        }
        assertTrue(foundUnassigned);
    }

    @Test
    public void testUnloadAllWagons() {
        WarehouseBay bay1 = createWarehouseBay(1, 1, 1, 1, 50);
        WarehouseBay bay2 = createWarehouseBay(2, 1, 1, 2, 50);
        bayController.createWarehouseBay(bay1);
        bayController.createWarehouseBay(bay2);

        // Create multiple unassigned balances
        List<InventoryLot> lots1 = new ArrayList<>();
        lots1.add(new InventoryLot(1, 100, 10, null, LocalDateTime.now()));
        InventoryBalance balance1 = new InventoryBalance(-1, null, lots1, 10);
        balanceController.createInventoryBalance(balance1);

        List<InventoryLot> lots2 = new ArrayList<>();
        lots2.add(new InventoryLot(2, 200, 20, null, LocalDateTime.now()));
        InventoryBalance balance2 = new InventoryBalance(-2, null, lots2, 20);
        balanceController.createInventoryBalance(balance2);

        wagonsUnloading.unloadAllWagons();

        // All unassigned balances should be processed
        List<InventoryBalance> unassigned = wagonsUnloading.getUnassignedBalances();
        assertEquals(0, unassigned.size());
    }

    @Test
    public void testUnloadAllWagonsNoUnassigned() {
        WarehouseBay bay = createWarehouseBay(1, 1, 1, 1, 50);
        bayController.createWarehouseBay(bay);

        // Create only assigned balances
        List<InventoryLot> lots = new ArrayList<>();
        lots.add(new InventoryLot(1, 100, 10, null, LocalDateTime.now()));
        InventoryBalance balance = new InventoryBalance(1, bay, lots, 10);
        balanceController.createInventoryBalance(balance);

        int initialSize = balanceController.getAllInventoryBalances().size();
        wagonsUnloading.unloadAllWagons();

        // Should not change anything
        assertEquals(initialSize, balanceController.getAllInventoryBalances().size());
    }

    @Test
    public void testIsBaySuitableWithEmptyLotsInBalance() {
        WarehouseBay bay = createWarehouseBay(1, 1, 1, 1, 50);
        bayController.createWarehouseBay(bay);

        // Add balance with empty lots
        List<InventoryLot> emptyLots = new ArrayList<>();
        InventoryBalance balance = new InventoryBalance(1, bay, emptyLots, 0);
        balanceController.createInventoryBalance(balance);

        // Should be suitable since no SKU exists
        boolean result = wagonsUnloading.isBaySuitable(bay, 100);
        assertTrue(result);
    }
}

