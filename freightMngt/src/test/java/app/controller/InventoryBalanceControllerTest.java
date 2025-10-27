package app.controller;

import app.model.InventoryBalance;
import app.model.InventoryLot;
import app.model.WarehouseBay;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class InventoryBalanceControllerTest {

    private InventoryBalanceController controller;
    private InventoryBalance balance1;
    private InventoryBalance balance2;
    private WarehouseBay bay1;
    private WarehouseBay bay2;
    private InventoryLot lot1;
    private InventoryLot lot2;
    private InventoryLot lot3;
    private InventoryLot lot4;

    @Before
    public void setUp() {
        // Clear repository to ensure test isolation
        Repositories.getInstance().getInventoryBalanceRepository().clear();

        controller = new InventoryBalanceController();

        // Create test bays
        bay1 = new WarehouseBay(1, null, 1, 100);
        bay2 = new WarehouseBay(2, null, 2, 100);

        // Create test lots with different scenarios for sorting
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expDate1 = now.plusDays(10);
        LocalDateTime expDate2 = now.plusDays(5);
        LocalDateTime receivedDate1 = now.minusDays(5);
        LocalDateTime receivedDate2 = now.minusDays(3);

        lot1 = new InventoryLot(1, 100, 50, expDate1, receivedDate1);
        lot2 = new InventoryLot(2, 100, 30, expDate2, receivedDate1);
        lot3 = new InventoryLot(3, 100, 20, null, receivedDate1);
        lot4 = new InventoryLot(4, 100, 40, null, receivedDate2);

        List<InventoryLot> lots1 = new ArrayList<>();
        lots1.add(lot1);
        lots1.add(lot2);

        List<InventoryLot> lots2 = new ArrayList<>();
        lots2.add(lot3);
        lots2.add(lot4);

        balance1 = new InventoryBalance(1, bay1, lots1, 80);
        balance2 = new InventoryBalance(2, bay2, lots2, 60);
    }

    @Test
    public void testCreateInventoryBalance() {
        InventoryBalance result = controller.createInventoryBalance(balance1);

        assertNotNull(result);
        assertEquals(balance1, result);
        assertEquals(1, controller.getAllInventoryBalances().size());
    }

    @Test
    public void testCreateInventoryBalanceWithNull() {
        InventoryBalance result = controller.createInventoryBalance(null);

        assertNull(result);
        assertEquals(0, controller.getAllInventoryBalances().size());
    }

    @Test
    public void testGetAllInventoryBalances() {
        controller.createInventoryBalance(balance1);
        controller.createInventoryBalance(balance2);

        List<InventoryBalance> balances = controller.getAllInventoryBalances();

        assertEquals(2, balances.size());
        assertTrue(balances.contains(balance1));
        assertTrue(balances.contains(balance2));
    }

    @Test
    public void testGetAllInventoryBalancesEmpty() {
        List<InventoryBalance> balances = controller.getAllInventoryBalances();

        assertNotNull(balances);
        assertEquals(0, balances.size());
    }

    @Test
    public void testGetInventoryBalanceByIdFound() {
        controller.createInventoryBalance(balance1);
        controller.createInventoryBalance(balance2);

        Optional<InventoryBalance> result = controller.getInventoryBalanceById(1);

        assertTrue(result.isPresent());
        assertEquals(balance1, result.get());
    }

    @Test
    public void testGetInventoryBalanceByIdNotFound() {
        controller.createInventoryBalance(balance1);

        Optional<InventoryBalance> result = controller.getInventoryBalanceById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateInventoryBalanceSuccess() {
        controller.createInventoryBalance(balance1);

        List<InventoryLot> updatedLots = new ArrayList<>();
        updatedLots.add(lot1);
        InventoryBalance updatedBalance = new InventoryBalance(1, bay1, updatedLots, 50);

        boolean result = controller.updateInventoryBalance(1, updatedBalance);

        assertTrue(result);
        Optional<InventoryBalance> retrievedBalance = controller.getInventoryBalanceById(1);
        assertTrue(retrievedBalance.isPresent());
        assertEquals(50, retrievedBalance.get().getQty().intValue());
    }

    @Test
    public void testUpdateInventoryBalanceNotFound() {
        controller.createInventoryBalance(balance1);

        boolean result = controller.updateInventoryBalance(999, balance2);

        assertFalse(result);
    }

    @Test
    public void testUpdateInventoryBalanceWithSortingByExpiryDate() {
        // Create lots with different expiry dates
        LocalDateTime now = LocalDateTime.now();
        InventoryLot lotExp1 = new InventoryLot(10, 100, 50, now.plusDays(10), now.minusDays(1));
        InventoryLot lotExp2 = new InventoryLot(11, 100, 30, now.plusDays(5), now.minusDays(1));

        List<InventoryLot> unsortedLots = new ArrayList<>();
        unsortedLots.add(lotExp1); // Later expiry
        unsortedLots.add(lotExp2); // Earlier expiry

        InventoryBalance balance = new InventoryBalance(1, bay1, unsortedLots, 80);
        controller.createInventoryBalance(balance);

        // Update to trigger sorting
        controller.updateInventoryBalance(1, balance);

        Optional<InventoryBalance> result = controller.getInventoryBalanceById(1);
        assertTrue(result.isPresent());
        List<InventoryLot> sortedLots = result.get().getLots();

        // Earlier expiry should come first (FEFO)
        assertEquals(11, sortedLots.get(0).getLotId().intValue());
        assertEquals(10, sortedLots.get(1).getLotId().intValue());
    }

    @Test
    public void testUpdateInventoryBalanceWithSortingByReceivedDate() {
        // Create lots without expiry dates
        LocalDateTime now = LocalDateTime.now();
        InventoryLot lotRecv1 = new InventoryLot(20, 100, 50, null, now.minusDays(5));
        InventoryLot lotRecv2 = new InventoryLot(21, 100, 30, null, now.minusDays(2));

        List<InventoryLot> unsortedLots = new ArrayList<>();
        unsortedLots.add(lotRecv2); // More recent
        unsortedLots.add(lotRecv1); // Older

        InventoryBalance balance = new InventoryBalance(1, bay1, unsortedLots, 80);
        controller.createInventoryBalance(balance);

        // Update to trigger sorting
        controller.updateInventoryBalance(1, balance);

        Optional<InventoryBalance> result = controller.getInventoryBalanceById(1);
        assertTrue(result.isPresent());
        List<InventoryLot> sortedLots = result.get().getLots();

        // Older received date should come first (FIFO)
        assertEquals(20, sortedLots.get(0).getLotId().intValue());
        assertEquals(21, sortedLots.get(1).getLotId().intValue());
    }

    @Test
    public void testUpdateInventoryBalanceWithSortingMixedLotsExpiryFirst() {
        // Mix of lots with and without expiry dates
        LocalDateTime now = LocalDateTime.now();
        InventoryLot lotWithExp = new InventoryLot(30, 100, 50, now.plusDays(10), now.minusDays(1));
        InventoryLot lotNoExp = new InventoryLot(31, 100, 30, null, now.minusDays(1));

        List<InventoryLot> unsortedLots = new ArrayList<>();
        unsortedLots.add(lotNoExp); // No expiry
        unsortedLots.add(lotWithExp); // With expiry

        InventoryBalance balance = new InventoryBalance(1, bay1, unsortedLots, 80);
        controller.createInventoryBalance(balance);

        // Update to trigger sorting
        controller.updateInventoryBalance(1, balance);

        Optional<InventoryBalance> result = controller.getInventoryBalanceById(1);
        assertTrue(result.isPresent());
        List<InventoryLot> sortedLots = result.get().getLots();

        // Lot with expiry should come first
        assertEquals(30, sortedLots.get(0).getLotId().intValue());
        assertEquals(31, sortedLots.get(1).getLotId().intValue());
    }

    @Test
    public void testUpdateInventoryBalanceWithSortingTieBreakerByLotId() {
        // Create lots with same expiry date to test tie-breaker
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sameExpDate = now.plusDays(10);
        InventoryLot lot40 = new InventoryLot(40, 100, 50, sameExpDate, now.minusDays(1));
        InventoryLot lot41 = new InventoryLot(41, 100, 30, sameExpDate, now.minusDays(1));

        List<InventoryLot> unsortedLots = new ArrayList<>();
        unsortedLots.add(lot41); // Higher ID
        unsortedLots.add(lot40); // Lower ID

        InventoryBalance balance = new InventoryBalance(1, bay1, unsortedLots, 80);
        controller.createInventoryBalance(balance);

        // Update to trigger sorting
        controller.updateInventoryBalance(1, balance);

        Optional<InventoryBalance> result = controller.getInventoryBalanceById(1);
        assertTrue(result.isPresent());
        List<InventoryLot> sortedLots = result.get().getLots();

        // Lower lot ID should come first (tie-breaker)
        assertEquals(40, sortedLots.get(0).getLotId().intValue());
        assertEquals(41, sortedLots.get(1).getLotId().intValue());
    }

    @Test
    public void testUpdateInventoryBalanceWithSortingTieBreakerNoExpiryByLotId() {
        // Create lots without expiry and same received date to test tie-breaker
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sameRecvDate = now.minusDays(5);
        InventoryLot lot50 = new InventoryLot(50, 100, 50, null, sameRecvDate);
        InventoryLot lot51 = new InventoryLot(51, 100, 30, null, sameRecvDate);

        List<InventoryLot> unsortedLots = new ArrayList<>();
        unsortedLots.add(lot51); // Higher ID
        unsortedLots.add(lot50); // Lower ID

        InventoryBalance balance = new InventoryBalance(1, bay1, unsortedLots, 80);
        controller.createInventoryBalance(balance);

        // Update to trigger sorting
        controller.updateInventoryBalance(1, balance);

        Optional<InventoryBalance> result = controller.getInventoryBalanceById(1);
        assertTrue(result.isPresent());
        List<InventoryLot> sortedLots = result.get().getLots();

        // Lower lot ID should come first (tie-breaker)
        assertEquals(50, sortedLots.get(0).getLotId().intValue());
        assertEquals(51, sortedLots.get(1).getLotId().intValue());
    }

    @Test
    public void testUpdateInventoryBalanceWithNullBalance() {
        controller.createInventoryBalance(balance1);

        boolean result = controller.updateInventoryBalance(1, null);

        assertFalse(result);
    }

    @Test
    public void testUpdateInventoryBalanceWithEmptyLots() {
        List<InventoryLot> emptyLots = new ArrayList<>();
        InventoryBalance balanceWithEmptyLots = new InventoryBalance(1, bay1, emptyLots, 0);
        controller.createInventoryBalance(balanceWithEmptyLots);

        boolean result = controller.updateInventoryBalance(1, balanceWithEmptyLots);

        assertTrue(result);
        Optional<InventoryBalance> retrievedBalance = controller.getInventoryBalanceById(1);
        assertTrue(retrievedBalance.isPresent());
        assertEquals(0, retrievedBalance.get().getLots().size());
    }

    @Test
    public void testDeleteInventoryBalanceSuccess() {
        controller.createInventoryBalance(balance1);
        controller.createInventoryBalance(balance2);

        boolean result = controller.deleteInventoryBalance(1);

        assertTrue(result);
        assertEquals(1, controller.getAllInventoryBalances().size());
        assertFalse(controller.getInventoryBalanceById(1).isPresent());
    }

    @Test
    public void testDeleteInventoryBalanceNotFound() {
        controller.createInventoryBalance(balance1);

        boolean result = controller.deleteInventoryBalance(999);

        assertFalse(result);
        assertEquals(1, controller.getAllInventoryBalances().size());
    }

    @Test
    public void testGetNextBalanceId() {
        Integer nextId = controller.getNextBalanceId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testConstructor() {
        InventoryBalanceController newController = new InventoryBalanceController();

        assertNotNull(newController);
        assertNotNull(newController.getAllInventoryBalances());
    }

    @Test
    public void testClearAllInventoryBalances() {
        controller.createInventoryBalance(balance1);
        controller.createInventoryBalance(balance2);
        assertEquals(2, controller.getAllInventoryBalances().size());

        controller.clearAllInventoryBalances();

        assertEquals(0, controller.getAllInventoryBalances().size());
    }
}

