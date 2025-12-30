package app.model;

/**
 * Represents the data associated with a railway connection (edge) between two stations.
 *
 * An edge has three measures:
 * <ul>
 *   <li><b>distance</b>: the connection's distance in kilometers</li>
 *   <li><b>capacity</b>: maximum flow of trains the connection can handle per planning day</li>
 *   <li><b>cost</b>: metric combining distance, capacity, congestion, and potential penalties/bonuses.
 *       Can be negative.</li>
 * </ul>
 *
 * @author G113 2DK
 * @version 1.0
 */
public class EdgeData {
    private final double distance;  // Distance in kilometers
    private final int capacity;     // Maximum train flow per day
    private final double cost;      // Combined metric (can be negative)

    /**
     * Creates an EdgeData with all three measures.
     *
     * @param distance the connection's distance in kilometers
     * @param capacity maximum flow of trains per planning day
     * @param cost combined metric (distance, capacity, congestion, penalties/bonuses)
     */
    public EdgeData(double distance, int capacity, double cost) {
        this.distance = distance;
        this.capacity = capacity;
        this.cost = cost;
    }

    /**
     * Gets the distance in kilometers.
     *
     * @return distance in km
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Gets the capacity (max trains per day).
     *
     * @return capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Gets the cost metric.
     *
     * @return cost (can be negative)
     */
    public double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return String.format("EdgeData{distance=%.2f km, capacity=%d, cost=%.2f}",
            distance, capacity, cost);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdgeData edgeData = (EdgeData) o;
        return Double.compare(edgeData.distance, distance) == 0 &&
               capacity == edgeData.capacity &&
               Double.compare(edgeData.cost, cost) == 0;
    }

    @Override
    public int hashCode() {
        int result = Double.hashCode(distance);
        result = 31 * result + capacity;
        result = 31 * result + Double.hashCode(cost);
        return result;
    }
}

