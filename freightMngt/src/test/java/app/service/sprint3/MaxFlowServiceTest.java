package app.service.sprint3;

import app.model.FacilityType;
import app.model.Station;
import app.repositories.Repositories;
import app.utils.graph.map.MapGraph;
import app.utils.graph.MaxFlow;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MaxFlowServiceTest {

    @Before
    public void setUp() {
        // ensure repository has FacilityType Station (similar to service expectations)
        Repositories repos = Repositories.getInstance();
        FacilityType stationType = repos.getFacilityTypeRepository().getFacilityTypeByName("Station");
        if (stationType == null) {
            repos.getFacilityTypeRepository().addFacilityType(new FacilityType(2, "Station"));
        }
    }

    @Test
    public void testComputeUsingGraph_withMapGraphIntegers() {
        MapGraph<Integer, Integer> g = new MapGraph<>(true);

        // create vertices (integers as both key and vertex)
        g.addVertex(1);
        g.addVertex(2);
        g.addVertex(3);

        g.addEdge(1, 2, 5);
        g.addEdge(2, 3, 3);

        MaxFlow.Result<?, ?> res = app.service.sprint3.MaxFlowService.computeUsingGraph(g, 0, 2);
        // path 1->2->3 has bottleneck 3
        assertNotNull(res);
        // res.maxFlow is boxed as Object (Integer expected)
        assertEquals(Integer.valueOf(3), (Integer) res.maxFlow);
    }

    @Test
    public void testCompute_resolvesStationFacilityId() {
        MapGraph<Station, Integer> g = new MapGraph<>(true);

        Repositories repos = Repositories.getInstance();
        FacilityType stType = repos.getFacilityTypeRepository().getFacilityTypeByName("Station");
        if (stType == null) stType = new FacilityType(2, "Station");

        Station s1 = new Station(1000, "S1", stType, 0.0, 0.0, false, null, null, null, null, null, null);
        Station s2 = new Station(2000, "S2", stType, 0.0, 0.0, false, null, null, null, null, null, null);
        Station s3 = new Station(3000, "S3", stType, 0.0, 0.0, false, null, null, null, null, null, null);

        g.addVertex(s1);
        g.addVertex(s2);
        g.addVertex(s3);

        g.addEdge(s1, s2, 5);
        g.addEdge(s2, s3, 3);

        // use facilityId values to resolve vertices
        MaxFlow.Result<?, ?> result = app.service.sprint3.MaxFlowService.computeUsingGraph(g, 1000, 3000);
        assertNotNull(result);
        assertNotNull(result.source);
        assertNotNull(result.sink);
        // cast source/sink to Station for assertions
        Station src = (Station) result.source;
        Station dst = (Station) result.sink;
        assertEquals((Integer)1000, src.getFacilityId());
        assertEquals((Integer)3000, dst.getFacilityId());
        // expected max flow is bottleneck 3
        assertEquals(Integer.valueOf(3), (Integer) result.maxFlow);
    }
}

