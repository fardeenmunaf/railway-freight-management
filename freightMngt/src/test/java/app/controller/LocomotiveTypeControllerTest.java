package app.controller;

import app.model.LocomotiveType;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class LocomotiveTypeControllerTest {

    private LocomotiveTypeController controller;
    private LocomotiveType type1;
    private LocomotiveType type2;
    private LocomotiveType type3;

    @Before
    public void setUp() {
        Repositories.getInstance().getLocomotiveTypeRepository().clear();
        controller = new LocomotiveTypeController();

        type1 = new LocomotiveType(1, "Freight");
        type2 = new LocomotiveType(2, "Passenger");
        type3 = new LocomotiveType(3, "Shunting");
    }

    @Test
    public void testCreateLocomotiveType() {
        LocomotiveType result = controller.createLocomotiveType(type1);

        assertNotNull(result);
        assertEquals(type1, result);
        assertEquals(1, controller.getAllLocomotiveTypes().size());
    }

    @Test
    public void testCreateLocomotiveTypeWithNull() {
        LocomotiveType result = controller.createLocomotiveType(null);

        assertNull(result);
        assertEquals(0, controller.getAllLocomotiveTypes().size());
    }

    @Test
    public void testGetAllLocomotiveTypes() {
        controller.createLocomotiveType(type1);
        controller.createLocomotiveType(type2);
        controller.createLocomotiveType(type3);

        List<LocomotiveType> types = controller.getAllLocomotiveTypes();

        assertEquals(3, types.size());
        assertTrue(types.contains(type1));
        assertTrue(types.contains(type2));
        assertTrue(types.contains(type3));
    }

    @Test
    public void testGetAllLocomotiveTypesEmpty() {
        List<LocomotiveType> types = controller.getAllLocomotiveTypes();

        assertNotNull(types);
        assertEquals(0, types.size());
    }

    @Test
    public void testGetLocomotiveTypeByIdFound() {
        controller.createLocomotiveType(type1);
        controller.createLocomotiveType(type2);

        Optional<LocomotiveType> result = controller.getLocomotiveTypeById(1);

        assertTrue(result.isPresent());
        assertEquals(type1, result.get());
    }

    @Test
    public void testGetLocomotiveTypeByIdNotFound() {
        controller.createLocomotiveType(type1);

        Optional<LocomotiveType> result = controller.getLocomotiveTypeById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateLocomotiveTypeSuccess() {
        controller.createLocomotiveType(type1);

        LocomotiveType updatedType = new LocomotiveType(1, "Heavy Freight");

        boolean result = controller.updateLocomotiveType(1, updatedType);

        assertTrue(result);
        Optional<LocomotiveType> retrievedType = controller.getLocomotiveTypeById(1);
        assertTrue(retrievedType.isPresent());
        assertEquals("Heavy Freight", retrievedType.get().getName());
    }

    @Test
    public void testUpdateLocomotiveTypeNotFound() {
        controller.createLocomotiveType(type1);

        boolean result = controller.updateLocomotiveType(999, type2);

        assertFalse(result);
    }

    @Test
    public void testDeleteLocomotiveTypeSuccess() {
        controller.createLocomotiveType(type1);
        controller.createLocomotiveType(type2);
        controller.createLocomotiveType(type3);

        boolean result = controller.deleteLocomotiveType(2);

        assertTrue(result);
        assertEquals(2, controller.getAllLocomotiveTypes().size());
        assertFalse(controller.getLocomotiveTypeById(2).isPresent());
    }

    @Test
    public void testDeleteLocomotiveTypeNotFound() {
        controller.createLocomotiveType(type1);

        boolean result = controller.deleteLocomotiveType(999);

        assertFalse(result);
        assertEquals(1, controller.getAllLocomotiveTypes().size());
    }

    @Test
    public void testGetNextLocomotiveTypeId() {
        Integer nextId = controller.getNextLocomotiveTypeId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testClearAllLocomotiveTypes() {
        controller.createLocomotiveType(type1);
        controller.createLocomotiveType(type2);
        controller.createLocomotiveType(type3);
        assertEquals(3, controller.getAllLocomotiveTypes().size());

        controller.clearAllLocomotiveTypes();

        assertEquals(0, controller.getAllLocomotiveTypes().size());
    }

    @Test
    public void testConstructor() {
        LocomotiveTypeController newController = new LocomotiveTypeController();

        assertNotNull(newController);
        assertNotNull(newController.getAllLocomotiveTypes());
    }
}

