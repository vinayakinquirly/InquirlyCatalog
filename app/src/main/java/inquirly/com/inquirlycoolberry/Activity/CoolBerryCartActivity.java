package inquirly.com.inquirlycoolberry.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import java.util.ArrayList;
import java.util.HashMap;

import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;
import android.graphics.Typeface;
import android.widget.RelativeLayout;
import inquirly.com.inquirlycatalogue.R;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import inquirly.com.inquirlycatalogue.models.BillResponse;
import inquirly.com.inquirlycatalogue.models.CampaignDbItem;
import inquirly.com.inquirlycatalogue.models.CartItem;
import inquirly.com.inquirlycatalogue.models.Fields;
import inquirly.com.inquirlycatalogue.models.ItemBillReq;
import inquirly.com.inquirlycatalogue.models.PlaceOrderRes;
import inquirly.com.inquirlycatalogue.rest.ApiRequest;
import inquirly.com.inquirlycatalogue.rest.IRequestCallback;
import inquirly.com.inquirlycatalogue.utils.ApiConstants;
import inquirly.com.inquirlycatalogue.ApplicationController;
import inquirly.com.inquirlycatalogue.activities.MainActivity;
import inquirly.com.inquirlycatalogue.utils.CatalogSharedPrefs;
import inquirly.com.inquirlycatalogue.utils.InternetConnectionStatus;
import inquirly.com.inquirlycatalogue.utils.RecyclerItemClickListener;
import inquirly.com.inquirlycoolberry.Adapters.CoolberryCartAdapter;

public class CoolBerryCartActivity extends AppCompatActivity {

    private Fields field;
    public Intent intent;
    public int cartCount;
    private float total ;
    private String color=null;
    private ItemBillReq billResponse;
    public ImageView cart_back_image;
    private ProgressDialog billDialog;
    public Button btn_CheckOut,btn_Shop;
    private ArrayList<Fields> fieldList;
    private Bundle bundle = new Bundle();
    public ArrayList<CartItem> cartItems;
    public CoolberryCartAdapter mAdapter;
    public String bill,itemsData,propsJson;
    private static RecyclerView mRecyclerView;
    public  static String mCampaignId,back_image;
    public  static TextView mTxtTotalPrice,noItems;
    private ArrayList<ItemBillReq.Items> itemsList;
    private ArrayList<ItemBillReq.Items> itemDetails;
    public SharedPreferences prefs,sharedPreferences;
    public String catalougeView,catalog_group,sec_token;
    private static final String TAG = "CoolBerryCartActivity";
    private ArrayList<CampaignDbItem> dbItem = new ArrayList<>();
    private ArrayList<ItemBillReq.Items> itemList = new ArrayList<>();
    private ArrayList<BillResponse.Taxes>  billTaxs = new ArrayList<>();
    private ArrayList<BillResponse.BillItems> billItems = new ArrayList<>();
    private ApplicationController instance = ApplicationController.getInstance();
//    public static HashMap<String, ArrayList<Fields>> propertyList = new HashMap<>();
    private ApplicationController appInstance = ApplicationController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTheme(R.style.CoolberryTheme);
        super.onCreate(savedInstanceState);
        color = appInstance.getImage("color_1");
        setContentView(R.layout.activity_coolberry_cart);
        intent = getIntent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.food_cart_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor(color));
        billDialog = new ProgressDialog(this);
        cart_back_image = (ImageView)findViewById(R.id.food_cartbackground);

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

        mCampaignId = intent.getStringExtra("campaign_id");
        sharedPreferences = getSharedPreferences(CatalogSharedPrefs.KEY_CUSTOM_THEME, Context.MODE_PRIVATE);
        catalougeView = sharedPreferences.getString(CatalogSharedPrefs.CATALOG_VIEW,null);
        catalog_group = sharedPreferences.getString(CatalogSharedPrefs.CATALOG_GROUP,null);
        back_image = sharedPreferences.getString(CatalogSharedPrefs.BG_IMAGE_1,null);
        Log.i(TAG,"check view----" + catalougeView + "--grp--" + catalog_group);

        //Picasso.with(this).load(back_image).resize(1650,850).placeholder(R.drawable.placeholder_large).into(cart_back_image);

        for(int i = 0; i < toolbar.getChildCount(); i++){
            View view = toolbar.getChildAt(i);
            if(view instanceof TextView){
                TextView tv = (TextView) view;
                if(tv.getText().equals(this.getTitle())){
                    tv.setTypeface(font);
                    tv.setAllCaps(true);
                    break;
                }
            }
        }

        Log.i(TAG,"campaign_id---->" + mCampaignId);
        noItems = (TextView)findViewById(R.id.noItems);
        btn_Shop = (Button) findViewById(R.id.btn_food_shop);
        btn_CheckOut = (Button) findViewById(R.id.btn_food_checkout);

        mTxtTotalPrice = (TextView) findViewById(R.id.food_total_price);
        TextView mCartEmpty = (TextView) findViewById(R.id.food_cart_empty);
        btn_CheckOut.setBackgroundColor(Color.parseColor(color));
        mTxtTotalPrice.setTypeface(font);
        btn_Shop.setTypeface(font);
        btn_CheckOut.setTypeface(font);
        mCartEmpty.setTypeface(font);

        prefs = getSharedPreferences(CatalogSharedPrefs.KEY_NAME, Context.MODE_PRIVATE);
        propsJson = prefs.getString(mCampaignId + "_" + CatalogSharedPrefs.KEY_ITEM_PROPERTIES, null);
        sec_token = prefs.getString(CatalogSharedPrefs.KEY_SEC_TOKEN,null);
        Log.i(TAG,"check propsjson--->" + propsJson + "---" + mCampaignId + "--token--" + sec_token);

        if(propsJson==null){
            instance.deleteAllCartItems();
        }

//        getTotalamount();

        mRecyclerView = (RecyclerView) findViewById(R.id.food_cart_list);
        cartCount = ApplicationController.getInstance().getCartItemCount();
        if (cartCount == 0) {
            Log.i(TAG,"says cart==0");
            cartAmountIsZero();
        } else {
            mCartEmpty.setVisibility(View.GONE);
            btn_CheckOut.setVisibility(View.VISIBLE);
            //mTxtTotalPrice.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            btn_CheckOut.setEnabled(true);
            cartItems = ApplicationController.getInstance().getCartItems();
            Log.i(TAG,"cart item size--->" + cartItems.size());
            mAdapter = new CoolberryCartAdapter(this, cartItems,propsJson);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLayoutManager);
        }

        btn_CheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (InternetConnectionStatus.checkConnection(getApplicationContext())) {
                    if (ApplicationController.getInstance().getCartItemCount() == 0) {
                        Toast.makeText(getApplicationContext(),"Cart cannot be empty...",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        billDialog.setMessage("Please wait! while we generate Bill.");
                        billDialog.setCancelable(false);
                        billDialog.show();
                        buildFieldList(propsJson);
                        createBillJson();
                    }
                }else{
                    Toast.makeText(CoolBerryCartActivity.this, "Unable to connect to server." +
                            " please check your network connection", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    public static void cartAmountIsZero(){
        mRecyclerView.setVisibility(View.GONE);
        noItems.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void buildFieldList(String propsJson){
        Log.i(TAG,"entered buildFieldList");
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
                    field = new Fields();
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
                    Log.i(TAG,"field size---" + field.getType() + "---" + fieldList.size());
                }
                Log.i(TAG,"field--->" + fieldList.size() + fieldList.get(keyIndex).getLabel());
//                propertyList.put(key, fieldList);
//                Log.i(TAG,"propertyList--->" + propertyList.get(0) +"---" + propertyList.size());
            }
        }catch (Exception ex) {
            Log.e(TAG, "Error parsing item properties from shared prefs:" + ex.getMessage());
        }
    }

    public void createBillJson() {
        Gson gson= new Gson();
        int itemNum = ApplicationController.getInstance().getCartItemCount();
        Log.i(TAG,"item Count----" + itemNum + "---" + fieldList.size());
        itemsList = new ArrayList<>();
        itemDetails = new ArrayList<>();
        String attr = "abc";
        for (int i = 0; i < itemNum; i++) {
            ItemBillReq.Items items = new ItemBillReq.Items();
            String itemCode = ApplicationController.getInstance().getCartItems().get(i).getItemCode();
            Log.i(TAG, "itemCode---" + itemCode + "----" + i);
            items.setItemCode(ApplicationController.getInstance().getCartItems().get(i).getItemCode());
            ArrayList<ItemBillReq.ItemDetails> itemDetailsList = new ArrayList<>();

            for (int j = 0; j < fieldList.size(); j++) {
                ItemBillReq.ItemDetails cartItemsDetail = new ItemBillReq.ItemDetails();
                Log.i(TAG, "itemList--1--" + fieldList.get(j).getLabel());
                cartItemsDetail.setAttribute(fieldList.get(j).getLabel());
                cartItemsDetail.setValue(ApplicationController.getInstance().
                        getCartItems().get(i).getItemQuantity());
                itemDetailsList.add(cartItemsDetail);
                items.setItemDetails(itemDetailsList);

                Log.i(TAG, "itemList--2--" + cartItemsDetail.getAttribute());
            }
            itemList.add(items);
            itemsData = gson.toJson(itemList);
        }
        Log.i(TAG, "itemList--JSON--" + itemsData);
        postBillJson();
    }

    public void postBillJson(){
        ApiRequest.postBillJson(
                catalog_group,
                itemsData,
                sec_token,
                new IRequestCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Log.i(TAG, "JSON received----" + response.toString());
                        Gson gson = new Gson();
                        billResponse = gson.fromJson(response.toString(),ItemBillReq.class);
                        billDialog.dismiss();
                        if(billResponse.getResCode()!=200){
                            Toast.makeText(CoolBerryCartActivity.this,"Unable to generate Bill!", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(getIntent());
                        }else {
                            Intent i = new Intent(getApplicationContext(), CustomerFormActivity.class);
                            i.putExtra("propJson", propsJson);
                            i.putExtra("billJson", response.toString());
                            Log.i(TAG, "check props----" + propsJson);
                            startActivity(i);
                            finish();
                        }
                    }
                    @Override
                    public void onError(VolleyError error) {
                        billDialog.dismiss();
                        Toast.makeText(CoolBerryCartActivity.this, "some Error occured. Please try again!", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "error-----" + error.getMessage());
                    }
                }
        );
    }
}