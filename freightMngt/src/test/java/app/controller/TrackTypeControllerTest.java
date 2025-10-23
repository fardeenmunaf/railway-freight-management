package app.controller;

import app.model.TrackType;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class TrackTypeControllerTest {

    private TrackTypeController controller;
    private TrackType trackType1;
    private TrackType trackType2;

    @Before
    public void setUp() {
        // Clear repository to ensure test isolation
        Repositories.getInstance().getTrackTypeRepository().clear();

        controller = new TrackTypeController();
        trackType1 = new TrackType(1, "Main Line");
        trackType2 = new TrackType(2, "Siding");
    }

    @Test
    public void testCreateTrackType() {
        TrackType result = controller.createTrackType(trackType1);

        assertNotNull(result);
        assertEquals(trackType1, result);
        assertEquals(1, controller.getAllTrackTypes().size());
    }

    @Test
    public void testCreateTrackTypeWithNull() {
        TrackType result = controller.createTrackType(null);

        assertNull(result);
        assertEquals(0, controller.getAllTrackTypes().size());
    }

    @Test
    public void testGetAllTrackTypes() {
        controller.createTrackType(trackType1);
        controller.createTrackType(trackType2);

        List<TrackType> trackTypes = controller.getAllTrackTypes();

        assertEquals(2, trackTypes.size());
        assertTrue(trackTypes.contains(trackType1));
        assertTrue(trackTypes.contains(trackType2));
    }

    @Test
    public void testGetAllTrackTypesEmpty() {
        List<TrackType> trackTypes = controller.getAllTrackTypes();

        assertNotNull(trackTypes);
        assertEquals(0, trackTypes.size());
    }

    @Test
    public void testGetTrackTypeByIdFound() {
        controller.createTrackType(trackType1);
        controller.createTrackType(trackType2);

        Optional<TrackType> result = controller.getTrackTypeById(1);

        assertTrue(result.isPresent());
        assertEquals(trackType1, result.get());
    }

    @Test
    public void testGetTrackTypeByIdNotFound() {
        controller.createTrackType(trackType1);

        Optional<TrackType> result = controller.getTrackTypeById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateTrackTypeSuccess() {
        controller.createTrackType(trackType1);
        TrackType updatedTrackType = new TrackType(1, "Main Line Updated");

        boolean result = controller.updateTrackType(1, updatedTrackType);

        assertTrue(result);
        Optional<TrackType> retrievedTrackType = controller.getTrackTypeById(1);
        assertTrue(retrievedTrackType.isPresent());
        assertEquals("Main Line Updated", retrievedTrackType.get().getName());
    }

    @Test
    public void testUpdateTrackTypeNotFound() {
        controller.createTrackType(trackType1);
        TrackType updatedTrackType = new TrackType(999, "Non-existent Type");

        boolean result = controller.updateTrackType(999, updatedTrackType);

        assertFalse(result);
    }

    @Test
    public void testDeleteTrackTypeSuccess() {
        controller.createTrackType(trackType1);
        controller.createTrackType(trackType2);

        boolean result = controller.deleteTrackType(1);

        assertTrue(result);
        assertEquals(1, controller.getAllTrackTypes().size());
        assertFalse(controller.getTrackTypeById(1).isPresent());
    }

    @Test
    public void testDeleteTrackTypeNotFound() {
        controller.createTrackType(trackType1);

        boolean result = controller.deleteTrackType(999);

        assertFalse(result);
        assertEquals(1, controller.getAllTrackTypes().size());
    }

    @Test
    public void testGetNextTrackTypeId() {
        Integer nextId = controller.getNextTrackTypeId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testConstructor() {
        TrackTypeController newController = new TrackTypeController();

        assertNotNull(newController);
        assertNotNull(newController.getAllTrackTypes());
    }

    @Test
    public void testClearAllTrackTypes() {
        controller.createTrackType(trackType1);
        controller.createTrackType(trackType2);
        assertEquals(2, controller.getAllTrackTypes().size());

        controller.clearAllTrackTypes();

        assertEquals(0, controller.getAllTrackTypes().size());
    }
}




