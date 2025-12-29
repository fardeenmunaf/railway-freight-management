package app.service;

import app.utils.graph.Graph;
import app.utils.graph.MaxFlow;
import app.utils.graph.Edge;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.Comparator;
import java.util.function.BinaryOperator;

/**
 * Service responsible for obtaining/constructing a Graph and invoking MaxFlow.compute.
 * Keeps algorithm logic outside views.
 */
public class MaxFlowService {

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
                // try no-arg constructor
                try {
                    Constructor<?> ctor = cls.getDeclaredConstructor();
                    ctor.setAccessible(true);
                    inst = ctor.newInstance();
                } catch (NoSuchMethodException ignored) {
                }

                // try single-boolean constructor (directed)
                if (inst == null) {
                    try {
                        Constructor<?> ctorBool = cls.getDeclaredConstructor(boolean.class);
                        ctorBool.setAccessible(true);
                        inst = ctorBool.newInstance(true); // directed graph
                    } catch (NoSuchMethodException ignored) {
                    }
                }

                // try (boolean,int) constructor (MatrixGraph has this overload)
                if (inst == null) {
                    try {
                        Constructor<?> ctorBI = cls.getDeclaredConstructor(boolean.class, int.class);
                        ctorBI.setAccessible(true);
                        inst = ctorBI.newInstance(true, 10);
                    } catch (NoSuchMethodException ignored) {
                    }
                }

                // try static getInstance()
                if (inst == null) {
                    try {
                        Method getInst = cls.getMethod("getInstance");
                        inst = getInst.invoke(null);
                    } catch (Exception ignored) {
                    }
                }

                if (inst == null) continue;
                if (!(inst instanceof Graph)) continue;

                return (Graph<?, ?>) inst;
            } catch (ClassNotFoundException ignored) {
                // try next
            } catch (Exception ignored) {
                // any other issue, continue
            }
        }
        return null;
    }

    private static Graph<Integer, Integer> createSampleIntegerGraph() {
        final List<Integer> verts = Arrays.asList(0,1,2,3);
        final List<Edge<Integer,Integer>> edges = new ArrayList<>();

        // use concrete Edge objects from app.utils.graph.Edge
        edges.add(new Edge<>(0,1,3));
        edges.add(new Edge<>(0,2,2));
        edges.add(new Edge<>(1,2,5));
        edges.add(new Edge<>(1,3,2));
        edges.add(new Edge<>(2,3,3));

        return new Graph<Integer,Integer>() {
            public boolean isDirected(){ return true; }
            public boolean validVertex(Integer v){ return v!=null && verts.contains(v); }
            public int numVertices(){ return verts.size(); }
            public ArrayList<Integer> vertices(){ return new ArrayList<>(verts); }
            public Collection<Edge<Integer,Integer>> edges(){ return edges; }
            public int key(Integer v){ return verts.indexOf(v); }
            public Integer vertex(int key){ return (key>=0 && key<verts.size()) ? Integer.valueOf(key) : null; }
            public Integer vertex(java.util.function.Predicate<Integer> p) {
                for (Integer v : verts) if (p.test(v)) return v;
                return null;
            }
            public java.util.Collection<Integer> adjVertices(Integer vert){ return null; }
            public int numEdges(){ return edges.size(); }
            public app.utils.graph.Edge<Integer,Integer> edge(Integer vOrig, Integer vDest){ return null; }
            public app.utils.graph.Edge<Integer,Integer> edge(int vOrigKey, int vDestKey){ return null; }
            public int outDegree(Integer vert){ return -1; }
            public int inDegree(Integer vert){ return -1; }
            public java.util.Collection<app.utils.graph.Edge<Integer,Integer>> outgoingEdges(Integer vert){ return null; }
            public java.util.Collection<app.utils.graph.Edge<Integer,Integer>> incomingEdges(Integer vert){ return null; }
            public boolean addVertex(Integer vert){ return false; }
            public boolean addEdge(Integer vOrig, Integer vDest, Integer weight){ return false; }
            public boolean removeVertex(Integer vert){ return false; }
            public boolean removeEdge(Integer vOrig, Integer vDest){ return false; }
            public Graph<Integer,Integer> clone(){ return this; }
        };
    }

    // Generic compute: detect edge weight type and call MaxFlow.compute with appropriate functions
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <V, E> MaxFlow.Result<V, E> compute(Graph<V, E> g, int sourceKey, int sinkKey) {
        if (g == null) throw new IllegalArgumentException("Graph cannot be null");

        V source = g.vertex(sourceKey);
        V sink = g.vertex(sinkKey);

        Class<?> weightClass = null;
        Object zero = null;
        Comparator comp = null;
        BinaryOperator add = null;

        for (Edge<V, E> e : g.edges()) {
            E w = e.getWeight();
            if (w != null) {
                weightClass = w.getClass();
                break;
            }
        }

        if (weightClass == null) {
            // No capacities found on edges: cannot compute meaningful max flow
            return new MaxFlow.Result<>(source, sink, sourceKey, sinkKey, null,
                    "Graph edges do not have capacities (weights are null). Provide numeric capacities.", "O(V·E^2)");
        }

        if (Integer.class.isAssignableFrom(weightClass)) {
            zero = Integer.valueOf(0);
            comp = Comparator.naturalOrder();
            add = (a, b) -> Integer.valueOf(((Integer)a) + ((Integer)b));
        } else if (Double.class.isAssignableFrom(weightClass)) {
            zero = Double.valueOf(0.0);
            comp = Comparator.naturalOrder();
            add = (a, b) -> Double.valueOf(((Double)a) + ((Double)b));
        } else if (Long.class.isAssignableFrom(weightClass)) {
            zero = Long.valueOf(0L);
            comp = Comparator.naturalOrder();
            add = (a, b) -> Long.valueOf(((Long)a) + ((Long)b));
        } else if (Float.class.isAssignableFrom(weightClass)) {
            zero = Float.valueOf(0f);
            comp = Comparator.naturalOrder();
            add = (a, b) -> Float.valueOf(((Float)a) + ((Float)b));
        } else if (BigInteger.class.isAssignableFrom(weightClass)) {
            zero = BigInteger.ZERO;
            comp = (Comparator) Comparator.naturalOrder();
            add = (a, b) -> ((BigInteger)a).add((BigInteger)b);
        } else if (BigDecimal.class.isAssignableFrom(weightClass)) {
            zero = BigDecimal.ZERO;
            comp = (Comparator) Comparator.naturalOrder();
            add = (a, b) -> ((BigDecimal)a).add((BigDecimal)b);
        } else {
            // fallback to Integer
            zero = Integer.valueOf(0);
            comp = Comparator.naturalOrder();
            add = (a, b) -> Integer.valueOf(((Integer)a) + ((Integer)b));
        }

        return (MaxFlow.Result<V, E>) MaxFlow.compute((Graph) g, sourceKey, sinkKey, comp, add, zero);
    }

    // Existing convenience methods
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static MaxFlow.Result<?,?> computeUsingProjectGraphIfAvailable(int sourceKey, int sinkKey) {
        Graph<?,?> g = tryInstantiateProjectGraph();
        if (g == null) g = createSampleIntegerGraph();
        return (MaxFlow.Result) compute((Graph) g, sourceKey, sinkKey);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static MaxFlow.Result<?,?> computeUsingGraph(Graph<?,?> g, int sourceKey, int sinkKey) {
        return (MaxFlow.Result) compute((Graph) g, sourceKey, sinkKey);
    }
}
