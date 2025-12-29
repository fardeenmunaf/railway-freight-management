package app.view;

import app.utils.graph.MaxFlow;

/**
 * View helper for printing or formatting MaxFlow results.
 * All System.out.println calls for MaxFlow results should be placed here,
 * keeping the algorithm class free of direct I/O.
 */
public class MaxFlowView {

    /**
     * Formats a MaxFlow.Result into a human readable string.
     */
    public static <V, E> String formatResult(MaxFlow.Result<V, E> res) {
        StringBuilder sb = new StringBuilder();
        sb.append("MaxFlow Result:\n");

        if (res == null) {
            sb.append("  Result is null\n");
            return sb.toString();
        }

        sb.append("  Source key: ").append(res.sourceKey).append("\n");
        sb.append("  Sink key:   ").append(res.sinkKey).append("\n");
        sb.append("  Source:     ").append(res.source == null ? "null" : res.source.toString()).append("\n");
        sb.append("  Sink:       ").append(res.sink == null ? "null" : res.sink.toString()).append("\n");
        sb.append("  Max flow:   ").append(res.maxFlow == null ? "null" : res.maxFlow.toString()).append("\n");
        sb.append("  Message:    ").append(res.message == null ? "" : res.message).append("\n");
        sb.append("  Complexity: ").append(res.complexity == null ? "" : res.complexity).append("\n");

        return sb.toString();
    }

    /**
     * Prints the formatted MaxFlow.Result to System.out.
     */
    public static <V, E> void printResult(MaxFlow.Result<V, E> res) {
        System.out.println(formatResult(res));
    }
}

