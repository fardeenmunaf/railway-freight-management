package app.controller;

import app.model.FacilityType;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class FacilityTypeControllerTest {

    private FacilityTypeController controller;
    private FacilityType facilityType1;
    private FacilityType facilityType2;

    @Before
    public void setUp() {
        controller = new FacilityTypeController();
        facilityType1 = new FacilityType(1, "Warehouse");
        facilityType2 = new FacilityType(2, "Distribution Center");
    }

    @Test
    public void testCreateFacilityType() {
        FacilityType result = controller.createFacilityType(facilityType1);

        assertNotNull(result);
        assertEquals(facilityType1, result);
        assertEquals(1, controller.getAllFacilityTypes().size());
    }

    @Test
    public void testCreateFacilityTypeWithNull() {
        FacilityType result = controller.createFacilityType(null);

        assertNull(result);
        assertEquals(0, controller.getAllFacilityTypes().size());
    }

    @Test
    public void testGetAllFacilityTypes() {
        controller.createFacilityType(facilityType1);
        controller.createFacilityType(facilityType2);

        List<FacilityType> facilityTypes = controller.getAllFacilityTypes();

        assertEquals(2, facilityTypes.size());
        assertTrue(facilityTypes.contains(facilityType1));
        assertTrue(facilityTypes.contains(facilityType2));
    }

    @Test
    public void testGetAllFacilityTypesEmpty() {
        List<FacilityType> facilityTypes = controller.getAllFacilityTypes();

        assertNotNull(facilityTypes);
        assertEquals(0, facilityTypes.size());
    }

    @Test
    public void testGetFacilityTypeByIdFound() {
        controller.createFacilityType(facilityType1);
        controller.createFacilityType(facilityType2);

        Optional<FacilityType> result = controller.getFacilityTypeById(1);

        assertTrue(result.isPresent());
        assertEquals(facilityType1, result.get());
    }

    @Test
    public void testGetFacilityTypeByIdNotFound() {
        controller.createFacilityType(facilityType1);

        Optional<FacilityType> result = controller.getFacilityTypeById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateFacilityTypeSuccess() {
        controller.createFacilityType(facilityType1);
        FacilityType updatedFacilityType = new FacilityType(1, "Warehouse Updated");

        boolean result = controller.updateFacilityType(1, updatedFacilityType);

        assertTrue(result);
        Optional<FacilityType> retrievedFacilityType = controller.getFacilityTypeById(1);
        assertTrue(retrievedFacilityType.isPresent());
        assertEquals("Warehouse Updated", retrievedFacilityType.get().getName());
    }

    @Test
    public void testUpdateFacilityTypeNotFound() {
        controller.createFacilityType(facilityType1);
        FacilityType updatedFacilityType = new FacilityType(999, "Non-existent Type");

        boolean result = controller.updateFacilityType(999, updatedFacilityType);

        assertFalse(result);
    }

    @Test
    public void testDeleteFacilityTypeSuccess() {
        controller.createFacilityType(facilityType1);
        controller.createFacilityType(facilityType2);

        boolean result = controller.deleteFacilityType(1);

        assertTrue(result);
        assertEquals(1, controller.getAllFacilityTypes().size());
        assertFalse(controller.getFacilityTypeById(1).isPresent());
    }

    @Test
    public void testDeleteFacilityTypeNotFound() {
        controller.createFacilityType(facilityType1);

        boolean result = controller.deleteFacilityType(999);

        assertFalse(result);
        assertEquals(1, controller.getAllFacilityTypes().size());
    }

    @Test
    public void testConstructor() {
        FacilityTypeController newController = new FacilityTypeController();

        assertNotNull(newController);
        assertNotNull(newController.getAllFacilityTypes());
    }
}

