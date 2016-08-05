package inquirly.com.inquirlycoolberry.Activity;

import java.util.Map;

import android.graphics.Color;
import android.util.Log;
import android.os.Bundle;
import android.view.View;
import java.util.HashMap;
import org.json.JSONArray;
import java.util.ArrayList;
import org.json.JSONObject;
import android.view.Window;
import com.google.gson.Gson;
import android.widget.Toast;
import android.view.MenuItem;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageView;
import android.graphics.Typeface;
import android.app.ProgressDialog;
import android.view.WindowManager;
import com.squareup.picasso.Picasso;
import com.android.volley.VolleyError;
import inquirly.com.inquirlycatalogue.R;
import android.support.v7.widget.Toolbar;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import inquirly.com.inquirlycatalogue.models.Fields;
import inquirly.com.inquirlycatalogue.rest.ApiRequest;
import inquirly.com.inquirlycatalogue.models.ItemBillReq;
import inquirly.com.inquirlycatalogue.models.BillResponse;
import inquirly.com.inquirlycatalogue.models.PlaceOrderRes;
import inquirly.com.inquirlycatalogue.ApplicationController;
import inquirly.com.inquirlycatalogue.rest.IRequestCallback;
import inquirly.com.inquirlycatalogue.utils.CatalogSharedPrefs;
import inquirly.com.inquirlycatalogue.utils.InternetConnectionStatus;

public class CustomerFormActivity extends AppCompatActivity {

    private String color;
    public Intent intent;
    public Button btn_pay;
    private int pipeline_id;
    private Gson gson = new Gson();
    public static String mCampaignId;
    private ProgressDialog orderDialog;
    private ArrayList<Fields> fieldList;
    private TextView bill_total,view_bill;
    public ArrayList<ItemBillReq.Items> itemsList;
    private ItemBillReq billRes = new ItemBillReq();
    public ArrayList<ItemBillReq.Items> itemDetails;
    private EditText user_name, user_mob, user_email;
    public SharedPreferences prefs, sharedPreferences;
    private BillResponse billResponse = new BillResponse();
    private static final String TAG = "CustomerFormActivity";
    private ArrayList<String> valuesList = new ArrayList<>();
    public ImageView back,form_back_1,form_back_2,form_back_3;
    private ArrayList<String> attributesList = new ArrayList<>();
    private ArrayList<ItemBillReq.Items> itemList = new ArrayList<>();
    private ArrayList<BillResponse.Taxes> taxesList = new ArrayList<>();
    private Map<String, ItemBillReq.Items> itemDetailsMap = new HashMap<>();
    private String itemsJsonList = null, customerString = null,logo_url,bill;
    private ArrayList<BillResponse.BillItems> billItemsList = new ArrayList<>();
    private ApplicationController instance = ApplicationController.getInstance();
    private ApplicationController appInstance = ApplicationController.getInstance();
    public static HashMap<String, ArrayList<Fields>>  propertyList = new HashMap<>();
    private ArrayList<ItemBillReq.CustomerDetails> customerDetailsList = new ArrayList<>();
    private String propsJson,catalog_group, sec_token,image_url,billReceived,taxJsonList,itemRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        intent = getIntent();
        billReceived = intent.getStringExtra("billJson");
        billRes = gson.fromJson(billReceived,ItemBillReq.class);
        color = appInstance.getImage("color_1");

        for(int i=0;i<billRes.getBill().getTaxes().size();i++){
            taxesList.add(billRes.getBill().getTaxes().get(i));
            Log.i(TAG,"taxList---" + taxesList.get(i));
        }

        for(int j=0;j<billRes.getBill().getItems().size();j++){
            billItemsList.add(billRes.getBill().getItems().get(j));
        }

        taxJsonList = gson.toJson(taxesList);
        itemRes = gson.toJson(billItemsList);
        Log.i(TAG,"taxes---" + taxJsonList + "---" + itemRes);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTheme(R.style.CoolberryTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_form);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_customer_form);
        setSupportActionBar(toolbar);

        form_back_1 = (ImageView)findViewById(R.id.form_back_1);
        form_back_2 = (ImageView)findViewById(R.id.form_back_2);
        form_back_3 = (ImageView)findViewById(R.id.form_back_3);
        bill_total = (TextView)findViewById(R.id.billTotal);
        view_bill = (TextView)findViewById(R.id.food_view_bill);
        Picasso.with(this).load(instance.getImage("bg_1")).resize(570,100).into(form_back_1);
        Picasso.with(this).load(instance.getImage("bg_1")).resize(570,100).into(form_back_2);
        Picasso.with(this).load(instance.getImage("bg_1")).resize(570,100).into(form_back_3);

        float total = billRes.getBill().getTotal();
        bill_total.setText(String.valueOf(total));

        propsJson = intent.getStringExtra("propJson");
        Log.i(TAG,"check json---" + propsJson);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "Montserrat-Regular.ttf");
        for(int i = 0; i < toolbar.getChildCount(); i++){
            View view = toolbar.getChildAt(i);
            if(view instanceof TextView){
                TextView tv = (TextView) view;
                if(tv.getText().equals(this.getTitle())) {
                    tv.setTypeface(font);
                    tv.setTextSize(18);
                    tv.setAllCaps(true);
                    break;
                }
            }
        }

        attributesList.add("customerName");
        attributesList.add("customerMobile");
        attributesList.add("customerEmail");

        btn_pay = (Button) findViewById(R.id.btn_pay);
        user_name = (EditText) findViewById(R.id.user_name);
        user_mob = (EditText) findViewById(R.id.user_phone);
        user_email = (EditText) findViewById(R.id.user_email);

        btn_pay.setBackgroundColor(Color.parseColor(color));
        user_name.setTextColor(Color.parseColor(color));
        user_mob.setTextColor(Color.parseColor(color));
        user_email.setTextColor(Color.parseColor(color));
        view_bill.setTextColor(Color.parseColor(color));
        //back = (ImageView)findViewById(R.id.form_back);

        orderDialog = new ProgressDialog(this);

        sharedPreferences = getSharedPreferences(CatalogSharedPrefs.KEY_CUSTOM_THEME, MODE_PRIVATE);
        prefs = getSharedPreferences(CatalogSharedPrefs.KEY_NAME, MODE_PRIVATE);
        image_url = sharedPreferences.getString(CatalogSharedPrefs.BG_IMAGE_1,null);
        logo_url = sharedPreferences.getString(CatalogSharedPrefs.LOGO_LARGE,null);
        Log.i(TAG, "URLS---" + logo_url + "---" + image_url);

        pipeline_id = sharedPreferences.getInt(CatalogSharedPrefs.PIPELINE_ID, 0);
        catalog_group = sharedPreferences.getString(CatalogSharedPrefs.CATALOG_GROUP, null);
        sec_token = prefs.getString(CatalogSharedPrefs.KEY_SEC_TOKEN, null);
        Log.i(TAG, "pipeline_id---" + pipeline_id + "---" + catalog_group + "----" + sec_token);

        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (user_mob.getText().toString().equals(null) ||
                        user_mob.getText().toString().equals("")) {
                    Toast.makeText(CustomerFormActivity.this, "Mobile number is mandatory!", Toast.LENGTH_SHORT).show();
                }else{
                    if((user_mob.getText().length() <10)) {
                        Toast.makeText(CustomerFormActivity.this, "Please check your number!", Toast.LENGTH_SHORT).show();
                    }else{
                        if (InternetConnectionStatus.checkConnection(getApplicationContext())) {
                            orderDialog.setMessage("Please wait! while we place your Order.");
                            orderDialog.setCancelable(false);
                            orderDialog.show();
                            valuesList.add(user_name.getText().toString());
                            valuesList.add(user_mob.getText().toString());
                            valuesList.add(user_email.getText().toString());
                            Log.i(TAG, "values entered----" + valuesList.get(0) + "--" +
                                    valuesList.get(1) + "---" + valuesList.get(2));
                            buildFieldList(propsJson);
                            createBillJson();
                        } else {
                            Toast.makeText(CustomerFormActivity.this, "Unable to connect to server. " +
                                    "please check your network connection", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }
            }
        });

        view_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(CustomerFormActivity.this,ItemBillActivity.class);
                intent.putExtra("billJson",billReceived);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void createBillJson() {
        int itemNum = ApplicationController.getInstance().getCartItemCount();
        itemsList = new ArrayList<>();
        itemDetails = new ArrayList<>();
        for (int i = 0; i < itemNum; i++) {
            ItemBillReq.Items items = new ItemBillReq.Items();
            items.setItemCode(ApplicationController.getInstance().getCartItems().get(i).getItemCode());

            ArrayList<ItemBillReq.ItemDetails> itemDetailsList = new ArrayList<>();
            for (int j = 0; j < fieldList.size(); j++) {
                ItemBillReq.ItemDetails cartItemsDetail = new ItemBillReq.ItemDetails();
                cartItemsDetail.setAttribute(fieldList.get(j).getLabel());
                cartItemsDetail.setValue(ApplicationController.getInstance().
                        getCartItems().get(i).getItemQuantity());

                itemDetailsList.add(cartItemsDetail);
                items.setItemDetails(itemDetailsList);
                Log.i(TAG, "itemList---" + i + "--a--" + itemsJsonList);
            }
            itemList.add(items);
            itemsJsonList = gson.toJson(itemList);
        }
        createCustomerDetails();
    }

    public void createCustomerDetails() {
        Log.i(TAG, "createCustomerDetails entered--size--" + valuesList.size());

        for (int k = 0; k < 3; k++) {
            Log.i(TAG,"check value---" + valuesList.get(k));
            ItemBillReq.CustomerDetails customerDetails = new ItemBillReq.CustomerDetails();
            customerDetails.setAttribute(attributesList.get(k));
            customerDetails.setValue(valuesList.get(k));
            customerDetailsList.add(customerDetails);
            Log.i(TAG, "check customerDetails---" + customerString + "----" +
                    customerDetailsList.size() + "---" + customerDetailsList.get(k).getValue());
        }

        customerString = gson.toJson(customerDetailsList);
        Log.i(TAG, "customer JSON---" + customerString);

        ApiRequest.postOrderJson(
                catalog_group,
                pipeline_id,
                taxJsonList,
                itemRes,
                billRes.getBill().getTotal(),
                customerString,
                itemsJsonList,
                sec_token,
                new IRequestCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Log.i(TAG, "JSON received----" + response.toString());
                        appInstance.deleteAllCartItems();
                        orderDialog.dismiss();
                        PlaceOrderRes placeOrderRes = gson.fromJson(response.toString(), PlaceOrderRes.class);
                        if(placeOrderRes.getStatus()!=200){
                            Toast.makeText(CustomerFormActivity.this, placeOrderRes.getResMessage(), Toast.LENGTH_SHORT).show();
                        }else{
                            Intent i = new Intent(getApplicationContext(), OrderPlacedActivity.class);
                            i.putExtra("large", placeOrderRes.getMessage_large());
                            i.putExtra("small", placeOrderRes.getMessage_small());
                            i.putExtra("back_url",image_url);
                            i.putExtra("logo_url",logo_url);
                            startActivity(i);
                            finish();
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        orderDialog.dismiss();
                        Log.i(TAG, "error-----" + error.getMessage());
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    public void buildFieldList(String propsJson) {
        Log.i(TAG, "entered buildFieldList");
        try {
            JSONObject itemProperties = new JSONObject(propsJson);
            for (int keyIndex = 0; keyIndex < itemProperties.names().length(); keyIndex++) {
                String key = (String) itemProperties.names().get(keyIndex);
                Log.i(TAG, "Getting item properties for key=" + key);
                JSONArray jsonArray = itemProperties.getJSONArray(key);
                fieldList = new ArrayList<>();
                Log.i(TAG, "jsonArray json" + jsonArray.length() + "---jsonArray--" + jsonArray.toString());

                for (int index = 0; index < jsonArray.length(); index++) {
                    //read all the item properties
                    JSONObject obj = jsonArray.getJSONObject(index);
                    Fields field = new Fields();
                    field.setType(obj.getString("type"));
                    field.setLabel(obj.getString("label"));

                    JSONArray optionsArray = obj.getJSONArray("options");
                    String[] options = new String[optionsArray.length()];

                    //loop through options array
                    for (int c = 0; c < optionsArray.length(); c++) {
                        Log.i(TAG, "optionsArray--->" + optionsArray.getString(c));
                        options[c] = optionsArray.getString(c);
                    }

                    field.setOptions(options);
                    fieldList.add(field);
                    Log.i(TAG, "field size---" + field.getType() + "---" + fieldList.size());
                }
                Log.i(TAG, "field--->" + fieldList.size() + fieldList.get(keyIndex).getLabel());
                propertyList.put(key, fieldList);
                Log.i(TAG, "propertyList--->" + propertyList.get(0) + "---" + propertyList.size());
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error parsing item properties from shared prefs:" + ex.getMessage());
        }
    }
}