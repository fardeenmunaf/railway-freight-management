package app.service.sprint3;

import app.model.Station;
import app.utils.graph.Edge;
import app.utils.graph.Graph;

import java.util.*;

/**
 * Serviço que, dado um grafo dirigido de estações, calcula
 * uma ordenação topológica ou, se houver ciclos, devolve
 * as estações e arestas que pertencem a ciclos.
 * Complexidade temporal: O(V+E).
 */
public class StationDependencyService {

    public static class Result {
        private final List<Station> topoOrder;
        private final Set<Station> cycleStations;
        private final Set<Edge<Station, ?>> cycleEdges;
        private final String complexity; // "O(V+E)"

        public Result(List<Station> topoOrder,
                      Set<Station> cycleStations,
                      Set<Edge<Station, ?>> cycleEdges,
                      String complexity) {
            this.topoOrder = topoOrder;
            this.cycleStations = cycleStations;
            this.cycleEdges = cycleEdges;
            this.complexity = complexity;
        }

        public List<Station> getTopoOrder() {
            return topoOrder;
        }

        public Set<Station> getCycleStations() {
            return cycleStations;
        }

        public Set<Edge<Station, ?>> getCycleEdges() {
            return cycleEdges;
        }

        public String getComplexity() {
            return complexity;
        }

        public boolean hasCycles() {
            return !cycleStations.isEmpty();
        }
    }

    /**
     * Topological sort + deteção de ciclos.
     * Complexidade: O(V+E).
     */
    public Result planUpgrades(Graph<Station, ?> g) {
        int n = g.numVertices();

        // grafo vazio → ordem vazia, sem erros
        if (n == 0) {
            return new Result(
                    Collections.emptyList(),
                    Collections.emptySet(),
                    Collections.emptySet(),
                    "O(V+E)"
            );
        }

        // lista de vértices e índice de cada um
        List<Station> vertices = new ArrayList<>(g.vertices());
        Map<Station, Integer> indexOf = new HashMap<>();
        for (int i = 0; i < vertices.size(); i++) {
            indexOf.put(vertices.get(i), i);
        }

        // in-degree inicial
        int[] inDegree = new int[n];
        for (int i = 0; i < n; i++) {
            Station v = vertices.get(i);
            inDegree[i] = g.inDegree(v);
        }

        // Kahn
        Queue<Station> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                q.add(vertices.get(i));
            }
        }

        List<Station> topoOrder = new ArrayList<>(n);

        while (!q.isEmpty()) {
            Station v = q.poll();
            topoOrder.add(v);

            for (Edge<Station, ?> e : g.outgoingEdges(v)) {
                Station w = e.getVDest();
                Integer idxW = indexOf.get(w);
                if (idxW == null) {
                    continue; // segurança
                }
                inDegree[idxW]--;
                if (inDegree[idxW] == 0) {
                    q.add(w);
                }
            }
        }

        // se apanhou todos → sem ciclos
        if (topoOrder.size() == n) {
            return new Result(
                    topoOrder,
                    Collections.emptySet(),
                    Collections.emptySet(),
                    "O(V+E)"
            );
        }

        // há ciclos → obter vértices e arestas em ciclos
        Set<Station> cycleStations = new HashSet<>();
        Set<Edge<Station, ?>> cycleEdges = new HashSet<>();

        // vértices que sobraram com in-degree > 0 são candidatos a ciclos
        for (int i = 0; i < n; i++) {
            if (inDegree[i] > 0) {
                Station v = vertices.get(i);
                cycleStations.add(v);
            }
        }

        // arestas entre vértices em ciclo
        for (Station v : cycleStations) {
            for (Edge<Station, ?> e : g.outgoingEdges(v)) {
                Station w = e.getVDest();
                if (cycleStations.contains(w)) {
                    cycleEdges.add(e);
                }
            }
        }

        return new Result(
                Collections.emptyList(), // sem ordem válida
                cycleStations,
                cycleEdges,
                "O(V+E)"
        );
    }
}
