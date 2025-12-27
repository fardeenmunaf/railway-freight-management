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
 * Serviço para carregamento de estações e linhas ferroviárias a partir de ficheiros CSV.
 *
 * Funcionalidades principais:
 * <ul>
 *   <li>Carregamento de estações de stations.csv</li>
 *   <li>Carregamento de ligações de lines.csv</li>
 *   <li>Fornecimento de dados para outros serviços (USEI13 e USEI15)</li>
 * </ul>
 *
 * Implementa padrão Singleton - o grafo é carregado uma vez e compartilhado
 * entre todos os serviços. Não permite instâncias independentes.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class StationLoaderService {

    /** Caminho para o ficheiro CSV das estações */
    private static final String STATIONS_PATH = "src/main/java/app/sampleDataset/stations.csv";

    /** Caminho para o ficheiro CSV das linhas */
    private static final String LINES_PATH = "src/main/java/app/sampleDataset/lines.csv";

    /** Singleton instance - lazy initialized */
    private static StationLoaderService instance;

    /** Grafo não-direcionado representa a rede ferroviária (shared across all services) */
    private static Graph<Station, Double> graph;

    /** Mapa que associa ID da estação ao objeto Station (shared across all services) */
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
     * Carrega estações do ficheiro CSV especificado.
     *
     * Formato esperado: {idEstacao,nome,latitude,longitude}
     * A primeira linha (cabeçalho) é ignorada.
     *
     * @param filePath caminho completo para o ficheiro CSV de estações
     * @throws IOException caso ocorra erro na leitura do ficheiro
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
     * Carrega ligações (arestas) do ficheiro CSV especificado.
     *
     * Formato esperado: { idEstacao1,idEstacao2,...,custo}
     * O custo está na 5ª coluna (índice 4). Primeira linha ignorada.
     *
     * @param filePath caminho completo para o ficheiro CSV de linhas
     * @throws IOException caso ocorra erro na leitura do ficheiro
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
     * Verifica se uma estação com o ID especificado existe no sistema.
     *
     * @param stationId ID da estação a verificar
     * @return { true} se a estação existe, { false} caso contrário
     */
    public boolean stationExists(Integer stationId) {
        return stationMap.containsKey(stationId);
    }

    /**
     * Retorna lista formatada de todas as estações ordenadas por ID.
     *
     * @return lista no formato "ID: 123 | Nome da Estação"
     */
    public List<String> getFormattedStationList() {
        return stationMap.values().stream()
                .sorted(Comparator.comparing(Station::getFacilityId))
                .map(s -> String.format("ID: %d | %s", s.getFacilityId(), s.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Retorna o número total de estações carregadas.
     *
     * @return quantidade de estações no sistema
     */
    public int getTotalStations() {
        return stationMap.size();
    }

    /**
     * Retorna o número total de conexões (arestas) no grafo.
     *
     * @return quantidade de ligações entre estações
     */
    public int getTotalConnections() {
        return graph.numEdges();
    }

    /**
     * Retorna o grafo da rede ferroviária.
     * Forces initialization if not yet loaded.
     *
     * @return grafo com estações e conexões
     * @throws IOException if CSV files cannot be read
     */
    public static Graph<Station, Double> getGraph() throws IOException {
        if (instance == null) {
            getInstance(); // Force initialization
        }
        return graph;
    }

    /**
     * Retorna o mapa de estações indexado por ID.
     * Forces initialization if not yet loaded.
     *
     * @return mapa ID → Station
     * @throws IOException if CSV files cannot be read
     */
    public static Map<Integer, Station> getStationMap() throws IOException {
        if (instance == null) {
            getInstance(); // Force initialization
        }
        return stationMap;
    }
}
