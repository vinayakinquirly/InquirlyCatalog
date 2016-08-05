package inquirly.com.inquirlycoolberry.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import inquirly.com.inquirlycatalogue.R;
import inquirly.com.inquirlycatalogue.activities.CustomerDetailActivity;
import inquirly.com.inquirlycatalogue.models.BillResponse;
import inquirly.com.inquirlycatalogue.models.ItemBillReq;
import inquirly.com.inquirlycatalogue.utils.CatalogSharedPrefs;
import inquirly.com.inquirlycoolberry.Adapters.ItemBillAdapter;

public class ItemBillActivity extends AppCompatActivity implements View.OnClickListener{

    public Intent intent;
    private String cat_grp;
    public Button btn_payment;
    private Gson gson = new Gson();
    public RecyclerView food_bill_list;
    private ProgressDialog progressDialog;
    public ItemBillAdapter itemBillAdapter ;
    public SharedPreferences sharedPreferences;
    public LinearLayoutManager linearLayoutManager;
    public ItemBillReq billRes = new ItemBillReq();
    private static final String TAG = "ItemBillActivity";
    private BillResponse billResponse = new BillResponse();
    private ArrayList<BillResponse.Taxes> billTaxesList = new ArrayList<>();
    private ArrayList<BillResponse.BillItems> billItemsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        intent = getIntent();
        final String billReceived = intent.getStringExtra("billJson");
        billRes = gson.fromJson(billReceived,ItemBillReq.class);
        Log.i(TAG,"--" + billRes + "--intent--"  + billReceived + "--getBill--" + billRes.getBill().getItems());
        billResponse.setItems(billRes.getBill().getItems());
        billResponse.setTaxes(billRes.getBill().getTaxes());

        for(int i=0;i<billResponse.getItems().size();i++){
            billItemsList.add(billResponse.getItems().get(i));
        }

        for(int i=0;i<billResponse.getTaxes().size();i++){
            billTaxesList.add(billResponse.getTaxes().get(i));
        }
        Log.i(TAG,"items and Taxes---" + billItemsList.size()+"----" + billTaxesList.size());
        progressDialog = new ProgressDialog(ItemBillActivity.this);
        progressDialog.setMessage("Please Hold! getting your bill...");
        progressDialog.setCancelable(false);
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTheme(R.style.CoolberryTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_bill);

        Toolbar toolbar = (Toolbar) findViewById(R.id.bill_toolbar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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

        progressDialog.show();
        food_bill_list = (RecyclerView)findViewById(R.id.food_bill_list);
        sharedPreferences = getSharedPreferences(CatalogSharedPrefs.KEY_CUSTOM_THEME,MODE_PRIVATE);
        btn_payment = (Button)findViewById(R.id.btn_make_payment);

        itemBillAdapter = new ItemBillAdapter(ItemBillActivity.this,billRes.getBill().getTotal(),billTaxesList,billItemsList);
        food_bill_list.setLayoutManager(linearLayoutManager);
        //food_bill_list.setHasFixedSize(true);
        food_bill_list.setAdapter(itemBillAdapter);

        progressDialog.dismiss();
        btn_payment.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_make_payment:
                progressDialog.dismiss();
                onBackPressed();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}