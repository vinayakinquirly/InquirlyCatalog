package inquirly.com.inquirlycoolberry.Adapters;

import android.view.View;
import java.util.ArrayList;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.TextView;
import android.widget.ImageView;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import com.squareup.picasso.Picasso;
import inquirly.com.inquirlycatalogue.R;
import android.support.v7.widget.RecyclerView;
import inquirly.com.inquirlycatalogue.utils.SQLiteDataBase;
import inquirly.com.inquirlycatalogue.models.CampaignItemData;

/**
 * Created by Vinayak on 7/13/2016.
 */
public class CoolberryMainAdapter extends RecyclerView.Adapter<CoolberryMainAdapter.ViewHolder> {

    public String url;
    private Context mContext;
    private SQLiteDataBase database;
    private ArrayList<CampaignItemData> mItems;
    private static final String TAG = "RecyclerCampaignAdapter";

    public CoolberryMainAdapter(ArrayList<CampaignItemData> items, Context ctx) {
        this.mItems = items;
        this.mContext = ctx;
    }

    @Override
    public CoolberryMainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_category, null);

        ViewHolder viewHolder = new ViewHolder(itemLayoutView, mContext);
        database = new SQLiteDataBase(mContext);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        CampaignItemData item = mItems.get(position);

        viewHolder.txtViewTitle.setText(item.getName());
        Typeface font = Typeface.createFromAsset(mContext.getApplicationContext().getAssets(), "Montserrat-Regular.ttf");
        viewHolder.txtViewTitle.setTypeface(font);
        viewHolder.txtViewTitle.setTypeface(font);
        try {
            String path = item.getPreview().getImage();
            // original - 920,480
            Picasso.with(mContext).load("file://" + path).resize(900,405).placeholder(R.drawable.placeholder_check_2)
                    .centerCrop().into(viewHolder.imgViewIcon);
            //Bitmap bitmap = BitmapFactory.decodeFile(item.getPreview().getImage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtViewTitle;
        public ImageView imgViewIcon;
        private Typeface mFont;

        public ViewHolder(View itemLayoutView, Context ctx) {
            super(itemLayoutView);
            txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.food_hastag_name);
            imgViewIcon = (ImageView) itemLayoutView.findViewById(R.id.food_campaign_img);
            mFont = Typeface.createFromAsset(ctx.getAssets(), "Montserrat-Regular.ttf");
            txtViewTitle.setTypeface(mFont);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}