package inquirly.com.inquirlycatalogue.models;

/**
 * Created by binvij on 2/1/16.
 */
public class PricingModel {

    //key BASIC
    public Properties []Weight;
    public Properties []Eggless;

    public class Properties {
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        private String value;
        private String price;
    }
}