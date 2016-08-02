package inquirly.com.inquirlycoolberry.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.io.FileInputStream;
import com.squareup.picasso.Picasso;
import java.io.FileNotFoundException;
import inquirly.com.inquirlycatalogue.R;
import inquirly.com.inquirlycatalogue.models.Campaign;
import inquirly.com.inquirlycatalogue.models.CartItem;
import inquirly.com.inquirlycatalogue.ApplicationController;
import inquirly.com.inquirlycatalogue.models.CampaignDbItem;
import inquirly.com.inquirlycoolberry.Activity.CoolberrySearchActivity;
import inquirly.com.inquirlycoolberry.Activity.CoolberryItemsTabActivity;

/**
 * Created by Vinayak on 7/13/2016.
 */
public class CoolberryItemsTabAdapter extends RecyclerView.Adapter<CoolberryItemsTabAdapter.ViewHolder> {

    private Context mContext;
    private CartItem cartItem;
    public  String mCampaignId;
    public static String callFrom,color;
    private CampaignDbItem[] items;
    public  ArrayList<CampaignDbItem> dbitem;
    private static final String TAG = "CoolItemsTabAdapter";
    private Campaign.FormAttributes.SubCategories.Item[] itemsData;
    private ApplicationController appInstance = ApplicationController.getInstance();

    public CoolberryItemsTabAdapter(ArrayList<CampaignDbItem> itemsData, String mCampaignId,String callFrom,Context ctx) {
        this.dbitem = itemsData;
        this.mCampaignId =mCampaignId;
        this.callFrom = callFrom;
        this.mContext = ctx;
        Log.i(TAG,"itemdata---tab>" + itemsData.size());
        color = appInstance.getImage("color_1");
    }

    @Override
    public CoolberryItemsTabAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_items_tab, null);
        Log.i(TAG,"--->entered");
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final CampaignDbItem item = dbitem.get(position);
        Log.i(TAG,"items----" + item.getItemName() + "----" + position);
        viewHolder.txtViewTitle.setText(item.getItemName());
        viewHolder.food_item_price.setText("Rs. " + String.valueOf(item.getPrice()));
        viewHolder.item_description.setText(item.getDescription());
        viewHolder.food_item_offer.setVisibility(View.GONE);
        Log.i(TAG,"QTY--1--->" + item.getItem_qty());

        final int[] i = {1};
        final int[] finalQty = new int[1];
        cartItem = new CartItem();

        viewHolder.item_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"QTY--2--->" + item.getItem_qty());
                viewHolder.item_qty.setText(String.valueOf(i[0]));
                Log.i(TAG,"check Qty--->" + viewHolder.item_qty.getText().toString());
                i[0]++;
                finalQty[0] = i[0]-1;
                Log.i(TAG,"final QTY--->" + finalQty[0]);
                if(callFrom.equals("search")){
                    CoolberrySearchActivity.addProduct(item.getItemName());
                }else{
                    CoolberryItemsTabActivity.addProduct(item.getItemName());
                }

                cartItem.setItemName(item.getItemName());
                cartItem.setItemQuantity(finalQty[0]);
                int totalprice = (finalQty[0] * item.getPrice());
                cartItem.setItemPrice(String.valueOf(totalprice));
                cartItem.setItemCode(item.getItemCode());
                Log.i(TAG,"check Imageurl--->" + item.getPrimaryImage());
                cartItem.setItemImage(item.getPrimaryImage());
                appInstance.saveItemInDb(cartItem);
               }
        });

        viewHolder.item_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewHolder.item_qty.getText().toString().equals("0")) {
                    Toast.makeText(mContext,"sorry! Cannot be less then 0",Toast.LENGTH_SHORT).show();
                }else {
                    i[0] = Integer.parseInt(viewHolder.item_qty.getText().toString());
                    i[0]--;
                    Log.i(TAG,"check i[0]---" + i[0]);
                    cartItem.setItemName(item.getItemName());
                    cartItem.setItemQuantity(i[0]);

                    int totalprice = (i[0] * item.getPrice());

                    cartItem.setItemPrice(String.valueOf(totalprice));
                    cartItem.setItemCode(item.getItemCode());
                    Log.i(TAG,"check Imageurl--->" + item.getPrimaryImage());
                    cartItem.setItemImage(item.getPrimaryImage());
                    appInstance.saveItemInDb(cartItem);
                    viewHolder.item_qty.setText(String.valueOf(i[0]));

                }
            }
        });

        try{
            String path = item.getPrimaryImage();
            Uri uri = Uri.fromFile(new File(item.getPrimaryImage()));
            Log.i(TAG,"tab images---" + uri);
           // original .resize(600,350)

            Picasso.with(mContext).load(uri).resize(900,480).centerCrop().placeholder(R.drawable.placeholder_check_2)
                    .into(viewHolder.imgViewIcon);
        }catch(Exception e) {
            e.printStackTrace();
        }

        Typeface font = Typeface.createFromAsset(mContext.getApplicationContext().getAssets(), "Montserrat-Regular.ttf");
        viewHolder.txtViewTitle.setTypeface(font);
    }

    public  static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtViewTitle,item_qty,food_item_price;
        public TextView item_description,food_item_offer;
        public Button item_add,item_sub;
        public ImageView imgViewIcon;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.text);
            imgViewIcon = (ImageView) itemLayoutView.findViewById(R.id.item_picture);
            item_qty = (TextView) itemLayoutView.findViewById(R.id.item_qty_grid);
            item_add = (Button) itemLayoutView.findViewById(R.id.item_add);
            item_sub = (Button) itemLayoutView.findViewById(R.id.item_sub);
            food_item_price = (TextView)itemLayoutView.findViewById(R.id.food_item_price);
            item_description = (TextView)itemLayoutView.findViewById(R.id.item_description);
            food_item_offer = (TextView)itemLayoutView.findViewById(R.id.food_item_offer);

            txtViewTitle.setTextColor(Color.parseColor(color));
            item_description.setTextColor(Color.parseColor(color));
            txtViewTitle.setTextColor(Color.parseColor(color));
            txtViewTitle.setTextColor(Color.parseColor(color));

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