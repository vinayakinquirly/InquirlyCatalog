package inquirly.com.inquirlycatalogue.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import inquirly.com.inquirlycatalogue.R;
import inquirly.com.inquirlycatalogue.activities.DetailViewActivity;
import inquirly.com.inquirlycatalogue.activities.SearchActivity;
import inquirly.com.inquirlycatalogue.adapters.RecyclerCategoryAdapter;
import inquirly.com.inquirlycatalogue.models.CampaignDbItem;
import inquirly.com.inquirlycatalogue.utils.ApiConstants;
import inquirly.com.inquirlycatalogue.utils.RecyclerItemClickListener;


public class ItemTabFragment extends Fragment  {

    public final static String ITEMS_KEY = "PartThreeFragment$ItemsCount";
    // private  Campaign.FormAttributes.SubCategories.Item[] mItems;
    private ArrayList<CampaignDbItem> mItems;
    private final static String TAG = ItemTabFragment.class.getSimpleName();
    private String mCampaignId;

    RecyclerView recyclerView;
    RecyclerCategoryAdapter mAdapter;

    public static ItemTabFragment createInstance(ArrayList<CampaignDbItem> itemList, String campaignId) {
        ItemTabFragment partThreeFragment = new ItemTabFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ITEMS_KEY, itemList);
        bundle.putString(ApiConstants.CAMPAIGN_ID, campaignId);
        partThreeFragment.setArguments(bundle);
        //mItems = items;
        return partThreeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //  Log.i(TAG, "subcategories count=" + mSubCategories.length);

        setHasOptionsMenu(true);
        Bundle bundle = getArguments();
        mItems = (ArrayList<CampaignDbItem>) bundle.getSerializable(ITEMS_KEY);
        mCampaignId = bundle.getString(ApiConstants.CAMPAIGN_ID);
        Log.i(TAG, "campaign_id=" + mCampaignId);

        recyclerView = (RecyclerView) inflater.inflate(
                R.layout.fragment_item_tab, container, false);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mAdapter = new RecyclerCategoryAdapter(mItems, this.getActivity());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        // RecyclerViewGridSpacing spacing = new RecyclerViewGridSpacing(getActivity(), R.dimen.recycler_item_spacing);
        // recyclerView.addItemDecoration(spacing);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Intent detailActivityIntent = new Intent(getActivity(), DetailViewActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("itemsList", mItems);
                        bundle.putInt("itemPosition", position);
                        Log.i(TAG, "ItemTabFragment clicked position=" + position);
                        bundle.putString(ApiConstants.CAMPAIGN_ID, ItemTabFragment.this.mCampaignId);
                        Log.i(TAG, "campaign_id=" + ItemTabFragment.this.mCampaignId);
                        detailActivityIntent.putExtras(bundle);
                        startActivity(detailActivityIntent);
                    }
                })
        );

        mAdapter.notifyDataSetChanged();
        return recyclerView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_cart, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            Intent i = new Intent(getActivity(), SearchActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(ApiConstants.CAMPAIGN_ID, ItemTabFragment.this.mCampaignId);
            i.putExtras(bundle);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}