package inquirly.com.inquirlycoolberry.Adapters;

import java.io.File;
import java.util.List;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import java.util.HashMap;
import android.os.Handler;
import org.json.JSONArray;
import java.util.ArrayList;
import org.json.JSONObject;
import android.view.Gravity;
import android.widget.TableRow;
import android.widget.Toast;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.view.ViewGroup;
import android.graphics.Color;
import org.json.JSONException;
import android.text.InputType;
import android.widget.TextView;
import android.content.Context;
import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.ImageView;
import android.graphics.Typeface;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.LinearLayout;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import com.squareup.picasso.Picasso;
import android.widget.CompoundButton;
import inquirly.com.inquirlycatalogue.R;
import android.support.annotation.IdRes;
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

    private Typeface font;
    private Context mContext;
    private CartItem cartItem;
    public String mCampaignId;
    public TextView multiLabel;
    public ArrayList<Fields> fieldList;
    public static String callFrom,color;
    private LinearLayout contentLayout ;
    private boolean hasEditText = false;
    public ArrayList<CampaignDbItem> dbitem;
    public JSONObject jsonObject = new JSONObject();
    private static final String TAG = "CoolItemsTabAdapter";
    private ArrayList<CartItem> cartItemList = new ArrayList<>();
    final HashMap<String,View> mOptionWidgets = new HashMap<>();
    final HashMap<String,String> mOptionValues = new HashMap<>();
    private HashMap<String, ArrayList<Fields>> propertyList = new HashMap<>();
    private ApplicationController appInstance = ApplicationController.getInstance();

    @IdRes
    private static final int ID_MALE = 1;

    @IdRes
    private static final int ID_FEMALE = 2;

    @IdRes
    private static final int ID_YES = 3;

    @IdRes
    private static final int ID_NO = 4;

    public CoolberryItemsTabAdapter(ArrayList<CampaignDbItem> itemsData,
                                    String mCampaignId,String callFrom,Context ctx) {

        this.dbitem = itemsData;
        this.mCampaignId =mCampaignId;
        this.callFrom = callFrom;
        this.mContext = ctx;
        Log.i(TAG,"itemdata---tab>" + itemsData.size() + itemsData.get(0).getType());
        cartItemList = appInstance.getCartItems();
        Log.i(TAG,"size----" +cartItemList.size());
        color = appInstance.getImage("color_1");
        font  = Typeface.createFromAsset(mContext.getApplicationContext().getAssets(), "Montserrat-Regular.ttf");
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

                if(propertyList.get(item.getType()).size()>1){
                    createDialogBox(item.getItemCode(),finalQty[0],item.getType());
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
        SharedPreferences prefs = mContext.getSharedPreferences(CatalogSharedPrefs.KEY_NAME, Context.MODE_PRIVATE);
        String propsJson = prefs.getString(mCampaignId + "_" + CatalogSharedPrefs.KEY_ITEM_PROPERTIES, null);
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

    public void createDialogBox(final String itemName, int itemQty,String type){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.layout_custom_item, null);
        contentLayout = (LinearLayout) dialogView.findViewById(R.id.custom_data_area);

        final TextView friendsName= (TextView)dialogView.findViewById(R.id.friend_name);
        final TextView itemNum = (TextView)dialogView.findViewById(R.id.item_count);
        final Button save_custom_item = (Button)dialogView.findViewById(R.id.btn_save_item);
        final Button cancel_save = (Button)dialogView.findViewById(R.id.btn_cancel_item);

        itemNum.setText(String.valueOf(itemQty));

        addSpecificationsToDialog(mOptionWidgets,contentLayout,propertyList.get(type));
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
                                        generateItemDetails(mOptionWidgets,mOptionValues, name, num, contentLayout))) {
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
                                    generateItemDetails(mOptionWidgets,mOptionValues, name, num, contentLayout))) {
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

    public void addSpecificationsToDialog(HashMap<String,View> mOptionWidgets,
                                          LinearLayout layout, ArrayList<Fields> itemFields){
        Log.i(TAG,"check fieldList received--" + fieldList.size() + fieldList.toString());

        try{
            for(Fields field : itemFields) {
                boolean hasChild = false;

                LinearLayout innerLayout = new LinearLayout(mContext);
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
                    innerLayout.setLayoutParams(new ViewGroup.LayoutParams(650,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    innerLayout.setPadding(0, 0, 0, 5);
                }else{
                    Log.i(TAG,"check SDK VERSION---" + Build.VERSION.SDK_INT);
                    innerLayout.setLayoutParams(new ViewGroup.LayoutParams(400,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    innerLayout.setPadding(0, 0, 0, 5);
                }
                innerLayout.setOrientation(LinearLayout.HORIZONTAL);
                innerLayout.setWeightSum(3.0f);

                EditText text;
                ArrayAdapter<String> spinnerArrayAdapter;
                if (field.getType().equals("number_input") || field.getType().equals("text_input") ) {
                    Log.v(TAG, "entered--" + "number input");
                    if (!field.getLabel().equals("Quantity")) {
                        TextView label = new TextView(mContext);
                        label.setText(field.getLabel());
                        label.setTypeface(font);
                        label.setTextSize(14);
                        label.setPadding(20,0,0,0);
                        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
                            label.setWidth(150);
                        }else{
                            multiLabel.setWidth(150);
                        }

                        if (label.getParent() != null)
                            ((ViewGroup) label.getParent()).removeView(label);
                        innerLayout.addView(label);

                        text = new EditText(mContext);
                        if (field.getType().equals("number_input")) {
                            text.setInputType(InputType.TYPE_CLASS_PHONE);
                        } else {
                            text.setInputType(InputType.TYPE_CLASS_TEXT);
                        }

                        if(Build.VERSION.SDK_INT> Build.VERSION_CODES.KITKAT){
                            text.setLayoutParams(new ViewGroup.LayoutParams(180, ViewGroup.LayoutParams.WRAP_CONTENT));
                        }else{
                            text.setLayoutParams(new ViewGroup.LayoutParams(180, ViewGroup.LayoutParams.WRAP_CONTENT));
                        }

                        text.setGravity(Gravity.START);
                        text.setTag(field.getLabel());
                        hasEditText = true;
                        innerLayout.addView(text);
                        hasChild = true;
                        mOptionWidgets.put(field.getLabel(), text);
                    }
                }
                else if(field.getType().equals("single_choice") || field.getType().equals("pricing_input")) {
                    Log.v(TAG,"entered--" + "single choice");
                    TextView label = new TextView(mContext);
                    label.setText(field.getLabel());
                    label.setTypeface(font);
                    label.setTextSize(14);
                    label.setPadding(20,0,0,0);
                    if(Build.VERSION.SDK_INT> Build.VERSION_CODES.KITKAT){
                        label.setWidth(150);
                    }else{
                        label.setWidth(150);
                    }
                    if(label.getParent() != null)
                        ((ViewGroup)label.getParent()).removeView(label);
                    innerLayout.addView(label);

                    Spinner spinnerSingle = new Spinner(mContext);
                    spinnerSingle.setTag(field.getLabel());
                    spinnerArrayAdapter = new ArrayAdapter<String>(mContext,
                            android.R.layout.simple_spinner_item,
                            field.getOptions()
                    ); //selected item will look like a spinner set from XML
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSingle.setAdapter(spinnerArrayAdapter);
                    if(Build.VERSION.SDK_INT> Build.VERSION_CODES.KITKAT){
                        spinnerSingle.setLayoutParams(new ViewGroup.LayoutParams(180, ViewGroup.LayoutParams.WRAP_CONTENT));
                    }else{
                        spinnerSingle.setLayoutParams(new ViewGroup.LayoutParams(180, ViewGroup.LayoutParams.WRAP_CONTENT));
                    }
                    innerLayout.addView(spinnerSingle);
                    hasChild = true;
                    mOptionWidgets.put(field.getLabel(), spinnerSingle);
                    mOptionValues.put(field.getLabel(),null);

                }else if(field.getType().equals("multiple_choice")){
                    LinearLayout labelVertical = new LinearLayout(mContext);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, 60,1.8f);
                    labelVertical.setOrientation(LinearLayout.VERTICAL);
                    labelVertical.setLayoutParams(params);
                    Log.v(TAG,"entered--" + "multiple choice");

                    multiLabel = new TextView(mContext);
                    multiLabel.setText(field.getLabel());
                    multiLabel.setTypeface(font);
                    multiLabel.setTextSize(14);
                    multiLabel.setPadding(20,0,0,0);
                    if(Build.VERSION.SDK_INT> Build.VERSION_CODES.KITKAT){
//                        multiLabel.setWidth(150);
                    }else{
                        multiLabel.setWidth(150);
                    }

                    TextView labelItemSel = new TextView(mContext);
                    labelItemSel.setText("0 selected");
                    labelItemSel.setTypeface(font);
                    labelItemSel.setTextSize(13);
                    labelItemSel.setWidth(100);
                    labelItemSel.setPadding(20,0,0,0);

                    if(multiLabel.getParent() != null)
                        ((ViewGroup)multiLabel.getParent()).removeView(multiLabel);
                    labelVertical.addView(multiLabel);
                    labelVertical.addView(labelItemSel);
                    innerLayout.addView(labelVertical);

                    Spinner spinnerMulti = new Spinner(mContext);
                    spinnerMulti.setTag(field.getLabel());
                    spinnerMulti.setScrollBarFadeDuration(0);

                    ArrayAdapter<String> myAdapter = new CustomArrayAdapter(mContext,
                            android.R.layout.simple_spinner_item, field.getOptions(),field.getLabel(),labelItemSel);
                    spinnerMulti.setAdapter(myAdapter);

                    if(Build.VERSION.SDK_INT> Build.VERSION_CODES.KITKAT){
                        spinnerMulti.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT,1.4f));
                    }else{
                        spinnerMulti.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT,1.4f));
                    }

                    innerLayout.addView(spinnerMulti);
                    hasChild = true;

                    mOptionWidgets.put(field.getLabel(), spinnerMulti);
                    mOptionValues.put(field.getLabel(),field.getOptions()[0]);
                    Log.i(TAG,"moptionValue--->" + mOptionValues.get(field.getLabel()));

                }else if(field.getType().equals("multiline_text")) {
                    Log.v(TAG,"entered--" + "multiline text");
                    TextView label = new TextView(mContext);
                    label.setText(field.getLabel());
                    label.setTypeface(font);
                    label.setTextSize(14);
                    label.setPadding(20,0,0,0);
                    if(Build.VERSION.SDK_INT> Build.VERSION_CODES.KITKAT){
                        label.setWidth(150);
                    }else{
                        label.setWidth(150);
                    }
                    if(label.getParent() != null)
                        ((ViewGroup)label.getParent()).removeView(label);
                    innerLayout.addView(label);

                    text = new EditText(mContext);
                    text.setMaxLines(3);
                    if(Build.VERSION.SDK_INT> Build.VERSION_CODES.KITKAT){
                        text.setLayoutParams(new ViewGroup.LayoutParams(180, ViewGroup.LayoutParams.WRAP_CONTENT));
                    }else{
                        text.setLayoutParams(new ViewGroup.LayoutParams(180, ViewGroup.LayoutParams.WRAP_CONTENT));
                    }
                    text.setGravity(Gravity.START);
                    text.setTag(field.getLabel());
                    hasEditText = true;
                    innerLayout.addView(text);
                    hasChild = true;
                    mOptionWidgets.put(field.getLabel(), text);

                }else if(field.getType().equals("gender_toggle")) {
                    Log.v(TAG,"entered--" + "gender toggle");
                    //radio button
                    TextView label = new TextView(mContext);
                    label.setText(field.getLabel());
                    label.setTypeface(font);
                    label.setTextSize(14);
                    label.setPadding(20,0,0,0);
                    if(Build.VERSION.SDK_INT> Build.VERSION_CODES.KITKAT){
                        label.setWidth(150);
                    }else{
                        label.setWidth(150);
                    }
                    if(label.getParent() != null)
                        ((ViewGroup)label.getParent()).removeView(label);
                    innerLayout.addView(label);

                    RadioGroup rbGroup = new RadioGroup(mContext);
                    if(Build.VERSION.SDK_INT> Build.VERSION_CODES.KITKAT){
                        rbGroup.setLayoutParams(new ViewGroup.LayoutParams(180, ViewGroup.LayoutParams.WRAP_CONTENT));
                    }else{
                        rbGroup.setLayoutParams(new ViewGroup.LayoutParams(180, ViewGroup.LayoutParams.WRAP_CONTENT));
                    }
                    rbGroup.setTag("GENDER");
                    RadioButton male = new RadioButton(mContext);
                    male.setText("Male");
                    male.setId(ID_MALE);

                    RadioButton female = new RadioButton(mContext);
                    female.setText("Female");
                    female.setId(ID_FEMALE);

                    rbGroup.addView(female);
                    rbGroup.addView(male);

                    innerLayout.addView(rbGroup);
                    hasChild = true;
                    mOptionWidgets.put(field.getLabel(), rbGroup);

                }else if(field.getType().equals("yes_no_toggle")) {
                    Log.v(TAG,"entered--" + "yes no toggle");
                    TextView label = new TextView(mContext);
                    label.setText(field.getLabel());
                    label.setTypeface(font);
                    label.setTextSize(14);
                    label.setPadding(20,0,0,0);
                    if(Build.VERSION.SDK_INT> Build.VERSION_CODES.KITKAT){
                        label.setWidth(150);
                    }else{
                        label.setWidth(150);
                    }
                    if(label.getParent() != null)
                        ((ViewGroup)label.getParent()).removeView(label);
                    innerLayout.addView(label);

                    RadioGroup rbGroup = new RadioGroup(mContext);
                    rbGroup.setOrientation(RadioGroup.HORIZONTAL);
                    rbGroup.setTag(field.getLabel());

                    RadioButton yes = new RadioButton(mContext);
                    yes.setText("Yes");
                    yes.setTag("Yes");
                    yes.setId(ID_YES);
                    rbGroup.addView(yes);

                    RadioButton no = new RadioButton(mContext);
                    no.setText("No");
                    no.setTag("No");
                    no.setId(ID_NO);
                    no.setChecked(true);
                    rbGroup.addView(no);

                    innerLayout.addView(rbGroup);
                    hasChild = true;
                    mOptionWidgets.put(field.getLabel(), rbGroup);
                }
                if(hasChild) {
                    Log.v(TAG, "Adding child" );
                    layout.addView(innerLayout);
                }
            }
            for(int i=0;i<mOptionWidgets.size();i++){
                mOptionWidgets.remove(i);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String generateItemDetails(HashMap<String,View> mOptionWidgets, HashMap<String,String> mOptionValues,
                                      String name,int num,LinearLayout contentLayout) throws JSONException {

        HashMap<String, String[]> options = new HashMap<>();
        String numValue = String.valueOf(num);
        jsonObject.put("tag",name);
        jsonObject.put("itemNum",numValue);

        for (int iChild = 0; iChild < contentLayout.getChildCount(); iChild++) {
            LinearLayout innerLayout = (LinearLayout) contentLayout.getChildAt(iChild);
            String key = (String) innerLayout.getChildAt(1).getTag();
            View widget = mOptionWidgets.get(key);
            Log.v(TAG,"key value---" + key +"--" + widget);

            if (widget instanceof EditText) {
                String qty_value = ((EditText) widget).getText().toString();
                Log.v(TAG, "edittext value--" + qty_value);
                options.put(key, new String[]{qty_value});
                jsonObject.put(key,qty_value);

            }else if (widget instanceof Spinner) {
                Spinner spinner = (Spinner) widget;
                String itemSelected = spinner.getSelectedItem().toString();
                Log.v(TAG, "check spinner---" + itemSelected);
                options.put(key, new String[]{itemSelected});
                Log.i(TAG,"check jsonObject-->" + jsonObject.toString());

                if(mOptionValues!=null){
                    String type = mOptionValues.get(key);
                    if(type==null){
                        jsonObject.put(key, itemSelected);
                    }
                }else{
                    jsonObject.put(key, itemSelected);
                }

            }else if ((widget instanceof RadioGroup) || (widget instanceof RadioButton)) {
                Log.v(TAG, "inside radio button group condition");
                RadioGroup rg = (RadioGroup) widget;
                Log.v(TAG,"check tag ---" + rg.getTag());
                if (rg.getCheckedRadioButtonId() == ID_FEMALE) {
                    options.put(key, new String[]{"female"});
                    Log.v(TAG, "female");
                    jsonObject.put(key,"female");

                }else if (rg.getCheckedRadioButtonId() == ID_MALE) {
                    Log.v(TAG, "male");
                    options.put(key, new String[]{"male"});
                    jsonObject.put(key,"male");

                }else if (rg.getCheckedRadioButtonId() == ID_YES) {
                    Log.v(TAG, "yes");
                    options.put(key, new String[]{"yes"});
                    jsonObject.put(key,"yes");

                }else if (rg.getCheckedRadioButtonId() == ID_NO) {
                    Log.v(TAG, "no");
                    options.put(key, new String[]{"no"});
                    jsonObject.put(key,"no");
                }
            }
            if (!jsonObject.has(key)) {
                jsonObject.put(key, mOptionValues.get(key));
            }
        }

        Log.i(TAG,"check json send ---" + jsonObject.toString());
        return jsonObject.toString();
    }

    public class CustomArrayAdapter extends ArrayAdapter<String> {
        private String[] objects;
        private Context context;
        private String fieldLabel;
        TextView labelItemSel;
        public ArrayList<String> selectedStrings = new ArrayList<>();

        public CustomArrayAdapter(Context context, int resourceId,
                                  String[] objects,String fieldLabel,TextView labelItemSel) {

            super(context, resourceId, objects);
            this.objects = objects;
            this.context = context;
            this.fieldLabel = fieldLabel;
            this.labelItemSel= labelItemSel;
            Log.i(TAG,"check fieldLabel--" + fieldLabel +"--" +selectedStrings.size());
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(final int position, View convertView, final ViewGroup parent) {
            LayoutInflater inflater=(LayoutInflater) context.getSystemService(  Context.LAYOUT_INFLATER_SERVICE );
            View row=inflater.inflate(R.layout.layout_spinner_adapter, parent, false);
            final TextView label=(TextView)row.findViewById(R.id.list_text);
            final CheckBox checkBox=(CheckBox) row.findViewById(R.id.checkBox);
            label.setText(objects[position]);


            checkBox.setChecked(false);
            if(selectedStrings.indexOf(label.getText().toString())>=0){
                labelItemSel.setText(String.valueOf(selectedStrings.size())+ " selected");
                checkBox.setChecked(true);
            }
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.i(TAG, "check item selected--" + objects.length + "---" + checkBox.isChecked());

                    if (checkBox.isChecked()) {
                        selectedStrings.add(objects[position]);
                        Log.i(TAG, "get itemsList---" + selectedStrings.size() + "--" + selectedStrings.toString());
                        String extras = selectedStrings.toString().replace("[", "").replace("]", "");
                        try {
                            jsonObject.put(fieldLabel, extras);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        labelItemSel.setText(String.valueOf(selectedStrings.size())+ " selected");
                    }else{
                        selectedStrings.remove(objects[position]);
                        Log.i(TAG, "get itemsList---" + selectedStrings.size() + "--" + selectedStrings.toString());
                        String extras = selectedStrings.toString().replace("[", "").replace("]", "");
                        try {
                            jsonObject.put(fieldLabel, extras);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        labelItemSel.setText(String.valueOf(selectedStrings.size())+ " selected");
                    }
                }
            });
            return row;
        }
    }
}
