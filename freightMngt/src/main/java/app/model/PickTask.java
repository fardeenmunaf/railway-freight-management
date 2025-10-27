package app.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a pick task in the freight management system.
 * Each pick task contains detailed information about individual picking operations including
 * the associated pick waves, order line, balance tracking, quantity specifications,
 * and position sequencing. This class is used to manage specific item picking activities
 * within warehouse operations.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class PickTask {
    private Integer pickTaskId;
    private List<PickWave> pickWaves;
    private OrderLine orderLine;
    private Integer balance;
    private Double qty;
    private Integer positionSequence;

    /**
     * Default constructor that creates a PickTask with null values.
     * Initializes pickWaves as an empty list and all other fields to null.
     */

    public PickTask() {
        this.pickTaskId = null;
        this.pickWaves = new ArrayList<>();
        this.orderLine = null;
        this.balance = null;
        this.qty = null;
        this.positionSequence = null;
    }

    /**
     * Parameterized constructor that creates a PickTask with specified values.
     *
     * @param pickTaskId the unique identifier for this pick task
     * @param pickWaves the list of pick waves this task belongs to
     * @param orderLine the order line associated with this pick task
     * @param balance the balance or remaining quantity to be picked
     * @param qty the quantity to be picked for this task
     * @param positionSequence the sequence position for optimized picking route
     */

    public PickTask(Integer pickTaskId, List<PickWave> pickWaves, OrderLine orderLine, Integer balance, Double qty, Integer positionSequence) {
        this.pickTaskId = pickTaskId;
        this.pickWaves = pickWaves != null ? new ArrayList<>(pickWaves) : new ArrayList<>();
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
     * Gets the list of pick waves this task belongs to.
     *
     * @return a copy of the list of pick waves
     */

    public List<PickWave> getPickWaves() {
        return new ArrayList<>(pickWaves);
    }

    /**
     * Sets the list of pick waves this task belongs to.
     *
     * @param pickWaves the list of pick waves to set
     */

    public void setPickWaves(List<PickWave> pickWaves) {
        this.pickWaves = pickWaves != null ? new ArrayList<>(pickWaves) : new ArrayList<>();
    }

    /**
     * Adds a pick wave to this task.
     *
     * @param pickWave the pick wave to add
     */

    public void addPickWave(PickWave pickWave) {
        if (pickWave != null) {
            this.pickWaves.add(pickWave);
        }
    }

    /**
     * Removes a pick wave from this task.
     *
     * @param pickWave the pick wave to remove
     * @return true if the pick wave was removed, false otherwise
     */

    public boolean removePickWave(PickWave pickWave) {
        return this.pickWaves.remove(pickWave);
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
                ", pickWaves=" + pickWaves +
                ", orderLine=" + orderLine +
                ", balance=" + balance +
                ", qty=" + qty +
                ", positionSequence=" + positionSequence +
                '}';
    }
}
