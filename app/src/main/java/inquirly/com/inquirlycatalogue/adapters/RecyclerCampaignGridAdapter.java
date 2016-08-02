package inquirly.com.inquirlycatalogue.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.graphics.Palette;
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
import inquirly.com.inquirlycatalogue.utils.SQLiteDataBase;

public class RecyclerCampaignGridAdapter extends RecyclerView.Adapter<RecyclerCampaignGridAdapter.ViewHolder> {
    private ArrayList<CampaignItemData> mItems;
    private Context mContext;
    private static final String TAG = "RecyclerCampaignAdapter";
    public String url;
    SQLiteDataBase database;

    public RecyclerCampaignGridAdapter(ArrayList<CampaignItemData> items, Context ctx) {

       this.mItems = items;
        this.mContext = ctx;
    }

    @Override
    public RecyclerCampaignGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_campaign_item, null);

        ViewHolder viewHolder = new ViewHolder(itemLayoutView,mContext);
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
            Picasso.with(mContext).load("file://"+path).resize(300, 300).placeholder(R.mipmap.placeholder).into(viewHolder.imgViewIcon);
            Bitmap bitmap = BitmapFactory.decodeFile(item.getPreview().getImage());

           if(bitmap != null && !bitmap.isRecycled()) {
               Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                   @Override
                   public void onGenerated(Palette palette) {
                       int mutedColor = palette.getMutedColor(R.attr.colorPrimary);
                       viewHolder.txtViewTitle.setBackgroundColor(mutedColor);
                       viewHolder.txtViewTitle.getBackground().setAlpha(230);
                   }
               });
           }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
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