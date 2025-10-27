package app.controller;

import app.model.ReturnLine;
import app.model.ReturnOrder;
import app.model.Order;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class ReturnLineControllerTest {

    private ReturnLineController controller;
    private ReturnLine returnLine1;
    private ReturnLine returnLine2;
    private ReturnLine returnLine3;
    private ReturnOrder returnOrder1;

    @Before
    public void setUp() {
        Repositories.getInstance().getReturnLineRepository().clear();
        controller = new ReturnLineController();

        Order order1 = new Order(1, "Completed", 1, LocalDateTime.of(2025, 1, 15, 10, 0));
        returnOrder1 = new ReturnOrder(1, order1, "Damaged", "Pending", LocalDateTime.of(2025, 2, 1, 14, 30));

        returnLine1 = new ReturnLine(1, returnOrder1, "SKU001", 5, "Damaged");
        returnLine2 = new ReturnLine(2, returnOrder1, "SKU002", 3, "Good");
        returnLine3 = new ReturnLine(3, returnOrder1, "SKU003", 10, "Defective");
    }

    @Test
    public void testCreateReturnLine() {
        ReturnLine result = controller.createReturnLine(returnLine1);

        assertNotNull(result);
        assertEquals(returnLine1, result);
        assertEquals(1, controller.getAllReturnLines().size());
    }

    @Test
    public void testCreateReturnLineWithNull() {
        ReturnLine result = controller.createReturnLine(null);

        assertNull(result);
        assertEquals(0, controller.getAllReturnLines().size());
    }

    @Test
    public void testGetAllReturnLines() {
        controller.createReturnLine(returnLine1);
        controller.createReturnLine(returnLine2);
        controller.createReturnLine(returnLine3);

        List<ReturnLine> returnLines = controller.getAllReturnLines();

        assertEquals(3, returnLines.size());
        assertTrue(returnLines.contains(returnLine1));
        assertTrue(returnLines.contains(returnLine2));
        assertTrue(returnLines.contains(returnLine3));
    }

    @Test
    public void testGetAllReturnLinesEmpty() {
        List<ReturnLine> returnLines = controller.getAllReturnLines();

        assertNotNull(returnLines);
        assertEquals(0, returnLines.size());
    }

    @Test
    public void testGetReturnLineByIdFound() {
        controller.createReturnLine(returnLine1);
        controller.createReturnLine(returnLine2);

        Optional<ReturnLine> result = controller.getReturnLineById(1);

        assertTrue(result.isPresent());
        assertEquals(returnLine1, result.get());
    }

    @Test
    public void testGetReturnLineByIdNotFound() {
        controller.createReturnLine(returnLine1);

        Optional<ReturnLine> result = controller.getReturnLineById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateReturnLineSuccess() {
        controller.createReturnLine(returnLine1);

        ReturnLine updatedReturnLine = new ReturnLine(1, returnOrder1, "SKU001", 7, "Damaged");

        boolean result = controller.updateReturnLine(1, updatedReturnLine);

        assertTrue(result);
        Optional<ReturnLine> retrievedReturnLine = controller.getReturnLineById(1);
        assertTrue(retrievedReturnLine.isPresent());
        assertEquals(7, retrievedReturnLine.get().getQty().intValue());
    }

    @Test
    public void testUpdateReturnLineNotFound() {
        controller.createReturnLine(returnLine1);

        boolean result = controller.updateReturnLine(999, returnLine2);

        assertFalse(result);
    }

    @Test
    public void testDeleteReturnLineSuccess() {
        controller.createReturnLine(returnLine1);
        controller.createReturnLine(returnLine2);
        controller.createReturnLine(returnLine3);

        boolean result = controller.deleteReturnLine(2);

        assertTrue(result);
        assertEquals(2, controller.getAllReturnLines().size());
        assertFalse(controller.getReturnLineById(2).isPresent());
    }

    @Test
    public void testDeleteReturnLineNotFound() {
        controller.createReturnLine(returnLine1);

        boolean result = controller.deleteReturnLine(999);

        assertFalse(result);
        assertEquals(1, controller.getAllReturnLines().size());
    }

    @Test
    public void testGetNextReturnLineId() {
        Integer nextId = controller.getNextReturnLineId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testClearAllReturnLines() {
        controller.createReturnLine(returnLine1);
        controller.createReturnLine(returnLine2);
        controller.createReturnLine(returnLine3);
        assertEquals(3, controller.getAllReturnLines().size());

        controller.clearAllReturnLines();

        assertEquals(0, controller.getAllReturnLines().size());
    }

    @Test
    public void testConstructor() {
        ReturnLineController newController = new ReturnLineController();

        assertNotNull(newController);
        assertNotNull(newController.getAllReturnLines());
    }
}
