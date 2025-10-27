package app.controller;

import app.model.*;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class WagonModelControllerTest {

    private WagonModelController controller;
    private WagonModel wagonModel1;
    private WagonModel wagonModel2;
    private WagonModel wagonModel3;
    private WagonType wagonType1;

    @Before
    public void setUp() {
        Repositories.getInstance().getWagonModelRepository().clear();
        controller = new WagonModelController();

        wagonType1 = new WagonType(1, "Freight");

        Dimensions dim1 = new Dimensions(15.0, 2.8, 3.5);
        Dimensions dim2 = new Dimensions(18.0, 3.0, 4.0);
        Dimensions dim3 = new Dimensions(20.0, 3.2, 4.2);

        wagonModel1 = new WagonModel(1, "FreightWagon-2000", wagonType1, "Bombardier",
                "Y25", 2, 18000.0, 60000.0, 120.0, dim1);
        wagonModel2 = new WagonModel(2, "FreightWagon-3000", wagonType1, "CAF",
                "Y25", 2, 20000.0, 70000.0, 120.0, dim2);
        wagonModel3 = new WagonModel(3, "FreightWagon-4000", wagonType1, "Siemens",
                "Y25", 2, 22000.0, 80000.0, 140.0, dim3);
    }

    @Test
    public void testCreateWagonModel() {
        WagonModel result = controller.createWagonModel(wagonModel1);

        assertNotNull(result);
        assertEquals(wagonModel1, result);
        assertEquals(1, controller.getAllWagonModels().size());
    }

    @Test
    public void testCreateWagonModelWithNull() {
        WagonModel result = controller.createWagonModel(null);

        assertNull(result);
        assertEquals(0, controller.getAllWagonModels().size());
    }

    @Test
    public void testGetAllWagonModels() {
        controller.createWagonModel(wagonModel1);
        controller.createWagonModel(wagonModel2);
        controller.createWagonModel(wagonModel3);

        List<WagonModel> wagonModels = controller.getAllWagonModels();

        assertEquals(3, wagonModels.size());
        assertTrue(wagonModels.contains(wagonModel1));
        assertTrue(wagonModels.contains(wagonModel2));
        assertTrue(wagonModels.contains(wagonModel3));
    }

    @Test
    public void testGetAllWagonModelsEmpty() {
        List<WagonModel> wagonModels = controller.getAllWagonModels();

        assertNotNull(wagonModels);
        assertEquals(0, wagonModels.size());
    }

    @Test
    public void testGetWagonModelByIdFound() {
        controller.createWagonModel(wagonModel1);
        controller.createWagonModel(wagonModel2);

        Optional<WagonModel> result = controller.getWagonModelById(1);

        assertTrue(result.isPresent());
        assertEquals(wagonModel1, result.get());
    }

    @Test
    public void testGetWagonModelByIdNotFound() {
        controller.createWagonModel(wagonModel1);

        Optional<WagonModel> result = controller.getWagonModelById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testGetWagonModelByNameFound() {
        controller.createWagonModel(wagonModel1);
        controller.createWagonModel(wagonModel2);

        Optional<WagonModel> result = controller.getWagonModelByName("FreightWagon-2000");

        assertTrue(result.isPresent());
        assertEquals(wagonModel1, result.get());
    }

    @Test
    public void testGetWagonModelByNameNotFound() {
        controller.createWagonModel(wagonModel1);

        Optional<WagonModel> result = controller.getWagonModelByName("NonExistent");

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateWagonModelSuccess() {
        controller.createWagonModel(wagonModel1);

        Dimensions newDim = new Dimensions(16.0, 2.9, 3.6);
        WagonModel updatedWagonModel = new WagonModel(1, "FreightWagon-2000-Updated", wagonType1, "Bombardier",
                "Y25", 2, 19000.0, 65000.0, 130.0, newDim);

        boolean result = controller.updateWagonModel(1, updatedWagonModel);

        assertTrue(result);
        Optional<WagonModel> retrievedModel = controller.getWagonModelById(1);
        assertTrue(retrievedModel.isPresent());
        assertEquals("FreightWagon-2000-Updated", retrievedModel.get().getName());
        assertEquals(19000.0, retrievedModel.get().getTareWeight(), 0.01);
    }

    @Test
    public void testUpdateWagonModelNotFound() {
        controller.createWagonModel(wagonModel1);

        boolean result = controller.updateWagonModel(999, wagonModel2);

        assertFalse(result);
    }

    @Test
    public void testDeleteWagonModelSuccess() {
        controller.createWagonModel(wagonModel1);
        controller.createWagonModel(wagonModel2);
        controller.createWagonModel(wagonModel3);

        boolean result = controller.deleteWagonModel(2);

        assertTrue(result);
        assertEquals(2, controller.getAllWagonModels().size());
        assertFalse(controller.getWagonModelById(2).isPresent());
    }

    @Test
    public void testDeleteWagonModelNotFound() {
        controller.createWagonModel(wagonModel1);

        boolean result = controller.deleteWagonModel(999);

        assertFalse(result);
        assertEquals(1, controller.getAllWagonModels().size());
    }

    @Test
    public void testGetNextWagonModelId() {
        Integer nextId = controller.getNextWagonModelId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testClearAllWagonModels() {
        controller.createWagonModel(wagonModel1);
        controller.createWagonModel(wagonModel2);
        controller.createWagonModel(wagonModel3);
        assertEquals(3, controller.getAllWagonModels().size());

        controller.clearAllWagonModels();

        assertEquals(0, controller.getAllWagonModels().size());
    }

    @Test
    public void testConstructor() {
        WagonModelController newController = new WagonModelController();

        assertNotNull(newController);
        assertNotNull(newController.getAllWagonModels());
    }
}

