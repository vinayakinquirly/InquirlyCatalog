package inquirly.com.inquirlycatalogue.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Vinayak on 7/28/2016.
 */
public class BillResponse implements Serializable{

    private ArrayList<BillItems> items;
    private ArrayList<Taxes> taxes;
    private float total;

    public ArrayList<BillItems> getItems() {
        return items;
    }

    public void setItems(ArrayList<BillItems> items) {
        this.items = items;
    }

    public ArrayList<Taxes> getTaxes() {
        return taxes;
    }

    public void setTaxes(ArrayList<Taxes> taxes) {
        this.taxes = taxes;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public static class BillItems {

        private ArrayList<Discounts> discounts;
        private ArrayList<Extras> extras;
        private String image;
        private String item_code;
        private float item_price;
        private String name;
        private int quantity;
        private int serial_num;
        private float total;
        private float total_after_discounts;
        private float total_after_extras;

        public float getItem_price() {
            return item_price;
        }

        public void setItem_price(float item_price) {
            this.item_price = item_price;
        }

        public void setTotal(float total) {
            this.total = total;
        }

        public void setTotal_after_discounts(float total_after_discounts) {
            this.total_after_discounts = total_after_discounts;
        }

        public void setTotal_after_extras(float total_after_extras) {
            this.total_after_extras = total_after_extras;
        }

        public ArrayList<Discounts> getDiscounts() {
            return discounts;
        }

        public void setDiscounts(ArrayList<Discounts> discounts) {
            this.discounts = discounts;
        }

        public ArrayList<Extras> getExtras() {
            return extras;
        }

        public void setExtras(ArrayList<Extras> extras) {
            this.extras = extras;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getItem_code() {
            return item_code;
        }

        public void setItem_code(String item_code) {
            this.item_code = item_code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public int getSerial_num() {
            return serial_num;
        }

        public void setSerial_num(int serial_num) {
            this.serial_num = serial_num;
        }

        public Float getTotal() {
            return total;
        }

        public void setTotal(Float total) {
            this.total = total;
        }

        public Float getTotal_after_discounts() {
            return total_after_discounts;
        }

        public void setTotal_after_discounts(Float total_after_discounts) {
            this.total_after_discounts = total_after_discounts;
        }

        public Float getTotal_after_extras() {
            return total_after_extras;
        }

        public void setTotal_after_extras(Float total_after_extras) {
            this.total_after_extras = total_after_extras;
        }
    }

    public class Discounts {
        private Float amount;
        private String label;

        public Float getAmount() {
            return amount;
        }

        public void setAmount(Float amount) {
            this.amount = amount;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

    public class Extras {
        private Float amount;
        private String label;

        public Float getAmount() {
            return amount;
        }

        public void setAmount(Float amount) {
            this.amount = amount;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

    public static class Taxes {

        private ArrayList<Integer> items;
        private String name;
        private float tax;
        private ArrayList<TaxComponents> tax_components;
        private String tax_grp_id;
        private float taxable_amount;

        public ArrayList<Integer> getItems() {
            return items;
        }

        public void setItems(ArrayList<Integer> items) {
            this.items = items;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public float getTax() {
            return tax;
        }

        public void setTax(float tax) {
            this.tax = tax;
        }

        public ArrayList<TaxComponents> getTax_components() {
            return tax_components;
        }

        public void setTax_components(ArrayList<TaxComponents> tax_components) {
            this.tax_components = tax_components;
        }

        public String getTax_grp_id() {
            return tax_grp_id;
        }

        public void setTax_grp_id(String tax_grp_id) {
            this.tax_grp_id = tax_grp_id;
        }

        public float getTaxable_amount() {
            return taxable_amount;
        }

        public void setTaxable_amount(float taxable_amount) {
            this.taxable_amount = taxable_amount;
        }
    }

    public static class TaxComponents {

        private String bill_label;
        private float tax_amount;
        private String name;
        private float percentage;
        private String tax_amount_str;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Float getPercentage() {
            return percentage;
        }

        public void setPercentage(Float percentage) {
            this.percentage = percentage;
        }

        public String getTax_amount_str() {
            return tax_amount_str;
        }

        public void setTax_amount_str(String tax_amount_str) {
            this.tax_amount_str = tax_amount_str;
        }

        public String getBill_label() {
            return bill_label;
        }

        public void setBill_label(String bill_label) {
            this.bill_label = bill_label;
        }

        public Float getTax_amount() {
            return tax_amount;
        }

        public void setTax_amount(Float tax_amount) {
            this.tax_amount = tax_amount;
        }
    }
}
