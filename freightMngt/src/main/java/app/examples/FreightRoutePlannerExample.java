package app.examples;

import app.controller.FreightController;
import app.controller.FreightRouteController;
import app.controller.FreightRouteController.SegmentDefinition;
import app.model.*;
import app.model.Freight.FreightStatus;
import app.repositories.Repositories;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Example usage of the Freight Route Planner system.
 * Demonstrates how to create freights, routes, and assign them.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class FreightRoutePlannerExample {

    public static void main(String[] args) {
        System.out.println("=== Freight Route Planner Example ===\n");

        // Initialize controllers
        FreightController freightController = new FreightController();
        FreightRouteController routeController = new FreightRouteController();

        // Example 1: Simple Route
        System.out.println("Example 1: Creating a Simple Route");
        System.out.println("-----------------------------------");
        setupSimpleRouteExample(freightController, routeController);

        // Example 2: Complex Route
        System.out.println("\n\nExample 2: Creating a Complex Route");
        System.out.println("------------------------------------");
        setupComplexRouteExample(freightController, routeController);

        // Example 3: List Pending Freights
        System.out.println("\n\nExample 3: Listing Pending Freights");
        System.out.println("------------------------------------");
        listPendingFreights(freightController);
    }

    private static void setupSimpleRouteExample(FreightController freightController, FreightRouteController routeController) {
        // Create facilities
        FacilityType warehouseType = new FacilityType(1, "Warehouse");
        Facility warehouseA = new Facility(1, "Warehouse A", warehouseType, 40.7128, -74.0060, false);
        Facility warehouseB = new Facility(2, "Warehouse B", warehouseType, 41.8781, -87.6298, false);

        Repositories.getInstance().getFacilityRepository().addFacility(warehouseA);
        Repositories.getInstance().getFacilityRepository().addFacility(warehouseB);

        // Create item
        ItemCategory category = new ItemCategory(1, "Electronics");
        Item laptop = new Item(1, category, "LAPTOP001", "Laptop Computer", "UNIT", 0.05, 2.5);

        // Create freight
        Freight freight1 = new Freight(
                1,
                "Laptop Shipment",
                laptop,
                500.0,  // 500 kg
                5.0,    // 5 m³
                warehouseA,
                warehouseB,
                FreightStatus.PENDING,
                LocalDateTime.now().plusDays(7),
                "Handle with care - electronics"
        );
        freightController.createFreight(freight1);
        System.out.println("✓ Created freight: " + freight1.getDescription());

        // Create simple route
        FreightRoute simpleRoute = routeController.createSimpleRoute(
                1,
                "Direct A to B",
                1,
                2,
                "Express delivery route"
        );
        System.out.println("✓ Created route: " + simpleRoute.getRouteName() + " (Type: " + simpleRoute.getRouteType() + ")");

        // Assign route to freight
        freightController.assignRouteToFreight(1, 1);
        System.out.println("✓ Assigned route to freight");

        // Display route summary
        String summary = routeController.getRouteSummary(1);
        System.out.println("\nRoute Summary:");
        System.out.println(summary);
    }

    private static void setupComplexRouteExample(FreightController freightController, FreightRouteController routeController) {
        // Create additional facilities
        FacilityType stationType = new FacilityType(2, "Station");
        Facility stationA = new Facility(3, "Station A", stationType, 40.7128, -74.0060, false);
        Facility stationB = new Facility(4, "Station B", stationType, 41.8781, -87.6298, false);
        Facility stationC = new Facility(5, "Station C", stationType, 34.0522, -118.2437, false);
        Facility stationD = new Facility(6, "Station D", stationType, 37.7749, -122.4194, false);

        Repositories.getInstance().getFacilityRepository().addFacility(stationA);
        Repositories.getInstance().getFacilityRepository().addFacility(stationB);
        Repositories.getInstance().getFacilityRepository().addFacility(stationC);
        Repositories.getInstance().getFacilityRepository().addFacility(stationD);

        // Create line and segments
        Company railway = new Company(1, "Railway Company", "RC", "VAT123");
        User owner = new User(1, "Test User", "owner@example.com", "password", railway, true);
        Line mainLine = new Line(1, owner);
        Repositories.getInstance().getLineRepository().addLine(mainLine);

        TrackType standardTrack = new TrackType(1, "Standard");
        Gauge standardGauge = new Gauge(1, "Standard", 1435.0);

        LineSegment seg1 = new LineSegment(1, mainLine, 150.0, standardTrack, true, standardGauge, 20.0, 120.0, 2, 1);
        LineSegment seg2 = new LineSegment(2, mainLine, 200.0, standardTrack, true, standardGauge, 20.0, 120.0, 2, 2);
        LineSegment seg3 = new LineSegment(3, mainLine, 180.0, standardTrack, true, standardGauge, 20.0, 120.0, 2, 3);

        Repositories.getInstance().getLineSegmentRepository().addLineSegment(seg1);
        Repositories.getInstance().getLineSegmentRepository().addLineSegment(seg2);
        Repositories.getInstance().getLineSegmentRepository().addLineSegment(seg3);

        // Create freight
        ItemCategory category = new ItemCategory(2, "Machinery");
        Item machinery = new Item(2, category, "MACH001", "Industrial Machinery", "UNIT", 2.5, 500.0);

        Freight freight2 = new Freight(
                2,
                "Heavy Machinery Transport",
                machinery,
                5000.0,  // 5000 kg
                50.0,    // 50 m³
                stationA,
                stationD,
                FreightStatus.PENDING,
                LocalDateTime.now().plusDays(14),
                "Requires special loading equipment"
        );
        freightController.createFreight(freight2);
        System.out.println("✓ Created freight: " + freight2.getDescription());

        // Create complex route with multiple segments
        List<SegmentDefinition> segments = new ArrayList<>();
        segments.add(new SegmentDefinition(1, 3, 4, 1, "First segment - Load at B"));
        segments.add(new SegmentDefinition(2, 4, 5, 2, "Second segment - Transfer at C"));
        segments.add(new SegmentDefinition(3, 5, 6, 3, "Final segment - Unload at D"));

        FreightRoute complexRoute = routeController.createComplexRoute(
                2,
                "Multi-Stop A to D",
                3,
                6,
                segments,
                "Standard freight route with intermediate stops"
        );
        System.out.println("✓ Created route: " + complexRoute.getRouteName() + " (Type: " + complexRoute.getRouteType() + ")");
        System.out.println("  - Segments: " + complexRoute.getRouteSegments().size());
        System.out.println("  - Total Distance: " + String.format("%.2f", complexRoute.getTotalDistanceKm()) + " km");
        System.out.println("  - Estimated Duration: " + String.format("%.2f", complexRoute.getEstimatedDurationHours()) + " hours");

        // Assign route to freight
        freightController.assignRouteToFreight(2, 2);
        System.out.println("✓ Assigned route to freight");

        // Display route summary
        String summary = routeController.getRouteSummary(2);
        System.out.println("\nRoute Summary:");
        System.out.println(summary);
    }

    private static void listPendingFreights(FreightController freightController) {
        List<Freight> pendingFreights = freightController.getPendingFreights();
        System.out.println("Total pending freights: " + pendingFreights.size());

        List<Freight> withoutRoute = freightController.getFreightsWithoutRoute();
        System.out.println("Freights without assigned route: " + withoutRoute.size());

        System.out.println("\nPending Freights Details:");
        for (Freight freight : pendingFreights) {
            System.out.println("- ID: " + freight.getFreightId());
            System.out.println("  Description: " + freight.getDescription());
            System.out.println("  Origin: " + freight.getOriginFacility().getName());
            System.out.println("  Destination: " + freight.getDestinationFacility().getName());
            System.out.println("  Status: " + freight.getStatus());
            System.out.println("  Route Assigned: " + (freight.getAssignedRoute() != null ? "Yes (" + freight.getAssignedRoute().getRouteName() + ")" : "No"));
            System.out.println();
        }
    }
}

