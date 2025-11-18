package app.service.sprint2;

import app.model.FacilityType;
import app.model.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RangeQueryServiceTest {

    private RangeQueryService service;

    @BeforeEach
    void setup() {
        // Criar algumas estações de teste
        Station s1 = new Station(1, "Lisboa", new FacilityType(2, "Station"), 38.7169, -9.139, true, "PT", "Europe/Lisbon", null, true, true, false);
        Station s2 = new Station(2, "Porto", new FacilityType(2, "Station"), 41.1496, -8.611, true, "PT", "Europe/Lisbon", null, true, true, false);
        Station s3 = new Station(3, "Coimbra", new FacilityType(2, "Station"), 40.2056, -8.419, true, "PT", "Europe/Lisbon", null, true, false, false);
        Station s4 = new Station(4, "Madrid", new FacilityType(2, "Station"), 40.4165, -3.7026, true, "ES", "Europe/Madrid", null, true, true, false);
        Station s5 = new Station(5, "Barcelona", new FacilityType(2, "Station"), 41.3851, 2.1734, true, "ES", "Europe/Madrid", null, true, false, false);

        // Construir árvore KD
        service = new RangeQueryService();
        service.buildIndex(List.of(s1, s2, s3, s4, s5));
    }

    @Test
    void testRectangularQueryBounds() {
        // lat ∈ [40,42], lon ∈ [-9,-6] → Porto e Coimbra
        List<Station> results = service.rangeQuery(40, 42, -9, -6, null, null, null);
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(s -> s.getName().equals("Porto")));
        assertTrue(results.stream().anyMatch(s -> s.getName().equals("Coimbra")));
    }

    @Test
    void testCountryFilter() {
        List<Station> results = service.rangeQuery(-90, 90, -180, 180, null, null, "PT");
        assertEquals(3, results.size());
        assertTrue(results.stream().allMatch(s -> s.getCountry().equalsIgnoreCase("PT")));
    }

    @Test
    void testIsMainStationFilter() {
        List<Station> results = service.rangeQuery(-90, 90, -180, 180, null, true, null);
        assertEquals(3, results.size());
        assertTrue(results.stream().allMatch(Station::getIsMainStation));
    }

    @Test
    void testPerformanceVsFullScan() {
        // Query com KD-tree
        long startKD = System.nanoTime();
        List<Station> kdResults = service.rangeQuery(40, 42, -9, -6, null, null, null);
        long endKD = System.nanoTime();

        // Full scan: percorre toda a lista original
        List<Station> allStations = List.of(
                new Station(1, "Lisboa", null, 38.7169, -9.139, true, "PT", null, null, true, true, false),
                new Station(2, "Porto", null, 41.1496, -8.611, true, "PT", null, null, true, true, false),
                new Station(3, "Coimbra", null, 40.2056, -8.419, true, "PT", null, null, true, false, false),
                new Station(4, "Madrid", null, 40.4165, -3.7026, true, "ES", null, null, true, true, false),
                new Station(5, "Barcelona", null, 41.3851, 2.1734, true, "ES", null, null, true, false, false)
        );

        long startScan = System.nanoTime();
        List<Station> scanResults = allStations.stream()
                .filter(s -> s.getLat() >= 40 && s.getLat() <= 42)
                .filter(s -> s.getLon() >= -9 && s.getLon() <= -6)
                .toList();
        long endScan = System.nanoTime();

        System.out.println("KD query time: " + (endKD - startKD) + " ns");
        System.out.println("Full scan time: " + (endScan - startScan) + " ns");

        assertEquals(2, kdResults.size());
        assertEquals(scanResults.size(), kdResults.size());
        assertTrue((endKD - startKD) < (endScan - startScan) * 10_000, "KD query should be much faster");
    }

}
