package app.service;

import app.model.FacilityType;
import app.model.Station;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for StationService.
 * Tests station search and indexing operations.
 */
public class StationServiceTest {

    private StationService stationService;

    @Before
    public void setUp() {
        stationService = new StationService();
        stationService.buildIndexes();
    }

    @Test
    public void testBuildIndexes() {
        int count = stationService.getStationCount();
        assertTrue("Should have loaded stations", count > 0);
    }

    @Test
    public void testSearchByCountry() {
        List<Station> portugueseStations = stationService.searchByCountry("pt");
        assertFalse("Should find Portuguese stations", portugueseStations.isEmpty());

        for (Station station : portugueseStations) {
            assertEquals("Station should be in Portugal", "pt", station.getCountry().toLowerCase());
        }
    }

    @Test
    public void testSearchByName() {
        List<Station> stations = stationService.searchByName("lisboa");

        for (Station station : stations) {
            assertTrue("Station name should contain 'lisboa'",
                    station.getName().toLowerCase().contains("lisboa"));
        }
    }

    @Test
    public void testSearchByTimeZone() {
        List<Station> stations = stationService.searchByTimeZone("Europe/Lisbon");

        for (Station station : stations) {
            assertEquals("Station should be in Europe/Lisbon timezone",
                    "Europe/Lisbon", station.getTimeZone());
        }
    }

    @Test
    public void testGetMainStations() {
        List<Station> mainStations = stationService.getMainStations();

        for (Station station : mainStations) {
            assertTrue("Station should be main station",
                    Boolean.TRUE.equals(station.getIsMainStation()));
        }
    }

    @Test
    public void testGetAirportStations() {
        List<Station> airports = stationService.getAirportStations();

        for (Station station : airports) {
            assertTrue("Station should be airport",
                    Boolean.TRUE.equals(station.getIsAirport()));
        }
    }

    @Test
    public void testGetAllStationsSortedByName() {
        List<Station> sorted = stationService.getAllStationsSortedByName();

        for (int i = 1; i < sorted.size(); i++) {
            String prev = sorted.get(i - 1).getName().toLowerCase();
            String curr = sorted.get(i).getName().toLowerCase();
            assertTrue("Stations should be sorted by name", prev.compareTo(curr) <= 0);
        }
    }

    @Test
    public void testSearchNonExistentCountry() {
        List<Station> stations = stationService.searchByCountry("zz");
        assertTrue("Should return empty list for non-existent country", stations.isEmpty());
    }

    @Test
    public void testSearchWithNullInput() {
        List<Station> stations = stationService.searchByName(null);
        assertTrue("Should handle null input gracefully", stations.isEmpty());
    }

    @Test
    public void testSearchWithEmptyInput() {
        List<Station> stations = stationService.searchByName("");
        assertTrue("Should handle empty input gracefully", stations.isEmpty());
    }

    @Test
    public void testSearchByCountryCaseInsensitive() {
        List<Station> lower = stationService.searchByCountry("pt");
        List<Station> upper = stationService.searchByCountry("PT");

        assertEquals("Search should be case insensitive", lower.size(), upper.size());
    }

    @Test
    public void testSearchByNameCaseInsensitive() {
        List<Station> lower = stationService.searchByName("lisboa");
        List<Station> upper = stationService.searchByName("LISBOA");

        assertEquals("Search should be case insensitive", lower.size(), upper.size());
    }

    @Test
    public void testSearchByTimeZoneRange() {
        List<Station> stations = stationService.searchByTimeZoneRange("Europe/Dublin", "Europe/Paris");

        for (Station station : stations) {
            assertNotNull("Station should have timezone", station.getTimeZone());
            assertTrue("Station timezone should be in range",
                    station.getTimeZone().compareTo("Europe/Dublin") >= 0 &&
                            station.getTimeZone().compareTo("Europe/Paris") <= 0);
        }
    }
}
