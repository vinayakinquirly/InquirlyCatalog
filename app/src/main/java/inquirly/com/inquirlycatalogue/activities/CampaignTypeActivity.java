package inquirly.com.inquirlycatalogue.activities;

import android.util.Log;
import android.view.View;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.Button;
import android.graphics.Color;
import android.content.Intent;
import android.widget.EditText;
import android.content.Context;
import android.graphics.Typeface;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.squareup.picasso.Picasso;
import android.content.DialogInterface;
import inquirly.com.inquirlycatalogue.R;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.app.AppCompatActivity;
import inquirly.com.inquirlycatalogue.utils.ApiConstants;
import inquirly.com.inquirlycatalogue.ApplicationController;
import inquirly.com.inquirlycatalogue.utils.CatalogSharedPrefs;
import inquirly.com.inquirlycoolberry.Activity.CoolberryMainActivity;

public class CampaignTypeActivity extends AppCompatActivity {

    private EditText input;
    private Boolean showFeedback = true;
    public AlertDialog.Builder alertDialog;
    public static String catalougeView,userId;
    public Button btn_feedback, btn_catalogue;
    public String themeResponse,color_1,color_2;
    public CardView card_catalogue,card_feedback;
    private static final String TAG = "CampaignTypeActivity";
    public ImageView type_back_1,type_back_2,type_back_3,type_logo;
    public SharedPreferences mSharedPrefs,sharedPreferences,sPrefs;
    private ApplicationController instance = ApplicationController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        themeResponse = intent.getStringExtra("themeResponse");
        Log.i(TAG,"check theme--->" + themeResponse);
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        isLoggedIn();
        Log.i(TAG,"--->entered");

        sPrefs = this.getSharedPreferences(CatalogSharedPrefs.KEY_NAME, Context.MODE_PRIVATE);
        sharedPreferences = getSharedPreferences(CatalogSharedPrefs.KEY_CUSTOM_THEME, Context.MODE_PRIVATE);
        catalougeView = sharedPreferences.getString(CatalogSharedPrefs.CATALOG_VIEW,null);
        showFeedback = sharedPreferences.getBoolean(CatalogSharedPrefs.SHOW_FEEDBACK,false);

        color_1 = sharedPreferences.getString(CatalogSharedPrefs.COLOR_1,null);
        color_2 = sharedPreferences.getString(CatalogSharedPrefs.COLOR_2,null);
        Log.i(TAG,"check view--->" + catalougeView + "---" + showFeedback);

        userId = sPrefs.getString(CatalogSharedPrefs.KEY_USER_EMAIL, null);
        mSharedPrefs.getBoolean(CatalogSharedPrefs.KEY_TABLE_ID_SELECTED, false);
        setContentView(R.layout.activity_campaign_type);

//        card_feedback = (CardView) findViewById(R.id.card_feedback);
//        card_catalogue = (CardView) findViewById(R.id.card_catalogue);
        btn_feedback = (Button) findViewById(R.id.btn_feedback);
        btn_catalogue = (Button) findViewById(R.id.btn_catalogue);

        if(!showFeedback){
            btn_feedback.setVisibility(View.GONE);
        }

        if(catalougeView!=null&& (showFeedback!=null)&& (color_1!=null) && (color_2!=null) ){
            btn_feedback.setBackgroundColor(Color.parseColor(color_1));
            btn_catalogue.setBackgroundColor(Color.parseColor(color_1));
            btn_feedback.setTextColor(getResources().getColor(android.R.color.white));
            btn_catalogue.setTextColor(getResources().getColor(android.R.color.white));
        }else {
//            Toast.makeText(CampaignTypeActivity.this, "Please Re-Login into app", Toast.LENGTH_SHORT).show();
        }

        type_back_1= (ImageView)findViewById(R.id.type_back_1);
        type_back_2 = (ImageView)findViewById(R.id.type_back_2);
        type_back_3 = (ImageView)findViewById(R.id.type_back_3);
        type_logo = (ImageView)findViewById(R.id.type_logo);

        Picasso.with(this).load(instance.getImage("bg_1")).resize(700,120).into(type_back_1);
        Picasso.with(this).load(instance.getImage("bg_1")).resize(700,120).into(type_back_2);
        Picasso.with(this).load(instance.getImage("bg_1")).resize(700,120).into(type_back_3);
        Picasso.with(this).load(instance.getImage("logo")).resize(500,0).into(type_logo);

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "Montserrat-Regular.ttf");
        btn_catalogue.setTypeface(font);
        btn_feedback.setTypeface(font);

        btn_catalogue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"view check----" + catalougeView);
                if(catalougeView!=null) {
                    if (catalougeView.equals("food_menu_view")) {
                        Log.i(TAG, "food menu entered");
                        Intent intent = new Intent(getApplicationContext(), CoolberryMainActivity.class);
                        intent.putExtra(ApiConstants.CAMPAIGN_TYPE, ApiConstants.CAMPAIGN_TYPE_CATALOG);
                        startActivity(intent);
                    } else {
                        Log.i(TAG, "no client");
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        i.putExtra(ApiConstants.CAMPAIGN_TYPE, ApiConstants.CAMPAIGN_TYPE_CATALOG);
                        startActivity(i);
                    }
                }else {
                    Toast.makeText(CampaignTypeActivity.this, "Please Re-Login to get started.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra(ApiConstants.CAMPAIGN_TYPE, ApiConstants.CAMPAIGN_TYPE_FEEDBACK);
                startActivity(intent);
            }
        });
    }

    private void isLoggedIn() {
        mSharedPrefs = getSharedPreferences(CatalogSharedPrefs.KEY_NAME, Context.MODE_PRIVATE);
        boolean isAuthenticated = mSharedPrefs.getBoolean(CatalogSharedPrefs.KEY_IS_USER_AUTHENTICATED, false);
        if (!isAuthenticated) {
            Log.i(TAG, "User not authenticated");
            Intent loginIntent = new Intent(getApplication(), LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            finish();
        }
    }

    private void showpopup() {
        final SharedPreferences.Editor editor = mSharedPrefs.edit();

        input = new EditText(CampaignTypeActivity.this);
        alertDialog = new AlertDialog.Builder(CampaignTypeActivity.this);
        alertDialog.setTitle("Enter Table Name");

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        lp.setMargins(50, 40, 200, 0);
        layout.addView(input,lp);

        alertDialog.setView(layout);
        alertDialog.setIcon(R.mipmap.dialog_icon);

        alertDialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String table_name = input.getText().toString();
                        if (table_name.equals("")) {
                            Toast.makeText(getApplicationContext(), "Please specify the table name", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            i.putExtra(ApiConstants.CAMPAIGN_TYPE, ApiConstants.CAMPAIGN_TYPE_CATALOG);
                            startActivity(i);
                            editor.putString(CatalogSharedPrefs.TABLE_NAME, table_name);
                            editor.putBoolean(CatalogSharedPrefs.KEY_TABLE_ID_SELECTED, true);
                            editor.commit();
                        }
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
    }
}