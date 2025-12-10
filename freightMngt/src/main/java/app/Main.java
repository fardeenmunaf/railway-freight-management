package app;

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

        System.out.println("\n=== Initializing Dataset ===");
        new AppInitializer().run();
        new WagonsUnloading().unloadAllWagons();
        System.out.println("Dataset loaded.\n");

        // --------------------------------------------------------------------
        // USEI11 - Belgian Station Upgrade Scheduling (Topo Sort + Cycle Check)
        // --------------------------------------------------------------------
        Repositories repos = Repositories.getInstance();

        List<Station> belgianStations = loadBelgianStations(repos);
        List<int[]> dependencies = loadBelgianDependencies();

        System.out.println("Loaded Stations: " + belgianStations.size());
        System.out.println("Loaded Dependencies: " + dependencies.size() + "\n");

        StationGraphBuilder builder = new StationGraphBuilder();
        MatrixGraph<Station, Double> graph = builder.buildGraph(belgianStations, dependencies);

        StationDependencyService service = new StationDependencyService();
        StationDependencyService.Result result = service.planUpgrades(graph);

        System.out.println("Algorithm Complexity: " + result.getComplexity());

        if (!result.hasCycles()) {
            //-----------------------------------------------------------
            // No cycles → show valid installation sequence
            //-----------------------------------------------------------
            System.out.println("\nValid upgrade order of stations:\n");
            int i = 1;
            for (Station s : result.getTopoOrder()) {
                System.out.printf("%3d. %4d - %s\n", i++, s.getFacilityId(), s.getName());
            }
        } else {
            //-----------------------------------------------------------
            // Cycles Detected → show all cycle components
            //-----------------------------------------------------------
            System.out.println("\nGRAPH HAS CYCLES — upgrade schedule impossible.\n");

            System.out.println("Stations involved in cycles (" + result.getCycleStations().size() + "):");
            result.getCycleStations().stream()
                    .sorted((a,b)->a.getFacilityId()-b.getFacilityId())
                    .forEach(s -> System.out.println(" • " + s.getFacilityId() + " - " + s.getName()));

            System.out.println("\nCycle Edges (" + result.getCycleEdges().size() + "):");
            result.getCycleEdges().forEach(e ->
                    System.out.println(" → " + e.getVOrig().getFacilityId() + " -> " + e.getVDest().getFacilityId()));
        }

        System.out.println("\n=== Application Ready ===\n");
        MainMenu.display();
    }

    /* Load Belgian Stations */
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
                        false,null,null,null,null,null,null
                );
                repos.getFacilityRepository().addFacility(s);
                result.add(s);
            } catch (Exception ignored) {}
        }
        return result;
    }

    /* Load edges from lines.csv */
    private static List<int[]> loadBelgianDependencies() {
        return CsvReader.readCsv(BELGIAN_LINES_CSV, ",").stream()
                .map(row -> {
                    try {
                        return new int[]{
                                Integer.parseInt(row.get("departure_stid").trim()),
                                Integer.parseInt(row.get("arrival_stid").trim())
                        };
                    } catch (Exception e) { return null; }
                })
                .filter(x -> x != null)
                .collect(Collectors.toList());
    }

    private static Double parseDouble(String value) {
        try { return value == null ? null : Double.parseDouble(value.trim()); }
        catch (Exception e) { return null; }
    }
}
