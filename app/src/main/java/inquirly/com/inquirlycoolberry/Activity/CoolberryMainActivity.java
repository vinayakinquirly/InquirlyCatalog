package inquirly.com.inquirlycoolberry.Activity;

import java.net.URL;
import java.io.File;

import android.graphics.Color;
import android.util.Log;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.ArrayList;
import java.io.OutputStream;

import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.os.AsyncTask;
import android.view.MenuItem;
import java.net.URLConnection;
import android.content.Intent;
import android.widget.TextView;
import android.content.Context;
import java.io.FileOutputStream;
import android.graphics.Typeface;
import android.app.ProgressDialog;
import java.io.BufferedInputStream;
import android.database.SQLException;
import com.android.volley.VolleyError;
import inquirly.com.inquirlycatalogue.R;
import android.content.SharedPreferences;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.RecyclerView;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.DefaultItemAnimator;
import inquirly.com.inquirlycatalogue.utils.CartCount;
import inquirly.com.inquirlycatalogue.rest.ApiRequest;
import inquirly.com.inquirlycatalogue.utils.ApiConstants;
import inquirly.com.inquirlycatalogue.utils.SQLiteDataBase;
import inquirly.com.inquirlycatalogue.ApplicationController;
import inquirly.com.inquirlycatalogue.rest.IRequestCallback;
import inquirly.com.inquirlycatalogue.helpers.CampaignHelper;
import inquirly.com.inquirlycatalogue.models.CampaignItemData;
import inquirly.com.inquirlycatalogue.utils.CatalogSharedPrefs;
import inquirly.com.inquirlycatalogue.utils.RecyclerViewGridSpacing;
import inquirly.com.inquirlycoolberry.Adapters.CoolberryMainAdapter;
import inquirly.com.inquirlycatalogue.utils.InternetConnectionStatus;
import inquirly.com.inquirlycatalogue.utils.RecyclerItemClickListener;
import inquirly.com.inquirlycatalogue.adapters.RecyclerFeedBackAdapter;

public class CoolberryMainActivity extends AppCompatActivity {

    private Menu mMenu;
    private ProgressDialog pDialog;
    private RecyclerView listCategory;
    private String type,camp_id,propsJson;
    private SharedPreferences mSharedPrefs;
    private CoolberryMainAdapter campAdapter;
    private static final String TAG = "CoolberryMainActivity";
    private ApplicationController appInstance = ApplicationController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setTheme(R.style.CoolberryTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coolberry_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor(appInstance.getImage("color_1")));
        Log.i(TAG,"--->entered");

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
           // actionBar.setHomeAsUpIndicator(getColoredArrow());
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

        pDialog = new ProgressDialog(CoolberryMainActivity.this);
        mSharedPrefs = getSharedPreferences(CatalogSharedPrefs.KEY_NAME, Context.MODE_PRIVATE);
//        camp_id = mSharedPrefs.getString("campaign_id",null);
//        propsJson = mSharedPrefs.getString(camp_id + "_" + CatalogSharedPrefs.KEY_ITEM_PROPERTIES,null);
        boolean isCampaignListLoaded = mSharedPrefs.getBoolean(CatalogSharedPrefs.IS_CAMPAIGN_LIST_LOADED, false);

        listCategory = (RecyclerView) findViewById(R.id.list_categories);
        type = getIntent().getStringExtra(ApiConstants.CAMPAIGN_TYPE);
        if (type != null) {
            if(type.equals(ApiConstants.CAMPAIGN_TYPE_CATALOG)) {
                actionBar.setTitle("CoolBerry");
                if (!isCampaignListLoaded) {
                    if(InternetConnectionStatus.checkConnection(this)) {
                        pDialog.setMessage("Loading...");
                        pDialog.setCancelable(false);
                        pDialog.show();

                        new Thread() {
                            public void run() {
                                try {
                                    sleep(5000);
                                    Log.i(TAG, "Campaign list not loaded. loading from server now");
                                    buildCampaignList(ApiConstants.CAMPAIGN_TYPE_CATALOG);
                                }
                                catch (Exception e) {
                                    Log.e(TAG, e.getMessage());
                                    Toast.makeText(getApplicationContext(), "Something went wrong...please try again later", Toast.LENGTH_SHORT).show();
                                }
//                                pDialog.dismiss();
                            }
                        }.start();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Internet not connected...please connect and proceed",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Log.i(TAG, "Campaign list loaded. Fetching offline");
                    getCampaignList(ApiConstants.CAMPAIGN_TYPE_CATALOG);
                }
            }
            else if (type.equals(ApiConstants.CAMPAIGN_TYPE_FEEDBACK)) {
                actionBar.setTitle("Feedback");
                buildCampaignList(ApiConstants.CAMPAIGN_TYPE_FEEDBACK);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMenu != null) {
            MenuItem item = mMenu.findItem(R.id.action_count);
            LayerDrawable icon = (LayerDrawable) item.getIcon();
            int size = ApplicationController.getInstance().getCartItemCount();
            CartCount.setBadgeCount(this, icon, size);
            this.recreate();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_count);
        LayerDrawable icon = (LayerDrawable) item.getIcon();
        int size = ApplicationController.getInstance().getCartItemCount();
        CartCount.setBadgeCount(this, icon, size);
        mMenu = menu;
        MenuItem item1 = menu.findItem(R.id.action_count);
        MenuItem item2 = menu.findItem(R.id.action_refresh);
        if(type.equals(ApiConstants.CAMPAIGN_TYPE_FEEDBACK)) {
            item1.setEnabled(false);
            item1.getIcon().setAlpha(100);
            item2.setEnabled(false);
            item2.getIcon().setAlpha(100);
        }
        else {
            item1.setEnabled(true);
            item1.getIcon().setAlpha(255);
            item2.setEnabled(true);
            item2.getIcon().setAlpha(255);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            if (InternetConnectionStatus.checkConnection(this)) {
                pDialog.setMessage("Reloading....Please wait");
                pDialog.setCancelable(false);
                pDialog.show();

                new Thread() {
                    public void run() {
                        try {
                            sleep(7000);
                            refreshCampaignCategories();
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                            //   Toast.makeText(getApplicationContext(), "Something went wrong...please try again later", Toast.LENGTH_SHORT).show();
                        }
                        pDialog.dismiss();
                    }
                }.start();
                // onRestart();
            }
            else {
                Toast.makeText(getApplicationContext(),"Please connect to the Internet and try again",Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        if (id == R.id.action_count) {
            Log.i(TAG, "action show cart clicked" + camp_id);
            Intent cartItent = new Intent(this, CoolBerryCartActivity.class);
            cartItent.putExtra("campaign_id",camp_id);
            startActivity(cartItent);
        }
        if(id==android.R.id.home){
            Log.i(TAG,"back clicked");
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void buildCampaignList(final String campaignType) {
        Log.i(TAG,"build Campaign List eneterd---"  + campaignType);
        SharedPreferences sp = getSharedPreferences(CatalogSharedPrefs.KEY_NAME, Context.MODE_PRIVATE);
        String securityToken = sp.getString(CatalogSharedPrefs.KEY_SEC_TOKEN, "");
        ApiRequest.getCampaignList(
                securityToken,
                campaignType,
                new IRequestCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Log.i(TAG,"buildCampaignList----" + response.toString());
                        final ArrayList<CampaignItemData> itemData = CampaignHelper.buildCampaignList(response, CoolberryMainActivity.this);
                        try {
                            if (campaignType != ApiConstants.CAMPAIGN_TYPE_FEEDBACK) {
                                for (int i = 0; i < itemData.size(); i++) {
                                    CampaignItemData data = itemData.get(i);
                                    if(data.getState().equals("ACTIVE")) {
                                        String img_path = "/sdcard/campaigns/" + data.getName() + ".png";
                                        // re-loading the activity due to some image issue loading with picasso//
                                        startActivity(getIntent());
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
                                SharedPreferences.Editor editor = mSharedPrefs.edit();
                                editor.putBoolean(CatalogSharedPrefs.IS_CAMPAIGN_LIST_LOADED, true);
                                editor.commit();

                            } else {
                                listCategory.setLayoutManager(new GridLayoutManager(CoolberryMainActivity.this, 2));
                                RecyclerFeedBackAdapter mAdapter = new RecyclerFeedBackAdapter(itemData, CoolberryMainActivity.this);
                                listCategory.setAdapter(mAdapter);
                                listCategory.setItemAnimator(new DefaultItemAnimator());
                                RecyclerViewGridSpacing spacing = new RecyclerViewGridSpacing(getApplicationContext(), R.dimen.item_offset);
                                listCategory.addItemDecoration(spacing);
                                listCategory.addOnItemTouchListener(

                                        new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(View view, int position) {
                                                CampaignItemData campaign = itemData.get(position);

                                                Log.i(TAG, "campaign clicked=" + campaign.getId());
                                                Intent i = new Intent(getApplicationContext(), CoolberryItemsTabActivity.class);
                                                i.putExtra("campaignId", campaign.getId());
                                                i.putExtra(ApiConstants.CAMPAIGN_TYPE, campaignType);
                                                startActivity(i);
                                            }
                                        })
                                );
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
        pDialog.dismiss();
    }

    private void getCampaignList(final String campaignType) {
        Log.i(TAG,"getCampaignList----" +campaignType);
        SQLiteDataBase myDB = new SQLiteDataBase(getApplicationContext());
        myDB.open();
        if(myDB.getCampaignListData().size()==0){
            Log.i(TAG,"campaignList size is---- 0");
            buildCampaignList(campaignType);
        }else {
            Log.i(TAG,"campaignList size is---- not 0");
            final ArrayList<CampaignItemData> items = myDB.getCampaignListData();
            camp_id = items.get(0).getId();
            listCategory.setLayoutManager(new GridLayoutManager(CoolberryMainActivity.this, 2));
            campAdapter = new CoolberryMainAdapter(items, CoolberryMainActivity.this);
            listCategory.setAdapter(campAdapter);
            listCategory.setItemAnimator(new DefaultItemAnimator());
            RecyclerViewGridSpacing spacing = new RecyclerViewGridSpacing(getApplicationContext(), R.dimen.recycler_item_spacing);
            listCategory.addItemDecoration(spacing);
            myDB.close();
            listCategory.addOnItemTouchListener(
                    new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            CampaignItemData campaign = items.get(position);
                            Log.i(TAG, "campaign clicked=" + campaign.getId());
                            Intent i = new Intent(getApplicationContext(), CoolberryItemsTabActivity.class);
                            Log.i(TAG, "campaignID" + campaign.getId() + "--type--" + campaignType);
                            i.putExtra("campaignId", campaign.getId());
                            i.putExtra(ApiConstants.CAMPAIGN_TYPE, campaignType);
                            startActivity(i);
                        }
                    })
            );
        }
    }

    public void refreshCampaignCategories() {
        Log.i(TAG,"refereshing--");
        SQLiteDataBase myDB = new SQLiteDataBase(getApplicationContext());
        myDB.open();
        myDB.deleteCampaigns();
        myDB.close();
        Log.i(TAG,"moving to build campaigns");
        buildCampaignList(ApiConstants.CAMPAIGN_TYPE_CATALOG);
        finish();
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

            if(dir.exists()==false) {
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
        protected void onPostExecute(Object o) {
            //pDialog.dismiss();
        }
    }
}