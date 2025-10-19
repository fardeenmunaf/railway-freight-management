package app.model;

/**
 * Represents a pick task in the freight management system.
 * <p>
 * Contains detailed information about individual picking operations including
 * the associated pick wave, order line, balance tracking, quantity specifications,
 * and position sequencing. Used to manage specific item picking activities
 * within warehouse operations.
 * </p>
 */
public class PickTask {
    private Integer pickTaskId;
    private PickWave pickWave;
    private OrderLine orderLine;
    private Integer balance;
    private Double qty;
    private Integer positionSequence;

    /**
     * Default constructor for PickTask.
     * Initializes all fields to null.
     */
    public PickTask() {
        this.pickTaskId = null;
        this.pickWave = null;
        this.orderLine = null;
        this.balance = null;
        this.qty = null;
        this.positionSequence = null;
    }

    /**
     * Parameterized constructor for PickTask.
     *
     * @param pickTaskId the unique identifier for the pick task
     * @param pickWave the pick wave this task belongs to
     * @param orderLine the order line associated with this pick task
     * @param balance the balance or remaining quantity to be picked
     * @param qty the quantity to be picked for this task
     * @param positionSequence the sequence position for optimized picking route
     */
    public PickTask(Integer pickTaskId, PickWave pickWave, OrderLine orderLine, Integer balance, Double qty, Integer positionSequence) {
        this.pickTaskId = pickTaskId;
        this.pickWave = pickWave;
        this.orderLine = orderLine;
        this.balance = balance;
        this.qty = qty;
        this.positionSequence = positionSequence;
    }

    /**
     * Gets the pick task identifier.
     *
     * @return the pick task ID
     */
    public Integer getPickTaskId() {
        return pickTaskId;
    }

    /**
     * Sets the pick task identifier.
     *
     * @param pickTaskId the pick task ID to set
     */
    public void setPickTaskId(Integer pickTaskId) {
        this.pickTaskId = pickTaskId;
    }

    /**
     * Gets the associated pick wave.
     *
     * @return the pick wave this task belongs to
     */
    public PickWave getPickWave() {
        return pickWave;
    }

    /**
     * Sets the associated pick wave.
     *
     * @param pickWave the pick wave to associate with this task
     */
    public void setPickWave(PickWave pickWave) {
        this.pickWave = pickWave;
    }

    /**
     * Gets the associated order line.
     *
     * @return the order line for this pick task
     */
    public OrderLine getOrderLine() {
        return orderLine;
    }

    /**
     * Sets the associated order line.
     *
     * @param orderLine the order line to associate with this task
     */
    public void setOrderLine(OrderLine orderLine) {
        this.orderLine = orderLine;
    }

    /**
     * Gets the balance or remaining quantity.
     *
     * @return the balance to be picked
     */
    public Integer getBalance() {
        return balance;
    }

    /**
     * Sets the balance or remaining quantity.
     *
     * @param balance the balance to set
     */
    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    /**
     * Gets the quantity to be picked.
     *
     * @return the quantity for this pick task
     */
    public Double getQty() {
        return qty;
    }

    /**
     * Sets the quantity to be picked.
     *
     * @param qty the quantity to set for this pick task
     */
    public void setQty(Double qty) {
        this.qty = qty;
    }

    /**
     * Gets the position sequence for picking optimization.
     *
     * @return the position sequence number
     */
    public Integer getPositionSequence() {
        return positionSequence;
    }

    /**
     * Sets the position sequence for picking optimization.
     *
     * @param positionSequence the position sequence to set
     */
    public void setPositionSequence(Integer positionSequence) {
        this.positionSequence = positionSequence;
    }

    @Override
    public String toString() {
        return "PickTask{" +
                "pickTaskId=" + pickTaskId +
                ", pickWave=" + pickWave +
                ", orderLine=" + orderLine +
                ", balance=" + balance +
                ", qty=" + qty +
                ", positionSequence=" + positionSequence +
                '}';
    }
}
