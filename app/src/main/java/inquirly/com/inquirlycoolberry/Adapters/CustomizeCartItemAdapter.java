package inquirly.com.inquirlycoolberry.Adapters;

import android.os.Build;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import android.view.View;
import android.os.Handler;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.graphics.Typeface;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.LinearLayout;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.support.annotation.IdRes;
import inquirly.com.inquirlycatalogue.R;
import android.support.v7.widget.RecyclerView;
import inquirly.com.inquirlycatalogue.models.Fields;
import inquirly.com.inquirlycatalogue.ApplicationController;

/**
 * Created by Vinayak on 7/18/2016.
 */
public class CustomizeCartItemAdapter extends RecyclerView.Adapter<CustomizeCartItemAdapter.MyViewHolder> {

    private EditText text;
    private Typeface font;
    private int itemCount ;
    private Context mContext;
    boolean hasEditText = false;
    public ArrayList<Fields> fieldList;
    private String itemName,itemType;
    public JSONObject jsonObject = new JSONObject();
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
        font  = Typeface.createFromAsset(mContext.getApplicationContext().getAssets(), "Montserrat-Regular.ttf");
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
        final HashMap<String,String> mOptionValues = new HashMap<>();

        final int itemNum= position+1;
        holder.setIsRecyclable(false);
        holder.itemCount.setText(String.valueOf(itemNum));

        String jsonString =appInstance.getCustomItemData(itemName);
        JSONObject jsonObject = null;
        Log.i(TAG,"itemCount---" + itemCount + "---" + jsonString);
        int po = position+1;
        if(jsonString!=null) {
            try {
                jsonObject = new JSONObject(jsonString);
                Log.i(TAG, "array---"  + "-----" + jsonObject.getJSONArray(itemName).length());
                JSONObject jsonObject1 = null;
                if (jsonObject.getJSONArray(itemName).length()>=po){
                    jsonObject1 = jsonObject.getJSONArray(itemName).getJSONObject(position);
                    Log.i(TAG, "check json---" + position + "---" + jsonObject1);
                    holder.friends_name.setText(jsonObject1.getString("tag"));
                    buildSpecsDialogWithValue(mOptionWidgets,mOptionValues,holder.contentLayout, fieldList, jsonObject1);
                    holder.save_custom_item.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_green_light));
                    holder.save_custom_item.setText("SAVED");
                    Log.i(TAG, "json Object---" + jsonObject);
                } else {
                    buildSpecsDialogWithValue(mOptionWidgets,mOptionValues,holder.contentLayout, fieldList, jsonObject1);
                    //    buildSpecsDialog(mOptionWidgets,mOptionValues,holder.contentLayout, fieldList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            buildSpecsDialogWithValue(mOptionWidgets,mOptionValues,holder.contentLayout, fieldList, jsonObject);
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
                            if(appInstance.saveCustomItemJson(itemName,num,
                                    generateItemDetails(mOptionWidgets,mOptionValues,name,num,holder.contentLayout))){
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

    public void buildSpecsDialogWithValue(HashMap<String,View> mOptionWidgets,HashMap<String,String> mOptionValues,
                                          LinearLayout layout, ArrayList<Fields> itemFields,JSONObject data) {
        try{
            for(Fields field : itemFields) {
                boolean hasChild = false;

                LinearLayout innerLayout = new LinearLayout(mContext);
                if(Build.VERSION.SDK_INT>Build.VERSION_CODES.KITKAT){
                    innerLayout.setLayoutParams(new ViewGroup.LayoutParams(650,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    innerLayout.setPadding(0, 0, 0, 5);
                }else{
                    innerLayout.setLayoutParams(new ViewGroup.LayoutParams(400,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    innerLayout.setPadding(0, 0, 0, 5);
                }
                innerLayout.setOrientation(LinearLayout.HORIZONTAL);
                innerLayout.setWeightSum(3.0f);

                text = new EditText(mContext);
                LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 60,1.8f);
                ArrayAdapter<String> spinnerArrayAdapter;

                if (field.getType().equals("number_input") || field.getType().equals("text_input") ) {
                    Log.i(TAG, "entered--" + "number input");
                    if (!field.getLabel().equals("Quantity")) {
                        TextView label = new TextView(mContext);
                        label.setText(field.getLabel());
                        label.setTypeface(font);
                        label.setTextSize(14);
                        label.setPadding(20,0,0,0);
                        label.setLayoutParams(labelParams);
                        if (label.getParent() != null)
                            ((ViewGroup) label.getParent()).removeView(label);
                        innerLayout.addView(label);

                        if (field.getType().equals("number_input")) {
                            text.setInputType(InputType.TYPE_CLASS_PHONE);
                        } else {
                            text.setInputType(InputType.TYPE_CLASS_TEXT);
                        }

                        text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT,1.4f));
                        text.setGravity(Gravity.START);
                        text.setTag(field.getLabel());

                        if(data!=null)
                            text.setText(data.getString(field.getLabel()));

                        hasEditText = true;
                        innerLayout.addView(text);
                        hasChild = true;
                        mOptionWidgets.put(field.getLabel(), text);
                    }
                }
                else if(field.getType().equals("single_choice") || field.getType().equals("pricing_input")) {
                    Log.i(TAG,"entered--" + "single choice");
                    TextView label = new TextView(mContext);
                    label.setText(field.getLabel());
                    label.setTypeface(font);
                    label.setTextSize(14);
                    label.setPadding(20,0,0,0);
                    label.setLayoutParams(labelParams);
                    if(label.getParent() != null)
                        ((ViewGroup)label.getParent()).removeView(label);
                    innerLayout.addView(label);

                    Spinner spinnerSingle = new Spinner(mContext);
                    spinnerSingle.setTag(field.getLabel());
                    spinnerArrayAdapter = new ArrayAdapter<String>(mContext,
                            android.R.layout.simple_spinner_item, field.getOptions());
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSingle.setAdapter(spinnerArrayAdapter);
                    spinnerSingle.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,1.4f));

                    if(data!=null) {
                        int j = 0;
                        for (int i = 0; i < field.getOptions().length; i++) {
                            Log.i(TAG, "field options--" + field.getOptions()[i] + "---" + data.get(field.getLabel()));
                            if (field.getOptions()[i].equals(data.get(field.getLabel()))) {
                                spinnerSingle.setSelection(j);
                            }
                            j++;
                        }
                    }
                    innerLayout.addView(spinnerSingle);
                    hasChild = true;
                    mOptionWidgets.put(field.getLabel(), spinnerSingle);
                    mOptionValues.put(field.getLabel(),null);

                }else if(field.getType().equals("multiple_choice")){
                    Log.v(TAG,"entered--" + "multiple choice");
                    LinearLayout labelVertical = new LinearLayout(mContext);
                    labelParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, 60,1.8f);

                    labelVertical.setOrientation(LinearLayout.VERTICAL);
                    labelVertical.setLayoutParams(labelParams);

                    TextView label = new TextView(mContext);
                    label.setText(field.getLabel());
                    label.setTypeface(font);
                    label.setTextSize(14);
                    label.setPadding(20,0,0,0);

                    TextView labelItemSel  = new TextView(mContext);
                    labelItemSel.setText("0 selected");
                    labelItemSel.setTypeface(font);
                    labelItemSel.setTextSize(13);
                    labelItemSel.setWidth(100);
                    labelItemSel.setPadding(20,0,0,0);

                    if(label.getParent() != null)
                        ((ViewGroup)label.getParent()).removeView(label);
                    labelVertical.addView(label);
                    labelVertical.addView(labelItemSel);
                    innerLayout.addView(labelVertical);

                    Spinner spinnerMulti = new Spinner(mContext);
                    spinnerMulti.setTag(field.getLabel());

                    ArrayList<String> itemsArray = new ArrayList<>();

                    if(data!=null){
                        List<String> items = Arrays.asList(data.getString(field.getLabel()).split("\\s*,\\s*"));
                        Log.i(TAG,"check item array--" + itemsArray.toString());
                        for(int i=0;i<items.size();i++){
                            itemsArray.add(items.get(i));
                        }
                        labelItemSel.setText(String.valueOf(items.size())+ " selected");
                    }

                    CustomArrayAdapter myAdapter = new CustomArrayAdapter(mContext, R.layout.layout_spinner,
                            field.getOptions(),itemsArray,field.getLabel(),labelItemSel);
                    spinnerMulti.setAdapter(myAdapter);
                    spinnerMulti.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,1.4f));

                    innerLayout.addView(spinnerMulti);
                    hasChild = true;

                    mOptionWidgets.put(field.getLabel(), spinnerMulti);
                    mOptionValues.put(field.getLabel(),field.getOptions()[0]);
                    Log.i(TAG,"moptionValue--->" + mOptionValues.get(field.getLabel()));

                }else if(field.getType().equals("multiline_text")) {
                    Log.i(TAG,"entered--" + "multiline text");
                    TextView label = new TextView(mContext);
                    label.setText(field.getLabel());
                    label.setTypeface(font);
                    label.setTextSize(14);
                    label.setPadding(20,0,0,0);
                    label.setLayoutParams(labelParams);

                    if(label.getParent() != null)
                        ((ViewGroup)label.getParent()).removeView(label);
                    innerLayout.addView(label);

                    text = new EditText(mContext);
                    text.setMaxLines(3);
                    text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,1.4f));
                    text.setGravity(Gravity.START);
                    text.setTag(field.getLabel());

                    if(data!=null)
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
                    label.setPadding(20,0,0,0);
                    label.setLayoutParams(labelParams);

                    if(label.getParent() != null)
                        ((ViewGroup)label.getParent()).removeView(label);
                    innerLayout.addView(label);

                    RadioGroup rbGroup = new RadioGroup(mContext);
                    rbGroup.setPadding(0,0,20,0);
                    rbGroup.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,1.4f));

                    rbGroup.setTag("GENDER");
                    RadioButton male = new RadioButton(mContext);
                    male.setText("Male");
                    male.setId(ID_MALE);

                    RadioButton female = new RadioButton(mContext);
                    female.setText("Female");
                    female.setId(ID_FEMALE);

                    rbGroup.addView(female);
                    rbGroup.addView(male);
                    if(data!=null) {
                        if (field.getLabel().equals(data.getString(field.getLabel()))) {
                            if (data.getString(field.getLabel()).equals("female")) {
                                female.setChecked(true);
                                male.setChecked(false);
                            } else {
                                male.setChecked(true);
                                female.setChecked(false);
                            }
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
                    label.setPadding(20,0,0,0);
                    label.setLayoutParams(labelParams);

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

                    if(data!=null) {
                        Log.i(TAG, "for radio---" + field.getLabel() + "---" + data.getString(field.getLabel()));
                        if (data.getString(field.getLabel()).equals("yes")) {
                            yes.setChecked(true);
                            no.setChecked(false);
                        } else {
                            no.setChecked(true);
                            yes.setChecked(false);
                        }
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
                        JSONObject obj = jsonArray.getJSONObject(index);
                        Fields field = new Fields();
                        field.setType(obj.getString("type"));
                        field.setLabel(obj.getString("label"));
                        JSONArray optionsArray = obj.getJSONArray("options");
                        String[] options = new String[optionsArray.length()];

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

    public String generateItemDetails(HashMap<String,View> mOptionWidgets,HashMap<String,String> mOptionValues,
                                      String name,int num, LinearLayout contentLayout) throws JSONException {
        HashMap<String, String[]> options = new HashMap<>();
        jsonObject.put("tag",name);
        String numValue = String.valueOf(num);
        jsonObject.put("itemNum",numValue);

        for (int iChild = 0; iChild < contentLayout.getChildCount(); iChild++) {
            LinearLayout innerLayout = (LinearLayout) contentLayout.getChildAt(iChild);
            String key = (String) innerLayout.getChildAt(1).getTag();
            View widget = mOptionWidgets.get(key);
            Log.i(TAG,"key value---" + key +"---" +widget.getTag());

            if (widget instanceof EditText) {
                String qty_value = ((EditText) widget).getText().toString();
                Log.i(TAG, "edittext value--" + qty_value);
                options.put(key, new String[]{qty_value});
                jsonObject.put(key, qty_value);

            }else if (widget instanceof Spinner) {
                Log.i(TAG,"entered in spinner");
                Spinner spinner = (Spinner) widget;
                String itemSelected = spinner.getSelectedItem().toString();
                Log.i(TAG, "check spinner---" + itemSelected);
                options.put(key, new String[]{itemSelected});
                Log.i(TAG,"jsonObject is---" + jsonObject.toString());
                if(mOptionValues!=null){
                    String type = mOptionValues.get(key);
                    if(type==null){
                        jsonObject.put(key, itemSelected);
                    }
                }else{
                    jsonObject.put(key, itemSelected);
                }

            }else if ((widget instanceof RadioGroup) || (widget instanceof RadioButton)) {
                Log.i(TAG, "inside radio button group condition");
                RadioGroup rg = (RadioGroup) widget;
                Log.i(TAG,"check tag ---" + rg.getTag());
                if (rg.getCheckedRadioButtonId() == ID_FEMALE) {
                    options.put(key, new String[]{"female"});
                    Log.i(TAG, "female");
                    jsonObject.put(key, "female");

                }else if (rg.getCheckedRadioButtonId() == ID_MALE) {
                    Log.i(TAG, "male");
                    options.put(key, new String[]{"male"});
                    jsonObject.put(key, "male");

                }else if (rg.getCheckedRadioButtonId() == ID_YES) {
                    Log.i(TAG, "yes");
                    options.put(key, new String[]{"yes"});
                    jsonObject.put(key, "yes");

                }else if (rg.getCheckedRadioButtonId() == ID_NO) {
                    Log.i(TAG, "no");
                    options.put(key, new String[]{"no"});
                    jsonObject.put(key, "no");
                }
            }
            if (!jsonObject.has(key)) {
                jsonObject.put(key, mOptionValues.get(key));
            }

        }
        Log.i(TAG,"check json here itslef--" + jsonObject.toString());
        return jsonObject.toString();
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

         private EditText friends_name;
        public TextView itemCount,text_loading;
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

    public class CustomArrayAdapter extends ArrayAdapter<String> {

        private View row;
        private String[] objects;
        private Context context;
        public CheckBox checkBox;
        private TextView labelItemSel;
        private ArrayList<String> items = new ArrayList<>();
        private String fieldLabel,itemNumSel="[0] selected";

        public CustomArrayAdapter(Context context, int resourceId, String[] objects,
                                  ArrayList<String> items,String fieldLabel,TextView labelItemSel) {

            super(context, resourceId, objects);
            this.objects = objects;
            this.context = context;
            this.items = items;
            this.fieldLabel = fieldLabel;
            this.labelItemSel = labelItemSel;
            Log.i(TAG,"check items received---" + fieldLabel+"---" + items.toString());
        }

        @Override
        public View getDropDownView(final int position, View convertView, final ViewGroup parent) {
            Log.i(TAG,"check lenghts--" + items.size()+"--" + objects.length);
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            Log.i(TAG,"check parent view--" + parent.getTag() + "--" + parent.getChildAt(position)
                    +"--" + parent.getChildCount());
            return getCustomView(position, convertView, parent);
        }

        @Override
        public String getItem(int position) {
            return objects[position];
        }

        public View getCustomView(final int position, View convertView, final ViewGroup parent) {
            LayoutInflater inflater=(LayoutInflater) context.getSystemService(  Context.LAYOUT_INFLATER_SERVICE );
            row=inflater.inflate(R.layout.layout_spinner_adapter, parent, false);
            TextView label=(TextView)row.findViewById(R.id.list_text);
            checkBox=(CheckBox) row.findViewById(R.id.checkBox);
            label.setText(objects[position]);
            Log.i(TAG,"check item selected parent--" + parent.getTag());

            checkBox.setChecked(false);
            if(items.indexOf(label.getText().toString())>=0){
                itemNumSel ="[" +String.valueOf(items.size())+ "] selected";
                labelItemSel.setText(String.valueOf(items.size())+ " selected");
                checkBox.setChecked(true);
                try {
                    String extraString = items.toString().replace("[","").replace("]","");
                    jsonObject.put(fieldLabel,extraString);
                    Log.i(TAG,"see json object--" + jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Log.i(TAG,"check index--" + items.indexOf(label.getText().toString()));

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.i(TAG, "check item selected--" + fieldLabel + objects[position] + objects.length);
                    if (isChecked) {
                        items.add(objects[position]);
                        Log.i(TAG, "get itemsList---" + items.size() + "--" + items.toString());
                        String extras = items.toString().replace("[", "").replace("]", "");
                        Log.i(TAG, "after removal---" + extras);

                        try {
                            jsonObject.put(fieldLabel, extras);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //itemNumSel ="[" +String.valueOf(items.size())+ "] selected";
                        labelItemSel.setText(String.valueOf(items.size())+ " selected");
                        Log.i(TAG, "check jsonObject--->" + jsonObject.toString());
                    } else {
                        items.remove(objects[position]);
                        Log.i(TAG, "get itemsList---" + items.size() + "--" + items.toString());
                        String extras = items.toString().replace("[", "").replace("]", "");
                        Log.i(TAG, "after removal---" + extras);

                        try {
                            jsonObject.put(fieldLabel, extras);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        labelItemSel.setText(String.valueOf(items.size())+ " selected");

                        Log.i(TAG, "check jsonObject--->" + jsonObject.toString());
                    }
                }
            });
            return row;
        }
    }
}