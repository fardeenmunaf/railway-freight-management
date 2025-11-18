package app.controller;

import app.model.FacilityType;
import app.model.Station;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class StationControllerTest {

    private StationController controller;
    private FacilityType facilityType;
    private Station station1;
    private Station station2;
    private Station station3;

    @Before
    public void setUp() {
        Repositories.getInstance().getFacilityRepository().clear();

        controller = new StationController();
        facilityType = new FacilityType(1, "Railway Station");

        station1 = new Station(
                100, "Lisbon Santa Apolónia", facilityType,
                38.7126, -9.1281, false,
                "PT", "Europe/Lisbon", "WET/GMT",
                false, true, false
        );

        station2 = new Station(
                101, "Porto Campanhã", facilityType,
                41.1496, -8.5850, false,
                "PT", "Europe/Lisbon", "WET/GMT",
                false, true, false
        );

        station3 = new Station(
                102, "Lisbon Airport", facilityType,
                38.7756, -9.1354, false,
                "PT", "Europe/Lisbon", "WET/GMT",
                false, false, true
        );
    }

    @Test
    public void testCreateStation() {
        Station created = controller.createStation(station1);

        assertNotNull(created);
        assertEquals(station1, created);
    }

    @Test
    public void testCreateStationWithNull() {
        Station created = controller.createStation(null);

        assertNull(created);
    }

    @Test
    public void testGetAllStations() {
        controller.createStation(station1);
        controller.createStation(station2);
        controller.createStation(station3);

        List<Station> stations = controller.getAllStations();

        assertEquals(3, stations.size());
    }

    @Test
    public void testGetStationById() {
        controller.createStation(station1);

        Optional<Station> result = controller.getStationById(100);

        assertTrue(result.isPresent());
        assertEquals("Lisbon Santa Apolónia", result.get().getName());
    }

    @Test
    public void testGetStationByIdNotFound() {
        Optional<Station> result = controller.getStationById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testGetStationByName() {
        controller.createStation(station1);

        Optional<Station> result = controller.getStationByName("Lisbon Santa Apolónia");

        assertTrue(result.isPresent());
        assertEquals(Integer.valueOf(100), result.get().getFacilityId());
    }

    @Test
    public void testGetStationByNameNotFound() {
        Optional<Station> result = controller.getStationByName("Nonexistent Station");

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateStation() {
        controller.createStation(station1);

        Station updated = new Station(
                100, "Lisbon Santa Apolónia - Updated", facilityType,
                38.7126, -9.1281, false,
                "PT", "Europe/Lisbon", "WET/GMT",
                false, true, false
        );

        boolean result = controller.updateStation(100, updated);

        assertTrue(result);
        Optional<Station> retrieved = controller.getStationById(100);
        assertTrue(retrieved.isPresent());
        assertEquals("Lisbon Santa Apolónia - Updated", retrieved.get().getName());
    }

    @Test
    public void testUpdateStationNotFound() {
        boolean result = controller.updateStation(999, station1);

        assertFalse(result);
    }

    @Test
    public void testDeleteStation() {
        controller.createStation(station1);
        controller.createStation(station2);

        boolean result = controller.deleteStation(100);

        assertTrue(result);
        assertEquals(1, controller.getAllStations().size());
    }

    @Test
    public void testDeleteStationNotFound() {
        boolean result = controller.deleteStation(999);

        assertFalse(result);
    }

    @Test
    public void testGetStationsByCountry() {
        controller.createStation(station1);
        controller.createStation(station2);

        Station spanishStation = new Station(
                103, "Madrid Atocha", facilityType,
                40.4064, -3.6910, false,
                "ES", "Europe/Madrid", "CET",
                false, true, false
        );
        controller.createStation(spanishStation);

        List<Station> portugueseStations = controller.getStationsByCountry("PT");

        assertEquals(2, portugueseStations.size());
        assertTrue(portugueseStations.stream().allMatch(s -> "PT".equals(s.getCountry())));
    }

    @Test
    public void testGetMainStations() {
        controller.createStation(station1);
        controller.createStation(station2);
        controller.createStation(station3);

        List<Station> mainStations = controller.getMainStations();

        assertEquals(2, mainStations.size());
        assertTrue(mainStations.stream().allMatch(s -> Boolean.TRUE.equals(s.getIsMainStation())));
    }

    @Test
    public void testGetAirportStations() {
        controller.createStation(station1);
        controller.createStation(station2);
        controller.createStation(station3);

        List<Station> airports = controller.getAirportStations();

        assertEquals(1, airports.size());
        assertTrue(airports.stream().allMatch(s -> Boolean.TRUE.equals(s.getIsAirport())));
    }
}
