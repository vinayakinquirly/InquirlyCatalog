package inquirly.com.inquirlycatalogue.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import inquirly.com.inquirlycatalogue.R;
import inquirly.com.inquirlycatalogue.models.Campaign;
import inquirly.com.inquirlycatalogue.models.CampaignDbItem;

public class RecyclerCategoryAdapter extends RecyclerView.Adapter<RecyclerCategoryAdapter.ViewHolder> {
    private Campaign.FormAttributes.SubCategories.Item[] itemsData;
    private Context mContext;
    private static final String TAG = "RecyclerCategoryAdapter";
    private CampaignDbItem[] items;
    ArrayList<CampaignDbItem> dbitem;


    public RecyclerCategoryAdapter(ArrayList<CampaignDbItem> itemsData, Context ctx) {
        this.dbitem = itemsData;
        this.mContext = ctx;
    }

    @Override
    public RecyclerCategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_main_grid_item, null);

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
      //  Campaign.FormAttributes.SubCategories.Item item = itemsData[position];
        CampaignDbItem item = dbitem.get(position);
        viewHolder.txtViewTitle.setText(item.getItemName());

        //Campaign.FormAttributes.SubCategories.Item[] items = itemsData[position].getItems();
        try {
            Log.i(TAG,"kaushal images--->" + item.getPrimaryImage());
            Picasso.with(mContext).load("file://"+item.getPrimaryImage()).resize(300, 300).placeholder(R.mipmap.placeholder).into(viewHolder.imgViewIcon);
            Bitmap bitmap = decodeFile(item.getPrimaryImage());

            if(bitmap!=null && !bitmap.isRecycled()) {
                Palette.generateAsync(bitmap,new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        int mutedColor = palette.getMutedColor(R.attr.colorPrimary);
                        viewHolder.txtViewTitle.setBackgroundColor(mutedColor);
                        viewHolder.txtViewTitle.getBackground().setAlpha(230);
                    }
                });
            }
        }

        catch(Exception e) {
            e.printStackTrace();
        }

        Typeface font = Typeface.createFromAsset(mContext.getApplicationContext().getAssets(), "Montserrat-Regular.ttf");
        viewHolder.txtViewTitle.setTypeface(font);

    }

public  static class ViewHolder extends RecyclerView.ViewHolder {

    public TextView txtViewTitle;
    public ImageView imgViewIcon;


    public ViewHolder(View itemLayoutView) {
        super(itemLayoutView);
        txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.text);
        imgViewIcon = (ImageView) itemLayoutView.findViewById(R.id.picture);

    }
}
    @Override
    public int getItemCount() {
        return dbitem.size();
    }

    public void setFilter(List<CampaignDbItem> items){
        dbitem = new ArrayList<>();
        dbitem.addAll(items);
        notifyDataSetChanged();
    }

    private Bitmap decodeFile(String f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE=70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }

}