package app.model;

/**
 * Represents the dimensions of an object in meters.
 * <p>
 * Contains width, length and height in meters and
 * utility methods for calculating volume, area and perimeter.
 * </p>
 */
public class Dimensions {
    private Double width;   // largura em metros
    private Double length;  // comprimento em metros
    private Double height;  // altura em metros

    public Dimensions() {
        this.width = null;
        this.length = null;
        this.height = null;
    }

    public Dimensions(Double width, Double length, Double height) {
        this.width = width;
        this.length = length;
        this.height = height;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    /**
     * Returns the volume (width * length * height) in cubic meters, or
     * {@code null} if any dimension is unknown.
     *
     * @return volume in m³ or {@code null}
     */
    public Double getVolume() {
        if (width != null && length != null && height != null) {
            return width * length * height;
        }
        return null;
    }

    /**
     * Returns the base area (width * length) in square meters, or
     * {@code null} if width or length are unknown.
     *
     * @return area in m² or {@code null}
     */
    public Double getArea() {
        if (width != null && length != null) {
            return width * length;
        }
        return null;
    }

    /**
     * Returns the base perimeter (2 * (width + length)) in meters, or
     * {@code null} if width or length are unknown.
     *
     * @return perimeter in m or {@code null}
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
