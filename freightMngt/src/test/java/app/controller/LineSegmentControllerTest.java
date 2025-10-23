package app.controller;

import app.model.LineSegment;
import app.model.Line;
import app.model.User;
import app.model.Company;
import app.model.TrackType;
import app.model.Gauge;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class LineSegmentControllerTest {

    private LineSegmentController controller;
    private LineSegment segment1;
    private LineSegment segment2;
    private LineSegment segment3;
    private Line line1;
    private Line line2;
    private TrackType trackType1;
    private Gauge gauge1;

    @Before
    public void setUp() {
        Repositories.getInstance().getLineSegmentRepository().clear();

        controller = new LineSegmentController();

        Company company1 = new Company(1, "Rail Company", "RC", "123456789");
        User owner1 = new User(1, "user1", "user1@rail.com", "pass123", company1, true);

        line1 = new Line(1, owner1);
        line2 = new Line(2, owner1);

        trackType1 = new TrackType(1, "Standard");
        gauge1 = new Gauge(1, "Standard Gauge", 1435.0);

        segment1 = new LineSegment(1, line1, 10.5, trackType1, true, gauge1, 80.0, 120.0, 1, 1);
        segment2 = new LineSegment(2, line1, 15.3, trackType1, true, gauge1, 80.0, 100.0, 1, 2);
        segment3 = new LineSegment(3, line2, 8.7, trackType1, false, gauge1, 70.0, 90.0, 1, 1);
    }

    @Test
    public void testCreateLineSegment() {
        LineSegment result = controller.createLineSegment(segment1);

        assertNotNull(result);
        assertEquals(segment1, result);
        assertEquals(1, controller.getAllLineSegments().size());
    }

    @Test
    public void testCreateLineSegmentWithNull() {
        LineSegment result = controller.createLineSegment(null);

        assertNull(result);
        assertEquals(0, controller.getAllLineSegments().size());
    }

    @Test
    public void testGetAllLineSegments() {
        controller.createLineSegment(segment1);
        controller.createLineSegment(segment2);
        controller.createLineSegment(segment3);

        List<LineSegment> segments = controller.getAllLineSegments();

        assertEquals(3, segments.size());
        assertTrue(segments.contains(segment1));
        assertTrue(segments.contains(segment2));
        assertTrue(segments.contains(segment3));
    }

    @Test
    public void testGetAllLineSegmentsEmpty() {
        List<LineSegment> segments = controller.getAllLineSegments();

        assertNotNull(segments);
        assertEquals(0, segments.size());
    }

    @Test
    public void testGetLineSegmentByIdFound() {
        controller.createLineSegment(segment1);
        controller.createLineSegment(segment2);

        Optional<LineSegment> result = controller.getLineSegmentById(1);

        assertTrue(result.isPresent());
        assertEquals(segment1, result.get());
    }

    @Test
    public void testGetLineSegmentByIdNotFound() {
        controller.createLineSegment(segment1);

        Optional<LineSegment> result = controller.getLineSegmentById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateLineSegmentSuccess() {
        controller.createLineSegment(segment1);

        LineSegment updatedSegment = new LineSegment(1, line1, 12.0, trackType1, true, gauge1, 85.0, 130.0, 1, 1);

        boolean result = controller.updateLineSegment(1, updatedSegment);

        assertTrue(result);
        Optional<LineSegment> retrievedSegment = controller.getLineSegmentById(1);
        assertTrue(retrievedSegment.isPresent());
        assertEquals(12.0, retrievedSegment.get().getLengthKm(), 0.01);
    }

    @Test
    public void testUpdateLineSegmentNotFound() {
        controller.createLineSegment(segment1);

        boolean result = controller.updateLineSegment(999, segment2);

        assertFalse(result);
    }

    @Test
    public void testDeleteLineSegmentSuccess() {
        controller.createLineSegment(segment1);
        controller.createLineSegment(segment2);
        controller.createLineSegment(segment3);

        boolean result = controller.deleteLineSegment(2);

        assertTrue(result);
        assertEquals(2, controller.getAllLineSegments().size());
        assertFalse(controller.getLineSegmentById(2).isPresent());
    }

    @Test
    public void testDeleteLineSegmentNotFound() {
        controller.createLineSegment(segment1);

        boolean result = controller.deleteLineSegment(999);

        assertFalse(result);
        assertEquals(1, controller.getAllLineSegments().size());
    }

    @Test
    public void testGetNextLineSegmentId() {
        Integer nextId = controller.getNextLineSegmentId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testClearAllLineSegments() {
        controller.createLineSegment(segment1);
        controller.createLineSegment(segment2);
        controller.createLineSegment(segment3);
        assertEquals(3, controller.getAllLineSegments().size());

        controller.clearAllLineSegments();

        assertEquals(0, controller.getAllLineSegments().size());
    }

    @Test
    public void testConstructor() {
        LineSegmentController newController = new LineSegmentController();

        assertNotNull(newController);
        assertNotNull(newController.getAllLineSegments());
    }
}

