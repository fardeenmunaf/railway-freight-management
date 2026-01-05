package app.utils.graph.map;

import app.utils.graph.Algorithms;
import app.utils.graph.Graph;
import app.utils.graph.matrix.MatrixGraph;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class MapAlgorithmsTest {

        final Graph<String, Integer> completeMap = new MapGraph<>(false);
        Graph<String, Integer> incompleteMap = new MapGraph<>(false);

        public MapAlgorithmsTest() {
        }

        @Before
        public void setUp() {

                completeMap.addVertex("Porto");
                completeMap.addVertex("Braga");
                completeMap.addVertex("Vila Real");
                completeMap.addVertex("Aveiro");
                completeMap.addVertex("Coimbra");
                completeMap.addVertex("Leiria");

                completeMap.addVertex("Viseu");
                completeMap.addVertex("Guarda");
                completeMap.addVertex("Castelo Branco");
                completeMap.addVertex("Lisboa");
                completeMap.addVertex("Faro");

                completeMap.addEdge("Porto", "Aveiro", 75);
                completeMap.addEdge("Porto", "Braga", 60);
                completeMap.addEdge("Porto", "Vila Real", 100);
                completeMap.addEdge("Viseu", "Guarda", 75);
                completeMap.addEdge("Guarda", "Castelo Branco", 100);
                completeMap.addEdge("Aveiro", "Coimbra", 60);
                completeMap.addEdge("Coimbra", "Lisboa", 200);
                completeMap.addEdge("Coimbra", "Leiria", 80);
                completeMap.addEdge("Aveiro", "Leiria", 120);
                completeMap.addEdge("Leiria", "Lisboa", 150);

                incompleteMap = completeMap.clone();

                completeMap.addEdge("Aveiro", "Viseu", 85);
                completeMap.addEdge("Leiria", "Castelo Branco", 170);
                completeMap.addEdge("Lisboa", "Faro", 280);
        }

        private void checkContentEquals(List<String> l1, List<String> l2, String msg) {
                Collections.sort(l1);
                Collections.sort(l2);
                assertEquals(msg, l1, l2);
        }

        /**
         * Test of BreadthFirstSearch method, of class Algorithms.
         */
        @Test
        public void testBreadthFirstSearch() {
                System.out.println("Test BreadthFirstSearch");

                assertNull("Should be null if vertex does not exist", Algorithms.BreadthFirstSearch(completeMap, "LX"));

                LinkedList<String> path = Algorithms.BreadthFirstSearch(incompleteMap, "Faro");

                assertEquals("Should be just one", 1, path.size());

                assertEquals("Faro", path.peekFirst());

                path = Algorithms.BreadthFirstSearch(incompleteMap, "Porto");
                assertEquals("Should give seven vertices", 7, path.size());

                assertEquals("BreathFirst Porto", "Porto", path.removeFirst());

                LinkedList<String> expected = new LinkedList<>(Arrays.asList("Aveiro", "Braga", "Vila Real"));
                checkContentEquals(expected, path.subList(0, 3), "BreathFirst Porto");

                expected = new LinkedList<>(Arrays.asList("Coimbra", "Leiria"));
                checkContentEquals(expected, path.subList(3, 5), "BreathFirst Porto");

                expected = new LinkedList<>(Arrays.asList("Lisboa"));
                checkContentEquals(expected, path.subList(5, 6), "BreathFirst Porto");

                path = Algorithms.BreadthFirstSearch(incompleteMap, "Viseu");
                expected = new LinkedList<>(Arrays.asList("Viseu", "Guarda", "Castelo Branco"));
                assertEquals("BreathFirst Viseu", expected, path);
        }

        /**
         * Test of DepthFirstSearch method, of class Algorithms.
         */
        @Test
        public void testDepthFirstSearch() {
                System.out.println("Test of DepthFirstSearch");

                assertNull("Should be null if vertex does not exist", Algorithms.DepthFirstSearch(completeMap, "LX"));

                LinkedList<String> path = Algorithms.DepthFirstSearch(incompleteMap, "Faro");
                assertEquals("Should be just one", 1, path.size());

                assertEquals("Faro", path.peekFirst());

                path = Algorithms.BreadthFirstSearch(incompleteMap, "Porto");
                assertEquals("Should give seven vertices", 7, path.size());

                assertEquals("DepthFirst Porto", "Porto", path.removeFirst());
                assertTrue("DepthFirst Porto",
                                new LinkedList<>(Arrays.asList("Aveiro", "Braga", "Vila Real"))
                                                .contains(path.removeFirst()));

                path = Algorithms.BreadthFirstSearch(incompleteMap, "Viseu");
                List<String> expected = new LinkedList<>(Arrays.asList("Viseu", "Guarda", "Castelo Branco"));
                assertEquals("DepthFirst Viseu", expected, path);
        }

        /**
         * Test of shortestPath method, of class Algorithms.
         */
        @Test
        public void testShortestPath() {
                System.out.println("Test of shortest path");

                LinkedList<String> shortPath = new LinkedList<>();

                Integer lenPath = Algorithms.shortestPath(completeMap, "Porto", "LX", Integer::compare, Integer::sum, 0,
                                shortPath);
                assertNull("Length path should be null if vertex does not exist", lenPath);
                assertEquals("Shortest Path does not exist", 0, shortPath.size());

                lenPath = Algorithms.shortestPath(incompleteMap, "Porto", "Faro", Integer::compare, Integer::sum, 0,
                                shortPath);
                assertNull("Length path should be null if vertex does not exist", lenPath);
                assertEquals("Shortest Path does not exist", 0, shortPath.size());

                lenPath = Algorithms.shortestPath(completeMap, "Porto", "Porto", Integer::compare, Integer::sum, 0,
                                shortPath);
                assertEquals("Length path should be 0 if vertices are the same", Integer.valueOf(0), lenPath);
                assertEquals("Shortest Path only contains Porto", Arrays.asList("Porto"), shortPath);

                lenPath = Algorithms.shortestPath(incompleteMap, "Porto", "Lisboa", Integer::compare, Integer::sum, 0,
                                shortPath);
                assertEquals("Length path should be 335", Integer.valueOf(335), lenPath);
                assertEquals("Shortest Path Porto - Lisboa", Arrays.asList("Porto", "Aveiro", "Coimbra", "Lisboa"),
                                shortPath);

                lenPath = Algorithms.shortestPath(incompleteMap, "Braga", "Leiria", Integer::compare, Integer::sum, 0,
                                shortPath);
                assertEquals("Length path should be 255", Integer.valueOf(255), lenPath);
                assertEquals("Shortest Path Braga - Leiria", Arrays.asList("Braga", "Porto", "Aveiro", "Leiria"),
                                shortPath);

                lenPath = Algorithms.shortestPath(completeMap, "Porto", "Castelo Branco", Integer::compare,
                                Integer::sum, 0,
                                shortPath);
                assertEquals("Length path should be 335", Integer.valueOf(335), lenPath);
                assertEquals("Shortest Path Porto - Castelo Branco",
                                Arrays.asList("Porto", "Aveiro", "Viseu", "Guarda", "Castelo Branco"), shortPath);

                // Changing Edge: Aveiro-Viseu with Edge: Leiria-C.Branco
                // should change shortest path between Porto and Castelo Branco

                completeMap.removeEdge("Aveiro", "Viseu");
                completeMap.addEdge("Leiria", "Castelo Branco", 170);

                lenPath = Algorithms.shortestPath(completeMap, "Porto", "Castelo Branco", Integer::compare,
                                Integer::sum, 0,
                                shortPath);
                assertEquals("Length path should be 365", Integer.valueOf(365), lenPath);
                assertEquals("Shortest Path Porto - Castelo Branco",
                                Arrays.asList("Porto", "Aveiro", "Leiria", "Castelo Branco"), shortPath);
        }

        /**
         * Test of shortestPaths method, of class Algorithms.
         */
        @Test
        public void testShortestPaths() {
                System.out.println("Test of shortest path");

                ArrayList<LinkedList<String>> paths = new ArrayList<>();
                ArrayList<Integer> dists = new ArrayList<>();

                Algorithms.shortestPaths(completeMap, "Porto", Integer::compare, Integer::sum, 0, paths, dists);

                assertEquals("There should be as many paths as sizes", paths.size(), dists.size());
                assertEquals("There should be a path to every vertex", completeMap.numVertices(), paths.size());
                assertEquals("Number of nodes should be 1 if source and vertex are the same", Arrays.asList("Porto"),
                                paths.get(completeMap.key("Porto")));
                assertEquals("Path to Lisbon", Arrays.asList("Porto", "Aveiro", "Coimbra", "Lisboa"),
                                paths.get(completeMap.key("Lisboa")));
                assertEquals("Path to Castelo Branco",
                                Arrays.asList("Porto", "Aveiro", "Viseu", "Guarda", "Castelo Branco"),
                                paths.get(completeMap.key("Castelo Branco")));
                assertEquals("Path between Porto and Castelo Branco should be 335 Km", Integer.valueOf(335),
                                dists.get(completeMap.key("Castelo Branco")));

                // Changing Edge: Aveiro-Viseu with Edge: Leiria-C.Branco
                // should change shortest path between Porto and Castelo Branco
                completeMap.removeEdge("Aveiro", "Viseu");
                completeMap.addEdge("Leiria", "Castelo Branco", 170);
                Algorithms.shortestPaths(completeMap, "Porto", Integer::compare, Integer::sum, 0, paths, dists);
                assertEquals("Path between Porto and Castelo Branco should now be 365 Km", Integer.valueOf(365),
                                dists.get(completeMap.key("Castelo Branco")));
                assertEquals("Path to Castelo Branco", Arrays.asList("Porto", "Aveiro", "Leiria", "Castelo Branco"),
                                paths.get(completeMap.key("Castelo Branco")));

                Algorithms.shortestPaths(incompleteMap, "Porto", Integer::compare, Integer::sum, 0, paths, dists);
                assertNull("Length path should be null if there is no path", dists.get(completeMap.key("Faro")));
                assertEquals("Path between Porto and Lisboa should be 335 Km", Integer.valueOf(335),
                                dists.get(completeMap.key("Lisboa")));
                assertEquals("Path to Lisboa", Arrays.asList("Porto", "Aveiro", "Coimbra", "Lisboa"),
                                paths.get(completeMap.key("Lisboa")));

                Algorithms.shortestPaths(incompleteMap, "Braga", Integer::compare, Integer::sum, 0, paths, dists);
                assertEquals("Path between Braga and Leiria should be 255 Km", Integer.valueOf(255),
                                dists.get(completeMap.key("Leiria")));
                assertEquals("Path to Leiria", Arrays.asList("Braga", "Porto", "Aveiro", "Leiria"),
                                paths.get(completeMap.key("Leiria")));
        }

        /**
         * Test minimum distance graph using Floyd-Warshall.
         */
        /**
         * Test minimum distance graph using Floyd-Warshall.
         */
        @Test
        public void testminDistGraph() {
                System.out.println("Test minDistGraph");

                MatrixGraph<String, Integer> mDist = Algorithms.minDistGraph(completeMap, Integer::compare,
                                Integer::sum);

                assertNotNull("Should not be null", mDist);
                assertEquals("Should have same vertices", completeMap.numVertices(), mDist.numVertices());
                assertEquals("Should have 335 from Porto to Lisboa", Integer.valueOf(335),
                                mDist.edge("Porto", "Lisboa").getWeight());
                assertEquals("Should have 335 from Porto to Castelo Branco", Integer.valueOf(335),
                                mDist.edge("Porto", "Castelo Branco").getWeight());
                assertEquals("Should have 255 from Braga to Leiria", Integer.valueOf(255),
                                mDist.edge("Braga", "Leiria").getWeight());

                // Changing Edge: Aveiro-Viseu with Edge: Leiria-C.Branco
                // should change shortest path between Porto and Castelo Branco
                Graph<String, Integer> clone = completeMap.clone();
                clone.removeEdge("Aveiro", "Viseu");
                clone.addEdge("Leiria", "Castelo Branco", 170);

                mDist = Algorithms.minDistGraph(clone, Integer::compare, Integer::sum);
                assertEquals("Should have 365 from Porto to Castelo Branco", Integer.valueOf(365),
                                mDist.edge("Porto", "Castelo Branco").getWeight());
        }

        /**
         * Test all paths.
         */
        @Test
        public void testAllPaths() {
                System.out.println("Test allPaths");

                ArrayList<LinkedList<String>> paths = Algorithms.allPaths(completeMap, "Porto", "Castelo Branco");
                assertNotNull("Should not be null", paths);
                assertEquals("There should be 4 paths", 4, paths.size());
                assertTrue("Path [Porto, Aveiro, Viseu, Guarda, Castelo Branco] should exist",
                                paths.contains(Arrays.asList("Porto", "Aveiro", "Viseu", "Guarda", "Castelo Branco")));
                assertTrue("Path [Porto, Aveiro, Leiria, Castelo Branco] should exist",
                                paths.contains(Arrays.asList("Porto", "Aveiro", "Leiria", "Castelo Branco")));
                assertTrue("Path [Porto, Aveiro, Coimbra, Leiria, Castelo Branco] should exist",
                                paths.contains(Arrays.asList("Porto", "Aveiro", "Coimbra", "Leiria",
                                                "Castelo Branco")));

                paths = Algorithms.allPaths(incompleteMap, "Porto", "Lisboa");
                assertEquals("There should be 4 paths", 4, paths.size());
                assertTrue("Path [Porto, Aveiro, Coimbra, Lisboa] should exist",
                                paths.contains(Arrays.asList("Porto", "Aveiro", "Coimbra", "Lisboa")));
                assertTrue("Path [Porto, Aveiro, Leiria, Lisboa] should exist",
                                paths.contains(Arrays.asList("Porto", "Aveiro", "Leiria", "Lisboa")));
                assertTrue("Path [Porto, Aveiro, Coimbra, Leiria, Lisboa] should exist",
                                paths.contains(Arrays.asList("Porto", "Aveiro", "Coimbra", "Leiria", "Lisboa")));

                paths = Algorithms.allPaths(incompleteMap, "Porto", "Faro");
                assertEquals("There should be 0 paths", 0, paths.size());
        }

        /**
         * USEI 15
         * Test of bellmanFord method, of class Algorithms.
         */
        @Test
        public void testBellmanFord() {
                System.out.println("Test of bellmanFord");

                LinkedList<String> shortPath = new LinkedList<>();

                // Test with non-existent vertex
                Integer lenPath = Algorithms.bellmanFord(completeMap, "Porto", "LX", Integer::compare, Integer::sum, 0, shortPath);
                assertNull("Length path should be null if vertex does not exist", lenPath);
                assertEquals("Shortest Path does not exist", 0, shortPath.size());

                // Test with same origin and destination
                lenPath = Algorithms.bellmanFord(completeMap, "Porto", "Porto", Integer::compare, Integer::sum, 0, shortPath);
                assertEquals("Length path should be 0 if vertices are the same", Integer.valueOf(0), lenPath);
                assertEquals("Shortest Path only contains Porto", Arrays.asList("Porto"), shortPath);

                // Test normal shortest path
                lenPath = Algorithms.bellmanFord(incompleteMap, "Porto", "Lisboa", Integer::compare, Integer::sum, 0, shortPath);
                assertEquals("Length path should be 335", Integer.valueOf(335), lenPath);
                assertEquals("Shortest Path Porto - Lisboa", Arrays.asList("Porto", "Aveiro", "Coimbra", "Lisboa"), shortPath);

                // Test another path
                lenPath = Algorithms.bellmanFord(completeMap, "Porto", "Castelo Branco", Integer::compare, Integer::sum, 0, shortPath);
                assertEquals("Length path should be 335", Integer.valueOf(335), lenPath);
                assertEquals("Shortest Path Porto - Castelo Branco",
                        Arrays.asList("Porto", "Aveiro", "Viseu", "Guarda", "Castelo Branco"), shortPath);

                // Test with no path available
                lenPath = Algorithms.bellmanFord(incompleteMap, "Porto", "Faro", Integer::compare, Integer::sum, 0, shortPath);
                assertNull("Length path should be null if no path exists", lenPath);

                // Test with negative weights - create a graph with negative edge
                Graph<String, Integer> negativeGraph = new MapGraph<>(true);
                negativeGraph.addVertex("A");
                negativeGraph.addVertex("B");
                negativeGraph.addVertex("C");
                negativeGraph.addEdge("A", "B", 5);
                negativeGraph.addEdge("B", "C", -2);
                negativeGraph.addEdge("A", "C", 4);

                lenPath = Algorithms.bellmanFord(negativeGraph, "A", "C", Integer::compare, Integer::sum, 0, shortPath);
                assertEquals("Should handle negative weights", Integer.valueOf(3), lenPath);
                assertEquals("Should find path through B", Arrays.asList("A", "B", "C"), shortPath);
        }

        /**
         * Test of topologicalSortWithCycles method, of class Algorithms.
         */
        @Test
        public void testTopologicalSortWithCycles() {
                System.out.println("Test of topologicalSortWithCycles");

                // Test with directed acyclic graph (DAG)
                Graph<String, Integer> dag = new MapGraph<>(true);
                dag.addVertex("A");
                dag.addVertex("B");
                dag.addVertex("C");
                dag.addVertex("D");
                dag.addVertex("E");
                dag.addEdge("A", "B", 1);
                dag.addEdge("A", "C", 1);
                dag.addEdge("B", "D", 1);
                dag.addEdge("C", "D", 1);
                dag.addEdge("D", "E", 1);

                Algorithms.TopologicalSortResult<String, Integer> result = Algorithms.topologicalSortWithCycles(dag);
                assertNotNull("Result should not be null", result);
                assertFalse("Should not have cycles", result.hasCycles());
                assertEquals("Should have 5 vertices in topological order", 5, result.getTopoOrder().size());
                assertEquals("First vertex should be A", "A", result.getTopoOrder().getFirst());
                assertEquals("Last vertex should be E", "E", result.getTopoOrder().getLast());
                assertTrue("Cycle stations should be empty", result.getCycleStations().isEmpty());
                assertTrue("Cycle edges should be empty", result.getCycleEdges().isEmpty());

                // Test with graph containing cycle
                Graph<String, Integer> cyclicGraph = new MapGraph<>(true);
                cyclicGraph.addVertex("A");
                cyclicGraph.addVertex("B");
                cyclicGraph.addVertex("C");
                cyclicGraph.addVertex("D");
                cyclicGraph.addEdge("A", "B", 1);
                cyclicGraph.addEdge("B", "C", 1);
                cyclicGraph.addEdge("C", "D", 1);
                cyclicGraph.addEdge("D", "B", 1); // Creates cycle B -> C -> D -> B

                result = Algorithms.topologicalSortWithCycles(cyclicGraph);
                assertNotNull("Result should not be null", result);
                assertTrue("Should detect cycles", result.hasCycles());
                assertTrue("Topological order should be empty when cycles exist", result.getTopoOrder().isEmpty());
                assertFalse("Cycle stations should not be empty", result.getCycleStations().isEmpty());
                assertFalse("Cycle edges should not be empty", result.getCycleEdges().isEmpty());
                assertTrue("B should be in cycle", result.getCycleStations().contains("B"));
                assertTrue("C should be in cycle", result.getCycleStations().contains("C"));
                assertTrue("D should be in cycle", result.getCycleStations().contains("D"));

                // Test with undirected graph - should throw exception
                try {
                        Algorithms.topologicalSortWithCycles(completeMap);
                        fail("Should throw IllegalArgumentException for undirected graph");
                } catch (IllegalArgumentException e) {
                        assertEquals("Should have correct error message",
                                "Topological sort requires directed graph", e.getMessage());
                }
        }

}