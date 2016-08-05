package inquirly.com.inquirlycatalogue.adapters;

/**
 * Created by kaushal on 12-12-2015.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import inquirly.com.inquirlycatalogue.ApplicationController;
import inquirly.com.inquirlycatalogue.R;
import inquirly.com.inquirlycatalogue.activities.CartActivity;
import inquirly.com.inquirlycatalogue.models.CartItem;

public class RecyclerCartAdapter extends RecyclerView.Adapter<RecyclerCartAdapter.ViewHolder> {
    private ArrayList<CartItem> mItems;
    private Context mContext;


    public RecyclerCartAdapter(Context context,ArrayList<CartItem> items) {
        this.mItems = items;
        mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerCartAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_cart_item, null);
        // create ViewHolder
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        CartItem item = mItems.get(position);
        // - get data from your itemsData at this position
        // - replace the contents of the view with that itemsData
        viewHolder.item_name.setText(item.getItemName());
        viewHolder.item_price.setText(item.getItemPrice());
        String qty = String.valueOf(item.getItemQuantity());
        Log.d("qty is:", qty);
        viewHolder.item_quantity.setText(qty);
        Picasso.with(mContext).load(item.getItemImage()).resize(300,300).centerInside().into(viewHolder.item_img);
        Typeface font = Typeface.createFromAsset(mContext.getApplicationContext().getAssets(), "Montserrat-Regular.ttf");
        viewHolder.item_name.setTypeface(font);
        viewHolder.item_price.setTypeface(font);
        viewHolder.item_quantity.setTypeface(font);
        Picasso.with(mContext).load(item.getItemImage()).resize(150,150).centerInside().into(viewHolder.item_img);
        viewHolder.cancel_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mItems.remove(position);
                ApplicationController.getInstance().removeCartItem(mItems.get(position));

                if(mContext instanceof CartActivity){
                    ((CartActivity)mContext).getTotalamount();
                }

                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mItems.size());
            }
        });
    }

    // inner class to hold a reference to each item of RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        public TextView item_name,item_price,item_quantity;
        public ImageView item_img,cancel_img;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            item_name = (TextView) itemLayoutView.findViewById(R.id.item_name);
            item_price = (TextView) itemLayoutView.findViewById(R.id.item_price);
            item_quantity = (TextView) itemLayoutView.findViewById(R.id.item_qty);
            item_img=(ImageView)itemLayoutView.findViewById(R.id.item_img);
            cancel_img=(ImageView)itemLayoutView.findViewById(R.id.cancel_btn);
        }

        @Override
        public void onClick(View v) {

            if(v.equals(cancel_img)){
                removeAt(getPosition());
            }
            else
            {

            }
        }
        public void removeAt(int position) {

        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}