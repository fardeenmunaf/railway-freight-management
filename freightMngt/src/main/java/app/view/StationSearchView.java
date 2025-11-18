package app.view;

import app.model.Station;
import app.service.sprint2.StationService;

import java.util.List;
import java.util.Scanner;

/**
 * View class for station search functionality.
 * Handles user interaction and displays search results.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class StationSearchView {

    /**
     * Displays the main menu for station search operations.
     */
    public static void display(Scanner scanner) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("         STATION SEARCH");
        System.out.println("=".repeat(60));
        System.out.println("Search and filter stations by various criteria");
        System.out.println("Type 'b' at any time to return to menu.\n");

        StationService stationService = new StationService();
        stationService.buildIndexes();

        boolean running = true;

        while (running) {
            displayMenu();
            String input = scanner.nextLine().trim();

            if ("b".equalsIgnoreCase(input)) {
                return;
            }

            try {
                int choice = Integer.parseInt(input);

                switch (choice) {
                    case 1 -> searchByName(scanner, stationService);
                    case 2 -> searchByCountry(scanner, stationService);
                    case 3 -> searchByTimeZone(scanner, stationService);
                    case 4 -> searchByTimeZoneGroup(scanner, stationService);
                    case 5 -> searchByTimeZoneGroupRange(scanner, stationService);
                    case 6 -> searchByLatitudeRange(scanner, stationService);
                    case 7 -> listMainStations(stationService);
                    case 8 -> listAirports(stationService);
                    case 9 -> searchByCoordinates(scanner, stationService);
                    case 0 -> running = false;
                    default -> System.out.println("Invalid option!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\n=== Station Search ===");
        System.out.println("1. Search by name");
        System.out.println("2. Search by country");
        System.out.println("3. Search by time zone");
        System.out.println("4. Search by time zone group");
        System.out.println("5. Search by time zone group range");
        System.out.println("6. Search by latitude range");
        System.out.println("7. List main stations");
        System.out.println("8. List airports");
        System.out.println("9. Search by coordinates");
        System.out.println("0. Back");
        System.out.print("\nChoose an option: ");
    }

    private static void searchByName(Scanner scanner, StationService stationService) {
        System.out.print("Station name: ");
        String name = scanner.nextLine().trim();
        if ("b".equalsIgnoreCase(name)) return;

        List<Station> results = stationService.searchByName(name);
        displayResults(results, "Search by name: " + name);
    }

    private static void searchByCountry(Scanner scanner, StationService stationService) {
        System.out.print("Country code (e.g., pt, es, fr): ");
        String country = scanner.nextLine().trim();
        if ("b".equalsIgnoreCase(country)) return;

        List<Station> results = stationService.searchByCountry(country);
        displayResults(results, "Stations in: " + country.toUpperCase());
    }

    private static void searchByTimeZone(Scanner scanner, StationService stationService) {
        System.out.print("Time zone (e.g., Europe/Lisbon): ");
        String timeZone = scanner.nextLine().trim();
        if ("b".equalsIgnoreCase(timeZone)) return;

        List<Station> results = stationService.searchByTimeZone(timeZone);
        displayResults(results, "Stations in time zone: " + timeZone);
    }

    private static void searchByTimeZoneGroup(Scanner scanner, StationService stationService) {
        System.out.print("Time zone group (e.g., CET, WET/GMT): ");
        String group = scanner.nextLine().trim();
        if ("b".equalsIgnoreCase(group)) return;

        List<Station> results = stationService.searchByTimeZoneGroupOrderedByCountry(group);
        displayResults(results, "Stations in " + group + " (ordered by country)");
    }

    private static void searchByTimeZoneGroupRange(Scanner scanner, StationService stationService) {
        System.out.print("From time zone group (e.g., CET): ");
        String fromGroup = scanner.nextLine().trim();
        if ("b".equalsIgnoreCase(fromGroup)) return;

        System.out.print("To time zone group (e.g., WET/GMT): ");
        String toGroup = scanner.nextLine().trim();
        if ("b".equalsIgnoreCase(toGroup)) return;

        List<Station> results = stationService.searchByTimeZoneGroupRange(fromGroup, toGroup);
        displayResults(results, "Stations in time zone group range [" + fromGroup + ", " + toGroup + "]");
    }

    private static void searchByLatitudeRange(Scanner scanner, StationService stationService) {
        try {
            System.out.print("Minimum latitude (e.g., 36.0): ");
            String minLatStr = scanner.nextLine().trim();
            if ("b".equalsIgnoreCase(minLatStr)) return;
            double minLat = Double.parseDouble(minLatStr);

            System.out.print("Maximum latitude (e.g., 43.0): ");
            String maxLatStr = scanner.nextLine().trim();
            if ("b".equalsIgnoreCase(maxLatStr)) return;
            double maxLat = Double.parseDouble(maxLatStr);

            if (minLat < -90.0 || minLat > 90.0 || maxLat < -90.0 || maxLat > 90.0) {
                System.out.println("ERROR: Latitude must be between -90 and 90.");
                return;
            }

            if (minLat > maxLat) {
                System.out.println("ERROR: Minimum latitude cannot be greater than maximum.");
                return;
            }

            List<Station> results = stationService.searchByLatitudeRange(minLat, maxLat);
            displayResults(results, "Stations with latitude [" + minLat + ", " + maxLat + "]");
        } catch (NumberFormatException e) {
            System.out.println("ERROR: Invalid latitude value.");
        }
    }

    private static void listMainStations(StationService stationService) {
        List<Station> results = stationService.getMainStations();
        displayResults(results, "Main Stations");
    }

    private static void listAirports(StationService stationService) {
        List<Station> results = stationService.getAirportStations();
        displayResults(results, "Airports");
    }

    private static void displayResults(List<Station> stations, String title) {
        System.out.println("\n" + "-".repeat(60));
        System.out.println(title);
        System.out.println("-".repeat(60));

        if (stations.isEmpty()) {
            System.out.println("No stations found.");
            return;
        }

        System.out.println("Found " + stations.size() + " station(s):\n");

        for (Station station : stations) {
            displayStation(station);
        }
    }

    private static void displayStation(Station station) {
        System.out.printf("• %s (%s)%n", station.getName(), station.getCountry());
        System.out.printf("  Coordinates: [%.4f, %.4f]%n", station.getLat(), station.getLon());
        System.out.printf("  Time zone: %s | Group: %s%n",
                station.getTimeZone(), station.getTimeZoneGroup());

        List<String> flags = new java.util.ArrayList<>();
        if (Boolean.TRUE.equals(station.getIsMainStation())) flags.add("MAIN");
        if (Boolean.TRUE.equals(station.getIsAirport())) flags.add("AIRPORT");
        if (Boolean.TRUE.equals(station.getIsCity())) flags.add("CITY");

        if (!flags.isEmpty()) {
            System.out.printf("  Type: %s%n", String.join(", ", flags));
        }

        System.out.println();
    }

    private static void searchByCoordinates(Scanner scanner, StationService stationService) {
        try {
            System.out.print("Latitude: ");
            String latStr = scanner.nextLine().trim();
            if ("b".equalsIgnoreCase(latStr)) return;
            double lat = Double.parseDouble(latStr);

            System.out.print("Longitude: ");
            String lonStr = scanner.nextLine().trim();
            if ("b".equalsIgnoreCase(lonStr)) return;
            double lon = Double.parseDouble(lonStr);

            if (lat < -90.0 || lat > 90.0 || lon < -180.0 || lon > 180.0) {
                System.out.println("ERROR: Invalid coordinates range.");
                return;
            }

            List<Station> results = stationService.searchByCoordinates(lat, lon);
            displayResults(results, "Stations at coordinates [" + lat + ", " + lon + "]");
        } catch (NumberFormatException e) {
            System.out.println("ERROR: Invalid coordinate value.");
        }
    }
}
