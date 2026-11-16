package app.view;

import app.model.Station;
import app.service.sprint2.RangeQueryService;
import app.service.sprint2.RangeQueryService.RangeQueryResult;
import app.service.sprint2.StationService;
import app.utils.Balanced2DTreeBuilder.Balanced2DTree;

import java.util.List;
import java.util.Scanner;

public class RangeQueryView {

    public static void display(Scanner scanner) {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║           RANGE QUERY - 2D TREE SEARCH                     ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");

        // Mostrar queries de exemplo
        System.out.println("\nSample Queries:");
        System.out.println("1. Iberian Peninsula: lat[36,44], lon[-10,4], country=ALL");
        System.out.println("2. Portugal Main Stations: lat[37,42], lon[-9.5,-6], country=PT, mainStation=true");
        System.out.println("3. Spanish Cities: lat[36,44], lon[-10,4], country=ES, city=true");
        System.out.println("4. PT + ES Combined: lat[36,44], lon[-10,4], country=PT,ES");
        System.out.println("5. Wide Search: lat[35,50], lon[-15,10]\n");

        try {
            // Inicializar serviços
            StationService stationService = new StationService();
            Balanced2DTree tree = stationService.getSpatial2DTree();

            if (tree == null || tree.getSize() == 0) {
                System.out.println("No stations available in the 2D tree.");
                return;
            }

            System.out.println("Tree Size: " + tree.getSize() + " nodes");
            System.out.println("Tree Height: " + tree.getHeight());
            System.out.println();

            RangeQueryService rangeQueryService = new RangeQueryService(tree);

            // Ler limites de latitude
            System.out.print("Enter minimum latitude: ");
            double latMin = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Enter maximum latitude: ");
            double latMax = Double.parseDouble(scanner.nextLine().trim());

            // Ler limites de longitude
            System.out.print("Enter minimum longitude: ");
            double lonMin = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Enter maximum longitude: ");
            double lonMax = Double.parseDouble(scanner.nextLine().trim());

            // Validar limites
            if (latMin > latMax || lonMin > lonMax) {
                    System.out.println("Error: Minimum values cannot be greater than maximum values.");
                return;
            }

            // Filtros opcionais
            Boolean isCity = readOptionalBoolean(scanner, "Filter by city? (y/n/skip): ");
            Boolean isMainStation = readOptionalBoolean(scanner, "Filter by main station? (y/n/skip): ");

            System.out.print("Filter by country (PT, ES, PT,ES, or ALL): ");
            String country = scanner.nextLine().trim();
            if (country.isEmpty() || country.equalsIgnoreCase("ALL")) {
                country = null;
            }

            // Executar query
            System.out.println("\nSearching for stations in range...");
            RangeQueryResult result = rangeQueryService.rangeQuery(
                    latMin, latMax, lonMin, lonMax,
                    isCity, isMainStation, country
            );

            // Mostrar métricas de performance
            displayPerformanceMetrics(result);

            // Mostrar resultados
            displayResults(result.stations);

        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid number format. Please enter valid coordinates.");
        } catch (Exception e) {
            System.out.println("Error during range query: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void displayPerformanceMetrics(RangeQueryResult result) {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              PERFORMANCE ANALYSIS                          ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.printf("Execution Time: %.3f ms%n", result.executionTimeMs);
        System.out.printf("Results Found: %d / %d stations (%.2f%%)%n",
                result.resultCount, result.treeSize, result.getEfficiency());
        System.out.printf("Complexity Estimate: %s%n", result.getComplexityEstimate());
        System.out.println("─".repeat(60));
    }

    private static void displayResults(List<Station> results) {
        if (results.isEmpty()) {
            System.out.println("\nNo stations found in the specified range.");
            return;
        }

        System.out.println("\nFound " + results.size() + " station(s):");
        System.out.println("─".repeat(100));
        System.out.printf("%-40s %-12s %-12s %-10s %-15s %-10s%n",
                "Name", "Latitude", "Longitude", "City", "Main Station", "Country");
        System.out.println("─".repeat(100));

        int limit = 50; // keep long result lists readable in the console
        for (int i = 0; i < results.size() && i < limit; i++) {
            Station station = results.get(i);
            System.out.printf("%-40s %-12.6f %-12.6f %-10s %-15s %-10s%n",
                    truncate(station.getName(), 39),
                    station.getLat(),
                    station.getLon(),
                    station.getIsCity() ? "Yes" : "No",
                    station.getIsMainStation() ? "Yes" : "No",
                    station.getCountry() != null ? station.getCountry() : "N/A"
            );
        }
        System.out.println("─".repeat(100));
        if (results.size() > limit) {
            System.out.println("... and " + (results.size() - limit) + " more (showing the first " + limit + ")");
        }
    }

    private static Boolean readOptionalBoolean(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim().toLowerCase();

        if (input.isEmpty() || input.equals("skip")) {
            return null;
        } else if (input.equals("y") || input.equals("yes")) {
            return true;
        } else if (input.equals("n") || input.equals("no")) {
            return false;
        } else {
            return null;
        }
    }

    private static String truncate(String str, int maxLength) {
        if (str == null) return "";
        return str.length() <= maxLength ? str : str.substring(0, maxLength - 3) + "...";
    }
}
