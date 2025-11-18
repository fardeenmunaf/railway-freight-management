package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class StationTest {

    private Station station;
    private FacilityType facilityType;

    @Before
    public void setUp() {
        facilityType = new FacilityType(1, "Railway Station");
        station = new Station(
                100,
                "Lisbon Santa Apolónia",
                facilityType,
                38.7126,
                -9.1281,
                false,
                "PT",
                "Europe/Lisbon",
                "WET/GMT",
                false,
                true,
                false
        );
    }

    @Test
    public void testDefaultConstructor() {
        Station emptyStation = new Station();

        assertNotNull(emptyStation);
        assertNull(emptyStation.getCountry());
        assertNull(emptyStation.getTimeZone());
        assertNull(emptyStation.getTimeZoneGroup());
        assertNull(emptyStation.getIsCity());
        assertNull(emptyStation.getIsMainStation());
        assertNull(emptyStation.getIsAirport());
    }

    @Test
    public void testFullConstructor() {
        assertEquals(Integer.valueOf(100), station.getFacilityId());
        assertEquals("Lisbon Santa Apolónia", station.getName());
        assertEquals("PT", station.getCountry());
        assertEquals("Europe/Lisbon", station.getTimeZone());
        assertEquals("WET/GMT", station.getTimeZoneGroup());
        assertEquals(Boolean.FALSE, station.getIsCity());
        assertEquals(Boolean.TRUE, station.getIsMainStation());
        assertEquals(Boolean.FALSE, station.getIsAirport());
    }

    @Test
    public void testSettersAndGetters() {
        station.setCountry("ES");
        station.setTimeZone("Europe/Madrid");
        station.setTimeZoneGroup("CET");
        station.setIsCity(true);
        station.setIsMainStation(false);
        station.setIsAirport(true);

        assertEquals("ES", station.getCountry());
        assertEquals("Europe/Madrid", station.getTimeZone());
        assertEquals("CET", station.getTimeZoneGroup());
        assertEquals(Boolean.TRUE, station.getIsCity());
        assertEquals(Boolean.FALSE, station.getIsMainStation());
        assertEquals(Boolean.TRUE, station.getIsAirport());
    }

    @Test
    public void testToString() {
        String result = station.toString();

        assertTrue(result.contains("Lisbon Santa Apolónia"));
        assertTrue(result.contains("PT"));
        assertTrue(result.contains("Europe/Lisbon"));
        assertTrue(result.contains("WET/GMT"));
    }

    @Test
    public void testNullValues() {
        Station nullStation = new Station(
                200,
                "Test Station",
                facilityType,
                null,
                null,
                false,
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertNull(nullStation.getCountry());
        assertNull(nullStation.getTimeZone());
        assertNull(nullStation.getTimeZoneGroup());
        assertNull(nullStation.getIsCity());
        assertNull(nullStation.getIsMainStation());
        assertNull(nullStation.getIsAirport());
    }
}
