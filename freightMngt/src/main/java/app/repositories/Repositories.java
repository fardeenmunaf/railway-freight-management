package app.repositories;


public class Repositories {

    private static Repositories instance;

    private final WarehouseRepository warehouseRepository;
    private final WarehouseAisleRepository warehouseAisleRepository;
    private final WarehouseBayRepository warehouseBayRepository;
    private final ItemRepository itemRepository;
    private final ItemCategoryRepository itemCategoryRepository;
    private final OrderRepository orderRepository;
    private final CompanyRepository companyRepository;
    private final FacilityRepository facilityRepository;
    private final GaugeRepository gaugeRepository;
    private final CartRepository cartRepository;
    private final FacilityFeatureTypeRepository facilityFeatureTypeRepository;
    private final FacilityTypeRepository facilityTypeRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final InventoryBalanceRepository inventoryBalanceRepository;
    private final InventoryLotRepository inventoryLotRepository;
    private final InventoryMoveRepository inventoryMoveRepository;
    private final LineRepository lineRepository;
    private final LocomotiveRepository locomotiveRepository;
    private final WagonRepository wagonRepository;
    private final WagonModelRepository wagonModelRepository;
    private final WagonGaugeRepository wagonGaugeRepository;
    private final UserRoleRepository userRoleRepository;
    private final WagonTypeRepository wagonTypeRepository;
    private final TrainStopRepository trainStopRepository;
    private final TrainRepository trainRepository;
    private final TrackTypeRepository trackTypeRepository;
    private final TrainServiceRepository trainServiceRepository;
    private final TrainServiceCompositionRepository trainServiceCompositionRepository;
    private final ServiceLocomotiveRepository serviceLocomotiveRepository;
    private final QuarantineHoldRepository quarantineHoldRepository;
    private final ReturnOrderRepository returnOrderRepository;
    private final QaInspectionRepository qaInspectionRepository;
    private final ReturnLineRepository returnLineRepository;
    private final PickWaveRepository pickWaveRepository;
    private final PickTaskRepository pickTaskRepository;
    private final ParkingTrackRepository parkingTrackRepository;
    private final ParkingQueueRepository parkingQueueRepository;
    private final OrderLineRepository orderLineRepository;
    private final LocomotiveTypeRepository locomotiveTypeRepository;
    private final LocomotiveGaugeRepository locomotiveGaugeRepository;
    private final LocomotiveEletricRepository locomotiveEletricRepository;
    private final LocomotiveDieselRepository locomotiveDieselRepository;
    private final LineSegmentFacilityRepository lineSegmentFacilityRepository;
    private final LineSegmentRepository lineSegmentRepository;
    private final TrainDispatchRepository trainDispatchRepository;
    private final RoutePointRepository routePointRepository;

    /**
     * The Singleton's constructor should always be private to prevent direct construction calls with the new operator.
     */

    private Repositories() {
        warehouseRepository = new WarehouseRepository();
        warehouseAisleRepository = new WarehouseAisleRepository();
        warehouseBayRepository = new WarehouseBayRepository();
        itemRepository = new ItemRepository();
        itemCategoryRepository = new ItemCategoryRepository();
        orderRepository = new OrderRepository();
        companyRepository = new CompanyRepository();
        facilityRepository = new FacilityRepository();
        gaugeRepository = new GaugeRepository();
        cartRepository = new CartRepository();
        facilityFeatureTypeRepository = new FacilityFeatureTypeRepository();
        facilityTypeRepository = new FacilityTypeRepository();
        userRepository = new UserRepository();
        roleRepository = new RoleRepository();
        inventoryBalanceRepository = new InventoryBalanceRepository();
        inventoryLotRepository = new InventoryLotRepository();
        inventoryMoveRepository = new InventoryMoveRepository();
        lineRepository = new LineRepository();
        locomotiveRepository = new LocomotiveRepository();
        wagonRepository = new WagonRepository();
        wagonModelRepository = new WagonModelRepository();
        wagonGaugeRepository = new WagonGaugeRepository();
        userRoleRepository = new UserRoleRepository();
        wagonTypeRepository = new WagonTypeRepository();
        trainStopRepository = new TrainStopRepository();
        trainRepository = new TrainRepository();
        trackTypeRepository = new TrackTypeRepository();
        trainServiceRepository = new TrainServiceRepository();
        trainServiceCompositionRepository = new TrainServiceCompositionRepository();
        serviceLocomotiveRepository = new ServiceLocomotiveRepository();
        quarantineHoldRepository = new QuarantineHoldRepository();
        returnOrderRepository = new ReturnOrderRepository();
        qaInspectionRepository = new QaInspectionRepository();
        returnLineRepository = new ReturnLineRepository();
        pickWaveRepository = new PickWaveRepository();
        pickTaskRepository = new PickTaskRepository();
        parkingTrackRepository = new ParkingTrackRepository();
        parkingQueueRepository = new ParkingQueueRepository();
        orderLineRepository = new OrderLineRepository();
        locomotiveTypeRepository = new LocomotiveTypeRepository();
        locomotiveGaugeRepository = new LocomotiveGaugeRepository();
        locomotiveEletricRepository = new LocomotiveEletricRepository();
        locomotiveDieselRepository = new LocomotiveDieselRepository();
        lineSegmentFacilityRepository = new LineSegmentFacilityRepository();
        lineSegmentRepository = new LineSegmentRepository();
        trainDispatchRepository = new TrainDispatchRepository();
        routePointRepository = new RoutePointRepository();
    }

    /**
     * This is the static method that controls the access to the singleton instance.
     * On the first run, it creates a singleton object and places it into the static attribute.
     * On subsequent runs, it returns the existing object stored in the static attribute.
     */
    public static Repositories getInstance() {
        if (instance == null) {
            instance = new Repositories();
        }
        return instance;
    }

    public WarehouseRepository getWarehouseRepository() {
        return warehouseRepository;
    }

    public WarehouseAisleRepository getWarehouseAisleRepository() {
        return warehouseAisleRepository;
    }

    public ItemRepository getItemRepository() {
        return itemRepository;
    }

    public ItemCategoryRepository getItemCategoryRepository() {
        return itemCategoryRepository;
    }

    public WarehouseBayRepository getWarehouseBayRepository() {
        return warehouseBayRepository;
    }

    public OrderRepository getOrderRepository() {
        return orderRepository;
    }

    public CompanyRepository getCompanyRepository() {
        return companyRepository;
    }

    public FacilityRepository getFacilityRepository() {
        return facilityRepository;
    }

    public GaugeRepository getGaugeRepository() {
        return gaugeRepository;
    }

    public CartRepository getCartRepository() {
        return cartRepository;
    }

    public FacilityFeatureTypeRepository getFacilityFeatureTypeRepository() {
        return facilityFeatureTypeRepository;
    }

    public FacilityTypeRepository getFacilityTypeRepository() {
        return facilityTypeRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public RoleRepository getRoleRepository() {
        return roleRepository;
    }

    public InventoryBalanceRepository getInventoryBalanceRepository() {
        return inventoryBalanceRepository;
    }

    public InventoryLotRepository getInventoryLotRepository() {
        return inventoryLotRepository;
    }

    public InventoryMoveRepository getInventoryMoveRepository() {
        return inventoryMoveRepository;
    }

    public LineRepository getLineRepository() {
        return lineRepository;
    }

    public LocomotiveRepository getLocomotiveRepository() {
        return locomotiveRepository;
    }

    public WagonRepository getWagonRepository() {
        return wagonRepository;
    }

    public WagonModelRepository getWagonModelRepository() {
        return wagonModelRepository;
    }

    public WagonGaugeRepository getWagonGaugeRepository() {
        return wagonGaugeRepository;
    }

    public UserRoleRepository getUserRoleRepository() {
        return userRoleRepository;
    }

    public WagonTypeRepository getWagonTypeRepository() {
        return wagonTypeRepository;
    }

    public TrainStopRepository getTrainStopRepository() {
        return trainStopRepository;
    }

    public TrainRepository getTrainRepository() {
        return trainRepository;
    }

    public TrackTypeRepository getTrackTypeRepository() {
        return trackTypeRepository;
    }

    public TrainServiceRepository getTrainServiceRepository() {
        return trainServiceRepository;
    }

    public TrainServiceCompositionRepository getTrainServiceCompositionRepository() {
        return trainServiceCompositionRepository;
    }

    public ServiceLocomotiveRepository getServiceLocomotiveRepository() {
        return serviceLocomotiveRepository;
    }

    public QuarantineHoldRepository getQuarantineHoldRepository() {
        return quarantineHoldRepository;
    }

    public ReturnOrderRepository getReturnOrderRepository() {
        return returnOrderRepository;
    }

    public QaInspectionRepository getQaInspectionRepository() {
        return qaInspectionRepository;
    }

    public ReturnLineRepository getReturnLineRepository() {
        return returnLineRepository;
    }

    public PickWaveRepository getPickWaveRepository() {
        return pickWaveRepository;
    }

    public PickTaskRepository getPickTaskRepository() {
        return pickTaskRepository;
    }

    public ParkingTrackRepository getParkingTrackRepository() {
        return parkingTrackRepository;
    }

    public ParkingQueueRepository getParkingQueueRepository() {
        return parkingQueueRepository;
    }

    public OrderLineRepository getOrderLineRepository() {
        return orderLineRepository;
    }

    public LocomotiveTypeRepository getLocomotiveTypeRepository() {
        return locomotiveTypeRepository;
    }

    public LocomotiveGaugeRepository getLocomotiveGaugeRepository() {
        return locomotiveGaugeRepository;
    }

    public LocomotiveEletricRepository getLocomotiveEletricRepository() {
        return locomotiveEletricRepository;
    }

    public LocomotiveDieselRepository getLocomotiveDieselRepository() {
        return locomotiveDieselRepository;
    }

    public LineSegmentFacilityRepository getLineSegmentFacilityRepository() {
        return lineSegmentFacilityRepository;
    }

    public LineSegmentRepository getLineSegmentRepository() {
        return lineSegmentRepository;
    }

    public TrainDispatchRepository getTrainDispatchRepository() {
        return trainDispatchRepository;
    }

    public RoutePointRepository getRoutePointRepository() {
        return routePointRepository;
    }
}
