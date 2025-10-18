package app.model;

public class PickWave {
    private Integer pickWaveId;
    private Cart cart;
    private String strategy;
    private String status;

    public PickWave() {
        this.pickWaveId = null;
        this.cart = null;
        this.strategy = null;
        this.status = null;
    }

    public PickWave(Integer pickWaveId, Cart cart, String strategy, String status) {
        this.pickWaveId = pickWaveId;
        this.cart = cart;
        this.strategy = strategy;
        this.status = status;
    }

    public Integer getPickWaveId() {
        return pickWaveId;
    }

    public void setPickWaveId(Integer pickWaveId) {
        this.pickWaveId = pickWaveId;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public String getStatus() {
        return status;
    }

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
