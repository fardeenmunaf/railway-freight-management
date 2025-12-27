package app.service.sprint3;

import app.model.Station;
import app.utils.graph.Algorithms;
import app.utils.graph.Edge;
import app.utils.graph.Graph;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Serviço para cálculo de caminhos mais curtos entre estações (USEI15).
 *
 * Implementa algoritmos de pathfinding com detecção automática:
 * - Dijkstra para grafos sem pesos negativos (mais eficiente)
 * - Bellman-Ford para grafos com pesos negativos (mais robusto)
 * - Detecção de ciclos negativos
 *
 * @author Fardeen
 * @version 1.0
 */
public class ShortestPathService {

    private final Graph<Station, Double> graph;
    private final Map<Integer, Station> stationMap;

    /**
     * Construtor que inicializa o serviço com dados do StationLoaderService.
     *
     * @throws IOException se houver erro ao carregar dados
     */
    public ShortestPathService() throws IOException {
        this.graph = StationLoaderService.getGraph();
        this.stationMap = StationLoaderService.getStationMap();
    }

    /**
     * Calcula o caminho mais curto entre duas estações escolhendo automaticamente
     * o algoritmo adequado (Dijkstra ou Bellman-Ford).
     *
     * Estratégia:
     * <ul>
     *   <li>Tenta Dijkstra primeiro (mais eficiente)</li>
     *   <li>Se falhar ou caminho tiver pesos negativos → Bellman-Ford</li>
     *   <li>Detecta ciclos negativos e reporta estações/arestas envolvidas</li>
     * </ul>
     *
     * @param departureId ID da estação de partida
     * @param arrivalId ID da estação de chegada
     * @return resultado com caminho, custo, algoritmo usado e detecção de ciclos,
     *         ou null se estações não existirem
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
     * @return true se alguma aresta consecutiva tem custo negativo
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
     * Resultado do cálculo de caminho mais curto com informação do algoritmo utilizado.
     */
    public static class PathResultWithAlgorithm {
        private final PathResult pathResult;
        private final String algorithmUsed;
        private final boolean hasNegativeCycle;

        public PathResultWithAlgorithm(PathResult pathResult, String algorithmUsed, boolean hasNegativeCycle) {
            this.pathResult = pathResult;
            this.algorithmUsed = algorithmUsed;
            this.hasNegativeCycle = hasNegativeCycle;
        }

        public PathResult getPathResult() { return pathResult; }
        public String getAlgorithmUsed() { return algorithmUsed; }
        public boolean hasNegativeCycle() { return hasNegativeCycle; }
    }

    /**
     * Resultado detalhado do cálculo de caminho mais curto.
     */
    public class PathResult {
        final LinkedList<Station> path;
        final Double totalCost;
        private final boolean hasNegativeCycle;
        private final Set<Station> cycleStations;
        private final Set<Edge<Station, Double>> cycleEdges;

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

