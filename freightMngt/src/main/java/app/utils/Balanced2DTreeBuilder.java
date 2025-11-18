package app.utils;

import app.model.Station; // Importar a classe de domínio Station
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Builds a balanced 2D-tree from station data using latitude and longitude.
 * Reuses AVLTree for efficient bulk-build strategy.
 *
 * @author G113 2DK
 * @version 1.3 (Cleaned up for compatibility with StationService)
 */
public class Balanced2DTreeBuilder {

    /**
     * Node in the 2D-tree alternating between latitude and longitude dimensions.
     */
    public static class Node2D {
        public final double key;
        public final boolean isLatitude;
        public final StationBucket bucket;
        public Node2D left;
        public Node2D right;
        public int height;

        public Node2D(double key, boolean isLatitude, StationBucket bucket) {
            this.key = key;
            this.isLatitude = isLatitude;
            this.bucket = bucket;
            this.height = 1;
        }

        public void updateHeight() {
            int leftHeight = (left == null) ? 0 : left.height;
            int rightHeight = (right == null) ? 0 : right.height;
            this.height = 1 + Math.max(leftHeight, rightHeight);
        }
    }

    /**
     * Stores stations with identical coordinates, sorted alphabetically by name.
     */
    public static class StationBucket {
        public final List<Station> stations;

        public StationBucket() {
            stations = new ArrayList<>();
        }

        public void addStation(Station s) {
            // Usar o getName() da classe de domínio
            int pos = Collections.binarySearch(stations, s, Comparator.comparing(Station::getName));
            if (pos < 0) pos = -pos - 1;
            stations.add(pos, s);
        }

        public int size() {
            return stations.size();
        }
    }

    /**
     * Temporal complexity analysis for tree operations.
     */
    public static class TemporalComplexity {
        public final String buildComplexity;
        public final String searchComplexity;
        public final String rangeSearchComplexity;
        public final int totalNodes;
        public final int expectedHeight;

        public TemporalComplexity(int size, int height) {
            this.totalNodes = size;
            this.expectedHeight = (int) Math.ceil(Math.log(size) / Math.log(2));
            this.buildComplexity = "O(N log N) where N = " + size;
            this.searchComplexity = "O(log N) ≈ O(" + expectedHeight + ")";
            this.rangeSearchComplexity = "O(√N + k) where k = results";
        }

        @Override
        public String toString() {
            return String.format(
                    "Temporal Complexity Analysis:%n" +
                            "  Build: %s%n" +
                            "  Point Search: %s%n" +
                            "  Range Search: %s%n" +
                            "  Total Nodes: %d%n" +
                            "  Expected Height: %d",
                    buildComplexity, searchComplexity, rangeSearchComplexity,
                    totalNodes, expectedHeight
            );
        }
    }

    /**
     * Balanced 2D-tree structure alternating latitude and longitude dimensions.
     */
    public static class Balanced2DTree {
        private Node2D root;
        private int size = 0;

        /**
         * Bulk build 2D-tree using AVLTree for efficient sorting and grouping.
         * Complexity: O(N log N) where N is the number of stations.
         *
         * @param stations Input station list (app.model.Station)
         */
        public void bulkBuild(List<Station> stations) {
            if (stations == null || stations.isEmpty()) {
                root = null;
                size = 0;
                return;
            }
            size = 0;
            root = bulkBuildRecursive(new ArrayList<>(stations), true);
        }

        /**
         * Recursive helper using AVLTree to efficiently sort and partition stations.
         * Uses median of unique keys for balanced partitioning.
         *
         * @param stations subset to build (app.model.Station)
         * @param useLatitude dimension selector (latitude vs longitude)
         * @return root of subtree
         */
        private Node2D bulkBuildRecursive(List<Station> stations, boolean useLatitude) {
            if (stations.isEmpty()) return null;

            // Use AVLTree to group stations by current dimension key
            AVLTree<Double, Station> avl = new AVLTree<>();
            for (Station s : stations) {
                // Usar os getters da classe de domínio (getLat() e getLon())
                Double key = useLatitude ? s.getLat() : s.getLon();
                avl.insert(key, s);
            }

            // Get all sorted stations from AVL
            List<Station> sortedStations = avl.inOrderTraversal();

            // Extract unique keys to find true median for balanced split
            List<Double> uniqueKeys = sortedStations.stream()
                    .map(s -> useLatitude ? s.getLat() : s.getLon()) // Usar getters
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());

            // Find median key among unique keys
            int medianIndex = uniqueKeys.size() / 2;
            double medianKey = uniqueKeys.get(medianIndex);

            // Partition stations into left, equal, and right groups
            List<Station> leftList = new ArrayList<>();
            List<Station> equalList = new ArrayList<>();
            List<Station> rightList = new ArrayList<>();

            for (Station s : stations) {
                double stationKey = useLatitude ? s.getLat() : s.getLon(); // Usar getters
                int cmp = Double.compare(stationKey, medianKey);
                if (cmp < 0) {
                    leftList.add(s);
                } else if (cmp == 0) {
                    equalList.add(s);
                } else {
                    rightList.add(s);
                }
            }

            // Create bucket with stations sorted alphabetically by name
            equalList.sort(Comparator.comparing(Station::getName)); // Usar getName()
            StationBucket bucket = new StationBucket();
            for (Station s : equalList) {
                bucket.addStation(s);
            }

            // Build current node
            Node2D node = new Node2D(medianKey, useLatitude, bucket);
            size++;

            // Recursive build with alternating dimension
            node.left = bulkBuildRecursive(leftList, !useLatitude);
            node.right = bulkBuildRecursive(rightList, !useLatitude);

            node.updateHeight();
            return node;
        }

        /**
         * Result wrapper for a station with its distance from query point.
         */
        public static class StationWithDistance implements Comparable<StationWithDistance> {
            public final Station station;
            public final double distance;

            public StationWithDistance(Station station, double distance) {
                this.station = station;
                this.distance = distance;
            }

            @Override
            public int compareTo(StationWithDistance other) {
                int cmp = Double.compare(this.distance, other.distance);
                if (cmp != 0) return cmp;
                // If distances are equal, sort by name
                return this.station.getName().compareTo(other.station.getName());
            }
        }

        /**
         * Finds the N nearest stations to a given coordinate using Haversine distance.
         *
         * Algorithm:
         * 1. Uses KD-tree pruning to avoid checking all stations
         * 2. Maintains a max-heap of N best candidates
         * 3. Prunes branches that cannot contain closer stations
         * 4. Returns results sorted by distance ASC
         *
         * Temporal Complexity: O(log N) average case, O(N) worst case
         *
         * @param targetLat Target latitude in degrees
         * @param targetLon Target longitude in degrees
         * @param n Number of nearest neighbors to find
         * @return List of N nearest stations with distances, sorted by distance ASC
         */
        public List<StationWithDistance> findNearestStations(double targetLat, double targetLon, int n) {
            if (root == null || n <= 0) {
                return new ArrayList<>();
            }

            // Use a list to maintain best N candidates
            List<StationWithDistance> bestCandidates = new ArrayList<>();

            // Start recursive search
            findNearestRecursive(root, targetLat, targetLon, n, bestCandidates);

            // Sort by distance ascending
            Collections.sort(bestCandidates);

            return bestCandidates;
        }

        /**
         * Recursive helper for nearest neighbor search.
         * Uses branch pruning to avoid checking irrelevant subtrees.
         */
        private void findNearestRecursive(Node2D node, double targetLat, double targetLon,
                                          int n, List<StationWithDistance> bestCandidates) {
            if (node == null) return;

            // Calculate distance to all stations in current bucket
            for (Station station : node.bucket.stations) {
                double distance = HaversineDistance.calculate(
                        targetLat, targetLon, station.getLat(), station.getLon());

                StationWithDistance candidate = new StationWithDistance(station, distance);

                // Add to candidates
                if (bestCandidates.size() < n) {
                    bestCandidates.add(candidate);
                    Collections.sort(bestCandidates);
                } else if (distance < bestCandidates.get(bestCandidates.size() - 1).distance) {
                    // Replace worst candidate if this one is better
                    bestCandidates.set(bestCandidates.size() - 1, candidate);
                    Collections.sort(bestCandidates);
                }
            }

            // Determine which subtree to search first
            double targetValue = node.isLatitude ? targetLat : targetLon;
            boolean goLeft = targetValue < node.key;

            Node2D firstChild = goLeft ? node.left : node.right;
            Node2D secondChild = goLeft ? node.right : node.left;

            // Search the closer subtree first
            findNearestRecursive(firstChild, targetLat, targetLon, n, bestCandidates);

            // Check if we need to search the other subtree
            // Calculate the distance to the splitting plane
            double planeDistance = Math.abs(targetValue - node.key);

            // Convert to approximate km (rough estimate for pruning)
            // At equator, 1 degree ≈ 111 km
            double planeDistanceKm = planeDistance * 111.0;

            // Only search other side if it could contain closer points
            if (bestCandidates.size() < n ||
                    planeDistanceKm < bestCandidates.get(bestCandidates.size() - 1).distance) {
                findNearestRecursive(secondChild, targetLat, targetLon, n, bestCandidates);
            }
        }

        /**
         * Finds the single nearest station to a given coordinate.
         * Convenience method that calls findNearestStations with n=1.
         *
         * @param targetLat Target latitude in degrees
         * @param targetLon Target longitude in degrees
         * @return The nearest station with distance, or null if tree is empty
         */
        public StationWithDistance findNearestStation(double targetLat, double targetLon) {
            List<StationWithDistance> results = findNearestStations(targetLat, targetLon, 1);
            return results.isEmpty() ? null : results.get(0);
        }


        /**
         * Returns total number of distinct coordinate nodes.
         */
        public int getSize() {
            return size;
        }

        /**
         * Returns the height of the 2D-tree.
         */
        public int getHeight() {
            return root == null ? 0 : root.height;
        }

        /**
         * Returns all bucket sizes in in-order traversal.
         */
        public List<Integer> getBucketSizes() {
            List<Integer> sizes = new ArrayList<>();
            collectBucketSizes(root, sizes);
            return sizes;
        }

        /**
         * Returns temporal complexity analysis for the tree.
         */
        public TemporalComplexity getTemporalComplexity() {
            return new TemporalComplexity(size, getHeight());
        }

        /**
         * Returns all stations in in-order traversal.
         * Complexity: O(N) where N is the total number of stations.
         *
         * @return List of all stations in the tree
         */
        public List<Station> getAllStations() {
            List<Station> allStations = new ArrayList<>();
            collectAllStations(root, allStations);
            return allStations;
        }

        private void collectBucketSizes(Node2D node, List<Integer> sizes) {
            if (node == null) return;
            collectBucketSizes(node.left, sizes);
            sizes.add(node.bucket.size());
            collectBucketSizes(node.right, sizes);
        }

        private void collectAllStations(Node2D node, List<Station> stations) {
            if (node == null) return;
            collectAllStations(node.left, stations);
            stations.addAll(node.bucket.stations);
            collectAllStations(node.right, stations);
        }
    }
}
