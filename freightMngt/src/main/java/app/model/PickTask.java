package app.model;

public class PickTask {
    private Integer pickTaskId;
    private PickWave pickWave;
    private OrderLine orderLine;
    private Integer balance;
    private Double qty;
    private Integer positionSequence;

    public PickTask() {
        this.pickTaskId = null;
        this.pickWave = null;
        this.orderLine = null;
        this.balance = null;
        this.qty = null;
        this.positionSequence = null;
    }

    public PickTask(Integer pickTaskId, PickWave pickWave, OrderLine orderLine, Integer balance, Double qty, Integer positionSequence) {
        this.pickTaskId = pickTaskId;
        this.pickWave = pickWave;
        this.orderLine = orderLine;
        this.balance = balance;
        this.qty = qty;
        this.positionSequence = positionSequence;
    }

    public Integer getPickTaskId() {
        return pickTaskId;
    }

    public void setPickTaskId(Integer pickTaskId) {
        this.pickTaskId = pickTaskId;
    }

    public PickWave getPickWave() {
        return pickWave;
    }

    public void setPickWave(PickWave pickWave) {
        this.pickWave = pickWave;
    }

    public OrderLine getOrderLine() {
        return orderLine;
    }

    public void setOrderLine(OrderLine orderLine) {
        this.orderLine = orderLine;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public Integer getPositionSequence() {
        return positionSequence;
    }

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
