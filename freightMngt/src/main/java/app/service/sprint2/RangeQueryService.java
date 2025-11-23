package app.service.sprint2;

import app.model.Station;
import app.utils.Balanced2DTreeBuilder.Balanced2DTree;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RangeQueryService {

    private final Balanced2DTree tree;

    public RangeQueryService(Balanced2DTree tree) {
        this.tree = tree;
    }

    /**
     * Range query sobre a árvore 2D com filtros opcionais.
     * Temporal Complexity Analysis:
     * - Best case: O(log N + k) when range is small and pruning is effective
     * - Average case: O(√N + k) for balanced partitioning
     * - Worst case: O(N + k) when range covers entire tree
     * where N = total stations, k = result count
     *
     * Spatial Complexity: O(k) for storing results
     *
     * @param countries Comma-separated countries (e.g., "PT,ES") or null for all
     */
    public RangeQueryResult rangeQuery(double latMin, double latMax,
                                       double lonMin, double lonMax,
                                       Boolean isCity, Boolean isMainStation,
                                       String countries) {

        if (tree == null)
            throw new IllegalStateException("2D Tree not initialized.");

        Set<String> countrySet = null;
        if (countries != null && !countries.trim().isEmpty() && !countries.equalsIgnoreCase("ALL")) {
            countrySet = Arrays.stream(countries.split(","))
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .collect(Collectors.toSet());
        }

        long startTime = System.nanoTime();

        List<Station> results = tree.rangeSearch(latMin, latMax, lonMin, lonMax,
                isCity, isMainStation, countrySet);

        long endTime = System.nanoTime();
        double elapsedMs = (endTime - startTime) / 1_000_000.0;

        return new RangeQueryResult(results, elapsedMs, tree.getSize());
    }

    /**
     * Result wrapper with performance metrics
     */
    public static class RangeQueryResult {
        public final List<Station> stations;
        public final double executionTimeMs;
        public final int treeSize;
        public final int resultCount;

        public RangeQueryResult(List<Station> stations, double executionTimeMs, int treeSize) {
            this.stations = stations;
            this.executionTimeMs = executionTimeMs;
            this.treeSize = treeSize;
            this.resultCount = stations.size();
        }

        public double getEfficiency() {
            return treeSize == 0 ? 0 : (resultCount * 100.0) / treeSize;
        }

        public String getComplexityEstimate() {
            if (resultCount == 0) return "O(log N) - pruned efficiently";
            double ratio = (double) resultCount / treeSize;
            if (ratio < 0.1) return "O(√N + k) - average case";
            if (ratio < 0.5) return "O(N^0.7 + k) - moderate range";
            return "O(N + k) - large range";
        }
    }
}
