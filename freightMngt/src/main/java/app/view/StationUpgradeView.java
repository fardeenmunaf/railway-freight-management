package app.view;

import app.model.FacilityType;
import app.model.Station;
import app.repositories.Repositories;
import app.service.sprint3.StationDependencyService;
import app.utils.CsvReader;
import app.utils.graph.StationGraphBuilder;
import app.utils.graph.matrix.MatrixGraph;

import java.util.*;
import java.util.stream.Collectors;

public class StationUpgradeView {

    private static final String BELGIAN_STATIONS_CSV = "src/main/java/app/sampleDataset/stations.csv";
    private static final String BELGIAN_LINES_CSV    = "src/main/java/app/sampleDataset/lines.csv";

    private static final int MAX_LISTED = 25;

    public static void display(Scanner scanner) {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║     BELGIAN STATION UPGRADE SCHEDULING (USEI11)            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("Stations must be upgraded in an order that respects their dependencies");
        System.out.println("(topological sort). Circular dependencies make such an order impossible,");
        System.out.println("so the algorithm also detects and reports them.\n");

        while (true) {
            System.out.println("1. Analyse the full Belgian network (cycle detection)");
            System.out.println("2. Plan upgrades for an acyclic dependency set (valid order)");
            System.out.println("0. Return to main menu");
            System.out.print("Choose an option: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1" -> run(false);
                case "2" -> run(true);
                case "0", "b", "B" -> { return; }
                default -> System.out.println("Invalid option!");
            }
            System.out.println();
        }
    }

    /**
     * Loads the Belgian network and runs the upgrade planner.
     *
     * @param acyclicOnly when true, every line is kept in a single direction
     *                    (lower station id -> higher station id), which guarantees a DAG
     */
    private static void run(boolean acyclicOnly) {
        try {
            Repositories repos = Repositories.getInstance();

            // 1) Load stations and dependencies
            List<Station> stations = loadBelgianStations(repos);
            List<int[]> deps = loadBelgianDependencies();

            // 2) Validate references
            if (!validateDependencies(stations, deps)) {
                System.out.println("\nERROR: lines.csv contains edges that reference non-existing station ids.");
                System.out.println("Upgrade planning aborted due to invalid data.\n");
                return;
            }

            if (acyclicOnly) {
                deps = deps.stream().filter(d -> d[0] < d[1]).collect(Collectors.toList());
                System.out.println("\nUsing each line in one direction only (lower id -> higher id).");
            }

            System.out.println("\nStations:     " + stations.size());
            System.out.println("Dependencies: " + deps.size());

            // 3) Build the station graph and plan the upgrades
            StationGraphBuilder builder = new StationGraphBuilder();
            MatrixGraph<Station, Double> graph = builder.buildGraph(stations, deps);

            StationDependencyService service = new StationDependencyService();
            StationDependencyService.Result<Double> result = service.planUpgrades(graph);

            // 4) Show the result
            if (!result.hasCycles()) {
                displayValidOrder(result);
            } else {
                displayCycles(result);
            }

        } catch (Exception e) {
            System.out.println("Error during station upgrade scheduling: " + e.getMessage());
        }
    }

    private static void displayValidOrder(StationDependencyService.Result<Double> result) {
        List<Station> order = result.getTopoOrder();
        System.out.println("\n✓ No circular dependencies — a valid upgrade order exists.");
        System.out.println("Upgrade order (first " + Math.min(MAX_LISTED, order.size()) + " of " + order.size() + " stations):\n");

        for (int i = 0; i < order.size() && i < MAX_LISTED; i++) {
            Station s = order.get(i);
            System.out.printf("%4d. %-28s (id %d)%n", i + 1, s.getName(), s.getFacilityId());
        }
        if (order.size() > MAX_LISTED) {
            System.out.println("   ... and " + (order.size() - MAX_LISTED) + " more");
        }
    }

    private static void displayCycles(StationDependencyService.Result<Double> result) {
        int nStations = result.getCycleStations().size();
        int nEdges = result.getCycleEdges().size();
        System.out.println("\n✗ Circular dependencies detected — no upgrade order can satisfy every dependency.");
        System.out.println("  " + nStations + " stations are part of cycles (" + nEdges + " dependencies).");
        System.out.println("  These cycles must be broken before an upgrade plan can be produced.\n");

        System.out.println("Stations in cycles (first " + Math.min(MAX_LISTED, nStations) + "):");
        result.getCycleStations().stream()
                .sorted(Comparator.comparingInt(Station::getFacilityId))
                .limit(MAX_LISTED)
                .forEach(s -> System.out.printf("   • %-28s (id %d)%n", s.getName(), s.getFacilityId()));
        if (nStations > MAX_LISTED) {
            System.out.println("   ... and " + (nStations - MAX_LISTED) + " more");
        }

        System.out.println("\nExample dependencies inside cycles:");
        result.getCycleEdges().stream()
                .limit(10)
                .forEach(e -> System.out.printf("   %s -> %s%n", e.getVOrig().getName(), e.getVDest().getName()));
        if (nEdges > 10) {
            System.out.println("   ... and " + (nEdges - 10) + " more");
        }
    }

    // ================== Helpers de carregamento/validação ==================

    private static List<Station> loadBelgianStations(Repositories repos) {
        List<Map<String, String>> rows = CsvReader.readCsv(BELGIAN_STATIONS_CSV, ",");
        List<Station> result = new ArrayList<>();

        FacilityType stationType = repos.getFacilityTypeRepository().getFacilityTypeByName("Station");
        if (stationType == null) {
            stationType = new FacilityType(2, "Station");
            repos.getFacilityTypeRepository().addFacilityType(stationType);
        }

        for (Map<String, String> row : rows) {
            try {
                int id = Integer.parseInt(row.get("Station id").trim());
                Station s = new Station(
                        id, row.get("Station"), stationType,
                        parseDouble(row.get("Lat")), parseDouble(row.get("Lon")),
                        false, null, null, null, null, null, null
                );
                repos.getFacilityRepository().addFacility(s);
                result.add(s);
            } catch (Exception ignored) {}
        }
        return result;
    }

    private static List<int[]> loadBelgianDependencies() {
        return CsvReader.readCsv(BELGIAN_LINES_CSV, ",").stream()
                .map(row -> {
                    try {
                        return new int[]{
                                Integer.parseInt(row.get("departure_stid").trim()),
                                Integer.parseInt(row.get("arrival_stid").trim())
                        };
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static boolean validateDependencies(List<Station> stations, List<int[]> deps) {
        Set<Integer> stationIds = stations.stream()
                .map(Station::getFacilityId)
                .collect(Collectors.toSet());

        boolean valid = true;

        for (int[] dep : deps) {
            int from = dep[0];
            int to   = dep[1];

            boolean fromOk = stationIds.contains(from);
            boolean toOk   = stationIds.contains(to);

            if (!fromOk || !toOk) {
                System.out.printf("Invalid dependency: %d -> %d (missing %s%s)%n",
                        from, to,
                        fromOk ? "" : "origin",
                        (!fromOk && !toOk) ? " and destination" : (!toOk ? " destination" : ""));
                valid = false;
            }
        }

        return valid;
    }

    private static Double parseDouble(String value) {
        try {
            return value == null ? null : Double.parseDouble(value.trim());
        } catch (Exception e) {
            return null;
        }
    }
}
