package app.controller;

import app.model.Wagon;
import app.model.WagonModel;
import app.model.WagonType;
import app.model.User;
import app.model.Company;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class WagonControllerTest {

    private WagonController controller;
    private Wagon wagon1;
    private Wagon wagon2;
    private Wagon wagon3;
    private WagonModel wagonModel1;

    @Before
    public void setUp() {
        Repositories.getInstance().getWagonRepository().clear();
        controller = new WagonController();

        Company company1 = new Company(1, "Rail Company", "RC", "123456789");
        User operator1 = new User(1, "user1", "user1@rail.com", "pass123", company1, true);

        WagonType wagonType1 = new WagonType(1, "Freight");
        app.model.Dimensions dim = new app.model.Dimensions(10.0, 2.5, 3.0);
        wagonModel1 = new WagonModel(1, "Model X", wagonType1, "Constructor A", "Y25", 2, 15000.0, 50000.0, 120.0, dim);

        wagon1 = new Wagon(1, wagonModel1, operator1, 2020);
        wagon2 = new Wagon(2, wagonModel1, operator1, 2021);
        wagon3 = new Wagon(3, wagonModel1, operator1, 2022);
    }

    @Test
    public void testCreateWagon() {
        Wagon result = controller.createWagon(wagon1);

        assertNotNull(result);
        assertEquals(wagon1, result);
        assertEquals(1, controller.getAllWagons().size());
    }

    @Test
    public void testCreateWagonWithNull() {
        Wagon result = controller.createWagon(null);

        assertNull(result);
        assertEquals(0, controller.getAllWagons().size());
    }

    @Test
    public void testGetAllWagons() {
        controller.createWagon(wagon1);
        controller.createWagon(wagon2);
        controller.createWagon(wagon3);

        List<Wagon> wagons = controller.getAllWagons();

        assertEquals(3, wagons.size());
        assertTrue(wagons.contains(wagon1));
        assertTrue(wagons.contains(wagon2));
        assertTrue(wagons.contains(wagon3));
    }

    @Test
    public void testGetAllWagonsEmpty() {
        List<Wagon> wagons = controller.getAllWagons();

        assertNotNull(wagons);
        assertEquals(0, wagons.size());
    }

    @Test
    public void testGetWagonByIdFound() {
        controller.createWagon(wagon1);
        controller.createWagon(wagon2);

        Optional<Wagon> result = controller.getWagonById(1);

        assertTrue(result.isPresent());
        assertEquals(wagon1, result.get());
    }

    @Test
    public void testGetWagonByIdNotFound() {
        controller.createWagon(wagon1);

        Optional<Wagon> result = controller.getWagonById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateWagonSuccess() {
        controller.createWagon(wagon1);

        Company company1 = new Company(1, "Rail Company", "RC", "123456789");
        User operator1 = new User(1, "user1", "user1@rail.com", "pass123", company1, true);
        Wagon updatedWagon = new Wagon(1, wagonModel1, operator1, 2023);

        boolean result = controller.updateWagon(1, updatedWagon);

        assertTrue(result);
        Optional<Wagon> retrievedWagon = controller.getWagonById(1);
        assertTrue(retrievedWagon.isPresent());
        assertEquals(2023, retrievedWagon.get().getServiceYear().intValue());
    }

    @Test
    public void testUpdateWagonNotFound() {
        controller.createWagon(wagon1);

        boolean result = controller.updateWagon(999, wagon2);

        assertFalse(result);
    }

    @Test
    public void testDeleteWagonSuccess() {
        controller.createWagon(wagon1);
        controller.createWagon(wagon2);
        controller.createWagon(wagon3);

        boolean result = controller.deleteWagon(2);

        assertTrue(result);
        assertEquals(2, controller.getAllWagons().size());
        assertFalse(controller.getWagonById(2).isPresent());
    }

    @Test
    public void testDeleteWagonNotFound() {
        controller.createWagon(wagon1);

        boolean result = controller.deleteWagon(999);

        assertFalse(result);
        assertEquals(1, controller.getAllWagons().size());
    }

    @Test
    public void testGetNextWagonId() {
        Integer nextId = controller.getNextWagonId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testClearAllWagons() {
        controller.createWagon(wagon1);
        controller.createWagon(wagon2);
        controller.createWagon(wagon3);
        assertEquals(3, controller.getAllWagons().size());

        controller.clearAllWagons();

        assertEquals(0, controller.getAllWagons().size());
    }

    @Test
    public void testConstructor() {
        WagonController newController = new WagonController();

        assertNotNull(newController);
        assertNotNull(newController.getAllWagons());
    }
}

