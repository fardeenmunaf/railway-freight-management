package app.service;

import app.controller.StationController;
import app.model.FacilityType;
import app.model.Station;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class StationServiceTest {

    private StationService service;
    private StationController controller;
    private FacilityType facilityType;

    @Before
    public void setUp() {
        Repositories.getInstance().getFacilityRepository().clear();

        controller = new StationController();
        service = new StationService();
        facilityType = new FacilityType(1, "Railway Station");

        // Criar estações de teste
        controller.createStation(new Station(
                100, "Lisbon Santa Apolónia", facilityType,
                38.7126, -9.1281, false,
                "PT", "Europe/Lisbon", "WET/GMT",
                false, true, false
        ));

        controller.createStation(new Station(
                101, "Porto Campanhã", facilityType,
                41.1496, -8.5850, false,
                "PT", "Europe/Lisbon", "WET/GMT",
                false, true, false
        ));

        controller.createStation(new Station(
                102, "Madrid Atocha", facilityType,
                40.4064, -3.6910, false,
                "ES", "Europe/Madrid", "CET",
                false, true, false
        ));

        controller.createStation(new Station(
                103, "Paris Gare du Nord", facilityType,
                48.8808, 2.3550, false,
                "FR", "Europe/Paris", "CET",
                false, true, false
        ));

        service.buildIndexes();
    }

    @Test
    public void testSearchByName() {
        List<Station> results = service.searchByName("Lisbon Santa Apolónia");

        assertFalse("Should find station by name", results.isEmpty());
        assertEquals("lisbon santa apolónia", results.get(0).getName().toLowerCase());
    }

    @Test
    public void testSearchByNameCaseInsensitive() {
        List<Station> results = service.searchByName("LISBON SANTA APOLÓNIA");

        assertFalse("Should find station regardless of case", results.isEmpty());
    }

    @Test
    public void testSearchByCountry() {
        List<Station> results = service.searchByCountry("PT");

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(s -> "PT".equalsIgnoreCase(s.getCountry())));
    }

    @Test
    public void testSearchByTimeZone() {
        List<Station> results = service.searchByTimeZone("Europe/Lisbon");

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(s -> "europe/lisbon".equals(s.getTimeZone().toLowerCase())));
    }

    @Test
    public void testSearchByTimeZoneGroupOrderedByCountry() {
        List<Station> results = service.searchByTimeZoneGroupOrderedByCountry("CET");

        assertEquals(2, results.size());

        // Validar ordenação por país (ES < FR)
        assertEquals("ES", results.get(0).getCountry());
        assertEquals("FR", results.get(1).getCountry());

        // Validar que todas pertencem ao grupo CET
        assertTrue(results.stream().allMatch(s -> "CET".equals(s.getTimeZoneGroup())));
    }

    @Test
    public void testSearchByTimeZoneGroupRange() {
        List<Station> results = service.searchByTimeZoneGroupRange("CET", "WET/GMT");

        assertEquals(4, results.size());
    }

    @Test
    public void testSearchByCoordinates() {
        List<Station> results = service.searchByCoordinates(38.7126, -9.1281);

        assertFalse("Should find station at coordinates", results.isEmpty());
        assertEquals(38.7126, results.get(0).getLat(), 0.0001);
        assertEquals(-9.1281, results.get(0).getLon(), 0.0001);
    }

    @Test
    public void testSearchByLatitudeRange() {
        List<Station> results = service.searchByLatitudeRange(38.0, 42.0);

        assertEquals(3, results.size());
    }

    @Test
    public void testGetMainStations() {
        List<Station> results = service.getMainStations();

        assertEquals(4, results.size());
        assertTrue(results.stream().allMatch(s -> Boolean.TRUE.equals(s.getIsMainStation())));
    }

    @Test
    public void testGetStationCount() {
        int count = service.getStationCount();

        assertEquals(4, count);
    }

    @Test
    public void testSearchByNameWithNull() {
        List<Station> results = service.searchByName(null);

        assertTrue("Should return empty list for null input", results.isEmpty());
    }

    @Test
    public void testSearchByNameWithBlank() {
        List<Station> results = service.searchByName("   ");

        assertTrue("Should return empty list for blank input", results.isEmpty());
    }
}
