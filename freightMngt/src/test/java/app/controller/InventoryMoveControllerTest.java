package app.controller;

import app.model.InventoryMove;
import app.model.InventoryLot;
import app.model.WarehouseBay;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class InventoryMoveControllerTest {

    private InventoryMoveController controller;
    private InventoryMove move1;
    private InventoryMove move2;
    private InventoryLot lot1;
    private InventoryLot lot2;
    private WarehouseBay bay1;
    private WarehouseBay bay2;
    private WarehouseBay bay3;

    @Before
    public void setUp() {
        // Clear repository to ensure test isolation
        Repositories.getInstance().getInventoryMoveRepository().clear();

        controller = new InventoryMoveController();

        // Create test data
        LocalDateTime now = LocalDateTime.now();
        lot1 = new InventoryLot(1, 100, 50, now.plusDays(30), now.minusDays(5));
        lot2 = new InventoryLot(2, 200, 75, now.plusDays(60), now.minusDays(3));

        bay1 = new WarehouseBay(1, null, 1, 100);
        bay2 = new WarehouseBay(2, null, 2, 100);
        bay3 = new WarehouseBay(3, null, 3, 100);

        move1 = new InventoryMove(1, lot1, bay1, bay2, 25, "Replenishment", now);
        move2 = new InventoryMove(2, lot2, bay2, bay3, 30, "Relocation", now.plusHours(1));
    }

    @Test
    public void testCreateInventoryMove() {
        InventoryMove result = controller.createInventoryMove(move1);

        assertNotNull(result);
        assertEquals(move1, result);
        assertEquals(1, controller.getAllInventoryMoves().size());
    }

    @Test
    public void testCreateInventoryMoveWithNull() {
        InventoryMove result = controller.createInventoryMove(null);

        assertNull(result);
        assertEquals(0, controller.getAllInventoryMoves().size());
    }

    @Test
    public void testGetAllInventoryMoves() {
        controller.createInventoryMove(move1);
        controller.createInventoryMove(move2);

        List<InventoryMove> moves = controller.getAllInventoryMoves();

        assertEquals(2, moves.size());
        assertTrue(moves.contains(move1));
        assertTrue(moves.contains(move2));
    }

    @Test
    public void testGetAllInventoryMovesEmpty() {
        List<InventoryMove> moves = controller.getAllInventoryMoves();

        assertNotNull(moves);
        assertEquals(0, moves.size());
    }

    @Test
    public void testGetInventoryMoveByIdFound() {
        controller.createInventoryMove(move1);
        controller.createInventoryMove(move2);

        Optional<InventoryMove> result = controller.getInventoryMoveById(1);

        assertTrue(result.isPresent());
        assertEquals(move1, result.get());
    }

    @Test
    public void testGetInventoryMoveByIdNotFound() {
        controller.createInventoryMove(move1);

        Optional<InventoryMove> result = controller.getInventoryMoveById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateInventoryMoveSuccess() {
        controller.createInventoryMove(move1);

        LocalDateTime now = LocalDateTime.now();
        InventoryMove updatedMove = new InventoryMove(1, lot1, bay1, bay2, 30, "Updated Reason", now);

        boolean result = controller.updateInventoryMove(1, updatedMove);

        assertTrue(result);
        Optional<InventoryMove> retrievedMove = controller.getInventoryMoveById(1);
        assertTrue(retrievedMove.isPresent());
        assertEquals(30, retrievedMove.get().getQty().intValue());
        assertEquals("Updated Reason", retrievedMove.get().getReason());
    }

    @Test
    public void testUpdateInventoryMoveNotFound() {
        controller.createInventoryMove(move1);

        boolean result = controller.updateInventoryMove(999, move2);

        assertFalse(result);
    }

    @Test
    public void testUpdateInventoryMoveWithNull() {
        controller.createInventoryMove(move1);

        boolean result = controller.updateInventoryMove(1, null);

        assertFalse(result);
    }

    @Test
    public void testDeleteInventoryMoveSuccess() {
        controller.createInventoryMove(move1);
        controller.createInventoryMove(move2);

        boolean result = controller.deleteInventoryMove(1);

        assertTrue(result);
        assertEquals(1, controller.getAllInventoryMoves().size());
        assertFalse(controller.getInventoryMoveById(1).isPresent());
    }

    @Test
    public void testDeleteInventoryMoveNotFound() {
        controller.createInventoryMove(move1);

        boolean result = controller.deleteInventoryMove(999);

        assertFalse(result);
        assertEquals(1, controller.getAllInventoryMoves().size());
    }

    @Test
    public void testGetNextInventoryMoveId() {
        Integer nextId = controller.getNextInventoryMoveId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testConstructor() {
        InventoryMoveController newController = new InventoryMoveController();

        assertNotNull(newController);
        assertNotNull(newController.getAllInventoryMoves());
    }

    @Test
    public void testClearAllInventoryMoves() {
        controller.createInventoryMove(move1);
        controller.createInventoryMove(move2);
        assertEquals(2, controller.getAllInventoryMoves().size());

        controller.clearAllInventoryMoves();

        assertEquals(0, controller.getAllInventoryMoves().size());
    }

    @Test
    public void testMultipleInventoryMoveOperations() {
        // Create multiple moves
        controller.createInventoryMove(move1);
        controller.createInventoryMove(move2);
        assertEquals(2, controller.getAllInventoryMoves().size());

        // Get next ID
        Integer nextId = controller.getNextInventoryMoveId();
        assertNotNull(nextId);

        // Create another move with next ID
        LocalDateTime now = LocalDateTime.now();
        InventoryMove move3 = new InventoryMove(nextId, lot1, bay3, bay1, 15, "Return", now);
        controller.createInventoryMove(move3);
        assertEquals(3, controller.getAllInventoryMoves().size());

        // Update one
        LocalDateTime updateTime = now.plusHours(2);
        InventoryMove updatedMove = new InventoryMove(2, lot2, bay2, bay3, 35, "Updated Relocation", updateTime);
        assertTrue(controller.updateInventoryMove(2, updatedMove));

        // Delete one
        assertTrue(controller.deleteInventoryMove(1));
        assertEquals(2, controller.getAllInventoryMoves().size());

        // Verify final state
        assertFalse(controller.getInventoryMoveById(1).isPresent());
        assertTrue(controller.getInventoryMoveById(2).isPresent());
        assertTrue(controller.getInventoryMoveById(nextId).isPresent());
    }
}

