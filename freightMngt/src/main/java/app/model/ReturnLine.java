package app.model;

public class ReturnLine {

    private Integer returnLineId;
    private ReturnOrder returnOrder;
    private String sku;
    private Integer qty;
    private String condition;

    public ReturnLine() {
        this.returnLineId = null;
        this.returnOrder = null;
        this.sku = null;
        this.qty = null;
        this.condition = null;
    }

    public ReturnLine(Integer returnLineId, ReturnOrder returnOrder, String sku, Integer qty, String condition) {
        this.returnLineId = returnLineId;
        this.returnOrder = returnOrder;
        this.sku = sku;
        this.qty = qty;
        this.condition = condition;
    }

    public Integer getReturnLineId() {
        return returnLineId;
    }

    public void setReturnLineId(Integer returnLineId) {
        this.returnLineId = returnLineId;
    }

    public ReturnOrder getReturnOrder() {
        return returnOrder;
    }

    public void setReturnOrder(ReturnOrder returnOrder) {
        this.returnOrder = returnOrder;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
