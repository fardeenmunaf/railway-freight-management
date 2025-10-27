package app.controller;

import app.model.Item;
import app.model.OrderLine;
import app.model.PickTask;
import app.service.Allocation;
import app.service.PickRouteOptimizationService;
import app.service.PickingPlan;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.Assert.*;

public class PickRouteControllerTest {

    private static void setPrivateField(Object target, String fieldName, Object value) {
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Before
    public void resetSharedState() {
        // Ensure PickingPlan's shared allocation state doesn't leak across tests
        PickingPlan.resetSharedAllocation();
    }

    // --- Fakes -------------------------------------------------------------

    private static class FakePickingPlan extends PickingPlan {
        private final Map<String, List<Allocation.AllocationRecord>> byKey = new HashMap<>();

        void whenGetItemLocationsForOrder(Integer orderId, List<Allocation.AllocationRecord> records) {
            byKey.put("order:" + orderId, records);
        }

        void whenGetItemLocationsForSku(Integer sku, List<Allocation.AllocationRecord> records) {
            byKey.put("sku:" + sku, records);
        }

        @Override
        public List<Allocation.AllocationRecord> getItemLocations(Integer sku, Integer orderId) {
            if (orderId != null) {
                return new ArrayList<>(byKey.getOrDefault("order:" + orderId, Collections.emptyList()));
            }
            if (sku != null) {
                return new ArrayList<>(byKey.getOrDefault("sku:" + sku, Collections.emptyList()));
            }
            return Collections.emptyList();
        }
    }

    private static class FakeRouteService extends PickRouteOptimizationService {
        int callCount = 0;

        @Override
        public Map<String, RouteResult> optimizeRoute(List<BayLocation> bays) {
            callCount++;
            Map<String, RouteResult> m = new HashMap<>();
            m.put("fake", new RouteResult("fake", new ArrayList<>(), 0));
            return m;
        }
    }

    // --- Tests -------------------------------------------------------------

    @Test
    public void optimizeRoute_withTwoBays_returnsBothStrategiesWithExpectedDistanceAndOrder() {
        PickRouteController controller = new PickRouteController();

        List<PickRouteOptimizationService.BayLocation> bays = Arrays.asList(
                new PickRouteOptimizationService.BayLocation("1", 5, 2.0),
                new PickRouteOptimizationService.BayLocation("1", 10, 1.0)
        );

        Map<String, PickRouteOptimizationService.RouteResult> result = controller.optimizeRoute(bays);

        assertNotNull(result);
        assertTrue("strategyA missing", result.containsKey("strategyA"));
        assertTrue("strategyB missing", result.containsKey("strategyB"));

        PickRouteOptimizationService.RouteResult a = result.get("strategyA");
        PickRouteOptimizationService.RouteResult b = result.get("strategyB");

        // Expected distances: Start (0,0) -> (1,5) is 8; then -> (1,10) is 5; total 13
        assertEquals(13, a.getTotalDistance());
        assertEquals(13, b.getTotalDistance());

        // Both strategies should choose (1,5) then (1,10) here
        assertEquals(2, a.getPath().size());
        assertEquals(5, a.getPath().get(0).getBay());
        assertEquals(10, a.getPath().get(1).getBay());

        assertEquals(2, b.getPath().size());
        assertEquals(5, b.getPath().get(0).getBay());
        assertEquals(10, b.getPath().get(1).getBay());
    }

    @Test
    public void calculateRouteForOrder_mergesDuplicateBays_andComputesDistance() {
        PickRouteController controller = new PickRouteController();

        // Inject fake PickingPlan with duplicate bay allocations for same order
        FakePickingPlan fakePlan = new FakePickingPlan();
        List<Allocation.AllocationRecord> orderAllocations = Arrays.asList(
                new Allocation.AllocationRecord(100, 1, 123, 1.0, 1000, "W1-A1", 12),
                new Allocation.AllocationRecord(100, 1, 123, 2.0, 1001, "W1-A1", 12)
        );
        fakePlan.whenGetItemLocationsForOrder(100, orderAllocations);
        setPrivateField(controller, "pickingPlan", fakePlan);

        Map<String, PickRouteOptimizationService.RouteResult> result = controller.calculateRouteForOrder(100);
        assertNotNull(result);
        PickRouteOptimizationService.RouteResult a = result.get("strategyA");
        assertNotNull(a);
        assertEquals(1, a.getPath().size());
        PickRouteOptimizationService.BayLocation loc = a.getPath().get(0);
        assertEquals(12, loc.getBay());
        assertEquals(3.0, loc.getQuantity(), 0.0001); // merged qty

        // Distance from (0,0) to (1,12): 0 + 3 + 12 = 15
        assertEquals(15, a.getTotalDistance());
    }

    @Test
    public void calculateRouteForSKU_filtersBySku_andHandlesEmpty() {
        PickRouteController controller = new PickRouteController();

        FakePickingPlan fakePlan = new FakePickingPlan();
        List<Allocation.AllocationRecord> sku555 = Arrays.asList(
                new Allocation.AllocationRecord(200, 10, 555, 1.0, 2000, "W1-A1", 5),
                new Allocation.AllocationRecord(200, 11, 555, 2.0, 2001, "W1-A1", 8)
        );
        fakePlan.whenGetItemLocationsForSku(555, sku555);
        fakePlan.whenGetItemLocationsForSku(777, Collections.emptyList());
        setPrivateField(controller, "pickingPlan", fakePlan);

        Map<String, PickRouteOptimizationService.RouteResult> for555 = controller.calculateRouteForSKU(555);
        assertNotNull(for555);
        assertEquals(2, for555.get("strategyA").getPath().size());

        Map<String, PickRouteOptimizationService.RouteResult> for777 = controller.calculateRouteForSKU(777);
        assertNotNull(for777);
        assertTrue(for777.get("strategyA").getPath().isEmpty());
        assertEquals(0, for777.get("strategyA").getTotalDistance());
        assertTrue(for777.get("strategyB").getPath().isEmpty());
        assertEquals(0, for777.get("strategyB").getTotalDistance());
    }

    @Test
    public void calculateRoutesForPickTasks_cachesRoutesPerSku_andMapsByPickTaskId() {
        PickRouteController controller = new PickRouteController();

        // Fake PickingPlan returning allocations per SKU
        FakePickingPlan fakePlan = new FakePickingPlan();
        fakePlan.whenGetItemLocationsForSku(42, Collections.singletonList(
                new Allocation.AllocationRecord(1, 1, 42, 1.0, 3000, "W1-A1", 3)
        ));
        fakePlan.whenGetItemLocationsForSku(43, Collections.singletonList(
                new Allocation.AllocationRecord(1, 2, 43, 2.0, 3001, "W1-A1", 9)
        ));
        setPrivateField(controller, "pickingPlan", fakePlan);

        // Fake route service to count optimizeRoute calls
        FakeRouteService fakeService = new FakeRouteService();
        setPrivateField(controller, "routeService", fakeService);

        // Build PickTasks: two with same SKU (42), one with different SKU (43)
        Item item42 = new Item();
        item42.setItemId(42);
        OrderLine ol1 = new OrderLine();
        ol1.setOrderLineId(101);
        ol1.setSku(item42);
        PickTask t1 = new PickTask();
        t1.setPickTaskId(1001);
        t1.setOrderLine(ol1);

        OrderLine ol2 = new OrderLine();
        ol2.setOrderLineId(102);
        ol2.setSku(item42);
        PickTask t2 = new PickTask();
        t2.setPickTaskId(1002);
        t2.setOrderLine(ol2);

        Item item43 = new Item();
        item43.setItemId(43);
        OrderLine ol3 = new OrderLine();
        ol3.setOrderLineId(103);
        ol3.setSku(item43);
        PickTask t3 = new PickTask();
        t3.setPickTaskId(1003);
        t3.setOrderLine(ol3);

        List<PickTask> tasks = Arrays.asList(t1, t2, t3);
        PickingPlan.PickingPlanResult planResult = new PickingPlan.PickingPlanResult(tasks, new ArrayList<>());

        Map<Integer, Map<String, PickRouteOptimizationService.RouteResult>> results =
                controller.calculateRoutesForPickTasks(planResult);

        // Should have entries for all pick tasks (two SKUs with allocations)
        assertTrue(results.containsKey(1001));
        assertTrue(results.containsKey(1002));
        assertTrue(results.containsKey(1003));

        // For same SKU, the map instance should be reused (cached)
        Map<String, PickRouteOptimizationService.RouteResult> r1 = results.get(1001);
        Map<String, PickRouteOptimizationService.RouteResult> r2 = results.get(1002);
        assertSame(r1, r2);

        // Two distinct SKUs => optimizeRoute should be called twice
        assertEquals(2, fakeService.callCount);

        // And each result should contain the fake key
        assertTrue(results.get(1001).containsKey("fake"));
        assertTrue(results.get(1003).containsKey("fake"));
    }
}
