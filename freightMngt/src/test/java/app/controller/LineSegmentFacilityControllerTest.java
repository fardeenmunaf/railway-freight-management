package app.controller;

import app.model.LineSegmentFacility;
import app.model.LineSegment;
import app.model.Line;
import app.model.Facility;
import app.model.User;
import app.model.Company;
import app.model.TrackType;
import app.model.Gauge;
import app.model.FacilityType;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class LineSegmentFacilityControllerTest {

    private LineSegmentFacilityController controller;
    private LineSegmentFacility lsf1;
    private LineSegmentFacility lsf2;
    private LineSegmentFacility lsf3;
    private LineSegment segment1;
    private LineSegment segment2;
    private Facility facility1;
    private Facility facility2;

    @Before
    public void setUp() {
        Repositories.getInstance().getLineSegmentFacilityRepository().clear();

        controller = new LineSegmentFacilityController();

        Company company1 = new Company(1, "Rail Company", "RC", "123456789");
        User owner1 = new User(1, "user1", "user1@rail.com", "pass123", company1, true);

        Line line1 = new Line(1, owner1);

        TrackType trackType1 = new TrackType(1, "Standard");
        Gauge gauge1 = new Gauge(1, "Standard Gauge", 1435.0);

        segment1 = new LineSegment(1, line1, 10.5, trackType1, true, gauge1, 80.0, 120.0, 1, 1);
        segment2 = new LineSegment(2, line1, 15.3, trackType1, true, gauge1, 80.0, 100.0, 1, 2);

        FacilityType facilityType1 = new FacilityType(1, "Station");
        facility1 = new Facility(1, "Station A", facilityType1, 41.1579, -8.6291, false);
        facility2 = new Facility(2, "Station B", facilityType1, 41.1679, -8.6391, false);

        lsf1 = new LineSegmentFacility(1, segment1, facility1);
        lsf2 = new LineSegmentFacility(2, segment1, facility2);
        lsf3 = new LineSegmentFacility(3, segment2, facility1);
    }

    @Test
    public void testCreateLineSegmentFacility() {
        LineSegmentFacility result = controller.createLineSegmentFacility(lsf1);

        assertNotNull(result);
        assertEquals(lsf1, result);
        assertEquals(1, controller.getAllLineSegmentFacilities().size());
    }

    @Test
    public void testCreateLineSegmentFacilityWithNull() {
        LineSegmentFacility result = controller.createLineSegmentFacility(null);

        assertNull(result);
        assertEquals(0, controller.getAllLineSegmentFacilities().size());
    }

    @Test
    public void testGetAllLineSegmentFacilities() {
        controller.createLineSegmentFacility(lsf1);
        controller.createLineSegmentFacility(lsf2);
        controller.createLineSegmentFacility(lsf3);

        List<LineSegmentFacility> lsfs = controller.getAllLineSegmentFacilities();

        assertEquals(3, lsfs.size());
        assertTrue(lsfs.contains(lsf1));
        assertTrue(lsfs.contains(lsf2));
        assertTrue(lsfs.contains(lsf3));
    }

    @Test
    public void testGetAllLineSegmentFacilitiesEmpty() {
        List<LineSegmentFacility> lsfs = controller.getAllLineSegmentFacilities();

        assertNotNull(lsfs);
        assertEquals(0, lsfs.size());
    }

    @Test
    public void testGetLineSegmentFacilitiesByLineSegment() {
        controller.createLineSegmentFacility(lsf1);
        controller.createLineSegmentFacility(lsf2);
        controller.createLineSegmentFacility(lsf3);

        List<LineSegmentFacility> result = controller.getLineSegmentFacilitiesByLineSegment(1);

        assertEquals(2, result.size());
        assertTrue(result.contains(lsf1));
        assertTrue(result.contains(lsf2));
        assertFalse(result.contains(lsf3));
    }

    @Test
    public void testGetLineSegmentFacilitiesByLineSegmentNotFound() {
        controller.createLineSegmentFacility(lsf1);

        List<LineSegmentFacility> result = controller.getLineSegmentFacilitiesByLineSegment(999);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testDeleteLineSegmentFacilitySuccess() {
        controller.createLineSegmentFacility(lsf1);
        controller.createLineSegmentFacility(lsf2);
        controller.createLineSegmentFacility(lsf3);

        boolean result = controller.deleteLineSegmentFacility(1, 2);

        assertTrue(result);
        assertEquals(2, controller.getAllLineSegmentFacilities().size());
    }

    @Test
    public void testDeleteLineSegmentFacilityNotFound() {
        controller.createLineSegmentFacility(lsf1);

        boolean result = controller.deleteLineSegmentFacility(999, 999);

        assertFalse(result);
        assertEquals(1, controller.getAllLineSegmentFacilities().size());
    }

    @Test
    public void testClearAllLineSegmentFacilities() {
        controller.createLineSegmentFacility(lsf1);
        controller.createLineSegmentFacility(lsf2);
        controller.createLineSegmentFacility(lsf3);
        assertEquals(3, controller.getAllLineSegmentFacilities().size());

        controller.clearAllLineSegmentFacilities();

        assertEquals(0, controller.getAllLineSegmentFacilities().size());
    }

    @Test
    public void testConstructor() {
        LineSegmentFacilityController newController = new LineSegmentFacilityController();

        assertNotNull(newController);
        assertNotNull(newController.getAllLineSegmentFacilities());
    }
}

