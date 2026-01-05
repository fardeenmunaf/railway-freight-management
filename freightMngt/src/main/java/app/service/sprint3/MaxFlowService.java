package app.service.sprint3;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.Comparator;
import java.util.function.BinaryOperator;

import app.model.EdgeData;
import app.model.Facility;
import app.model.Station;
import app.repositories.Repositories;
import app.utils.graph.Edge;
import app.utils.graph.Graph;
import app.utils.graph.MaxFlow;

/**
 * Serviço que obtém o grafo do projeto e invoca MaxFlow.compute.
 * Camada intermediária entre a View e o algoritmo MaxFlow (padrão MVC).
 * Responsável por resolver vértices, detectar tipos de capacidade e criar operadores.
 */
public class MaxFlowService {

    /**
     * Tenta instanciar uma implementação de Graph do projeto.
     * Testa várias classes (MapGraph, MatrixGraph) e construtores.
     */
    @SuppressWarnings("unchecked")
    private static Graph<?, ?> tryInstantiateProjectGraph() {
        String[] candidates = {
                "app.utils.graph.map.MapGraph",
                "app.utils.graph.MapGraph",
                "app.utils.graph.matrix.MatrixGraph",
                "app.utils.graph.MatrixGraph"
        };

        for (String name : candidates) {
            try {
                Class<?> cls = Class.forName(name);
                Object inst = null;

                // Tenta construtor sem argumentos
                try {
                    Constructor<?> ctor = cls.getDeclaredConstructor();
                    ctor.setAccessible(true);
                    inst = ctor.newInstance();
                } catch (NoSuchMethodException ignored) {}

                // Tenta construtor com boolean (directed)
                if (inst == null) {
                    try {
                        Constructor<?> ctorBool = cls.getDeclaredConstructor(boolean.class);
                        ctorBool.setAccessible(true);
                        inst = ctorBool.newInstance(true);
                    } catch (NoSuchMethodException ignored) {}
                }

                // Tenta construtor (boolean, int)
                if (inst == null) {
                    try {
                        Constructor<?> ctorBI = cls.getDeclaredConstructor(boolean.class, int.class);
                        ctorBI.setAccessible(true);
                        inst = ctorBI.newInstance(true, 10);
                    } catch (NoSuchMethodException ignored) {}
                }

                // Tenta método estático getInstance()
                if (inst == null) {
                    try {
                        Method getInst = cls.getMethod("getInstance");
                        inst = getInst.invoke(null);
                    } catch (Exception ignored) {}
                }

                if (inst == null) continue;
                if (!(inst instanceof Graph)) continue;

                return (Graph<?, ?>) inst;
            } catch (ClassNotFoundException ignored) {
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    /**
     * Método principal: calcula fluxo máximo entre sourceKey e sinkKey.
     *
     * PASSOS:
     * 1. Resolve vértices por facilityId (se Station) ou chave do grafo
     * 2. Verifica se existe aresta direta (otimização O(1))
     * 3. Detecta tipo de capacidade (Integer, Double, EdgeData, etc.)
     * 4. Cria operadores apropriados (comparador, adição)
     * 5. Invoca MaxFlow.compute
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <V, E> MaxFlow.Result<V, E> compute(Graph<V, E> g, int sourceKey, int sinkKey) {
        if (g == null) throw new IllegalArgumentException("Graph cannot be null");

        // PASSO 1: Resolve source/sink (prefere facilityId quando vértices são Station)
        V source = null;
        V sink = null;
        V probe = g.numVertices() > 0 ? g.vertex(0) : null;
        if (probe instanceof Station) {
            try {
                source = g.vertex(v -> ((Station) v).getFacilityId() != null &&
                                       ((Station) v).getFacilityId().intValue() == sourceKey);
            } catch (Exception ignored) {}
            try {
                sink = g.vertex(v -> ((Station) v).getFacilityId() != null &&
                                     ((Station) v).getFacilityId().intValue() == sinkKey);
            } catch (Exception ignored) {}
        }

        // Fallback: usa chave numérica do vértice
        if (source == null) source = g.vertex(sourceKey);
        if (sink == null) sink = g.vertex(sinkKey);

        if (source == null || sink == null) {
            Integer sKey = g.validVertex(source) ? Integer.valueOf(g.key(source)) : null;
            Integer tKey = g.validVertex(sink) ? Integer.valueOf(g.key(sink)) : null;
            return new MaxFlow.Result<>(source, sink, sKey, tKey, null, "Invalid source or sink vertex.", "O(VE^2)");
        }

        // Determina chaves para resultado (prefere facilityId)
        Integer resultSourceKey;
        Integer resultSinkKey;
        try {
            resultSourceKey = (source instanceof Station && ((Station) source).getFacilityId() != null) ?
                ((Station) source).getFacilityId() : Integer.valueOf(g.key(source));
        } catch (Exception ignored) {
            resultSourceKey = Integer.valueOf(g.key(source));
        }
        try {
            resultSinkKey = (sink instanceof Station && ((Station) sink).getFacilityId() != null) ?
                ((Station) sink).getFacilityId() : Integer.valueOf(g.key(sink));
        } catch (Exception ignored) {
            resultSinkKey = Integer.valueOf(g.key(sink));
        }

        // PASSO 2: OTIMIZAÇÃO - se existe aresta direta, retorna capacidade imediatamente
        try {
            app.utils.graph.Edge<V, E> directEdge = null;
            try { directEdge = g.edge(source, sink); } catch (Exception ignored) { directEdge = null; }
            if (directEdge == null) {
                try { directEdge = g.edge(g.key(source), g.key(sink)); } catch (Exception ignored) { directEdge = null; }
            }
            if (directEdge != null && directEdge.getWeight() != null) {
                Object w = directEdge.getWeight();
                Object flowVal = null;
                if (w instanceof Number) {
                    flowVal = Integer.valueOf(((Number) w).intValue());
                } else if (w instanceof EdgeData) {
                    flowVal = Integer.valueOf(((EdgeData) w).getCapacity());
                }
                if (flowVal != null) {
                    @SuppressWarnings("unchecked") E eFlow = (E) flowVal;
                    return new MaxFlow.Result<>(source, sink, resultSourceKey, resultSinkKey, eFlow,
                            "Direct edge used - max flow is direct capacity.", "O(1)");
                }
            }
        } catch (Exception ignored) {}

        // PASSO 3: Detecta tipo de capacidade nas arestas
        Class<?> weightClass = null;
        Object zero = null;
        Comparator comp = null;
        BinaryOperator add = null;

        for (Edge<V, E> e : g.edges()) {
            E w = e.getWeight();
            if (w != null) {
                weightClass = w.getClass();

                // CASO ESPECIAL: EdgeData - cria view que mapeia EdgeData -> Integer (capacity)
                if (weightClass.getName().equals("app.model.EdgeData") || EdgeData.class.isAssignableFrom(weightClass)) {
                    @SuppressWarnings({"unchecked","rawtypes"})
                    Graph<V,Integer> capacityView = new Graph<V,Integer>() {
                        private final Graph<V,E> orig = (Graph) g;

                        public boolean isDirected() { return orig.isDirected(); }
                        public int numVertices() { return orig.numVertices(); }
                        public ArrayList<V> vertices() { return new ArrayList<>(orig.vertices()); }
                        public boolean validVertex(V vert) { return orig.validVertex(vert); }
                        public int key(V vert) { return orig.key(vert); }
                        public V vertex(int key) { return orig.vertex(key); }
                        public V vertex(java.util.function.Predicate<V> p) { return orig.vertex(p); }
                        public java.util.Collection<V> adjVertices(V vert) { return orig.adjVertices(vert); }
                        public int numEdges() { return orig.numEdges(); }

                        // Mapeia arestas: EdgeData -> Integer (capacity)
                        public Collection<Edge<V,Integer>> edges() {
                            ArrayList<Edge<V,Integer>> res = new ArrayList<>();
                            for (Edge<V,E> e2 : orig.edges()) {
                                Object w2 = e2.getWeight();
                                int cap = 0;
                                try { cap = ((EdgeData) w2).getCapacity(); } catch (Exception ignored) {}
                                res.add(new Edge<>(e2.getVOrig(), e2.getVDest(), Integer.valueOf(cap)));
                            }
                            return res;
                        }

                        public Edge<V,Integer> edge(V vOrig, V vDest) {
                            Edge<V,E> e0 = orig.edge(vOrig, vDest);
                            if (e0 == null) return null;
                            Object w2 = e0.getWeight();
                            int cap = 0;
                            try { cap = ((EdgeData) w2).getCapacity(); } catch (Exception ignored) {}
                            return new Edge<>(e0.getVOrig(), e0.getVDest(), Integer.valueOf(cap));
                        }

                        public Edge<V,Integer> edge(int vOrigKey, int vDestKey) {
                            V a = vertex(vOrigKey);
                            V b = vertex(vDestKey);
                            return edge(a,b);
                        }

                        public int outDegree(V vert) { return orig.outDegree(vert); }
                        public int inDegree(V vert) { return orig.inDegree(vert); }

                        public java.util.Collection<Edge<V,Integer>> outgoingEdges(V vert) {
                            Collection<Edge<V,E>> outs = orig.outgoingEdges(vert);
                            if (outs == null) return null;
                            ArrayList<Edge<V,Integer>> res = new ArrayList<>();
                            for (Edge<V,E> e2 : outs) {
                                Object w2 = e2.getWeight();
                                int cap = 0;
                                try { cap = ((EdgeData) w2).getCapacity(); } catch (Exception ignored) {}
                                res.add(new Edge<>(e2.getVOrig(), e2.getVDest(), Integer.valueOf(cap)));
                            }
                            return res;
                        }

                        public java.util.Collection<Edge<V,Integer>> incomingEdges(V vert) {
                            Collection<Edge<V,E>> ins = orig.incomingEdges(vert);
                            ArrayList<Edge<V,Integer>> res = new ArrayList<>();
                            for (Edge<V,E> e2 : ins) {
                                Object w2 = e2.getWeight();
                                int cap = 0;
                                try { cap = ((EdgeData) w2).getCapacity(); } catch (Exception ignored) {}
                                res.add(new Edge<>(e2.getVOrig(), e2.getVDest(), Integer.valueOf(cap)));
                            }
                            return res;
                        }

                        public boolean addVertex(V vert) { throw new UnsupportedOperationException("Read-only capacity view"); }
                        public boolean addEdge(V vOrig, V vDest, Integer weight) { throw new UnsupportedOperationException("Read-only capacity view"); }
                        public boolean removeVertex(V vert) { throw new UnsupportedOperationException("Read-only capacity view"); }
                        public boolean removeEdge(V vOrig, V vDest) { throw new UnsupportedOperationException("Read-only capacity view"); }
                        public Graph<V,Integer> clone() { throw new UnsupportedOperationException("Not supported"); }
                    };

                    // Chama compute recursivamente com view de capacidade
                    return (MaxFlow.Result<V, E>) compute((Graph) capacityView, sourceKey, sinkKey);
                }
                break;
            }
        }

        if (weightClass == null) {
            return new MaxFlow.Result<>(source, sink, resultSourceKey, resultSinkKey, null,
                    "Graph edges do not have capacities (weights are null). Provide numeric capacities.", "O(VE^2)");
        }

        // PASSO 4: Cria operadores baseado no tipo detectado
        if (Integer.class.isAssignableFrom(weightClass)) {
            zero = Integer.valueOf(0);
            comp = Comparator.naturalOrder();
            add = (a, b) -> Integer.valueOf(((Integer) a) + ((Integer) b));
        } else if (Double.class.isAssignableFrom(weightClass)) {
            zero = Double.valueOf(0.0);
            comp = Comparator.naturalOrder();
            add = (a, b) -> Double.valueOf(((Double) a) + ((Double) b));
        } else if (Long.class.isAssignableFrom(weightClass)) {
            zero = Long.valueOf(0L);
            comp = Comparator.naturalOrder();
            add = (a, b) -> Long.valueOf(((Long) a) + ((Long) b));
        } else if (Float.class.isAssignableFrom(weightClass)) {
            zero = Float.valueOf(0f);
            comp = Comparator.naturalOrder();
            add = (a, b) -> Float.valueOf(((Float) a) + ((Float) b));
        } else if (BigInteger.class.isAssignableFrom(weightClass)) {
            zero = BigInteger.ZERO;
            comp = (Comparator) Comparator.naturalOrder();
            add = (a, b) -> ((BigInteger) a).add((BigInteger) b);
        } else if (BigDecimal.class.isAssignableFrom(weightClass)) {
            zero = BigDecimal.ZERO;
            comp = (Comparator) Comparator.naturalOrder();
            add = (a, b) -> ((BigDecimal) a).add((BigDecimal) b);
        } else {
            zero = Integer.valueOf(0);
            comp = Comparator.naturalOrder();
            add = (a, b) -> Integer.valueOf(((Integer) a) + ((Integer) b));
        }

        // PASSO 5: Invoca MaxFlow.compute com operadores criados
        return (MaxFlow.Result<V, E>) MaxFlow.compute((Graph) g, source, sink, comp, add, zero);
    }

    /**
     * Calcula fluxo máximo usando grafo do projeto (via StationLoaderService ou instanciação).
     * Valida que grafo tem arestas e capacidades numéricas.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static MaxFlow.Result<?, ?> computeUsingProjectGraphIfAvailable(int sourceKey, int sinkKey) {
        Graph<?, ?> g = ensureProjectGraphPopulated();

        if (g == null) {
            return new MaxFlow.Result<>(null, null, sourceKey, sinkKey, null,
                    "Project graph not available - MaxFlow requires the project's graph (StationLoaderService.getGraph()).",
                    "O(VE^2)");
        }

        if (g.numEdges() == 0) {
            return new MaxFlow.Result<>(null, null, sourceKey, sinkKey, null,
                    "Project graph has no edges - MaxFlow requires capacity edges to compute.",
                    "O(VE^2)");
        }

        // Verifica se arestas têm capacidades numéricas
        boolean hasNumericCapacity = false;
        for (app.utils.graph.Edge<?, ?> e : g.edges()) {
            Object w = e.getWeight();
            if (w instanceof Number || w instanceof EdgeData) {
                hasNumericCapacity = true;
                break;
            }
        }

        if (!hasNumericCapacity) {
            return new MaxFlow.Result<>(null, null, sourceKey, sinkKey, null,
                    "Project graph edges do not have numeric capacities. Provide numeric weights (or EdgeData) on edges.",
                    "O(VE^2)");
        }

        try {
            return (MaxFlow.Result) compute((Graph) g, sourceKey, sinkKey);
        } catch (Exception ex) {
            String msg = "MaxFlow computation failed: " + ex.getClass().getSimpleName() + " - " + ex.getMessage();
            return new MaxFlow.Result<>(null, null, sourceKey, sinkKey, null, msg, "O(VE^2)");
        }
    }

    /**
     * Wrapper de conveniência para computar com grafo explícito.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static MaxFlow.Result<?, ?> computeUsingGraph(Graph<?, ?> g, int sourceKey, int sinkKey) {
        return (MaxFlow.Result) compute((Graph) g, sourceKey, sinkKey);
    }

    /**
     * Retorna lista de Stations no grafo (útil para enumerar IDs disponíveis).
     */
    public static List<Station> getProjectGraphStations() {
        Graph<?,?> g = ensureProjectGraphPopulated();
        if (g == null) return null;

        List<Station> res = new ArrayList<>();
        for (Object v : g.vertices()) {
            if (v instanceof Station) res.add((Station) v);
        }
        return res;
    }

    /**
     * Garante que grafo está populado com Stations.
     * Tenta StationLoaderService.getGraph() primeiro, depois instanciação + Repositories.
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    private static Graph<?,?> ensureProjectGraphPopulated() {
        // Tenta obter de StationLoaderService
        try {
            Graph<Station, ?> sg = app.service.sprint3.StationLoaderService.getGraph();
            if (sg != null) return sg;
        } catch (Throwable ignored) {}

        // Tenta instanciar e popular com Repositories
        Graph<?,?> g = tryInstantiateProjectGraph();
        if (g == null) return null;

        Repositories repos = Repositories.getInstance();
        try {
            List<app.model.Facility> facilities = repos.getFacilityRepository().getAllFacilities();
            for (app.model.Facility f : facilities) {
                if (f instanceof Station) {
                    Station s = (Station) f;
                    try {
                        Graph gRaw = (Graph) g;
                        Object existingObj = gRaw.vertex(v ->
                            (v instanceof Station) &&
                            ((Station) v).getFacilityId() != null &&
                            ((Station) v).getFacilityId().intValue() == s.getFacilityId()
                        );
                        Station existing = (existingObj instanceof Station) ? (Station) existingObj : null;
                        if (existing == null) gRaw.addVertex(s);
                    } catch (Exception ignored) {}
                }
            }
        } catch (Exception ignored) {}

        return g;
    }

    private static Double safeParseDouble(String value) {
        try {
            return value == null ? null : Double.parseDouble(value.trim());
        } catch (Exception e) {
            return null;
        }
    }
}

