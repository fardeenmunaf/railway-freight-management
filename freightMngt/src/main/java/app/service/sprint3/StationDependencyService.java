package app.service.sprint3;

import app.model.Station;
import app.utils.graph.Algorithms;
import app.utils.graph.Edge;
import app.utils.graph.Graph;

import java.util.*;

/**
 * Serviço que usa Algorithms.topologicalSortWithCycles
 * para calcular ordem de upgrades ou detetar ciclos.
 */
public class StationDependencyService {

    /**
     * Resultado do planeamento de upgrades, parametrizado pelo tipo de peso E.
     */
    public static class Result<E> {
        private final List<Station> topoOrder;
        private final Set<Station> cycleStations;
        private final Set<Edge<Station, E>> cycleEdges;
        private final String complexity;

        public Result(List<Station> topoOrder,
                      Set<Station> cycleStations,
                      Set<Edge<Station, E>> cycleEdges,
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

        public Set<Edge<Station, E>> getCycleEdges() {
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
     * Delega para Algorithms.topologicalSortWithCycles()
     */
    public <E> Result<E> planUpgrades(Graph<Station, E> g) {
        return planUpgradesHelper(g);
    }

    /**
     * Método auxiliar que captura o tipo genérico E
     */
    private <E> Result<E> planUpgradesHelper(Graph<Station, E> g) {
        Algorithms.TopologicalSortResult<Station, E> result =
                Algorithms.topologicalSortWithCycles(g);

        return new Result<>(
                result.hasCycles()
                        ? Collections.emptyList()
                        : new ArrayList<>(result.getTopoOrder()),
                result.getCycleStations(),
                result.getCycleEdges(),
                "O(V+E)"
        );
    }
}
