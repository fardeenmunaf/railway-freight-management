package app.service.sprint2;

import app.model.FacilityType;
import app.model.Station;
import app.utils.Balanced2DTreeBuilder.Balanced2DTree;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RangeQueryServiceTest {

    private static RangeQueryService service;
    private static List<Station> stations;

    @BeforeAll
    static void setup() {
        // Criar tipos de facility
        FacilityType STATION = new FacilityType(2, "Station");
        FacilityType AIRPORT = new FacilityType(3, "Airport");

        // Criar lista de estações
        stations = new ArrayList<>();
        stations.add(new Station(1, "Lisboa Oriente", STATION,
                38.7680, -9.1010, true, "PT", "Europe/Lisbon", "WET",
                false, true, false));
        stations.add(new Station(2, "Lisboa Santa Apolónia", STATION,
                38.7138, -9.1217, true, "PT", "Europe/Lisbon", "WET",
                false, false, false));
        stations.add(new Station(3, "Porto Campanhã", STATION,
                41.1473, -8.5850, true, "PT", "Europe/Lisbon", "WET",
                false, true, false));
        stations.add(new Station(4, "Madrid Atocha", STATION,
                40.4066, -3.6890, true, "ES", "Europe/Madrid", "CET",
                false, true, false));
        stations.add(new Station(5, "Faro", STATION,
                37.0194, -7.9322, false, "PT", "Europe/Lisbon", "WET",
                false, false, false));
        stations.add(new Station(6, "Porto Airport", AIRPORT,
                41.2356, -8.6781, true, "PT", "Europe/Lisbon", "WET",
                false, false, true));

        // Construir a árvore 2D
        Balanced2DTree tree = new Balanced2DTree();
        tree.bulkBuild(stations);

        // Inicializar service
        service = new RangeQueryService(tree);
    }

    @Test
    void testRectangularQuery() {
        // Queremos apenas estações dentro de um retângulo
        List<Station> result = service.rangeQuery(38.7, 38.8, -9.2, -9.0,
                null, null, null);

        assertEquals(2, result.size());
        for (Station s : result) {
            assertTrue(s.getLat() >= 38.7 && s.getLat() <= 38.8);
            assertTrue(s.getLon() >= -9.2 && s.getLon() <= -9.0);
        }
    }

    @Test
    void testCountryFilter() {
        List<Station> result = service.rangeQuery(36.0, 43.0, -10.0, -6.0,
                null, null, "PT");

        // Apenas estações PT
        for (Station s : result) {
            assertEquals("PT", s.getCountry());
        }
    }

    @Test
    void testMainStationFilter() {
        List<Station> result = service.rangeQuery(36.0, 43.0, -10.0, -6.0,
                null, true, "PT");

        // Apenas main stations
        for (Station s : result) {
            assertTrue(s.getIsMainStation());
        }
    }

    @Test
    void testPerformanceVsFullScan() {
        // Medir tempo do range query
        long startKD = System.nanoTime();
        List<Station> kdResult = service.rangeQuery(36.0, 43.0, -10.0, -6.0,
                null, null, null);
        long endKD = System.nanoTime();

        // Medir tempo de full scan (simulação)
        long startFull = System.nanoTime();
        List<Station> fullScan = new ArrayList<>();
        for (Station s : stations) {
            if (s.getLat() >= 36.0 && s.getLat() <= 43.0 &&
                    s.getLon() >= -10.0 && s.getLon() <= -6.0) {
                fullScan.add(s);
            }
        }
        long endFull = System.nanoTime();

        // Resultados devem ser iguais
        assertEquals(fullScan.size(), kdResult.size());

        // KD-tree deve ser mais rápida (em média)
        long kdTime = endKD - startKD;
        long fullTime = endFull - startFull;

        System.out.printf("Range query KD-tree: %d ns, full scan: %d ns%n", kdTime, fullTime);

        assertTrue(kdTime <= fullTime * 5); // aceitável em micro-dataset
    }
}
