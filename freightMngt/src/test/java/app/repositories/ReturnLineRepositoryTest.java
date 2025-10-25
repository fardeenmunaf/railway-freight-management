package app.repositories;

import app.model.ReturnLine;
import app.model.ReturnOrder;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for ReturnLineRepository.
 * Tests all public methods including CRUD operations, search by return ID, and edge cases.
 */
public class ReturnLineRepositoryTest {

    private ReturnLineRepository repository;
    private ReturnLine returnLine1;
    private ReturnLine returnLine2;
    private ReturnLine returnLine3;
    private ReturnOrder returnOrder1;
    private ReturnOrder returnOrder2;

    @Before
    public void setUp() {
        repository = new ReturnLineRepository();

        returnOrder1 = new ReturnOrder(1, null, "damaged", "received", LocalDateTime.now());
        returnOrder2 = new ReturnOrder(2, null, "customer-remorse", "received", LocalDateTime.now());

        returnLine1 = new ReturnLine(1, returnOrder1, "SKU001", 5, "damaged");
        returnLine2 = new ReturnLine(2, returnOrder1, "SKU002", 10, "damaged");
        returnLine3 = new ReturnLine(3, returnOrder2, "SKU003", 3, "good");
    }

    @Test
    public void testAddReturnLine() {
        repository.addReturnLine(returnLine1);

        List<ReturnLine> returnLines = repository.getAllReturnLines();
        assertEquals(1, returnLines.size());
        assertTrue(returnLines.contains(returnLine1));
    }

    @Test
    public void testAddReturnLineWithNull() {
        repository.addReturnLine(null);

        List<ReturnLine> returnLines = repository.getAllReturnLines();
        assertEquals(0, returnLines.size());
    }

    @Test
    public void testAddReturnLineWithNullId() {
        ReturnLine returnLineWithNullId = new ReturnLine(null, returnOrder1, "SKU001", 5, "good");
        repository.addReturnLine(returnLineWithNullId);

        List<ReturnLine> returnLines = repository.getAllReturnLines();
        assertEquals(0, returnLines.size());
    }

    @Test
    public void testAddDuplicateReturnLine() {
        repository.addReturnLine(returnLine1);
        repository.addReturnLine(returnLine1);

        List<ReturnLine> returnLines = repository.getAllReturnLines();
        assertEquals(1, returnLines.size());
    }

    @Test
    public void testGetAllReturnLines() {
        repository.addReturnLine(returnLine1);
        repository.addReturnLine(returnLine2);
        repository.addReturnLine(returnLine3);

        List<ReturnLine> returnLines = repository.getAllReturnLines();

        assertEquals(3, returnLines.size());
        assertTrue(returnLines.contains(returnLine1));
        assertTrue(returnLines.contains(returnLine2));
        assertTrue(returnLines.contains(returnLine3));
    }

    @Test
    public void testGetAllReturnLinesEmpty() {
        List<ReturnLine> returnLines = repository.getAllReturnLines();

        assertNotNull(returnLines);
        assertEquals(0, returnLines.size());
    }

    @Test
    public void testGetReturnLineByIdFound() {
        repository.addReturnLine(returnLine1);
        repository.addReturnLine(returnLine2);

        ReturnLine result = repository.getReturnLineById(1);

        assertNotNull(result);
        assertEquals(returnLine1, result);
    }

    @Test
    public void testGetReturnLineByIdNotFound() {
        repository.addReturnLine(returnLine1);

        ReturnLine result = repository.getReturnLineById(999);

        assertNull(result);
    }

    @Test
    public void testGetReturnLinesByReturnId() {
        repository.addReturnLine(returnLine1);
        repository.addReturnLine(returnLine2);
        repository.addReturnLine(returnLine3);

        List<ReturnLine> result = repository.getReturnLinesByReturnId(1);

        assertEquals(2, result.size());
        assertTrue(result.contains(returnLine1));
        assertTrue(result.contains(returnLine2));
        assertFalse(result.contains(returnLine3));
    }

    @Test
    public void testGetReturnLinesByReturnIdEmpty() {
        repository.addReturnLine(returnLine1);

        List<ReturnLine> result = repository.getReturnLinesByReturnId(999);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testGetReturnLinesByReturnIdWithNullReturnOrder() {
        ReturnLine returnLineWithNullOrder = new ReturnLine(4, null, "SKU004", 2, "good");
        repository.addReturnLine(returnLineWithNullOrder);
        repository.addReturnLine(returnLine1);

        List<ReturnLine> result = repository.getReturnLinesByReturnId(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(returnLine1));
        assertFalse(result.contains(returnLineWithNullOrder));
    }

    @Test
    public void testUpdateReturnLineSuccess() {
        repository.addReturnLine(returnLine1);

        ReturnLine updatedReturnLine = new ReturnLine(1, returnOrder1, "SKU001", 15, "good");

        boolean result = repository.updateReturnLine(1, updatedReturnLine);

        assertTrue(result);
        ReturnLine retrievedReturnLine = repository.getReturnLineById(1);
        assertNotNull(retrievedReturnLine);
        assertEquals(Integer.valueOf(15), retrievedReturnLine.getQty());
        assertEquals("good", retrievedReturnLine.getCondition());
    }

    @Test
    public void testUpdateReturnLineNotFound() {
        repository.addReturnLine(returnLine1);

        boolean result = repository.updateReturnLine(999, returnLine2);

        assertFalse(result);
    }

    @Test
    public void testDeleteReturnLineSuccess() {
        repository.addReturnLine(returnLine1);
        repository.addReturnLine(returnLine2);
        repository.addReturnLine(returnLine3);

        boolean result = repository.deleteReturnLine(2);

        assertTrue(result);
        assertEquals(2, repository.getAllReturnLines().size());
        assertNull(repository.getReturnLineById(2));
    }

    @Test
    public void testDeleteReturnLineNotFound() {
        repository.addReturnLine(returnLine1);

        boolean result = repository.deleteReturnLine(999);

        assertFalse(result);
        assertEquals(1, repository.getAllReturnLines().size());
    }

    @Test
    public void testGetNextReturnLineId() {
        repository.addReturnLine(returnLine1);
        repository.addReturnLine(returnLine2);

        Integer nextId = repository.getNextReturnLineId();

        assertNotNull(nextId);
        assertEquals(Integer.valueOf(3), nextId);
    }

    @Test
    public void testGetNextReturnLineIdEmpty() {
        Integer nextId = repository.getNextReturnLineId();

        assertNotNull(nextId);
        assertEquals(Integer.valueOf(1), nextId);
    }

    @Test
    public void testGetNextReturnLineIdWithNullIds() {
        ReturnLine returnLineWithNullId = new ReturnLine(null, returnOrder1, "SKU001", 5, "good");
        repository.addReturnLine(returnLineWithNullId);

        Integer nextId = repository.getNextReturnLineId();

        assertNotNull(nextId);
        assertEquals(Integer.valueOf(1), nextId);
    }

    @Test
    public void testClear() {
        repository.addReturnLine(returnLine1);
        repository.addReturnLine(returnLine2);
        repository.addReturnLine(returnLine3);
        assertEquals(3, repository.getAllReturnLines().size());

        repository.clear();

        assertEquals(0, repository.getAllReturnLines().size());
    }

    @Test
    public void testConstructor() {
        ReturnLineRepository newRepository = new ReturnLineRepository();

        assertNotNull(newRepository);
        assertNotNull(newRepository.getAllReturnLines());
    }
}
