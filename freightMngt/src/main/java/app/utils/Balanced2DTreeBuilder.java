package app.utils;

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
 * @version 1.0
 */
public class Balanced2DTreeBuilder {

    /**
     * Represents a station with coordinates and a name.
     */
    public static class Station {
        public final double latitude;
        public final double longitude;
        public final String name;

        public Station(double latitude, double longitude, String name) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.name = name;
        }
    }

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
            int pos = Collections.binarySearch(stations, s, Comparator.comparing(st -> st.name));
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
         * @param stations Input station list
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
         * @param stations subset to build
         * @param useLatitude dimension selector (latitude vs longitude)
         * @return root of subtree
         */
        private Node2D bulkBuildRecursive(List<Station> stations, boolean useLatitude) {
            if (stations.isEmpty()) return null;

            // Use AVLTree to group stations by current dimension key
            AVLTree<Double, Station> avl = new AVLTree<>();
            for (Station s : stations) {
                Double key = useLatitude ? s.latitude : s.longitude;
                avl.insert(key, s);
            }

            // Get all sorted stations from AVL
            List<Station> sortedStations = avl.inOrderTraversal();

            // Extract unique keys to find true median for balanced split
            List<Double> uniqueKeys = sortedStations.stream()
                    .map(s -> useLatitude ? s.latitude : s.longitude)
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
                double stationKey = useLatitude ? s.latitude : s.longitude;
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
            equalList.sort(Comparator.comparing(s -> s.name));
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
