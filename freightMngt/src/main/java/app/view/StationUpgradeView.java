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

    // Scanner não é usado aqui, mas fica na assinatura para ser consistente com o menu
    public static void display(Scanner scanner) {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║     BELGIAN STATION UPGRADE SCHEDULING (USEI11)            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");

        try {
            Repositories repos = Repositories.getInstance();

            // 1) Carregar estações e dependências
            List<Station> stations = loadBelgianStations(repos);
            List<int[]> deps = loadBelgianDependencies();

            System.out.println("Loaded Stations: " + stations.size());
            System.out.println("Loaded Dependencies: " + deps.size() + "\n");

            // 2) Validar referências inválidas
            if (!validateDependencies(stations, deps)) {
                System.out.println("\nERROR: lines.csv contains edges that reference non‑existing station ids.");
                System.out.println("Upgrade planning aborted due to invalid data.\n");
                return;
            }

            // 3) Construir grafo de estações
            StationGraphBuilder builder = new StationGraphBuilder();
            MatrixGraph<Station, Double> graph = builder.buildGraph(stations, deps);

            // 4) Calcular plano de upgrades (serviço de domínio)
            StationDependencyService service = new StationDependencyService();
            StationDependencyService.Result<Double> result = service.planUpgrades(graph);

            // 5) Mostrar resultado conforme haja ciclos ou não
            if (!result.hasCycles()) {
                displayValidOrder(result);
            } else {
                displayCycles(result);
            }

        } catch (Exception e) {
            System.out.println("Error during station upgrade scheduling: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void displayValidOrder(StationDependencyService.Result<Double> result) {
        System.out.println("\nNo cycles detected.");
        System.out.println("Valid upgrade order of stations:\n");

        int i = 1;
        for (Station s : result.getTopoOrder()) {
            System.out.printf("%3d. %4d - %s%n", i++, s.getFacilityId(), s.getName());
        }
    }

    private static void displayCycles(StationDependencyService.Result<Double> result) {
        System.out.println("\nGRAPH HAS CYCLES — upgrade schedule impossible.\n");

        System.out.println("Stations involved in cycles (" + result.getCycleStations().size() + "):");
        result.getCycleStations().stream()
                .sorted(Comparator.comparingInt(Station::getFacilityId))
                .forEach(s -> System.out.println(" • " + s.getFacilityId() + " - " + s.getName()));

        System.out.println("\nCycle Edges (" + result.getCycleEdges().size() + "):");
        result.getCycleEdges().forEach(e ->
                System.out.println(" → " + e.getVOrig().getFacilityId() + " -> " + e.getVDest().getFacilityId()));
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
