package app.controller;

import app.model.*;
import app.model.Freight.FreightStatus;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Test class for FreightController.
 * Tests freight creation, management, and route assignment functionality.
 */
public class FreightControllerTest {

    private FreightController freightController;
    private FreightRouteController routeController;
    private Facility originFacility;
    private Facility destinationFacility;
    private Item testItem;

    @Before
    public void setUp() {
        // Clear repositories
        Repositories.getInstance().getFreightRepository().clear();
        Repositories.getInstance().getFreightRouteRepository().clear();
        Repositories.getInstance().getFacilityRepository().clear();

        // Initialize controllers
        freightController = new FreightController();
        routeController = new FreightRouteController();

        // Create test facilities
        FacilityType facilityType = new FacilityType(1, "Warehouse");
        originFacility = new Facility(1, "Origin Facility", facilityType, 40.7128, -74.0060, false);
        destinationFacility = new Facility(2, "Destination Facility", facilityType, 34.0522, -118.2437, false);

        Repositories.getInstance().getFacilityRepository().addFacility(originFacility);
        Repositories.getInstance().getFacilityRepository().addFacility(destinationFacility);

        // Create test item
        ItemCategory category = new ItemCategory(1, "General Cargo");
        testItem = new Item(1, category, "SKU001", "Test Item", "UNIT", 100.0, 50.0);
    }

    @Test
    public void testCreateFreight() {
        Freight freight = new Freight(1, "Test Freight", testItem, 1000.0, 10.0,
                originFacility, destinationFacility, FreightStatus.PENDING,
                LocalDateTime.now().plusDays(7), "Handle with care");

        Freight created = freightController.createFreight(freight);

        assertNotNull(created);
        assertEquals(Integer.valueOf(1), created.getFreightId());
        assertEquals("Test Freight", created.getDescription());
        assertEquals(FreightStatus.PENDING, created.getStatus());
    }

    @Test
    public void testGetFreightById() {
        Freight freight = new Freight(1, "Test Freight", testItem, 1000.0, 10.0,
                originFacility, destinationFacility, FreightStatus.PENDING,
                LocalDateTime.now().plusDays(7), null);
        freightController.createFreight(freight);

        Optional<Freight> result = freightController.getFreightById(1);

        assertTrue(result.isPresent());
        assertEquals("Test Freight", result.get().getDescription());
    }

    @Test
    public void testGetFreightByIdNotFound() {
        Optional<Freight> result = freightController.getFreightById(999);
        assertFalse(result.isPresent());
    }

    @Test
    public void testGetAllFreights() {
        Freight freight1 = new Freight(1, "Freight 1", testItem, 1000.0, 10.0,
                originFacility, destinationFacility, FreightStatus.PENDING,
                LocalDateTime.now().plusDays(7), null);
        Freight freight2 = new Freight(2, "Freight 2", testItem, 2000.0, 20.0,
                originFacility, destinationFacility, FreightStatus.PENDING,
                LocalDateTime.now().plusDays(14), null);

        freightController.createFreight(freight1);
        freightController.createFreight(freight2);

        List<Freight> allFreights = freightController.getAllFreights();

        assertEquals(2, allFreights.size());
    }

    @Test
    public void testGetPendingFreights() {
        Freight freight1 = new Freight(1, "Freight 1", testItem, 1000.0, 10.0,
                originFacility, destinationFacility, FreightStatus.PENDING,
                LocalDateTime.now().plusDays(7), null);
        Freight freight2 = new Freight(2, "Freight 2", testItem, 2000.0, 20.0,
                originFacility, destinationFacility, FreightStatus.IN_TRANSIT,
                LocalDateTime.now().plusDays(14), null);

        freightController.createFreight(freight1);
        freightController.createFreight(freight2);

        List<Freight> pendingFreights = freightController.getPendingFreights();

        assertEquals(1, pendingFreights.size());
        assertEquals("Freight 1", pendingFreights.get(0).getDescription());
    }

    @Test
    public void testGetFreightsWithoutRoute() {
        Freight freight1 = new Freight(1, "Freight 1", testItem, 1000.0, 10.0,
                originFacility, destinationFacility, FreightStatus.PENDING,
                LocalDateTime.now().plusDays(7), null);
        Freight freight2 = new Freight(2, "Freight 2", testItem, 2000.0, 20.0,
                originFacility, destinationFacility, FreightStatus.PENDING,
                LocalDateTime.now().plusDays(14), null);

        freightController.createFreight(freight1);
        freightController.createFreight(freight2);

        List<Freight> freightsWithoutRoute = freightController.getFreightsWithoutRoute();

        assertEquals(2, freightsWithoutRoute.size());
    }

    @Test
    public void testAssignRouteToFreight_Success() {
        // Create freight
        Freight freight = new Freight(1, "Test Freight", testItem, 1000.0, 10.0,
                originFacility, destinationFacility, FreightStatus.PENDING,
                LocalDateTime.now().plusDays(7), null);
        freightController.createFreight(freight);

        // Create route
        routeController.createSimpleRoute(1, "Test Route", 1, 2, "Direct route");

        // Assign route to freight
        boolean result = freightController.assignRouteToFreight(1, 1);

        assertTrue(result);
        Optional<Freight> updatedFreight = freightController.getFreightById(1);
        assertTrue(updatedFreight.isPresent());
        assertNotNull(updatedFreight.get().getAssignedRoute());
        assertEquals(Integer.valueOf(1), updatedFreight.get().getAssignedRoute().getRouteId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssignRouteToFreight_FreightNotExists() {
        // Create route
        routeController.createSimpleRoute(1, "Test Route", 1, 2, "Direct route");

        // Try to assign to non-existent freight
        freightController.assignRouteToFreight(999, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssignRouteToFreight_RouteNotExists() {
        // Create freight
        Freight freight = new Freight(1, "Test Freight", testItem, 1000.0, 10.0,
                originFacility, destinationFacility, FreightStatus.PENDING,
                LocalDateTime.now().plusDays(7), null);
        freightController.createFreight(freight);

        // Try to assign non-existent route
        freightController.assignRouteToFreight(1, 999);
    }

    @Test(expected = IllegalStateException.class)
    public void testAssignRouteToFreight_FreightNotPending() {
        // Create freight with IN_TRANSIT status
        Freight freight = new Freight(1, "Test Freight", testItem, 1000.0, 10.0,
                originFacility, destinationFacility, FreightStatus.IN_TRANSIT,
                LocalDateTime.now().plusDays(7), null);
        freightController.createFreight(freight);

        // Create route
        routeController.createSimpleRoute(1, "Test Route", 1, 2, "Direct route");

        // Try to assign route to non-pending freight
        freightController.assignRouteToFreight(1, 1);
    }

    @Test
    public void testRemoveRouteFromFreight() {
        // Create freight
        Freight freight = new Freight(1, "Test Freight", testItem, 1000.0, 10.0,
                originFacility, destinationFacility, FreightStatus.PENDING,
                LocalDateTime.now().plusDays(7), null);
        freightController.createFreight(freight);

        // Create and assign route
        routeController.createSimpleRoute(1, "Test Route", 1, 2, "Direct route");
        freightController.assignRouteToFreight(1, 1);

        // Remove route
        boolean result = freightController.removeRouteFromFreight(1);

        assertTrue(result);
        Optional<Freight> updatedFreight = freightController.getFreightById(1);
        assertTrue(updatedFreight.isPresent());
        assertNull(updatedFreight.get().getAssignedRoute());
    }

    @Test
    public void testUpdateFreightStatus() {
        Freight freight = new Freight(1, "Test Freight", testItem, 1000.0, 10.0,
                originFacility, destinationFacility, FreightStatus.PENDING,
                LocalDateTime.now().plusDays(7), null);
        freightController.createFreight(freight);

        boolean result = freightController.updateFreightStatus(1, FreightStatus.IN_TRANSIT);

        assertTrue(result);
        Optional<Freight> updatedFreight = freightController.getFreightById(1);
        assertTrue(updatedFreight.isPresent());
        assertEquals(FreightStatus.IN_TRANSIT, updatedFreight.get().getStatus());
    }

    @Test
    public void testUpdateFreight() {
        Freight freight = new Freight(1, "Test Freight", testItem, 1000.0, 10.0,
                originFacility, destinationFacility, FreightStatus.PENDING,
                LocalDateTime.now().plusDays(7), null);
        freightController.createFreight(freight);

        freight.setDescription("Updated Freight");
        boolean result = freightController.updateFreight(1, freight);

        assertTrue(result);
        Optional<Freight> updatedFreight = freightController.getFreightById(1);
        assertTrue(updatedFreight.isPresent());
        assertEquals("Updated Freight", updatedFreight.get().getDescription());
    }

    @Test
    public void testDeleteFreight() {
        Freight freight = new Freight(1, "Test Freight", testItem, 1000.0, 10.0,
                originFacility, destinationFacility, FreightStatus.PENDING,
                LocalDateTime.now().plusDays(7), null);
        freightController.createFreight(freight);

        boolean result = freightController.deleteFreight(1);

        assertTrue(result);
        Optional<Freight> deletedFreight = freightController.getFreightById(1);
        assertFalse(deletedFreight.isPresent());
    }

    @Test
    public void testGetNextFreightId() {
        Freight freight1 = new Freight(1, "Freight 1", testItem, 1000.0, 10.0,
                originFacility, destinationFacility, FreightStatus.PENDING,
                LocalDateTime.now().plusDays(7), null);
        Freight freight2 = new Freight(5, "Freight 2", testItem, 2000.0, 20.0,
                originFacility, destinationFacility, FreightStatus.PENDING,
                LocalDateTime.now().plusDays(14), null);

        freightController.createFreight(freight1);
        freightController.createFreight(freight2);

        Integer nextId = freightController.getNextFreightId();

        assertEquals(Integer.valueOf(6), nextId);
    }
}

