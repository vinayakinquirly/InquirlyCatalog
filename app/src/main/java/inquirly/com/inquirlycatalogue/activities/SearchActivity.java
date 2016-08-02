package inquirly.com.inquirlycatalogue.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import inquirly.com.inquirlycatalogue.R;
import inquirly.com.inquirlycatalogue.adapters.RecyclerCategoryAdapter;
import inquirly.com.inquirlycatalogue.models.CampaignDbItem;
import inquirly.com.inquirlycatalogue.utils.ApiConstants;
import inquirly.com.inquirlycatalogue.utils.RecyclerItemClickListener;
import inquirly.com.inquirlycatalogue.utils.RecyclerViewGridSpacing;
import inquirly.com.inquirlycatalogue.utils.SQLiteDataBase;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private String mCampaignId;
    boolean isRecyclerviewoffset;
    private RecyclerView recyclerView;
    private RecyclerCategoryAdapter mAdapter;
    private ArrayList<CampaignDbItem> mItems;
    private final static String TAG = SearchActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppThemeBlue);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "Montserrat-Regular.ttf");
        for(int i = 0; i < toolbar.getChildCount(); i++)
        {
            View view = toolbar.getChildAt(i);
            if(view instanceof TextView){
                TextView tv = (TextView) view;
                if(tv.getText().equals(this.getTitle())){
                    tv.setTypeface(font);
                    break;
                }
            }
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_search);
        Bundle bundle = getIntent().getExtras();
        mCampaignId = bundle.getString(ApiConstants.CAMPAIGN_ID);
        isRecyclerviewoffset = false;
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Intent detailActivityIntent = new Intent(getApplicationContext(), DetailViewActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("itemsList", mItems);
                        bundle.putInt("itemPosition", position);
                        Log.i(TAG, "ItemTabFragment clicked position=" + position);
                        bundle.putString(ApiConstants.CAMPAIGN_ID, SearchActivity.this.mCampaignId);
                        Log.i(TAG, "campaign_id=" + SearchActivity.this.mCampaignId);
                        detailActivityIntent.putExtras(bundle);
                        startActivity(detailActivityIntent);
                    }
                })
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        searchView.setIconified(false);
        searchView.setMaxWidth(1500);
        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // Do something when collapsed
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
            if(isRecyclerviewoffset == true) {
                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
                mAdapter = new RecyclerCategoryAdapter(mItems, getApplicationContext());
                recyclerView.setAdapter(mAdapter);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                RecyclerViewGridSpacing spacing = new RecyclerViewGridSpacing(SearchActivity.this, R.dimen.item_offsetnull);
                recyclerView.addItemDecoration(spacing);
                final List<CampaignDbItem> filteredModelList = filter(mItems, newText);
                mAdapter.setFilter(filteredModelList);
                mAdapter.notifyDataSetChanged();
            }
            else {
                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
                mAdapter = new RecyclerCategoryAdapter(mItems, getApplicationContext());
                recyclerView.setAdapter(mAdapter);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                RecyclerViewGridSpacing spacing = new RecyclerViewGridSpacing(SearchActivity.this, R.dimen.item_offset);
                recyclerView.addItemDecoration(spacing);
                isRecyclerviewoffset = true;
                final List<CampaignDbItem> filteredModelList = filter(mItems, newText);
                mAdapter.setFilter(filteredModelList);
                mAdapter.notifyDataSetChanged();
            }
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
        switch (item.getItemId())
        {
        case android.R.id.home:
        onBackPressed();
        return true;
       }
        return super.onOptionsItemSelected(item);
    }
}
