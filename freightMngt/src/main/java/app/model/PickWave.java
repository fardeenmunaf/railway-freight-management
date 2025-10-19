package app.model;

/**
 * Represents a pick wave in the freight management system.
 * Each pick wave contains information about picking operations including the assigned cart,
 * picking strategy, and current status. This class is used to organize and optimize
 * warehouse picking activities by grouping related pick tasks.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class PickWave {
    private Integer pickWaveId;
    private Cart cart;
    private String strategy;
    private String status;

    /**
     * Default constructor that creates a PickWave with null values.
     * Initializes all fields to null.
     */

    public PickWave() {
        this.pickWaveId = null;
        this.cart = null;
        this.strategy = null;
        this.status = null;
    }

    /**
     * Parameterized constructor that creates a PickWave with specified values.
     *
     * @param pickWaveId the unique identifier for this pick wave
     * @param cart the cart assigned to this pick wave
     * @param strategy the picking strategy to be used
     * @param status the current status of the pick wave
     */

    public PickWave(Integer pickWaveId, Cart cart, String strategy, String status) {
        this.pickWaveId = pickWaveId;
        this.cart = cart;
        this.strategy = strategy;
        this.status = status;
    }

    /**
     * Gets the unique identifier of this pick wave.
     *
     * @return the pick wave identifier as an Integer
     */

    public Integer getPickWaveId() {
        return pickWaveId;
    }

    /**
     * Sets the unique identifier of this pick wave.
     *
     * @param pickWaveId the pick wave identifier to set
     */

    public void setPickWaveId(Integer pickWaveId) {
        this.pickWaveId = pickWaveId;
    }

    /**
     * Gets the cart assigned to this pick wave.
     *
     * @return the cart as a Cart object
     */

    public Cart getCart() {
        return cart;
    }

    /**
     * Sets the cart assigned to this pick wave.
     *
     * @param cart the cart to set
     */

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    /**
     * Gets the picking strategy.
     *
     * @return the picking strategy as a String
     */

    public String getStrategy() {
        return strategy;
    }

    /**
     * Sets the picking strategy.
     *
     * @param strategy the picking strategy to set
     */

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    /**
     * Gets the current status of this pick wave.
     *
     * @return the status as a String
     */

    public String getStatus() {
        return status;
    }

    /**
     * Sets the current status of this pick wave.
     *
     * @param status the status to set
     */

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "PickWave{" +
                "pickWaveId=" + pickWaveId +
                ", cart=" + cart +
                ", strategy='" + strategy + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
