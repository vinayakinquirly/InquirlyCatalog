package inquirly.com.inquirlycoolberry.Activity;

import java.net.URL;
import java.io.File;
import java.util.List;
import android.util.Log;
import android.view.Menu;
import android.os.Bundle;
import android.view.View;
import org.json.JSONObject;
import android.view.Window;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import android.os.AsyncTask;
import android.widget.Toast;
import android.view.MenuItem;
import java.net.URLConnection;
import android.view.ViewGroup;
import android.content.Intent;
import android.graphics.Color;
import android.content.Context;
import android.widget.TextView;
import android.widget.ImageView;
import java.io.FileOutputStream;
import android.graphics.Typeface;
import android.view.WindowManager;
import android.app.ProgressDialog;
import java.io.BufferedInputStream;
import android.util.DisplayMetrics;
import com.android.volley.VolleyError;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import inquirly.com.inquirlycatalogue.R;
import android.support.v7.widget.Toolbar;
import android.support.v4.view.ViewPager;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.SearchView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.graphics.drawable.LayerDrawable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentPagerAdapter;
import inquirly.com.inquirlycatalogue.rest.ApiRequest;
import inquirly.com.inquirlycatalogue.utils.CartCount;
import inquirly.com.inquirlycatalogue.models.Campaign;
import android.support.design.widget.CoordinatorLayout;
import inquirly.com.inquirlycatalogue.utils.ApiConstants;
import inquirly.com.inquirlycatalogue.utils.SQLiteDataBase;
import android.support.v4.graphics.drawable.DrawableCompat;
import inquirly.com.inquirlycatalogue.ApplicationController;
import inquirly.com.inquirlycatalogue.models.CampaignDbItem;
import inquirly.com.inquirlycatalogue.rest.IRequestCallback;
import inquirly.com.inquirlycatalogue.helpers.CampaignHelper;
import inquirly.com.inquirlycatalogue.utils.CatalogSharedPrefs;
import inquirly.com.inquirlycatalogue.activities.WebViewActivity;
import inquirly.com.inquirlycatalogue.utils.InternetConnectionStatus;
import inquirly.com.inquirlycoolberry.Fragments.CoolberryItemsTabFragment;

public class CoolberryItemsTabActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private Intent intent;
    private static Menu mMenu;
    private String mCampaignType;
    private ProgressDialog pDialog;
    private static Context context;
    public String mCampaignId,color;
    private SharedPreferences mSharedPrefs;
    public  ArrayList<Campaign> campaignList;
    public  ArrayList<CampaignDbItem> itemList;
    private static CoordinatorLayout coordinatorLayout;
    private static final String TAG = "CoolTabActivity";
    private ApplicationController appInstance = ApplicationController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        color =appInstance.getImage("color_1");
        intent = getIntent();
        setContentView(R.layout.activity_coolberry_items_tab);
        Toolbar toolbar = (Toolbar) findViewById(R.id.food_tab);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor(color));

        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorTabLayout);
        context = CoolberryItemsTabActivity.this;
        initToolbar();
        Log.i(TAG,"--->entered");

        pDialog = new ProgressDialog(CoolberryItemsTabActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        // add the listener so it will tries to suggest while the user types
        mCampaignId = intent.getStringExtra("campaignId");
        Log.i(TAG,"campaignId received--->" + mCampaignId);
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
                        Log.i(TAG,"entered----4000");
                        sleep(4000);
                        buildCampaignDetails(mCampaignId);
                        SharedPreferences.Editor editor = mSharedPrefs.edit();
                        editor.putBoolean(CatalogSharedPrefs.IS_TABLE_LOADED +" of "+mCampaignId, true);
                        editor.commit();
                        pDialog.dismiss();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        pDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Something went wrong...please try again later", Toast.LENGTH_SHORT).show();
                    }
                }
            }.start();
        } else {
            Log.i(TAG,"entered----initviewpager");
            initViewPagerAndTabs();
            pDialog.dismiss();
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
        }
    }

    public static void setCartCount(){
        if (mMenu != null) {
            MenuItem item = mMenu.findItem(R.id.action_count);
            LayerDrawable icon = (LayerDrawable) item.getIcon();
            int size = ApplicationController.getInstance().getCartItemCount();
            CartCount.setBadgeCount(context, icon, size);
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.food_tab);
        setSupportActionBar(toolbar);

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "Montserrat-Regular.ttf");
        for(int i = 0; i < toolbar.getChildCount(); i++){
            View view = toolbar.getChildAt(i);
            if(view instanceof TextView){
                TextView tv = (TextView) view;
                if(tv.getText().equals(this.getTitle())) {
                    tv.setTypeface(font);
                    tv.setTextSize(18);
                    tv.setTextColor(getResources().getColor(R.color.white));
                    tv.setAllCaps(true);
                    break;
                }
            }
        }
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
        ViewPager viewPager = (ViewPager) findViewById(R.id.itemTabPager);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
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
            itemList = myDB.getCampaignDetailForSubCategory(mCampaignId, subcategory.get(i));
            pagerAdapter.addFragment(CoolberryItemsTabFragment.createInstance(itemList, CoolberryItemsTabActivity.this.mCampaignId),
                    subcategory.get(i));
            Log.i("subcategory id is ", subcategory.get(i));
        }
        myDB.close();

        viewPager.setAdapter(pagerAdapter);
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.food_tabLayout);
        tabLayout.setBackgroundColor(Color.parseColor(color));
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                int tabLayoutWidth = tabLayout.getWidth();
                DisplayMetrics metrics = new DisplayMetrics();
                CoolberryItemsTabActivity.this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
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
//        int tabCount = tabLayout.getTabCount();
//        viewPager.setCurrentItem(tabCount);
        viewPager.getAdapter().notifyDataSetChanged();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
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
        int size = ApplicationController.getInstance().getCartItemCount();
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
                            sleep(3000);
                            refreshSubcategories(mCampaignId);
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                            Toast.makeText(getApplicationContext(), "Something went wrong...please try again later", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.start();
            }
            else {
                Toast.makeText(getApplicationContext(),"Unable to connect to server. " +
                        "please check your network connection",Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        if (id == R.id.action_count) {
            finish();
            startActivity(getIntent());
            Intent cartIntent = new Intent(this, CoolBerryCartActivity.class);
            cartIntent.putExtra("campaign_id", mCampaignId);
            Log.i(TAG,"check campaign_id sent" + mCampaignId);
            startActivity(cartIntent);
        }
        if(id==android.R.id.home){
            Log.i(TAG,"back clicked");
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void buildCampaignDetails(final String campaignId) {
        if (InternetConnectionStatus.checkConnection(this)) {
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

                            if (CoolberryItemsTabActivity.this.mCampaignType.equals(ApiConstants.CAMPAIGN_TYPE_FEEDBACK)) {
                                try {
                                    //bug:SI-296
                                    //the json format for feedback campaigns changed, so now it's a campaign url instead of feedback link
                                    String feedbackUrl = response.getJSONObject("campaign").getString("campaign_url");
                                    Intent feedbackViewIntent = new Intent(CoolberryItemsTabActivity.this, WebViewActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString(ApiConstants.CAMPAIGN_FEEDBACK_LINK, feedbackUrl);
                                    feedbackViewIntent.putExtras(bundle);
                                    startActivity(feedbackViewIntent);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                Log.i(TAG, "Loading campaign details for campaign for="+ campaignId);
                                campaignList = CampaignHelper.buildCampaignDetails(response, CoolberryItemsTabActivity.this, campaignId);
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
//                                            if (item.getMedia().length > 1) {
//                                                dbItem.setMediaImg1(item.getMedia()[1].getUrl());
//                                                dbItem.setMediaImg2(item.getMedia()[2].getUrl());
//                                                dbItem.setMediaImg3(item.getMedia()[3].getUrl());
//                                                dbItem.setMediaImg4(item.getMedia()[4].getUrl());
//                                                dbItem.setMediaImg5(item.getMedia()[5].getUrl());
//                                            }
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
                                initViewPagerAndTabs();
                            }
                        }
                        @Override
                        public void onError(VolleyError error) {
                            Log.e(CoolberryItemsTabActivity.this.TAG, "Error fetching campaigns=" + error.getMessage());
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
        pDialog.dismiss();
        startActivity(getIntent());
        finish();
        Log.d("campaignId is :", campaignId);
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

    public static void addProduct(String itemName){
        Snackbar snackbar = Snackbar.make(coordinatorLayout ,itemName + " - is added to your cart",Snackbar.LENGTH_SHORT);
        View snackView = snackbar.getView();
        TextView textView = (TextView) snackView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(context.getResources().getColor(R.color.customColor));
        snackbar.show();
    }
}