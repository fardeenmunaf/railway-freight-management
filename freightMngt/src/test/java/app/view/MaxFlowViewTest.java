package app.view;

import app.utils.graph.MaxFlow;
import org.junit.Test;

import static org.junit.Assert.*;

public class MaxFlowViewTest {

    @Test
    public void formatResultHandlesNull() {
        // call overload with explicit mode to avoid compile ambiguity
        String out = MaxFlowView.formatResult((MaxFlow.Result<String, Integer>) null, MaxFlowView.Mode.DETAILED);
        assertNotNull(out);
        assertTrue(out.contains("Result is null"));
    }

    @Test
    public void formatResultProducesFields() {
        MaxFlow.Result<String, Integer> res = new MaxFlow.Result<>("s", "t", 0, 1, 5, "OK", "O(V·E^2)");
        String out = MaxFlowView.formatResult(res, MaxFlowView.Mode.DETAILED);
        assertTrue(out.contains("MaxFlow Result"));
        assertTrue(out.contains("Source key"));
        assertTrue(out.contains("Sink key"));
        assertTrue(out.contains("Max flow"));
    }
}
