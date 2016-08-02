package inquirly.com.inquirlycatalogue.rest;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by binvij on 11/12/15.
 */
public interface IRequestCallback {
    void onSuccess(JSONObject response);
    void onError(VolleyError error);
}



