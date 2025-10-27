package app.controller;

import app.model.Line;
import app.model.User;
import app.model.Company;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class LineControllerTest {

    private LineController controller;
    private Line line1;
    private Line line2;
    private Line line3;
    private User owner1;
    private User owner2;

    @Before
    public void setUp() {
        Repositories.getInstance().getLineRepository().clear();
        controller = new LineController();

        Company company1 = new Company(1, "Rail Company", "RC", "123456789");
        owner1 = new User(1, "user1", "user1@rail.com", "pass123", company1, true);
        owner2 = new User(2, "user2", "user2@rail.com", "pass456", company1, true);

        line1 = new Line(1, owner1);
        line2 = new Line(2, owner1);
        line3 = new Line(3, owner2);
    }

    @Test
    public void testCreateLine() {
        Line result = controller.createLine(line1);

        assertNotNull(result);
        assertEquals(line1, result);
        assertEquals(1, controller.getAllLines().size());
    }

    @Test
    public void testCreateLineWithNull() {
        Line result = controller.createLine(null);

        assertNull(result);
        assertEquals(0, controller.getAllLines().size());
    }

    @Test
    public void testGetAllLines() {
        controller.createLine(line1);
        controller.createLine(line2);
        controller.createLine(line3);

        List<Line> lines = controller.getAllLines();

        assertEquals(3, lines.size());
        assertTrue(lines.contains(line1));
        assertTrue(lines.contains(line2));
        assertTrue(lines.contains(line3));
    }

    @Test
    public void testGetAllLinesEmpty() {
        List<Line> lines = controller.getAllLines();

        assertNotNull(lines);
        assertEquals(0, lines.size());
    }

    @Test
    public void testGetLineByIdFound() {
        controller.createLine(line1);
        controller.createLine(line2);

        Optional<Line> result = controller.getLineById(1);

        assertTrue(result.isPresent());
        assertEquals(line1, result.get());
    }

    @Test
    public void testGetLineByIdNotFound() {
        controller.createLine(line1);

        Optional<Line> result = controller.getLineById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateLineSuccess() {
        controller.createLine(line1);

        Line updatedLine = new Line(1, owner2);

        boolean result = controller.updateLine(1, updatedLine);

        assertTrue(result);
        Optional<Line> retrievedLine = controller.getLineById(1);
        assertTrue(retrievedLine.isPresent());
        assertEquals(owner2, retrievedLine.get().getOwner());
    }

    @Test
    public void testUpdateLineNotFound() {
        controller.createLine(line1);

        boolean result = controller.updateLine(999, line2);

        assertFalse(result);
    }

    @Test
    public void testDeleteLineSuccess() {
        controller.createLine(line1);
        controller.createLine(line2);
        controller.createLine(line3);

        boolean result = controller.deleteLine(2);

        assertTrue(result);
        assertEquals(2, controller.getAllLines().size());
        assertFalse(controller.getLineById(2).isPresent());
    }

    @Test
    public void testDeleteLineNotFound() {
        controller.createLine(line1);

        boolean result = controller.deleteLine(999);

        assertFalse(result);
        assertEquals(1, controller.getAllLines().size());
    }

    @Test
    public void testGetNextLineId() {
        Integer nextId = controller.getNextLineId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testClearAllLines() {
        controller.createLine(line1);
        controller.createLine(line2);
        controller.createLine(line3);
        assertEquals(3, controller.getAllLines().size());

        controller.clearAllLines();

        assertEquals(0, controller.getAllLines().size());
    }

    @Test
    public void testConstructor() {
        LineController newController = new LineController();

        assertNotNull(newController);
        assertNotNull(newController.getAllLines());
    }
}

