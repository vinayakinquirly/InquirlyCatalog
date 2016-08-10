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
    private int itemCount ;
    private Context mContext;
    private String mCampaignId;
    boolean hasEditText = false;
    public SharedPreferences prefs;
    private ArrayList<CampaignDbItem> mItems;
    private String propJson,itemType,itemName;
    public static ArrayList<Fields> fieldList;
    private static final String TAG = "CustomCartItemAdapter";
    private static HashMap<String,View> mOptionWidgets = new HashMap<>();
    private ApplicationController appInstance = ApplicationController.getInstance();
    public static HashMap<String, ArrayList<Fields>>  propertyList = new HashMap<>();

    @IdRes
    private static final int ID_MALE = 1;

    @IdRes
    private static final int ID_FEMALE = 2;

    @IdRes
    private static final int ID_YES = 3;

    @IdRes
    private static final int ID_NO = 4;

    public CustomizeCartItemAdapter(Context mContext,int itemCount,
                                    String propJson,String itemType,String itemName){

        this.mContext = mContext;
        this.itemCount = itemCount;
        this.itemType = itemType;
        this.itemName = itemName;
        Log.i(TAG,"check propsjson--->" +itemType +"---" +  itemCount+"---" + propJson);
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
        int itemNum= position+1;
        holder.setIsRecyclable(false);
        holder.checkItemCount.setText(itemName +": "+ itemNum);
        buildSpecsDialog(holder.contentLayout,propertyList.get(itemType));
    }

    public void buildSpecsDialog(LinearLayout layout, ArrayList<Fields> itemFields) {
        try{
            for(Fields field : itemFields) {
                boolean hasChild = false;

                LinearLayout innerLayout = new LinearLayout(mContext);
                innerLayout.setLayoutParams(new ViewGroup.LayoutParams(600,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                innerLayout.setPadding(5, 5, 5, 5);
                innerLayout.setOrientation(LinearLayout.HORIZONTAL);

                if (field.getType().equals("number_input") || field.getType().equals("text_input") ) {
                    TextView label = new TextView(mContext);
                    label.setText(field.getLabel());
                    label.setTypeface(font);
                    label.setTextSize(14);
                    label.setWidth(300);
                    label.setPadding(10,0,0,0);
                    if(label.getParent() != null)
                        ((ViewGroup)label.getParent()).removeView(label);
                    innerLayout.addView(label);

                    text = new EditText(mContext);
                    if(field.getType().equals("number_input")){
                        text.setInputType(InputType.TYPE_CLASS_PHONE);
                    }else{
                        text.setInputType(InputType.TYPE_CLASS_TEXT);
                    }
                    text.setLayoutParams(new ViewGroup.LayoutParams(280, ViewGroup.LayoutParams.WRAP_CONTENT));
                    text.setGravity(Gravity.START);
                    text.setTag(field.getLabel());
                    text.setPadding(0,0,10,0);
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
                    label.setTextSize(14);
                    label.setWidth(300);
                    label.setPadding(10,0,0,0);
                    if(label.getParent() != null)
                        ((ViewGroup)label.getParent()).removeView(label);
                    innerLayout.addView(label);

                    Spinner spinner = new Spinner(mContext);
                    spinner.setTag(field.getLabel());
                    spinner.setPadding(0,0,10,0);
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mContext,
                            android.R.layout.simple_spinner_item,
                            field.getOptions()
                    ); //selected item will look like a spinner set from XML
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(spinnerArrayAdapter);
                    spinner.setLayoutParams(new ViewGroup.LayoutParams(280, ViewGroup.LayoutParams.WRAP_CONTENT));
                    innerLayout.addView(spinner);
                    hasChild = true;
                    mOptionWidgets.put(field.getLabel(), spinner);
                    Log.i(TAG, "Adding single choice");

                }else if(field.getType().equals("multiple_choice")){
                    TextView label = new TextView(mContext);
                    label.setText(field.getLabel());
                    label.setTypeface(font);
                    label.setTextSize(14);
                    label.setWidth(300);
                    label.setPadding(10,0,0,0);
                    if(label.getParent() != null)
                        ((ViewGroup)label.getParent()).removeView(label);
                    innerLayout.addView(label);

                    Spinner spinner = new Spinner(mContext);
                    spinner.setPadding(0,0,10,0);
                    spinner.setTag(field.getLabel());
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mContext,
                            android.R.layout.simple_spinner_item,
                            field.getOptions()
                    ); //selected item will look like a spinner set from XML
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(spinnerArrayAdapter);
                    spinner.setLayoutParams(new ViewGroup.LayoutParams(280, ViewGroup.LayoutParams.WRAP_CONTENT));
                    innerLayout.addView(spinner);
                    hasChild = true;
                    mOptionWidgets.put(field.getLabel(), spinner);
                    Log.i(TAG, "Adding single choice");

                } else if(field.getType().equals("multiline_text")) {
                    TextView label = new TextView(mContext);
                    label.setText(field.getLabel());
                    label.setTypeface(font);
                    label.setTextSize(14);
                    label.setWidth(300);
                    label.setPadding(10,0,0,0);
                    if(label.getParent() != null)
                        ((ViewGroup)label.getParent()).removeView(label);
                    innerLayout.addView(label);

                    text = new EditText(mContext);
                    text.setMaxLines(3);
                    text.setLayoutParams(new ViewGroup.LayoutParams(280, ViewGroup.LayoutParams.WRAP_CONTENT));
                    text.setGravity(Gravity.START);
                    text.setTag(field.getLabel());
                    text.setPadding(0,0,10,0);
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
                    label.setTextSize(14);
                    label.setWidth(300);
                    label.setPadding(10,0,0,0);
                    if(label.getParent() != null)
                        ((ViewGroup)label.getParent()).removeView(label);
                    innerLayout.addView(label);

                    RadioGroup rbGroup = new RadioGroup(mContext);
                    rbGroup.setPadding(0,0,10,0);
                    rbGroup.setLayoutParams(new ViewGroup.LayoutParams(280, ViewGroup.LayoutParams.WRAP_CONTENT));
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
                    label.setTextSize(14);
                    label.setWidth(300);
                    label.setPadding(10,0,0,0);
                    if(label.getParent() != null)
                        ((ViewGroup)label.getParent()).removeView(label);
                    innerLayout.addView(label);

                    RadioGroup rbGroup = new RadioGroup(mContext);
                    rbGroup.setPadding(0,0,10,0);
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

    public void buildItemProperties(String propsJson) {
        Log.i(TAG,"check propsJson---" + propsJson);

        if(propsJson == null ){
            Log.i(TAG, "item properites json is null");
        }else {
            try {
                JSONObject itemProperties = new JSONObject(propsJson);
                for (int keyIndex = 0; keyIndex < itemProperties.names().length(); keyIndex++) {
                    Log.i(TAG, "Getting item properties for key=" + itemType);
                    JSONArray jsonArray = itemProperties.getJSONArray(itemType);
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
                    propertyList.put(itemType, fieldList);
                    Log.i(TAG,"propertyList--->" + propertyList.get(0) +"---" + propertyList.size());
                }
            }catch (Exception ex) {
                Log.e(TAG, "Error parsing item properties from shared prefs:" + ex.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        Log.i(TAG,"itemName---" + itemName + "itemCount---" + itemCount);
        return itemCount;
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