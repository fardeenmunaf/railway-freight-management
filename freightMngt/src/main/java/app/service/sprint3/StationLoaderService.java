package app.service.sprint3;

import app.model.Station;
import app.utils.graph.Algorithms;
import app.utils.graph.Edge;
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
 * Serviço para carregamento de estações e linhas ferroviárias a partir de ficheiros CSV
 * e cálculo de caminhos mais curtos entre estações usando algoritmos adequados.
 *
 * Funcionalidades principais:
 * 
 *   <li>Carregamento de estações de { stations.csv}</li>
 *   <li>Carregamento de ligações de { lines.csv}</li>
 *   <li>Cálculo automático de caminho mais curto (Dijkstra ou Bellman-Ford)</li>
 *   <li>Detecção de ciclos negativos no grafo</li>
 * </ul>
 *
 * @author Fardeen
 * @version 1.0
 */
public class StationLoaderService {

    /** Caminho para o ficheiro CSV das estações */
    private static final String STATIONS_PATH = "src/main/java/app/sampleDataset/stations.csv";

    /** Caminho para o ficheiro CSV das linhas */
    private static final String LINES_PATH = "src/main/java/app/sampleDataset/lines.csv";

    /** Grafo não-direcionado representa a rede ferroviária  */
    private static Graph<Station, Double> graph;

    /** Mapa que associa ID da estação ao objeto Station */
    private Map<Integer, Station> stationMap;

    /**
     * Construtor que inicializa o grafo não-direcionado e o mapa de estações. (ida e volta)
     */
    public StationLoaderService() {
        this.graph = new MapGraph<>(false);
        this.stationMap = new HashMap<>();
    }

    /**
     * Inicializa o serviço carregando estações e linhas dos ficheiros padrão.
     *
     * @throws IOException caso ocorra erro na leitura dos ficheiros CSV
     */
    public void initialize() throws IOException {
        loadStations(STATIONS_PATH);
        loadLines(LINES_PATH);
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
    private void loadStations(String filePath) throws IOException {
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
    private void loadLines(String filePath) throws IOException {
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
     * Calcula o caminho mais curto entre duas estações escolhendo automaticamente
     * o algoritmo adequado (Dijkstra ou Bellman-Ford).
     *
     * Estratégia:
     * 
     *   <li>Tenta Dijkstra primeiro (mais eficiente)</li>
     *   <li>Se falhar ou caminho tiver pesos negativos → Bellman-Ford</li>
     *   <li>Detecta ciclos negativos e reporta estações/arestas envolvidas</li>
     * </ul>
     *
     * @param departureId ID da estação de partida
     * @param arrivalId ID da estação de chegada
     * @return resultado com caminho, custo, algoritmo usado e detecção de ciclos,
     *         ou { null} se estações não existirem
     */
    public PathResultWithAlgorithm calculateBestPath(Integer departureId, Integer arrivalId) {
        Station start = stationMap.get(departureId);
        Station end = stationMap.get(arrivalId);
        if (start == null || end == null) return null;

        // 1. Encontra caminho com Dijkstra PRIMEIRO
        LinkedList<Station> path = new LinkedList<>();
        Double cost = Algorithms.shortestPath(graph, start, end, Double::compare, Double::sum, 0.0, path);

        if (cost == null) {
            // SEM CAMINHO - tenta Bellman-Ford para ciclo negativo
            LinkedList<Station> pathBF = new LinkedList<>();
            Double costBF = Algorithms.bellmanFord(graph, start, end, Double::compare, Double::sum, 0.0, pathBF);
            if (costBF == null) {
                Set<Station> cycleStations = findNegativeCycleStations(start);
                Set<Edge<Station, Double>> cycleEdges = findNegativeCycleEdges(cycleStations);
                PathResult result = new PathResult(new LinkedList<>(), null, true, cycleStations, cycleEdges);
                return new PathResultWithAlgorithm(result, "Bellman-Ford", true);
            }
            // Bellman-Ford encontrou caminho
            PathResult result = new PathResult(pathBF, costBF, false, Collections.emptySet(), Collections.emptySet());
            return new PathResultWithAlgorithm(result, "Bellman-Ford", false);
        }

        // 2. Dijkstra encontrou caminho - VERIFICA se tem negativos
        boolean hasNegativeInPath = hasNegativeEdgesInPath(path);

        PathResult result = new PathResult(path, cost, false, Collections.emptySet(), Collections.emptySet());
        String algorithm = hasNegativeInPath ? "Bellman-Ford" : "Dijkstra";

        return new PathResultWithAlgorithm(result, algorithm, false);
    }

    /**
     * Verifica se o caminho específico contém arestas com custo negativo.
     *
     * @param path lista ordenada de estações do caminho
     * @return { true} se alguma aresta consecutiva tem custo negativo
     */
    private boolean hasNegativeEdgesInPath(List<Station> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            Station from = path.get(i);
            Station to = path.get(i + 1);
            for (Edge<Station, Double> edge : graph.outgoingEdges(from)) {
                if (edge.getVDest().equals(to) && edge.getWeight() < 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Encontra estações alcançáveis a partir de uma estação inicial usando DFS.
     *
     * @param current estação inicial
     * @param reachable conjunto de estações alcançáveis (output)
     */
    private void dfsReachable(Station current, Set<Station> reachable) {
        if (reachable.contains(current)) return;
        reachable.add(current);
        for (Edge<Station, Double> e : graph.outgoingEdges(current)) {
            dfsReachable(e.getVDest(), reachable);
        }
    }

    /**
     * Identifica estações envolvidas em ciclos negativos alcançáveis da estação inicial.
     *
     * @param start estação inicial para determinar componente conectado
     * @return conjunto de estações envolvidas em arestas negativas
     */
    private Set<Station> findNegativeCycleStations(Station start) {
        Set<Station> reachable = new HashSet<>();
        dfsReachable(start, reachable);

        Set<Station> cycleStations = new HashSet<>();
        for (Station v : reachable) {
            for (Edge<Station, Double> e : graph.outgoingEdges(v)) {
                if (e.getWeight() < 0 && reachable.contains(e.getVDest())) {
                    cycleStations.add(v);
                    cycleStations.add(e.getVDest());
                }
            }
        }
        return cycleStations;
    }

    /**
     * Encontra arestas negativas entre as estações especificadas.
     *
     * @param cycleStations estações candidatas ao ciclo negativo
     * @return conjunto de arestas negativas que formam o ciclo
     */
    private Set<Edge<Station, Double>> findNegativeCycleEdges(Set<Station> cycleStations) {
        Set<Edge<Station, Double>> edges = new HashSet<>();
        for (Station v : cycleStations) {
            for (Edge<Station, Double> e : graph.outgoingEdges(v)) {
                if (cycleStations.contains(e.getVDest()) && e.getWeight() < 0) {
                    edges.add(e);
                }
            }
        }
        return edges;
    }

    /**
     * Retorna o grafo da rede ferroviária.
     *
     * @return grafo com estações e conexões
     */
    public Graph<Station, Double> getGraph() {
        return graph;
    }

    /**
     * Retorna o mapa de estações indexado por ID.
     *
     * @return mapa ID → Station
     */
    public Map<Integer, Station> getStationMap() {
        return stationMap;
    }

    /**
     * Resultado do cálculo de caminho mais curto com informação do algoritmo utilizado.
     */
    public static class PathResultWithAlgorithm {
        /** Resultado detalhado do caminho */
        private final PathResult pathResult;
        /** Nome do algoritmo utilizado */
        private final String algorithmUsed;
        /** Indica se foi detetado ciclo negativo */
        private final boolean hasNegativeCycle;

        /**
         * Construtor do resultado com algoritmo.
         *
         * @param pathResult resultado do caminho
         * @param algorithmUsed nome do algoritmo ("Dijkstra" ou "Bellman-Ford")
         * @param hasNegativeCycle true se ciclo negativo foi detetado
         */
        public PathResultWithAlgorithm(PathResult pathResult, String algorithmUsed, boolean hasNegativeCycle) {
            this.pathResult = pathResult;
            this.algorithmUsed = algorithmUsed;
            this.hasNegativeCycle = hasNegativeCycle;
        }

        /** @return resultado detalhado do caminho */
        public PathResult getPathResult() { return pathResult; }
        /** @return nome do algoritmo utilizado */
        public String getAlgorithmUsed() { return algorithmUsed; }
        /** @return true se ciclo negativo foi detetado */
        public boolean hasNegativeCycle() { return hasNegativeCycle; }
    }

    /**
     * Resultado detalhado do cálculo de caminho mais curto.
     */
    public static class PathResult {
        /** Sequência de estações do caminho */
        private final LinkedList<Station> path;
        /** Custo total do caminho */
        private final Double totalCost;
        /** Indica existência de ciclo negativo */
        private final boolean hasNegativeCycle;
        /** Estações envolvidas no ciclo negativo */
        private final Set<Station> cycleStations;
        /** Arestas do ciclo negativo */
        private final Set<Edge<Station, Double>> cycleEdges;

        /**
         * Construtor do resultado do caminho.
         *
         * @param path sequência de estações
         * @param totalCost custo total
         * @param hasNegativeCycle true se ciclo negativo
         * @param cycleStations estações do ciclo
         * @param cycleEdges arestas do ciclo
         */
        public PathResult(LinkedList<Station> path, Double totalCost, boolean hasNegativeCycle,
                          Set<Station> cycleStations, Set<Edge<Station, Double>> cycleEdges) {
            this.path = path;
            this.totalCost = totalCost;
            this.hasNegativeCycle = hasNegativeCycle;
            this.cycleStations = cycleStations;
            this.cycleEdges = cycleEdges;
        }

        /**
         * Formata o resultado para apresentação.
         *
         * Caso normal: mostra caminho com custo de cada aresta
         * Ciclo negativo: lista estações e arestas envolvidas
         *
         * @return string formatada com caminho e custos
         */
        public String formatPath() {
            if (hasNegativeCycle) {
                StringBuilder sb = new StringBuilder("Ciclo negativo detetado envolvendo:\n");
                sb.append("Estações: ");
                sb.append(cycleStations.stream().map(Station::getName).collect(Collectors.joining(", ")));
                sb.append("\n\nArestas: ");
                sb.append(cycleEdges.stream()
                        .map(e -> String.format("%s(%d)->%s(%d):%.2f",
                                e.getVOrig().getName(), e.getVOrig().getFacilityId(),
                                e.getVDest().getName(), e.getVDest().getFacilityId(), e.getWeight()))
                        .collect(Collectors.joining(", ")));
                return sb.toString();
            }

            StringBuilder sb = new StringBuilder();

            // Caminho com custos individuais
            double total = 0.0;
            for (int i = 0; i < path.size() - 1; i++) {
                Station from = path.get(i);
                Station to = path.get(i + 1);

                // Encontra aresta exata
                Edge<Station, Double> edge = null;
                for (Edge<Station, Double> e : graph.outgoingEdges(from)) {
                    if (e.getVDest().equals(to)) {
                        edge = e;
                        break;
                    }
                }

                double cost = edge != null ? edge.getWeight() : 0.0;
                total += cost;

                sb.append(String.format("%s(ID:%d) -> %s(ID:%d) [%.2f]",
                        from.getName(), from.getFacilityId(),
                        to.getName(), to.getFacilityId(), cost));

                if (i < path.size() - 2) {
                    sb.append(" -> ");
                }
            }

            sb.append("\nCusto total: ").append(String.format("%.2f", total));
            return sb.toString();
        }
    }
}
