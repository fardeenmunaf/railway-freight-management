package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for Dimensions model.
 * Tests constructors, getters, setters, and calculated methods (volume, area, perimeter).
 */
public class DimensionsTest {

    private Dimensions dimensions;

    @Before
    public void setUp() {
        dimensions = new Dimensions();
    }

    @Test
    public void testDefaultConstructor() {
        Dimensions defaultDimensions = new Dimensions();

        assertNull("Width should be null", defaultDimensions.getWidth());
        assertNull("Length should be null", defaultDimensions.getLength());
        assertNull("Height should be null", defaultDimensions.getHeight());
    }

    @Test
    public void testParameterizedConstructor() {
        Dimensions paramDimensions = new Dimensions(2.0, 3.0, 1.5);

        assertEquals("Width should be 2.0", Double.valueOf(2.0), paramDimensions.getWidth());
        assertEquals("Length should be 3.0", Double.valueOf(3.0), paramDimensions.getLength());
        assertEquals("Height should be 1.5", Double.valueOf(1.5), paramDimensions.getHeight());
    }

    @Test
    public void testSetAndGetWidth() {
        dimensions.setWidth(5.0);

        assertEquals("Width should be 5.0", Double.valueOf(5.0), dimensions.getWidth());
    }

    @Test
    public void testSetAndGetLength() {
        dimensions.setLength(10.0);

        assertEquals("Length should be 10.0", Double.valueOf(10.0), dimensions.getLength());
    }

    @Test
    public void testSetAndGetHeight() {
        dimensions.setHeight(3.5);

        assertEquals("Height should be 3.5", Double.valueOf(3.5), dimensions.getHeight());
    }

    @Test
    public void testGetVolumeWithValidDimensions() {
        dimensions.setWidth(2.0);
        dimensions.setLength(3.0);
        dimensions.setHeight(4.0);

        assertEquals("Volume should be 24.0", Double.valueOf(24.0), dimensions.getVolume());
    }

    @Test
    public void testGetVolumeWithNullWidth() {
        dimensions.setWidth(null);
        dimensions.setLength(3.0);
        dimensions.setHeight(4.0);

        assertNull("Volume should be null when width is null", dimensions.getVolume());
    }

    @Test
    public void testGetVolumeWithNullLength() {
        dimensions.setWidth(2.0);
        dimensions.setLength(null);
        dimensions.setHeight(4.0);

        assertNull("Volume should be null when length is null", dimensions.getVolume());
    }

    @Test
    public void testGetVolumeWithNullHeight() {
        dimensions.setWidth(2.0);
        dimensions.setLength(3.0);
        dimensions.setHeight(null);

        assertNull("Volume should be null when height is null", dimensions.getVolume());
    }

    @Test
    public void testGetAreaWithValidDimensions() {
        dimensions.setWidth(5.0);
        dimensions.setLength(4.0);

        assertEquals("Area should be 20.0", Double.valueOf(20.0), dimensions.getArea());
    }

    @Test
    public void testGetAreaWithNullWidth() {
        dimensions.setWidth(null);
        dimensions.setLength(4.0);

        assertNull("Area should be null when width is null", dimensions.getArea());
    }

    @Test
    public void testGetAreaWithNullLength() {
        dimensions.setWidth(5.0);
        dimensions.setLength(null);

        assertNull("Area should be null when length is null", dimensions.getArea());
    }

    @Test
    public void testGetPerimeterWithValidDimensions() {
        dimensions.setWidth(3.0);
        dimensions.setLength(4.0);

        assertEquals("Perimeter should be 14.0", Double.valueOf(14.0), dimensions.getPerimeter());
    }

    @Test
    public void testGetPerimeterWithNullWidth() {
        dimensions.setWidth(null);
        dimensions.setLength(4.0);

        assertNull("Perimeter should be null when width is null", dimensions.getPerimeter());
    }

    @Test
    public void testGetPerimeterWithNullLength() {
        dimensions.setWidth(3.0);
        dimensions.setLength(null);

        assertNull("Perimeter should be null when length is null", dimensions.getPerimeter());
    }

    @Test
    public void testSetDimensionsToNull() {
        dimensions.setWidth(2.0);
        dimensions.setLength(3.0);
        dimensions.setHeight(4.0);

        dimensions.setWidth(null);
        dimensions.setLength(null);
        dimensions.setHeight(null);

        assertNull("Width should be null", dimensions.getWidth());
        assertNull("Length should be null", dimensions.getLength());
        assertNull("Height should be null", dimensions.getHeight());
    }

    @Test
    public void testVolumeWithZeroDimensions() {
        dimensions.setWidth(0.0);
        dimensions.setLength(0.0);
        dimensions.setHeight(0.0);

        assertEquals("Volume should be 0.0", Double.valueOf(0.0), dimensions.getVolume());
    }

    @Test
    public void testAreaWithZeroDimensions() {
        dimensions.setWidth(0.0);
        dimensions.setLength(0.0);

        assertEquals("Area should be 0.0", Double.valueOf(0.0), dimensions.getArea());
    }

    @Test
    public void testPerimeterWithZeroDimensions() {
        dimensions.setWidth(0.0);
        dimensions.setLength(0.0);

        assertEquals("Perimeter should be 0.0", Double.valueOf(0.0), dimensions.getPerimeter());
    }

    @Test
    public void testConstructorWithNullValues() {
        Dimensions nullDimensions = new Dimensions(null, null, null);

        assertNull("Width should be null", nullDimensions.getWidth());
        assertNull("Length should be null", nullDimensions.getLength());
        assertNull("Height should be null", nullDimensions.getHeight());
        assertNull("Volume should be null", nullDimensions.getVolume());
        assertNull("Area should be null", nullDimensions.getArea());
        assertNull("Perimeter should be null", nullDimensions.getPerimeter());
    }

    @Test
    public void testToString() {
        dimensions.setWidth(2.0);
        dimensions.setLength(3.0);
        dimensions.setHeight(4.0);

        String result = dimensions.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain width", result.contains("width=2.0"));
        assertTrue("toString should contain length", result.contains("length=3.0"));
        assertTrue("toString should contain height", result.contains("height=4.0"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = dimensions.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'Dimensions{'", result.contains("Dimensions{"));
    }

    @Test
    public void testEqualsWithSameObject() {
        assertTrue("Same object should be equal", dimensions.equals(dimensions));
    }

    @Test
    public void testEqualsWithNull() {
        assertFalse("Object should not equal null", dimensions.equals(null));
    }

    @Test
    public void testEqualsWithDifferentClass() {
        assertFalse("Object should not equal different class", dimensions.equals("Not a Dimensions"));
    }

    @Test
    public void testEqualsWithEqualDimensions() {
        Dimensions dim1 = new Dimensions(2.0, 3.0, 4.0);
        Dimensions dim2 = new Dimensions(2.0, 3.0, 4.0);

        assertTrue("Equal dimensions should be equal", dim1.equals(dim2));
        assertTrue("Equality should be symmetric", dim2.equals(dim1));
    }

    @Test
    public void testEqualsWithDifferentWidth() {
        Dimensions dim1 = new Dimensions(2.0, 3.0, 4.0);
        Dimensions dim2 = new Dimensions(5.0, 3.0, 4.0);

        assertFalse("Different width should not be equal", dim1.equals(dim2));
    }

    @Test
    public void testEqualsWithDifferentLength() {
        Dimensions dim1 = new Dimensions(2.0, 3.0, 4.0);
        Dimensions dim2 = new Dimensions(2.0, 5.0, 4.0);

        assertFalse("Different length should not be equal", dim1.equals(dim2));
    }

    @Test
    public void testEqualsWithDifferentHeight() {
        Dimensions dim1 = new Dimensions(2.0, 3.0, 4.0);
        Dimensions dim2 = new Dimensions(2.0, 3.0, 5.0);

        assertFalse("Different height should not be equal", dim1.equals(dim2));
    }

    @Test
    public void testEqualsWithNullValues() {
        Dimensions dim1 = new Dimensions(null, null, null);
        Dimensions dim2 = new Dimensions(null, null, null);

        assertTrue("Both null dimensions should be equal", dim1.equals(dim2));
    }

    @Test
    public void testEqualsWithOneNullWidth() {
        Dimensions dim1 = new Dimensions(null, 3.0, 4.0);
        Dimensions dim2 = new Dimensions(2.0, 3.0, 4.0);

        assertFalse("One null width should not be equal", dim1.equals(dim2));
        assertFalse("One null width should not be equal (symmetric)", dim2.equals(dim1));
    }

    @Test
    public void testEqualsWithOneNullLength() {
        Dimensions dim1 = new Dimensions(2.0, null, 4.0);
        Dimensions dim2 = new Dimensions(2.0, 3.0, 4.0);

        assertFalse("One null length should not be equal", dim1.equals(dim2));
        assertFalse("One null length should not be equal (symmetric)", dim2.equals(dim1));
    }

    @Test
    public void testEqualsWithOneNullHeight() {
        Dimensions dim1 = new Dimensions(2.0, 3.0, null);
        Dimensions dim2 = new Dimensions(2.0, 3.0, 4.0);

        assertFalse("One null height should not be equal", dim1.equals(dim2));
        assertFalse("One null height should not be equal (symmetric)", dim2.equals(dim1));
    }

    @Test
    public void testHashCodeConsistency() {
        Dimensions dim = new Dimensions(2.0, 3.0, 4.0);

        int hash1 = dim.hashCode();
        int hash2 = dim.hashCode();

        assertEquals("hashCode should be consistent", hash1, hash2);
    }

    @Test
    public void testHashCodeEqualObjects() {
        Dimensions dim1 = new Dimensions(2.0, 3.0, 4.0);
        Dimensions dim2 = new Dimensions(2.0, 3.0, 4.0);

        assertEquals("Equal objects should have equal hashCodes", dim1.hashCode(), dim2.hashCode());
    }

    @Test
    public void testHashCodeWithNullValues() {
        Dimensions dim1 = new Dimensions(null, null, null);
        Dimensions dim2 = new Dimensions(null, null, null);

        assertEquals("Null dimensions should have equal hashCodes", dim1.hashCode(), dim2.hashCode());
    }

    @Test
    public void testHashCodeWithPartialNulls() {
        Dimensions dim1 = new Dimensions(2.0, null, 4.0);
        Dimensions dim2 = new Dimensions(2.0, null, 4.0);

        assertEquals("Partially null dimensions should have equal hashCodes", dim1.hashCode(), dim2.hashCode());
    }

    @Test
    public void testHashCodeDifferentObjects() {
        Dimensions dim1 = new Dimensions(2.0, 3.0, 4.0);
        Dimensions dim2 = new Dimensions(5.0, 6.0, 7.0);

        assertNotEquals("Different objects should likely have different hashCodes", dim1.hashCode(), dim2.hashCode());
    }
}

