package app.utils.graph;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.BinaryOperator;

/**
 * Edmonds-Karp implementation for maximum flow on graphs with numeric capacities stored as E.
 * Supports a generic capacity type E via provided Comparator, add and subtract operators.
 *
 * Complexity: O(V * E^2) for Edmonds-Karp.
 */
public class MaxFlow {

    /**
     * Edmonds-Karp algorithm using explicit subtraction operator.
     *
     * @param g the graph containing vertices and weighted edges representing capacities
     * @param source the source vertex object (must be a valid vertex in g)
     * @param sink the sink vertex object (must be a valid vertex in g)
     * @param cmp comparator for capacity values (used to compare to zero and between capacities)
     * @param add binary operator that returns the sum of two capacity values
     * @param sub binary operator that returns the subtraction a - b for capacity values
     * @param zero representation of zero for the capacity type E
     * @param <V> vertex type
     * @param <E> capacity numeric type
     * @return the maximum flow value of type E from source to sink, or null if source/sink are invalid
     * @throws IllegalArgumentException if negative capacities are found
     */
    public static <V, E> E edmondsKarp(Graph<V, E> g, V source, V sink,
                                       Comparator<E> cmp, BinaryOperator<E> add, BinaryOperator<E> sub, E zero) {
        if (!g.validVertex(source) || !g.validVertex(sink)) return null;
        if (source.equals(sink)) return zero;

        // number of vertices
        int n = g.numVertices();

        // validate capacities non-negative
        for (Edge<V, E> e : g.edges()) {
            E cap = e.getWeight();
            if (cap == null) continue;
            if (cmp.compare(cap, zero) < 0) throw new IllegalArgumentException("Negative capacities are not allowed");
        }

        // build mapping vertex -> index based on g.vertices() (stable list)
        List<V> verts = g.vertices();
        Map<V, Integer> index = new IdentityHashMap<>();
        for (int i = 0; i < verts.size(); i++) {
            V v = verts.get(i);
            if (v != null) index.put(v, i);
        }

        // fallback mapping using g.key if some vertex objects differ
        // build residual adjacency maps
        Map<Integer, Map<Integer, E>> residual = new HashMap<>();
        for (int i = 0; i < verts.size(); i++) residual.put(i, new HashMap<>());

        // helper to resolve index of a vertex object
        java.util.function.Function<V, Integer> resolveIndex = (V v) -> {
            Integer idx = index.get(v);
            if (idx != null) return idx;
            try {
                int k = g.key(v);
                if (k >= 0 && k < n) return k;
            } catch (Exception ignored) {}
            return null;
        };

        // initialize residual capacities from graph edges
        for (Edge<V, E> e : g.edges()) {
            Integer u = resolveIndex.apply(e.getVOrig());
            Integer v = resolveIndex.apply(e.getVDest());
            if (u == null || v == null) continue;
            E cap = e.getWeight();
            if (cap == null) continue;
            if (cmp.compare(cap, zero) > 0) {
                Map<Integer, E> row = residual.get(u);
                // if multiple edges between same pair, sum capacities
                E prev = row.get(v);
                row.put(v, prev == null ? cap : add.apply(prev, cap));
            }
        }

        // resolve source/sink indices
        Integer sIdx = resolveIndex.apply(source);
        Integer tIdx = resolveIndex.apply(sink);
        if (sIdx == null || tIdx == null) return null;
        int s = sIdx, t = tIdx;

        E maxFlow = zero;

        // BFS parent array
        int[] parent = new int[n];

        while (true) {
            Arrays.fill(parent, -1);
            Queue<Integer> q = new ArrayDeque<>();
            parent[s] = -2;
            q.add(s);

            while (!q.isEmpty() && parent[t] == -1) {
                int u = q.poll();
                Map<Integer, E> row = residual.get(u);
                if (row == null) continue;
                for (Map.Entry<Integer, E> ent : row.entrySet()) {
                    int v = ent.getKey();
                    E cap = ent.getValue();
                    if (cap == null) continue;
                    if (parent[v] == -1 && cmp.compare(cap, zero) > 0) {
                        parent[v] = u;
                        q.add(v);
                        if (v == t) break;
                    }
                }
            }

            if (parent[t] == -1) break; // no augmenting path

            // find bottleneck
            E bottleneck = null;
            int v = t;
            while (v != s) {
                int u = parent[v];
                E cap = residual.get(u).get(v);
                if (cap == null) cap = zero;
                if (bottleneck == null) bottleneck = cap; else bottleneck = min(bottleneck, cap, cmp);
                v = u;
            }

            // add to maxFlow
            maxFlow = add.apply(maxFlow, bottleneck);

            // update residual along path
            v = t;
            while (v != s) {
                int u = parent[v];
                Map<Integer, E> row = residual.get(u);
                E curr = row.get(v);
                if (curr == null) curr = zero;
                E newCap = sub.apply(curr, bottleneck);
                if (cmp.compare(newCap, zero) <= 0) row.remove(v); else row.put(v, newCap);

                Map<Integer, E> revRow = residual.get(v);
                E rev = revRow.get(u);
                if (rev == null) revRow.put(u, bottleneck); else revRow.put(u, add.apply(rev, bottleneck));

                v = u;
            }
        }

        return maxFlow;
    }

    /**
     * Edmonds-Karp convenience overload that derives a subtraction operator for common numeric types.
     *
     * @param g the graph
     * @param source the source vertex
     * @param sink the sink vertex
     * @param cmp comparator for capacity values
     * @param add addition operator for capacity values
     * @param zero zero value for the capacity type
     * @param <V> vertex type
     * @param <E> capacity type
     * @return maximum flow value or null on invalid vertices
     */
    public static <V, E> E edmondsKarp(Graph<V, E> g, V source, V sink,
                                       Comparator<E> cmp, BinaryOperator<E> add, E zero) {
        BinaryOperator<E> sub = defaultSubtract(zero);
        return edmondsKarp(g, source, sink, cmp, add, sub, zero);
    }

    // helper to pick minimum
    private static <E> E min(E a, E b, Comparator<E> cmp) { return (cmp.compare(a, b) <= 0) ? a : b; }

    @SuppressWarnings("unchecked")
    private static <E> BinaryOperator<E> defaultSubtract(E zero) {
        if (zero instanceof Integer) return (a, b) -> (E) Integer.valueOf(((Integer) a) - ((Integer) b));
        if (zero instanceof Double) return (a, b) -> (E) Double.valueOf(((Double) a) - ((Double) b));
        if (zero instanceof Long) return (a, b) -> (E) Long.valueOf(((Long) a) - ((Long) b));
        if (zero instanceof Float) return (a, b) -> (E) Float.valueOf(((Float) a) - ((Float) b));
        if (zero instanceof BigInteger) return (a, b) -> (E) ((BigInteger) a).subtract((BigInteger) b);
        if (zero instanceof BigDecimal) return (a, b) -> (E) ((BigDecimal) a).subtract((BigDecimal) b);
        throw new UnsupportedOperationException("Subtraction not supported for capacity type: " + zero.getClass());
    }

    /**
     * Result wrapper containing source/sink vertices, keys and the computed max flow.
     *
     * @param <V> vertex type
     * @param <E> capacity type
     */
    public static class Result<V, E> {
        public final V source;
        public final V sink;
        public final Integer sourceKey;
        public final Integer sinkKey;
        public final E maxFlow;
        public final String message;
        public final String complexity;

        public Result(V source, V sink, E maxFlow, String message, String complexity) {
            this(source, sink, null, null, maxFlow, message, complexity);
        }

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
     * Compute the maximum flow between two vertex objects.
     *
     * @param g the graph
     * @param source source vertex object
     * @param sink sink vertex object
     * @param cmp comparator for capacity type
     * @param add addition operator for capacity type
     * @param sub subtraction operator for capacity type
     * @param zero zero value for capacity type
     * @param <V> vertex type
     * @param <E> capacity type
     * @return Result wrapper with computed max flow and metadata
     */
    public static <V, E> Result<V, E> compute(Graph<V, E> g, V source, V sink,
                                              Comparator<E> cmp, BinaryOperator<E> add, BinaryOperator<E> sub, E zero) {
        Integer sKey = g.validVertex(source) ? Integer.valueOf(g.key(source)) : null;
        Integer tKey = g.validVertex(sink) ? Integer.valueOf(g.key(sink)) : null;
        if (!g.validVertex(source) || !g.validVertex(sink)) return new Result<>(source, sink, sKey, tKey, null, "Invalid source or sink vertex.", "O(V·E^2)");
        if (source.equals(sink)) return new Result<>(source, sink, sKey, tKey, zero, "Source equals sink - max flow is 0.", "O(1)");

        E flow = edmondsKarp(g, source, sink, cmp, add, sub, zero);
        if (flow == null) return new Result<>(source, sink, sKey, tKey, null, "Computation failed (invalid vertices)", "O(V·E^2)");
        String msg = cmp.compare(flow, zero) == 0 ? "No augmenting path found - max flow is 0." : "Max flow computed successfully.";
        return new Result<>(source, sink, sKey, tKey, flow, msg, "O(V·E^2)");
    }

    /**
     * Compute maximum flow and derive subtraction for common numeric types.
     *
     * @param g the graph
     * @param source source vertex object
     * @param sink sink vertex object
     * @param cmp comparator for capacity type
     * @param add addition operator for capacity type
     * @param zero zero value for capacity type
     * @param <V> vertex type
     * @param <E> capacity type
     * @return Result wrapper with computed max flow and metadata
     */
    public static <V, E> Result<V, E> compute(Graph<V, E> g, V source, V sink,
                                              Comparator<E> cmp, BinaryOperator<E> add, E zero) {
        BinaryOperator<E> sub = defaultSubtract(zero);
        return compute(g, source, sink, cmp, add, sub, zero);
    }

    /**
     * Compute maximum flow by resolving vertex keys (graph-internal integer keys) to vertex objects.
     *
     * @param g the graph
     * @param sourceKey graph-internal key for source vertex
     * @param sinkKey graph-internal key for sink vertex
     * @param cmp comparator for capacity type
     * @param add addition operator for capacity type
     * @param sub subtraction operator for capacity type
     * @param zero zero value for capacity type
     * @param <V> vertex type
     * @param <E> capacity type
     * @return Result wrapper with computed max flow and metadata
     */
    public static <V, E> Result<V, E> compute(Graph<V, E> g, int sourceKey, int sinkKey,
                                              Comparator<E> cmp, BinaryOperator<E> add, BinaryOperator<E> sub, E zero) {
        V s = g.vertex(sourceKey);
        V t = g.vertex(sinkKey);
        if (s == null || t == null) return new Result<>(null, null, sourceKey, sinkKey, null, "Invalid source or sink key.", "O(V·E^2)");
        Result<V, E> r = compute(g, s, t, cmp, add, sub, zero);
        return new Result<>(r.source, r.sink, sourceKey, sinkKey, r.maxFlow, r.message, r.complexity);
    }

    /**
     * Compute maximum flow by resolving vertex keys and deriving subtraction for common numeric types.
     *
     * @param g the graph
     * @param sourceKey graph-internal key for source vertex
     * @param sinkKey graph-internal key for sink vertex
     * @param cmp comparator for capacity type
     * @param add addition operator for capacity type
     * @param zero zero value for capacity type
     * @param <V> vertex type
     * @param <E> capacity type
     * @return Result wrapper with computed max flow and metadata
     */
    public static <V, E> Result<V, E> compute(Graph<V, E> g, int sourceKey, int sinkKey,
                                              Comparator<E> cmp, BinaryOperator<E> add, E zero) {
        BinaryOperator<E> sub = defaultSubtract(zero);
        return compute(g, sourceKey, sinkKey, cmp, add, sub, zero);
    }
}
