package inquirly.com.inquirlycoolberry.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import java.util.ArrayList;
import android.view.MenuItem;
import android.content.Intent;
import android.view.ViewGroup;
import android.view.MenuInflater;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import inquirly.com.inquirlycatalogue.R;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import inquirly.com.inquirlycatalogue.utils.ApiConstants;
import inquirly.com.inquirlycatalogue.models.CampaignDbItem;
import inquirly.com.inquirlycatalogue.activities.SearchActivity;
import inquirly.com.inquirlycatalogue.activities.DetailViewActivity;
import inquirly.com.inquirlycatalogue.utils.RecyclerItemClickListener;
import inquirly.com.inquirlycoolberry.Activity.CoolberrySearchActivity;
import inquirly.com.inquirlycoolberry.Adapters.CoolberryItemsTabAdapter;

public class CoolberryItemsTabFragment extends Fragment {

    private View view;
    private String mCampaignId;
    private static Context context;
    private RelativeLayout layout_category;
    private static RecyclerView recyclerView;
    private CoolberryItemsTabAdapter mAdapter;
    private static CoordinatorLayout linearmain;
    public static ArrayList<CampaignDbItem> mItems;
    private final static String TAG = "CoolItemsTabFragment";
    public final static String ITEMS_KEY = "PartThreeFragment$ItemsCount";

    public static CoolberryItemsTabFragment createInstance(ArrayList<CampaignDbItem> itemList, String campaignId) {

        CoolberryItemsTabFragment partThreeFragment = new CoolberryItemsTabFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ITEMS_KEY, itemList);
        bundle.putString(ApiConstants.CAMPAIGN_ID, campaignId);
        partThreeFragment.setArguments(bundle);
        return partThreeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        view  =  inflater.inflate(R.layout.fragment_coolberry_items_tab, container, false);
        context = getActivity();
        Bundle bundle = getArguments();
        mItems = (ArrayList<CampaignDbItem>) bundle.getSerializable(ITEMS_KEY);
        mCampaignId = bundle.getString(ApiConstants.CAMPAIGN_ID);
        Log.i(TAG, "campaign_id=" + mCampaignId + "----items--->" + mItems.size());

        recyclerView  = (RecyclerView)view.findViewById(R.id.food_recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mAdapter = new CoolberryItemsTabAdapter(mItems, mCampaignId,"tabActivity",this.getActivity());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

/*        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent detailActivityIntent = new Intent(getActivity(), DetailViewActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("itemsList", mItems);
                        bundle.putInt("itemPosition", position);
                        Log.i(TAG, "ItemTabFragment clicked position=" + position);
                        bundle.putString(ApiConstants.CAMPAIGN_ID, CoolberryItemsTabFragment.this.mCampaignId);
                        Log.i(TAG, "campaign_id=" + CoolberryItemsTabFragment.this.mCampaignId);
                        detailActivityIntent.putExtras(bundle);
                        startActivity(detailActivityIntent);
                    }
                })
        );*/

        mAdapter.notifyDataSetChanged();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_cart, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            Intent i = new Intent(getActivity(), CoolberrySearchActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(ApiConstants.CAMPAIGN_ID, CoolberryItemsTabFragment.this.mCampaignId);
            i.putExtras(bundle);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}