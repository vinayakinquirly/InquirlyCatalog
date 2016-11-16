package inquirly.com.inquirlycatalogue.rest;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.TimeZone;

import com.google.gson.Gson;
import org.json.JSONException;
import android.content.Context;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;

import inquirly.com.inquirlycatalogue.models.BillResponse;
import inquirly.com.inquirlycatalogue.models.OrderItem;
import inquirly.com.inquirlycatalogue.models.ItemBillReq;
import inquirly.com.inquirlycatalogue.utils.ApiConstants;
import inquirly.com.inquirlycatalogue.ApplicationController;

/**
 * Created by binvij on 11/12/15.
 */
public class ApiRequest {

    private static final String TAG = "ApiRequest";
    private static ApplicationController appInstance = ApplicationController.getInstance();

    public static void postOrder(OrderItem item, String url, final IRequestCallback callback) {
        Gson gson = new Gson();
        //  item.form.customer.Email = "inquirly5@gmail.com";
        JSONObject orderDetails = null;
        Log.i(TAG, "posting order to=" + url);
        try {
            orderDetails =  new JSONObject(gson.toJson(item));
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(

                Request.Method.POST,
                url,
                orderDetails,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        //Log.i(TAG, "order post success message=" + jsonObject.toString());
                        Log.i(TAG, "Success posting order!" + jsonObject.toString());
                        callback.onSuccess(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e(TAG, "order post error message=" + volleyError.getMessage());
                        volleyError.printStackTrace();
                        callback.onError(volleyError);
                    }
                }

        );
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ApplicationController.getInstance().addToRequestQueue(request);

    }

    public static void getCampaignDetails(String campaignId, String email, final String securityToken, final IRequestCallback callback) {

        Log.i(TAG,"eneterd getCampaignDetails" + campaignId + "----" + email);

        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("id", campaignId);
        jsonParams.put("email",email);

        JSONObject object = new JSONObject();
        try {
            object.put("",jsonParams);
            Log.i(TAG,"check final post JSON---" + object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                ApiConstants.API_CAMPAIGN_DETAILS,
                new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.i(TAG,"campaign Details---" + jsonObject.toString());
                        callback.onSuccess(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.i(TAG,"error occured" );
                        callback.onError(volleyError);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put(ApiConstants.CONTENT_TYPE, ApiConstants.APPLICATION_JSON);
                headers.put(ApiConstants.ACCEPT, ApiConstants.APPLICATION_JSON);
                headers.put("UserSecurityToken", securityToken);
                return headers;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 60, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        ApplicationController.getInstance().addToRequestQueue(request);
    }

    public static void getCampaignList(final String securityToken,final String campaignType,final IRequestCallback callback) {
        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("type", campaignType);
        Log.i(TAG, "final post json--" + jsonParams.toString());
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                ApiConstants.API_CAMPAIGN_LIST,
                new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.i(TAG,"campaignList--->" + jsonObject.toString());
                        callback.onSuccess(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        callback.onError(volleyError);
                    }
                }

        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put(ApiConstants.CONTENT_TYPE, ApiConstants.APPLICATION_JSON);
                headers.put(ApiConstants.ACCEPT, ApiConstants.APPLICATION_JSON);
                headers.put("UserSecurityToken",securityToken);
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 60, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        ApplicationController.getInstance().addToRequestQueue(request);
    }

    public static void authenticate( String email, String password,final IRequestCallback callback) {
        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("email", email);
        jsonParams.put("password", password);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                ApiConstants.API_AUTH,
                new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.i(TAG,"authenticate--->" + jsonObject.toString());
                        callback.onSuccess(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e(TAG, "Unable to authenticate user." + volleyError.getMessage());
                        callback.onError(volleyError);

                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put(ApiConstants.CONTENT_TYPE, ApiConstants.APPLICATION_JSON);
                headers.put(ApiConstants.ACCEPT, ApiConstants.APPLICATION_JSON);
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 60, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ApplicationController.getInstance().addToRequestQueue(request);
    }

    public static void getClientTheme(final String security_token, final IRequestCallback callback) {
//        Map<String,String> jsonParams = new HashMap<>();
//        jsonParams.put("security_token", security_token);
//        jsonParams.put("company_id", String.valueOf(tenant_id));

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                ApiConstants.API_CAMPAIGN_TYPE,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.i(TAG,"check response for theme--->" + jsonObject.toString());
                        callback.onSuccess(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        callback.onError(volleyError);
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put(ApiConstants.CONTENT_TYPE, ApiConstants.APPLICATION_JSON);
                headers.put(ApiConstants.ACCEPT, ApiConstants.APPLICATION_JSON);
                headers.put("UserSecurityToken",security_token);
                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 60, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public static void postOrderJson(String catalog_group, int pipeline_id, String bill,String itemRes,
                                     float grandtotal,String customerDetails, String items,
                                     final String security_token, final IRequestCallback callback){

        Log.i(TAG,"bill----" + bill);
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray customerArray = new JSONArray(customerDetails);
            JSONArray itemsArray = new JSONArray(items);
            JSONArray itemsResArray = new JSONArray(itemRes);
            JSONArray taxesArray = new JSONArray(bill);

            for(int i=0;i<itemsArray.length();i++){
                Log.i(TAG,"check size--" +itemsArray.length()+"---"+ itemsArray.getJSONObject(i).
                        getJSONArray("itemProperties").length());

                for(int j=0;j<itemsArray.getJSONObject(i).getJSONArray("itemProperties").length();j++){

                    Object obj = itemsArray.getJSONObject(i).getJSONArray("itemProperties").
                            getJSONObject(j).get("nameValuePairs");
                    itemsArray.getJSONObject(i).getJSONArray("itemProperties").put(j,obj);
                }
            }

            Log.i(TAG,"itemsArray -----" + itemsArray);
            Log.i(TAG,"taxesArray -----" + taxesArray);
            Log.i(TAG,"itemsResArray -----" + itemsResArray);

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("items",itemsResArray);
            jsonObject1.put("taxes",taxesArray);
            jsonObject1.put("total",grandtotal);

            jsonObject.put("catalog_group",catalog_group);
            jsonObject.put("bill",jsonObject1);
            jsonObject.put("customer_details",customerArray);
            jsonObject.put("items",itemsArray);
            jsonObject.put("pipeline_id",pipeline_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i(TAG,"final post JSON----->" + jsonObject.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                ApiConstants.API_CAFE_ORDER,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.i(TAG,"postBillJson---" + jsonObject.toString());
                        callback.onSuccess(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        callback.onError(volleyError);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put(ApiConstants.CONTENT_TYPE, ApiConstants.APPLICATION_JSON);
                headers.put(ApiConstants.ACCEPT, ApiConstants.APPLICATION_JSON);
                headers.put("UserSecurityToken",security_token);
                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 60, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void postBillJson(String catalog_group, String items,
                                    final String security_token, final IRequestCallback callback){
        Log.i(TAG,"check items received---" + catalog_group+ "----" + items + "---" + security_token);
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray itemsArray = new JSONArray(items);
            jsonObject.put("catalog_group",catalog_group);
            jsonObject.put("items",itemsArray);
            Log.i(TAG,"itemsArray--" + itemsArray);

            for(int i=0;i<itemsArray.length();i++){
                Log.i(TAG,"check size--" +itemsArray.length()+"---"+ itemsArray.getJSONObject(i).
                        getJSONArray("itemProperties").length());

                for(int j=0;j<itemsArray.getJSONObject(i).getJSONArray("itemProperties").length();j++){

                    Object obj = itemsArray.getJSONObject(i).getJSONArray("itemProperties").
                            getJSONObject(j).get("nameValuePairs");
                    itemsArray.getJSONObject(i).getJSONArray("itemProperties").put(j,obj);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i(TAG,"final post JSON----->" + jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                ApiConstants.API_CAFE_BILL,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.i(TAG,"postBillJson---" + jsonObject.toString());
                        callback.onSuccess(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        callback.onError(volleyError);
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put(ApiConstants.CONTENT_TYPE, ApiConstants.APPLICATION_JSON);
                headers.put(ApiConstants.ACCEPT, ApiConstants.APPLICATION_JSON);
                headers.put("UserSecurityToken",security_token);
                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 60, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public static void getUpdates(final String security_token,final IRequestCallback callback){

        JSONObject jsonUpdate = new JSONObject();
        try {
            jsonUpdate.put("ts",appInstance.getImage("last_updated"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i(TAG,"final post Json--" + jsonUpdate.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
            Request.Method.POST,
            ApiConstants.API_CAFE_UPDATE,
            jsonUpdate,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    callback.onSuccess(jsonObject);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    callback.onError(volleyError);
                }
            }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put(ApiConstants.CONTENT_TYPE, ApiConstants.APPLICATION_JSON);
                    headers.put(ApiConstants.ACCEPT, ApiConstants.APPLICATION_JSON);
                    headers.put("UserSecurityToken",security_token);
                    return headers;
                }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 60, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest);
    }
}