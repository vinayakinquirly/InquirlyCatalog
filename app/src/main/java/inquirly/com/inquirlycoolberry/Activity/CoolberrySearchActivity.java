 package inquirly.com.inquirlycoolberry.Activity;

import java.util.List;
import android.util.Log;
import android.view.Menu;
import android.os.Bundle;
import android.view.View;
import java.util.ArrayList;
import android.view.Window;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.content.Context;
import android.graphics.Typeface;
import android.view.WindowManager;
import inquirly.com.inquirlycatalogue.R;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.SearchView;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.design.widget.CoordinatorLayout;
import inquirly.com.inquirlycatalogue.utils.ApiConstants;
import inquirly.com.inquirlycatalogue.utils.SQLiteDataBase;
import inquirly.com.inquirlycatalogue.models.CampaignDbItem;
import inquirly.com.inquirlycoolberry.Adapters.CoolberryItemsTabAdapter;

public class CoolberrySearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private String mCampaignId;
    boolean isRecyclerviewoffset;
    private static Context context;
    private RecyclerView recyclerView;
    private ArrayList<CampaignDbItem> mItems;
    private CoolberryItemsTabAdapter mAdapter;
    private static CoordinatorLayout coordinatorLayout;
    private final static String TAG = "CoolberrySearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTheme(R.style.CoolberryTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coolberry_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.food_search_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        context = CoolberrySearchActivity.this;
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "Montserrat-Regular.ttf");
        for(int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if(view instanceof TextView){
                TextView tv = (TextView) view;
                if(tv.getText().equals(this.getTitle())){
                    tv.setTypeface(font);
                    break;
                }
            }
        }

        recyclerView = (RecyclerView) findViewById(R.id.food_search_list);
        Bundle bundle = getIntent().getExtras();
        mCampaignId = bundle.getString(ApiConstants.CAMPAIGN_ID);
        isRecyclerviewoffset = false;
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.food_search_cordinator);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        final MenuItem item = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(CoolberrySearchActivity.this);
        searchView.setIconified(false);
        searchView.setMaxWidth(1500);
        searchView.setImeOptions(searchView.getImeOptions() | EditorInfo.IME_FLAG_NO_FULLSCREEN |
                EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Do something when expanded
                        return true; // Return true to expand action view
                    }
                });
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        SQLiteDataBase myDb = new SQLiteDataBase(getApplicationContext());
        myDb.open();
        Log.d("string is", newText);
        mItems = myDb.getCampaignDetailForSearch(newText,mCampaignId);
        myDb.close();
        int n = mItems.size();
        Log.d("size is ", String.valueOf(n));
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        mAdapter = new CoolberryItemsTabAdapter(mItems, mCampaignId,"search",getApplicationContext());
        recyclerView.setAdapter(mAdapter);
        final List<CampaignDbItem> filteredModelList = filter(mItems, newText);
        mAdapter.setFilter(filteredModelList);
        mAdapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<CampaignDbItem> filter(List<CampaignDbItem> models, String query) {
        query = query.toLowerCase();

        final List<CampaignDbItem> filteredModelList = new ArrayList<>();
        for (CampaignDbItem model : models) {
            final String text = model.getItemName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void addProduct(String itemName){
        Snackbar snackbar = Snackbar.make(coordinatorLayout ,itemName + " - is added to your cart",Snackbar.LENGTH_SHORT);
        View snackView = snackbar.getView();
        TextView textView = (TextView) snackView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(context.getResources().getColor(R.color.customColor));
        snackbar.show();
    }
}