package inquirly.com.inquirlycoolberry.Adapters;

import android.util.Log;
import java.util.HashMap;
import android.view.View;
import android.os.Handler;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import android.widget.Toast;
import android.widget.Button;
import org.json.JSONException;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.LayoutInflater;
import inquirly.com.inquirlycatalogue.R;
import android.support.v7.widget.RecyclerView;
import inquirly.com.inquirlycatalogue.models.Fields;
import inquirly.com.inquirlycatalogue.ApplicationController;

/**
 * Created by Vinayak on 7/18/2016.
 */
public class CustomizeCartItemAdapter extends RecyclerView.Adapter<CustomizeCartItemAdapter.MyViewHolder> {

    private int itemCount ;
    private Context mContext;
    private String itemName,itemType;
    public ArrayList<Fields> fieldList;
    private static final String TAG = "CustomCartItemAdapter";
    public  HashMap<String, ArrayList<Fields>>  propertyList = new HashMap<>();
    private ApplicationController appInstance =  ApplicationController.getInstance();

    public CustomizeCartItemAdapter(Context mContext,int itemCount,
                                    String propJson,String itemType,String itemName){

        this.mContext = mContext;
        this.itemCount = itemCount;
        this.itemType = itemType;
        this.itemName = itemName;
        Log.i(TAG,"check propsjson--->" +itemType +"---" +  itemCount+"---" + propJson);
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
                if (jsonObject.getJSONArray(itemName).length()>=po) {
                    Log.i(TAG,"jsonObject retreived--" + jsonObject.toString());
                    jsonObject1 = jsonObject.getJSONArray(itemName).getJSONObject(position);
                    holder.friends_name.setText(jsonObject1.getString("tag"));
                    Log.i(TAG, "check json---" + position + "---" + jsonObject1);

                    if (jsonObject.getJSONArray(itemName).getJSONObject(position).length() != 2){
                        Log.i(TAG, "if entered");
                        CommonMethods.addSpecificationsToDialog(holder.contentLayout,
                                fieldList, mContext, jsonObject1);
                    }else{
                        Log.i(TAG,"else entered--"  );
                        CommonMethods.addSpecificationsToDialog(holder.contentLayout,
                                fieldList, mContext, null);
                    }
                    holder.save_custom_item.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_green_light));
                    holder.save_custom_item.setText("SAVED");
                    Log.i(TAG, "json Object---" + jsonObject);
                } else {
                    CommonMethods.addSpecificationsToDialog(holder.contentLayout,
                            fieldList, mContext, null);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
                                    CommonMethods.generateItemDetails(name,num,holder.contentLayout))){
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
                Log.i(TAG, "Error parsing item properties from shared prefs:" + ex.getMessage());
            }
        }
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
}
/*
package inquirly.com.inquirlycoolberry.Adapters;

import android.util.Log;
import java.util.HashMap;
import android.view.View;
import android.os.Handler;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import android.widget.Toast;
import android.widget.Button;
import org.json.JSONException;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.LayoutInflater;
import inquirly.com.inquirlycatalogue.R;
import android.support.v7.widget.RecyclerView;
import inquirly.com.inquirlycatalogue.models.Fields;
import inquirly.com.inquirlycatalogue.ApplicationController;

*/
/**
 * Created by Vinayak on 7/18/2016.
 *//*

public class CustomizeCartItemAdapter extends RecyclerView.Adapter<CustomizeCartItemAdapter.MyViewHolder> {

    private int itemCount ;
    private Context mContext;
    private String itemName,itemType;
    public ArrayList<Fields> fieldList;
    private static final String TAG = "CustomCartItemAdapter";
    private  HashMap<String,View> mOptionWidgets = new HashMap<>();
    private  HashMap<String,String> mOptionValues = new HashMap<>();
    public  HashMap<String, ArrayList<Fields>>  propertyList = new HashMap<>();
    private ApplicationController appInstance =  ApplicationController.getInstance();

    public CustomizeCartItemAdapter(Context mContext,int itemCount,
                                    String propJson,String itemType,String itemName){

        this.mContext = mContext;
        this.itemCount = itemCount;
        this.itemType = itemType;
        this.itemName = itemName;
        Log.i(TAG,"check propsjson--->" +itemType +"---" +  itemCount+"---" + propJson);
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
                Log.i(TAG, "array--1-" + jsonObject.toString());
//                Log.i(TAG, "array--2-" + jsonObject.getJSONArray(itemName).getJSONObject(position).length()+"---"+po);
                JSONObject jsonObject1 = null;
                if(jsonObject.getJSONArray(itemName).getJSONObject(position).length()==2){
                    Log.i(TAG, "check json--2-" + position + "---" + jsonObject);
                    CommonMethods.addSpecificationsToDialog(mOptionWidgets, mOptionValues, holder.contentLayout,
                            fieldList, mContext, jsonObject1);
                }else {
                    jsonObject1 = jsonObject.getJSONArray(itemName).getJSONObject(position);
                    Log.i(TAG, "check json--1-" + position + "---" + jsonObject1 + "--field--" + fieldList.size());
                    holder.friends_name.setText(jsonObject1.getString("tag"));
                    CommonMethods.addSpecificationsToDialog(mOptionWidgets, mOptionValues, holder.contentLayout,
                            fieldList, mContext, jsonObject1);
                    holder.save_custom_item.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_green_light));
                    holder.save_custom_item.setText("SAVED");
                    Log.i(TAG, "json Object---" + jsonObject1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            JSONObject jsonObject1 = new JSONObject();
            Log.i(TAG, "check json--2-" + position + "---" + jsonObject);
            CommonMethods.addSpecificationsToDialog(mOptionWidgets, mOptionValues, holder.contentLayout,
                    fieldList, mContext, jsonObject1);
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
                                    CommonMethods.generateItemDetails(mOptionWidgets,mOptionValues,name,num,holder.contentLayout))){
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
                Log.i(TAG, "Error parsing item properties from shared prefs:" + ex.getMessage());
            }
        }
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
}*/
