package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for ReturnLine model.
 * Tests constructors, getters, setters, and toString with 100% coverage.
 */
public class ReturnLineTest {

    private ReturnLine returnLine;

    @Before
    public void setUp() {
        returnLine = new ReturnLine();
    }

    @Test
    public void testDefaultConstructor() {
        ReturnLine defaultReturnLine = new ReturnLine();

        assertNull("Return Line ID should be null", defaultReturnLine.getReturnLineId());
        assertNull("Return Order should be null", defaultReturnLine.getReturnOrder());
        assertNull("SKU should be null", defaultReturnLine.getSku());
        assertNull("Qty should be null", defaultReturnLine.getQty());
        assertNull("Condition should be null", defaultReturnLine.getCondition());
    }

    @Test
    public void testParameterizedConstructor() {
        ReturnOrder returnOrder = new ReturnOrder();
        ReturnLine paramReturnLine = new ReturnLine(1, returnOrder, "SKU123", 10, "Damaged");

        assertEquals("Return Line ID should be 1", Integer.valueOf(1), paramReturnLine.getReturnLineId());
        assertEquals("Return Order should match", returnOrder, paramReturnLine.getReturnOrder());
        assertEquals("SKU should be 'SKU123'", "SKU123", paramReturnLine.getSku());
        assertEquals("Qty should be 10", Integer.valueOf(10), paramReturnLine.getQty());
        assertEquals("Condition should be 'Damaged'", "Damaged", paramReturnLine.getCondition());
    }

    @Test
    public void testSetAndGetReturnLineId() {
        returnLine.setReturnLineId(10);

        assertEquals("Return Line ID should be 10", Integer.valueOf(10), returnLine.getReturnLineId());
    }

    @Test
    public void testSetAndGetReturnOrder() {
        ReturnOrder returnOrder = new ReturnOrder();
        returnLine.setReturnOrder(returnOrder);

        assertEquals("Return Order should match", returnOrder, returnLine.getReturnOrder());
    }

    @Test
    public void testSetAndGetSku() {
        returnLine.setSku("SKU456");

        assertEquals("SKU should be 'SKU456'", "SKU456", returnLine.getSku());
    }

    @Test
    public void testSetAndGetQty() {
        returnLine.setQty(25);

        assertEquals("Qty should be 25", Integer.valueOf(25), returnLine.getQty());
    }

    @Test
    public void testSetAndGetCondition() {
        returnLine.setCondition("Defective");

        assertEquals("Condition should be 'Defective'", "Defective", returnLine.getCondition());
    }

    @Test
    public void testSetReturnLineIdToNull() {
        returnLine.setReturnLineId(5);
        returnLine.setReturnLineId(null);

        assertNull("Return Line ID should be null", returnLine.getReturnLineId());
    }

    @Test
    public void testSetReturnOrderToNull() {
        ReturnOrder returnOrder = new ReturnOrder();
        returnLine.setReturnOrder(returnOrder);
        returnLine.setReturnOrder(null);

        assertNull("Return Order should be null", returnLine.getReturnOrder());
    }

    @Test
    public void testSetSkuToNull() {
        returnLine.setSku("SKU789");
        returnLine.setSku(null);

        assertNull("SKU should be null", returnLine.getSku());
    }

    @Test
    public void testSetQtyToNull() {
        returnLine.setQty(15);
        returnLine.setQty(null);

        assertNull("Qty should be null", returnLine.getQty());
    }

    @Test
    public void testSetConditionToNull() {
        returnLine.setCondition("Used");
        returnLine.setCondition(null);

        assertNull("Condition should be null", returnLine.getCondition());
    }

    @Test
    public void testConstructorWithNullValues() {
        ReturnLine nullReturnLine = new ReturnLine(null, null, null, null, null);

        assertNull("Return Line ID should be null", nullReturnLine.getReturnLineId());
        assertNull("Return Order should be null", nullReturnLine.getReturnOrder());
        assertNull("SKU should be null", nullReturnLine.getSku());
        assertNull("Qty should be null", nullReturnLine.getQty());
        assertNull("Condition should be null", nullReturnLine.getCondition());
    }

    @Test
    public void testToString() {
        ReturnOrder returnOrder = new ReturnOrder();
        returnLine.setReturnLineId(7);
        returnLine.setReturnOrder(returnOrder);
        returnLine.setSku("SKU999");
        returnLine.setQty(50);
        returnLine.setCondition("Good");

        String result = returnLine.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain returnLineId", result.contains("returnLineId=7"));
        assertTrue("toString should contain sku", result.contains("sku='SKU999'"));
        assertTrue("toString should contain qty", result.contains("qty=50"));
        assertTrue("toString should contain condition", result.contains("condition='Good'"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = returnLine.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'ReturnLine{'", result.contains("ReturnLine{"));
    }
}

