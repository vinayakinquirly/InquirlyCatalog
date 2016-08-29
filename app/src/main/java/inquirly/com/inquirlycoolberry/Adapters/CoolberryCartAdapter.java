package inquirly.com.inquirlycoolberry.Adapters;

import android.util.Log;
import java.util.HashMap;
import android.view.View;
import android.os.Handler;
import java.util.ArrayList;
import android.widget.Toast;
import android.widget.Button;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.TextView;
import android.widget.ImageView;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import com.squareup.picasso.Picasso;
import inquirly.com.inquirlycatalogue.R;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import inquirly.com.inquirlycatalogue.models.CartItem;
import inquirly.com.inquirlycatalogue.ApplicationController;
import inquirly.com.inquirlycatalogue.utils.CatalogSharedPrefs;
import inquirly.com.inquirlycoolberry.Activity.CoolBerryCartActivity;
import inquirly.com.inquirlycoolberry.Fragments.CoolberryItemsTabFragment;

public class CoolberryCartAdapter extends RecyclerView.Adapter<CoolberryCartAdapter.ViewHolder> {

    private int isClicked=0;
    private String propJson;
    private Context mContext;
    private SharedPreferences sharedPreferences;
    public static ArrayList<CartItem> mItems;
    public CartItem cartItem = new CartItem();
    public ArrayList<Integer> qtyToSent = new ArrayList<>();
    private static final String TAG = "RecyclerCartAdapter";
    private CustomizeCartItemAdapter customizeCartItemAdapter;
    private ApplicationController appInstance = ApplicationController.getInstance();
    private HashMap<String,ArrayList<Integer>> customizableItemCount = new HashMap<>();

    public CoolberryCartAdapter(Context context, ArrayList<CartItem> items) {
        this.mContext = context;
        this.mItems = items;
        Log.v(TAG,"--items--"+items.size());
        sharedPreferences = mContext.getSharedPreferences(CatalogSharedPrefs.KEY_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public CoolberryCartAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_cart_parent, null);

        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        final CartItem item = mItems.get(position);
        viewHolder.item_name.setText(item.getItemName());
        viewHolder.item_price.setText(item.getItemPrice());
        final int itemCount = item.getItemQuantity();
        final int qty = item.getItemQuantity();
        Log.v("qty is:", String.valueOf(qty));
        viewHolder.item_quantity.setText(String.valueOf(appInstance.getCartItems().
                get(position).getItemQuantity()));
        Log.v(TAG,"check position---" + position + "--qty--" + qty + "--name--" + item.getItemName());
        qtyToSent.add(qty);

        Typeface font = Typeface.createFromAsset(mContext.getApplicationContext().
                getAssets(), "Montserrat-Regular.ttf");
        viewHolder.item_name.setTypeface(font);
        viewHolder.item_price.setTypeface(font);
        viewHolder.item_quantity.setTypeface(font);

        Picasso.with(mContext).load("file://" + item.getItemImage()).resize(120,83)
                .placeholder(R.drawable.placeholder_check_2).centerCrop().into(viewHolder.item_img);

        viewHolder.customizeCartItemList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, true);
        viewHolder.customizeCartItemList.setLayoutManager(linearLayoutManager);

        viewHolder.cancel_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG,"check position---" + position);
                ApplicationController.getInstance().deleteCartItem(mItems.get(position));
                ApplicationController.getInstance().deleteCustomData(item.getItemCode());
                mItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mItems.size());
                if(mContext instanceof CoolBerryCartActivity){
                    if(ApplicationController.getInstance().getCartItems().size()==0){
                        CoolBerryCartActivity.cartAmountIsZero();
                    }
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                },500);
            }
        });

        Log.v(TAG,"item--" + item.getItemName() + "--id--" + item.getCampaignId());
        Log.v(TAG,"propJson----" + propJson);

        final int[] currentQty = new int[1];
        final int[] currentPrice = new int[1];
        final int[] newPrice = new int[1];
        final int[] newQty = new int[1];

        viewHolder.food_cart_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG,"add item clicked");
                currentQty[0] = Integer.parseInt(viewHolder.item_quantity.getText().toString());
                currentPrice[0] = Integer.parseInt(viewHolder.item_price.getText().toString());
                newPrice[0] = (currentPrice[0] / currentQty[0]);

                Log.v(TAG, " currentQty--->" + currentQty[0] + " currentPrice--->" + currentPrice[0]);
                newQty[0] = currentQty[0] + 1;
                newPrice[0] = (newPrice[0] * newQty[0]);

                Log.v(TAG, "Qty-->" + newQty[0] + " Price--->" + newPrice[0] +"---" +itemCount +
                        "----position----" + position);
                cartItem.setItemQuantity(newQty[0]);
                cartItem.setItemName(item.getItemName());
                cartItem.setItemPrice(String.valueOf(newPrice[0]));
                cartItem.setItemCode(item.getItemCode());
                cartItem.setItemImage(item.getItemImage());
                cartItem.setItemType(item.getItemType());
                cartItem.setCampaignId(item.getCampaignId());
                appInstance.saveItemInDb(cartItem);

                viewHolder.item_quantity.setText(String.valueOf(newQty[0]));
                viewHolder.item_price.setText(String.valueOf(newPrice[0]));

                propJson = sharedPreferences.getString(item.getCampaignId() + "_" + CatalogSharedPrefs.KEY_ITEM_PROPERTIES, null);
                customizeCartItemAdapter = new CustomizeCartItemAdapter(mContext,
                        cartItem.getItemQuantity(),propJson,item.getItemType(),item.getItemCode());

                //CoolBerryCartActivity.mTxtTotalPrice.setText(String.valueOf(newTotalPrice[0]));
                viewHolder.customizeCartItemList.setAdapter(customizeCartItemAdapter);
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
                    Log.v(TAG, " currentQty--->" + currentQty[0] + " currentPrice--->" + currentPrice[0]);
                    newQty[0] = currentQty[0] - 1;
                    newPrice[0] = (newPrice[0] * newQty[0]);

                    Log.v(TAG, "Qty-->" + newQty[0] + " Price--->" + newPrice[0]);
                    qtyToSent.remove(position);
                    qtyToSent.add(newQty[0]);

                    Log.v(TAG,"newQty---" + newQty[0]);
                    int num = newQty[0]+1;
                    appInstance.deleteCustomItemDataJson(item.getItemCode(),num);

                    viewHolder.item_quantity.setText(String.valueOf(newQty[0]));
                    viewHolder.item_price.setText(String.valueOf(newPrice[0]));

                    cartItem.setItemQuantity(newQty[0]);
                    cartItem.setItemName(item.getItemName());
                    cartItem.setItemPrice(String.valueOf(newPrice[0]));
                    cartItem.setItemCode(item.getItemCode());
                    cartItem.setItemImage(item.getItemImage());
                    cartItem.setItemType(item.getItemType());
                    cartItem.setCampaignId(item.getCampaignId());
                    appInstance.saveItemInDb(cartItem);

                    propJson = sharedPreferences.getString(item.getCampaignId() + "_" + CatalogSharedPrefs.KEY_ITEM_PROPERTIES, null);
                    customizeCartItemAdapter = new CustomizeCartItemAdapter(mContext,
                            cartItem.getItemQuantity(), propJson,item.getItemType(),item.getItemCode());

                    //CoolBerryCartActivity.mTxtTotalPrice.setText(String.valueOf(newTotalPrice[0]));
                    viewHolder.customizeCartItemList.setAdapter(customizeCartItemAdapter);
                }
            }
        });

        isClicked = 1;
        viewHolder.cart_item_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isClicked==1) {
                    isClicked=2;
                    Log.v(TAG,"isClicked---if--"+ isClicked + CoolberryItemsTabFragment.mItems);
                    ViewGroup.LayoutParams params = viewHolder.cart_item_card.getLayoutParams();
                    //params.height = 350;
                    params.height = 300;
                    viewHolder.cart_item_card.setLayoutParams(params);
                    int itemQty = appInstance.getCartItems().get(position).getItemQuantity();
                    propJson = sharedPreferences.getString(item.getCampaignId() + "_" + CatalogSharedPrefs.KEY_ITEM_PROPERTIES, null);
                    customizeCartItemAdapter = new CustomizeCartItemAdapter(mContext,
                            itemQty , propJson,item.getItemType(),item.getItemCode());
                    viewHolder.customizeCartItemList.setAdapter(customizeCartItemAdapter);
                }else {
                    isClicked=1;
                    Log.v(TAG,"isClicked---else--"+ isClicked);
                    ViewGroup.LayoutParams params = viewHolder.cart_item_card.getLayoutParams();
                    params.height = 130;
                    viewHolder.cart_item_card.setLayoutParams(params);
                }
                Log.v(TAG,"isClicked---"+ isClicked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView item_img;
        private ImageView cancel_img;
        private CardView cart_item_card;
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
            cart_item_card = (CardView)itemLayoutView.findViewById(R.id.cart_item_card);
            customizeCartItemList = (RecyclerView)itemLayoutView.findViewById(R.id.customizeCartItemList);
        }
    }
}