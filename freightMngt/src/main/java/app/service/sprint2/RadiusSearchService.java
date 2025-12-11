package app.service.sprint2;

import app.model.Station;
import app.utils.AVLTree;
import app.utils.Balanced2DTreeBuilder;
import app.utils.HaversineDistance;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for radius search operations on stations.
 * Implements USEI10 - Radius search and density summary.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class RadiusSearchService {

    private final Balanced2DTreeBuilder.Balanced2DTree tree;

    /**
     * Inject the already built 2D tree; StationService is responsible for building it.
     *
     * @param tree The 2D tree containing station data
     */
    public RadiusSearchService(Balanced2DTreeBuilder.Balanced2DTree tree) {
        this.tree = tree;
    }

    /**
     * Simple DTO carrying both the radius search results and a temporal analysis
     * description for reporting purposes.
     */
    public static class RadiusSearchResult {
        public final AVLTree<Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance,
                Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance> resultsTree;
        public final TemporalAnalysis temporalAnalysis;

        public RadiusSearchResult(AVLTree<Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance,
                Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance> resultsTree,
                                  TemporalAnalysis temporalAnalysis) {
            this.resultsTree = resultsTree;
            this.temporalAnalysis = temporalAnalysis;
        }
    }

    /**
     * Temporal analysis for USEI10 (Radius Search + Density Summary).
     *
     * Uses the documented complexity:
     *   Radius Search: O(√N + k · log k)
     *   Density Summary: O(k)
     *   Total (USEI10): O(√N + k · log k)
     */
    public static class TemporalAnalysis {
        public final String radiusSearchComplexity;
        public final String densitySummaryComplexity;
        public final String totalUseCaseComplexity;
        public final int totalNodes;
        public final int resultCount;

        public TemporalAnalysis(int totalNodes, int resultCount) {
            this.totalNodes = totalNodes;
            this.resultCount = resultCount;

            // Radius search: traversal on 2D-tree O(√N) + AVL inserts O(k · log k)
            this.radiusSearchComplexity =
                    "Radius Search: O(√N + k · log k) where N = " + totalNodes + " and k = " + resultCount;

            // Density summary: single pass over k results
            this.densitySummaryComplexity =
                    "Density Summary: O(k) where k = " + resultCount;

            // Total for USEI10 is dominated by the radius search phase
            this.totalUseCaseComplexity =
                    "Total (USEI10): O(√N + k · log k) where N = " + totalNodes + " and k = " + resultCount;
        }

        @Override
        public String toString() {
            return String.format(
                "Temporal Analysis (USEI10):%n" +
                "  %s%n" +
                "  %s%n" +
                "  %s",
                radiusSearchComplexity,
                densitySummaryComplexity,
                totalUseCaseComplexity
            );
        }
    }

    /**
     * Performs radius search and returns both the AVL tree of results and a
     * temporal analysis description.
     */
    public RadiusSearchResult radiusSearchWithAnalysis(double targetLat, double targetLon, double radiusKm) {
        AVLTree<Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance,
                Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance> results =
                radiusSearchAsTree(targetLat, targetLon, radiusKm);

        int totalNodes;
        if (tree != null) {
            totalNodes = tree.getSize();
        } else {
            totalNodes = 0;
        }
        int resultCount = results.inOrderTraversal().size();
        TemporalAnalysis analysis = new TemporalAnalysis(totalNodes, resultCount);

        return new RadiusSearchResult(results, analysis);
    }

    public AVLTree<Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance, Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance> radiusSearchAsTree(double targetLat, double targetLon, double radiusKm) {

        AVLTree<Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance, Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance> results = new AVLTree<>();

        radiusSearchRecursive(tree.getRoot(), targetLat, targetLon, radiusKm, results);

        return results;
    }

    /**
     * Gets all stations from a node's bucket that are within the specified radius.
     * Inserts matching stations directly into the AVL tree.
     *
     * @param node The node to check
     * @param targetLat Target latitude
     * @param targetLon Target longitude
     * @param radiusKm Radius in kilometers
     * @param results AVL tree to insert matching stations into
     */
    private void getNodeStations(Balanced2DTreeBuilder.Node2D node, double targetLat, double targetLon, double radiusKm, AVLTree<Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance, Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance> results) {
        for (Station station : node.bucket.stations) {
            double distance = HaversineDistance.calculate(targetLat, targetLon, station.getLat(), station.getLon());

            if (distance <= radiusKm) {
                Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance swd = new Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance(station, distance);
                results.insert(swd, swd); // Insert into AVL tree (key = value)
            }
        }
    }

    /**
     * Recursive helper for radius search with branch pruning.
     * Inserts matching stations directly into the AVL tree for automatic sorting.
     */
    private void radiusSearchRecursive(Balanced2DTreeBuilder.Node2D node, double targetLat, double targetLon, double radiusKm, AVLTree<Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance, Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance> results) {

        if (node == null) return;

        // Check all stations in the current bucket
        getNodeStations(node, targetLat, targetLon, radiusKm, results);

        // Calculate distance to splitting plane for pruning
        double targetValue;
        if (node.isLatitude) {
            targetValue = targetLat;
        } else {
            targetValue = targetLon;
        }

        // At the equator, 1 degree ≈ 111 km (estimate for pruning)
        // Search the left subtree if it could contain points within radius
        if (node.left != null && (targetValue - node.key) * 111.0 <= radiusKm) {
            radiusSearchRecursive(node.left, targetLat, targetLon, radiusKm, results);
        }

        // Search the right subtree if it could contain points within radius
        if (node.right != null && (node.key - targetValue) * 111.0 <= radiusKm) {
            radiusSearchRecursive(node.right, targetLat, targetLon, radiusKm, results);
        }
    }

    /**
     * Calculates density summary by country and isCity.
     * Works directly with AVL tree without converting to list.
     *
     * @param resultsTree AVL tree containing stations with distances
     * @return DensitySummary containing distribution statistics
     */
    public DensitySummary calculateDensitySummary(AVLTree<Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance, Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance> resultsTree) {
        Map<String, Integer> byCountry = new HashMap<>();
        int cityCount = 0;
        int nonCityCount = 0;
        int totalCount = 0;

        // Traverse tree and count using traditional for loop
        for (Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance result : resultsTree.inOrderTraversal()) {
            Station station = result.station;
            totalCount++;

            // Count by country
            String country = station.getCountry();
            if (country != null) {
                byCountry.put(country, byCountry.getOrDefault(country, 0) + 1);
            }

            // Count by isCity
            Boolean isCity = station.getIsCity();
            if (isCity != null && isCity) {
                cityCount++;
            } else {
                nonCityCount++;
            }
        }

        return new DensitySummary(byCountry, cityCount, nonCityCount, totalCount);
    }

    /**
     * Density summary containing distribution statistics by country and isCity.
     */
    public static class DensitySummary {
        public final Map<String, Integer> byCountry;
        public final int cityCount;
        public final int nonCityCount;
        public final int totalCount;

        public DensitySummary(Map<String, Integer> byCountry, int cityCount, int nonCityCount, int totalCount) {
            this.byCountry = byCountry;
            this.cityCount = cityCount;
            this.nonCityCount = nonCityCount;
            this.totalCount = totalCount;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Density Summary:\n");
            sb.append(String.format("  Total Stations: %d\n", totalCount));
            sb.append(String.format("  City Stations: %d\n", cityCount));
            sb.append(String.format("  Non-City Stations: %d\n", nonCityCount));
            sb.append("  Distribution by Country:\n");
            byCountry.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).forEach(entry -> sb.append(String.format("    %s: %d stations\n", entry.getKey(), entry.getValue())));
            return sb.toString();
        }
    }
}
