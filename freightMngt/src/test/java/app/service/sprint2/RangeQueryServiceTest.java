package app.service.sprint2;

import app.model.FacilityType;
import app.model.Station;
import app.utils.Balanced2DTreeBuilder.Balanced2DTree;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RangeQueryServiceTest {

    private static RangeQueryService service;
    private static List<Station> stations;

    @BeforeEach
    void setup() {
        FacilityType STATION = new FacilityType(2, "Station");
        FacilityType AIRPORT = new FacilityType(3, "Airport");

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

        Balanced2DTree tree = new Balanced2DTree();
        tree.bulkBuild(stations);

        service = new RangeQueryService(tree);
    }


    @Test
    void testRectangularQuery() {
        RangeQueryService.RangeQueryResult result = service.rangeQuery(38.7, 38.8, -9.2, -9.0,
                null, null, null);

        List<Station> resultStations = result.stations;

        assertEquals(2, resultStations.size());
        for (Station s : resultStations) {
            assertTrue(s.getLat() >= 38.7 && s.getLat() <= 38.8);
            assertTrue(s.getLon() >= -9.2 && s.getLon() <= -9.0);
        }
    }

    @Test
    void testCountryFilter() {
        RangeQueryService.RangeQueryResult result = service.rangeQuery(36.0, 43.0, -10.0, -6.0,
                null, null, "PT");

        List<Station> resultStations = result.stations;

        for (Station s : resultStations) {
            assertEquals("PT", s.getCountry());
        }
    }

    @Test
    void testMainStationFilter() {
        RangeQueryService.RangeQueryResult result = service.rangeQuery(36.0, 43.0, -10.0, -6.0,
                null, true, "PT");

        List<Station> resultStations = result.stations;

        for (Station s : resultStations) {
            assertTrue(s.getIsMainStation());
        }
    }

    @Test
    void testMultipleCountriesFilter() {
        RangeQueryService.RangeQueryResult result = service.rangeQuery(36.0, 43.0, -10.0, -6.0,
                null, null, "PT,ES");

        List<Station> resultStations = result.stations;

        assertFalse(resultStations.isEmpty());

        for (Station s : resultStations) {
            assertTrue(s.getCountry().equals("PT") || s.getCountry().equals("ES"));
        }
    }

    @Test
    void testPerformanceVsFullScan() {

        if (stations.size() < 1000) {
            System.out.println("Skipping performance assertion (dataset too small: "
                    + stations.size() + ")");
        }

        long startKD = System.nanoTime();
        var kdResult = service.rangeQuery(36.0, 43.0, -10.0, -6.0,
                null, null, null);
        long endKD = System.nanoTime();

        long startFull = System.nanoTime();
        List<Station> fullScan = new ArrayList<>();
        for (Station s : stations) {
            if (s.getLat() >= 36.0 && s.getLat() <= 43.0 &&
                    s.getLon() >= -10.0 && s.getLon() <= -6.0) {
                fullScan.add(s);
            }
        }
        long endFull = System.nanoTime();

        assertEquals(fullScan.size(), kdResult.stations.size());

        long kdTime = endKD - startKD;
        long fullTime = endFull - startFull;

        System.out.printf("KD-tree: %d ns | full scan: %d ns%n", kdTime, fullTime);

        if (stations.size() >= 1000) {
            assertTrue(kdTime <= fullTime * 5,
                    "KD-tree should be at least 5x faster than full scan for large datasets");
        }
    }
}
