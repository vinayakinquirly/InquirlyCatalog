package inquirly.com.inquirlycatalogue;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import inquirly.com.inquirlycatalogue.models.CartItem;
import inquirly.com.inquirlycatalogue.models.Fields;
import inquirly.com.inquirlycatalogue.utils.CatalogSharedPrefs;
import inquirly.com.inquirlycatalogue.utils.LruBitmapCache;
import inquirly.com.inquirlycatalogue.utils.SQLiteDataBase;
import io.branch.referral.Branch;

/**
 * Created by binvij on 11/12/15.
 */
public class ApplicationController extends Application {

    private int position;
    private Context context;
    private SQLiteDataBase mydb;
    private ImageLoader mImageLoader;
    private RequestQueue mRequestQueue;
    private static String mFeedbackLink;
    SharedPreferences sharedPreferences;
    private String itemPresent,itemReceived;
    private ProgressDialog addingItemDialog;
    private  ArrayList<CartItem> mCartItems;
    private static Float mTotalCartAmount = 0.0f;
    private static ApplicationController mInstance;
    private HashSet<String> cartItemName = new HashSet<>();
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