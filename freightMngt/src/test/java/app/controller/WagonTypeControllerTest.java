package app.controller;

import app.model.WagonType;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class WagonTypeControllerTest {

    private WagonTypeController controller;
    private WagonType wagonType1;
    private WagonType wagonType2;
    private WagonType wagonType3;

    @Before
    public void setUp() {
        Repositories.getInstance().getWagonTypeRepository().clear();
        controller = new WagonTypeController();

        wagonType1 = new WagonType(1, "Freight");
        wagonType2 = new WagonType(2, "Passenger");
        wagonType3 = new WagonType(3, "Tank");
    }

    @Test
    public void testCreateWagonType() {
        WagonType result = controller.createWagonType(wagonType1);

        assertNotNull(result);
        assertEquals(wagonType1, result);
        assertEquals(1, controller.getAllWagonTypes().size());
    }

    @Test
    public void testCreateWagonTypeWithNull() {
        WagonType result = controller.createWagonType(null);

        assertNull(result);
        assertEquals(0, controller.getAllWagonTypes().size());
    }

    @Test
    public void testGetAllWagonTypes() {
        controller.createWagonType(wagonType1);
        controller.createWagonType(wagonType2);
        controller.createWagonType(wagonType3);

        List<WagonType> wagonTypes = controller.getAllWagonTypes();

        assertEquals(3, wagonTypes.size());
        assertTrue(wagonTypes.contains(wagonType1));
        assertTrue(wagonTypes.contains(wagonType2));
        assertTrue(wagonTypes.contains(wagonType3));
    }

    @Test
    public void testGetAllWagonTypesEmpty() {
        List<WagonType> wagonTypes = controller.getAllWagonTypes();

        assertNotNull(wagonTypes);
        assertEquals(0, wagonTypes.size());
    }

    @Test
    public void testGetWagonTypeByIdFound() {
        controller.createWagonType(wagonType1);
        controller.createWagonType(wagonType2);

        Optional<WagonType> result = controller.getWagonTypeById(1);

        assertTrue(result.isPresent());
        assertEquals(wagonType1, result.get());
    }

    @Test
    public void testGetWagonTypeByIdNotFound() {
        controller.createWagonType(wagonType1);

        Optional<WagonType> result = controller.getWagonTypeById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateWagonTypeSuccess() {
        controller.createWagonType(wagonType1);

        WagonType updatedWagonType = new WagonType(1, "Heavy Freight");

        boolean result = controller.updateWagonType(1, updatedWagonType);

        assertTrue(result);
        Optional<WagonType> retrievedType = controller.getWagonTypeById(1);
        assertTrue(retrievedType.isPresent());
        assertEquals("Heavy Freight", retrievedType.get().getName());
    }

    @Test
    public void testUpdateWagonTypeNotFound() {
        controller.createWagonType(wagonType1);

        boolean result = controller.updateWagonType(999, wagonType2);

        assertFalse(result);
    }

    @Test
    public void testDeleteWagonTypeSuccess() {
        controller.createWagonType(wagonType1);
        controller.createWagonType(wagonType2);
        controller.createWagonType(wagonType3);

        boolean result = controller.deleteWagonType(2);

        assertTrue(result);
        assertEquals(2, controller.getAllWagonTypes().size());
        assertFalse(controller.getWagonTypeById(2).isPresent());
    }

    @Test
    public void testDeleteWagonTypeNotFound() {
        controller.createWagonType(wagonType1);

        boolean result = controller.deleteWagonType(999);

        assertFalse(result);
        assertEquals(1, controller.getAllWagonTypes().size());
    }

    @Test
    public void testGetNextWagonTypeId() {
        Integer nextId = controller.getNextWagonTypeId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testClearAllWagonTypes() {
        controller.createWagonType(wagonType1);
        controller.createWagonType(wagonType2);
        controller.createWagonType(wagonType3);
        assertEquals(3, controller.getAllWagonTypes().size());

        controller.clearAllWagonTypes();

        assertEquals(0, controller.getAllWagonTypes().size());
    }

    @Test
    public void testConstructor() {
        WagonTypeController newController = new WagonTypeController();

        assertNotNull(newController);
        assertNotNull(newController.getAllWagonTypes());
    }
}

