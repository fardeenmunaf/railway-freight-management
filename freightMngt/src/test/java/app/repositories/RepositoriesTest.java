package app.repositories;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for Repositories singleton.
 * Tests singleton pattern implementation and all repository getters.
 */
public class RepositoriesTest {

    private Repositories repositories;

    @Before
    public void setUp() {
        repositories = Repositories.getInstance();
    }

    @Test
    public void testGetInstanceReturnsSameInstance() {
        Repositories instance1 = Repositories.getInstance();
        Repositories instance2 = Repositories.getInstance();

        assertSame("getInstance should return the same instance", instance1, instance2);
    }

    @Test
    public void testGetInstanceNotNull() {
        Repositories instance = Repositories.getInstance();

        assertNotNull("Repositories instance should not be null", instance);
    }

    @Test
    public void testGetWarehouseRepository() {
        WarehouseRepository repository = repositories.getWarehouseRepository();

        assertNotNull("WarehouseRepository should not be null", repository);
    }

    @Test
    public void testGetWarehouseAisleRepository() {
        WarehouseAisleRepository repository = repositories.getWarehouseAisleRepository();

        assertNotNull("WarehouseAisleRepository should not be null", repository);
    }

    @Test
    public void testGetWarehouseBayRepository() {
        WarehouseBayRepository repository = repositories.getWarehouseBayRepository();

        assertNotNull("WarehouseBayRepository should not be null", repository);
    }

    @Test
    public void testGetItemRepository() {
        ItemRepository repository = repositories.getItemRepository();

        assertNotNull("ItemRepository should not be null", repository);
    }

    @Test
    public void testGetItemCategoryRepository() {
        ItemCategoryRepository repository = repositories.getItemCategoryRepository();

        assertNotNull("ItemCategoryRepository should not be null", repository);
    }

    @Test
    public void testGetOrderRepository() {
        OrderRepository repository = repositories.getOrderRepository();

        assertNotNull("OrderRepository should not be null", repository);
    }

    @Test
    public void testGetCompanyRepository() {
        CompanyRepository repository = repositories.getCompanyRepository();

        assertNotNull("CompanyRepository should not be null", repository);
    }

    @Test
    public void testGetFacilityRepository() {
        FacilityRepository repository = repositories.getFacilityRepository();

        assertNotNull("FacilityRepository should not be null", repository);
    }

    @Test
    public void testGetGaugeRepository() {
        GaugeRepository repository = repositories.getGaugeRepository();

        assertNotNull("GaugeRepository should not be null", repository);
    }

    @Test
    public void testGetCartRepository() {
        CartRepository repository = repositories.getCartRepository();

        assertNotNull("CartRepository should not be null", repository);
    }

    @Test
    public void testGetFacilityFeatureTypeRepository() {
        FacilityFeatureTypeRepository repository = repositories.getFacilityFeatureTypeRepository();

        assertNotNull("FacilityFeatureTypeRepository should not be null", repository);
    }

    @Test
    public void testGetFacilityTypeRepository() {
        FacilityTypeRepository repository = repositories.getFacilityTypeRepository();

        assertNotNull("FacilityTypeRepository should not be null", repository);
    }

    @Test
    public void testGetUserRepository() {
        UserRepository repository = repositories.getUserRepository();

        assertNotNull("UserRepository should not be null", repository);
    }

    @Test
    public void testGetRoleRepository() {
        RoleRepository repository = repositories.getRoleRepository();

        assertNotNull("RoleRepository should not be null", repository);
    }

    @Test
    public void testGetInventoryBalanceRepository() {
        InventoryBalanceRepository repository = repositories.getInventoryBalanceRepository();

        assertNotNull("InventoryBalanceRepository should not be null", repository);
    }

    @Test
    public void testGetInventoryLotRepository() {
        InventoryLotRepository repository = repositories.getInventoryLotRepository();

        assertNotNull("InventoryLotRepository should not be null", repository);
    }

    @Test
    public void testGetInventoryMoveRepository() {
        InventoryMoveRepository repository = repositories.getInventoryMoveRepository();

        assertNotNull("InventoryMoveRepository should not be null", repository);
    }

    @Test
    public void testGetLineRepository() {
        LineRepository repository = repositories.getLineRepository();

        assertNotNull("LineRepository should not be null", repository);
    }

    @Test
    public void testGetLocomotiveRepository() {
        LocomotiveRepository repository = repositories.getLocomotiveRepository();

        assertNotNull("LocomotiveRepository should not be null", repository);
    }

    @Test
    public void testGetWagonRepository() {
        WagonRepository repository = repositories.getWagonRepository();

        assertNotNull("WagonRepository should not be null", repository);
    }

    @Test
    public void testGetWagonModelRepository() {
        WagonModelRepository repository = repositories.getWagonModelRepository();

        assertNotNull("WagonModelRepository should not be null", repository);
    }

    @Test
    public void testGetWagonGaugeRepository() {
        WagonGaugeRepository repository = repositories.getWagonGaugeRepository();

        assertNotNull("WagonGaugeRepository should not be null", repository);
    }

    @Test
    public void testGetUserRoleRepository() {
        UserRoleRepository repository = repositories.getUserRoleRepository();

        assertNotNull("UserRoleRepository should not be null", repository);
    }

    @Test
    public void testGetWagonTypeRepository() {
        WagonTypeRepository repository = repositories.getWagonTypeRepository();

        assertNotNull("WagonTypeRepository should not be null", repository);
    }

    @Test
    public void testGetTrainStopRepository() {
        TrainStopRepository repository = repositories.getTrainStopRepository();

        assertNotNull("TrainStopRepository should not be null", repository);
    }

    @Test
    public void testGetTrainRepository() {
        TrainRepository repository = repositories.getTrainRepository();

        assertNotNull("TrainRepository should not be null", repository);
    }

    @Test
    public void testGetTrackTypeRepository() {
        TrackTypeRepository repository = repositories.getTrackTypeRepository();

        assertNotNull("TrackTypeRepository should not be null", repository);
    }

    @Test
    public void testGetTrainServiceRepository() {
        TrainServiceRepository repository = repositories.getTrainServiceRepository();

        assertNotNull("TrainServiceRepository should not be null", repository);
    }

    @Test
    public void testGetTrainServiceCompositionRepository() {
        TrainServiceCompositionRepository repository = repositories.getTrainServiceCompositionRepository();

        assertNotNull("TrainServiceCompositionRepository should not be null", repository);
    }

    @Test
    public void testGetServiceLocomotiveRepository() {
        ServiceLocomotiveRepository repository = repositories.getServiceLocomotiveRepository();

        assertNotNull("ServiceLocomotiveRepository should not be null", repository);
    }

    @Test
    public void testGetQuarantineHoldRepository() {
        QuarantineHoldRepository repository = repositories.getQuarantineHoldRepository();

        assertNotNull("QuarantineHoldRepository should not be null", repository);
    }

    @Test
    public void testGetReturnOrderRepository() {
        ReturnOrderRepository repository = repositories.getReturnOrderRepository();

        assertNotNull("ReturnOrderRepository should not be null", repository);
    }

    @Test
    public void testGetQaInspectionRepository() {
        QaInspectionRepository repository = repositories.getQaInspectionRepository();

        assertNotNull("QaInspectionRepository should not be null", repository);
    }

    @Test
    public void testGetReturnLineRepository() {
        ReturnLineRepository repository = repositories.getReturnLineRepository();

        assertNotNull("ReturnLineRepository should not be null", repository);
    }

    @Test
    public void testGetPickWaveRepository() {
        PickWaveRepository repository = repositories.getPickWaveRepository();

        assertNotNull("PickWaveRepository should not be null", repository);
    }

    @Test
    public void testGetPickTaskRepository() {
        PickTaskRepository repository = repositories.getPickTaskRepository();

        assertNotNull("PickTaskRepository should not be null", repository);
    }

    @Test
    public void testGetParkingTrackRepository() {
        ParkingTrackRepository repository = repositories.getParkingTrackRepository();

        assertNotNull("ParkingTrackRepository should not be null", repository);
    }

    @Test
    public void testGetParkingQueueRepository() {
        ParkingQueueRepository repository = repositories.getParkingQueueRepository();

        assertNotNull("ParkingQueueRepository should not be null", repository);
    }

    @Test
    public void testGetOrderLineRepository() {
        OrderLineRepository repository = repositories.getOrderLineRepository();

        assertNotNull("OrderLineRepository should not be null", repository);
    }

    @Test
    public void testGetLocomotiveTypeRepository() {
        LocomotiveTypeRepository repository = repositories.getLocomotiveTypeRepository();

        assertNotNull("LocomotiveTypeRepository should not be null", repository);
    }

    @Test
    public void testGetLocomotiveGaugeRepository() {
        LocomotiveGaugeRepository repository = repositories.getLocomotiveGaugeRepository();

        assertNotNull("LocomotiveGaugeRepository should not be null", repository);
    }

    @Test
    public void testGetLocomotiveEletricRepository() {
        LocomotiveEletricRepository repository = repositories.getLocomotiveEletricRepository();

        assertNotNull("LocomotiveEletricRepository should not be null", repository);
    }

    @Test
    public void testGetLocomotiveDieselRepository() {
        LocomotiveDieselRepository repository = repositories.getLocomotiveDieselRepository();

        assertNotNull("LocomotiveDieselRepository should not be null", repository);
    }

    @Test
    public void testGetLineSegmentFacilityRepository() {
        LineSegmentFacilityRepository repository = repositories.getLineSegmentFacilityRepository();

        assertNotNull("LineSegmentFacilityRepository should not be null", repository);
    }

    @Test
    public void testGetLineSegmentRepository() {
        LineSegmentRepository repository = repositories.getLineSegmentRepository();

        assertNotNull("LineSegmentRepository should not be null", repository);
    }

    @Test
    public void testAllRepositoriesReturnSameInstance() {
        WarehouseRepository warehouse1 = repositories.getWarehouseRepository();
        WarehouseRepository warehouse2 = repositories.getWarehouseRepository();

        assertSame("Same repository instance should be returned on multiple calls", warehouse1, warehouse2);
    }

    @Test
    public void testSingletonPatternConsistency() {
        Repositories instance1 = Repositories.getInstance();
        Repositories instance2 = Repositories.getInstance();

        WarehouseRepository repo1 = instance1.getWarehouseRepository();
        WarehouseRepository repo2 = instance2.getWarehouseRepository();

        assertSame("Repositories from different singleton instances should be the same", repo1, repo2);
    }
}

