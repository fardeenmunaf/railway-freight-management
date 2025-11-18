package app.view;

import app.model.Station;
import app.service.StationService;
import app.utils.Balanced2DTreeBuilder;

import java.util.List;
import java.util.Scanner;

/**
 * Interactive view for demonstrating N-nearest station search functionality.
 * Allows users to query for nearest stations with optional filters.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class NearestStationView {

    private static StationService stationService;

    /**
     * Display method that follows the MainMenu pattern.
     * Receives a Scanner from the main menu.
     */
    public static void display(Scanner scanner) {
        // Initialize service if needed
        if (stationService == null) {
            stationService = new StationService();
            System.out.println("Building station indexes...");
            stationService.buildIndexes();
            System.out.println("Indexes built successfully!\n");
        }

        boolean running = true;
        while (running) {
            System.out.println("\n=== N-Nearest Station Search ===");
            System.out.println("1. Find N nearest stations");
            System.out.println("2. Find N nearest stations (with time-zone filter)");
            System.out.println("3. Find single nearest station");
            System.out.println("4. Display complexity analysis");
            System.out.println("5. Test with sample locations");
            System.out.println("0. Return to Main Menu");
            System.out.print("Choose option: ");

            int choice = readInt(scanner);

            switch (choice) {
                case 1:
                    findNearestStations(scanner);
                    break;
                case 2:
                    findNearestStationsWithFilter(scanner);
                    break;
                case 3:
                    findSingleNearestStation(scanner);
                    break;
                case 4:
                    displayComplexityAnalysis();
                    break;
                case 5:
                    runSampleTests();
                    break;
                case 0:
                    running = false;
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    /**
     * Find N nearest stations to given coordinates.
     */
    private static void findNearestStations(Scanner scanner) {
        System.out.println("\n--- Find N Nearest Stations ---");

        System.out.print("Enter latitude: ");
        double latitude = readDouble(scanner);

        System.out.print("Enter longitude: ");
        double longitude = readDouble(scanner);

        System.out.print("Enter number of stations (N): ");
        int n = readInt(scanner);

        if (n <= 0) {
            System.out.println("N must be positive!");
            return;
        }

        long startTime = System.nanoTime();
        List<StationService.StationDistance> results =
            stationService.findNearestStations(latitude, longitude, n);
        long endTime = System.nanoTime();

        displayResults(results, latitude, longitude, endTime - startTime);
    }

    /**
     * Find N nearest stations with time-zone group filter.
     */
    private static void findNearestStationsWithFilter(Scanner scanner) {
        System.out.println("\n--- Find N Nearest Stations (with Time-Zone Filter) ---");

        System.out.print("Enter latitude: ");
        double latitude = readDouble(scanner);

        System.out.print("Enter longitude: ");
        double longitude = readDouble(scanner);

        System.out.print("Enter number of stations (N): ");
        int n = readInt(scanner);

        System.out.print("Enter time-zone group (e.g., CET, WET/GMT): ");
        scanner.nextLine(); // consume newline
        String timeZoneGroup = scanner.nextLine().trim();

        if (n <= 0) {
            System.out.println("N must be positive!");
            return;
        }

        long startTime = System.nanoTime();
        List<StationService.StationDistance> results =
            stationService.findNearestStations(latitude, longitude, n, timeZoneGroup);
        long endTime = System.nanoTime();

        System.out.println("\nFilter: Time-Zone Group = " + timeZoneGroup);
        displayResults(results, latitude, longitude, endTime - startTime);
    }

    /**
     * Find single nearest station.
     */
    private static void findSingleNearestStation(Scanner scanner) {
        System.out.println("\n--- Find Single Nearest Station ---");

        System.out.print("Enter latitude: ");
        double latitude = readDouble(scanner);

        System.out.print("Enter longitude: ");
        double longitude = readDouble(scanner);

        long startTime = System.nanoTime();
        StationService.StationDistance result =
            stationService.findNearestStation(latitude, longitude);
        long endTime = System.nanoTime();

        System.out.println("\nQuery Point: (" + latitude + ", " + longitude + ")");
        System.out.println("Search Time: " + formatTime(endTime - startTime));

        if (result != null) {
            System.out.println("\n✓ Nearest Station Found:");
            displayStationDetails(result, 1);
        } else {
            System.out.println("\n✗ No stations found!");
        }
    }

    /**
     * Display temporal complexity analysis.
     */
    private static void displayComplexityAnalysis() {
        System.out.println("\n=== Temporal Complexity Analysis ===");
        Balanced2DTreeBuilder.TemporalComplexity complexity =
            stationService.get2DTreeComplexity();
        System.out.println(complexity.toString());
        System.out.println("\nImplementation Details:");
        System.out.println("  • Algorithm: KD-tree with branch pruning");
        System.out.println("  • Distance metric: Haversine formula");
        System.out.println("  • Earth radius: 6371.0 km");
        System.out.println("  • Pruning strategy: Spatial plane distance");
    }

    /**
     * Run sample tests with known locations.
     */
    private static void runSampleTests() {
        System.out.println("\n=== Sample Location Tests ===\n");

        // Test 1: Lisbon city center
        System.out.println("Test 1: Nearest to Lisbon City Center (38.7167, -9.1333)");
        testLocation(38.7167, -9.1333, 3, null);

        // Test 2: Porto city center
        System.out.println("\nTest 2: Nearest to Porto City Center (41.1579, -8.6291)");
        testLocation(41.1579, -8.6291, 3, null);

        // Test 3: Madrid with WET/GMT filter (should get Portuguese stations)
        System.out.println("\nTest 3: Near Madrid but filtered by WET/GMT time-zone");
        testLocation(40.4068, -3.6920, 2, "WET/GMT");

        // Test 4: Exact station coordinates
        System.out.println("\nTest 4: Exact coordinates of a station");
        testLocation(38.7223, -9.1393, 1, null);
    }

    /**
     * Test a specific location.
     */
    private static void testLocation(double lat, double lon, int n, String timeZoneFilter) {
        long startTime = System.nanoTime();
        List<StationService.StationDistance> results;

        if (timeZoneFilter != null) {
            results = stationService.findNearestStations(lat, lon, n, timeZoneFilter);
        } else {
            results = stationService.findNearestStations(lat, lon, n);
        }

        long endTime = System.nanoTime();
        displayResults(results, lat, lon, endTime - startTime);
    }

    /**
     * Display search results.
     */
    private static void displayResults(List<StationService.StationDistance> results,
                                double queryLat, double queryLon, long nanoTime) {
        System.out.println("\nQuery Point: (" + queryLat + ", " + queryLon + ")");
        System.out.println("Search Time: " + formatTime(nanoTime));
        System.out.println("Results Found: " + results.size());

        if (results.isEmpty()) {
            System.out.println("\n✗ No stations found!");
            return;
        }

        System.out.println("\n┌─────┬─────────────────────────────────┬──────────────┬───────────────┬──────────────┐");
        System.out.println("│ #   │ Station Name                    │ Distance (km)│ Country       │ Time Zone Grp│");
        System.out.println("├─────┼─────────────────────────────────┼──────────────┼───────────────┼──────────────┤");

        for (int i = 0; i < results.size(); i++) {
            StationService.StationDistance result = results.get(i);
            Station station = result.station;

            System.out.printf("│ %-3d │ %-31s │ %12.2f │ %-13s │ %-12s │%n",
                i + 1,
                truncate(station.getName(), 31),
                result.distanceKm,
                truncate(station.getCountry(), 13),
                truncate(station.getTimeZoneGroup(), 12)
            );
        }

        System.out.println("└─────┴─────────────────────────────────┴──────────────┴───────────────┴──────────────┘");

        // Display verification note
        if (!results.isEmpty()) {
            System.out.println("\n✓ Results are sorted by distance (ascending order)");
            System.out.println("✓ Distances calculated using Haversine formula");
        }
    }

    /**
     * Display detailed station information.
     */
    private static void displayStationDetails(StationService.StationDistance result, int rank) {
        Station station = result.station;
        System.out.println("  Rank: #" + rank);
        System.out.println("  Name: " + station.getName());
        System.out.println("  Coordinates: (" + station.getLat() + ", " + station.getLon() + ")");
        System.out.println("  Distance: " + String.format("%.2f km", result.distanceKm));
        System.out.println("  Country: " + station.getCountry());
        System.out.println("  Time Zone: " + station.getTimeZone());
        System.out.println("  Time Zone Group: " + station.getTimeZoneGroup());
    }

    /**
     * Format nanoseconds to readable time.
     */
    private static String formatTime(long nanos) {
        if (nanos < 1_000) {
            return nanos + " ns";
        } else if (nanos < 1_000_000) {
            return String.format("%.2f μs", nanos / 1_000.0);
        } else if (nanos < 1_000_000_000) {
            return String.format("%.2f ms", nanos / 1_000_000.0);
        } else {
            return String.format("%.2f s", nanos / 1_000_000_000.0);
        }
    }

    /**
     * Truncate string to specified length.
     */
    private static String truncate(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }

    /**
     * Read integer from user input.
     */
    private static int readInt(Scanner scanner) {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (Exception e) {
                System.out.print("Invalid input! Enter a number: ");
                scanner.nextLine(); // clear buffer
            }
        }
    }

    /**
     * Read double from user input.
     */
    private static double readDouble(Scanner scanner) {
        while (true) {
            try {
                return scanner.nextDouble();
            } catch (Exception e) {
                System.out.print("Invalid input! Enter a number: ");
                scanner.nextLine(); // clear buffer
            }
        }
    }
}
