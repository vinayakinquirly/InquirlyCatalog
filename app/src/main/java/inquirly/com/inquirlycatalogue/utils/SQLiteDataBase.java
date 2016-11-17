package inquirly.com.inquirlycatalogue.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

import inquirly.com.inquirlycatalogue.models.CampaignDbItem;
import inquirly.com.inquirlycatalogue.models.CampaignItemData;
import inquirly.com.inquirlycatalogue.models.CartItem;

/**
 * Created by kaushal on 22-12-2015.
 */
public class SQLiteDataBase {

    public static final String DATABASE_NAME = "inquirlycatalogue";
    public static final String CAMPAIGN_TABLE = "campaign_list";
    public static final String CAMPAIGNDETAILS_TABLE = "campaign_details";
    public static final String TABLE_ITEM_NAME = "cart_item_name";
    public static final String TABLE_CUSTOM_ITEM = "custom_item";
    public static final String TABLE_UPDATES = "table_updates";
    public static final String PIPELINE_TABLE = "pipleline";
    public static final String DB_ID = "db_id";
    public static final int DATABASE_VERSION = 3;

    public static final String _ID = "_ID";
    public static final String CAMPAIGN_UUID = "campaignUUID";
    public static final String CAMPAIGN_NAME = "campaignName";
    public static final String PIPELINE_URL = "pipelineUrl";
    public static final String PREVIEW = "previewImg";
    public static final String HASH_TAG = "hashTag";
    public static final String STATE = "state";
    public static final String VALID_TILL = "validTill";
    public static final String UPDATE_ON = "update_on";

    public final String[] ALL_KEYS_ITEMS = new String[] {CART_ITEM_CODE,CART_ITEM_NAME,CART_ITEM_QTY,
            CART_ITEM_PRICE,CART_ITEM_IMAGE,CART_ITEM_TYPE,CART_ITEM_CAMPAIGN_ID};

    public final String[] ALL_UPDATE_KEYS = new String[] {CAMPAIGN_ID,UPDATE_ON};

    public static final String CATEGORY_NAME = "category_name";
    public static final String DESCRIPTION = "description";
    public static final String ISACTIVE = "isActive";
    public static final String PRIMARY_IMAGE = "primaryImg";
    public static final String ITEM_NAME = "itemName";
    public static final String MEDIA1 = "media1";
    public static final String MEDIA2 = "media2";
    public static final String MEDIA3 = "media3";
    public static final String MEDIA4 = "media4";
    public static final String MEDIA5 = "media5";
    public static final String ITEM_CODE = "itemCode";
    public static final String PRICE = "price";
    public static final String TYPE = "type";
    public static final String CAMPAIGN_ID = "campaignId";
    public static final String SUBCATEGORY_NAME = "subcategoryName";

    public static final String SN_NUM = "s.n.o";
    public static final String CART_ITEM_CODE = "item_code";
    public static final String CART_ITEM_NAME = "item_name";
    public static final String CART_ITEM_QTY = "item_qty";
    public static final String CART_ITEM_PRICE = "item_price";
    public static final String CART_ITEM_IMAGE = "item_image";
    public static final String CART_ITEM_TYPE = "item_type";
    public static final String CART_ITEM_CAMPAIGN_ID = "item_campaignId";

    public final String[] ALL_CUSTOM_ITEMS = new String[]{CUSTOM_ITEM_NAME,CUSTOM_ITEM_JSON};
    public final String[] ALL_CAMPAIGN_ITEMS = new String[]{CUSTOM_ITEM_NAME,CUSTOM_ITEM_JSON};

    public static final String CUSTOM_ITEM_NAME = "item_name";
    public static final String CUSTOM_ITEM_JSON = "item_json";

    private static final SQLiteDatabase db = null;

    public static final String TAG = "CatalogueDatabase";

    ArrayList<CampaignItemData> item = new ArrayList<>();
    ArrayList<CampaignDbItem> details = new ArrayList<>();

    SQLiteDatabase database ;
    Context mContext;
    DBHelper Helper;

    public SQLiteDataBase(Context c) {

        this.mContext = c;
    }

    public  class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL("CREATE TABLE " + CAMPAIGN_TABLE + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    CAMPAIGN_UUID + " TEXT," + CAMPAIGN_NAME + " TEXT," + PREVIEW + " TEXT NOT NULL,"
                    + HASH_TAG + " TEXT," + STATE + " TEXT," + VALID_TILL + " TEXT);");

            db.execSQL("CREATE TABLE " + CAMPAIGNDETAILS_TABLE + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + CATEGORY_NAME + " TEXT," + CAMPAIGN_ID + " TEXT," + SUBCATEGORY_NAME + " TEXT," + DESCRIPTION + " TEXT,"
                    + ISACTIVE + " TEXT," + ITEM_CODE + " TEXT," + ITEM_NAME + " TEXT," + MEDIA1 + " TEXT,"
                    + MEDIA2 + " TEXT," + MEDIA3 + " TEXT," + MEDIA4 + " TEXT," + MEDIA5 + " TEXT," + PRIMARY_IMAGE + " TEXT," + TYPE + " TEXT,"
                    + PRICE + " TEXT);");

            db.execSQL(
                    "CREATE TABLE " + TABLE_ITEM_NAME
                            + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + CART_ITEM_CODE + " TEXT NOT NULL, "
                            + CART_ITEM_NAME + " TEXT NOT NULL, "
                            + CART_ITEM_QTY + " INTEGER NOT NULL, "
                            + CART_ITEM_PRICE + " INTEGER NOT NULL, "
                            + CART_ITEM_IMAGE + " TEXT NOT NULL, "
                            + CART_ITEM_TYPE + " TEXT NOT NULL, "
                            + CART_ITEM_CAMPAIGN_ID + " TEXT NOT NULL"
                            + ");");

            db.execSQL(
                    "CREATE TABLE " + TABLE_CUSTOM_ITEM
                            + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + CUSTOM_ITEM_NAME + " TEXT NOT NULL, "
                            + CUSTOM_ITEM_JSON + " TEXT NOT NULL"
                            + ");");

            db.execSQL(
                    "CREATE TABLE " + TABLE_UPDATES
                            + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + CAMPAIGN_ID + " TEXT NOT NULL, "
                            + UPDATE_ON + " TEXT NOT NULL"
                            + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            try {
                db.execSQL("Drop table If Exists " + SQLiteDataBase.CAMPAIGN_TABLE);
                db.execSQL("Drop table If Exists " + SQLiteDataBase.CAMPAIGNDETAILS_TABLE);
                db.execSQL("Drop table If Exists " + SQLiteDataBase.TABLE_ITEM_NAME);
                db.execSQL("Drop table If Exists " + SQLiteDataBase.TABLE_CUSTOM_ITEM);
                db.execSQL("Drop table If Exists " + SQLiteDataBase.TABLE_UPDATES);
                onCreate(db);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public SQLiteDataBase open() throws SQLException {
        Helper = new DBHelper(mContext);
        database = Helper.getWritableDatabase();
        return this;
    }

    public void close() {
        Helper.close();
    }

    public SQLiteDatabase getWritableDatabase() {
        return null;
    }

    public boolean modifyQty(CartItem item) {
        String code = item.getItemCode();
        String where = CART_ITEM_CODE + "= '" + code + "'";
        Cursor cart = database.query(false, TABLE_ITEM_NAME, ALL_KEYS_ITEMS, where, null, null,
                null, null, null);

        Log.i(TAG, "check address---" + cart.getCount() + "---" + item.getItemCode() + "---" + item.getItemQuantity());
        ContentValues cv = new ContentValues();
        if (item.getItemQuantity() != 0) {
            if (cart.getCount() != 0) {
                Log.i(TAG, "if entered");
                String whereToPut = CART_ITEM_CODE + "= '" + item.getItemCode() + "'";
                cv.put(CART_ITEM_QTY, item.getItemQuantity());
                cv.put(CART_ITEM_PRICE, item.getItemPrice());
                //Update the values
                cart.close();
                return database.update(TABLE_ITEM_NAME, cv, whereToPut, null) != 0;
            } else {
                Log.i(TAG, "else entered");
                cv.put(CART_ITEM_CODE, item.getItemCode());
                cv.put(CART_ITEM_NAME, item.getItemName());
                cv.put(CART_ITEM_QTY, item.getItemQuantity());
                cv.put(CART_ITEM_PRICE, item.getItemPrice());
                cv.put(CART_ITEM_IMAGE, (item.getItemImage()));
                cv.put(CART_ITEM_TYPE,(item.getItemType()));
                cv.put(CART_ITEM_CAMPAIGN_ID,(item.getCampaignId()));
                cart.close();
                return database.insertWithOnConflict(TABLE_ITEM_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE) != 0;
            }
        }
        cart.close();
        return false;
    }

    public ArrayList<CartItem> getItemsList(){
        String query = "SELECT * FROM " + TABLE_ITEM_NAME;// + " WHERE " + CART_ITEM_CODE + "=?" ;
        Cursor cursorCart = database.rawQuery(query, null);
        Log.i(TAG,"sql cart count---" + cursorCart.getCount() + "---" + cursorCart.getColumnName(0) +
        "---" + cursorCart.getColumnName(1));

        ArrayList<CartItem> cartItemList = new ArrayList<>();
        if(cursorCart.getCount()!= 0 && cursorCart.moveToFirst()) {
            do{
                CartItem item = new CartItem();
                item.setItemCode(cursorCart.getString(1));
                item.setItemName(cursorCart.getString(2));
                item.setItemQuantity(cursorCart.getInt(3));
                item.setItemPrice(String.valueOf(cursorCart.getInt(4)));
                item.setItemImage(cursorCart.getString(5));
                item.setItemType(cursorCart.getString(6));
                item.setCampaignId(cursorCart.getString(7));
                cartItemList.add(item);

            }while (cursorCart.moveToNext());
            cursorCart.close();
        }
        cursorCart.close();
        return cartItemList;
    }

    public int getCartItemCount(){
        String query = "SELECT * FROM " + TABLE_ITEM_NAME;// + " WHERE " + CART_ITEM_CODE + "=?" ;
        Cursor cursorCart = database.rawQuery(query, null);
        Log.i(TAG,"sql cart count---" + cursorCart.getCount());
        int count = cursorCart.getCount();
        cursorCart.close();
        return count;
    }

    public boolean deleteCartItem(String itemCode){
        Log.i(TAG,"item received----" + itemCode);
        String where = CART_ITEM_CODE + "= '" + itemCode + "'";
        Cursor cart = database.query(false,TABLE_ITEM_NAME,ALL_KEYS_ITEMS,where,null,null,
                null,null,null);
        if (cart.getCount()!=0){
            cart.close();
            return database.delete(TABLE_ITEM_NAME, where, null) != 0;
        }else{
            cart.close();
            Log.i(TAG,"no item with that Code");
            return false;
        }
    }

    public boolean deleteAllItems() {
        return database.delete(TABLE_ITEM_NAME,null,null)!=0;
    }

    public boolean saveCustomItem(String itemName,String json){

        String where = CUSTOM_ITEM_NAME + "= '" + itemName + "'";
        Cursor cart = database.query(false, TABLE_CUSTOM_ITEM, ALL_CUSTOM_ITEMS, where, null, null,
                null, null, null);

        Log.i(TAG, "check address---" + cart.getCount() + "---"+ itemName +"---" +json);
        ContentValues cv = new ContentValues();
        if (cart.getCount() != 0) {
            Log.i(TAG, "if entered");
            String whereToPut = CUSTOM_ITEM_NAME + "= '" + itemName + "'";
            cv.put(CUSTOM_ITEM_JSON,json);
            cart.close();
            return database.update(TABLE_CUSTOM_ITEM, cv, whereToPut, null) != 0;
        } else {
            Log.i(TAG, "else entered");
            cv.put(CUSTOM_ITEM_NAME,itemName);
            cv.put(CUSTOM_ITEM_JSON,json);
            cart.close();
            return database.insertWithOnConflict(TABLE_CUSTOM_ITEM, null, cv, SQLiteDatabase.CONFLICT_REPLACE) != 0;
        }
    }

    public String getCustomItemList(String itemCode){
        Log.i(TAG,"get Item by name---" + itemCode);
        String where = CUSTOM_ITEM_NAME + "= '" + itemCode + "'";
        Cursor customItem = database.query(false, TABLE_CUSTOM_ITEM, ALL_CUSTOM_ITEMS, where, null, null,
                null, null, null);
        String json=null,name=null;
        Log.i(TAG,"custom count---" + customItem.getCount() + "--" + customItem.getColumnCount());
        if(customItem.getCount()!= 0) {
            customItem.moveToFirst();
            do{
                json = customItem.getString(1);
            }while (customItem.moveToNext());
            customItem.close();
        }
        customItem.close();
        return json;
    }

    public boolean deleteCustomData(String itemName){
        Log.i(TAG,"item received----" + itemName);
        String where = CUSTOM_ITEM_NAME + "= '" + itemName + "'";
        Cursor cart = database.query(false,TABLE_CUSTOM_ITEM,ALL_CUSTOM_ITEMS,where,null,null,
                null,null,null);
        if (cart.getCount()!=0){
            cart.close();
            return database.delete(TABLE_CUSTOM_ITEM, where, null) != 0;
        }else{
            cart.close();
            Log.i(TAG,"no item with that Code");
            return false;
        }
    }

    public boolean deleteAllCustomItems() {
        return database.delete(TABLE_CUSTOM_ITEM,null,null)!=0;
    }

    public boolean saveUpdatesFromServer(String update_on,String camp_id){
        Log.i(TAG, "sql update json---" + camp_id);
        Cursor cart = database.query(false, TABLE_UPDATES, ALL_UPDATE_KEYS, null, null, null,
                null, null, null);

        Log.i(TAG, "check address---" + cart.getCount());
        ContentValues cv = new ContentValues();
        cv.put(CAMPAIGN_ID,camp_id);
        cv.put(UPDATE_ON,update_on);
        cart.close();
        return database.insertWithOnConflict(TABLE_UPDATES, null, cv, SQLiteDatabase.CONFLICT_REPLACE) != 0;
    }

    public ArrayList<String> getSavedCampaignList(){
        ArrayList<String> savedCampaignIdList = new ArrayList<>();
        Log.i(TAG, "sql get updates entered---");
        Cursor updatesCursor = database.query(false, TABLE_UPDATES, ALL_UPDATE_KEYS, null, null, null,
                null, null, null);
        Log.i(TAG,"sql updatesCursor---" + updatesCursor.getCount() +"---" +  updatesCursor.getColumnName(1));
        if(updatesCursor.getCount()!=0 && updatesCursor.moveToFirst()){
            do {
                savedCampaignIdList.add(updatesCursor.getString(1));
            }while (updatesCursor.moveToNext());
        }
        return  savedCampaignIdList;
    }

    public boolean deleteSavedUpdates(){
        Log.i(TAG,"sql delete saved updates entered---");
        return database.delete(TABLE_UPDATES,null,null)!=0;
    }

    public ArrayList<String> getCampaignUuidList(){
        Log.i(TAG,"sql uuid list entered");
        ArrayList<String> uuidList = new ArrayList<>();
        Cursor customItem = database.query(false, CAMPAIGN_TABLE, null, null, null, null,
                null, null, null);
        Log.i(TAG,"custom count---" + customItem.getCount() + "--" + customItem.getColumnCount());
        if(customItem.getCount()!= 0) {
            customItem.moveToFirst();
            do{
                uuidList.add(customItem.getString(1));
                Log.i(TAG,"sql uuid list---" + uuidList.size() + "---" + customItem.getString(1));
            }while (customItem.moveToNext());
            customItem.close();
        }
        customItem.close();
        return uuidList;
    }

    public void createCampaignList(String uuid, String name,String state,String hashtag,
                                   String imagepath, String validtill) {
        ContentValues cv = new ContentValues();
        cv.put(CAMPAIGN_UUID, uuid);
        cv.put(CAMPAIGN_NAME, name);
        cv.put(HASH_TAG, hashtag);
        cv.put(PREVIEW, imagepath);
        cv.put(VALID_TILL, validtill);
        cv.put(STATE, state);

        Log.i("uuid", uuid);
        Log.i("name", name);
        database.insertWithOnConflict(CAMPAIGN_TABLE, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void createCampaignDetails(ArrayList<CampaignDbItem> dbItem) {
        SQLiteDataBase mydb = new SQLiteDataBase(mContext);
        mydb.open();
        for(int i = 0; i < dbItem.size(); i++) {
            CampaignDbItem item = dbItem.get(i);
            ContentValues cv = new ContentValues();
            cv.put(CATEGORY_NAME, item.getCategoryName());
            cv.put(CAMPAIGN_ID, item.getCampaignId());
            cv.put(SUBCATEGORY_NAME, item.getSubCategoryName());
            cv.put(DESCRIPTION, item.getDescription());
            cv.put(ISACTIVE, item.isActive());
            cv.put(ITEM_CODE, item.getItemCode());
            cv.put(ITEM_NAME, item.getItemName());
            cv.put(MEDIA1, item.getMediaImg1());
            cv.put(MEDIA2, item.getMediaImg2());
            cv.put(MEDIA3, item.getMediaImg3());
            cv.put(MEDIA4, item.getMediaImg4());
            cv.put(MEDIA5, item.getMediaImg5());
            cv.put(PRIMARY_IMAGE, item.getPrimaryImage());
            cv.put(TYPE, item.getType());
            cv.put(PRICE, item.getPrice());
            database.insertWithOnConflict(CAMPAIGNDETAILS_TABLE, null, cv, SQLiteDatabase.CONFLICT_NONE);

            mydb.close();
            Log.i(TAG, "inserting categoryname=" + item.getCategoryName());
        }
    }

    public ArrayList<CampaignItemData> getCampaignListData() {
        item = new ArrayList<>();
        String[] columns = new String[]{CAMPAIGN_UUID, CAMPAIGN_NAME, HASH_TAG, PREVIEW, VALID_TILL};
        Cursor c = database.query(CAMPAIGN_TABLE, columns, null, null, null, null, null);
        if (c != null && c.moveToFirst()) {
            do {
                String id = c.getString(c.getColumnIndexOrThrow(CAMPAIGN_UUID));
                String name = c.getString(c.getColumnIndex(CAMPAIGN_NAME));
                String hastag = c.getString(c.getColumnIndex(HASH_TAG));
                String preview_img = c.getString(c.getColumnIndex(PREVIEW));
                String validtill = c.getString(c.getColumnIndex(VALID_TILL));

                CampaignItemData data = new CampaignItemData();
                CampaignItemData.Preview preview = new CampaignItemData().new Preview();

                preview.setImage(preview_img);
                data.setId(id);
                data.setName(name);
                data.setHash_tag(hastag);
                data.setValid_till(validtill);
                data.setPreview(preview);
                item.add(data);

            }
            while (c.moveToNext());
            c.close();
        }
        return item;
    }

    public void deleteCampaigns() {
        Log.i(TAG,"deletinf campaigns");
        database.delete(CAMPAIGN_TABLE,null,null);
    }

    public void deleteCampaignListDetails(String campaignId) {
        database.delete(CAMPAIGNDETAILS_TABLE, CAMPAIGN_ID + " = ? ", new String[] {campaignId});
    }

    public ArrayList<String> getSubcategories(String campaignId) {
        ArrayList<String> subcategory = new ArrayList<>();
        Cursor c = database.rawQuery("select distinct " + SUBCATEGORY_NAME + " from " + CAMPAIGNDETAILS_TABLE + " where " + CAMPAIGN_ID + " =?", new String[]{campaignId}) ;
        if(c!=null && c.moveToFirst()) {
            do {
                String item  =  c.getString(0);
                subcategory.add(item);
            }
            while (c.moveToNext());
            c.close();
        }
        return subcategory;
    }

    public ArrayList<CampaignDbItem> getCampaignDetailForSubCategory(String campaignId, String subcategoryName) {
        ArrayList<CampaignDbItem> items = new ArrayList<>();
        String query = "Select * from campaign_details where " + SUBCATEGORY_NAME + "=? and " + CAMPAIGN_ID + "=?";
        Cursor c = database.rawQuery(query, new String[]{subcategoryName,campaignId});

        if(c != null && c.moveToFirst()) {
            do{
                String cat_name = c.getString(c.getColumnIndexOrThrow(CATEGORY_NAME));
                String camp_id = c.getString(c.getColumnIndex(CAMPAIGN_ID));
                String subcategory = c.getString(c.getColumnIndex(SUBCATEGORY_NAME));
                String description = c.getString(c.getColumnIndex(DESCRIPTION));
                boolean active = Boolean.parseBoolean(c.getString(c.getColumnIndex(ISACTIVE)));
                String item_code = c.getString(c.getColumnIndex(ITEM_CODE));
                String item_name = c.getString(c.getColumnIndex(ITEM_NAME));
                String media1 = c.getString(c.getColumnIndex(MEDIA1));
                String media2 = c.getString(c.getColumnIndex(MEDIA2));
                String media3 = c.getString(c.getColumnIndex(MEDIA3));
                String media4 = c.getString(c.getColumnIndex(MEDIA4));
                String media5 = c.getString(c.getColumnIndex(MEDIA5));
                String type = c.getString(c.getColumnIndex(TYPE));
                String price = c.getString(c.getColumnIndex(PRICE));
                String primaryImg = c.getString(c.getColumnIndex(PRIMARY_IMAGE));

                CampaignDbItem campaignDbItem = new CampaignDbItem();
                campaignDbItem.setCategoryName(cat_name);
                campaignDbItem.setCampaignId(camp_id);
                campaignDbItem.setSubCategoryName(subcategory);
                campaignDbItem.setDescription(description);
                campaignDbItem.setIsActive(active);
                campaignDbItem.setItemCode(item_code);
                campaignDbItem.setItemName(item_name);
                campaignDbItem.setMediaImg1(media1);
                campaignDbItem.setMediaImg2(media2);
                campaignDbItem.setMediaImg3(media3);
                campaignDbItem.setMediaImg4(media4);
                campaignDbItem.setMediaImg5(media5);
                campaignDbItem.setType(type);
                campaignDbItem.setPrice(Integer.parseInt(price));
                campaignDbItem.setPrimaryImage(primaryImg);

                items.add(campaignDbItem);
                Log.i("inserting campId", camp_id);

            }
            while(c.moveToNext());
            c.close();
        }else {
            Log.i(TAG, "No campaign details found for campaignid=" + campaignId + " sub-cateogory=" + subcategoryName);
        }
        return items;
    }

    public ArrayList<CampaignDbItem> getCampaignDetailForSearch(String searchTerm, String campaignId) {
        ArrayList<CampaignDbItem> items = new ArrayList<>();
        String query = "SELECT * FROM " + CAMPAIGNDETAILS_TABLE+ " WHERE " + ITEM_NAME + " LIKE '%" + searchTerm + "%' and " + CAMPAIGN_ID + "=?" ;

        Cursor c = database.rawQuery(query,new String[]{campaignId});

        if(c != null && c.moveToFirst()) {
            do{
                String cat_name = c.getString(c.getColumnIndexOrThrow(CATEGORY_NAME));
                String camp_id = c.getString(c.getColumnIndex(CAMPAIGN_ID));
                String subcategory = c.getString(c.getColumnIndex(SUBCATEGORY_NAME));
                String description = c.getString(c.getColumnIndex(DESCRIPTION));
                boolean active = Boolean.parseBoolean(c.getString(c.getColumnIndex(ISACTIVE)));
                String item_code = c.getString(c.getColumnIndex(ITEM_CODE));
                String item_name = c.getString(c.getColumnIndex(ITEM_NAME));
                String media1 = c.getString(c.getColumnIndex(MEDIA1));
                String media2 = c.getString(c.getColumnIndex(MEDIA2));
                String media3 = c.getString(c.getColumnIndex(MEDIA3));
                String media4 = c.getString(c.getColumnIndex(MEDIA4));
                String media5 = c.getString(c.getColumnIndex(MEDIA5));
                String type = c.getString(c.getColumnIndex(TYPE));
                String price = c.getString(c.getColumnIndex(PRICE));
                String primaryImg = c.getString(c.getColumnIndex(PRIMARY_IMAGE));

                CampaignDbItem campaignDbItem = new CampaignDbItem();
                campaignDbItem.setCategoryName(cat_name);
                campaignDbItem.setCampaignId(camp_id);
                campaignDbItem.setSubCategoryName(subcategory);
                campaignDbItem.setDescription(description);
                campaignDbItem.setIsActive(active);
                campaignDbItem.setItemCode(item_code);
                campaignDbItem.setItemName(item_name);
                campaignDbItem.setMediaImg1(media1);
                campaignDbItem.setMediaImg2(media2);
                campaignDbItem.setMediaImg3(media3);
                campaignDbItem.setMediaImg4(media4);
                campaignDbItem.setMediaImg5(media5);
                campaignDbItem.setType(type);
                campaignDbItem.setPrice(Integer.parseInt(price));
                campaignDbItem.setPrimaryImage(primaryImg);

                items.add(campaignDbItem);

            }
            while(c.moveToNext());
            c.close();
        }
        else {
            Toast.makeText(mContext, "Sorry...no items for that search exist", Toast.LENGTH_SHORT).show();
        }
        return items;
    }
}