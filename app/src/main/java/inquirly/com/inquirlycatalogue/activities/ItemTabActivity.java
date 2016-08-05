package inquirly.com.inquirlycatalogue.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;

import inquirly.com.inquirlycatalogue.ApplicationController;
import inquirly.com.inquirlycatalogue.R;
import inquirly.com.inquirlycatalogue.fragments.ItemTabFragment;
import inquirly.com.inquirlycatalogue.helpers.CampaignHelper;
import inquirly.com.inquirlycatalogue.models.Campaign;
import inquirly.com.inquirlycatalogue.models.CampaignDbItem;
import inquirly.com.inquirlycatalogue.rest.ApiRequest;
import inquirly.com.inquirlycatalogue.rest.IRequestCallback;
import inquirly.com.inquirlycatalogue.utils.ApiConstants;
import inquirly.com.inquirlycatalogue.utils.CartCount;
import inquirly.com.inquirlycatalogue.utils.CatalogSharedPrefs;
import inquirly.com.inquirlycatalogue.utils.InternetConnectionStatus;
import inquirly.com.inquirlycatalogue.utils.SQLiteDataBase;

public class ItemTabActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private Menu mMenu;
    private String mCampaignId;
    private String mCampaignType;
    private ProgressDialog pDialog;
    private SharedPreferences mSharedPrefs;
    private static final String TAG = "ItemTabActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppThemeBlue);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materail_tab);

        initToolbar();
        pDialog = new ProgressDialog(ItemTabActivity.this);

        // add the listener so it will tries to suggest while the user types
        mCampaignId = getIntent().getStringExtra("campaignId");
        mCampaignType = getIntent().getStringExtra(ApiConstants.CAMPAIGN_TYPE);

        mSharedPrefs = getSharedPreferences(CatalogSharedPrefs.KEY_NAME, Context.MODE_PRIVATE);
        final boolean isTableLoaded = mSharedPrefs.getBoolean(CatalogSharedPrefs.IS_TABLE_LOADED +" of "+ mCampaignId,false);
        Log.i(TAG, "isTableLoaded for campaign id " + mCampaignId + " status=" + isTableLoaded);

        if (!isTableLoaded) {
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            pDialog.show();

            new Thread() {
                public void run() {
                    try {
                        sleep(4000);
                        buildCampaignDetails(mCampaignId);
                        SharedPreferences.Editor editor = mSharedPrefs.edit();
                        editor.putBoolean(CatalogSharedPrefs.IS_TABLE_LOADED +" of "+mCampaignId, true);
                        editor.commit();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(getApplicationContext(), "Something went wrong...please try again later", Toast.LENGTH_SHORT).show();
                    }
                    pDialog.dismiss();
                }
            }.start();
        } else {
            initViewPagerAndTabs();
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
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_tab);
        setSupportActionBar(toolbar);

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "Montserrat-Regular.ttf");
        TextView toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        toolbar_title.setText("Catalogue");
        toolbar_title.setTypeface(font);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(getColoredArrow());
        }
    }

    private Drawable getColoredArrow() {
        Drawable arrowDrawable = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        Drawable wrapped = DrawableCompat.wrap(arrowDrawable);

        if (arrowDrawable != null && wrapped != null) {
            // This should avoid tinting all the arrows
            arrowDrawable.mutate();
            DrawableCompat.setTint(wrapped, Color.WHITE);
        }

        return wrapped;
    }

    private void initViewPagerAndTabs() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        /*Campaign.FormAttributes.SubCategories[] subCategories = campaignList.get(0).getForm_attributes().getSub_categories();
        String campaignType = campaignList.get(0).getCategory();
        ApplicationController.getInstance().setFeedbackLink(campaignList.get(0).getForm_attributes().getFeedback_link());*/
        SQLiteDataBase myDB = new SQLiteDataBase(getApplicationContext());
        myDB.open();
        Log.i(TAG, "Getting subcategory for campaign=" +mCampaignId);
        ArrayList<String> subcategory = myDB.getSubcategories(mCampaignId);
        Log.i(TAG, "Subcategorysize is"+ subcategory.size());


        if(subcategory.size() == 0) {
            Log.i(TAG, "subcategory size found 0, loading data");
            buildCampaignDetails(mCampaignId);
            return;
        }
        for (int i = 0; i < subcategory.size(); i++) {
            //get the campaign details for the given subcategory name and campaign id
            ArrayList<CampaignDbItem> itemList = myDB.getCampaignDetailForSubCategory(mCampaignId, subcategory.get(i));
            pagerAdapter.addFragment(ItemTabFragment.createInstance(itemList, ItemTabActivity.this.mCampaignId),
                    subcategory.get(i));
            Log.i("subcategory id is ", subcategory.get(i));

        }
        myDB.close();

        viewPager.setAdapter(pagerAdapter);
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                int tabLayoutWidth = tabLayout.getWidth();
                DisplayMetrics metrics = new DisplayMetrics();
                ItemTabActivity.this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
                int deviceWidth = metrics.widthPixels;

                ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
                int tabsCount = vg.getChildCount();
                for (int j = 0; j < tabsCount; j++) {
                    ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
                    int tabChildsCount = vgTab.getChildCount();
                    for (int i = 0; i < tabChildsCount; i++) {
                        View tabViewChild = vgTab.getChildAt(i);
                        if (tabViewChild instanceof TextView) {

                            Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "Montserrat-Regular.ttf");
                            ((TextView) tabViewChild).setTypeface(font);
                        }
                    }
                }
                if (tabLayoutWidth < deviceWidth) {
                    tabLayout.setTabMode(TabLayout.MODE_FIXED);
                    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                } else {
                    tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                }
            }
        });
        viewPager.getAdapter().notifyDataSetChanged();
    }

    static class PagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        public PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tab, menu);
        MenuItem item = menu.findItem(R.id.action_count);
        LayerDrawable icon = (LayerDrawable) item.getIcon();
        int size = ApplicationController.getInstance().getJustCartItemCount();
        CartCount.setBadgeCount(this, icon, size);
        mMenu = menu;
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
                            refreshSubcategories(mCampaignId);
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                            Toast.makeText(getApplicationContext(), "Something went wrong...please try again later", Toast.LENGTH_SHORT).show();
                        }
                        pDialog.dismiss();
                    }
                }.start();
            }
            else {
                Toast.makeText(getApplicationContext(),"Please connect to the Internet and try again",Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        if (id == R.id.action_count) {
            Intent cartIntent = new Intent(this, CartActivity.class);
            startActivity(cartIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void buildCampaignDetails(final String campaignId) {
        if (InternetConnectionStatus.checkConnection(this)) {
            SharedPreferences prefs = getSharedPreferences(CatalogSharedPrefs.KEY_NAME, Context.MODE_PRIVATE);
            String securityToken = prefs.getString(CatalogSharedPrefs.KEY_SEC_TOKEN, "");
            String email = prefs.getString(CatalogSharedPrefs.KEY_USER_EMAIL, "");
            ApiRequest.getCampaignDetails(campaignId,
                    email,
                    securityToken,
                    new IRequestCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            Log.i(ItemTabActivity.this.TAG, "campaign details=" + response.toString());
                            if (ItemTabActivity.this.mCampaignType.equals(ApiConstants.CAMPAIGN_TYPE_FEEDBACK)) {
                                try {
                                    //bug:SI-296
                                    //the json format for feedback campaigns changed, so now it's a campaign url instead of feedback link
                                    String feedbackUrl = response.getJSONObject("campaign").getString("campaign_url");
                                    Intent feedbackViewIntent = new Intent(ItemTabActivity.this, WebViewActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString(ApiConstants.CAMPAIGN_FEEDBACK_LINK, feedbackUrl);
                                    feedbackViewIntent.putExtras(bundle);
                                    startActivity(feedbackViewIntent);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                Log.i(TAG, "Loading campaign details for campaign for="+ campaignId);
                                ArrayList<Campaign> campaignList = CampaignHelper.buildCampaignDetails(response, ItemTabActivity.this, campaignId);
                                Log.i(ItemTabActivity.this.TAG, "Campaign list size=" + campaignList.size());
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
                                            if (item.getMedia().length > 1) {
                                                dbItem.setMediaImg1(item.getMedia()[1].getUrl());
                                                dbItem.setMediaImg2(item.getMedia()[2].getUrl());
                                                dbItem.setMediaImg3(item.getMedia()[3].getUrl());
                                                dbItem.setMediaImg4(item.getMedia()[4].getUrl());
                                                dbItem.setMediaImg5(item.getMedia()[5].getUrl());
                                            }
                                            itemsList.add(dbItem);
                                        }
                                        catch(Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                SQLiteDataBase myDb = new SQLiteDataBase(getApplicationContext());
                                myDb.open();
                                myDb.createCampaignDetails(itemsList);
                                myDb.close();
                                initViewPagerAndTabs();
                            }
                        }

                        @Override
                        public void onError(VolleyError error) {
                            Log.e(ItemTabActivity.this.TAG, "Error fetching campaigns=" + error.getMessage());
                            error.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Something Went Wrong... please try again later", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
        else {
            Toast.makeText(getApplicationContext(), "failed to load data...check your internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void refreshSubcategories(String campaignId) {
        campaignId = mCampaignId;
        SQLiteDataBase myDB = new SQLiteDataBase(getApplicationContext());
        myDB.open();
        myDB.deleteCampaignListDetails(mCampaignId);
        myDB.close();
        startActivity(getIntent());
        Log.d("campaignId is :", campaignId);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private class DownloadFile extends AsyncTask<Object, Object, Object> {
        private String requestUrl, imagename;
        private DownloadFile(String requestUrl, String imagename) {
            this.requestUrl = requestUrl;
            this.imagename = imagename;
        }

        @Override
        protected Object doInBackground(Object... objects) {
            try {
                String[] tempFileNames;
                String tempFileName ="";
                String delimiter = ":";
                tempFileNames = imagename.split(delimiter);
                tempFileName = tempFileNames[0];
                for (int j = 1; j < tempFileNames.length; j++){
                    tempFileName = tempFileName+" - "+tempFileNames[j];
                }

                File root = android.os.Environment.getExternalStorageDirectory();
                File dir = new File (root.getAbsolutePath() + "/campaigndetails/");
                if(dir.exists()==false) {
                    dir.mkdirs();
                }
                URL url = new URL(requestUrl);
                Log.i("FILE_NAME", "campaigns"+imagename+".png");
                Log.i("FILE_URLLINK", "File URL is "+url);

                URLConnection connection = url.openConnection();
                connection.connect();
                int fileLength = connection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(dir+"/"+tempFileName+".png");

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
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.i("ERROR DOWNLOADING FILES", "ERROR IS" + e);
            }
            return null;
        }
    }
}