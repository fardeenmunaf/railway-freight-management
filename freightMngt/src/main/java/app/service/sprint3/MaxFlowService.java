package app.service.sprint3;

import app.utils.graph.Graph;
import app.utils.graph.MaxFlow;
import app.utils.graph.Edge;
import app.model.Station;
import app.model.FacilityType;
import app.repositories.Repositories;
import app.utils.CsvReader;
import app.utils.graph.map.MapGraph;

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

    private static final String BELGIAN_STATIONS_CSV = "src/main/java/app/sampleDataset/stations.csv";
    private static final String BELGIAN_LINES_CSV    = "src/main/java/app/sampleDataset/lines.csv";

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

        // Try to resolve source/sink as graph keys first
        V source = g.vertex(sourceKey);
        V sink = g.vertex(sinkKey);

        // If not found, and vertices are Station objects, try to resolve by Station.facilityId
        if (source == null || sink == null) {
            V probe = g.numVertices() > 0 ? g.vertex(0) : null;
            if (probe instanceof Station) {
                if (source == null) {
                    // find station with facilityId == sourceKey
                    V found = g.vertex(v -> ((Station) v).getFacilityId() != null && ((Station) v).getFacilityId().intValue() == sourceKey);
                    if (found != null) source = found;
                }
                if (sink == null) {
                    V found = g.vertex(v -> ((Station) v).getFacilityId() != null && ((Station) v).getFacilityId().intValue() == sinkKey);
                    if (found != null) sink = found;
                }
            }
        }

        if (source == null || sink == null) {
            // preserve previous behavior: create Result indicating invalid vertices
            Integer sKey = g.validVertex(source) ? Integer.valueOf(g.key(source)) : null;
            Integer tKey = g.validVertex(sink) ? Integer.valueOf(g.key(sink)) : null;
            return new MaxFlow.Result<>(source, sink, sKey, tKey, null, "Invalid source or sink vertex.", "O(V·E^2)");
        }

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
            return new MaxFlow.Result<>(source, sink, g.key(source), g.key(sink), null,
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
            add = (a, b) -> ((BigInteger)a).add((BigInteger) b);
        } else if (BigDecimal.class.isAssignableFrom(weightClass)) {
            zero = BigDecimal.ZERO;
            comp = (Comparator) Comparator.naturalOrder();
            add = (a, b) -> ((BigDecimal)a).add((BigDecimal) b);
        } else {
            // fallback to Integer
            zero = Integer.valueOf(0);
            comp = Comparator.naturalOrder();
            add = (a, b) -> Integer.valueOf(((Integer)a) + ((Integer)b));
        }

        // Use vertex-based compute now that 'source' and 'sink' have been resolved
        return (MaxFlow.Result<V, E>) MaxFlow.compute((Graph) g, source, sink, comp, add, zero);
    }

    // Existing convenience methods
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static MaxFlow.Result<?,?> computeUsingProjectGraphIfAvailable(int sourceKey, int sinkKey) {
        Graph<?,?> g = tryInstantiateProjectGraph();
        // If no concrete project graph was found, or it seems empty, try to build from Repositories first
        if (g == null || g.numVertices() == 0 || g.numEdges() == 0) {
            // try to build graph from already-initialized Stations in Repositories
            MapGraph<Station, Integer> repoGraph = buildStationGraphFromRepositories();
            if (repoGraph != null && repoGraph.numVertices() > 0 && repoGraph.numEdges() > 0) {
                g = repoGraph;
            } else {
                // fallback to CSV-based builder (legacy behavior)
                g = buildStationCapacityGraphFromSample();
            }
        }
        return (MaxFlow.Result) compute((Graph) g, sourceKey, sinkKey);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static MaxFlow.Result<?,?> computeUsingGraph(Graph<?,?> g, int sourceKey, int sinkKey) {
        return (MaxFlow.Result) compute((Graph) g, sourceKey, sinkKey);
    }

    // Builds a MapGraph<Station,Integer> from sampleDataset CSVs using the "capacity" column as integer capacities.
    public static MapGraph<Station, Integer> buildStationCapacityGraphFromSample() {
        MapGraph<Station, Integer> g = new MapGraph<>(true);

        Repositories repos = Repositories.getInstance();
        FacilityType stationType = repos.getFacilityTypeRepository().getFacilityTypeByName("Station");
        if (stationType == null) {
            stationType = new FacilityType(2, "Station");
            repos.getFacilityTypeRepository().addFacilityType(stationType);
        }

        List<Map<String, String>> stationRows = CsvReader.readCsv(BELGIAN_STATIONS_CSV, ",");
        Map<Integer, Station> byId = new HashMap<>();
        for (Map<String, String> row : stationRows) {
            try {
                int id = Integer.parseInt(row.get("Station id").trim());
                Station s = new Station(
                        id, row.get("Station"), stationType,
                        safeParseDouble(row.get("Lat")), safeParseDouble(row.get("Lon")),
                        false, null, null, null, null, null, null
                );
                repos.getFacilityRepository().addFacility(s);
                byId.put(id, s);
                g.addVertex(s);
            } catch (Exception ignored) {}
        }

        List<Map<String, String>> lineRows = CsvReader.readCsv(BELGIAN_LINES_CSV, ",");
        for (Map<String, String> row : lineRows) {
            try {
                int orig = Integer.parseInt(row.get("departure_stid").trim());
                int dest = Integer.parseInt(row.get("arrival_stid").trim());
                Integer cap = null;
                try {
                    String capStr = row.get("capacity");
                    if (capStr != null) cap = Integer.valueOf((int) Double.parseDouble(capStr));
                } catch (Exception ignored) { cap = null; }

                Station sOrig = byId.get(orig);
                Station sDest = byId.get(dest);
                if (sOrig != null && sDest != null && cap != null) {
                    g.addEdge(sOrig, sDest, cap);
                }
            } catch (Exception ignored) {}
        }

        return g;
    }

    // Build a MapGraph from Stations already loaded in Repositories, and wire edges using lines CSV if available
    public static MapGraph<Station, Integer> buildStationGraphFromRepositories() {
        Repositories repos = Repositories.getInstance();
        MapGraph<Station, Integer> g = new MapGraph<>(true);

        // collect stations from repositories
        Map<Integer, Station> byId = new HashMap<>();
        try {
            List<app.model.Facility> facilities = repos.getFacilityRepository().getAllFacilities();
            for (app.model.Facility f : facilities) {
                if (f instanceof Station) {
                    Station s = (Station) f;
                    byId.put(s.getFacilityId(), s);
                    g.addVertex(s);
                }
            }
        } catch (Exception ignored) {
        }

        if (byId.isEmpty()) return g;

        // attempt to wire edges using lines CSV (if present)
        List<Map<String, String>> lineRows = CsvReader.readCsv(BELGIAN_LINES_CSV, ",");
        for (Map<String, String> row : lineRows) {
            try {
                String origS = row.get("departure_stid");
                String destS = row.get("arrival_stid");
                if (origS == null || destS == null) continue;
                int orig = Integer.parseInt(origS.trim());
                int dest = Integer.parseInt(destS.trim());
                Integer cap = null;
                try {
                    String capStr = row.get("capacity");
                    if (capStr != null) cap = Integer.valueOf((int) Double.parseDouble(capStr));
                } catch (Exception ignored) { cap = null; }

                Station sOrig = byId.get(orig);
                Station sDest = byId.get(dest);
                if (sOrig != null && sDest != null && cap != null) {
                    g.addEdge(sOrig, sDest, cap);
                }
            } catch (Exception ignored) {}
        }

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
