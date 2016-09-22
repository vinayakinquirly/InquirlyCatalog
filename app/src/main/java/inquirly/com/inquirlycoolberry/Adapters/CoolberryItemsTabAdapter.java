package inquirly.com.inquirlycoolberry.Adapters;

import java.io.File;
import java.util.List;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import java.util.HashMap;
import android.os.Handler;
import org.json.JSONArray;
import java.util.ArrayList;
import org.json.JSONObject;
import android.widget.Toast;
import android.widget.Button;
import android.view.ViewGroup;
import android.graphics.Color;
import org.json.JSONException;
import android.widget.TextView;
import android.content.Context;
import android.app.AlertDialog;
import android.widget.ImageView;
import android.graphics.Typeface;
import android.widget.LinearLayout;
import android.view.LayoutInflater;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import inquirly.com.inquirlycatalogue.R;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import inquirly.com.inquirlycatalogue.models.Fields;
import inquirly.com.inquirlycatalogue.models.CartItem;
import inquirly.com.inquirlycatalogue.ApplicationController;
import inquirly.com.inquirlycatalogue.models.CampaignDbItem;
import inquirly.com.inquirlycatalogue.utils.CatalogSharedPrefs;
import inquirly.com.inquirlycoolberry.Activity.CoolberrySearchActivity;
import inquirly.com.inquirlycoolberry.Activity.CoolberryItemsTabActivity;

/**
 * Created by Vinayak on 7/13/2016.
 */
public class CoolberryItemsTabAdapter extends RecyclerView.Adapter<CoolberryItemsTabAdapter.ViewHolder> {

    private Context mContext;
    private CartItem cartItem;
    public String mCampaignId,propsJson,termsJson;
    public ArrayList<Fields> fieldList;
    JSONObject termsJsonObject;
    public static String callFrom,color;
    private LinearLayout contentLayout ;
    private Gson gson = new Gson();
    public ArrayList<CampaignDbItem> dbitem;
    private static final String TAG = "CoolItemsTabAdapter";
    private ArrayList<CartItem> cartItemList = new ArrayList<>();
    final HashMap<String,View> mOptionWidgets = new HashMap<>();
    final HashMap<String,String> mOptionValues = new HashMap<>();
    private HashMap<String, ArrayList<Fields>> propertyList = new HashMap<>();
    private ApplicationController appInstance = ApplicationController.getInstance();

    public CoolberryItemsTabAdapter(ArrayList<CampaignDbItem> itemsData,
                                    String mCampaignId,String callFrom,Context ctx) {

        this.dbitem = itemsData;
        this.mCampaignId =mCampaignId;
        this.callFrom = callFrom;
        this.mContext = ctx;
        cartItemList = appInstance.getCartItems();
        Log.i(TAG,"size----" +cartItemList.size());
        color = appInstance.getImage("color_1");
        SharedPreferences prefs = mContext.getSharedPreferences(CatalogSharedPrefs.KEY_NAME, Context.MODE_PRIVATE);
        propsJson = prefs.getString(mCampaignId + "_" + CatalogSharedPrefs.KEY_ITEM_PROPERTIES, null);
        termsJson = prefs.getString(mCampaignId + "_" + CatalogSharedPrefs.KEY_TERMS_CONDITIONS, null);
        buildItemProperties();
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
        Log.i(TAG,"items----" + item.getItemName() + "----" + position + "---" + item.getType());

        try {
            termsJsonObject = new JSONObject(termsJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        viewHolder.txtViewTitle.setTextColor(Color.parseColor(color));
        viewHolder.food_item_price.setBackgroundColor(Color.parseColor(color));
        viewHolder.item_description.setTextColor(Color.parseColor(color));

        viewHolder.txtViewTitle.setText(item.getItemName());
        viewHolder.food_item_price.setText("Rs. " + String.valueOf(item.getPrice()));
        viewHolder.item_description.setText(item.getDescription());
        viewHolder.food_item_offer.setVisibility(View.GONE);

        Log.i(TAG,"position---" + position + "---" + item.getItemName());
        int previousQty=0;
        if(cartItemList.size()!=0) {
            for(int k=0;k<cartItemList.size();k++){
                if(item.getItemName().equals(cartItemList.get(k).getItemName())){
                    viewHolder.item_qty.setText(
                            String.valueOf(cartItemList.get(k).getItemQuantity()));
                    previousQty = cartItemList.get(k).getItemQuantity();
                }
            }
        }

        final int[] i = {previousQty};
        final int[] finalQty = new int[1];
        cartItem = new CartItem();

        viewHolder.item_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"QTY--2--->" + item.getItem_qty());
                i[0]++;
                viewHolder.item_qty.setText(String.valueOf(i[0]));
                Log.i(TAG,"check Qty--->" + viewHolder.item_qty.getText().toString());
                finalQty[0] = i[0];
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
                cartItem.setItemType(item.getType());
                cartItem.setCampaignId(item.getCampaignId());
                appInstance.saveItemInDb(cartItem);
                CoolberryItemsTabActivity.setCartCount();

                Log.i(TAG,"fieldList---" + item.getType());
                for(int i=0;i<propertyList.get(item.getType()).size();i++) {
                    Log.i(TAG, "check fieldList ---" +  propertyList.get(item.getType()).get(i).getLabel());
                }
                String note=null;
                if(propertyList.get(item.getType()).size()>1) {
                    try {
                        note = termsJsonObject.getString(item.getType());
                        Log.i(TAG,"check note rec--" + note);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (note!=null) {
                        createDialogBox(item.getItemCode(), finalQty[0], item.getType(),note);
                    }
                }
            }
        });

        viewHolder.item_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if (viewHolder.item_qty.getText().toString().equals("0")) {
                        Toast.makeText(mContext, "sorry! Cannot be less then 0", Toast.LENGTH_SHORT).show();
                    } else if (viewHolder.item_qty.getText().toString().equals("1")) {
                        Toast.makeText(mContext, "Please delete item from cart!", Toast.LENGTH_SHORT).show();
                    } else {
                        i[0] = Integer.parseInt(viewHolder.item_qty.getText().toString());
                        i[0]--;
                        Log.i(TAG, "check i[0]---" + i[0]);
                        cartItem.setItemName(item.getItemName());
                        cartItem.setItemQuantity(i[0]);

                        int totalprice = (i[0] * item.getPrice());

                        cartItem.setItemPrice(String.valueOf(totalprice));
                        cartItem.setItemCode(item.getItemCode());
                        Log.i(TAG, "check Imageurl--->" + item.getPrimaryImage());
                        cartItem.setItemImage(item.getPrimaryImage());
                        cartItem.setItemType(item.getType());
                        cartItem.setCampaignId(item.getCampaignId());
                        appInstance.saveItemInDb(cartItem);
                        viewHolder.item_qty.setText(String.valueOf(i[0]));
                        CoolberryItemsTabActivity.setCartCount();
                    }
            }
        });

        try{
            Uri uri = Uri.fromFile(new File(item.getPrimaryImage()));
            Log.i(TAG,"tab images---" + uri);

            // original .resize(600,350)
            // original .resize(900,465)

            Picasso.with(mContext).load(uri).resize(900,465).centerCrop().placeholder(R.drawable.placeholder_check_2)
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
            txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.item_title);
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

    public void buildItemProperties() {
        Log.i(TAG,"campaign ID---" + mCampaignId);

        propertyList = new HashMap<>();
        Log.i(TAG,"check propsJson---" + propsJson);

        if(propsJson == null ){
            Log.i(TAG, "item properites json is null");
        }else {
            try {
                JSONObject itemProperties = new JSONObject(propsJson);
                for (int keyIndex = 0; keyIndex < itemProperties.names().length(); keyIndex++) {
                    String key = (String) itemProperties.names().get(keyIndex);
                    Log.i(TAG, "Getting item properties for key=" + key);
                    JSONArray jsonArray = itemProperties.getJSONArray(key);
                    fieldList = new ArrayList<>();
                    for (int index = 0; index < jsonArray.length(); index++) {
                        //read all the item properties
                        JSONObject obj = jsonArray.getJSONObject(index);
                        Fields field = new Fields();
                        field.setType(obj.getString("type"));
                        field.setLabel(obj.getString("label"));
                        JSONArray optionsArray = obj.getJSONArray("options");
                        String[] options = new String[optionsArray.length()];
                        //loop through options array
                        for (int c = 0; c < optionsArray.length(); c++) {
                            options[c] = optionsArray.getString(c);
                            Log.i(TAG,"check options send---" + options[c]);
                        }
                        field.setOptions(options);
                        fieldList.add(field);
                        Log.i(TAG,"fieldList Size--" + fieldList.size());
                    }
                    propertyList.put(key, fieldList);
                }
            } catch (Exception ex) {
                Log.e(TAG, "Error parsing item properties from shared prefs:" + ex.getMessage());
            }
        }
    }

    public void createDialogBox(final String itemName, int itemQty,String type,String note){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.layout_custom_item, null);
        contentLayout = (LinearLayout) dialogView.findViewById(R.id.custom_data_area);

        final TextView friendsName= (TextView)dialogView.findViewById(R.id.friend_name);
        final TextView itemNum = (TextView)dialogView.findViewById(R.id.item_count);
        final TextView t_and_c= (TextView)dialogView.findViewById(R.id.terms_conditions);
        final Button save_custom_item = (Button)dialogView.findViewById(R.id.btn_save_item);
        final Button cancel_save = (Button)dialogView.findViewById(R.id.btn_cancel_item);

        t_and_c.setText(note);

        itemNum.setText(String.valueOf(itemQty));
        JSONObject data=null;
        CommonMethods.addSpecificationsToDialog(mOptionWidgets,mOptionValues,contentLayout,
                propertyList.get(type),mContext,data);
        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle("Select Options");
        dialogBuilder.setCancelable(false);

        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        save_custom_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = friendsName.getText().toString();
                final int num = Integer.parseInt(itemNum.getText().toString());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                            try {
                                if (appInstance.saveCustomItemJson(itemName, num,
                                        CommonMethods.generateItemDetails(mOptionWidgets,mOptionValues, name, num, contentLayout))) {
                                    Log.v(TAG, "saved successfully");
                                    save_custom_item.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_green_light));
                                    save_custom_item.setText("SAVED");
                                    Toast.makeText(mContext, "item saved successfully!", Toast.LENGTH_SHORT).show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.dismiss();
                                        }
                                    }, 500);
                                } else {
                                    Toast.makeText(mContext, "Please try again! Some Error occurred.", Toast.LENGTH_SHORT).show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.dismiss();
                                        }
                                    }, 500);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                    }
                },180);
            }
        });

        cancel_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final String name = friendsName.getText().toString();
                        final int num = Integer.parseInt(itemNum.getText().toString());
                        try {
                            if (appInstance.saveCustomItemJson(itemName, num,
                                    CommonMethods.generateItemDetails(mOptionWidgets,mOptionValues, name, num, contentLayout))) {
                                Log.v(TAG, "saved successfully");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                    }
                                }, 500);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },180);
            }
        });
    }
}
