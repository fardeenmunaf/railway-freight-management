package app.controller;

import app.model.QaInspection;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class QaInspectionControllerTest {

    private QaInspectionController controller;
    private QaInspection inspection1;
    private QaInspection inspection2;
    private QaInspection inspection3;

    @Before
    public void setUp() {
        Repositories.getInstance().getQaInspectionRepository().clear();
        controller = new QaInspectionController();

        inspection1 = new QaInspection(1, 1, "Passed", LocalDateTime.of(2025, 2, 5, 10, 0));
        inspection2 = new QaInspection(2, 1, "Failed", LocalDateTime.of(2025, 2, 6, 14, 30));
        inspection3 = new QaInspection(3, 2, "Passed", LocalDateTime.of(2025, 2, 7, 9, 15));
    }

    @Test
    public void testCreateQaInspection() {
        QaInspection result = controller.createQaInspection(inspection1);

        assertNotNull(result);
        assertEquals(inspection1, result);
        assertEquals(1, controller.getAllQaInspections().size());
    }

    @Test
    public void testCreateQaInspectionWithNull() {
        QaInspection result = controller.createQaInspection(null);

        assertNull(result);
        assertEquals(0, controller.getAllQaInspections().size());
    }

    @Test
    public void testGetAllQaInspections() {
        controller.createQaInspection(inspection1);
        controller.createQaInspection(inspection2);
        controller.createQaInspection(inspection3);

        List<QaInspection> inspections = controller.getAllQaInspections();

        assertEquals(3, inspections.size());
        assertTrue(inspections.contains(inspection1));
        assertTrue(inspections.contains(inspection2));
        assertTrue(inspections.contains(inspection3));
    }

    @Test
    public void testGetAllQaInspectionsEmpty() {
        List<QaInspection> inspections = controller.getAllQaInspections();

        assertNotNull(inspections);
        assertEquals(0, inspections.size());
    }

    @Test
    public void testGetQaInspectionByIdFound() {
        controller.createQaInspection(inspection1);
        controller.createQaInspection(inspection2);

        Optional<QaInspection> result = controller.getQaInspectionById(1);

        assertTrue(result.isPresent());
        assertEquals(inspection1, result.get());
    }

    @Test
    public void testGetQaInspectionByIdNotFound() {
        controller.createQaInspection(inspection1);

        Optional<QaInspection> result = controller.getQaInspectionById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateQaInspectionSuccess() {
        controller.createQaInspection(inspection1);

        QaInspection updatedInspection = new QaInspection(1, 1, "Approved", LocalDateTime.of(2025, 2, 5, 10, 0));

        boolean result = controller.updateQaInspection(1, updatedInspection);

        assertTrue(result);
        Optional<QaInspection> retrievedInspection = controller.getQaInspectionById(1);
        assertTrue(retrievedInspection.isPresent());
        assertEquals("Approved", retrievedInspection.get().getStatus());
    }

    @Test
    public void testUpdateQaInspectionNotFound() {
        controller.createQaInspection(inspection1);

        boolean result = controller.updateQaInspection(999, inspection2);

        assertFalse(result);
    }

    @Test
    public void testDeleteQaInspectionSuccess() {
        controller.createQaInspection(inspection1);
        controller.createQaInspection(inspection2);
        controller.createQaInspection(inspection3);

        boolean result = controller.deleteQaInspection(2);

        assertTrue(result);
        assertEquals(2, controller.getAllQaInspections().size());
        assertFalse(controller.getQaInspectionById(2).isPresent());
    }

    @Test
    public void testDeleteQaInspectionNotFound() {
        controller.createQaInspection(inspection1);

        boolean result = controller.deleteQaInspection(999);

        assertFalse(result);
        assertEquals(1, controller.getAllQaInspections().size());
    }

    @Test
    public void testGetNextQaInspectionId() {
        Integer nextId = controller.getNextQaInspectionId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testClearAllQaInspections() {
        controller.createQaInspection(inspection1);
        controller.createQaInspection(inspection2);
        controller.createQaInspection(inspection3);
        assertEquals(3, controller.getAllQaInspections().size());

        controller.clearAllQaInspections();

        assertEquals(0, controller.getAllQaInspections().size());
    }

    @Test
    public void testConstructor() {
        QaInspectionController newController = new QaInspectionController();

        assertNotNull(newController);
        assertNotNull(newController.getAllQaInspections());
    }
}

