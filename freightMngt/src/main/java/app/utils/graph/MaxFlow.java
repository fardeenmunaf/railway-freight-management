package app.utils.graph;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.BinaryOperator;

/**
 * Implementação do algoritmo Edmonds-Karp para cálculo do fluxo máximo.
 * USEI14: Calcula o throughput máximo teórico entre dois hubs (estações).
 * Complexidade: O(V * E²) onde V=vértices (estações) e E=arestas (corredores).
 */
public class MaxFlow {

    /**
     * Algoritmo Edmonds-Karp: encontra o fluxo máximo entre source e sink.
     * Usa BFS para encontrar caminhos aumentantes e atualiza capacidades residuais.
     */
    public static <V, E> E edmondsKarp(Graph<V, E> g, V source, V sink,
                                       Comparator<E> cmp, BinaryOperator<E> add, BinaryOperator<E> sub, E zero) {
        // Validações iniciais
        if (!g.validVertex(source) || !g.validVertex(sink)) return null;
        if (source.equals(sink)) return zero;

        int n = g.numVertices();

        // Valida que todas as capacidades são não-negativas
        for (Edge<V, E> e : g.edges()) {
            E cap = e.getWeight();
            if (cap == null) continue;
            if (cmp.compare(cap, zero) < 0) throw new IllegalArgumentException("Negative capacities are not allowed");
        }

        // Mapeia vértices para índices inteiros (para usar arrays)
        List<V> verts = g.vertices();
        Map<V, Integer> index = new IdentityHashMap<>();
        for (int i = 0; i < verts.size(); i++) {
            V v = verts.get(i);
            if (v != null) index.put(v, i);
        }

        // Constrói o grafo residual: armazena capacidades disponíveis entre vértices
        // Estrutura: Map<origem, Map<destino, capacidade>>
        Map<Integer, Map<Integer, E>> residual = new HashMap<>();
        for (int i = 0; i < verts.size(); i++) residual.put(i, new HashMap<>());

        // Função auxiliar para resolver índice de um vértice
        java.util.function.Function<V, Integer> resolveIndex = (V v) -> {
            Integer idx = index.get(v);
            if (idx != null) return idx;
            try {
                int k = g.key(v);
                if (k >= 0 && k < n) return k;
            } catch (Exception ignored) {}
            return null;
        };

        // Inicializa capacidades residuais a partir das arestas do grafo
        for (Edge<V, E> e : g.edges()) {
            Integer u = resolveIndex.apply(e.getVOrig());
            Integer v = resolveIndex.apply(e.getVDest());
            if (u == null || v == null) continue;
            E cap = e.getWeight();
            if (cap == null) continue;
            if (cmp.compare(cap, zero) > 0) {
                Map<Integer, E> row = residual.get(u);
                E prev = row.get(v);
                // Se múltiplas arestas entre mesmo par, soma capacidades
                row.put(v, prev == null ? cap : add.apply(prev, cap));
            }
        }

        // Resolve índices de source e sink
        Integer sIdx = resolveIndex.apply(source);
        Integer tIdx = resolveIndex.apply(sink);
        if (sIdx == null || tIdx == null) return null;
        int s = sIdx, t = tIdx;

        E maxFlow = zero; // Acumula o fluxo máximo total
        int[] parent = new int[n]; // Array para reconstruir caminho BFS

        // LOOP PRINCIPAL: enquanto existir caminho aumentante
        while (true) {
            // FASE 1: BFS para encontrar caminho de source a sink
            Arrays.fill(parent, -1);
            Queue<Integer> q = new ArrayDeque<>();
            parent[s] = -2; // Marca source como visitado
            q.add(s);

            while (!q.isEmpty() && parent[t] == -1) {
                int u = q.poll();
                Map<Integer, E> row = residual.get(u);
                if (row == null) continue;

                for (Map.Entry<Integer, E> ent : row.entrySet()) {
                    int v = ent.getKey();
                    E cap = ent.getValue();
                    if (cap == null) continue;
                    // Se vértice não visitado E tem capacidade disponível
                    if (parent[v] == -1 && cmp.compare(cap, zero) > 0) {
                        parent[v] = u;
                        q.add(v);
                        if (v == t) break; // Alcançou sink
                    }
                }
            }

            // Se não encontrou caminho, algoritmo termina
            if (parent[t] == -1) break;

            // FASE 2: Encontra bottleneck (menor capacidade no caminho)
            E bottleneck = null;
            int v = t;
            while (v != s) {
                int u = parent[v];
                E cap = residual.get(u).get(v);
                if (cap == null) cap = zero;
                if (bottleneck == null) bottleneck = cap;
                else bottleneck = min(bottleneck, cap, cmp);
                v = u;
            }

            // FASE 3: Adiciona bottleneck ao fluxo máximo
            maxFlow = add.apply(maxFlow, bottleneck);

            // FASE 4: Atualiza capacidades residuais no caminho
            v = t;
            while (v != s) {
                int u = parent[v];

                // Reduz capacidade forward (u->v)
                Map<Integer, E> row = residual.get(u);
                E curr = row.get(v);
                if (curr == null) curr = zero;
                E newCap = sub.apply(curr, bottleneck);
                if (cmp.compare(newCap, zero) <= 0) row.remove(v);
                else row.put(v, newCap);

                // Aumenta capacidade backward (v->u) - permite "desfazer" fluxo
                Map<Integer, E> revRow = residual.get(v);
                E rev = revRow.get(u);
                if (rev == null) revRow.put(u, bottleneck);
                else revRow.put(u, add.apply(rev, bottleneck));

                v = u;
            }
        }

        return maxFlow; // Retorna throughput máximo calculado
    }

    /**
     * Sobrecarga que cria automaticamente operador de subtração.
     */
    public static <V, E> E edmondsKarp(Graph<V, E> g, V source, V sink,
                                       Comparator<E> cmp, BinaryOperator<E> add, E zero) {
        BinaryOperator<E> sub = defaultSubtract(zero);
        return edmondsKarp(g, source, sink, cmp, add, sub, zero);
    }

    private static <E> E min(E a, E b, Comparator<E> cmp) {
        return (cmp.compare(a, b) <= 0) ? a : b;
    }

    /**
     * Cria operador de subtração baseado no tipo (Integer, Double, Long, etc.).
     */
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
     * Classe Result: encapsula resultado do cálculo de fluxo máximo.
     * Contém source/sink, IDs das estações, valor do fluxo e complexidade.
     */
    public static class Result<V, E> {
        public final V source;          // Vértice origem
        public final V sink;            // Vértice destino
        public final Integer sourceKey; // ID da estação origem
        public final Integer sinkKey;   // ID da estação destino
        public final E maxFlow;         // Valor do fluxo máximo (throughput)
        public final String message;    // Mensagem descritiva
        public final String complexity; // Complexidade temporal

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
     * Método principal para USEI14: calcula fluxo máximo entre dois vértices.
     * Retorna Result com source stid, target stid, maxFlowValue e complexidade.
     */
    public static <V, E> Result<V, E> compute(Graph<V, E> g, V source, V sink,
                                              Comparator<E> cmp, BinaryOperator<E> add, BinaryOperator<E> sub, E zero) {
        Integer sKey = g.validVertex(source) ? Integer.valueOf(g.key(source)) : null;
        Integer tKey = g.validVertex(sink) ? Integer.valueOf(g.key(sink)) : null;

        // Validações
        if (!g.validVertex(source) || !g.validVertex(sink))
            return new Result<>(source, sink, sKey, tKey, null, "Invalid source or sink vertex.", "O(VE^2)");
        if (source.equals(sink))
            return new Result<>(source, sink, sKey, tKey, zero, "Source equals sink - max flow is 0.", "O(1)");

        // Executa algoritmo Edmonds-Karp
        E flow = edmondsKarp(g, source, sink, cmp, add, sub, zero);
        if (flow == null)
            return new Result<>(source, sink, sKey, tKey, null, "Computation failed (invalid vertices)", "O(VE^2)");

        String msg = cmp.compare(flow, zero) == 0 ?
            "No augmenting path found - max flow is 0." :
            "Max flow computed successfully.";

        return new Result<>(source, sink, sKey, tKey, flow, msg, "O(VE^2)");
    }

    /**
     * Versão simplificada que cria automaticamente operador de subtração.
     */
    public static <V, E> Result<V, E> compute(Graph<V, E> g, V source, V sink,
                                              Comparator<E> cmp, BinaryOperator<E> add, E zero) {
        BinaryOperator<E> sub = defaultSubtract(zero);
        return compute(g, source, sink, cmp, add, sub, zero);
    }

    /**
     * Calcula fluxo máximo usando IDs das estações (facilityId ou chave do grafo).
     */
    public static <V, E> Result<V, E> compute(Graph<V, E> g, int sourceKey, int sinkKey,
                                              Comparator<E> cmp, BinaryOperator<E> add, BinaryOperator<E> sub, E zero) {
        V s = g.vertex(sourceKey);
        V t = g.vertex(sinkKey);
        if (s == null || t == null)
            return new Result<>(null, null, sourceKey, sinkKey, null, "Invalid source or sink key.", "O(VE^2)");

        Result<V, E> r = compute(g, s, t, cmp, add, sub, zero);
        return new Result<>(r.source, r.sink, sourceKey, sinkKey, r.maxFlow, r.message, r.complexity);
    }

    /**
     * Versão mais conveniente: usa IDs e cria operador de subtração automaticamente.
     */
    public static <V, E> Result<V, E> compute(Graph<V, E> g, int sourceKey, int sinkKey,
                                              Comparator<E> cmp, BinaryOperator<E> add, E zero) {
        BinaryOperator<E> sub = defaultSubtract(zero);
        return compute(g, sourceKey, sinkKey, cmp, add, sub, zero);
    }
}

