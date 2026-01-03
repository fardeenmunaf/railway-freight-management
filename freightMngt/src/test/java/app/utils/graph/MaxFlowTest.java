package app.utils.graph;

import app.utils.graph.matrix.MatrixGraph;
import org.junit.Test;

import static org.junit.Assert.*;

public class MaxFlowTest {

    @Test
    public void testSourceEqualsSink() {
        MatrixGraph<String, Integer> g = new MatrixGraph<>(true);
        g.addVertex("s");

        MaxFlow.Result<String, Integer> res = MaxFlow.compute(g, "s", "s", Integer::compare, Integer::sum, 0);
        assertNotNull("Result should not be null", res);
        assertEquals("Max flow should be zero when source equals sink", Integer.valueOf(0), res.maxFlow);
        assertTrue("Message should mention source equals sink", res.message != null && res.message.contains("Source equals sink"));
    }

    @Test
    public void testNoPath() {
        MatrixGraph<String, Integer> g = new MatrixGraph<>(true);
        g.addVertex("A");
        g.addVertex("B");
        g.addVertex("C");
        g.addEdge("A", "B", 5);

        MaxFlow.Result<String, Integer> res = MaxFlow.compute(g, "A", "C", Integer::compare, Integer::sum, 0);
        assertNotNull("Result should not be null", res);
        assertEquals("Max flow should be zero when no path exists", Integer.valueOf(0), res.maxFlow);
        assertTrue("Message should indicate no augmenting path", res.message != null && res.message.contains("No augmenting path"));
    }

    @Test
    public void testClassicExample() {
        MatrixGraph<String, Integer> g = new MatrixGraph<>(true);

        g.addEdge("s", "a", 3);
        g.addEdge("s", "b", 2);
        g.addEdge("a", "b", 1);
        g.addEdge("a", "t", 2);
        g.addEdge("b", "t", 3);

        MaxFlow.Result<String, Integer> res = MaxFlow.compute(g, "s", "t", Integer::compare, Integer::sum, 0);
        assertNotNull("Result should not be null", res);
        // The graph allows total flow 5: s->a (3) split as 2 to a->t and 1 a->b->t, and s->b (2) to b->t => total 5
        assertEquals("Expected max flow of 5 for the classic example", Integer.valueOf(5), res.maxFlow);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeCapacityThrows() {
        MatrixGraph<String, Integer> g = new MatrixGraph<>(true);
        g.addEdge("s", "t", -5);

        // Should throw IllegalArgumentException due to negative capacity
        MaxFlow.compute(g, "s", "t", Integer::compare, Integer::sum, 0);
    }
}
