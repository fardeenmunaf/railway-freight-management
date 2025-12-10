package app.service.sprint3;

import app.model.Station;
import app.utils.graph.Edge;
import app.utils.graph.Graph;
import app.utils.graph.algorithms.TopologicalSort;

import java.util.*;

/**
 * Serviço que usa TopologicalSort de app.utils.graph.algorithms
 * para calcular ordem de upgrades ou detetar ciclos.
 */
public class StationDependencyService {

    public static class Result {
        private final List<Station> topoOrder;
        private final Set<Station> cycleStations;
        private final Set<Edge<Station, ?>> cycleEdges;
        private final String complexity;

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
     * Delega para TopologicalSort.compute() de algorithms/
     */
    public Result planUpgrades(Graph<Station, ?> g) {
        return planUpgradesHelper(g);
    }

    /**
     * Método auxiliar que captura o tipo genérico E
     */
    private <E> Result planUpgradesHelper(Graph<Station, E> g) {
        TopologicalSort<Station, E> topoSort = new TopologicalSort<>();
        TopologicalSort.Result<Station, E> result = topoSort.compute(g);

        // Cast seguro porque Set<Edge<Station,E>> é covariante para Set<Edge<Station,?>>
        @SuppressWarnings("unchecked")
        Set<Edge<Station, ?>> cycleEdges = (Set<Edge<Station, ?>>) (Set<?>) result.getCycleEdges();

        return new Result(
                result.hasCycles() ? Collections.emptyList() : new ArrayList<>(result.getTopoOrder()),
                result.getCycleStations(),
                cycleEdges,
                "O(V+E)"
        );
    }
}
