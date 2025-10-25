package app.controller;

import app.model.FacilityFeatureType;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class FacilityFeatureTypeControllerTest {

    private FacilityFeatureTypeController controller;
    private FacilityFeatureType facilityFeatureType1;
    private FacilityFeatureType facilityFeatureType2;

    @Before
    public void setUp() {
        controller = new FacilityFeatureTypeController();
        facilityFeatureType1 = new FacilityFeatureType(1, "Loading Dock");
        facilityFeatureType2 = new FacilityFeatureType(2, "Storage Area");
    }

    @Test
    public void testCreateFacilityFeatureType() {
        FacilityFeatureType result = controller.createFacilityFeatureType(facilityFeatureType1);

        assertNotNull(result);
        assertEquals(facilityFeatureType1, result);
        assertEquals(1, controller.getAllFacilityFeatureTypes().size());
    }

    @Test
    public void testCreateFacilityFeatureTypeWithNull() {
        FacilityFeatureType result = controller.createFacilityFeatureType(null);

        assertNull(result);
        assertEquals(0, controller.getAllFacilityFeatureTypes().size());
    }

    @Test
    public void testGetAllFacilityFeatureTypes() {
        controller.createFacilityFeatureType(facilityFeatureType1);
        controller.createFacilityFeatureType(facilityFeatureType2);

        List<FacilityFeatureType> facilityFeatureTypes = controller.getAllFacilityFeatureTypes();

        assertEquals(2, facilityFeatureTypes.size());
        assertTrue(facilityFeatureTypes.contains(facilityFeatureType1));
        assertTrue(facilityFeatureTypes.contains(facilityFeatureType2));
    }

    @Test
    public void testGetAllFacilityFeatureTypesEmpty() {
        List<FacilityFeatureType> facilityFeatureTypes = controller.getAllFacilityFeatureTypes();

        assertNotNull(facilityFeatureTypes);
        assertEquals(0, facilityFeatureTypes.size());
    }

    @Test
    public void testGetFacilityFeatureTypeByIdFound() {
        controller.createFacilityFeatureType(facilityFeatureType1);
        controller.createFacilityFeatureType(facilityFeatureType2);

        Optional<FacilityFeatureType> result = controller.getFacilityFeatureTypeById(1);

        assertTrue(result.isPresent());
        assertEquals(facilityFeatureType1, result.get());
    }

    @Test
    public void testGetFacilityFeatureTypeByIdNotFound() {
        controller.createFacilityFeatureType(facilityFeatureType1);

        Optional<FacilityFeatureType> result = controller.getFacilityFeatureTypeById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateFacilityFeatureTypeSuccess() {
        controller.createFacilityFeatureType(facilityFeatureType1);
        FacilityFeatureType updatedFacilityFeatureType = new FacilityFeatureType(1, "Loading Dock Updated");

        boolean result = controller.updateFacilityFeatureType(1, updatedFacilityFeatureType);

        assertTrue(result);
        Optional<FacilityFeatureType> retrievedFacilityFeatureType = controller.getFacilityFeatureTypeById(1);
        assertTrue(retrievedFacilityFeatureType.isPresent());
        assertEquals("Loading Dock Updated", retrievedFacilityFeatureType.get().getName());
    }

    @Test
    public void testUpdateFacilityFeatureTypeNotFound() {
        controller.createFacilityFeatureType(facilityFeatureType1);
        FacilityFeatureType updatedFacilityFeatureType = new FacilityFeatureType(999, "Non-existent Feature");

        boolean result = controller.updateFacilityFeatureType(999, updatedFacilityFeatureType);

        assertFalse(result);
    }

    @Test
    public void testDeleteFacilityFeatureTypeSuccess() {
        controller.createFacilityFeatureType(facilityFeatureType1);
        controller.createFacilityFeatureType(facilityFeatureType2);

        boolean result = controller.deleteFacilityFeatureType(1);

        assertTrue(result);
        assertEquals(1, controller.getAllFacilityFeatureTypes().size());
        assertFalse(controller.getFacilityFeatureTypeById(1).isPresent());
    }

    @Test
    public void testDeleteFacilityFeatureTypeNotFound() {
        controller.createFacilityFeatureType(facilityFeatureType1);

        boolean result = controller.deleteFacilityFeatureType(999);

        assertFalse(result);
        assertEquals(1, controller.getAllFacilityFeatureTypes().size());
    }

    @Test
    public void testConstructor() {
        FacilityFeatureTypeController newController = new FacilityFeatureTypeController();

        assertNotNull(newController);
        assertNotNull(newController.getAllFacilityFeatureTypes());
    }
}

