package inquirly.com.inquirlycatalogue.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import inquirly.com.inquirlycatalogue.R;
import inquirly.com.inquirlycatalogue.models.AuthenticateRes;
import inquirly.com.inquirlycatalogue.models.CustomSettingsRes;
import inquirly.com.inquirlycatalogue.rest.ApiRequest;
import inquirly.com.inquirlycatalogue.rest.IRequestCallback;
import inquirly.com.inquirlycatalogue.utils.CatalogSharedPrefs;
import inquirly.com.inquirlycatalogue.utils.CustomEditTextView;
import inquirly.com.inquirlycatalogue.utils.CustomTextView;
import inquirly.com.inquirlycatalogue.utils.InternetConnectionStatus;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;

public class LoginActivity extends AppCompatActivity {

    private Branch branch;
    public ProgressDialog pDialog;
    public AuthenticateRes userAuth;
    private CustomTextView mLoginButton;
    private CustomTextView mErrorTextView;
    private CustomEditTextView mEmailText;
    private CustomEditTextView mPasswordText;
    public CustomSettingsRes customSettingsRes;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_login);
        this.mErrorTextView = (CustomTextView) findViewById(R.id.txt_login_error);
        this.mLoginButton = (CustomTextView) findViewById(R.id.txt_login_btn);
        this.mEmailText = (CustomEditTextView) findViewById(R.id.edit_login_email);
        this.mPasswordText = (CustomEditTextView) findViewById(R.id.edit_login_password);
        Typeface font = Typeface.createFromAsset(this.getApplicationContext().getAssets(), "Montserrat-Regular.ttf");
        mLoginButton.setTypeface(font);
        mEmailText.setTypeface(font);
        mPasswordText.setTypeface(font);

        this.mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = mEmailText.getText().toString();
                String password = mPasswordText.getText().toString();
                if ((password.length() == 0) || (email.length() == 0)) {
                    Toast.makeText(LoginActivity.this, "Email or Password is missing!", Toast.LENGTH_LONG).show();
                } else {
                    makeApiRequest();
                }
            }
        });
    }

    public void makeApiRequest() {
        if (InternetConnectionStatus.checkConnection(getApplicationContext())) {
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Authenticating..");
            pDialog.show();
            ApiRequest.authenticate(
                    mEmailText.getText().toString(),
                    mPasswordText.getText().toString(),
                    new IRequestCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            Gson gson = new Gson();
                            //Log.i(TAG, "reponse from server="+response.toString());
                            userAuth = gson.fromJson(response.toString(), AuthenticateRes.class);
                            if (userAuth.getStatus().getResCode() != 200) {
                                pDialog.dismiss();
                                LoginActivity.this.mErrorTextView.setText(userAuth.getStatus().getResMessage());
                                LoginActivity.this.mErrorTextView.setVisibility(View.VISIBLE);
                                Log.e(LoginActivity.this.TAG, "Error logging user=" + userAuth.getStatus().getResMessage());
                                Toast.makeText(getApplicationContext(), userAuth.getStatus().getResMessage(), Toast.LENGTH_LONG).show();
                            } else {
                                Log.i(TAG, "Authenticate successful=" + userAuth.toString());
                                SharedPreferences.Editor sharedPrefs = getSharedPreferences(CatalogSharedPrefs.KEY_NAME, Context.MODE_PRIVATE).edit();
                                sharedPrefs.putBoolean(CatalogSharedPrefs.KEY_IS_USER_AUTHENTICATED, true);
                                sharedPrefs.putInt(CatalogSharedPrefs.KEY_USER_ID, userAuth.getResponse().getUserId());
                                sharedPrefs.putString(CatalogSharedPrefs.KEY_SEC_TOKEN, userAuth.getResponse().getUserSecurityToken());
                                sharedPrefs.putString(CatalogSharedPrefs.KEY_USER_EMAIL, userAuth.getResponse().getEmailId());
                                sharedPrefs.putString(CatalogSharedPrefs.KEY_CATALOUGE_VIEW, userAuth.getResponse().getcatalogueView());
                                sharedPrefs.commit();
                                Log.i(TAG, "User details stored");
                                new loadClientTheme().execute();
                            }
                        }

                        @Override
                        public void onError(VolleyError error) {
                            pDialog.dismiss();
                            LoginActivity.this.mErrorTextView.setText(error.getMessage());
                            LoginActivity.this.mErrorTextView.setVisibility(View.VISIBLE);
                            LoginActivity.this.mErrorTextView.setText(error.getMessage());
                            Toast.makeText(getApplicationContext(), "Error logging in...Please try again later", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        } else {
            //pDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("No Internet!!");
            builder.setMessage("Internet not connected...please connect and proceed");
            builder.setPositiveButton("OK", null);
            builder.show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        branch = Branch.getInstance();
        branch.initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    // params are the deep linked params associated with the link that the user clicked -> was re-directed to this app
                    // params will be empty if no data found
                    // ... insert custom logic here ...
                    try {
                        Log.i(TAG, "deep linking json :" + referringParams.toString());
                        if (referringParams.has("client")) {
                            String clientName = referringParams.getString("client");
                            SharedPreferences sharedPreferences = getSharedPreferences(CatalogSharedPrefs.KEY_NAME, MODE_PRIVATE);
                            sharedPreferences.edit().putString(CatalogSharedPrefs.KEY_CLIENT_NAME, clientName).commit();
                            Log.i(TAG, "client is : " + clientName);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.i("MyApp", error.getMessage());
                }
            }
        }, this.getIntent().getData(), this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        branch.closeSession();
    }

    public class loadClientTheme extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {

            ApiRequest.getClientTheme(
                    userAuth.getResponse().getUserSecurityToken(),
                    new IRequestCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            String checkResponse = response.toString();
                            Gson gson = new Gson();
                            Log.i(TAG, "reponse from server=" + checkResponse);
                            customSettingsRes = gson.fromJson(response.toString(), CustomSettingsRes.class);
                            Log.i(TAG, "reCode--" + customSettingsRes.getResCode());

                            if (customSettingsRes.getResCode() != 200) {
                                pDialog.dismiss();
                                LoginActivity.this.mErrorTextView.setText(userAuth.getStatus().getResMessage());
                                LoginActivity.this.mErrorTextView.setVisibility(View.VISIBLE);
                                Log.e(LoginActivity.this.TAG, "Error logging user=" + userAuth.getStatus().getResMessage());
                                Toast.makeText(getApplicationContext(), customSettingsRes.getStatus().getResMessage(), Toast.LENGTH_LONG).show();
                            } else {
                                Log.i(TAG, "Custom Theme loaded=" + customSettingsRes.getConfig().getTheme().toString());
                                SharedPreferences.Editor sharedPrefs = getSharedPreferences(CatalogSharedPrefs.KEY_CUSTOM_THEME, Context.MODE_PRIVATE).edit();
                                sharedPrefs.putString(CatalogSharedPrefs.CATALOG_VIEW, customSettingsRes.getConfig().getCatalog_view());
                                sharedPrefs.putBoolean(CatalogSharedPrefs.SHOW_CATALOG, customSettingsRes.getConfig().isShow_catalog());
                                sharedPrefs.putBoolean(CatalogSharedPrefs.SHOW_FEEDBACK, customSettingsRes.getConfig().isShow_feedback());
                                sharedPrefs.putString(CatalogSharedPrefs.CATALOG_GROUP, customSettingsRes.getConfig().getCatalog_group());
                                sharedPrefs.putInt(CatalogSharedPrefs.PIPELINE_ID, customSettingsRes.getConfig().getPipeline_id());
                                sharedPrefs.putString(CatalogSharedPrefs.COLOR_1, customSettingsRes.getConfig().getTheme().getColor_1());
                                sharedPrefs.putString(CatalogSharedPrefs.COLOR_2, customSettingsRes.getConfig().getTheme().getColor_2());
                                sharedPrefs.putString(CatalogSharedPrefs.BG_IMAGE_1, customSettingsRes.getConfig().getTheme().getBg_image_1());
                                sharedPrefs.putString(CatalogSharedPrefs.BG_IMAGE_2, customSettingsRes.getConfig().getTheme().getBg_image_2());
                                sharedPrefs.putString(CatalogSharedPrefs.BG_IMAGE_3, customSettingsRes.getConfig().getTheme().getBg_image_3());
                                sharedPrefs.putString(CatalogSharedPrefs.IMAGE_PLACE_HOLDER, customSettingsRes.getConfig().getTheme().getImage_place_holder());
                                sharedPrefs.putString(CatalogSharedPrefs.LOGO_LARGE, customSettingsRes.getConfig().getTheme().getLogo_large());
                                sharedPrefs.apply();
                                pDialog.dismiss();
                    /*            if(customSettingsRes.getConfig().isShow_feedback()!=null){
                                    if(customSettingsRes.getConfig().isShow_feedback()){
                                        Intent mainActivity = new Intent(LoginActivity.this, CampaignTypeActivity.class);
                                        startActivity(mainActivity);
                                        finish();
                                    }else{*/
                                        Intent mainActivity = new Intent(LoginActivity.this, CampaignTypeActivity.class);
                                        startActivity(mainActivity);
                                        finish();
                                /*    }
                                }else{

                                }*/
                            }
                        }

                        @Override
                        public void onError(VolleyError error) {
                            pDialog.dismiss();
                            Log.i(TAG, "could not set your theme");
                        }
                    }
            );
            return null;
        }
    }

    private class DownloadFile extends AsyncTask {
        private String urlLink, fileName;

        private DownloadFile(String requestUrl, String imagename) {
            this.urlLink = requestUrl;
            this.fileName = imagename;
        }

        @Override
        protected Object doInBackground(Object... objects) {
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/campaignThemes/");
            if (dir.exists() == false) {
                dir.mkdirs();
            }

            try {
                URL url = new URL(urlLink);
                Log.i("FILE_NAME", "campaigns" + fileName + ".png");
                Log.i("FILE_URLLINK", "File URL is " + url);
                URLConnection connection = url.openConnection();
                connection.connect();
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(dir + "/" + fileName + ".png");

                byte data[] = new byte[1024];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("ERROR DOWNLOADING FILES", "ERROR IS" + e);
            }
            return null;
        }
    }
}