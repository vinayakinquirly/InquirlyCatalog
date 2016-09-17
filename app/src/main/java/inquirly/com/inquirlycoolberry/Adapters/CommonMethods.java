package inquirly.com.inquirlycoolberry.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.IdRes;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import inquirly.com.inquirlycatalogue.R;
import inquirly.com.inquirlycatalogue.models.Fields;

/**
 * Created by Vinayak on 9/17/2016.
 */
public class CommonMethods  {

    private static Typeface font;
    private static boolean hasEditText = false;
    private static final String TAG = "CommonMethods";
    private static JSONObject jsonObject = new JSONObject();


    @IdRes
    private static final int ID_MALE = 1;

    @IdRes
    private static final int ID_FEMALE = 2;

    @IdRes
    private static final int ID_YES = 3;

    @IdRes
    private static final int ID_NO = 4;

    public static void addSpecificationsToDialog(HashMap<String,View> mOptionWidgets,HashMap<String,String> mOptionValues,
                                          LinearLayout layout, ArrayList<Fields> itemFields,Context mContext){
        font  = Typeface.createFromAsset(mContext.getApplicationContext().getAssets(), "Montserrat-Regular.ttf");
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
                LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 60,1.8f);
                ArrayAdapter<String> spinnerArrayAdapter;
                if (field.getType().equals("number_input") || field.getType().equals("text_input") ) {
                    Log.v(TAG, "entered--" + "number input");
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

                        text = new EditText(mContext);
                        if (field.getType().equals("number_input")) {
                            text.setInputType(InputType.TYPE_CLASS_PHONE);
                        } else {
                            text.setInputType(InputType.TYPE_CLASS_TEXT);
                        }

                        text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT,1.4f));

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
                    label.setLayoutParams(labelParams);

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
                    spinnerSingle.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,1.4f));

                    innerLayout.addView(spinnerSingle);
                    hasChild = true;
                    mOptionWidgets.put(field.getLabel(), spinnerSingle);
                    mOptionValues.put(field.getLabel(),null);

                }else if(field.getType().equals("multiple_choice")){
                    LinearLayout labelVertical = new LinearLayout(mContext);
                    labelParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, 60,1.8f);
                    labelVertical.setOrientation(LinearLayout.VERTICAL);
                    labelVertical.setLayoutParams(labelParams);
                    Log.v(TAG,"entered--" + "multiple choice");

                    TextView multiLabel = new TextView(mContext);
                    multiLabel.setText(field.getLabel());
                    multiLabel.setTypeface(font);
                    multiLabel.setTextSize(14);
                    multiLabel.setPadding(20,0,0,0);
                    multiLabel.setLayoutParams(labelParams);

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

                    spinnerMulti.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,1.4f));

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
                    label.setLayoutParams(labelParams);
                    if(label.getParent() != null)
                        ((ViewGroup)label.getParent()).removeView(label);
                    innerLayout.addView(label);

                    RadioGroup rbGroup = new RadioGroup(mContext);
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
                    label.setLayoutParams(labelParams);

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

    public static class CustomArrayAdapter extends ArrayAdapter<String> {
        private String[] objects;
        private Context context;
        private String fieldLabel;
        private TextView labelItemSel;
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

    public static String generateItemDetails(HashMap<String,View> mOptionWidgets, HashMap<String,String> mOptionValues,
                                      String name,int num,LinearLayout contentLayout) throws JSONException {

        Log.i(TAG,"check Json recevied--" + mOptionValues+"--" + jsonObject.toString());
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

//        JSONObject jsonObject1 = jsonObject;
//        jsonObject.remove((String)jsonObject.keys().next());
        Log.i(TAG,"check json send ---" +jsonObject + "<----->");
        return jsonObject.toString();
    }
}
