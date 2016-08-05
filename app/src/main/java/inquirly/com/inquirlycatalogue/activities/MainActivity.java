package inquirly.com.inquirlycatalogue.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import inquirly.com.inquirlycatalogue.ApplicationController;
import inquirly.com.inquirlycatalogue.R;
import inquirly.com.inquirlycatalogue.adapters.RecyclerCampaignGridAdapter;
import inquirly.com.inquirlycatalogue.adapters.RecyclerFeedBackAdapter;
import inquirly.com.inquirlycatalogue.helpers.CampaignHelper;
import inquirly.com.inquirlycatalogue.models.CampaignItemData;
import inquirly.com.inquirlycatalogue.rest.ApiRequest;
import inquirly.com.inquirlycatalogue.rest.IRequestCallback;
import inquirly.com.inquirlycatalogue.utils.ApiConstants;
import inquirly.com.inquirlycatalogue.utils.CartCount;
import inquirly.com.inquirlycatalogue.utils.CatalogSharedPrefs;
import inquirly.com.inquirlycatalogue.utils.InternetConnectionStatus;
import inquirly.com.inquirlycatalogue.utils.RecyclerItemClickListener;
import inquirly.com.inquirlycatalogue.utils.RecyclerViewGridSpacing;
import inquirly.com.inquirlycatalogue.utils.SQLiteDataBase;


public class MainActivity extends AppCompatActivity {

    private String type;
    private Menu mMenu;
    private ProgressDialog pDialog;
    private RecyclerView mRecyclerViewMain;
    private SharedPreferences mSharedPrefs;
    private RecyclerCampaignGridAdapter campAdapter;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppThemeBlue);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

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

        pDialog = new ProgressDialog(MainActivity.this);

        mSharedPrefs = getSharedPreferences(CatalogSharedPrefs.KEY_NAME, Context.MODE_PRIVATE);
        boolean isCampaignListLoaded = mSharedPrefs.getBoolean(CatalogSharedPrefs.IS_CAMPAIGN_LIST_LOADED, false);
        mRecyclerViewMain = (RecyclerView) findViewById(R.id.recycler_view_main);

        type = getIntent().getStringExtra(ApiConstants.CAMPAIGN_TYPE);
        if (type != null) {
            if(type.equals(ApiConstants.CAMPAIGN_TYPE_CATALOG)) {
                actionBar.setTitle("Catalogue");
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
                                pDialog.dismiss();
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
            int size = ApplicationController.getInstance().getJustCartItemCount();
            CartCount.setBadgeCount(this, icon, size);
                this.recreate();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_count);
        LayerDrawable icon = (LayerDrawable) item.getIcon();
        int size = ApplicationController.getInstance().getJustCartItemCount();
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
                else
                {
                    Toast.makeText(getApplicationContext(),"Please connect to the Internet and try again",Toast.LENGTH_SHORT).show();
                }

            return true;
        }
        if (id == R.id.action_count) {
            //handle click of
            Log.i(TAG, "action show cart clicked");
            Intent cartItent = new Intent(this, CartActivity.class);
            startActivity(cartItent);
        }
       /* if(id==R.id.action_logout)
        {
            Intent i = new Intent(getApplicationContext(),LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }*/
        return super.onOptionsItemSelected(item);
    }


    private void buildCampaignList(final String campaignType) {

            SharedPreferences sp = getSharedPreferences(CatalogSharedPrefs.KEY_NAME, Context.MODE_PRIVATE);
            String securityToken = sp.getString(CatalogSharedPrefs.KEY_SEC_TOKEN, "");
            ApiRequest.getCampaignList(
                    securityToken,
                    campaignType,
                    new IRequestCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            final ArrayList<CampaignItemData> itemData = CampaignHelper.buildCampaignList(response, MainActivity.this);
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
                                    mRecyclerViewMain.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
                                    RecyclerFeedBackAdapter mAdapter = new RecyclerFeedBackAdapter(itemData, MainActivity.this);
                                    mRecyclerViewMain.setAdapter(mAdapter);
                                    mRecyclerViewMain.setItemAnimator(new DefaultItemAnimator());
                                    RecyclerViewGridSpacing spacing = new RecyclerViewGridSpacing(getApplicationContext(), R.dimen.item_offset);
                                    mRecyclerViewMain.addItemDecoration(spacing);
                                    mRecyclerViewMain.addOnItemTouchListener(
                                            new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(View view, int position) {
                                                    CampaignItemData campaign = itemData.get(position);
                                                    Log.i(MainActivity.this.TAG, "campaign clicked=" + campaign.getId());
                                                    Intent i = new Intent(getApplicationContext(), ItemTabActivity.class);
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
                            Log.e(MainActivity.this.TAG, "Error getting campaign list=" + error.getMessage());
                            Toast.makeText(getApplicationContext(), "Something went wrong...please try again later", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

    }
    private void getCampaignList(final String campaignType) {
        SQLiteDataBase myDB = new SQLiteDataBase(getApplicationContext());
        myDB.open();
        final ArrayList<CampaignItemData> items = myDB.getCampaignListData();
        mRecyclerViewMain.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
        campAdapter = new RecyclerCampaignGridAdapter(items, MainActivity.this);
        mRecyclerViewMain.setAdapter(campAdapter);
        mRecyclerViewMain.setItemAnimator(new DefaultItemAnimator());
        RecyclerViewGridSpacing spacing = new RecyclerViewGridSpacing(getApplicationContext(), R.dimen.recycler_item_spacing);
        mRecyclerViewMain.addItemDecoration(spacing);
        myDB.close();
        mRecyclerViewMain.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        CampaignItemData campaign = items.get(position);
                        Log.i(MainActivity.this.TAG, "campaign clicked=" + campaign.getId());
                        Intent i = new Intent(getApplicationContext(), ItemTabActivity.class);
                        i.putExtra("campaignId", campaign.getId());
                        i.putExtra(ApiConstants.CAMPAIGN_TYPE, campaignType);
                        startActivity(i);
                    }
                })
        );
    }

    @Override
    public void onBackPressed() {

    }

    public void refreshCampaignCategories() {

            SQLiteDataBase myDB = new SQLiteDataBase(getApplicationContext());
            myDB.open();
            myDB.deleteCampaigns();
            myDB.close();

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
        protected void onPreExecute() {
            super.onPreExecute();
          /*pDialog = new ProgressDialog(MainActivity.this);
          pDialog.setMessage("Please Wait...");
          pDialog.setIndeterminate(false);
          pDialog.setCancelable(false);
          pDialog.show();*/
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