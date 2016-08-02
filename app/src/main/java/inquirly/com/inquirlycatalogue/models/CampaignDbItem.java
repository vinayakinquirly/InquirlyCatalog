package inquirly.com.inquirlycatalogue.models;

import java.io.Serializable;

/**
 * Created by kaushal on 29-12-2015.
 */
public class CampaignDbItem implements Serializable{

    private int price;
    private String type;
    private String itemCode;
    private String itemName;
    private String item_qty;
    private boolean isActive;
    private String mediaImg1;
    private String mediaImg2;
    private String mediaImg3;
    private String mediaImg4;
    private String mediaImg5;
    private String campaignId;
    private String description;
    private String placeholder;
    private String primaryImage;
    private String categoryName;
    private String offer;
    private String subCategoryName;

    public String getItem_qty() {
        return item_qty;
    }

    public void setItem_qty(String item_qty) {
        this.item_qty = item_qty;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getMediaImg4() {
        return mediaImg4;
    }

    public void setMediaImg4(String mediaImg4) {
        this.mediaImg4 = mediaImg4;
    }

    public String getMediaImg5() {
        return mediaImg5;
    }

    public void setMediaImg5(String mediaimg5) {
        this.mediaImg5 = mediaimg5;
    }

    public String getCampaignId(){
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getMediaImg1() {
        return mediaImg1;
    }

    public void setMediaImg1(String mediaImg1) {
        this.mediaImg1 = mediaImg1;
    }

    public String getMediaImg2() {
        return mediaImg2;
    }

    public void setMediaImg2(String mediaImg2) {
        this.mediaImg2 = mediaImg2;
    }

    public String getMediaImg3() {
        return mediaImg3;
    }

    public void setMediaImg3(String mediaImg3) {
        this.mediaImg3 = mediaImg3;
    }

    public String getPrimaryImage() {
        return primaryImage;
    }

    public void setPrimaryImage(String primaryImage) {
        this.primaryImage = primaryImage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }
}