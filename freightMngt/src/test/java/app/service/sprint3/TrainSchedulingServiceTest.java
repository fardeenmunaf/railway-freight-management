package app.service.sprint3;

import app.model.*;
import app.repositories.Repositories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para TrainSchedulingService.
 */
public class TrainSchedulingServiceTest {

    private TrainSchedulingService service;

    @BeforeEach
    void setUp() {
        service = new TrainSchedulingService();
    }

    private Facility facility(int id, String name) {
        Facility f = new Facility();
        f.setFacilityId(id);
        f.setName(name);
        return f;
    }

    private LineSegment singleTrackSegment(double lengthKm) {
        LineSegment line = new LineSegment();
        line.setTrackNumber(1); // single track
        line.setLengthKm(lengthKm);
        return line;
    }

    private LineSegment doubleTrackSegment(double lengthKm) {
        LineSegment line = new LineSegment();
        line.setTrackNumber(2);
        line.setLengthKm(lengthKm);
        return line;
    }

    /**
     * Testa agendamento manual simples sem conflitos.
     */
    @Test
    void testManualDispatchNoConflict() {
        Facility a = facility(1, "A");
        Facility b = facility(2, "B");

        RoutePoint rp1 = new RoutePoint();
        rp1.setFacility(a);
        RoutePoint rp2 = new RoutePoint();
        rp2.setFacility(b);

        LineSegment line = singleTrackSegment(100);
        rp1.setSegmentToNext(line);

        List<RoutePoint> manualRoute = List.of(rp1, rp2);

        TrainService trainService = new TrainService();
        trainService.setServiceId(1);

        TrainDispatch dispatch = service.scheduleDispatch(
                1,
                trainService,
                null,
                LocalDateTime.of(2025, 12, 31, 12, 0),
                "MANUAL",
                manualRoute,
                new ArrayList<>()
        );

        assertEquals(2, dispatch.getRoute().size());
        assertEquals("NONE", dispatch.getRoute().get(1).getCrossingOperation());
        assertTrue(dispatch.getTotalDistanceKm() > 0);
    }

    /**
     * Testa agendamento manual com SINGLE TRACK e conflito.
     */
    @Test
    void testSingleTrackCrossingDelay() {
        Facility a = facility(1, "A");
        Facility b = facility(2, "B");

        LineSegment line = singleTrackSegment(10);

        // Trem existente ocupando a linha
        RoutePoint existingRp1 = new RoutePoint();
        existingRp1.setFacility(a);
        existingRp1.setSegmentToNext(line);
        existingRp1.setEstimatedDepartureTime(LocalDateTime.of(2025, 12, 31, 12, 0));
        existingRp1.setEstimatedArrivalTime(LocalDateTime.of(2025, 12, 31, 12, 10));

        RoutePoint existingRp2 = new RoutePoint();
        existingRp2.setFacility(b);
        existingRp2.setSegmentToNext(null);
        existingRp2.setEstimatedDepartureTime(LocalDateTime.of(2025, 12, 31, 12, 10));
        existingRp2.setEstimatedArrivalTime(LocalDateTime.of(2025, 12, 31, 12, 20));

        TrainDispatch existingDispatch = new TrainDispatch();
        existingDispatch.setRoute(List.of(existingRp1, existingRp2));

        List<TrainDispatch> existingDispatches = List.of(existingDispatch);

        // Novo trem que vai passar pelo mesmo segmento
        RoutePoint rp1 = new RoutePoint();
        rp1.setFacility(a);
        rp1.setSegmentToNext(line);
        RoutePoint rp2 = new RoutePoint();
        rp2.setFacility(b);

        List<RoutePoint> manualRoute = List.of(rp1, rp2);

        TrainService trainService = new TrainService();
        trainService.setServiceId(2);

        TrainDispatch dispatch = service.scheduleDispatch(
                2,
                trainService,
                null,
                LocalDateTime.of(2025, 12, 31, 12, 5), // conflito de horário
                "MANUAL",
                manualRoute,
                existingDispatches
        );

        RoutePoint lastPoint = dispatch.getRoute().get(1);

        assertEquals("WAIT_FOR_CROSSING", lastPoint.getCrossingOperation(), "Deve esperar na linha única");
        assertTrue(lastPoint.getExpectedWaitTimeMinutes() > 0, "Tempo de espera deve ser maior que 0");
    }

    /**
     * Testa agendamento automático.
     */
    @Test
    void testAutomaticDispatch() {
        Facility a = facility(1, "A");
        Facility b = facility(2, "B");
        Facility c = facility(3, "C");

        LineSegment line1 = doubleTrackSegment(50);
        LineSegment line2 = doubleTrackSegment(60);

        FreightRouteSegment seg1 = new FreightRouteSegment();
        seg1.setFromFacility(a);
        seg1.setToFacility(b);
        seg1.setLineSegment(line1);
        seg1.setSequenceOrder(1);

        FreightRouteSegment seg2 = new FreightRouteSegment();
        seg2.setFromFacility(b);
        seg2.setToFacility(c);
        seg2.setLineSegment(line2);
        seg2.setSequenceOrder(2);

        FreightRoute route = new FreightRoute();
        route.setRouteSegments(List.of(seg1, seg2));

        TrainService trainService = new TrainService();
        trainService.setServiceId(3);

        TrainDispatch dispatch = service.scheduleDispatch(
                3,
                trainService,
                route,
                LocalDateTime.of(2025, 12, 31, 10, 0),
                "AUTOMATIC",
                null,
                new ArrayList<>()
        );

        assertEquals(3, dispatch.getRoute().size()); // A, B, C
        assertEquals(0, dispatch.getRoute().get(0).getExpectedWaitTimeMinutes());
        assertEquals(0, dispatch.getRoute().get(1).getExpectedWaitTimeMinutes());
    }
}