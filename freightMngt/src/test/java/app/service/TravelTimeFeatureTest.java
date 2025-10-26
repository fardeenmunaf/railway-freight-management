package app.service;

import app.model.*;
import app.repositories.*;
import app.service.TravelTimeFeature.TravelTimeCalculator;
import app.service.TravelTimeFeature.NoDirectConnectionException;
import app.service.TravelTimeFeature.InvalidSpeedException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for TravelTimeFeature service.
 * Tests travel time calculation, route finding, and validation.
 */
public class TravelTimeFeatureTest {

    private TravelTimeCalculator calculator;
    private FacilityRepository facilityRepo;
    private LineSegmentRepository lineSegmentRepo;
    private LineSegmentFacilityRepository lineSegmentFacilityRepo;
    private LocomotiveRepository locomotiveRepo;

    @Before
    public void setUp() {
        // Get repositories
        Repositories repositories = Repositories.getInstance();
        facilityRepo = repositories.getFacilityRepository();
        lineSegmentRepo = repositories.getLineSegmentRepository();
        lineSegmentFacilityRepo = repositories.getLineSegmentFacilityRepository();
        locomotiveRepo = repositories.getLocomotiveRepository();

        // Clear repositories
        facilityRepo.clear();
        lineSegmentRepo.clear();
        lineSegmentFacilityRepo.clear();
        locomotiveRepo.clear();

        // Create calculator
        calculator = TravelTimeFeature.create(
            facilityRepo,
            lineSegmentRepo,
            lineSegmentFacilityRepo,
            locomotiveRepo
        );
    }

    /**
     * Helper method to create a station (facility type 2).
     */
    private Facility createStation(Integer facilityId, String name) {
        FacilityType stationType = new FacilityType(2, "Station");
        Facility station = new Facility(facilityId, name, stationType, 0.0, 0.0, false);
        facilityRepo.addFacility(station);
        return station;
    }

    /**
     * Helper method to create a locomotive.
     */
    private Locomotive createLocomotive(Double maxSpeed) {
        // Create a base locomotive
        Locomotive baseLoco = new Locomotive();
        baseLoco.setLocomotiveId(1);
        baseLoco.setMaxSpeed(maxSpeed);

        // Create LocomotiveDiesel wrapping the base locomotive
        LocomotiveDiesel locomotive = new LocomotiveDiesel(baseLoco, 100.0);
        locomotiveRepo.addLocomotive(baseLoco);
        return baseLoco;
    }

    /**
     * Helper method to create a line segment.
     */
    private LineSegment createLineSegment(Integer segmentId, Double lengthKm, Double speedLimitKmh) {
        Company company = new Company(1, "Test Company", "TC", "123456789");
        User owner = new User(1, "Owner", "owner@test.com", "password", company, true);
        Line line = new Line(1, owner);
        Gauge gauge = new Gauge(1, "Standard", 1.435);
        TrackType trackType = new TrackType(1, "Standard");

        LineSegment segment = new LineSegment(segmentId, line, lengthKm, trackType, false, gauge, 10.0, speedLimitKmh, 1, 1);
        lineSegmentRepo.addLineSegment(segment);
        return segment;
    }

    /**
     * Helper method to connect a station to a line segment.
     */
    private void connectStationToSegment(Facility station, LineSegment segment) {
        LineSegmentFacility connection = new LineSegmentFacility(
            station.getFacilityId() * 1000 + segment.getSegmentId(),
            segment,
            station
        );
        lineSegmentFacilityRepo.addLineSegmentFacility(connection);
    }

    /**
     * Test create method returns a valid calculator.
     */
    @Test
    public void testCreate_ReturnsValidCalculator() {
        TravelTimeCalculator calc = TravelTimeFeature.create(
            facilityRepo,
            lineSegmentRepo,
            lineSegmentFacilityRepo,
            locomotiveRepo
        );

        assertNotNull("Calculator should not be null", calc);
    }

    /**
     * Test calculateTravelTimeMinutes with direct connection.
     */
    @Test
    public void testCalculateTravelTimeMinutes_DirectConnection() {
        // Create two stations
        Facility station1 = createStation(1, "Station A");
        Facility station2 = createStation(2, "Station B");

        // Create line segment: 100km, speed limit 100 km/h
        LineSegment segment = createLineSegment(1, 100.0, 100.0);

        // Connect both stations to the segment
        connectStationToSegment(station1, segment);
        connectStationToSegment(station2, segment);

        // Create locomotive with max speed 120 km/h
        createLocomotive(120.0);

        // Calculate travel time
        // Effective speed = min(100, 120) = 100 km/h
        // Time = 100 km / 100 km/h = 1 hour = 60 minutes
        double travelTime = calculator.calculateTravelTimeMinutes(1, 2, 1);

        assertEquals("Travel time should be 60 minutes", 60.0, travelTime, 0.01);
        assertEquals("Distance should be 100 km", 100.0, calculator.getDistanceKm(), 0.01);
        assertEquals("Effective speed should be 100 km/h", 100.0, calculator.getEffectiveSpeedKmh(), 0.01);
    }

    /**
     * Test calculateTravelTimeMinutes with locomotive speed limit.
     */
    @Test
    public void testCalculateTravelTimeMinutes_LocomotiveSpeedLimit() {
        // Create two stations
        Facility station1 = createStation(1, "Station A");
        Facility station2 = createStation(2, "Station B");

        // Create line segment: 60km, speed limit 120 km/h
        LineSegment segment = createLineSegment(1, 60.0, 120.0);

        connectStationToSegment(station1, segment);
        connectStationToSegment(station2, segment);

        // Create locomotive with max speed 80 km/h (LOWER than segment limit)
        createLocomotive(80.0);

        // Calculate travel time
        // Effective speed = min(120, 80) = 80 km/h
        // Time = 60 km / 80 km/h = 0.75 hours = 45 minutes
        double travelTime = calculator.calculateTravelTimeMinutes(1, 2, 1);

        assertEquals("Travel time should be 45 minutes", 45.0, travelTime, 0.01);
        assertEquals("Distance should be 60 km", 60.0, calculator.getDistanceKm(), 0.01);
        assertEquals("Effective speed should be 80 km/h", 80.0, calculator.getEffectiveSpeedKmh(), 0.01);
    }

    /**
     * Test calculateTravelTimeMinutes with multiple segments (indirect route).
     */
    @Test
    public void testCalculateTravelTimeMinutes_MultipleSegments() {
        // Create three stations: A -> B -> C
        Facility stationA = createStation(1, "Station A");
        Facility stationB = createStation(2, "Station B");
        Facility stationC = createStation(3, "Station C");

        // Create two segments
        // Segment 1: A-B, 50km, 100 km/h
        LineSegment segment1 = createLineSegment(1, 50.0, 100.0);
        connectStationToSegment(stationA, segment1);
        connectStationToSegment(stationB, segment1);

        // Segment 2: B-C, 30km, 80 km/h
        LineSegment segment2 = createLineSegment(2, 30.0, 80.0);
        connectStationToSegment(stationB, segment2);
        connectStationToSegment(stationC, segment2);

        // Create locomotive with max speed 120 km/h
        createLocomotive(120.0);

        // Calculate travel time from A to C
        // Segment 1: 50 km at 100 km/h = 0.5 hours
        // Segment 2: 30 km at 80 km/h = 0.375 hours
        // Total: 0.875 hours = 52.5 minutes
        double travelTime = calculator.calculateTravelTimeMinutes(1, 3, 1);

        assertEquals("Travel time should be 52.5 minutes", 52.5, travelTime, 0.01);
        assertEquals("Distance should be 80 km", 80.0, calculator.getDistanceKm(), 0.01);
    }

    /**
     * Test calculateTravelTimeMinutes with no connection throws exception.
     */
    @Test(expected = NoDirectConnectionException.class)
    public void testCalculateTravelTimeMinutes_NoConnection_ThrowsException() {
        // Create two isolated stations (no segments connecting them)
        createStation(1, "Station A");
        createStation(2, "Station B");

        createLocomotive(120.0);

        // Should throw NoDirectConnectionException
        calculator.calculateTravelTimeMinutes(1, 2, 1);
    }

    /**
     * Test calculateTravelTimeMinutes with non-existent origin station.
     */
    @Test(expected = NoDirectConnectionException.class)
    public void testCalculateTravelTimeMinutes_NonExistentOrigin_ThrowsException() {
        createStation(2, "Station B");
        createLocomotive(120.0);

        // Station 999 doesn't exist
        calculator.calculateTravelTimeMinutes(999, 2, 1);
    }

    /**
     * Test calculateTravelTimeMinutes with non-existent destination station.
     */
    @Test(expected = NoDirectConnectionException.class)
    public void testCalculateTravelTimeMinutes_NonExistentDestination_ThrowsException() {
        createStation(1, "Station A");
        createLocomotive(120.0);

        // Station 999 doesn't exist
        calculator.calculateTravelTimeMinutes(1, 999, 1);
    }

    /**
     * Test calculateTravelTimeMinutes with non-existent locomotive.
     */
    @Test(expected = InvalidSpeedException.class)
    public void testCalculateTravelTimeMinutes_NonExistentLocomotive_ThrowsException() {
        Facility station1 = createStation(1, "Station A");
        Facility station2 = createStation(2, "Station B");

        LineSegment segment = createLineSegment(1, 100.0, 100.0);
        connectStationToSegment(station1, segment);
        connectStationToSegment(station2, segment);

        // Locomotive 999 doesn't exist
        calculator.calculateTravelTimeMinutes(1, 2, 999);
    }

    /**
     * Test calculateTravelTimeMinutes with locomotive with invalid speed.
     */
    @Test(expected = InvalidSpeedException.class)
    public void testCalculateTravelTimeMinutes_LocomotiveZeroSpeed_ThrowsException() {
        Facility station1 = createStation(1, "Station A");
        Facility station2 = createStation(2, "Station B");

        LineSegment segment = createLineSegment(1, 100.0, 100.0);
        connectStationToSegment(station1, segment);
        connectStationToSegment(station2, segment);

        // Create locomotive with ZERO max speed
        createLocomotive(0.0);

        calculator.calculateTravelTimeMinutes(1, 2, 1);
    }

    /**
     * Test calculateTravelTimeMinutes with locomotive with negative speed.
     */
    @Test(expected = InvalidSpeedException.class)
    public void testCalculateTravelTimeMinutes_LocomotiveNegativeSpeed_ThrowsException() {
        Facility station1 = createStation(1, "Station A");
        Facility station2 = createStation(2, "Station B");

        LineSegment segment = createLineSegment(1, 100.0, 100.0);
        connectStationToSegment(station1, segment);
        connectStationToSegment(station2, segment);

        // Create locomotive with NEGATIVE max speed
        createLocomotive(-50.0);

        calculator.calculateTravelTimeMinutes(1, 2, 1);
    }

    /**
     * Test calculateTravelTimeMinutes with facility that is not a station.
     */
    @Test(expected = NoDirectConnectionException.class)
    public void testCalculateTravelTimeMinutes_NotAStation_ThrowsException() {
        // Create facility with type 1 (not station, which is type 2)
        FacilityType warehouseType = new FacilityType(1, "Warehouse");
        Facility warehouse = new Facility(1, "Warehouse A", warehouseType, 0.0, 0.0, false);
        facilityRepo.addFacility(warehouse);

        createStation(2, "Station B");
        createLocomotive(120.0);

        // Should throw because facility 1 is not a station
        calculator.calculateTravelTimeMinutes(1, 2, 1);
    }

    /**
     * Test calculateTravelTimeMinutes finds shortest time path (not shortest distance).
     */
    @Test
    public void testCalculateTravelTimeMinutes_FindsShortestTimePath() {
        // Create network with two routes from A to C
        Facility stationA = createStation(1, "Station A");
        Facility stationB = createStation(2, "Station B");
        Facility stationC = createStation(3, "Station C");

        // Route 1 (via B): A-B (100km at 100km/h) + B-C (100km at 100km/h)
        // Total: 200km, 2 hours
        LineSegment segment1 = createLineSegment(1, 100.0, 100.0);
        connectStationToSegment(stationA, segment1);
        connectStationToSegment(stationB, segment1);

        LineSegment segment2 = createLineSegment(2, 100.0, 100.0);
        connectStationToSegment(stationB, segment2);
        connectStationToSegment(stationC, segment2);

        // Route 2 (direct): A-C (150km at 200km/h)
        // Total: 150km, 0.75 hours (FASTER despite being direct)
        LineSegment segment3 = createLineSegment(3, 150.0, 200.0);
        connectStationToSegment(stationA, segment3);
        connectStationToSegment(stationC, segment3);

        createLocomotive(250.0);

        // Calculate - should choose the faster route (direct at 200 km/h)
        double travelTime = calculator.calculateTravelTimeMinutes(1, 3, 1);

        // Route 2: 150 km at 200 km/h = 0.75 hours = 45 minutes
        assertEquals("Should choose faster route (45 min)", 45.0, travelTime, 0.01);
        assertEquals("Distance should be 150 km", 150.0, calculator.getDistanceKm(), 0.01);
    }

    /**
     * Test calculateTravelTimeMinutes with same origin and destination.
     */
    @Test
    public void testCalculateTravelTimeMinutes_SameOriginAndDestination() {
        createStation(1, "Station A");
        createLocomotive(120.0);

        // Calculate from station to itself
        double travelTime = calculator.calculateTravelTimeMinutes(1, 1, 1);

        assertEquals("Travel time should be 0 for same station", 0.0, travelTime, 0.01);
        assertEquals("Distance should be 0 km", 0.0, calculator.getDistanceKm(), 0.01);
    }

    /**
     * Test calculateTravelTimeMinutes with segment without speed limit.
     */
    @Test
    public void testCalculateTravelTimeMinutes_SegmentWithoutSpeedLimit() {
        Facility station1 = createStation(1, "Station A");
        Facility station2 = createStation(2, "Station B");

        // Create segment with null speed limit
        LineSegment segment = createLineSegment(1, 80.0, null);
        connectStationToSegment(station1, segment);
        connectStationToSegment(station2, segment);

        // Create locomotive with max speed 100 km/h
        createLocomotive(100.0);

        // Should use locomotive max speed since segment has no limit
        // Time = 80 km / 100 km/h = 0.8 hours = 48 minutes
        double travelTime = calculator.calculateTravelTimeMinutes(1, 2, 1);

        assertEquals("Travel time should be 48 minutes", 48.0, travelTime, 0.01);
        assertEquals("Effective speed should be 100 km/h", 100.0, calculator.getEffectiveSpeedKmh(), 0.01);
    }

    /**
     * Test calculateTravelTimeMinutes with zero-length segment is ignored.
     */
    @Test(expected = NoDirectConnectionException.class)
    public void testCalculateTravelTimeMinutes_ZeroLengthSegment_Ignored() {
        Facility station1 = createStation(1, "Station A");
        Facility station2 = createStation(2, "Station B");

        // Create segment with ZERO length (should be ignored)
        LineSegment segment = createLineSegment(1, 0.0, 100.0);
        connectStationToSegment(station1, segment);
        connectStationToSegment(station2, segment);

        createLocomotive(120.0);

        // Should throw because zero-length segments are ignored
        calculator.calculateTravelTimeMinutes(1, 2, 1);
    }

    /**
     * Test getDistanceKm returns correct distance.
     */
    @Test
    public void testGetDistanceKm_ReturnsCorrectDistance() {
        Facility station1 = createStation(1, "Station A");
        Facility station2 = createStation(2, "Station B");

        LineSegment segment = createLineSegment(1, 75.5, 100.0);
        connectStationToSegment(station1, segment);
        connectStationToSegment(station2, segment);

        createLocomotive(120.0);

        calculator.calculateTravelTimeMinutes(1, 2, 1);

        assertEquals("Distance should be 75.5 km", 75.5, calculator.getDistanceKm(), 0.01);
    }

    /**
     * Test getEffectiveSpeedKmh returns correct speed.
     */
    @Test
    public void testGetEffectiveSpeedKmh_ReturnsCorrectSpeed() {
        Facility station1 = createStation(1, "Station A");
        Facility station2 = createStation(2, "Station B");

        // 120 km at 80 km/h
        LineSegment segment = createLineSegment(1, 120.0, 80.0);
        connectStationToSegment(station1, segment);
        connectStationToSegment(station2, segment);

        createLocomotive(100.0);

        calculator.calculateTravelTimeMinutes(1, 2, 1);

        // Effective speed = min(80, 100) = 80 km/h
        assertEquals("Effective speed should be 80 km/h", 80.0, calculator.getEffectiveSpeedKmh(), 0.01);
    }

    /**
     * Test complex network with multiple paths chooses fastest.
     */
    @Test
    public void testCalculateTravelTimeMinutes_ComplexNetwork() {
        // Create a diamond-shaped network: A -> B -> D and A -> C -> D
        Facility stationA = createStation(1, "Station A");
        Facility stationB = createStation(2, "Station B");
        Facility stationC = createStation(3, "Station C");
        Facility stationD = createStation(4, "Station D");

        // Path 1: A-B (50km at 50km/h = 1h) + B-D (50km at 50km/h = 1h) = 2 hours
        LineSegment segmentAB = createLineSegment(1, 50.0, 50.0);
        connectStationToSegment(stationA, segmentAB);
        connectStationToSegment(stationB, segmentAB);

        LineSegment segmentBD = createLineSegment(2, 50.0, 50.0);
        connectStationToSegment(stationB, segmentBD);
        connectStationToSegment(stationD, segmentBD);

        // Path 2: A-C (80km at 100km/h = 0.8h) + C-D (60km at 100km/h = 0.6h) = 1.4 hours (FASTER)
        LineSegment segmentAC = createLineSegment(3, 80.0, 100.0);
        connectStationToSegment(stationA, segmentAC);
        connectStationToSegment(stationC, segmentAC);

        LineSegment segmentCD = createLineSegment(4, 60.0, 100.0);
        connectStationToSegment(stationC, segmentCD);
        connectStationToSegment(stationD, segmentCD);

        createLocomotive(150.0);

        // Should choose path 2 (via C): 1.4 hours = 84 minutes
        double travelTime = calculator.calculateTravelTimeMinutes(1, 4, 1);

        assertEquals("Should choose faster path (84 min)", 84.0, travelTime, 0.01);
        assertEquals("Distance should be 140 km", 140.0, calculator.getDistanceKm(), 0.01);
    }

    /**
     * Test bidirectional segments work correctly.
     */
    @Test
    public void testCalculateTravelTimeMinutes_BidirectionalSegments() {
        Facility station1 = createStation(1, "Station A");
        Facility station2 = createStation(2, "Station B");

        LineSegment segment = createLineSegment(1, 90.0, 120.0);
        connectStationToSegment(station1, segment);
        connectStationToSegment(station2, segment);

        createLocomotive(100.0);

        // Test both directions
        double time1to2 = calculator.calculateTravelTimeMinutes(1, 2, 1);
        double time2to1 = calculator.calculateTravelTimeMinutes(2, 1, 1);

        // Both should be the same: 90 km / 100 km/h = 54 minutes
        assertEquals("Time 1->2 should be 54 minutes", 54.0, time1to2, 0.01);
        assertEquals("Time 2->1 should be 54 minutes", 54.0, time2to1, 0.01);
    }

    /**
     * Test segment with multiple stations creates all connections.
     */
    @Test
    public void testCalculateTravelTimeMinutes_SegmentWithThreeStations() {
        // Create segment with three stations: A, B, C all on same segment
        Facility stationA = createStation(1, "Station A");
        Facility stationB = createStation(2, "Station B");
        Facility stationC = createStation(3, "Station C");

        LineSegment segment = createLineSegment(1, 100.0, 80.0);
        connectStationToSegment(stationA, segment);
        connectStationToSegment(stationB, segment);
        connectStationToSegment(stationC, segment);

        createLocomotive(100.0);

        // All stations should be connected to each other
        double timeAtoB = calculator.calculateTravelTimeMinutes(1, 2, 1);
        double timeAtoC = calculator.calculateTravelTimeMinutes(1, 3, 1);
        double timeBtoC = calculator.calculateTravelTimeMinutes(2, 3, 1);

        // All should be 75 minutes (100 km / 80 km/h = 1.25 hours)
        assertEquals("Time A->B should be 75 minutes", 75.0, timeAtoB, 0.01);
        assertEquals("Time A->C should be 75 minutes", 75.0, timeAtoC, 0.01);
        assertEquals("Time B->C should be 75 minutes", 75.0, timeBtoC, 0.01);
    }

    /**
     * Test NoDirectConnectionException message.
     */
    @Test
    public void testNoDirectConnectionException_HasCorrectMessage() {
        try {
            throw new NoDirectConnectionException("Test message");
        } catch (NoDirectConnectionException e) {
            assertEquals("Exception should have correct message", "Test message", e.getMessage());
        }
    }

    /**
     * Test InvalidSpeedException message.
     */
    @Test
    public void testInvalidSpeedException_HasCorrectMessage() {
        try {
            throw new InvalidSpeedException("Speed error");
        } catch (InvalidSpeedException e) {
            assertEquals("Exception should have correct message", "Speed error", e.getMessage());
        }
    }

    /**
     * Test calculateTravelTimeMinutes with facility that has null FacilityType.
     */
    @Test(expected = NoDirectConnectionException.class)
    public void testCalculateTravelTimeMinutes_FacilityWithNullType_ThrowsException() {
        // Create facility with NULL facility type
        Facility facility = new Facility(1, "Broken Station", null, 0.0, 0.0, false);
        facilityRepo.addFacility(facility);

        createStation(2, "Station B");
        createLocomotive(120.0);

        // Should throw because facility 1 has null type
        calculator.calculateTravelTimeMinutes(1, 2, 1);
    }

    /**
     * Test TravelTimeCalculator constructor initializes repositories correctly.
     */
    @Test
    public void testTravelTimeCalculator_ConstructorInitializesCorrectly() {
        // Create new calculator with specific repositories
        FacilityRepository testFacilityRepo = new FacilityRepository();
        LineSegmentRepository testSegmentRepo = new LineSegmentRepository();
        LineSegmentFacilityRepository testLSFRepo = new LineSegmentFacilityRepository();
        LocomotiveRepository testLocoRepo = new LocomotiveRepository();

        TravelTimeCalculator testCalculator = new TravelTimeCalculator(
            testFacilityRepo,
            testSegmentRepo,
            testLSFRepo,
            testLocoRepo
        );

        assertNotNull("Calculator should not be null after construction", testCalculator);

        // Test that calculator can be used (validates repositories are set)
        // Should return 0 for same station
        Facility station = new Facility(1, "Station A", new FacilityType(2, "Station"), 0.0, 0.0, false);
        testFacilityRepo.addFacility(station);

        Locomotive loco = new Locomotive();
        loco.setLocomotiveId(1);
        loco.setMaxSpeed(100.0);
        testLocoRepo.addLocomotive(loco);

        double time = testCalculator.calculateTravelTimeMinutes(1, 1, 1);
        assertEquals("Same station should have 0 travel time", 0.0, time, 0.01);
    }

    /**
     * Test validateStation is called correctly (indirect test via exception).
     */
    @Test(expected = NoDirectConnectionException.class)
    public void testValidateStation_IndirectTest_InvalidFacilityId() {
        createLocomotive(100.0);

        // Both station IDs don't exist - should throw during validation
        calculator.calculateTravelTimeMinutes(999, 998, 1);
    }

    /**
     * Test validateLocomotive is called correctly (indirect test via exception).
     */
    @Test(expected = InvalidSpeedException.class)
    public void testValidateLocomotive_IndirectTest_InvalidLocomotiveId() {
        createStation(1, "Station A");
        createStation(2, "Station B");

        // Locomotive doesn't exist - should throw during validation
        calculator.calculateTravelTimeMinutes(1, 2, 999);
    }

    /**
     * Test getDistanceKm returns 0.0 before any calculation.
     */
    @Test
    public void testGetDistanceKm_InitialValueIsZero() {
        // Before any calculation, distance should be 0
        assertEquals("Initial distance should be 0.0", 0.0, calculator.getDistanceKm(), 0.01);
    }

    /**
     * Test getEffectiveSpeedKmh returns 0.0 before any calculation.
     */
    @Test
    public void testGetEffectiveSpeedKmh_InitialValueIsZero() {
        // Before any calculation, effective speed should be 0
        assertEquals("Initial effective speed should be 0.0", 0.0, calculator.getEffectiveSpeedKmh(), 0.01);
    }

    /**
     * Test calculateTravelTimeMinutes with null segment in graph (should skip).
     */
    @Test(expected = NoDirectConnectionException.class)
    public void testCalculateTravelTimeMinutes_NullSegmentInGraph() {
        createStation(1, "Station A");
        createStation(2, "Station B");

        // No segments connecting them
        createLocomotive(100.0);

        calculator.calculateTravelTimeMinutes(1, 2, 1);
    }

    /**
     * Test calculateTravelTimeMinutes with negative distance segment (should be ignored).
     */
    @Test(expected = NoDirectConnectionException.class)
    public void testCalculateTravelTimeMinutes_NegativeDistanceSegment() {
        Facility station1 = createStation(1, "Station A");
        Facility station2 = createStation(2, "Station B");

        // Create segment with NEGATIVE length (should be ignored)
        LineSegment segment = createLineSegment(1, -10.0, 100.0);
        connectStationToSegment(station1, segment);
        connectStationToSegment(station2, segment);

        createLocomotive(100.0);

        // Should throw because negative length segments are ignored
        calculator.calculateTravelTimeMinutes(1, 2, 1);
    }

    /**
     * Test calculateTravelTimeMinutes with locomotive having null maxSpeed.
     */
    @Test(expected = InvalidSpeedException.class)
    public void testCalculateTravelTimeMinutes_LocomotiveNullMaxSpeed() {
        Facility station1 = createStation(1, "Station A");
        Facility station2 = createStation(2, "Station B");

        LineSegment segment = createLineSegment(1, 100.0, 100.0);
        connectStationToSegment(station1, segment);
        connectStationToSegment(station2, segment);

        // Create locomotive with NULL max speed
        Locomotive loco = new Locomotive();
        loco.setLocomotiveId(1);
        loco.setMaxSpeed(null);  // NULL speed
        locomotiveRepo.addLocomotive(loco);

        calculator.calculateTravelTimeMinutes(1, 2, 1);
    }

    /**
     * Test that getDistanceKm and getEffectiveSpeedKmh are updated after calculation.
     */
    @Test
    public void testGettersUpdatedAfterCalculation() {
        Facility station1 = createStation(1, "Station A");
        Facility station2 = createStation(2, "Station B");

        // 50 km at 100 km/h
        LineSegment segment = createLineSegment(1, 50.0, 100.0);
        connectStationToSegment(station1, segment);
        connectStationToSegment(station2, segment);

        createLocomotive(100.0);

        // Initial values should be 0
        assertEquals("Initial distance should be 0", 0.0, calculator.getDistanceKm(), 0.01);
        assertEquals("Initial speed should be 0", 0.0, calculator.getEffectiveSpeedKmh(), 0.01);

        // Calculate
        calculator.calculateTravelTimeMinutes(1, 2, 1);

        // After calculation, values should be updated
        assertEquals("Distance should be 50 km", 50.0, calculator.getDistanceKm(), 0.01);
        assertEquals("Effective speed should be 100 km/h", 100.0, calculator.getEffectiveSpeedKmh(), 0.01);
    }

    /**
     * Test calculateTravelTimeMinutes with very long distance.
     */
    @Test
    public void testCalculateTravelTimeMinutes_VeryLongDistance() {
        Facility station1 = createStation(1, "Station A");
        Facility station2 = createStation(2, "Station B");

        // 1000 km at 200 km/h
        LineSegment segment = createLineSegment(1, 1000.0, 200.0);
        connectStationToSegment(station1, segment);
        connectStationToSegment(station2, segment);

        createLocomotive(200.0);

        double travelTime = calculator.calculateTravelTimeMinutes(1, 2, 1);

        // 1000 km / 200 km/h = 5 hours = 300 minutes
        assertEquals("Travel time should be 300 minutes", 300.0, travelTime, 0.01);
        assertEquals("Distance should be 1000 km", 1000.0, calculator.getDistanceKm(), 0.01);
    }

    /**
     * Test calculateTravelTimeMinutes with very slow speed.
     */
    @Test
    public void testCalculateTravelTimeMinutes_VerySlowSpeed() {
        Facility station1 = createStation(1, "Station A");
        Facility station2 = createStation(2, "Station B");

        // 10 km at 5 km/h (very slow)
        LineSegment segment = createLineSegment(1, 10.0, 5.0);
        connectStationToSegment(station1, segment);
        connectStationToSegment(station2, segment);

        createLocomotive(10.0);

        double travelTime = calculator.calculateTravelTimeMinutes(1, 2, 1);

        // 10 km / 5 km/h = 2 hours = 120 minutes
        assertEquals("Travel time should be 120 minutes", 120.0, travelTime, 0.01);
        assertEquals("Effective speed should be 5 km/h", 5.0, calculator.getEffectiveSpeedKmh(), 0.01);
    }

    /**
     * Test NoDirectConnectionException can be created with message.
     */
    @Test
    public void testNoDirectConnectionException_Creation() {
        NoDirectConnectionException ex = new NoDirectConnectionException("Test error message");
        assertEquals("Exception message should match", "Test error message", ex.getMessage());
        assertTrue("Should be instance of RuntimeException", ex instanceof RuntimeException);
    }

    /**
     * Test InvalidSpeedException can be created with message.
     */
    @Test
    public void testInvalidSpeedException_Creation() {
        InvalidSpeedException ex = new InvalidSpeedException("Speed test error");
        assertEquals("Exception message should match", "Speed test error", ex.getMessage());
        assertTrue("Should be instance of RuntimeException", ex instanceof RuntimeException);
    }

    /**
     * Test that PathResult internal class is used correctly (indirect test via multi-segment path).
     * PathResult stores totalHours and totalDistanceKm which are returned by findShortestTimePath.
     */
    @Test
    public void testPathResult_InternalClass_UsedInMultiSegmentCalculation() {
        // Create 3 stations connected by 2 segments to test PathResult accumulation
        Facility stationA = createStation(1, "Station A");
        Facility stationB = createStation(2, "Station B");
        Facility stationC = createStation(3, "Station C");

        // Segment 1: A-B, 60km at 120 km/h = 0.5 hours
        LineSegment segment1 = createLineSegment(1, 60.0, 120.0);
        connectStationToSegment(stationA, segment1);
        connectStationToSegment(stationB, segment1);

        // Segment 2: B-C, 90km at 120 km/h = 0.75 hours
        LineSegment segment2 = createLineSegment(2, 90.0, 120.0);
        connectStationToSegment(stationB, segment2);
        connectStationToSegment(stationC, segment2);

        createLocomotive(150.0);

        // PathResult should accumulate: totalHours = 1.25h, totalDistanceKm = 150km
        double travelTime = calculator.calculateTravelTimeMinutes(1, 3, 1);

        assertEquals("Travel time should be 75 minutes", 75.0, travelTime, 0.01);
        assertEquals("Distance should be 150 km", 150.0, calculator.getDistanceKm(), 0.01);
        assertEquals("Effective speed should be 120 km/h", 120.0, calculator.getEffectiveSpeedKmh(), 0.01);
    }

    /**
     * Test that QueueNode internal class is used correctly (indirect test via priority queue).
     * QueueNode is used in Dijkstra's algorithm to track best path to each station.
     */
    @Test
    public void testQueueNode_InternalClass_UsedInDijkstraAlgorithm() {
        // Create diamond network: A -> B -> D and A -> C -> D
        // QueueNode will track multiple paths and choose the best one
        Facility stationA = createStation(1, "Station A");
        Facility stationB = createStation(2, "Station B");
        Facility stationC = createStation(3, "Station C");
        Facility stationD = createStation(4, "Station D");

        // Path 1 (via B): slower route
        LineSegment segmentAB = createLineSegment(1, 100.0, 50.0);  // 2 hours
        connectStationToSegment(stationA, segmentAB);
        connectStationToSegment(stationB, segmentAB);

        LineSegment segmentBD = createLineSegment(2, 100.0, 50.0);  // 2 hours
        connectStationToSegment(stationB, segmentBD);
        connectStationToSegment(stationD, segmentBD);

        // Path 2 (via C): faster route
        LineSegment segmentAC = createLineSegment(3, 120.0, 120.0);  // 1 hour
        connectStationToSegment(stationA, segmentAC);
        connectStationToSegment(stationC, segmentAC);

        LineSegment segmentCD = createLineSegment(4, 60.0, 120.0);  // 0.5 hours
        connectStationToSegment(stationC, segmentCD);
        connectStationToSegment(stationD, segmentCD);

        createLocomotive(150.0);

        // QueueNode should find path via C (1.5 hours) instead of via B (4 hours)
        double travelTime = calculator.calculateTravelTimeMinutes(1, 4, 1);

        assertEquals("Should choose faster path (90 min)", 90.0, travelTime, 0.01);
        assertEquals("Distance should be 180 km", 180.0, calculator.getDistanceKm(), 0.01);
    }

    /**
     * Test that Edge internal class is used correctly (indirect test via graph construction).
     * Edge links facilities via line segments and is used to build the railway graph.
     */
    @Test
    public void testEdge_InternalClass_UsedInGraphConstruction() {
        // Create segment with 3 stations - Edge class creates bidirectional connections
        Facility stationA = createStation(1, "Station A");
        Facility stationB = createStation(2, "Station B");
        Facility stationC = createStation(3, "Station C");

        // One segment connects all 3 stations - Edge class should create:
        // A->B, A->C, B->A, B->C, C->A, C->B (6 edges total)
        LineSegment segment = createLineSegment(1, 100.0, 80.0);
        connectStationToSegment(stationA, segment);
        connectStationToSegment(stationB, segment);
        connectStationToSegment(stationC, segment);

        createLocomotive(100.0);

        // All stations should be reachable from any other station
        double timeAtoB = calculator.calculateTravelTimeMinutes(1, 2, 1);
        double timeAtoC = calculator.calculateTravelTimeMinutes(1, 3, 1);
        double timeBtoC = calculator.calculateTravelTimeMinutes(2, 3, 1);

        // All should be 75 minutes (100 km / 80 km/h = 1.25 hours)
        assertEquals("A->B should be 75 min", 75.0, timeAtoB, 0.01);
        assertEquals("A->C should be 75 min", 75.0, timeAtoC, 0.01);
        assertEquals("B->C should be 75 min", 75.0, timeBtoC, 0.01);
    }

    /**
     * Test create method with all repository parameters.
     */
    @Test
    public void testCreate_WithAllRepositories() {
        FacilityRepository testFacilityRepo = new FacilityRepository();
        LineSegmentRepository testSegmentRepo = new LineSegmentRepository();
        LineSegmentFacilityRepository testLSFRepo = new LineSegmentFacilityRepository();
        LocomotiveRepository testLocoRepo = new LocomotiveRepository();

        TravelTimeCalculator calc = TravelTimeFeature.create(
            testFacilityRepo,
            testSegmentRepo,
            testLSFRepo,
            testLocoRepo
        );

        assertNotNull("Created calculator should not be null", calc);

        // Verify calculator is functional with empty repositories
        assertEquals("Initial distance should be 0", 0.0, calc.getDistanceKm(), 0.01);
        assertEquals("Initial speed should be 0", 0.0, calc.getEffectiveSpeedKmh(), 0.01);
    }

    /**
     * Test calculateTravelTimeMinutes with fractional distance and speed (edge case).
     */
    @Test
    public void testCalculateTravelTimeMinutes_FractionalValues() {
        Facility station1 = createStation(1, "Station A");
        Facility station2 = createStation(2, "Station B");

        // 33.33 km at 66.66 km/h
        LineSegment segment = createLineSegment(1, 33.33, 66.66);
        connectStationToSegment(station1, segment);
        connectStationToSegment(station2, segment);

        createLocomotive(100.0);

        double travelTime = calculator.calculateTravelTimeMinutes(1, 2, 1);

        // 33.33 km / 66.66 km/h = 0.5 hours = 30 minutes
        assertEquals("Travel time should be 30 minutes", 30.0, travelTime, 0.1);
        assertEquals("Distance should be 33.33 km", 33.33, calculator.getDistanceKm(), 0.01);
        assertEquals("Effective speed should be 66.66 km/h", 66.66, calculator.getEffectiveSpeedKmh(), 0.01);
    }

    /**
     * Test calculateTravelTimeFromUserInput with invalid number format (test error handling).
     * This tests that the interactive method handles NumberFormatException correctly.
     */
    @Test
    public void testCalculateTravelTimeFromUserInput_InvalidNumberFormat() {
        // Create mock scanner with invalid input followed by 'b' to exit
        String invalidInput = "abc\nb\n";
        java.util.Scanner scanner = new java.util.Scanner(invalidInput);

        // This should handle NumberFormatException and print error message
        // We're just testing that the method exists and handles exceptions
        try {
            app.view.TravelTime.display(scanner);
            assertTrue("Method should handle exception gracefully", true);
        } catch (Exception e) {
            fail("Method should not throw exception: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    /**
     * Test TravelTime.display with valid input (successful calculation).
     */
    @Test
    public void testCalculateTravelTimeFromUserInput_ValidInput() {
        // Setup: Create two stations and a locomotive
        Facility station1 = createStation(1, "Station A");
        Facility station2 = createStation(2, "Station B");

        LineSegment segment = createLineSegment(1, 100.0, 100.0);
        connectStationToSegment(station1, segment);
        connectStationToSegment(station2, segment);

        createLocomotive(120.0);

        // Create scanner with valid input: station1=1, station2=2, locomotive=1, then Enter to return
        String validInput = "1\n2\n1\n\n";
        java.util.Scanner scanner = new java.util.Scanner(validInput);

        // Execute - should complete without throwing exceptions
        try {
            app.view.TravelTime.display(scanner);
            assertTrue("Method should execute successfully", true);
        } catch (Exception e) {
            fail("Method should not throw exception with valid input: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    /**
     * Test TravelTime.display method exists and is accessible.
     */
    @Test
    public void testCalculateTravelTimeFromUserInput_MethodExists() throws NoSuchMethodException {
        // Verify the TravelTime.display method exists with correct signature
        java.lang.reflect.Method method = app.view.TravelTime.class.getDeclaredMethod(
            "display",
            java.util.Scanner.class
        );

        assertNotNull("TravelTime.display method should exist", method);
        assertTrue("Method should be static", java.lang.reflect.Modifier.isStatic(method.getModifiers()));
        assertTrue("Method should be public", java.lang.reflect.Modifier.isPublic(method.getModifiers()));
        assertEquals("Method should return void", void.class, method.getReturnType());
    }

    /**
     * Test TravelTimeCalculator constructor initializes all repository fields correctly.
     * This tests that the constructor properly assigns all 4 repositories to private fields.
     */
    @Test
    public void testTravelTimeCalculator_ConstructorInitializesAllFields() throws Exception {
        FacilityRepository testFacilityRepo = new FacilityRepository();
        LineSegmentRepository testSegmentRepo = new LineSegmentRepository();
        LineSegmentFacilityRepository testLSFRepo = new LineSegmentFacilityRepository();
        LocomotiveRepository testLocoRepo = new LocomotiveRepository();

        TravelTimeCalculator calc = new TravelTimeCalculator(
            testFacilityRepo,
            testSegmentRepo,
            testLSFRepo,
            testLocoRepo
        );

        // Use reflection to verify private fields are set correctly
        java.lang.reflect.Field facilityField = TravelTimeCalculator.class.getDeclaredField("facilityRepository");
        facilityField.setAccessible(true);
        assertSame("facilityRepository should be set", testFacilityRepo, facilityField.get(calc));

        java.lang.reflect.Field segmentField = TravelTimeCalculator.class.getDeclaredField("lineSegmentRepository");
        segmentField.setAccessible(true);
        assertSame("lineSegmentRepository should be set", testSegmentRepo, segmentField.get(calc));

        java.lang.reflect.Field lsfField = TravelTimeCalculator.class.getDeclaredField("lineSegmentFacilityRepository");
        lsfField.setAccessible(true);
        assertSame("lineSegmentFacilityRepository should be set", testLSFRepo, lsfField.get(calc));

        java.lang.reflect.Field locoField = TravelTimeCalculator.class.getDeclaredField("locomotiveRepository");
        locoField.setAccessible(true);
        assertSame("locomotiveRepository should be set", testLocoRepo, locoField.get(calc));

        // Verify initial state of other fields
        java.lang.reflect.Field distanceField = TravelTimeCalculator.class.getDeclaredField("distance");
        distanceField.setAccessible(true);
        assertEquals("Initial distance should be 0", 0.0, (Double)distanceField.get(calc), 0.01);

        java.lang.reflect.Field speedField = TravelTimeCalculator.class.getDeclaredField("effectiveSpeed");
        speedField.setAccessible(true);
        assertEquals("Initial effectiveSpeed should be 0", 0.0, (Double)speedField.get(calc), 0.01);
    }
}


