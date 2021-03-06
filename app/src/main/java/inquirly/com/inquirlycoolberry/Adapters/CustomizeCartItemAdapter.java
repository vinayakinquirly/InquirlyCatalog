package inquirly.com.inquirlycoolberry.Adapters;

import org.json.JSONArray;
import org.json.JSONObject;
import android.view.ViewGroup;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import java.util.ArrayList;
import java.util.HashMap;
import android.graphics.Typeface;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.gson.Gson;
import android.content.Context;
import android.view.LayoutInflater;
import inquirly.com.inquirlycatalogue.R;
import android.content.SharedPreferences;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import inquirly.com.inquirlycatalogue.models.CartItem;
import inquirly.com.inquirlycatalogue.models.Fields;
import inquirly.com.inquirlycatalogue.models.PricingModel;
import inquirly.com.inquirlycatalogue.ApplicationController;
import inquirly.com.inquirlycatalogue.models.CampaignDbItem;
import inquirly.com.inquirlycatalogue.utils.CatalogSharedPrefs;
import inquirly.com.inquirlycatalogue.activities.DetailViewActivity;

/**
 * Created by Vinayak on 7/18/2016.
 */
public class CustomizeCartItemAdapter extends RecyclerView.Adapter<CustomizeCartItemAdapter.MyViewHolder> {

    private EditText text;
    private Typeface font;
    private String propJson;
    private Context mContext;
    private String mCampaignId;
    boolean hasEditText = false;
    public SharedPreferences prefs;
    private CampaignDbItem mSelectedItem;
    private ArrayList<CampaignDbItem> mItems;
    public static ArrayList<Fields> fieldList;
    private ArrayList<Integer> itemCount = new ArrayList<>();
    private static final String TAG = "CustomCartItemAdapter";
    private static HashMap<String,View> mOptionWidgets = new HashMap<>();
    public static HashMap<String, ArrayList<Fields>>  propertyList = new HashMap<>();
    private ApplicationController appInstance = ApplicationController.getInstance();

    @IdRes
    private static final int ID_MALE = 1;
    @IdRes
    private static final int ID_FEMALE = 2;
    @IdRes
    private static final int ID_YES = 3;
    @IdRes
    private static final int ID_NO = 4;

    public CustomizeCartItemAdapter(Context mContext,ArrayList<Integer> itemCount,
                                    ArrayList<CampaignDbItem> mItems,String propJson){

        this.mContext = mContext;
        this.itemCount = itemCount;
        this.mItems = mItems;
        Log.i(TAG,"check propsjson--->" + propJson);
        font = Typeface.createFromAsset(mContext.getApplicationContext().getAssets(), "Montserrat-Regular.ttf");
        buildItemProperties(propJson);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_cart_child, null);
        MyViewHolder viewHolder = new MyViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

//        Log.i(TAG,"itemcount--->" + itemCount.size() + "--mItems--" + mItems.size());

        //mSelectedItem = mItems.get(position);
//        final CampaignDbItem selectedItem = mSelectedItem;
//        holder.checkItemCount.setText("Item-" + String.valueOf(itemCount.get(position)));
//        Log.i(TAG,"property list---1>" + selectedItem.getType() + "---" +propertyList.size() +"---"+propertyList.get(selectedItem.getType()));

//        buildSpecsDialog(holder.contentLayout,propertyList.get(selectedItem.getType()));
//        generateitemdetails(selectedItem,holder.contentLayout);
    }

    public void generateitemdetails(CampaignDbItem selItem,LinearLayout contentLayout) {
        Log.i(TAG,"itemDetails---->" + selItem.getType() +"---" + contentLayout.getChildAt(0));
        Log.i(TAG,"--childCount--" + contentLayout.getChildCount());
        CartItem cartItem = new CartItem();
        cartItem.setCampaignId(mCampaignId);
        int basePrice = selItem.getPrice();
        String type = selItem.getType();
        JSONObject pricingJSON = getPricingJson();
        Log.i(TAG,"procing json---" + pricingJSON.toString());
        PricingModel pricingModel = null;
        float selectedWeight = 1;
        int selectedQuantity = 1;
        Gson gson = new Gson();
        try {
            if (pricingJSON.has(type)) {
                pricingModel = gson.fromJson(pricingJSON.getString(type), PricingModel.class);
            } else {
                //get BASIC pricing model
                pricingModel = gson.fromJson(pricingJSON.getString("BASIC"), PricingModel.class);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Log.i(TAG, "campaign_id=" + mCampaignId + "----" + contentLayout.getChildCount());
        for(int i=0;i<contentLayout.getChildCount();i++){
            LinearLayout innerLayout = (LinearLayout) contentLayout.getChildAt(i);
            Log.i(TAG,"------" + innerLayout.getChildAt(1).getTag());
        }
        HashMap<String, String[]> options = new HashMap<>();
        for (int iChild = 0; iChild < contentLayout.getChildCount(); iChild++) {

            LinearLayout innerLayout = (LinearLayout) contentLayout.getChildAt(iChild);
            Log.i(TAG,"child at---" + innerLayout.getId() + "---" + innerLayout.getChildAt(1).getTag());
            String key = (String) innerLayout.getChildAt(1).getTag();
            Log.i(TAG, "widget key=" + key);
            View widget = mOptionWidgets.get(key);
            if (widget instanceof EditText) {
                String qty_value = ((EditText) widget).getText().toString();
                //selectedQuantity = Integer.parseInt(qty_value);
                options.put(key, new String[]{qty_value});
            }
            else if (widget instanceof Spinner)
            {
                Spinner spinner = (Spinner) widget;
                String value = spinner.getSelectedItem().toString();
                if (key.equals("Weight")) {
                    if (value.equals("0.5")) {
                        String price = pricingModel.Weight[0].getPrice();
                        int weightPrice = Integer.parseInt(price.substring(1, price.length()));
                        basePrice += weightPrice;
                    }
                    selectedWeight = Float.parseFloat(value);
                } else if (key.equals("Quantity")) {
                    // selectedQuantity = Integer.parseInt(value);
                }
                options.put(key, new String[]{value});
            } else if ((widget instanceof RadioGroup) || (widget instanceof RadioButton)) {
                Log.i(TAG, "inside radio button group condition");
                RadioGroup rg = (RadioGroup) widget;
                if (rg.getTag().equals("GENDER")) {
                    if (rg.getCheckedRadioButtonId() == ID_FEMALE) {
                        options.put(key, new String[]{"female"});
                    } else if (rg.getCheckedRadioButtonId() == ID_MALE) {
                        options.put(key, new String[]{"male"});
                    }
                } else if (rg.getTag().equals("Eggless")) {
                    if (rg.getCheckedRadioButtonId() == ID_YES) {
                        String price = pricingModel.Eggless[0].getPrice();
                        int egglessPrice = Integer.parseInt(price.substring(1, price.length()));
                        basePrice += egglessPrice;
                        options.put(key, new String[]{"yes"});
                    } else if (rg.getCheckedRadioButtonId() == ID_NO) {
                        options.put(key, new String[]{"no"});
                    }
                }
            }
            if (mContext instanceof DetailViewActivity) {
                ((DetailViewActivity) mContext).endActivity();
            }
        }

        float totalPrice = 0.0f;
        if (selectedWeight == 0.5) {
            totalPrice = (basePrice / 2) * (selectedQuantity);
        } else {
            totalPrice = basePrice * (selectedQuantity * selectedWeight);
        }

        /*cartItem.setItemSpecs(options);
        cartItem.setItemImage(selItem.getPrimaryImage());
        cartItem.setItemName(selItem.getItemName());
        cartItem.setItemPrice(totalPrice + "");
        cartItem.setItemQuantity(selectedQuantity);
        cartItem.setItemCode(selItem.getItemCode());
        appInstance.addCartItem(cartItem);
        Toast.makeText(mContext,
                "Item added to the cart!",
                Toast.LENGTH_LONG).show();

        Intent cartActivity = new Intent(mContext, CartActivity.class);
        cartActivity.putExtra("campaign_id", mCampaignId);
        mContext.startActivity(cartActivity);*/
    }

    public void buildSpecsDialog(LinearLayout layout, ArrayList<Fields> itemFields) {
        try{
            for(Fields field : itemFields) {
                boolean hasChild = false;

                LinearLayout innerLayout = new LinearLayout(mContext);
                innerLayout.setLayoutParams(new ViewGroup.LayoutParams(
                        800,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                innerLayout.setPadding(5, 10, 5, 10);
                innerLayout.setOrientation(LinearLayout.HORIZONTAL);

                if (field.getType().equals("number_input") || field.getType().equals("text_input") ) {
                    TextView label = new TextView(mContext);
                    label.setText(field.getLabel());
                    label.setTypeface(font);
                    label.setTextSize(17);
                    label.setWidth(300);
                    if(label.getParent() != null)
                        ((ViewGroup)label.getParent()).removeView(label);
                    innerLayout.addView(label);

                    text = new EditText(mContext);
                    text.setInputType(InputType.TYPE_CLASS_PHONE);
                    text.setLayoutParams(new ViewGroup.LayoutParams(400, ViewGroup.LayoutParams.WRAP_CONTENT));
                    text.setGravity(Gravity.END);
                    text.setTag(field.getLabel());
                    hasEditText = true;
                    innerLayout.addView(text);
                    hasChild = true;
                    Log.i(TAG, "Adding singleline text");

                    mOptionWidgets.put(field.getLabel(), text);
                }
                else if(field.getType().equals("single_choice") || field.getType().equals("pricing_input")) {
                    TextView label = new TextView(mContext);
                    label.setText(field.getLabel());
                    label.setTypeface(font);
                    label.setTextSize(17);
                    label.setWidth(300);
                    if(label.getParent() != null)
                        ((ViewGroup)label.getParent()).removeView(label);
                    innerLayout.addView(label);

                    Spinner spinner = new Spinner(mContext);
                    spinner.setTag(field.getLabel());
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mContext,
                            android.R.layout.simple_spinner_item,
                            field.getOptions()
                    ); //selected item will look like a spinner set from XML
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(spinnerArrayAdapter);
                    spinner.setLayoutParams(new ViewGroup.LayoutParams(400, ViewGroup.LayoutParams.WRAP_CONTENT));
                    innerLayout.addView(spinner);
                    hasChild = true;
                    mOptionWidgets.put(field.getLabel(), spinner);
                    Log.i(TAG, "Adding single choice");

                }else if(field.getType().equals("multiline_text")) {
                    TextView label = new TextView(mContext);
                    label.setText(field.getLabel());
                    label.setTypeface(font);
                    label.setTextSize(17);
                    label.setWidth(300);
                    if(label.getParent() != null)
                        ((ViewGroup)label.getParent()).removeView(label);
                    innerLayout.addView(label);

                    text = new EditText(mContext);
                    text.setMaxLines(10);
                    text.setLayoutParams(new ViewGroup.LayoutParams(400, ViewGroup.LayoutParams.WRAP_CONTENT));
                    text.setGravity(Gravity.END);
                    text.setTag(field.getLabel());
                    hasEditText = true;
                    innerLayout.addView(text);
                    hasChild = true;
                    Log.i(TAG, "Adding multiline text");

                    mOptionWidgets.put(field.getLabel(), text);

                }else if(field.getType().equals("gender_toggle")) {
                    //radio button
                    TextView label = new TextView(mContext);
                    label.setText(field.getLabel());
                    label.setTypeface(font);
                    label.setTextSize(17);
                    label.setWidth(300);
                    if(label.getParent() != null)
                        ((ViewGroup)label.getParent()).removeView(label);
                    innerLayout.addView(label);

                    RadioGroup rbGroup = new RadioGroup(mContext);
                    rbGroup.setLayoutParams(new ViewGroup.LayoutParams(400, ViewGroup.LayoutParams.WRAP_CONTENT));
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
                    mOptionWidgets.put(field.getLabel(), rbGroup);
                    hasChild = true;

                }else if(field.getType().equals("yes_no_toggle")) {

                    Log.i(TAG, "Found yes no toggle=" + field.getLabel());
                    TextView label = new TextView(mContext);
                    label.setText(field.getLabel());
                    label.setTypeface(font);
                    label.setTextSize(17);
                    label.setWidth(300);
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
                    mOptionWidgets.put(field.getLabel(), rbGroup);
                    hasChild = true;
                }
                if(hasChild) {
                    Log.i(TAG, "Adding child" );
                    layout.addView(innerLayout);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject getPricingJson(){
        SharedPreferences prefs = mContext.getSharedPreferences(CatalogSharedPrefs.KEY_NAME, Context.MODE_PRIVATE);
        String pricingJson = prefs.getString(CatalogSharedPrefs.KEY_PRICING_MODEL, null);
        JSONObject pricingJSON = null;
        try {
            pricingJSON = new JSONObject(pricingJson);
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        return pricingJSON;
    }

    public void buildItemProperties(String propsJson) {
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

                    Log.i(TAG, "jsonArray json" + jsonArray.length() + "---jsonArray--" + jsonArray.toString());

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
                            Log.i(TAG, "optionsArray--->" + optionsArray.getString(c));
                            options[c] = optionsArray.getString(c);
                        }

                        field.setOptions(options);
                        fieldList.add(field);
                        Log.i(TAG,"field size---" + field.getType() + "---" + fieldList.size());
                    }
                    Log.i(TAG,"field--->" + fieldList.size() + fieldList.get(keyIndex).getLabel());
                    propertyList.put(key, fieldList);
                    Log.i(TAG,"propertyList--->" + propertyList.get(0) +"---" + propertyList.size());
                }
            }catch (Exception ex) {
                Log.e(TAG, "Error parsing item properties from shared prefs:" + ex.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        return itemCount.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView checkItemCount;
        public ImageView delete_custom_item;
        public Button save_custom_item;
        public LinearLayout contentLayout;

        public MyViewHolder(View itemView) {
            super(itemView);

            contentLayout = (LinearLayout)itemView.findViewById(R.id.customizeItemLinear);
            checkItemCount = (TextView)itemView.findViewById(R.id.checkItemCount);
            delete_custom_item = (ImageView)itemView.findViewById(R.id.delete_custom_item);
            save_custom_item = (Button)itemView.findViewById(R.id.save_custom_item);
           }
    }
}