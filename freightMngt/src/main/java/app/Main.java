package app;

import app.model.Facility;
import app.model.FacilityType;
import app.model.Station;
import app.repositories.Repositories;
import app.service.sprint1.WagonsUnloading;
import app.service.sprint3.StationDependencyService;
import app.utils.graph.StationGraphBuilder;
import app.utils.CsvReader;
import app.utils.graph.matrix.MatrixGraph;
import app.view.MainMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    private static final String BELGIAN_STATIONS_CSV = "src/main/java/app/sampleDataset/stations.csv";
    private static final String BELGIAN_LINES_CSV    = "src/main/java/app/sampleDataset/lines.csv";

    public static void main(String[] args) {
        AppInitializer appInitializer = new AppInitializer();

        System.out.println("Initializing dataset...");
        appInitializer.run();

        WagonsUnloading wagonsUnloading = new WagonsUnloading();
        wagonsUnloading.unloadAllWagons();

        System.out.println("Dataset initialized successfully.\n\n");
        System.out.println("\n\n\n");

        // =========================================
        // US USEI11 — Directed Line Upgrade Plan
        // =========================================
        Repositories repos = Repositories.getInstance();

        // 1) Carregar estações belgas de stations.csv como Station
        List<Station> belgianStations = loadBelgianStations(repos);

        // 2) Carregar dependências (arestas) de lines.csv
        List<int[]> dependencies = loadBelgianDependencies();

        // 3) Construir grafo dirigido Station -> Station
        StationGraphBuilder builder = new StationGraphBuilder();
        MatrixGraph<Station, Double> graph = builder.buildGraph(belgianStations, dependencies);

        // 4) Topological sort + deteção de ciclos
        StationDependencyService service = new StationDependencyService();
        StationDependencyService.Result result = service.planUpgrades(graph);

        System.out.println("Topological sort / cycle detection complexity: " + result.getComplexity());

        if (!result.hasCycles()) {
            System.out.println("Valid upgrade order of stations:");
            for (Station s : result.getTopoOrder()) {
                System.out.println(
                        s.getFacilityId() + " - " + s.getName()
                                + " (" + s.getLat() + ", " + s.getLon() + ")"
                );
            }
        } else {
            System.out.println("Graph has cycles, no valid order.");

            System.out.println("Stations in cycles:");
            result.getCycleStations().forEach(s ->
                    System.out.println(s.getFacilityId() + " - " + s.getName()));

            System.out.println("Edges in cycles:");
            result.getCycleEdges().forEach(e ->
                    System.out.println(
                            e.getVOrig().getFacilityId()
                                    + " -> "
                                    + e.getVDest().getFacilityId()
                    ));
        }

        // ============================
        // resto da aplicação
        // ============================
        MainMenu.display();
    }

    /**
     * Lê stations.csv e cria Station com FacilityId = Station id.
     */
    private static List<Station> loadBelgianStations(Repositories repos) {
        List<Map<String, String>> rows = CsvReader.readCsv(BELGIAN_STATIONS_CSV, ",");
        List<Station> result = new ArrayList<>();

        // criar / obter FacilityType "Station"
        FacilityType stationType = repos.getFacilityTypeRepository()
                .getFacilityTypeByName("Station");
        if (stationType == null) {
            stationType = new FacilityType(2, "Station");
            repos.getFacilityTypeRepository().addFacilityType(stationType);
        }

        for (Map<String, String> row : rows) {
            String idStr = row.get("Station id");
            String name = row.get("Station");
            Double lat = parseDouble(row.get("Lat"));
            Double lon = parseDouble(row.get("Lon"));

            if (idStr == null || name == null || lat == null || lon == null) {
                continue;
            }

            int id = Integer.parseInt(idStr.trim());

            Station s = new Station(
                    id,
                    name,
                    stationType,
                    lat,
                    lon,
                    false,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            // guarda também no FacilityRepository se quiseres reutilizar noutros casos
            repos.getFacilityRepository().addFacility(s);
            result.add(s);
        }

        return result;
    }

    /**
     * Lê lines.csv e devolve lista de pares (departure_stid, arrival_stid).
     */
    private static List<int[]> loadBelgianDependencies() {
        List<Map<String, String>> rows = CsvReader.readCsv(BELGIAN_LINES_CSV, ",");
        return rows.stream()
                .map(row -> {
                    String fromStr = row.get("departure_stid");
                    String toStr = row.get("arrival_stid");
                    if (fromStr == null || toStr == null) {
                        return null;
                    }
                    int from = Integer.parseInt(fromStr.trim());
                    int to = Integer.parseInt(toStr.trim());
                    return new int[]{from, to};
                })
                .filter(x -> x != null)
                .collect(Collectors.toList());
    }

    private static Double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
