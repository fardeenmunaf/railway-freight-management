package app.controller;

import app.model.QuarantineHold;
import app.model.ReturnOrder;
import app.model.Order;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class QuarantineHoldControllerTest {

    private QuarantineHoldController controller;
    private QuarantineHold hold1;
    private QuarantineHold hold2;
    private QuarantineHold hold3;
    private ReturnOrder returnOrder1;

    @Before
    public void setUp() {
        Repositories.getInstance().getQuarantineHoldRepository().clear();
        controller = new QuarantineHoldController();

        Order order1 = new Order(1, "Completed", 1, LocalDateTime.of(2025, 1, 15, 10, 0));
        returnOrder1 = new ReturnOrder(1, order1, "Damaged", "Pending", LocalDateTime.of(2025, 2, 1, 14, 30));

        hold1 = new QuarantineHold(1, returnOrder1, "Active", LocalDateTime.of(2025, 2, 2, 10, 0), null);
        hold2 = new QuarantineHold(2, returnOrder1, "Released", LocalDateTime.of(2025, 2, 3, 9, 0), LocalDateTime.of(2025, 2, 5, 15, 0));
        hold3 = new QuarantineHold(3, returnOrder1, "Active", LocalDateTime.of(2025, 2, 4, 11, 30), null);
    }

    @Test
    public void testCreateQuarantineHold() {
        QuarantineHold result = controller.createQuarantineHold(hold1);

        assertNotNull(result);
        assertEquals(hold1, result);
        assertEquals(1, controller.getAllQuarantineHolds().size());
    }

    @Test
    public void testCreateQuarantineHoldWithNull() {
        QuarantineHold result = controller.createQuarantineHold(null);

        assertNull(result);
        assertEquals(0, controller.getAllQuarantineHolds().size());
    }

    @Test
    public void testGetAllQuarantineHolds() {
        controller.createQuarantineHold(hold1);
        controller.createQuarantineHold(hold2);
        controller.createQuarantineHold(hold3);

        List<QuarantineHold> holds = controller.getAllQuarantineHolds();

        assertEquals(3, holds.size());
        assertTrue(holds.contains(hold1));
        assertTrue(holds.contains(hold2));
        assertTrue(holds.contains(hold3));
    }

    @Test
    public void testGetAllQuarantineHoldsEmpty() {
        List<QuarantineHold> holds = controller.getAllQuarantineHolds();

        assertNotNull(holds);
        assertEquals(0, holds.size());
    }

    @Test
    public void testGetQuarantineHoldByIdFound() {
        controller.createQuarantineHold(hold1);
        controller.createQuarantineHold(hold2);

        Optional<QuarantineHold> result = controller.getQuarantineHoldById(1);

        assertTrue(result.isPresent());
        assertEquals(hold1, result.get());
    }

    @Test
    public void testGetQuarantineHoldByIdNotFound() {
        controller.createQuarantineHold(hold1);

        Optional<QuarantineHold> result = controller.getQuarantineHoldById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateQuarantineHoldSuccess() {
        controller.createQuarantineHold(hold1);

        QuarantineHold updatedHold = new QuarantineHold(1, returnOrder1, "Released", LocalDateTime.of(2025, 2, 2, 10, 0), LocalDateTime.of(2025, 2, 6, 14, 0));

        boolean result = controller.updateQuarantineHold(1, updatedHold);

        assertTrue(result);
        Optional<QuarantineHold> retrievedHold = controller.getQuarantineHoldById(1);
        assertTrue(retrievedHold.isPresent());
        assertEquals("Released", retrievedHold.get().getStatus());
    }

    @Test
    public void testUpdateQuarantineHoldNotFound() {
        controller.createQuarantineHold(hold1);

        boolean result = controller.updateQuarantineHold(999, hold2);

        assertFalse(result);
    }

    @Test
    public void testDeleteQuarantineHoldSuccess() {
        controller.createQuarantineHold(hold1);
        controller.createQuarantineHold(hold2);
        controller.createQuarantineHold(hold3);

        boolean result = controller.deleteQuarantineHold(2);

        assertTrue(result);
        assertEquals(2, controller.getAllQuarantineHolds().size());
        assertFalse(controller.getQuarantineHoldById(2).isPresent());
    }

    @Test
    public void testDeleteQuarantineHoldNotFound() {
        controller.createQuarantineHold(hold1);

        boolean result = controller.deleteQuarantineHold(999);

        assertFalse(result);
        assertEquals(1, controller.getAllQuarantineHolds().size());
    }

    @Test
    public void testGetNextQuarantineHoldId() {
        Integer nextId = controller.getNextQuarantineHoldId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testClearAllQuarantineHolds() {
        controller.createQuarantineHold(hold1);
        controller.createQuarantineHold(hold2);
        controller.createQuarantineHold(hold3);
        assertEquals(3, controller.getAllQuarantineHolds().size());

        controller.clearAllQuarantineHolds();

        assertEquals(0, controller.getAllQuarantineHolds().size());
    }

    @Test
    public void testConstructor() {
        QuarantineHoldController newController = new QuarantineHoldController();

        assertNotNull(newController);
        assertNotNull(newController.getAllQuarantineHolds());
    }
}

