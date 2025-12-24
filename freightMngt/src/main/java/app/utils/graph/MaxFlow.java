package app.utils.graph;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.BinaryOperator;

/**
 * Edmonds-Karp implementation for maximum flow on graphs with numeric capacities stored as E.
 * This implementation assumes capacities are non-negative and supports any numeric type where
 * addition, comparison and subtraction are provided externally via BinaryOperator and Comparator.
 *
 * The algorithm uses the graph's Edge weights as capacities. It constructs and updates a
 * residual graph using a matrix indexed by vertex keys. The original Graph is not mutated.
 *
 * Complexity: O(V * E^2) for Edmonds-Karp.
 */
public class MaxFlow {

    /**
     * Computes maximum flow value between source and sink using Edmonds-Karp.
     * This variant requires a subtraction operator so the implementation does not rely on unsafe casts.
     *
     * @param g input graph where edge weights are capacities (type E)
     * @param source source vertex (must be a vertex present in g)
     * @param sink sink vertex (must be a vertex present in g)
     * @param cmp comparator for capacities
     * @param add function to add two capacities
     * @param sub function to subtract two capacities (a - b)
     * @param zero zero element for capacities
     * @param <V> vertex type
     * @param <E> capacity (edge weight) type
     * @return max flow value (E) or null if validation fails (e.g., source/sink invalid)
     * @throws IllegalArgumentException if negative capacities are found
     */
    public static <V, E> E edmondsKarp(Graph<V, E> g, V source, V sink,
                                       Comparator<E> cmp, BinaryOperator<E> add, BinaryOperator<E> sub, E zero) {
        if (!g.validVertex(source) || !g.validVertex(sink))
            return null;
        if (source.equals(sink))
            return zero; // by definition

        int n = g.numVertices();

        // Validate capacities are non-negative: we check using comparator assuming zero is the neutral element
        for (Edge<V, E> e : g.edges()) {
            E cap = e.getWeight();
            if (cap == null)
                continue;
            if (cmp.compare(cap, zero) < 0)
                throw new IllegalArgumentException("Negative capacities are not allowed");
        }

        @SuppressWarnings("unchecked")
        E[][] residual = (E[][]) new Object[n][n];

        // initialize residual with capacities from original graph
        for (Edge<V, E> e : g.edges()) {
            int u = g.key(e.getVOrig());
            int v = g.key(e.getVDest());
            E cap = e.getWeight();
            if (cap != null)
                residual[u][v] = cap;
        }

        E maxFlow = zero;

        int s = g.key(source);
        int t = g.key(sink);

        // BFS parent array
        int[] parent = new int[n];

        while (true) {
            // BFS to find augmenting path in residual graph
            Arrays.fill(parent, -1);
            Queue<Integer> q = new LinkedList<>();
            parent[s] = -2; // mark source as visited
            q.add(s);

            while (!q.isEmpty() && parent[t] == -1) {
                int u = q.poll();
                for (int v = 0; v < n; v++) {
                    if (parent[v] == -1 && residual[u][v] != null && cmp.compare(residual[u][v], zero) > 0) {
                        parent[v] = u;
                        q.add(v);
                        if (v == t) break;
                    }
                }
            }

            if (parent[t] == -1)
                break; // no more augmenting paths

            // find bottleneck capacity along path
            E bottleneck = null;
            int v = t;
            while (v != s) {
                int u = parent[v];
                E cap = residual[u][v];
                if (bottleneck == null) bottleneck = cap;
                else bottleneck = min(bottleneck, cap, cmp);
                v = u;
            }

            // add bottleneck to maxFlow
            maxFlow = add.apply(maxFlow, bottleneck);

            // update residual capacities along path
            v = t;
            while (v != s) {
                int u = parent[v];
                // residual[u][v] -= bottleneck
                residual[u][v] = sub.apply(residual[u][v], bottleneck);
                // residual[v][u] += bottleneck
                if (residual[v][u] == null) residual[v][u] = bottleneck;
                else residual[v][u] = add.apply(residual[v][u], bottleneck);
                v = u;
            }
        }

        return maxFlow;
    }

    // backward-compatible overload that derives a subtraction operator for common numeric types
    public static <V, E> E edmondsKarp(Graph<V, E> g, V source, V sink,
                                       Comparator<E> cmp, BinaryOperator<E> add, E zero) {
        BinaryOperator<E> sub = defaultSubtract(zero);
        return edmondsKarp(g, source, sink, cmp, add, sub, zero);
    }

    // utility to choose minimum of two E using comparator
    private static <E> E min(E a, E b, Comparator<E> cmp) {
        return (cmp.compare(a, b) <= 0) ? a : b;
    }

    @SuppressWarnings("unchecked")
    private static <E> BinaryOperator<E> defaultSubtract(E zero) {
        if (zero instanceof Integer) {
            return (a, b) -> (E) Integer.valueOf(((Integer) a) - ((Integer) b));
        } else if (zero instanceof Double) {
            return (a, b) -> (E) Double.valueOf(((Double) a) - ((Double) b));
        } else if (zero instanceof Long) {
            return (a, b) -> (E) Long.valueOf(((Long) a) - ((Long) b));
        } else if (zero instanceof Float) {
            return (a, b) -> (E) Float.valueOf(((Float) a) - ((Float) b));
        } else if (zero instanceof BigInteger) {
            return (a, b) -> (E) ((BigInteger) a).subtract((BigInteger) b);
        } else if (zero instanceof BigDecimal) {
            return (a, b) -> (E) ((BigDecimal) a).subtract((BigDecimal) b);
        }
        throw new UnsupportedOperationException("Subtraction not supported for capacity type: " + zero.getClass());
    }

    /**
     * Result wrapper containing flow summary and metadata.
     * @param <V> vertex type
     * @param <E> capacity type
     */
    public static class Result<V, E> {
        public final V source;
        public final V sink;
        public final Integer sourceKey; // optional numeric key for source (stid)
        public final Integer sinkKey;   // optional numeric key for sink (stid)
        public final E maxFlow;
        public final String message;
        public final String complexity;

        // original constructor (no keys)
        public Result(V source, V sink, E maxFlow, String message, String complexity) {
            this(source, sink, null, null, maxFlow, message, complexity);
        }

        // constructor with explicit keys
        public Result(V source, V sink, Integer sourceKey, Integer sinkKey, E maxFlow, String message, String complexity) {
            this.source = source;
            this.sink = sink;
            this.sourceKey = sourceKey;
            this.sinkKey = sinkKey;
            this.maxFlow = maxFlow;
            this.message = message;
            this.complexity = complexity;
        }
    }

    /**
     * Convenience method that computes max flow and returns a Result that includes a message
     * and documented complexity. Messages follow the acceptance criteria:
     * - source == sink -> maxFlow = zero, message indicates equality
     * - no path -> maxFlow = zero, message indicates no path
     * - negative capacities -> throws IllegalArgumentException
     *
     * This variant requires a subtraction operator.
     */
    public static <V, E> Result<V, E> compute(Graph<V, E> g, V source, V sink,
                                              Comparator<E> cmp, BinaryOperator<E> add, BinaryOperator<E> sub, E zero) {
        // compute keys when possible to include stids in the Result
        Integer sKey = g.validVertex(source) ? Integer.valueOf(g.key(source)) : null;
        Integer tKey = g.validVertex(sink) ? Integer.valueOf(g.key(sink)) : null;

        if (!g.validVertex(source) || !g.validVertex(sink))
            return new Result<>(source, sink, sKey, tKey, null, "Invalid source or sink vertex.", "O(V·E^2)");

        if (source.equals(sink)) {
            return new Result<>(source, sink, sKey, tKey, zero, "Source equals sink - max flow is 0.", "O(1)");
        }

        // run algorithm
        E flow = edmondsKarp(g, source, sink, cmp, add, sub, zero);
        if (flow == null) {
            // This indicates validation failed inside edmondsKarp (e.g., invalid vertices)
            return new Result<>(source, sink, sKey, tKey, null, "Computation failed (invalid vertices)", "O(V·E^2)");
        }

        String msg;
        if (cmp.compare(flow, zero) == 0) {
            msg = "No augmenting path found - max flow is 0.";
        } else {
            msg = "Max flow computed successfully.";
        }

        return new Result<>(source, sink, sKey, tKey, flow, msg, "O(V·E^2)");
    }

    // backward-compatible overload that derives a subtraction operator for common numeric types
    public static <V, E> Result<V, E> compute(Graph<V, E> g, V source, V sink,
                                              Comparator<E> cmp, BinaryOperator<E> add, E zero) {
        BinaryOperator<E> sub = defaultSubtract(zero);
        return compute(g, source, sink, cmp, add, sub, zero);
    }

    // Overloads that accept numeric keys (station ids) directly
    public static <V, E> Result<V, E> compute(Graph<V, E> g, int sourceKey, int sinkKey,
                                              Comparator<E> cmp, BinaryOperator<E> add, BinaryOperator<E> sub, E zero) {
        V s = g.vertex(sourceKey);
        V t = g.vertex(sinkKey);
        if (s == null || t == null)
            return new Result<>(null, null, sourceKey, sinkKey, null, "Invalid source or sink key.", "O(V·E^2)");

        Result<V, E> r = compute(g, s, t, cmp, add, sub, zero);
        // return same result but include keys
        return new Result<>(r.source, r.sink, sourceKey, sinkKey, r.maxFlow, r.message, r.complexity);
    }

    public static <V, E> Result<V, E> compute(Graph<V, E> g, int sourceKey, int sinkKey,
                                              Comparator<E> cmp, BinaryOperator<E> add, E zero) {
        BinaryOperator<E> sub = defaultSubtract(zero);
        return compute(g, sourceKey, sinkKey, cmp, add, sub, zero);
    }

}
