package app.service.sprint2;

import app.model.*;
import app.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Train Dispatch Service.
 * Tests dispatch creation, route planning, speed calculation, and crossing operations.
 *
 * @author G113 2DK
 * @version 1.0
 */
class TrainDispatchServiceTest {

    private TrainDispatchService service;
    private TrainDispatchRepository dispatchRepo;
    private RoutePointRepository routePointRepo;
    private TrainServiceCompositionRepository compositionRepo;
    private LineSegmentRepository segmentRepo;
    private LineSegmentFacilityRepository segmentFacilityRepo;
    private FacilityRepository facilityRepo;
    private TrainServiceRepository trainServiceRepo;
    private LocomotiveRepository locomotiveRepo;

    @BeforeEach
    void setUp() {
        // Initialize repositories
        dispatchRepo = new TrainDispatchRepository();
        routePointRepo = new RoutePointRepository();
        compositionRepo = new TrainServiceCompositionRepository();
        segmentRepo = new LineSegmentRepository();
        segmentFacilityRepo = new LineSegmentFacilityRepository();
        facilityRepo = new FacilityRepository();
        trainServiceRepo = new TrainServiceRepository();
        locomotiveRepo = new LocomotiveRepository();

        // Create service
        service = new TrainDispatchService(
            dispatchRepo, routePointRepo, compositionRepo,
            segmentRepo, segmentFacilityRepo, facilityRepo, locomotiveRepo
        );

        // Setup test data
        setupTestData();
    }

    private void setupTestData() {
        // Create facility type
        FacilityType stationType = new FacilityType(1, "Station");

        // Create facilities (stations)
        Facility station1 = new Facility(1, "Station A", stationType, 40.0, -8.0, false);
        Facility station2 = new Facility(2, "Station B", stationType, 41.0, -8.5, false);
        Facility station3 = new Facility(3, "Station C", stationType, 41.5, -9.0, false);

        facilityRepo.addFacility(station1);
        facilityRepo.addFacility(station2);
        facilityRepo.addFacility(station3);

        // Create line segments
        LineSegment segment1 = new LineSegment(1, null, 100.0, null, false, null, null, 120.0, 1, 1);
        LineSegment segment2 = new LineSegment(2, null, 80.0, null, false, null, null, 100.0, 1, 2);

        segmentRepo.addLineSegment(segment1);
        segmentRepo.addLineSegment(segment2);

        // Link facilities to segments
        LineSegmentFacility link1 = new LineSegmentFacility(1, segment1, station1);
        LineSegmentFacility link2 = new LineSegmentFacility(2, segment1, station2);
        LineSegmentFacility link3 = new LineSegmentFacility(3, segment2, station2);
        LineSegmentFacility link4 = new LineSegmentFacility(4, segment2, station3);

        segmentFacilityRepo.addLineSegmentFacility(link1);
        segmentFacilityRepo.addLineSegmentFacility(link2);
        segmentFacilityRepo.addLineSegmentFacility(link3);
        segmentFacilityRepo.addLineSegmentFacility(link4);

        // Create train and train service
        Company operator = new Company(1, "Test Railway", "Test Railway", "VAT123456");
        Train train = new Train(1, 101, "Station A", "Station C", operator);
        TrainService trainService = new TrainService(1, train,
            LocalDateTime.now(), LocalDateTime.now().plusHours(3), null, "scheduled");

        trainServiceRepo.addTrainService(trainService);

        // Create locomotive
        Locomotive locomotive = new Locomotive(1, null, null, "TestBrand", "TestModel",
            2020, 2000, 1.5, null, 150.0, 140.0, 50000.0, 80000.0, 4, "B-B", "500");

        locomotiveRepo.addLocomotive(locomotive);

        // Create service locomotive and composition
        ServiceLocomotive serviceLoco = new ServiceLocomotive(1, trainService, locomotive, null);
        TrainServiceComposition composition = new TrainServiceComposition(trainService, serviceLoco, null);
        compositionRepo.addTrainServiceComposition(composition);
    }

    @Test
    void testCreateManualDispatch() {
        // Arrange
        TrainService trainService = trainServiceRepo.getTrainServiceById(1);
        LocalDateTime departureTime = LocalDateTime.now().plusHours(2);
        List<Integer> route = Arrays.asList(1, 2, 3);

        // Act
        TrainDispatch dispatch = service.createManualDispatch(trainService, departureTime, route);

        // Assert
        assertNotNull(dispatch);
        assertNotNull(dispatch.getDispatchId());
        assertEquals("MANUAL", dispatch.getRouteType());
        assertEquals("PLANNED", dispatch.getStatus());
        assertEquals(3, dispatch.getRoute().size());
        assertTrue(dispatch.getTotalDistanceKm() > 0);
        assertTrue(dispatch.getEstimatedTravelTimeMinutes() > 0);
    }

    @Test
    void testRoutePointsSequence() {
        // Arrange
        TrainService trainService = trainServiceRepo.getTrainServiceById(1);
        LocalDateTime departureTime = LocalDateTime.now();
        List<Integer> route = Arrays.asList(1, 2, 3);

        // Act
        TrainDispatch dispatch = service.createManualDispatch(trainService, departureTime, route);

        // Assert
        List<RoutePoint> routePoints = dispatch.getRoute();
        assertEquals(1, routePoints.get(0).getSequenceOrder());
        assertEquals(2, routePoints.get(1).getSequenceOrder());
        assertEquals(3, routePoints.get(2).getSequenceOrder());
    }

    @Test
    void testRoutePointTimestamps() {
        // Arrange
        TrainService trainService = trainServiceRepo.getTrainServiceById(1);
        LocalDateTime departureTime = LocalDateTime.now();
        List<Integer> route = Arrays.asList(1, 2, 3);

        // Act
        TrainDispatch dispatch = service.createManualDispatch(trainService, departureTime, route);

        // Assert
        List<RoutePoint> routePoints = dispatch.getRoute();

        // First point: arrival equals departure (starting point)
        assertEquals(routePoints.get(0).getEstimatedArrivalTime(),
                    routePoints.get(0).getEstimatedDepartureTime());

        // Subsequent points: arrival before or equal to departure
        for (int i = 1; i < routePoints.size(); i++) {
            RoutePoint rp = routePoints.get(i);
            assertNotNull(rp.getEstimatedArrivalTime());
            assertNotNull(rp.getEstimatedDepartureTime());
            assertTrue(rp.getEstimatedArrivalTime().isBefore(rp.getEstimatedDepartureTime()) ||
                      rp.getEstimatedArrivalTime().isEqual(rp.getEstimatedDepartureTime()));
        }
    }

    @Test
    void testCumulativeDistance() {
        // Arrange
        TrainService trainService = trainServiceRepo.getTrainServiceById(1);
        LocalDateTime departureTime = LocalDateTime.now();
        List<Integer> route = Arrays.asList(1, 2, 3);

        // Act
        TrainDispatch dispatch = service.createManualDispatch(trainService, departureTime, route);

        // Assert
        List<RoutePoint> routePoints = dispatch.getRoute();

        // Distance should be cumulative and increasing
        assertEquals(0.0, routePoints.get(0).getDistanceFromStartKm());
        assertTrue(routePoints.get(1).getDistanceFromStartKm() > 0);
        assertTrue(routePoints.get(2).getDistanceFromStartKm() >
                  routePoints.get(1).getDistanceFromStartKm());
    }

    @Test
    void testCalculateSegmentSpeed() {
        // Arrange
        TrainService trainService = trainServiceRepo.getTrainServiceById(1);
        LineSegment segment = segmentRepo.getLineSegmentById(1);

        // Act
        double speed = service.calculateSegmentSpeed(trainService, segment);

        // Assert
        assertTrue(speed > 0);
        assertTrue(speed <= 120.0); // Should not exceed track limit
    }

    @Test
    void testUpdateDispatchStatus() {
        // Arrange
        TrainService trainService = trainServiceRepo.getTrainServiceById(1);
        List<Integer> route = Arrays.asList(1, 2);
        TrainDispatch dispatch = service.createManualDispatch(
            trainService, LocalDateTime.now(), route);
        Integer dispatchId = dispatch.getDispatchId();

        // Act
        TrainDispatch updated = service.updateDispatchStatus(dispatchId, "DISPATCHED");

        // Assert
        assertNotNull(updated);
        assertEquals("DISPATCHED", updated.getStatus());
    }

    @Test
    void testGetDispatchesByStatus() {
        // Arrange
        TrainService trainService = trainServiceRepo.getTrainServiceById(1);
        List<Integer> route = Arrays.asList(1, 2);

        service.createManualDispatch(trainService, LocalDateTime.now(), route);
        service.createManualDispatch(trainService, LocalDateTime.now().plusHours(1), route);

        // Act
        List<TrainDispatch> planned = service.getDispatchesByStatus("PLANNED");

        // Assert
        assertEquals(2, planned.size());
    }

    @Test
    void testUpdateCrossingOperation() {
        // Arrange
        TrainService trainService = trainServiceRepo.getTrainServiceById(1);
        List<Integer> route = Arrays.asList(1, 2, 3);
        TrainDispatch dispatch = service.createManualDispatch(
            trainService, LocalDateTime.now(), route);

        RoutePoint firstPoint = dispatch.getRoute().get(0);

        // Act
        RoutePoint updated = service.updateCrossingOperation(
            firstPoint.getRoutePointId(), "SIDING_WAIT", 15);

        // Assert
        assertNotNull(updated);
        assertEquals("SIDING_WAIT", updated.getCrossingOperation());
        assertEquals(15, updated.getExpectedWaitTimeMinutes());
    }

    @Test
    void testEstimatedArrivalTime() {
        // Arrange
        TrainService trainService = trainServiceRepo.getTrainServiceById(1);
        LocalDateTime departureTime = LocalDateTime.of(2025, 11, 20, 10, 0);
        List<Integer> route = Arrays.asList(1, 2, 3);

        // Act
        TrainDispatch dispatch = service.createManualDispatch(trainService, departureTime, route);

        // Assert
        LocalDateTime estimatedArrival = dispatch.getEstimatedArrivalTime();
        assertNotNull(estimatedArrival);
        assertTrue(estimatedArrival.isAfter(departureTime));
    }

    @Test
    void testRoutePointsWithCrossings() {
        // Arrange
        TrainService trainService = trainServiceRepo.getTrainServiceById(1);
        List<Integer> route = Arrays.asList(1, 2, 3);
        TrainDispatch dispatch = service.createManualDispatch(
            trainService, LocalDateTime.now(), route);

        // Update one point to have a crossing
        RoutePoint point = dispatch.getRoute().get(1);
        service.updateCrossingOperation(point.getRoutePointId(), "WAIT_FOR_CROSSING", 10);

        // Act
        List<RoutePoint> crossings = service.getRoutePointsWithCrossings();

        // Assert
        assertTrue(crossings.size() > 0);
        assertTrue(crossings.stream().anyMatch(
            rp -> "WAIT_FOR_CROSSING".equals(rp.getCrossingOperation())));
    }
}

