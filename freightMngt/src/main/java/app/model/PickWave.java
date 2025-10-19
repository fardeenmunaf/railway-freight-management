package app.model;

/**
 * Represents a pick wave in the freight management system.
 * <p>
 * Contains information about picking operations including the assigned cart,
 * picking strategy, and current status. Used to organize and optimize
 * warehouse picking activities by grouping related pick tasks.
 * </p>
 */
public class PickWave {
    private Integer pickWaveId;
    private Cart cart;
    private String strategy;
    private String status;

    /**
     * Default constructor for PickWave.
     * Initializes all fields to null.
     */
    public PickWave() {
        this.pickWaveId = null;
        this.cart = null;
        this.strategy = null;
        this.status = null;
    }

    /**
     * Parameterized constructor for PickWave.
     *
     * @param pickWaveId the unique identifier for the pick wave
     * @param cart       the cart assigned to this pick wave
     * @param strategy   the picking strategy to be used
     * @param status     the current status of the pick wave
     */
    public PickWave(Integer pickWaveId, Cart cart, String strategy, String status) {
        this.pickWaveId = pickWaveId;
        this.cart = cart;
        this.strategy = strategy;
        this.status = status;
    }

    /**
     * Gets the pick wave identifier.
     *
     * @return the pick wave ID
     */
    public Integer getPickWaveId() {
        return pickWaveId;
    }

    /**
     * Sets the pick wave identifier.
     *
     * @param pickWaveId the pick wave ID to set
     */
    public void setPickWaveId(Integer pickWaveId) {
        this.pickWaveId = pickWaveId;
    }

    /**
     * Gets the assigned cart.
     *
     * @return the cart assigned to this pick wave
     */
    public Cart getCart() {
        return cart;
    }

    /**
     * Sets the assigned cart.
     *
     * @param cart the cart to assign to this pick wave
     */
    public void setCart(Cart cart) {
        this.cart = cart;
    }

    /**
     * Gets the picking strategy.
     *
     * @return the picking strategy
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
     * Gets the pick wave status.
     *
     * @return the current status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the pick wave status.
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
