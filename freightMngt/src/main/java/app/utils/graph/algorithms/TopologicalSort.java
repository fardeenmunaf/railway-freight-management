package app.utils.graph.algorithms;

import app.utils.graph.Edge;
import app.utils.graph.Graph;
import java.util.*;

/**
 * USEI11: Topological Sort + Cycle Detection (todas as componentes cíclicas)
 * Complexidade: O(V+E)
 */
public class TopologicalSort<V, E> {

    public static class Result<V, E> {
        private final LinkedList<V> topoOrder;
        private final Set<V> cycleStations;
        private final Set<Edge<V, E>> cycleEdges;

        public Result(LinkedList<V> topoOrder, Set<V> cycleStations, Set<Edge<V, E>> cycleEdges) {
            this.topoOrder = topoOrder;
            this.cycleStations = cycleStations;
            this.cycleEdges = cycleEdges;
        }

        public LinkedList<V> getTopoOrder() { return topoOrder; }
        public Set<V> getCycleStations() { return cycleStations; }
        public Set<Edge<V, E>> getCycleEdges() { return cycleEdges; }
        public boolean hasCycles() { return !cycleStations.isEmpty(); }
    }

    public Result<V, E> compute(Graph<V, E> g) {
        if (!g.isDirected())
            throw new IllegalArgumentException("Topological sort requires directed graph");

        LinkedList<V> topoOrder = new LinkedList<>();
        Set<V> visited = new HashSet<>();
        Set<V> recStack = new HashSet<>();

        // agora recolhemos TODOS os ciclos
        Set<V> cycleStations = new HashSet<>();
        Set<Edge<V, E>> cycleEdges = new HashSet<>();

        for (V v : g.vertices()) {
            if (!visited.contains(v)) {
                dfs(g, v, visited, recStack, topoOrder, cycleStations, cycleEdges);
            }
        }

        // se houver ciclos, topoOrder deve ser vazio
        if (!cycleStations.isEmpty()) {
            collectCycleEdges(g, cycleStations, cycleEdges);
            return new Result<>(new LinkedList<>(), cycleStations, cycleEdges);
        }

        return new Result<>(topoOrder, Collections.emptySet(), Collections.emptySet());
    }

    private void dfs(Graph<V, E> g, V v, Set<V> visited, Set<V> recStack,
                     LinkedList<V> topoOrder, Set<V> cycleStations, Set<Edge<V, E>> cycleEdges) {

        visited.add(v);
        recStack.add(v);

        for (V adj : g.adjVertices(v)) {

            // caso A -> B e B já está no recStack → ciclo encontrado
            if (recStack.contains(adj)) {
                cycleStations.add(v);
                cycleStations.add(adj);
            }

            if (!visited.contains(adj)) {
                dfs(g, adj, visited, recStack, topoOrder, cycleStations, cycleEdges);
            }
        }

        recStack.remove(v);
        topoOrder.addFirst(v);
    }

    /** Recolhe todas as arestas cujo destino e origem fazem parte de ciclos */
    private void collectCycleEdges(Graph<V, E> g, Set<V> cycleStations, Set<Edge<V, E>> cycleEdges) {
        for (V v : cycleStations) {
            for (Edge<V, E> e : g.outgoingEdges(v)) {
                if (cycleStations.contains(e.getVDest())) {
                    cycleEdges.add(e);
                }
            }
        }
    }
}
