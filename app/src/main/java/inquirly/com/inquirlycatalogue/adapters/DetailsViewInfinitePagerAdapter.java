package inquirly.com.inquirlycatalogue.adapters;

/**
 * Created by kaushal on 07-12-2015.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import inquirly.com.inquirlycatalogue.ApplicationController;
import inquirly.com.inquirlycatalogue.R;
import inquirly.com.inquirlycatalogue.activities.CartActivity;
import inquirly.com.inquirlycatalogue.activities.DetailViewActivity;
import inquirly.com.inquirlycatalogue.models.CampaignDbItem;
import inquirly.com.inquirlycatalogue.models.CartItem;
import inquirly.com.inquirlycatalogue.models.Fields;
import inquirly.com.inquirlycatalogue.models.PricingModel;
import inquirly.com.inquirlycatalogue.utils.CatalogSharedPrefs;
import inquirly.com.inquirlycatalogue.utils.ScrollTextView;

public class DetailsViewInfinitePagerAdapter extends PagerAdapter {

    private Typeface font;
    private EditText text;
    private int mPosition;
    private Context mContext;
    private String mCampaignId;
    boolean hasEditText = false;
    private LinearLayout contentLayout ;
    private CampaignDbItem mSelectedItem;
    private ArrayList<CampaignDbItem> mItems;
    private HashMap<String, ArrayList<Fields>> propertyList;
    private static HashMap<String,View> mOptionWidgets = new HashMap<>();
    private ApplicationController appInstance = ApplicationController.getInstance();
    private static final String TAG = DetailsViewInfinitePagerAdapter.class.getSimpleName();

    @IdRes
    private static final int ID_MALE = 1;
    @IdRes
    private static final int ID_FEMALE = 2;
    @IdRes
    private static final int ID_YES = 3;
    @IdRes
    private static final int ID_NO = 4;


    public DetailsViewInfinitePagerAdapter(Context context, ArrayList<CampaignDbItem> items, int position, String campaignId)
    {
        mItems = items;
        mContext = context;
        mPosition = position;
        font = Typeface.createFromAsset(mContext.getApplicationContext().getAssets(), "Montserrat-Regular.ttf");
        mCampaignId = campaignId;
        buildItemProperties();
        Log.i(TAG, "campaign_id="+campaignId);
    }

    public int getCount() {
        return mItems.size();
    }

    public Object instantiateItem(ViewGroup collection, final int position) {

        LayoutInflater inflater = (LayoutInflater) collection.getContext
                ().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_viewpager, null);
        Button addToCartBtn = (Button) layout.findViewById(R.id.addToCart);
        addToCartBtn.setTypeface(font);
        Log.i(TAG, "position called for=" + position);
        int currentPos = (mPosition != -1) ? mPosition : position;
        mSelectedItem = mItems.get(currentPos);
        mPosition = -1;
        Log.i(TAG, "selected item position is=" + position);

        final ImageView itemLargeImage = (ImageView) layout.findViewById(R.id.primary_img);
        final ImageView itemSmallImage1 = (ImageView) layout.findViewById(R.id.smallimg_1);
        final ImageView itemSmallImage2 = (ImageView) layout.findViewById(R.id.smallimg_2);
        final ImageView itemSmallImage3 = (ImageView) layout.findViewById(R.id.smallimg_3);
        final ImageView itemSmallImage4 = (ImageView) layout.findViewById(R.id.smallimg_4);
        final ImageView itemSmallImage5 = (ImageView) layout.findViewById(R.id.smallimg_5);

        final ImageView ImageView[]={itemSmallImage1,itemSmallImage2,itemSmallImage3,itemSmallImage4,itemSmallImage5};

        TextView itemPrice = (TextView) layout.findViewById(R.id.txtPrice);
        TextView itemDesc = (TextView) layout.findViewById(R.id.txtDescription);
        ScrollTextView termsConditions = (ScrollTextView) layout.findViewById(R.id.tac);

        Log.i(TAG, "selected primary image is =" + mSelectedItem.getPrimaryImage());

        itemLargeImage.setVisibility(View.VISIBLE);
        Picasso.with(mContext).load("file://" + mSelectedItem.getPrimaryImage()).resize(600, 500).into(itemLargeImage);

        TextView itemname = (TextView) layout.findViewById(R.id.txtItemName);

        itemname.setTypeface(font);
        itemPrice.setTypeface(font);
        itemDesc.setTypeface(font);


        SharedPreferences prefs = mContext.getSharedPreferences(CatalogSharedPrefs.KEY_NAME, Context.MODE_PRIVATE);
        String TandC = prefs.getString(mCampaignId + "_" + CatalogSharedPrefs.KEY_TERMS_CONDITIONS, null);
//        Log.d(TAG, TandC);
        termsConditions.setText(TandC);
        termsConditions.setTypeface(font);
        termsConditions.startScroll();


        final CampaignDbItem selectedItem = mSelectedItem;
        if(mSelectedItem.getMediaImg1() != null) {
            Picasso.with(mContext).load("file://" +selectedItem.getMediaImg1()).resize(220, 240).into(itemSmallImage1);
            itemSmallImage1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Picasso.with(mContext).load(selectedItem.getMediaImg1()).resize(1100, 900).into(itemLargeImage);
                    highlight(0, ImageView);
                }
            });
        }
        else {
            itemSmallImage1.setVisibility(View.GONE);
        }

        if(mSelectedItem.getMediaImg2() != null) {
            Picasso.with(mContext).load("file://" +selectedItem.getMediaImg2()).resize(220, 240).into(itemSmallImage2);
            itemSmallImage2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Picasso.with(mContext).load(selectedItem.getMediaImg2()).resize(1100, 900).into(itemLargeImage);
                    highlight(1, ImageView);
                }
            });
        }
        else {
            itemSmallImage2.setVisibility(View.GONE);
        }
        if(mSelectedItem.getMediaImg3() != null) {
            Picasso.with(mContext).load("file://" +selectedItem.getMediaImg3()).resize(220, 240).into(itemSmallImage3);

            itemSmallImage3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Picasso.with(mContext).load(selectedItem.getMediaImg3()).resize(1100, 900).into(itemLargeImage);
                    highlight(2, ImageView);
                }
            });
        }
        else {
            itemSmallImage3.setVisibility(View.GONE);
        }
        if(mSelectedItem.getMediaImg4() != null) {
            Picasso.with(mContext).load("file://" +selectedItem.getMediaImg4()).resize(220, 240).into(itemSmallImage4);

            itemSmallImage4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Picasso.with(mContext).load(selectedItem.getMediaImg4()).resize(1100, 900).into(itemLargeImage);
                    highlight(3, ImageView);
                }
            });
        }
        else {
            itemSmallImage4.setVisibility(View.GONE);
        }
        if(mSelectedItem.getMediaImg5() != null) {
            Picasso.with(mContext).load("file://" +selectedItem.getMediaImg3()).resize(220, 240).into(itemSmallImage5);

            itemSmallImage5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Picasso.with(mContext).load(selectedItem.getMediaImg3()).resize(1100, 900).into(itemLargeImage);
                    highlight(4, ImageView);
                }
            });
        }
        else {
            itemSmallImage5.setVisibility(View.GONE);
        }


        itemname.setText(mSelectedItem.getItemName());
        itemPrice.setText("Rs." + mSelectedItem.getPrice());
        //  itemCode.setText(mSelectedItem.getItemCode());
        itemDesc.setText(mSelectedItem.getDescription());

        //handle add to cart click event
        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context = DetailsViewInfinitePagerAdapter.this.mContext;

                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

                View dialogView = LayoutInflater.from(context).inflate(R.layout.item_specs_dialog, null);
                contentLayout = (LinearLayout) dialogView.findViewById(R.id.dlg_specs_content);

                //bring up the item specs chooser dialogs
                Log.i(TAG,"property list" + selectedItem.getType() + "---" + propertyList.size()+ "----" +propertyList.get(selectedItem.getType()));
                DetailsViewInfinitePagerAdapter.this.buildSpecsDialog(contentLayout, propertyList.get(selectedItem.getType()));

                dialogBuilder.setView(dialogView);
                dialogBuilder.setTitle("Specify Options");
                dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });

                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });

                final AlertDialog dialog = dialogBuilder.create();
                dialog.show();

                Button dialogButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                dialogButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (hasEditText) {
                            if (text.getText().toString().equals("")) {
                                Toast.makeText(mContext, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                dialog.dismiss();
                                generateitemdetails(selectedItem);
                            }
                        }
                        else {
                            dialog.dismiss();
                            generateitemdetails(selectedItem);
                        }
                    }
                });
            }

        });

        ((ViewPager) collection).addView(layout, 0);
        mPosition = -1;

        return layout;
    }

    public void generateitemdetails(CampaignDbItem selItem) {
        CartItem cartItem = new CartItem();
        cartItem.setCampaignId(DetailsViewInfinitePagerAdapter.this.mCampaignId);
        int basePrice = selItem.getPrice();
        String type = selItem.getType();
        JSONObject pricingJSON = getPricingJson();
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

        Log.i(TAG, "campaign_id=" + DetailsViewInfinitePagerAdapter.this.mCampaignId);
        for(int i=0;i<contentLayout.getChildCount();i++){
            LinearLayout innerLayout = (LinearLayout) contentLayout.getChildAt(i);
            Log.i(TAG,"------" + innerLayout.getChildAt(1).getTag());
        }

        HashMap<String, String[]> options = new HashMap<>();
        for (int iChild = 0; iChild < contentLayout.getChildCount(); iChild++) {
            LinearLayout innerLayout = (LinearLayout) contentLayout.getChildAt(iChild);
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

        cartItem.setItemSpecs(options);
        cartItem.setItemImage(selItem.getPrimaryImage());
        cartItem.setItemName(selItem.getItemName());
        cartItem.setItemPrice(totalPrice + "");
        cartItem.setItemQuantity(selectedQuantity);
        cartItem.setItemCode(selItem.getItemCode());
        appInstance.addCartItem(cartItem);
        Toast.makeText(DetailsViewInfinitePagerAdapter.this.mContext,
                "Item added to the cart!",
                Toast.LENGTH_LONG).show();

        Intent cartActivity = new Intent(mContext, CartActivity.class);
        cartActivity.putExtra("campaign_id", DetailsViewInfinitePagerAdapter.this.mCampaignId);
        mContext.startActivity(cartActivity);
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView((View) arg2);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == ((View) arg1);
    }

    @Override
    public Parcelable saveState() {
        return null;
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
                   /* TextView label = new TextView(mContext);
                    label.setText(field.getLabel());
                    label.setTypeface(font);
                    label.setTextSize(17);
                    label.setWidth(300);
                    label.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                    if(label.getParent() != null)
                        ((ViewGroup)label.getParent()).removeView(label);
                    innerLayout.addView(label);

                    text = new EditText(mContext);
                    text.setTag(field.getLabel());
                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(400, ViewGroup.LayoutParams.WRAP_CONTENT);
                    text.setLayoutParams(layoutParams);
                    text.setGravity(Gravity.RIGHT | Gravity.END);
                    innerLayout.addView(text);
                    hasEditText = true;
                    mOptionWidgets.put(field.getLabel(),text);
                    Log.i(TAG, "Adding single input");
                    hasChild = true;*/

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

    public void highlight(int index, ImageView[] imgView) {

        for(int i=0;i<5;i++) {

            if(index == i) {
                imgView[i].setBackgroundResource(R.drawable.selected_image_border);
            }else
            {
                imgView[i].setBackgroundResource(R.drawable.unselected_image_border);
            }
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

    public void buildItemProperties() {
        SharedPreferences prefs = mContext.getSharedPreferences(CatalogSharedPrefs.KEY_NAME, Context.MODE_PRIVATE);
        String propsJson = prefs.getString(mCampaignId + "_" + CatalogSharedPrefs.KEY_ITEM_PROPERTIES, null);
        propertyList = new HashMap<>();
        if(propsJson == null ){
            Log.i(TAG, "item properites json is null");
        }else {
            try {
                JSONObject itemProperties = new JSONObject(propsJson);
                for (int keyIndex = 0; keyIndex < itemProperties.names().length(); keyIndex++) {
                    String key = (String) itemProperties.names().get(keyIndex);
                    Log.i(TAG, "Getting item properties for key=" + key);
                    JSONArray jsonArray = itemProperties.getJSONArray(key);
                    ArrayList<Fields> fieldList = new ArrayList<>();
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
                            //JSONObject opt = optionsArray.getJSONObject(c);
                            options[c] = optionsArray.getString(c);
                        }
                        field.setOptions(options);
                        fieldList.add(field);
                    }
                    Log.i(TAG,"field size--->" + fieldList.size() + fieldList.get(keyIndex).getLabel());
                    propertyList.put("cake", fieldList);
                    Log.i(TAG,"check  propertyList--JUST-->" + propertyList.size() + "---" + propertyList.get(0));
                }
            } catch (Exception ex) {
                Log.e(TAG, "Error parsing item properties from shared prefs:" + ex.getMessage());
            }
        }
    }
    @Override
    public int getItemPosition(Object object) {
        // POSITION_NONE makes it possible to reload the PagerAdapter
        return POSITION_NONE;
    }

    private void setMarqueeSpeed(TextView tv, float speed, boolean speedIsMultiplier) {

        try {
            Field f = tv.getClass().getDeclaredField("mMarquee");
            f.setAccessible(true);

            Object marquee = f.get(tv);
            if (marquee != null) {

                String scrollSpeedFieldName = "mScrollUnit";
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    scrollSpeedFieldName = "mPixelsPerSecond";

                Field mf = marquee.getClass().getDeclaredField(scrollSpeedFieldName);
                mf.setAccessible(true);

                float newSpeed = speed;
                if (speedIsMultiplier)
                    newSpeed = mf.getFloat(marquee) * speed;

                mf.setFloat(marquee, newSpeed);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}