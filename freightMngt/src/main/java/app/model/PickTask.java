package app.model;

/**
 * Represents a pick task in the freight management system.
 * Each pick task contains detailed information about individual picking operations including
 * the associated pick wave, order line, balance tracking, quantity specifications,
 * and position sequencing. This class is used to manage specific item picking activities
 * within warehouse operations.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class PickTask {
    private Integer pickTaskId;
    private PickWave pickWave;
    private OrderLine orderLine;
    private Integer balance;
    private Double qty;
    private Integer positionSequence;

    /**
     * Default constructor that creates a PickTask with null values.
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
     * Parameterized constructor that creates a PickTask with specified values.
     *
     * @param pickTaskId the unique identifier for this pick task
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
     * Gets the unique identifier of this pick task.
     *
     * @return the pick task identifier as an Integer
     */

    public Integer getPickTaskId() {
        return pickTaskId;
    }

    /**
     * Sets the unique identifier of this pick task.
     *
     * @param pickTaskId the pick task identifier to set
     */

    public void setPickTaskId(Integer pickTaskId) {
        this.pickTaskId = pickTaskId;
    }

    /**
     * Gets the pick wave this task belongs to.
     *
     * @return the pick wave as a PickWave object
     */

    public PickWave getPickWave() {
        return pickWave;
    }

    /**
     * Sets the pick wave this task belongs to.
     *
     * @param pickWave the pick wave to set
     */

    public void setPickWave(PickWave pickWave) {
        this.pickWave = pickWave;
    }

    /**
     * Gets the order line associated with this pick task.
     *
     * @return the order line as an OrderLine object
     */

    public OrderLine getOrderLine() {
        return orderLine;
    }

    /**
     * Sets the order line associated with this pick task.
     *
     * @param orderLine the order line to set
     */

    public void setOrderLine(OrderLine orderLine) {
        this.orderLine = orderLine;
    }

    /**
     * Gets the balance or remaining quantity to be picked.
     *
     * @return the balance as an Integer
     */

    public Integer getBalance() {
        return balance;
    }

    /**
     * Sets the balance or remaining quantity to be picked.
     *
     * @param balance the balance to set
     */

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    /**
     * Gets the quantity to be picked for this task.
     *
     * @return the quantity as a Double
     */

    public Double getQty() {
        return qty;
    }

    /**
     * Sets the quantity to be picked for this task.
     *
     * @param qty the quantity to set
     */

    public void setQty(Double qty) {
        this.qty = qty;
    }

    /**
     * Gets the sequence position for optimized picking route.
     *
     * @return the position sequence as an Integer
     */

    public Integer getPositionSequence() {
        return positionSequence;
    }

    /**
     * Sets the sequence position for optimized picking route.
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
