package inquirly.com.inquirlycoolberry.Adapters;

import java.io.File;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import java.util.ArrayList;
import android.widget.Toast;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.content.Context;
import java.io.FileInputStream;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import com.squareup.picasso.Picasso;
import java.io.FileNotFoundException;
import android.graphics.BitmapFactory;
import inquirly.com.inquirlycatalogue.R;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;

import inquirly.com.inquirlycatalogue.models.CampaignDbItem;
import inquirly.com.inquirlycatalogue.models.CartItem;
import inquirly.com.inquirlycatalogue.ApplicationController;
import inquirly.com.inquirlycoolberry.Activity.CoolBerryCartActivity;
import inquirly.com.inquirlycoolberry.Fragments.CoolberryItemsTabFragment;

public class CoolberryCartAdapter extends RecyclerView.Adapter<CoolberryCartAdapter.ViewHolder>{

    private String propJson;
    private Context mContext;
    public CartItem cartItem = new CartItem();
    public  ArrayList<CartItem> mItems;
    public  ArrayList<CampaignDbItem> dbItems;
    private LinearLayoutManager linearLayoutManager;
    private static final String TAG = "RecyclerCartAdapter";
    private CustomizeCartItemAdapter customizeCartItemAdapter;
    private ArrayList<Integer> customizableItemCount = new ArrayList<>();
    private ApplicationController appInstance = ApplicationController.getInstance();

    public CoolberryCartAdapter(Context context, ArrayList<CartItem> items, String propJson) {
        this.mContext = context;
        this.mItems = items;
        this.propJson = propJson;
        Log.i(TAG,"check propsjson--->" + propJson +"--items--"+items.size());
    }

    @Override
    public CoolberryCartAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_cart_parent, null);

        // create ViewHolder
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        final CartItem item = mItems.get(position);
        viewHolder.item_name.setText(item.getItemName());
        viewHolder.item_price.setText(item.getItemPrice());
        final int itemCount = item.getItemQuantity();
        String qty = String.valueOf(item.getItemQuantity());
        customizableItemCount.add(position,itemCount);
        Log.d("qty is:", qty);
        viewHolder.item_quantity.setText(qty);

        Typeface font = Typeface.createFromAsset(mContext.getApplicationContext().
                getAssets(), "Montserrat-Regular.ttf");
        viewHolder.item_name.setTypeface(font);
        viewHolder.item_price.setTypeface(font);
        viewHolder.item_quantity.setTypeface(font);

        Picasso.with(mContext).load("file://" + item.getItemImage()).resize(120,83)
        .placeholder(R.drawable.placeholder_check_2).centerCrop().into(viewHolder.item_img);

        viewHolder.customizeCartItemList.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,true);
        viewHolder.customizeCartItemList.setLayoutManager(linearLayoutManager);

        viewHolder.cancel_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationController.getInstance().deleteCartItem(mItems.get(position));
                mItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mItems.size());

                if(mContext instanceof CoolBerryCartActivity){
                    if(ApplicationController.getInstance().getCartItems().size()==0){
                        CoolBerryCartActivity.cartAmountIsZero();
                    }
                }
            }
        });

        final float[] currentTotalPrice = new float[1];
        final float[] newTotalPrice = new float[1];
        final int[] currentQty = new int[1];
        final int[] currentPrice = new int[1];
        final int[] newPrice = new int[1];
        final int[] newQty = new int[1];

        viewHolder.food_cart_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i(TAG,"add item clicked");
                currentQty[0] = Integer.parseInt(viewHolder.item_quantity.getText().toString());
                currentPrice[0] = Integer.parseInt(viewHolder.item_price.getText().toString());
                newPrice[0] = (currentPrice[0] / currentQty[0]);

                Log.i(TAG, " currentQty--->" + currentQty[0] + " currentPrice--->" + currentPrice[0]);
                newQty[0] = currentQty[0] + 1;
                newPrice[0] = (newPrice[0] * newQty[0]);

                Log.i(TAG, "Qty-->" + newQty[0] + " Price--->" + newPrice[0] +"---" +itemCount +
                        "----position----" + position);
                //customizableItemCount.add(position,newQty[0]);
                customizableItemCount.add(position,newQty[0]);
                cartItem.setItemQuantity(newQty[0]);
                cartItem.setItemName(item.getItemName());
                cartItem.setItemPrice(String.valueOf(newPrice[0]));
                cartItem.setItemCode(item.getItemCode());
                cartItem.setItemImage(item.getItemImage());
                appInstance.saveItemInDb(cartItem);

                Log.i(TAG,"on adding--->" + customizableItemCount.get(0));
//                customizeCartItemAdapter = new CustomizeCartItemAdapter(mContext,customizableItemCount,
//                        CoolberryItemsTabFragment.mItems,propJson);

                viewHolder.item_quantity.setText(String.valueOf(newQty[0]));
                viewHolder.item_price.setText(String.valueOf(newPrice[0]));

                currentTotalPrice[0] = Float.parseFloat(CoolBerryCartActivity.mTxtTotalPrice.getText().toString());
                newTotalPrice[0] = currentTotalPrice[0] - currentPrice[0] + newPrice[0];

                CoolBerryCartActivity.mTxtTotalPrice.setText(String.valueOf(newTotalPrice[0]));
//                viewHolder.customizeCartItemList.setAdapter(customizeCartItemAdapter);
            }
        });

        viewHolder.food_cart_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentQty[0] = Integer.parseInt(viewHolder.item_quantity.getText().toString());
                currentPrice[0] = Integer.parseInt(viewHolder.item_price.getText().toString());

                if(currentQty[0]==1) {
                    Toast.makeText(mContext, "minimum Quantity reached.", Toast.LENGTH_SHORT).show();
                }else {
                    newPrice[0] = (currentPrice[0] / currentQty[0]);
                    Log.i(TAG, " currentQty--->" + currentQty[0] + " currentPrice--->" + currentPrice[0]);
                    newQty[0] = currentQty[0] - 1;
                    newPrice[0] = (newPrice[0] * newQty[0]);

                    Log.i(TAG, "Qty-->" + newQty[0] + " Price--->" + newPrice[0]);
//                    customizableItemCount.set(position,newQty[0]);
//                    customizeCartItemAdapter = new CustomizeCartItemAdapter(mContext,customizableItemCount,
//                            CoolberryItemsTabFragment.mItems,propJson);

                    viewHolder.item_quantity.setText(String.valueOf(newQty[0]));
                    viewHolder.item_price.setText(String.valueOf(newPrice[0]));

                    currentTotalPrice[0] = Float.parseFloat(CoolBerryCartActivity.mTxtTotalPrice.getText().toString());
                    newTotalPrice[0] = currentTotalPrice[0] - currentPrice[0] + newPrice[0];

                    customizableItemCount.add(position,newQty[0]);
                    cartItem.setItemQuantity(newQty[0]);
                    cartItem.setItemName(item.getItemName());
                    cartItem.setItemPrice(String.valueOf(newPrice[0]));
                    cartItem.setItemCode(item.getItemCode());
                    cartItem.setItemImage(item.getItemImage());
                    appInstance.saveItemInDb(cartItem);

                    CoolBerryCartActivity.mTxtTotalPrice.setText(String.valueOf(newTotalPrice[0]));
                    //viewHolder.customizeCartItemList.setAdapter(customizeCartItemAdapter);
                }
            }
        });
//        customizeCartItemAdapter = new CustomizeCartItemAdapter(mContext,customizableItemCount,
//                dbItems,propJson);
    //    viewHolder.customizeCartItemList.setAdapter(customizeCartItemAdapter);
    }

    @Override
    public int getItemCount() {
        if(mItems.size()==0){
            CoolBerryCartActivity.mTxtTotalPrice.setText("0");
        }
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView cancel_img;
        public ImageView item_img;
        public RecyclerView customizeCartItemList;
        public Button food_cart_add,food_cart_sub;
        public TextView item_name,item_price,item_quantity;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            food_cart_add =(Button)itemLayoutView.findViewById(R.id.food_cart_add);
            food_cart_sub =(Button)itemLayoutView.findViewById(R.id.food_cart_sub);
            item_name     =(TextView) itemLayoutView.findViewById(R.id.food_item_name);
            item_price    =(TextView) itemLayoutView.findViewById(R.id.food_cart_price);
            item_quantity =(TextView) itemLayoutView.findViewById(R.id.food_item_qty);
            cancel_img    =(ImageView)itemLayoutView.findViewById(R.id.food_item_delete);
            item_img      =(ImageView) itemLayoutView.findViewById(R.id.food_item_image);
            customizeCartItemList = (RecyclerView)itemLayoutView.findViewById(R.id.customizeCartItemList);
        }

        @Override
        public void onClick(View v) {
            if(v.equals(cancel_img)){
                removeAt(getPosition());
            }
        }
        public void removeAt(int position) {
        }
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