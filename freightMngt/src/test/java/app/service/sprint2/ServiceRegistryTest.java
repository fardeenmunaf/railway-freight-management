package app.service.sprint2;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ServiceRegistry ensuring that a shared
 * StationService instance is created lazily and reused.
 */
public class ServiceRegistryTest {

    @Test
    public void testGetStationService_NotNullAndSingleton() {
        StationService service1 = ServiceRegistry.getStationService();
        StationService service2 = ServiceRegistry.getStationService();

        assertNotNull(service1, "ServiceRegistry should return a non-null StationService");
        assertSame(service1, service2, "ServiceRegistry should cache and return the same StationService instance");
    }

    @Test
    public void testGetStationService_IndexesAreBuiltOnFirstAccess() {
        StationService service = ServiceRegistry.getStationService();

        // After first access, buildIndexes() should have been called once internally.
        // We can't easily spy on the call here, but we can assert that the spatial tree
        // is available and that accessing it doesn't throw.
        assertDoesNotThrow(() -> {
            assertNotNull(service.getSpatial2DTree());
        }, "Spatial 2D tree should be built on first access via ServiceRegistry");
    }
}

