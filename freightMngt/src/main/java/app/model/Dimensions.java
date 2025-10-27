package app.model;

/**
 * Represents the physical dimensions of an object in the freight management system.
 * This class stores width, length, and height measurements in meters and provides
 * utility methods for calculating volume, area, and perimeter.
 * Used to define the size of items, containers, warehouse spaces, and other physical entities.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class Dimensions {
    private Double width;   // largura em metros
    private Double length;  // comprimento em metros
    private Double height;  // altura em metros

    /**
     * Default constructor that creates Dimensions with null values.
     * Initializes all dimensional fields to null.
     */

    public Dimensions() {
        this.width = null;
        this.length = null;
        this.height = null;
    }

    /**
     * Parameterized constructor that creates Dimensions with specified values.
     *
     * @param width the width in meters
     * @param length the length in meters
     * @param height the height in meters
     */

    public Dimensions(Double width, Double length, Double height) {
        this.width = width;
        this.length = length;
        this.height = height;
    }

    /**
     * Gets the width dimension.
     *
     * @return the width as a Double in meters
     */

    public Double getWidth() {
        return width;
    }

    /**
     * Sets the width dimension.
     *
     * @param width the width to set in meters
     */

    public void setWidth(Double width) {
        this.width = width;
    }

    /**
     * Gets the length dimension.
     *
     * @return the length as a Double in meters
     */

    public Double getLength() {
        return length;
    }

    /**
     * Sets the length dimension.
     *
     * @param length the length to set in meters
     */

    public void setLength(Double length) {
        this.length = length;
    }

    /**
     * Gets the height dimension.
     *
     * @return the height as a Double in meters
     */

    public Double getHeight() {
        return height;
    }

    /**
     * Sets the height dimension.
     *
     * @param height the height to set in meters
     */

    public void setHeight(Double height) {
        this.height = height;
    }

    /**
     * Calculates and returns the volume (width * length * height) in cubic meters.
     * If any dimension is unknown (null), returns null.
     *
     * @return the volume in cubic meters, or null if any dimension is null
     */

    public Double getVolume() {
        if (width != null && length != null && height != null) {
            return width * length * height;
        }
        return null;
    }

    /**
     * Calculates and returns the base area (width * length) in square meters.
     * If width or length are unknown (null), returns null.
     *
     * @return the area in square meters, or null if width or length is null
     */

    public Double getArea() {
        if (width != null && length != null) {
            return width * length;
        }
        return null;
    }

    /**
     * Calculates and returns the base perimeter (2 * (width + length)) in meters.
     * If width or length are unknown (null), returns null.
     *
     * @return the perimeter in meters, or null if width or length is null
     */

    public Double getPerimeter() {
        if (width != null && length != null) {
            return 2 * (width + length);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Dimensions{" +
                "width=" + width +
                ", length=" + length +
                ", height=" + height +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Dimensions that = (Dimensions) obj;

        if (width != null ? !width.equals(that.width) : that.width != null) return false;
        if (length != null ? !length.equals(that.length) : that.length != null) return false;
        return height != null ? height.equals(that.height) : that.height == null;
    }

    @Override
    public int hashCode() {
        int result = width != null ? width.hashCode() : 0;
        result = 31 * result + (length != null ? length.hashCode() : 0);
        result = 31 * result + (height != null ? height.hashCode() : 0);
        return result;
    }
}
