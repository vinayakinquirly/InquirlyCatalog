package inquirly.com.inquirlycoolberry.Adapters;

import android.util.Log;
import java.util.HashMap;
import android.view.View;
import android.os.Handler;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import android.view.Gravity;
import android.widget.Toast;
import android.widget.Button;
import org.json.JSONException;
import android.view.ViewGroup;
import android.text.InputType;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Context;
import android.widget.ImageView;
import android.graphics.Typeface;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.LinearLayout;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.support.annotation.IdRes;
import inquirly.com.inquirlycatalogue.R;
import android.support.v7.widget.RecyclerView;
import inquirly.com.inquirlycatalogue.models.Fields;
import inquirly.com.inquirlycatalogue.ApplicationController;

/**
 * Created by Vinayak on 7/18/2016.
 */
public class CustomizeCartItemAdapter extends RecyclerView.Adapter<CustomizeCartItemAdapter.MyViewHolder> {

    private Typeface font;
    private int itemCount ;
    private String itemName;
    private String itemType;
    private Context mContext;
    boolean hasEditText = false;
    public ArrayList<Fields> fieldList;
    private static final String TAG = "CustomCartItemAdapter";
    public  HashMap<String, ArrayList<Fields>>  propertyList = new HashMap<>();
    private ApplicationController appInstance =  ApplicationController.getInstance();

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
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final HashMap<String,View> mOptionWidgets = new HashMap<>();
        final int itemNum= position+1;
        holder.setIsRecyclable(false);
        holder.itemCount.setText(String.valueOf(itemNum));

        String jsonString =appInstance.getCustomItemData(itemName);
        JSONObject jsonObject = null;
        Log.i(TAG,"itemCount---" + itemCount + "---" + jsonString);
        int po = position+1;
            Log.i(TAG,"itemCount---" + itemCount + "---" + position +"---" );
            if(jsonString!=null) {
                try {
                    jsonObject = new JSONObject(jsonString);
                    Log.i(TAG, "array---"  + "-----" + jsonObject.getJSONArray(itemName).length());
                    JSONObject jsonObject5 = new JSONObject();
                    if (jsonObject.getJSONArray(itemName).length()>=po){
                        JSONObject jsonObject1 = jsonObject.getJSONArray(itemName).getJSONObject(position);
                        Log.i(TAG, "check json---" + position + "---" + jsonObject1);

                        holder.friends_name.setText(jsonObject1.getString("name"));
                        buildSpecsDialogWithValue(mOptionWidgets,holder.contentLayout, fieldList, jsonObject1);
                        holder.save_custom_item.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_green_light));
                        holder.save_custom_item.setText("SAVED");
                        Log.i(TAG, "json Object---" + jsonObject);
                    } else {
                        buildSpecsDialog(mOptionWidgets,holder.contentLayout, fieldList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                buildSpecsDialog(mOptionWidgets,holder.contentLayout, fieldList);
            }

        holder.save_custom_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = holder.friends_name.getText().toString();
                final int num = Integer.parseInt(holder.itemCount.getText().toString());
                Log.i(TAG,"check position on save----" + position);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(appInstance.saveCustomItemJson(itemName,num,generateItemDetails(mOptionWidgets,name,num,holder.contentLayout))){
                                Log.i(TAG,"saved successfully");
                                holder.save_custom_item.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_green_light));
                                holder.save_custom_item.setText("SAVED");
                                Toast.makeText(mContext, "item saved successfully!", Toast.LENGTH_SHORT).show();
                            }else{
                                holder.saving_progress.setVisibility(View.GONE);
                                Toast.makeText(mContext, "Error Ocurred! Please Try Again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },250);
            }
        });
    }

    public void buildSpecsDialog(HashMap<String,View> mOptionWidgets,LinearLayout layout, ArrayList<Fields> itemFields) {

        try{
            for(Fields field : itemFields) {
                boolean hasChild = false;

                LinearLayout innerLayout = new LinearLayout(mContext);
                innerLayout.setLayoutParams(new ViewGroup.LayoutParams(600,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                innerLayout.setPadding(5, 10, 5, 10);
                innerLayout.setOrientation(LinearLayout.HORIZONTAL);

                EditText text;
                ArrayAdapter<String> spinnerArrayAdapter;
                if (field.getType().equals("number_input") || field.getType().equals("text_input") ) {
                    Log.i(TAG,"entered--" + "number input");
                    TextView label = new TextView(mContext);
                    label.setText(field.getLabel());
                    label.setTypeface(font);
                    label.setTextSize(14);
                    label.setWidth(300);
                    label.setPadding(20,0,0,0);
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
                    text.setPadding(5,0,20,0);
                    hasEditText = true;
                    innerLayout.addView(text);
                    hasChild = true;
                    mOptionWidgets.put(field.getLabel(), text);
                }
                else if(field.getType().equals("single_choice") || field.getType().equals("pricing_input")) {
                    Log.i(TAG,"entered--" + "single choice");
                    TextView label = new TextView(mContext);
                    label.setText(field.getLabel());
                    label.setTypeface(font);
                    label.setTextSize(14);
                    label.setWidth(300);
                    label.setPadding(20,0,0,0);
                    if(label.getParent() != null)
                        ((ViewGroup)label.getParent()).removeView(label);
                    innerLayout.addView(label);

                    Spinner spinnerSingle = new Spinner(mContext);
                    spinnerSingle.setTag(field.getLabel());
                    spinnerSingle.setPadding(0,0,20,0);
                    spinnerArrayAdapter = new ArrayAdapter<String>(mContext,
                            android.R.layout.simple_spinner_item,
                            field.getOptions()
                    ); //selected item will look like a spinner set from XML
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSingle.setAdapter(spinnerArrayAdapter);
                    spinnerSingle.setLayoutParams(new ViewGroup.LayoutParams(280, ViewGroup.LayoutParams.WRAP_CONTENT));
                    innerLayout.addView(spinnerSingle);
                    hasChild = true;
                    mOptionWidgets.put(field.getLabel(), spinnerSingle);

                }else if(field.getType().equals("multiple_choice")){
                    Log.i(TAG,"entered--" + "multiple choice");
                    TextView label = new TextView(mContext);
                    label.setText(field.getLabel());
                    label.setTypeface(font);
                    label.setTextSize(14);
                    label.setWidth(300);
                    label.setPadding(20,0,0,0);
                    if(label.getParent() != null)
                        ((ViewGroup)label.getParent()).removeView(label);
                    innerLayout.addView(label);

                    Spinner spinnerMulti = new Spinner(mContext);
                    spinnerMulti.setPadding(0,0,20,0);
                    spinnerMulti.setTag(field.getLabel());
                    spinnerArrayAdapter = new ArrayAdapter<>(mContext,
                            android.R.layout.simple_spinner_item,
                            field.getOptions()
                    ); //selected item will look like a spinner set from XML
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerMulti.setAdapter(spinnerArrayAdapter);
                    spinnerMulti.setLayoutParams(new ViewGroup.LayoutParams(280, ViewGroup.LayoutParams.WRAP_CONTENT));
                    innerLayout.addView(spinnerMulti);
                    hasChild = true;
                    mOptionWidgets.put(field.getLabel(), spinnerMulti);

                }else if(field.getType().equals("multiline_text")) {
                    Log.i(TAG,"entered--" + "multiline text");
                    TextView label = new TextView(mContext);
                    label.setText(field.getLabel());
                    label.setTypeface(font);
                    label.setTextSize(14);
                    label.setWidth(300);
                    label.setPadding(20,0,0,0);
                    if(label.getParent() != null)
                        ((ViewGroup)label.getParent()).removeView(label);
                    innerLayout.addView(label);

                    text = new EditText(mContext);
                    text.setMaxLines(3);
                    text.setLayoutParams(new ViewGroup.LayoutParams(280, ViewGroup.LayoutParams.WRAP_CONTENT));
                    text.setGravity(Gravity.START);
                    text.setTag(field.getLabel());
                    text.setPadding(5,0,20,0);
                    hasEditText = true;
                    innerLayout.addView(text);
                    hasChild = true;
                    mOptionWidgets.put(field.getLabel(), text);

                }else if(field.getType().equals("gender_toggle")) {
                    Log.i(TAG,"entered--" + "gender toggle");
                    //radio button
                    TextView label = new TextView(mContext);
                    label.setText(field.getLabel());
                    label.setTypeface(font);
                    label.setTextSize(14);
                    label.setWidth(300);
                    label.setPadding(20,0,0,0);
                    if(label.getParent() != null)
                        ((ViewGroup)label.getParent()).removeView(label);
                    innerLayout.addView(label);

                    RadioGroup rbGroup = new RadioGroup(mContext);
                    rbGroup.setPadding(0,0,20,0);
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
                    hasChild = true;
                    mOptionWidgets.put(field.getLabel(), rbGroup);

                }else if(field.getType().equals("yes_no_toggle")) {
                    Log.i(TAG,"entered--" + "yes no toggle");
                    TextView label = new TextView(mContext);
                    label.setText(field.getLabel());
                    label.setTypeface(font);
                    label.setTextSize(14);
                    label.setWidth(300);
                    label.setPadding(20,0,0,0);
                    if(label.getParent() != null)
                        ((ViewGroup)label.getParent()).removeView(label);
                    innerLayout.addView(label);

                    RadioGroup rbGroup = new RadioGroup(mContext);
                    rbGroup.setPadding(0,0,20,0);
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
                    Log.i(TAG, "Adding child" );
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

    public void buildSpecsDialogWithValue(HashMap<String,View> mOptionWidgets,LinearLayout layout, ArrayList<Fields> itemFields,JSONObject data) {
        try{
            for(Fields field : itemFields) {
                boolean hasChild = false;

                LinearLayout innerLayout = new LinearLayout(mContext);
                innerLayout.setLayoutParams(new ViewGroup.LayoutParams(600,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                innerLayout.setPadding(5, 10, 5, 10);
                innerLayout.setOrientation(LinearLayout.HORIZONTAL);

                EditText text;
                ArrayAdapter<String> spinnerArrayAdapter;
                if (field.getType().equals("number_input") || field.getType().equals("text_input") ) {
                    Log.i(TAG,"entered--" + "number input");
                    TextView label = new TextView(mContext);
                    label.setText(field.getLabel());
                    label.setTypeface(font);
                    label.setTextSize(14);
                    label.setWidth(300);
                    label.setPadding(20,0,0,0);
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
                    text.setPadding(5,0,20,0);
                    text.setText(data.getString(field.getLabel()));
                    hasEditText = true;
                    innerLayout.addView(text);
                    hasChild = true;
                    mOptionWidgets.put(field.getLabel(), text);
                }
                else if(field.getType().equals("single_choice") || field.getType().equals("pricing_input")) {
                    Log.i(TAG,"entered--" + "single choice");
                    TextView label = new TextView(mContext);
                    label.setText(field.getLabel());
                    label.setTypeface(font);
                    label.setTextSize(14);
                    label.setWidth(300);
                    label.setPadding(20,0,0,0);
                    if(label.getParent() != null)
                        ((ViewGroup)label.getParent()).removeView(label);
                    innerLayout.addView(label);

                    Spinner spinnerSingle = new Spinner(mContext);
                    spinnerSingle.setTag(field.getLabel());
                    spinnerSingle.setPadding(0,0,20,0);
                    spinnerArrayAdapter = new ArrayAdapter<String>(mContext,
                            android.R.layout.simple_spinner_item,
                            field.getOptions()
                    ); //selected item will look like a spinner set from XML
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSingle.setAdapter(spinnerArrayAdapter);
                    spinnerSingle.setLayoutParams(new ViewGroup.LayoutParams(280, ViewGroup.LayoutParams.WRAP_CONTENT));
                    int j=0;
                    for(int i=0;i<field.getOptions().length;i++){
                        Log.i(TAG,"field options--" + field.getOptions()[i] + "---" + data.get(field.getLabel()));
                        if(field.getOptions()[i].equals(data.get(field.getLabel()))){
                            spinnerSingle.setSelection(j);
                        }
                        j++;
                    }
                    innerLayout.addView(spinnerSingle);
                    hasChild = true;
                    mOptionWidgets.put(field.getLabel(), spinnerSingle);

                }else if(field.getType().equals("multiple_choice")){
                    Log.i(TAG,"entered--" + "multiple choice");
                    TextView label = new TextView(mContext);
                    label.setText(field.getLabel());
                    label.setTypeface(font);
                    label.setTextSize(14);
                    label.setWidth(300);
                    label.setPadding(20,0,0,0);
                    if(label.getParent() != null)
                        ((ViewGroup)label.getParent()).removeView(label);
                    innerLayout.addView(label);

                    Spinner spinnerMulti = new Spinner(mContext);
                    spinnerMulti.setPadding(0,0,20,0);
                    spinnerMulti.setTag(field.getLabel());
                    spinnerArrayAdapter = new ArrayAdapter<>(mContext,
                            android.R.layout.simple_spinner_item,
                            field.getOptions()
                    ); //selected item will look like a spinner set from XML
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerMulti.setAdapter(spinnerArrayAdapter);
                    spinnerMulti.setLayoutParams(new ViewGroup.LayoutParams(280, ViewGroup.LayoutParams.WRAP_CONTENT));
                    int j=0;
                    for(int i=0;i<field.getOptions().length;i++){
                        Log.i(TAG,"fiel options--" + field.getOptions()[i] + "---" + data.get(field.getLabel()));
                        if(field.getOptions()[i].equals(data.get(field.getLabel()))){
                            spinnerMulti.setSelection(j);
                        }
                        j++;
                    }
                    innerLayout.addView(spinnerMulti);
                    hasChild = true;
                    mOptionWidgets.put(field.getLabel(), spinnerMulti);

                }else if(field.getType().equals("multiline_text")) {
                    Log.i(TAG,"entered--" + "multiline text");
                    TextView label = new TextView(mContext);
                    label.setText(field.getLabel());
                    label.setTypeface(font);
                    label.setTextSize(14);
                    label.setWidth(300);
                    label.setPadding(20,0,0,0);
                    if(label.getParent() != null)
                        ((ViewGroup)label.getParent()).removeView(label);
                    innerLayout.addView(label);

                    text = new EditText(mContext);
                    text.setMaxLines(3);
                    text.setLayoutParams(new ViewGroup.LayoutParams(280, ViewGroup.LayoutParams.WRAP_CONTENT));
                    text.setGravity(Gravity.START);
                    text.setTag(field.getLabel());
                    text.setPadding(5,0,20,0);
                    text.setText(data.getString(field.getLabel()));
                    hasEditText = true;
                    innerLayout.addView(text);
                    hasChild = true;
                    mOptionWidgets.put(field.getLabel(), text);

                }else if(field.getType().equals("gender_toggle")) {
                    Log.i(TAG,"entered--" + "gender toggle");
                    //radio button
                    TextView label = new TextView(mContext);
                    label.setText(field.getLabel());
                    label.setTypeface(font);
                    label.setTextSize(14);
                    label.setWidth(300);
                    label.setPadding(20,0,0,0);
                    if(label.getParent() != null)
                        ((ViewGroup)label.getParent()).removeView(label);
                    innerLayout.addView(label);

                    RadioGroup rbGroup = new RadioGroup(mContext);
                    rbGroup.setPadding(0,0,20,0);
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
                    if(field.getLabel().equals(data.getString(field.getLabel()))){
                        if(data.getString(field.getLabel()).equals("female")){
                            female.setChecked(true);
                            male.setChecked(false);
                        }else{
                            male.setChecked(true);
                            female.setChecked(false);
                        }
                    }
                    innerLayout.addView(rbGroup);
                    hasChild = true;
                    mOptionWidgets.put(field.getLabel(), rbGroup);

                }else if(field.getType().equals("yes_no_toggle")) {
                    Log.i(TAG,"entered--" + "yes no toggle");
                    TextView label = new TextView(mContext);
                    label.setText(field.getLabel());
                    label.setTypeface(font);
                    label.setTextSize(14);
                    label.setWidth(300);
                    label.setPadding(20,0,0,0);
                    if(label.getParent() != null)
                        ((ViewGroup)label.getParent()).removeView(label);
                    innerLayout.addView(label);

                    RadioGroup rbGroup = new RadioGroup(mContext);
                    rbGroup.setPadding(0,0,20,0);
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

                    Log.i(TAG,"for radio---" + field.getLabel() + "---" + data.getString(field.getLabel()));

                        if(data.getString(field.getLabel()).equals("yes")){
                            yes.setChecked(true);
                            no.setChecked(false);
                        }else{
                            no.setChecked(true);
                            yes.setChecked(false);
                        }

                    hasChild = true;
                    mOptionWidgets.put(field.getLabel(), rbGroup);
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

    public String generateItemDetails(HashMap<String,View> mOptionWidgets,String name,int num,LinearLayout contentLayout) throws JSONException {
        HashMap<String, String[]> options = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name",name);
        jsonObject.put("itemNum",num);

        for (int iChild = 0; iChild < contentLayout.getChildCount(); iChild++) {
            LinearLayout innerLayout = (LinearLayout) contentLayout.getChildAt(iChild);
            String key = (String) innerLayout.getChildAt(1).getTag();
            View widget = mOptionWidgets.get(key);
            Log.i(TAG,"key value---" + key + widget);

            if (widget instanceof EditText) {
                String qty_value = ((EditText) widget).getText().toString();
                Log.i(TAG, "edittext value--" + qty_value);
                options.put(key, new String[]{qty_value});
                jsonObject.put(key, qty_value);

            } else if (widget instanceof Spinner) {
                Spinner spinner = (Spinner) widget;
                String itemSelected = spinner.getSelectedItem().toString();
                Log.i(TAG, "check spinner---" + itemSelected);
                options.put(key, new String[]{itemSelected});
                jsonObject.put(key, itemSelected);

            } else if ((widget instanceof RadioGroup) || (widget instanceof RadioButton)) {
                Log.i(TAG, "inside radio button group condition");
                RadioGroup rg = (RadioGroup) widget;
                Log.i(TAG,"check tag ---" + rg.getTag());

                if (rg.getCheckedRadioButtonId() == ID_FEMALE) {
                    options.put(key, new String[]{"female"});
                    Log.i(TAG, "female");
                    jsonObject.put(key, "female");

                } else if (rg.getCheckedRadioButtonId() == ID_MALE) {
                    Log.i(TAG, "male");
                    options.put(key, new String[]{"male"});
                    jsonObject.put(key, "male");

                } else if (rg.getCheckedRadioButtonId() == ID_YES) {
                    Log.i(TAG, "yes");
                    options.put(key, new String[]{"yes"});
                    jsonObject.put(key, "yes");

                } else if (rg.getCheckedRadioButtonId() == ID_NO) {
                    Log.i(TAG, "no");
                    options.put(key, new String[]{"no"});
                    jsonObject.put(key, "no");
                }
            }
        }
        return jsonObject.toString();
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private EditText friends_name;
        public TextView itemCount,itemName,text_loading;
        public ImageView saving_progress;
        public Button save_custom_item;
        public LinearLayout contentLayout;

        public MyViewHolder(View itemView) {
            super(itemView);

            contentLayout = (LinearLayout)itemView.findViewById(R.id.customizeItemLinear);
            itemCount = (TextView)itemView.findViewById(R.id.custom_itemCount);
            friends_name = (EditText)itemView.findViewById(R.id.custom_itemName);
            save_custom_item = (Button)itemView.findViewById(R.id.save_custom_item);
            text_loading = (TextView)itemView.findViewById(R.id.text_loading);
            saving_progress = (ImageView)itemView.findViewById(R.id.saving_progress);
        }
    }
}