package inquirly.com.inquirlycatalogue.models;

import java.util.HashMap;

/**
 * Created by kaushal on 12-12-2015.
 */
public class CartItem {

    private String itemName;
    private String itemImage;
    private int itemQuantity;
    private String itemPrice;
    private String campaignId;
    private String itemCode;
    private String itemType;

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    private HashMap<String,String[]> itemSpecs;

    public void setItemSpecs(HashMap<String,String[]> specs) {
        itemSpecs = specs;
    }

    public HashMap<String,String[]> getItemSpecs(){
        return itemSpecs;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String item_price) {
        this.itemPrice = item_price;
    }
}