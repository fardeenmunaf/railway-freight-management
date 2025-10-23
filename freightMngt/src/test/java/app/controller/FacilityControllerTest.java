package app.controller;

import app.model.Facility;
import app.model.FacilityType;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class FacilityControllerTest {

    private FacilityController controller;
    private Facility facility1;
    private Facility facility2;
    private FacilityType facilityType;

    @Before
    public void setUp() {
        // Clear repository to ensure test isolation
        Repositories.getInstance().getFacilityRepository().clear();

        controller = new FacilityController();
        facilityType = new FacilityType(1, "Warehouse");
        facility1 = new Facility(1, "Facility A", facilityType, 40.0, -8.0, true);
        facility2 = new Facility(2, "Facility B", facilityType, 41.0, -9.0, false);
    }

    @Test
    public void testCreateFacility() {
        Facility result = controller.createFacility(facility1);

        assertNotNull(result);
        assertEquals(facility1, result);
        assertEquals(1, controller.getAllFacilities().size());
    }

    @Test
    public void testCreateFacilityWithNull() {
        Facility result = controller.createFacility(null);

        assertNull(result);
        assertEquals(0, controller.getAllFacilities().size());
    }

    @Test
    public void testGetAllFacilities() {
        controller.createFacility(facility1);
        controller.createFacility(facility2);

        List<Facility> facilities = controller.getAllFacilities();

        assertEquals(2, facilities.size());
        assertTrue(facilities.contains(facility1));
        assertTrue(facilities.contains(facility2));
    }

    @Test
    public void testGetAllFacilitiesEmpty() {
        List<Facility> facilities = controller.getAllFacilities();

        assertNotNull(facilities);
        assertEquals(0, facilities.size());
    }

    @Test
    public void testGetFacilityByIdFound() {
        controller.createFacility(facility1);
        controller.createFacility(facility2);

        Optional<Facility> result = controller.getFacilityById(1);

        assertTrue(result.isPresent());
        assertEquals(facility1, result.get());
    }

    @Test
    public void testGetFacilityByIdNotFound() {
        controller.createFacility(facility1);

        Optional<Facility> result = controller.getFacilityById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testGetFacilityByNameFound() {
        controller.createFacility(facility1);
        controller.createFacility(facility2);

        Optional<Facility> result = controller.getFacilityByName("Facility A");

        assertTrue(result.isPresent());
        assertEquals(facility1, result.get());
    }

    @Test
    public void testGetFacilityByNameNotFound() {
        controller.createFacility(facility1);

        Optional<Facility> result = controller.getFacilityByName("Non-existent");

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateFacilitySuccess() {
        controller.createFacility(facility1);
        Facility updatedFacility = new Facility(1, "Facility A Updated", facilityType, 40.0, -8.0, true);

        boolean result = controller.updateFacility(1, updatedFacility);

        assertTrue(result);
        Optional<Facility> retrievedFacility = controller.getFacilityById(1);
        assertTrue(retrievedFacility.isPresent());
        assertEquals("Facility A Updated", retrievedFacility.get().getName());
    }

    @Test
    public void testUpdateFacilityNotFound() {
        controller.createFacility(facility1);
        Facility updatedFacility = new Facility(999, "Non-existent Facility", facilityType, 40.0, -8.0, true);

        boolean result = controller.updateFacility(999, updatedFacility);

        assertFalse(result);
    }

    @Test
    public void testDeleteFacilitySuccess() {
        controller.createFacility(facility1);
        controller.createFacility(facility2);

        boolean result = controller.deleteFacility(1);

        assertTrue(result);
        assertEquals(1, controller.getAllFacilities().size());
        assertFalse(controller.getFacilityById(1).isPresent());
    }

    @Test
    public void testDeleteFacilityNotFound() {
        controller.createFacility(facility1);

        boolean result = controller.deleteFacility(999);

        assertFalse(result);
        assertEquals(1, controller.getAllFacilities().size());
    }

    @Test
    public void testGetNextFacilityId() {
        Integer nextId = controller.getNextFacilityId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testConstructor() {
        FacilityController newController = new FacilityController();

        assertNotNull(newController);
        assertNotNull(newController.getAllFacilities());
    }

    @Test
    public void testContainsNameFound() {
        controller.createFacility(facility1);

        boolean result = controller.containsName("Facility A");

        assertTrue(result);
    }

    @Test
    public void testContainsNameNotFound() {
        controller.createFacility(facility1);

        boolean result = controller.containsName("Non-existent Facility");

        assertFalse(result);
    }

    @Test
    public void testClearAllFacilities() {
        controller.createFacility(facility1);
        controller.createFacility(facility2);
        assertEquals(2, controller.getAllFacilities().size());

        controller.clearAllFacilities();

        assertEquals(0, controller.getAllFacilities().size());
    }
}

