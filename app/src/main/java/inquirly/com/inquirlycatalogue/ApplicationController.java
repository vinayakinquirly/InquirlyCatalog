package inquirly.com.inquirlycatalogue;

import android.os.Build;
import android.util.Log;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import android.text.TextUtils;
import org.json.JSONException;
import android.app.Application;
import android.content.Context;
import io.branch.referral.Branch;
import com.android.volley.Request;
import com.facebook.stetho.Stetho;
import android.annotation.TargetApi;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import android.content.SharedPreferences;
import com.android.volley.toolbox.ImageLoader;
import inquirly.com.inquirlycatalogue.models.Fields;
import inquirly.com.inquirlycatalogue.models.CartItem;
import inquirly.com.inquirlycatalogue.utils.LruBitmapCache;
import inquirly.com.inquirlycatalogue.utils.SQLiteDataBase;
import inquirly.com.inquirlycatalogue.utils.CatalogSharedPrefs;

/**
 * Created by binvij on 11/12/15.
 */
public class ApplicationController extends Application {

    private Context context;
    private SQLiteDataBase mydb;
    private ImageLoader mImageLoader;
    private RequestQueue mRequestQueue;
    private static String mFeedbackLink;
    private  ArrayList<CartItem> mCartItems;
    private SharedPreferences sharedPreferences;
    private static Float mTotalCartAmount = 0.0f;
    private static ApplicationController mInstance;
    public static HashMap<String,ArrayList<Fields>> mItemFields;
    public static final String TAG = ApplicationController.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Branch.getAutoInstance(this);
        mCartItems = new ArrayList<>();
        Stetho.initializeWithDefaults(this);
        context = ApplicationController.this;
        mydb = new SQLiteDataBase(getApplicationContext());
    }

    public static synchronized ApplicationController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public String getFeedbackLink() {
        return mFeedbackLink;
    }

    public void setFeedbackLink(String feedbackLink) {
        mFeedbackLink = feedbackLink;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public ArrayList<CartItem> getListCartItems() {
        return mCartItems;
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    /*-------------------------JUST BAKE METHODS----------------------------------*/

    public void addCartItem(CartItem item) {
        mCartItems.add(item);
        mTotalCartAmount += Float.parseFloat(item.getItemPrice());
    }

    public int getJustCartItemCount(){
        return mCartItems.size();
    }

    public Float getTotalCartAmount(){
        return mTotalCartAmount;
    }

    public Float setTotalCartAmount() {
        mTotalCartAmount = 0.0f;
        return mTotalCartAmount;
    }

    public void removeCartItem(CartItem item) {
        mCartItems.remove(item);
        mTotalCartAmount -= Float.parseFloat(item.getItemPrice());
    }

    public void removeAllCartItems() {
        mCartItems = new ArrayList<>();
    }

    /*-------------------------COOLBERRY METHODS----------------------------------*/

    public void saveItemInDb(CartItem item){
        mydb.open();
        if(mydb.modifyQty(item)){
            Log.i(TAG,"saved successfully");
        }else{
            Log.i(TAG,"some error");
        }
        mydb.close();
    }

    public ArrayList<CartItem> getCartItems() {
        ArrayList<CartItem> items = new ArrayList<>();
        mydb.open();
        int size = mydb.getItemsList().size();
        Log.i(TAG,"getCartItems enetered" + size);
        for(int i=0;i<size;i++){
            items.add(mydb.getItemsList().get(i));
        }
        mydb.close();
        return items;
    }

    public int getCartItemCount(){
        Log.i(TAG,"count item entered");
        mydb.open();
        int count = mydb.getItemsList().size();
        mydb.close();
        return count;
    }

    public void deleteCartItem(CartItem item){
        mydb.open();
        if(mydb.deleteCartItem(item.getItemCode())){
            Log.i(TAG,"delete successfully");
            getCartItems();
        }else{
            Log.i(TAG,"some issue happend");
        }
        mydb.close();
    }

    public void deleteAllCartItems(){
        Log.i(TAG,"delete all entered");
        mydb.open();
        if(mydb.deleteAllItems());
        mydb.close();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public boolean saveCustomItemJson(String itemName, int num, String json) {
        ArrayList<String> customItemJson = new ArrayList<>();
        Log.i(TAG, "save custom item entered--" +customItemJson.size() + "---" + itemName + "---" + num + "---" + json);

        mydb.open();
        if (mydb.getCustomItemList(itemName) != null) {
            String data = mydb.getCustomItemList(itemName);
            try {
                JSONObject jsonObject = new JSONObject(data);
                int dbJsonLength = jsonObject.getJSONArray(itemName).length();
                for (int p =0;p<dbJsonLength;p++){
                    Log.i(TAG,"json added--" + jsonObject.getJSONArray(itemName).getJSONObject(p).toString());
                    customItemJson.add(p,jsonObject.getJSONArray(itemName).getJSONObject(p).toString());
                    Log.i(TAG,"size----->" + customItemJson.size());
                }
                boolean isAdded = false;
                for (int i = 0; i < dbJsonLength; i++) {
                    Log.i(TAG,"--i--" + i + "length--" + String.valueOf(dbJsonLength-1));
                    if (jsonObject.getJSONArray(itemName).getJSONObject(i).
                            getString("itemNum").equals(String.valueOf(num))) {
                        Log.i(TAG,"same item num--" + customItemJson.size() + json);
                        customItemJson.remove(i);
                        Log.i(TAG,"after delete---" + customItemJson.size());
                        customItemJson.add(i,json);
                        Log.i(TAG,"same thing--" + customItemJson.size());
                        isAdded = true;
                    }
                    else if(i==(jsonObject.getJSONArray(itemName).length()-1)) {
                        Log.i(TAG, "else if entered---");
                        if (!isAdded) {
                            customItemJson.add(json);
                        }
                    }
                }
                for(int j=0;j<customItemJson.size();j++) {
                    Log.i(TAG, "check itemListSize--" + customItemJson.get(j));
                }
                JSONArray jsonArray = new JSONArray(customItemJson.toString());
                JSONObject mainObject = new JSONObject();
                mainObject.put(itemName, jsonArray);
                if (mydb.saveCustomItem(itemName, mainObject.toString())) {
                    Log.i(TAG, "custom item saved successfully");
                    mydb.close();
                    return true;
                }else {
                    Log.i(TAG, "some error occurred");
                    mydb.close();
                    return false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            for (int p =0;p<customItemJson.size();p++){
                Log.i(TAG,"------>" + customItemJson.get(p));
                customItemJson.remove(p);
            }
            customItemJson.add(json);
            JSONArray jsonArray = null;
            JSONObject mainObject = null;
            try {
                jsonArray = new JSONArray(customItemJson.toString());
                mainObject = new JSONObject();
                mainObject.put(itemName, jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "check Main String---" + mainObject.toString());
            if (mydb.saveCustomItem(itemName, mainObject.toString())) {
                Log.i(TAG, "custom item saved successfully");
                mydb.close();
                return true;
            } else {
                Log.i(TAG, "some error occurred");
                mydb.close();
                return false;
            }
        }
    }

    public String getCustomItemData(String itemName){
        Log.i(TAG,"load custom item data enetered");
        mydb.open();
        String json=null;
        json=  mydb.getCustomItemList(itemName);
        Log.i(TAG,"check JSON---" + json);
        return json;
    }

    public boolean deleteCustomData(String itemName){
        Log.i(TAG,"entered delete custom data");
        mydb.open();
        if(mydb.deleteCustomData(itemName)){
            return true;
        }
        Log.i(TAG,"some error occured");
        return false;
    }

    /*---------------------------------------------------------------------------*/

    //set item specifications
    public  void setFieldsItem(HashMap<String,ArrayList<Fields>> fields) {
        this.mItemFields = fields;
    }

    public ArrayList<Fields> getFields(String key) {

        ArrayList<Fields> itemFields = mItemFields.get(key);
        return itemFields;
    }

    public String getImage(String image){
        sharedPreferences = getSharedPreferences(CatalogSharedPrefs.KEY_CUSTOM_THEME,MODE_PRIVATE);
        String url,urlfinal = null;
        switch (image){
            case "bg_1":
                url =sharedPreferences.getString(CatalogSharedPrefs.BG_IMAGE_1,null);
                urlfinal =url;
                break;

            case "placeholder":
                url =sharedPreferences.getString(CatalogSharedPrefs.IMAGE_PLACE_HOLDER,null);
                urlfinal =url;
                break;

            case "logo":
                url =sharedPreferences.getString(CatalogSharedPrefs.LOGO_LARGE,null);
                urlfinal =url;
                break;

            case "bg_2":
                url =sharedPreferences.getString(CatalogSharedPrefs.BG_IMAGE_2,null);
                urlfinal =url;
                break;

            case "bg_3":
                url =sharedPreferences.getString(CatalogSharedPrefs.BG_IMAGE_3,null);
                urlfinal =url;
                break;

            case "pipeline":
                url =sharedPreferences.getString(CatalogSharedPrefs.PIPELINE_ID,null);
                urlfinal =url;
                break;

            case "group":
                url =sharedPreferences.getString(CatalogSharedPrefs.CATALOG_GROUP,null);
                urlfinal =url;
                break;

            case "color_1":
                url = sharedPreferences.getString(CatalogSharedPrefs.COLOR_1,null);
                urlfinal = url;
                break;
        }
        return urlfinal;
    }
}