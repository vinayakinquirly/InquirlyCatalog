package inquirly.com.inquirlycoolberry.Activity;

import android.util.Log;
import android.view.View;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.content.Intent;
import android.graphics.Color;
import android.widget.TextView;
import android.widget.ImageView;
import android.graphics.Typeface;
import android.view.WindowManager;
import com.squareup.picasso.Picasso;
import inquirly.com.inquirlycatalogue.R;
import android.support.v7.widget.Toolbar;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import inquirly.com.inquirlycatalogue.utils.ApiConstants;
import inquirly.com.inquirlycatalogue.ApplicationController;

public class OrderPlacedActivity extends AppCompatActivity {

    public Intent intent;
    private Button closeBtn;
    public TextView message_large,message_small;
    public String large,small,image_url,logo_url;
    private static final String TAG = "OrderPlaceActivity";
    public ImageView order_back_1,order_back_2,order_back_3,logo_image;
    private ApplicationController appInstance = ApplicationController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_order_place);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            toolbar.setBackgroundColor(Color.parseColor(appInstance.getImage("color_1")));
            Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "Montserrat-Regular.ttf");
            for(int i = 0; i < toolbar.getChildCount(); i++){
                View view = toolbar.getChildAt(i);
                if(view instanceof TextView){
                    TextView tv = (TextView) view;
                    if(tv.getText().equals(this.getTitle())) {
                        tv.setTypeface(font);
                        tv.setTextSize(18);
                        tv.setAllCaps(true);
                        break;
                    }
                }
            }
        }

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        intent = getIntent();
        image_url = intent.getStringExtra("back_url");
        logo_url = intent.getStringExtra("logo_url");
        large = intent.getStringExtra("large");
        small = intent.getStringExtra("small");

        message_large = (TextView)findViewById(R.id.message_large);
        message_small = (TextView)findViewById(R.id.message_small);
        order_back_1 = (ImageView)findViewById(R.id.order_back_1);
        order_back_2 = (ImageView)findViewById(R.id.order_back_2);
        order_back_3 = (ImageView)findViewById(R.id.order_back_3);
        logo_image = (ImageView)findViewById(R.id.logo_image);
        closeBtn = (Button)findViewById(R.id.btn_close);

        closeBtn.setBackgroundColor(Color.parseColor(appInstance.getImage("color_1")));
        Log.i(TAG,"large---" + large + "--small--" + small);
        Log.i(TAG,"large---" + image_url + "--small--" + logo_url);

        Picasso.with(this).load(logo_url).into(logo_image);
        Picasso.with(this).load(image_url).resize(700,112).into(order_back_1);
        Picasso.with(this).load(image_url).resize(700,112).into(order_back_2);
        Picasso.with(this).load(image_url).resize(700,112).into(order_back_3);

        message_large.setText(large);
        message_small.setText(small);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CoolberryMainActivity.class);
                intent.putExtra(ApiConstants.CAMPAIGN_TYPE, ApiConstants.CAMPAIGN_TYPE_CATALOG);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}