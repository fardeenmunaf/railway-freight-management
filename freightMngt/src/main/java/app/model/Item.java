package app.model;

public class Item {
    private Integer itemId;
    private String sku;
    private String name;
    private ItemCategory itemCategory;
    private String uom;
    private Double volume;
    private Double unitWeight;

    public Item() {
        this.itemId = null;
        this.sku = null;
        this.name = null;
        this.itemCategory = null;
        this.uom = null;
        this.volume = null;
        this.unitWeight = null;
    }

    public Item(Integer itemId, String sku, String name, ItemCategory itemCategory, String uom, Double volume, Double unitWeight) {
        this.itemId = itemId;
        this.sku = sku;
        this.name = name;
        this.itemCategory = itemCategory;
        this.uom = uom;
        this.volume = volume;
        this.unitWeight = unitWeight;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ItemCategory getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(ItemCategory itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Double getUnitWeight() {
        return unitWeight;
    }

    public void setUnitWeight(Double unitWeight) {
        this.unitWeight = unitWeight;
    }

    @Override
    public String toString() {
        return "Item{" +
                "itemId=" + itemId +
                ", sku='" + sku + '\'' +
                ", name='" + name + '\'' +
                ", itemCategory=" + itemCategory +
                ", uom='" + uom + '\'' +
                ", volume=" + volume +
                ", unitWeight=" + unitWeight +
                '}';
    }
}
