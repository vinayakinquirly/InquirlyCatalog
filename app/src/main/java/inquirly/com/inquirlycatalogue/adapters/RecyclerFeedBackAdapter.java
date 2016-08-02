package inquirly.com.inquirlycatalogue.adapters;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import inquirly.com.inquirlycatalogue.R;
import inquirly.com.inquirlycatalogue.models.CampaignItemData;

public class RecyclerFeedBackAdapter extends RecyclerView.Adapter<RecyclerFeedBackAdapter.ViewHolder> {
    private ArrayList<CampaignItemData> mItems;
    private Context mContext;
    private static final String TAG = "RecyclerFeedBackAdapter";

    public RecyclerFeedBackAdapter(ArrayList<CampaignItemData> items, Context ctx) {

        this.mItems = items;
        this.mContext = ctx;
    }

    @Override
    public RecyclerFeedBackAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_campaign_item, null);

        Typeface font = Typeface.createFromAsset(mContext.getApplicationContext().getAssets(), "Montserrat-Regular.ttf");
        ViewHolder viewHolder = new ViewHolder(itemLayoutView,mContext);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        CampaignItemData item = mItems.get(position);
        viewHolder.txtViewTitle.setText(item.getHash_tag());

        Picasso.with(mContext).load(item.getPreview().getImage()).resize(700,400).into(viewHolder.imgViewIcon);
        Typeface font = Typeface.createFromAsset(mContext.getApplicationContext().getAssets(), "Montserrat-Regular.ttf");
        viewHolder.txtViewTitle.setTypeface(font);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtViewTitle;
        public ImageView imgViewIcon;
        private Typeface mFont;

        public ViewHolder(View itemLayoutView,Context ctx) {
            super(itemLayoutView);
            txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.hastag_name);
            imgViewIcon = (ImageView) itemLayoutView.findViewById(R.id.campaign_img);

            mFont = Typeface.createFromAsset(ctx.getAssets(), "Montserrat-Regular.ttf");

            txtViewTitle.setTypeface(mFont);

        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

}