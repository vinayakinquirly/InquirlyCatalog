package inquirly.com.inquirlycatalogue.models;

import java.util.HashMap;

/**
 * Created by binvij on 18/12/15.
 */
public class OrderItem {
    public CampaignDetails campaignDetails = new CampaignDetails();
    public Form form = new Form();
    public class Form {
        public String item;
        public HashMap<String,String[]>  itemDetails;
        public Customer customer = new Customer();

        public class Customer {
             public String Name;
             public String Email;
             public String Mobile;
             public String Address;
             public String deliveryDate;
             public String tableId;
        }

    }
    public class CampaignDetails {
        public String id;
        public String type;
        public String channel;
    }
}
