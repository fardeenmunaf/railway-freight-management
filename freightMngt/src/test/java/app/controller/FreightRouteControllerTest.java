package app.controller;

import app.model.*;
import app.model.FreightRoute.RouteType;
import app.repositories.Repositories;
import app.controller.FreightRouteController.SegmentDefinition;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Test class for FreightRouteController.
 * Tests route creation, validation, and management functionality.
 */
public class FreightRouteControllerTest {

    private FreightRouteController routeController;
    private Facility facility1;
    private Facility facility2;
    private Facility facility3;
    private Facility facility4;
    private LineSegment lineSegment1;
    private LineSegment lineSegment2;
    private LineSegment lineSegment3;
    private Line line;

    @Before
    public void setUp() {
        // Clear repositories
        Repositories.getInstance().getFreightRouteRepository().clear();
        Repositories.getInstance().getFacilityRepository().clear();
        Repositories.getInstance().getLineSegmentRepository().clear();
        Repositories.getInstance().getLineRepository().clear();

        // Initialize controller
        routeController = new FreightRouteController();

        // Create test facilities
        FacilityType facilityType = new FacilityType(1, "Station");
        facility1 = new Facility(1, "Station A", facilityType, 40.7128, -74.0060, false);
        facility2 = new Facility(2, "Station B", facilityType, 41.8781, -87.6298, false);
        facility3 = new Facility(3, "Station C", facilityType, 34.0522, -118.2437, false);
        facility4 = new Facility(4, "Station D", facilityType, 37.7749, -122.4194, false);

        Repositories.getInstance().getFacilityRepository().addFacility(facility1);
        Repositories.getInstance().getFacilityRepository().addFacility(facility2);
        Repositories.getInstance().getFacilityRepository().addFacility(facility3);
        Repositories.getInstance().getFacilityRepository().addFacility(facility4);

        // Create test line
        Company company = new Company(1, "Test Railway", "TR", "VAT123");
        User owner = new User(1, "Test User", "test@example.com", "password", company, true);
        line = new Line(1, owner);
        Repositories.getInstance().getLineRepository().addLine(line);

        // Create test line segments
        TrackType trackType = new TrackType(1, "Standard");
        Gauge gauge = new Gauge(1, "Standard", 1435.0);

        lineSegment1 = new LineSegment(1, line, 150.0, trackType, true, gauge, 20.0, 120.0, 2, 1);
        lineSegment2 = new LineSegment(2, line, 200.0, trackType, true, gauge, 20.0, 120.0, 2, 2);
        lineSegment3 = new LineSegment(3, line, 180.0, trackType, true, gauge, 20.0, 120.0, 2, 3);

        Repositories.getInstance().getLineSegmentRepository().addLineSegment(lineSegment1);
        Repositories.getInstance().getLineSegmentRepository().addLineSegment(lineSegment2);
        Repositories.getInstance().getLineSegmentRepository().addLineSegment(lineSegment3);
    }

    @Test
    public void testCreateSimpleRoute() {
        FreightRoute route = routeController.createSimpleRoute(1, "Simple Route A-B",
                1, 2, "Direct route from A to B");

        assertNotNull(route);
        assertEquals(Integer.valueOf(1), route.getRouteId());
        assertEquals("Simple Route A-B", route.getRouteName());
        assertEquals(RouteType.SIMPLE, route.getRouteType());
        assertEquals("Station A", route.getStartFacility().getName());
        assertEquals("Station B", route.getEndFacility().getName());
        assertTrue(route.getRouteSegments().isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateSimpleRoute_InvalidStartFacility() {
        routeController.createSimpleRoute(1, "Invalid Route", 999, 2, "Invalid start");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateSimpleRoute_InvalidEndFacility() {
        routeController.createSimpleRoute(1, "Invalid Route", 1, 999, "Invalid end");
    }

    @Test
    public void testCreateComplexRoute() {
        List<SegmentDefinition> segments = new ArrayList<>();
        segments.add(new SegmentDefinition(1, 1, 2, 1, "Segment A-B"));
        segments.add(new SegmentDefinition(2, 2, 3, 2, "Segment B-C"));
        segments.add(new SegmentDefinition(3, 3, 4, 3, "Segment C-D"));

        FreightRoute route = routeController.createComplexRoute(1, "Complex Route A-D",
                1, 4, segments, "Multi-stop route from A to D");

        assertNotNull(route);
        assertEquals(Integer.valueOf(1), route.getRouteId());
        assertEquals("Complex Route A-D", route.getRouteName());
        assertEquals(RouteType.COMPLEX, route.getRouteType());
        assertEquals("Station A", route.getStartFacility().getName());
        assertEquals("Station D", route.getEndFacility().getName());
        assertEquals(3, route.getRouteSegments().size());

        // Verify total distance calculation
        double expectedDistance = 150.0 + 200.0 + 180.0; // Sum of segment lengths
        assertEquals(expectedDistance, route.getTotalDistanceKm(), 0.01);
    }

    @Test
    public void testCreateComplexRoute_WithOrderedSegments() {
        List<SegmentDefinition> segments = new ArrayList<>();
        segments.add(new SegmentDefinition(1, 1, 2, 1, "First segment"));
        segments.add(new SegmentDefinition(2, 2, 3, 2, "Second segment"));

        FreightRoute route = routeController.createComplexRoute(1, "Ordered Route",
                1, 3, segments, "Route with ordered segments");

        List<FreightRouteSegment> routeSegments = route.getRouteSegments();
        assertEquals(2, routeSegments.size());
        assertEquals(Integer.valueOf(1), routeSegments.get(0).getSequenceOrder());
        assertEquals(Integer.valueOf(2), routeSegments.get(1).getSequenceOrder());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateComplexRoute_InvalidFacility() {
        List<SegmentDefinition> segments = new ArrayList<>();
        segments.add(new SegmentDefinition(1, 1, 999, 1, "Invalid segment"));

        routeController.createComplexRoute(1, "Invalid Route", 1, 2, segments, "Invalid");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateComplexRoute_InvalidLineSegment() {
        List<SegmentDefinition> segments = new ArrayList<>();
        segments.add(new SegmentDefinition(1, 1, 2, 999, "Invalid line segment"));

        routeController.createComplexRoute(1, "Invalid Route", 1, 2, segments, "Invalid");
    }

    @Test
    public void testGetFreightRouteById() {
        routeController.createSimpleRoute(1, "Test Route", 1, 2, "Test");

        Optional<FreightRoute> result = routeController.getFreightRouteById(1);

        assertTrue(result.isPresent());
        assertEquals("Test Route", result.get().getRouteName());
    }

    @Test
    public void testGetFreightRouteById_NotFound() {
        Optional<FreightRoute> result = routeController.getFreightRouteById(999);
        assertFalse(result.isPresent());
    }

    @Test
    public void testGetFreightRouteByName() {
        routeController.createSimpleRoute(1, "Unique Route Name", 1, 2, "Test");

        Optional<FreightRoute> result = routeController.getFreightRouteByName("Unique Route Name");

        assertTrue(result.isPresent());
        assertEquals(Integer.valueOf(1), result.get().getRouteId());
    }

    @Test
    public void testGetAllFreightRoutes() {
        routeController.createSimpleRoute(1, "Route 1", 1, 2, "Test 1");
        routeController.createSimpleRoute(2, "Route 2", 2, 3, "Test 2");

        List<FreightRoute> routes = routeController.getAllFreightRoutes();

        assertEquals(2, routes.size());
    }

    @Test
    public void testGetSimpleRoutes() {
        routeController.createSimpleRoute(1, "Simple Route", 1, 2, "Simple");

        List<SegmentDefinition> segments = new ArrayList<>();
        segments.add(new SegmentDefinition(1, 2, 3, 2, "Segment"));
        routeController.createComplexRoute(2, "Complex Route", 2, 3, segments, "Complex");

        List<FreightRoute> simpleRoutes = routeController.getSimpleRoutes();

        assertEquals(1, simpleRoutes.size());
        assertEquals(RouteType.SIMPLE, simpleRoutes.get(0).getRouteType());
    }

    @Test
    public void testGetComplexRoutes() {
        routeController.createSimpleRoute(1, "Simple Route", 1, 2, "Simple");

        List<SegmentDefinition> segments = new ArrayList<>();
        segments.add(new SegmentDefinition(1, 2, 3, 2, "Segment"));
        routeController.createComplexRoute(2, "Complex Route", 2, 3, segments, "Complex");

        List<FreightRoute> complexRoutes = routeController.getComplexRoutes();

        assertEquals(1, complexRoutes.size());
        assertEquals(RouteType.COMPLEX, complexRoutes.get(0).getRouteType());
    }

    @Test
    public void testUpdateFreightRoute() {
        FreightRoute route = routeController.createSimpleRoute(1, "Original Name", 1, 2, "Test");

        route.setRouteName("Updated Name");
        boolean result = routeController.updateFreightRoute(1, route);

        assertTrue(result);
        Optional<FreightRoute> updated = routeController.getFreightRouteById(1);
        assertTrue(updated.isPresent());
        assertEquals("Updated Name", updated.get().getRouteName());
    }

    @Test
    public void testDeleteFreightRoute() {
        routeController.createSimpleRoute(1, "Test Route", 1, 2, "Test");

        boolean result = routeController.deleteFreightRoute(1);

        assertTrue(result);
        Optional<FreightRoute> deleted = routeController.getFreightRouteById(1);
        assertFalse(deleted.isPresent());
    }

    @Test
    public void testGetNextFreightRouteId() {
        routeController.createSimpleRoute(1, "Route 1", 1, 2, "Test");
        routeController.createSimpleRoute(5, "Route 2", 2, 3, "Test");

        Integer nextId = routeController.getNextFreightRouteId();

        assertEquals(Integer.valueOf(6), nextId);
    }

    @Test
    public void testContainsRouteName() {
        routeController.createSimpleRoute(1, "Unique Route", 1, 2, "Test");

        assertTrue(routeController.containsRouteName("Unique Route"));
        assertFalse(routeController.containsRouteName("Non-existent Route"));
    }

    @Test
    public void testGetRouteSummary() {
        List<SegmentDefinition> segments = new ArrayList<>();
        segments.add(new SegmentDefinition(1, 1, 2, 1, "Segment 1"));
        segments.add(new SegmentDefinition(2, 2, 3, 2, "Segment 2"));

        routeController.createComplexRoute(1, "Test Route",
                1, 3, segments, "Multi-stop route");

        String summary = routeController.getRouteSummary(1);

        assertNotNull(summary);
        assertTrue(summary.contains("Test Route"));
        assertTrue(summary.contains("Station A"));
        assertTrue(summary.contains("Station B"));
        assertTrue(summary.contains("Station C"));
        assertTrue(summary.contains("Total Distance"));
    }

    @Test
    public void testGetRouteSummary_NotFound() {
        String summary = routeController.getRouteSummary(999);
        assertNull(summary);
    }

    @Test
    public void testComplexRoute_EstimatedDuration() {
        List<SegmentDefinition> segments = new ArrayList<>();
        segments.add(new SegmentDefinition(1, 1, 2, 1, "Segment 1"));
        segments.add(new SegmentDefinition(2, 2, 3, 2, "Segment 2"));

        FreightRoute route = routeController.createComplexRoute(1, "Test Route",
                1, 3, segments, "Multi-stop route");

        // Verify estimated duration is calculated
        // Duration = distance / speed for each segment
        // Segment 1: 150 km / 120 km/h = 1.25 hours
        // Segment 2: 200 km / 120 km/h = 1.67 hours
        // Total: ~2.92 hours
        double expectedDuration = (150.0 / 120.0) + (200.0 / 120.0);
        assertEquals(expectedDuration, route.getEstimatedDurationHours(), 0.01);
    }

    @Test
    public void testRouteValidation() {
        FreightRoute validRoute = routeController.createSimpleRoute(1, "Valid Route", 1, 2, "Test");
        assertTrue(validRoute.isValid());

        // Complex route without segments should be invalid
        FreightRoute invalidRoute = new FreightRoute(2, "Invalid Route", RouteType.COMPLEX,
                facility1, facility2, "No segments");
        assertFalse(invalidRoute.isValid());
    }
}

