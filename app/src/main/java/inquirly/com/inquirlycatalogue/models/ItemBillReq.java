package inquirly.com.inquirlycatalogue.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Vinayak on 7/21/2016.
 */
public  class ItemBillReq implements Serializable {

    private String catalog_group;
    private int pipeline_id;
    private BillResponse bill;
    private ArrayList<Items> items;
    private int resCode;
    private String resMessage;
    private CustomerDetails customer_details;

    public String getResMessage() {
        return resMessage;
    }

    public void setResMessage(String resMessage) {
        this.resMessage = resMessage;
    }

    public int getPipeline_id() {
        return pipeline_id;
    }

    public void setPipeline_id(int pipeline_id) {
        this.pipeline_id = pipeline_id;
    }

    public BillResponse getBill() {
        return bill;
    }

    public void setBill(BillResponse bill) {
        this.bill = bill;
    }

    public CustomerDetails getCustomer_details() {
        return customer_details;
    }

    public void setCustomer_details(CustomerDetails customer_details) {
        this.customer_details = customer_details;
    }

    public String getCatalog_group() {
        return catalog_group;
    }

    public void setCatalog_group(String catalog_group) {
        this.catalog_group = catalog_group;
    }

    public ArrayList<Items> getItems() {
        return items;
    }

    public void setItems(ArrayList<Items> items) {
        this.items = items;
    }

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    public static class  Items {
        private String itemCode;
        private ArrayList<ItemDetails> itemDetails;

        public String getItemCode() {
            return itemCode;
        }

        public void setItemCode(String itemCode) {
            this.itemCode = itemCode;
        }

        public ArrayList<ItemDetails> getItemDetails() {
            return itemDetails;
        }

        public void setItemDetails(ArrayList<ItemDetails> itemDetails) {
            this.itemDetails = itemDetails;
        }
    }

    public static class ItemDetails {

        private String attribute;
        private int value;

        public String getAttribute() {
            return attribute;
        }

        public void setAttribute(String attribute) {
            this.attribute = attribute;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public static class CustomerDetails {

        private String attribute;
        private String value;

        public String getAttribute() {
            return attribute;
        }

        public void setAttribute(String attribute) {
            this.attribute = attribute;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
