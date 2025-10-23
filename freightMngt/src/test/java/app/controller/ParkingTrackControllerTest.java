package app.controller;

import app.model.ParkingTrack;
import app.model.Line;
import app.model.Facility;
import app.model.User;
import app.model.Company;
import app.model.FacilityType;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class ParkingTrackControllerTest {

    private ParkingTrackController controller;
    private ParkingTrack track1;
    private ParkingTrack track2;
    private ParkingTrack track3;
    private Line line1;
    private Facility facility1;
    private Facility facility2;

    @Before
    public void setUp() {
        Repositories.getInstance().getParkingTrackRepository().clear();

        controller = new ParkingTrackController();

        Company company1 = new Company(1, "Rail Company", "RC", "123456789");
        User owner1 = new User(1, "user1", "user1@rail.com", "pass123", company1, true);

        line1 = new Line(1, owner1);

        FacilityType facilityType1 = new FacilityType(1, "Yard");
        facility1 = new Facility(1, "Main Yard", facilityType1, 41.1579, -8.6291, true);
        facility2 = new Facility(2, "North Yard", facilityType1, 41.1679, -8.6391, true);

        track1 = new ParkingTrack(1, line1, facility1, "Track 1", 20);
        track2 = new ParkingTrack(2, line1, facility1, "Track 2", 25);
        track3 = new ParkingTrack(3, line1, facility2, "Track 1", 30);
    }

    @Test
    public void testCreateParkingTrack() {
        ParkingTrack result = controller.createParkingTrack(track1);

        assertNotNull(result);
        assertEquals(track1, result);
        assertEquals(1, controller.getAllParkingTracks().size());
    }

    @Test
    public void testCreateParkingTrackWithNull() {
        ParkingTrack result = controller.createParkingTrack(null);

        assertNull(result);
        assertEquals(0, controller.getAllParkingTracks().size());
    }

    @Test
    public void testGetAllParkingTracks() {
        controller.createParkingTrack(track1);
        controller.createParkingTrack(track2);
        controller.createParkingTrack(track3);

        List<ParkingTrack> tracks = controller.getAllParkingTracks();

        assertEquals(3, tracks.size());
        assertTrue(tracks.contains(track1));
        assertTrue(tracks.contains(track2));
        assertTrue(tracks.contains(track3));
    }

    @Test
    public void testGetAllParkingTracksEmpty() {
        List<ParkingTrack> tracks = controller.getAllParkingTracks();

        assertNotNull(tracks);
        assertEquals(0, tracks.size());
    }

    @Test
    public void testGetParkingTrackByIdFound() {
        controller.createParkingTrack(track1);
        controller.createParkingTrack(track2);

        Optional<ParkingTrack> result = controller.getParkingTrackById(1);

        assertTrue(result.isPresent());
        assertEquals(track1, result.get());
    }

    @Test
    public void testGetParkingTrackByIdNotFound() {
        controller.createParkingTrack(track1);

        Optional<ParkingTrack> result = controller.getParkingTrackById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateParkingTrackSuccess() {
        controller.createParkingTrack(track1);

        ParkingTrack updatedTrack = new ParkingTrack(1, line1, facility1, "Track 1 Extended", 22);

        boolean result = controller.updateParkingTrack(1, updatedTrack);

        assertTrue(result);
        Optional<ParkingTrack> retrievedTrack = controller.getParkingTrackById(1);
        assertTrue(retrievedTrack.isPresent());
        assertEquals("Track 1 Extended", retrievedTrack.get().getName());
        assertEquals(22, retrievedTrack.get().getCapacityWagons().intValue());
    }

    @Test
    public void testUpdateParkingTrackNotFound() {
        controller.createParkingTrack(track1);

        boolean result = controller.updateParkingTrack(999, track2);

        assertFalse(result);
    }

    @Test
    public void testDeleteParkingTrackSuccess() {
        controller.createParkingTrack(track1);
        controller.createParkingTrack(track2);
        controller.createParkingTrack(track3);

        boolean result = controller.deleteParkingTrack(2);

        assertTrue(result);
        assertEquals(2, controller.getAllParkingTracks().size());
        assertFalse(controller.getParkingTrackById(2).isPresent());
    }

    @Test
    public void testDeleteParkingTrackNotFound() {
        controller.createParkingTrack(track1);

        boolean result = controller.deleteParkingTrack(999);

        assertFalse(result);
        assertEquals(1, controller.getAllParkingTracks().size());
    }

    @Test
    public void testGetNextParkingTrackId() {
        Integer nextId = controller.getNextParkingTrackId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testClearAllParkingTracks() {
        controller.createParkingTrack(track1);
        controller.createParkingTrack(track2);
        controller.createParkingTrack(track3);
        assertEquals(3, controller.getAllParkingTracks().size());

        controller.clearAllParkingTracks();

        assertEquals(0, controller.getAllParkingTracks().size());
    }

    @Test
    public void testConstructor() {
        ParkingTrackController newController = new ParkingTrackController();

        assertNotNull(newController);
        assertNotNull(newController.getAllParkingTracks());
    }
}

