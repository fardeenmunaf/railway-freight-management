package app.service.sprint3;

import app.model.Station;
import app.utils.graph.Graph;
import app.utils.graph.map.MapGraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for loading stations and railway lines from CSV files.
 *
 * Main functionalities:
 * <ul>
 *   <li>Loading stations from stations.csv</li>
 *   <li>Loading connections from lines.csv</li>
 *   <li>Providing data for other services (USEI13 and USEI15)</li>
 * </ul>
 *
 * Implements Singleton pattern - the graph is loaded once and shared
 * across all services. Does not allow independent instances.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class StationLoaderService {

    /** Path to the stations CSV file */
    private static final String STATIONS_PATH = "src/main/java/app/sampleDataset/stations.csv";

    /** Path to the lines CSV file */
    private static final String LINES_PATH = "src/main/java/app/sampleDataset/lines.csv";

    /** Singleton instance */
    private static StationLoaderService instance;

    /** Undirected graph representing the railway network (shared across all services) */
    private static Graph<Station, Double> graph;

    /** Map associating station ID to Station object (shared across all services) */
    private static Map<Integer, Station> stationMap;

    /** Flag indicating if data has been loaded */
    private static boolean initialized = false;

    /**
     * Private constructor - enforces singleton pattern.
     * Use getInstance() to access the service.
     */
    private StationLoaderService() {
        // Singleton - no public instantiation
    }

    /**
     * Returns the singleton instance, creating it if necessary.
     * The graph is loaded lazily on first access.
     *
     * @return shared instance of StationLoaderService
     * @throws IOException if CSV files cannot be read
     */
    public static synchronized StationLoaderService getInstance() throws IOException {
        if (instance == null) {
            instance = new StationLoaderService();
            // Initialize static graph and stationMap
            graph = new MapGraph<>(false);
            stationMap = new HashMap<>();
            loadData();
        }
        return instance;
    }

    /**
     * Forces a reload of the singleton instance.
     * Useful when CSV data has changed or for testing.
     */
    public static synchronized void resetInstance() {
        instance = null;
        graph = null;
        stationMap = null;
        initialized = false;
    }

    /**
     * Loads stations and lines from CSV files (internal use only).
     *
     * @throws IOException if CSV files cannot be read
     */
    private static void loadData() throws IOException {
        if (initialized) {
            return; // Already loaded
        }
        loadStations(STATIONS_PATH);
        loadLines(LINES_PATH);
        initialized = true;
    }

    /**
     * Loads stations from the specified CSV file.
     *
     * Expected format: {stationId,name,latitude,longitude}
     * The first line (header) is ignored.
     *
     * @param filePath full path to the stations CSV file
     * @throws IOException if an error occurs reading the file
     */
    private static void loadStations(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                String[] parts = line.split(",");
                if (parts.length < 4) continue;
                try {
                    Integer id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    Station station = new Station();
                    station.setFacilityId(id);
                    station.setName(name);
                    stationMap.put(id, station);
                    graph.addVertex(station);
                } catch (NumberFormatException ignored) {}
            }
        }
    }

    /**
     * Loads connections (edges) from the specified CSV file.
     *
     * Expected format: {stationId1,stationId2,...,cost}
     * The cost is in the 5th column (index 4). First line ignored.
     *
     * @param filePath full path to the lines CSV file
     * @throws IOException if an error occurs reading the file
     */
    private static void loadLines(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length < 5) continue;
                try {
                    Integer s1 = Integer.parseInt(fields[0].trim());
                    Integer s2 = Integer.parseInt(fields[1].trim());
                    Double cost = Double.parseDouble(fields[4].trim());
                    Station station1 = stationMap.get(s1);
                    Station station2 = stationMap.get(s2);
                    if (station1 != null && station2 != null) {
                        graph.addEdge(station1, station2, cost);
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
    }

    /**
     * Checks if a station with the specified ID exists in the system.
     *
     * @param stationId ID of the station to check
     * @return true if the station exists, false otherwise
     */
    public boolean stationExists(Integer stationId) {
        return stationMap.containsKey(stationId);
    }

    /**
     * Returns formatted list of all stations sorted by ID.
     *
     * @return list in format "ID: 123 | Station Name"
     */
    public List<String> getFormattedStationList() {
        return stationMap.values().stream()
                .sorted(Comparator.comparing(Station::getFacilityId))
                .map(s -> String.format("ID: %d | %s", s.getFacilityId(), s.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Returns the total number of loaded stations.
     *
     * @return number of stations in the system
     */
    public int getTotalStations() {
        return stationMap.size();
    }

    /**
     * Returns the total number of connections (edges) in the graph.
     *
     * @return number of connections between stations
     */
    public int getTotalConnections() {
        return graph.numEdges();
    }

    /**
     * Returns the railway network graph.
     * Forces initialization if not yet loaded.
     *
     * @return graph with stations and connections
     * @throws IOException if CSV files cannot be read
     */
    public static Graph<Station, Double> getGraph() throws IOException {
        if (instance == null) {
            getInstance(); // Force initialization
        }
        return graph;
    }

    /**
     * Returns the station map indexed by ID.
     * Forces initialization if not yet loaded.
     *
     * @return map ID → Station
     * @throws IOException if CSV files cannot be read
     */
    public static Map<Integer, Station> getStationMap() throws IOException {
        if (instance == null) {
            getInstance(); // Force initialization
        }
        return stationMap;
    }
}
