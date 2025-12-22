package app.test;

import app.controller.FreightRouteController;
import app.model.*;
import app.repositories.Repositories;

/**
 * Quick test to verify distance and duration calculation for routes.
 */
public class RouteCalculationTest {

    public static void main(String[] args) {
        System.out.println("=== Route Distance & Duration Calculation Test ===\n");

        // Clear repositories
        Repositories.getInstance().getFacilityRepository().clear();

        // Create test facilities
        FacilityType facilityType = new FacilityType(1, "Warehouse");

        // New York coordinates: 40.7128° N, 74.0060° W
        Facility facilityNY = new Facility(1, "New York Warehouse", facilityType, 40.7128, -74.0060, false);

        // Los Angeles coordinates: 34.0522° N, 118.2437° W
        Facility facilityLA = new Facility(2, "Los Angeles Warehouse", facilityType, 34.0522, -118.2437, false);

        Repositories.getInstance().getFacilityRepository().addFacility(facilityNY);
        Repositories.getInstance().getFacilityRepository().addFacility(facilityLA);

        // Create FreightRouteController
        FreightRouteController routeController = new FreightRouteController();

        System.out.println("Test 1: Simple Route NY -> LA");
        System.out.println("----------------------------------------");

        FreightRoute simpleRoute = routeController.createSimpleRoute(
            1,
            "NY to LA Direct",
            1,
            2,
            "Cross-country route"
        );

        System.out.println("Route ID: " + simpleRoute.getRouteId());
        System.out.println("Route Name: " + simpleRoute.getRouteName());
        System.out.println("Route Type: " + simpleRoute.getRouteType());
        System.out.println("Start: " + simpleRoute.getStartFacility().getName());
        System.out.println("End: " + simpleRoute.getEndFacility().getName());
        System.out.println("Total Distance: " + String.format("%.2f", simpleRoute.getTotalDistanceKm()) + " km");
        System.out.println("Estimated Duration: " + String.format("%.2f", simpleRoute.getEstimatedDurationHours()) + " hours");
        System.out.println("Average Speed: 80 km/h (assumed for simple routes)");

        // Expected distance: approximately 3,944 km (straight line)
        System.out.println("\n✓ Expected distance: ~3,944 km (NY to LA straight line)");

        if (simpleRoute.getTotalDistanceKm() > 3900 && simpleRoute.getTotalDistanceKm() < 4000) {
            System.out.println("✓ Distance calculation: PASS");
        } else {
            System.out.println("✗ Distance calculation: FAIL (got " + simpleRoute.getTotalDistanceKm() + " km)");
        }

        if (simpleRoute.getEstimatedDurationHours() > 0) {
            System.out.println("✓ Duration calculation: PASS");
        } else {
            System.out.println("✗ Duration calculation: FAIL");
        }

        System.out.println("\n=== Test Complete ===");
    }
}

