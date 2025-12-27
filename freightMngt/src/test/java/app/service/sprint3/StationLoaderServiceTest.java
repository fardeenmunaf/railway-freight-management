package app.service.sprint3;

import app.model.Station;
import app.utils.graph.Graph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de testes para o serviço StationLoaderService.
 * Nota: StationLoaderService usa padrão Singleton. Os testes usam resetInstance() antes de cada teste.
 *
 * @author Fardeen
 * @version 1.0
 */
public class StationLoaderServiceTest {

    @BeforeEach
    void setUp() {
        StationLoaderService.resetInstance();
    }

    @Test
    void testServiceInitialization() throws IOException {
        StationLoaderService service = StationLoaderService.getInstance();

        assertNotNull(StationLoaderService.getGraph());
        assertNotNull(StationLoaderService.getStationMap());
        assertNotNull(service);
        assertTrue(service.getTotalStations() >= 0);
        assertTrue(service.getTotalConnections() >= 0);
    }

    @Test
    void testGetGraph() throws IOException {
        Graph<Station, Double> graph = StationLoaderService.getGraph();
        assertNotNull(graph);
        assertTrue(graph.numVertices() > 0);
    }

    @Test
    void testGetStationMap() throws IOException {
        Map<Integer, Station> stationMap = StationLoaderService.getStationMap();
        assertNotNull(stationMap);
        assertFalse(stationMap.isEmpty());
    }

    @Test
    void testStationExists() throws IOException {
        StationLoaderService service = StationLoaderService.getInstance();
        Map<Integer, Station> stationMap = StationLoaderService.getStationMap();

        if (!stationMap.isEmpty()) {
            Integer existingId = stationMap.keySet().iterator().next();
            assertTrue(service.stationExists(existingId));
        }

        assertFalse(service.stationExists(999999));
        assertFalse(service.stationExists(null));
    }

    @Test
    void testSingletonPattern() throws IOException {
        StationLoaderService instance1 = StationLoaderService.getInstance();
        StationLoaderService instance2 = StationLoaderService.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    void testResetInstance() throws IOException {
        StationLoaderService instance1 = StationLoaderService.getInstance();
        StationLoaderService.resetInstance();
        StationLoaderService instance2 = StationLoaderService.getInstance();

        assertNotNull(instance1);
        assertNotNull(instance2);
        assertNotSame(instance1, instance2);
    }
}

