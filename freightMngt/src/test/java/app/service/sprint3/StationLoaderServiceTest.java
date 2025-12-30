package app.service.sprint3;

import app.model.EdgeData;
import app.model.Station;
import app.utils.graph.Graph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
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
        Graph<Station, EdgeData> graph = StationLoaderService.getGraph();
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

    @Test
    void testGetFormattedStationList() throws IOException {
        StationLoaderService service = StationLoaderService.getInstance();
        var formattedList = service.getFormattedStationList();

        assertNotNull(formattedList, "Formatted station list should not be null");
        assertFalse(formattedList.isEmpty(), "Formatted station list should not be empty");

        // Verify format: "ID: 123 | Station Name"
        String firstEntry = formattedList.get(0);
        assertTrue(firstEntry.contains("ID:"), "Entry should contain 'ID:'");
        assertTrue(firstEntry.contains("|"), "Entry should contain '|' separator");
    }

    @Test
    void testGetTotalStations() throws IOException {
        StationLoaderService service = StationLoaderService.getInstance();
        int totalStations = service.getTotalStations();

        assertTrue(totalStations > 0, "Total stations should be greater than 0");

        // Verify it matches the station map size
        Map<Integer, Station> stationMap = StationLoaderService.getStationMap();
        assertEquals(stationMap.size(), totalStations,
                    "Total stations should match station map size");
    }

    @Test
    void testGetTotalConnections() throws IOException {
        StationLoaderService service = StationLoaderService.getInstance();
        int totalConnections = service.getTotalConnections();

        assertTrue(totalConnections > 0, "Total connections should be greater than 0");

        // Verify it matches the graph edges
        Graph<Station, EdgeData> graph = StationLoaderService.getGraph();
        assertEquals(graph.numEdges(), totalConnections,
                    "Total connections should match graph edges");
    }
}
