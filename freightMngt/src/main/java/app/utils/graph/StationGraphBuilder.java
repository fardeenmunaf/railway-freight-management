package app.utils.graph;

import app.model.Station;
import app.utils.graph.matrix.MatrixGraph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Constrói um grafo dirigido de estações a partir de:
 *  - lista de Station (vértices)
 *  - lista de dependências por id (departure_stid -> arrival_stid)
 */
public class StationGraphBuilder {

    /**
     * @param stations        estações (vértices)
     * @param dependenciesById cada int[]{origId, destId}
     */
    public MatrixGraph<Station, Double> buildGraph(
            Collection<Station> stations,
            Collection<int[]> dependenciesById
    ) {
        MatrixGraph<Station, Double> g = new MatrixGraph<>(true); // dirigido

        Map<Integer, Station> byId = new HashMap<>();
        for (Station s : stations) {
            byId.put(s.getFacilityId(), s);
            g.addVertex(s);
        }

        for (int[] dep : dependenciesById) {
            if (dep.length < 2) {
                continue;
            }
            int origId = dep[0];
            int destId = dep[1];

            Station orig = byId.get(origId);
            Station dest = byId.get(destId);

            if (orig == null || dest == null) {
                throw new IllegalArgumentException(
                        "Invalid dependency: " + origId + " -> " + destId
                );
            }

            // peso pode ser null ou a dist/tempo; aqui usa-se null
            g.addEdge(orig, dest, null);
        }

        return g;
    }
}
