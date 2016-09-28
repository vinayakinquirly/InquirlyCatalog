package inquirly.com.inquirlycoolberry.service;

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.Locale;
import android.util.Log;
import java.util.Calendar;
import java.util.TimeZone;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.ArrayList;
import android.widget.Toast;
import java.text.DateFormat;
import java.io.OutputStream;
import com.google.gson.Gson;
import android.os.AsyncTask;
import java.net.URLConnection;
import android.content.Intent;
import android.content.Context;
import java.io.FileOutputStream;
import android.app.IntentService;
import java.text.SimpleDateFormat;
import java.io.BufferedInputStream;
import android.database.SQLException;
import com.android.volley.VolleyError;
import android.content.SharedPreferences;
import inquirly.com.inquirlycatalogue.models.Campaign;
import inquirly.com.inquirlycatalogue.rest.ApiRequest;
import inquirly.com.inquirlycatalogue.utils.ApiConstants;
import inquirly.com.inquirlycatalogue.models.DataUpdateRes;
import inquirly.com.inquirlycatalogue.utils.SQLiteDataBase;
import inquirly.com.inquirlycatalogue.rest.IRequestCallback;
import inquirly.com.inquirlycatalogue.models.CampaignDbItem;
import inquirly.com.inquirlycatalogue.ApplicationController;
import inquirly.com.inquirlycatalogue.helpers.CampaignHelper;
import inquirly.com.inquirlycatalogue.models.CampaignItemData;
import inquirly.com.inquirlycatalogue.utils.CatalogSharedPrefs;
import inquirly.com.inquirlycatalogue.utils.InternetConnectionStatus;

/**
 * Created by Vinayak on 9/23/2016.
 */
public class AutoUpdateDataService extends IntentService {

    private SharedPreferences sharedPreferences;
    public  ArrayList<Campaign> campaignList;
    private static String security_token=null;
    private static final String TAG = "AutoUpdateDataService";
    private ApplicationController appInstance = ApplicationController.getInstance();

    public AutoUpdateDataService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sharedPreferences = getSharedPreferences(CatalogSharedPrefs.KEY_NAME, Context.MODE_PRIVATE);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Log.i(TAG,"date time stamp----"+  dateFormat.format(new Date()));
        security_token = appInstance.getImage("security_token");
        checkForUpdate(security_token);
    }

    private void checkForUpdate(String security_token) {
        ApiRequest.getUpdates(
            security_token,
            new IRequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    Log.i(TAG,"check response--" + response.toString());
                    Gson gson = new Gson();
                    DataUpdateRes dataUpdateRes = gson.fromJson(response.toString(),DataUpdateRes.class);
                    if(dataUpdateRes.getResCode()!=200){
                        Log.i(TAG,"response code---" + dataUpdateRes.getResCode());
                    }else{
                        String updateTime = sharedPreferences.getString("update_on",null);
                        if(updateTime!=null){
                            Calendar calendar = Calendar.getInstance();
                            String currentTime = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
                            char[] localHour = new char[2];
                            updateTime.getChars(0,2,localHour,0);
                            updateTime = new String(localHour);
                            Log.i(TAG,"current time--CT-" + currentTime + "--UT-" + updateTime);
                            new DoFinalUpdate().execute();
                            for(int i=0;i<dataUpdateRes.getUpdates().size();i++) {
                                Log.i(TAG,"timestamps are equal UPDATING..." + dataUpdateRes.getUpdates().get(i).getId());
                                new UpdateDetails(dataUpdateRes.getUpdates().get(i).getId()).execute();
                            }
                            if(currentTime.equals(updateTime)){
                                Log.i(TAG,"timestamps are equal UPDATING...");
                                new DoFinalUpdate().execute();
                                for(int i=0;i<dataUpdateRes.getUpdates().size();i++) {
                                    new UpdateDetails(dataUpdateRes.getUpdates().get(i).getId()).execute();
                                }
                            }else{
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("updateJson",dataUpdateRes.getUpdates().toString());
                                editor.putString("update_on",dataUpdateRes.getUpdate_on());
                                editor.putString(CatalogSharedPrefs.KEY_LAST_UPDATED,dataUpdateRes.getTs());
                                editor.apply();
                            }
                        }else{
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("updateJson",dataUpdateRes.getUpdates().toString());
                            editor.putString("update_on",dataUpdateRes.getUpdate_on());
                            editor.putString(CatalogSharedPrefs.KEY_LAST_UPDATED,dataUpdateRes.getTs());
                            editor.apply();
                        }
                    }
                }
                @Override
                public void onError(VolleyError error) {
                    Log.i(TAG,"check error--" + error.getMessage());
                }
            }
        );
    }

    public class DoFinalUpdate extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            Log.i(TAG,"final update class entered");
            SQLiteDataBase myDB = new SQLiteDataBase(getApplicationContext());
            myDB.open();
            myDB.deleteCampaigns();
            myDB.close();
            buildCampaignList(ApiConstants.CAMPAIGN_TYPE_CATALOG,security_token);
            return null;
        }

        private void buildCampaignList(final String campaignType,final String security_token) {
            Log.i(TAG,"build Campaign List eneterd---"  + campaignType + "---" + security_token);
            ApiRequest.getCampaignList(
                security_token,
                campaignType,
                new IRequestCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Log.i(TAG,"buildCampaignList----" + response.toString());
                        final ArrayList<CampaignItemData> itemData = CampaignHelper.buildCampaignList(response, AutoUpdateDataService.this);
                        try {
                            if (campaignType != ApiConstants.CAMPAIGN_TYPE_FEEDBACK) {
                                for (int i = 0; i < itemData.size(); i++) {
                                    CampaignItemData data = itemData.get(i);
                                    if(data.getState().equals("ACTIVE")) {
                                        String img_path = "/sdcard/campaigns/" + data.getName() + ".png";
                                        // re-loading the activity due to some image issue loading with picasso//
                                        //startActivity(getIntent());
                                        new DownloadFile(data.getPreview().getImage(), data.getName()).execute();
                                        SQLiteDataBase myDB = new SQLiteDataBase(getApplicationContext());
                                        myDB.open();
                                        myDB.createCampaignList(data.getId(), data.getName(), data.getState(), data.getHash_tag(), img_path, data.getValid_till());
                                        Log.i(TAG,"data saved in DB");
                                        getCampaignList(ApiConstants.CAMPAIGN_TYPE_CATALOG);
                                        myDB.close();
                                    }
                                    else {
                                        Log.i(TAG,"campaign is inactive");
                                    }
                                }
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean(CatalogSharedPrefs.IS_CAMPAIGN_LIST_LOADED, true);
                                editor.apply();
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(VolleyError error) {
                        Log.e(TAG, "Error getting campaign list=" + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Something went wrong...please try again later", Toast.LENGTH_SHORT).show();
                    }
                }
            );
        }

        private void getCampaignList(final String campaignType) {
            Log.i(TAG,"getCampaignList----" +campaignType);
            SQLiteDataBase myDB = new SQLiteDataBase(getApplicationContext());
            myDB.open();
            if(myDB.getCampaignListData().size()==0){
                Log.i(TAG,"campaignList size is---- 0");
                buildCampaignList(campaignType,security_token);
            }
        }
    }

    private class DownloadFile extends AsyncTask<Object, Object, Object> {

        private String urlLink, fileName;
        private DownloadFile(String requestUrl, String imagename) {
            this.urlLink = requestUrl;
            this.fileName = imagename;
        }

        @Override
        protected Object doInBackground(Object... objects) {
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File (root.getAbsolutePath() + "/campaigns/");

            if(!dir.exists()) {
                dir.mkdirs();
            }

            try {
                URL url = new URL(urlLink);
                Log.i("FILE_NAME", "campaigns"+fileName+".png");
                Log.i("FILE_URLLINK", "File URL is " + url);
                URLConnection connection = url.openConnection();
                connection.connect();
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(dir+"/"+fileName+".png");

                byte data[] = new byte[1024];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("ERROR DOWNLOADING FILES","ERROR IS" +e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {}
    }

    public class UpdateDetails extends AsyncTask {

        String campaingId=null;

        public UpdateDetails(String campaingId){
            this.campaingId= campaingId;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            buildCampaignDetails(campaingId);
            return null;
        }

        private void buildCampaignDetails(final String campaignId) {
            if (InternetConnectionStatus.checkConnection(getApplicationContext())) {
                SharedPreferences prefs = getSharedPreferences(CatalogSharedPrefs.KEY_NAME, Context.MODE_PRIVATE);
                String securityToken = prefs.getString(CatalogSharedPrefs.KEY_SEC_TOKEN, "");
                String email = prefs.getString(CatalogSharedPrefs.KEY_USER_EMAIL, "");
                Log.i(TAG,"will make API Req.");
                ApiRequest.getCampaignDetails(campaignId,
                    email,
                    securityToken,
                    new IRequestCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            Log.i(TAG, "campaign details=" + response.toString());

                            String[] split = response.toString().split("override_preview",2);
                            String str_1 = split[0];
                            String str_2 = split[1];
                            Log.i(TAG,"str_1" + str_1);
                            Log.i(TAG,"str_2" + str_2);

                            Log.i(TAG, "Loading campaign details for campaign for="+ campaignId);
                            campaignList = CampaignHelper.buildCampaignDetails(response, getApplicationContext(), campaignId);
                            Log.i(TAG, "Campaign list size=" + campaignList.size());
                            Campaign.FormAttributes.SubCategories[] subCategories = campaignList.get(0).getForm_attributes().getSub_categories();

                            ArrayList<CampaignDbItem> itemsList = new ArrayList<CampaignDbItem>();
                            for (int i = 0; i < subCategories.length; i++) {
                                Campaign.FormAttributes.SubCategories.Item[] items = subCategories[i].getItems();
                                final String subcategoryName = subCategories[i].getName();

                                for (int j = 0; j < items.length; j++) {
                                    Campaign.FormAttributes.SubCategories.Item item = items[j];
                                    CampaignDbItem dbItem = new CampaignDbItem();

                                    File root = android.os.Environment.getExternalStorageDirectory();
                                    String img_path = root.getAbsolutePath() + "/campaigndetails/" + item.getName() + item.getItem_code()+".png";

                                    new DownloadFile(item.getPrimary_image(), item.getName()+item.getItem_code()).execute();
                                    dbItem.setSubCategoryName(subcategoryName);
                                    dbItem.setDescription(item.getDescription());
                                    dbItem.setIsActive(item.is_active());
                                    dbItem.setItemCode(item.getItem_code());
                                    dbItem.setItemName(item.getName());
                                    dbItem.setPrice(item.getPrice());
                                    dbItem.setType(item.getType());
                                    dbItem.setCampaignId(campaignId);
                                    dbItem.setCategoryName(subcategoryName);
                                    dbItem.setPrimaryImage(img_path);

                                    try {
                                        itemsList.add(dbItem);
                                    }
                                    catch(Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            SQLiteDataBase myDb = new SQLiteDataBase(getApplicationContext());
                            myDb.open();
                            myDb.createCampaignDetails(itemsList);
                            myDb.close();
                        }

                        @Override
                        public void onError(VolleyError error) {
                            Log.e(TAG, "Error fetching campaigns=" + error.getMessage());
                        }
                    }
                );
            }
        }
    }
}