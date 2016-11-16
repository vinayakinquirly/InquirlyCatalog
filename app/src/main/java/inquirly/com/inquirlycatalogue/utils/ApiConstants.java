package inquirly.com.inquirlycatalogue.utils;

/**
 * Created by binvij on 11/12/15.
 */
public class ApiConstants {

    public static final String API_BASE_URL = "https://stagingsignin.inquirly.com";
//    public static final String API_BASE_URL = "https://beta.inquirly.com/";
    public static final String API_AUTH = API_BASE_URL + "/rest/authenticate";
    public static final String API_CAMPAIGN_LIST = API_BASE_URL + "/campaigns/mobileApi/get_campaigns";
    public static final String API_CAMPAIGN_DETAILS = API_BASE_URL + "/campaigns/mobileApi/campaign_details";
    public static final String API_CAMPAIGN_TYPE = API_BASE_URL + "/campaigns/cafe/config";
    public static final String API_CAFE_ORDER = API_BASE_URL + "/pipeline2/cafe_order";
    public static final String API_CAFE_BILL = API_BASE_URL + "/campaigns/billing/generate_bill";
    public static final String API_CAFE_UPDATE = API_BASE_URL + "/campaigns/cafe/updates";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";
    public static final String ACCEPT = "Accept";

    public static final String CAMPAIGN_TYPE = "campaignType";
    public static final String CAMPAIGN_TYPE_FEEDBACK="FEEDBACK";
    public static final String CAMPAIGN_TYPE_CATALOG="CATALOG";
    public static final String CAMPAIGN_FEEDBACK_LINK = "FEEDBACK_LINK";
    public static final String CAMPAIGN_ID = "CAMPAIGN_ID";
}
