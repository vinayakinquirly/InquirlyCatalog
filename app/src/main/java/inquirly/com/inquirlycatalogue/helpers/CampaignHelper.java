package inquirly.com.inquirlycatalogue.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import inquirly.com.inquirlycatalogue.models.Campaign;
import inquirly.com.inquirlycatalogue.models.CampaignItemData;
import inquirly.com.inquirlycatalogue.utils.CatalogSharedPrefs;

/**
 * Created by binvij on 11/12/15.
 */
public class CampaignHelper {

    private static final String TAG = CampaignHelper.class.getSimpleName();

    /**
     * Get the list of campaign catalogs shared by the current logged in user
     * */
    public static ArrayList<CampaignItemData> buildCampaignList(JSONObject response, Context context) {
        ArrayList<CampaignItemData> itemData = new ArrayList<>();

        try {
            //save the pipeline url for this campaign
            //add pipeline url to shared prefs
            SharedPreferences.Editor editor = context.getSharedPreferences(CatalogSharedPrefs.KEY_NAME, Context.MODE_PRIVATE).edit();

            Log.i(TAG, "pipeline url is =" + response.getString("pipeline_url"));
            JSONArray itemsArray = response.getJSONArray("campaigns");
            Log.i(TAG, "campaigns count parsed="+itemsArray.length());
            Gson gson = new Gson();
            for (int i=0;i<itemsArray.length(); i++) {
                editor.putString(((JSONObject)itemsArray.get(i)).getString("id") + "_" + CatalogSharedPrefs.KEY_PIPELINE_URL, response.getString("pipeline_url"));
                CampaignItemData item = gson.fromJson(itemsArray.get(i).toString(), CampaignItemData.class);
                itemData.add(item);
            }
            editor.commit();

        }catch(Exception ex) {
            Log.e(TAG, "Error parsing campaign list=" + ex.getMessage());
        }

        return itemData;
    }

    /**
     * Builds the campaign details for a given campaign
     * */
    public static ArrayList<Campaign> buildCampaignDetails(JSONObject response, Context context, final String campaignId) {
        ArrayList<Campaign> campaignList = new ArrayList<>();
        Gson gson = new Gson();
        try {
            JSONArray cardsArray = response.getJSONObject("campaign").getJSONArray("cards");
            Log.i(TAG, "card count=" + cardsArray.length());
            for(int i=0; i<cardsArray.length(); i++) {
                Campaign campaign = gson.fromJson(cardsArray.get(i).toString(), Campaign.class);
                JSONObject itemProperties =  ((JSONObject)cardsArray.get(i)).getJSONObject("form_attributes").getJSONObject("item_properties");
                JSONObject pricingModel = ((JSONObject)cardsArray.get(i)).getJSONObject("form_attributes").getJSONObject("pricing_model");
                JSONObject item_tac = ((JSONObject)cardsArray.get(i)).getJSONObject("form_attributes").getJSONObject("item_tac");
                String termsConditions = ((JSONObject)cardsArray.get(i)).getJSONObject("form_attributes").getString("tac");
                SharedPreferences.Editor editor = context.getSharedPreferences(CatalogSharedPrefs.KEY_NAME, Context.MODE_PRIVATE).edit();
                Log.i(TAG, "inserting json properties for item props and pricing props");
                if(itemProperties != null) {
                    Log.i(TAG, "inserting item properties json="+ itemProperties.toString());
                    editor.putString(campaignId + "_" + CatalogSharedPrefs.KEY_ITEM_PROPERTIES, itemProperties.toString());
                }

                if(pricingModel != null) {
                    Log.i(TAG, "inserting pricing model json=" + pricingModel.toString());
                    editor.putString(CatalogSharedPrefs.KEY_PRICING_MODEL, pricingModel.toString());
                }

                if(item_tac!=null){
                    Log.i(TAG, "inserting Item terms & conditions Json=" + item_tac.toString());
                    editor.putString(campaignId + "_" + CatalogSharedPrefs.KEY_TERMS_CONDITIONS, item_tac.toString());
                }

                editor.apply();
                campaignList.add(campaign);
            }
        }catch(JSONException ex) {
            Log.e(TAG, "Error parsing cards="+ex.getMessage());
        }
        Log.i(TAG, "Campaign size found=" + campaignList.size());
        return campaignList;
    }
}
