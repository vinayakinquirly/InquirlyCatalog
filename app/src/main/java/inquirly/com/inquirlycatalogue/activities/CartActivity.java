package inquirly.com.inquirlycatalogue.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import inquirly.com.inquirlycatalogue.ApplicationController;
import inquirly.com.inquirlycatalogue.R;
import inquirly.com.inquirlycatalogue.adapters.RecyclerCartAdapter;
import inquirly.com.inquirlycatalogue.models.CartItem;
import inquirly.com.inquirlycatalogue.utils.ApiConstants;
import inquirly.com.inquirlycatalogue.utils.RecyclerItemClickListener;

public class CartActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    RecyclerCartAdapter mAdapter;
    public TextView mTxtTotalPrice;
    private TextView mCartEmpty;
    private RecyclerView.LayoutManager mLayoutManager;
    int cartCount;
    Button btn_CheckOut, btn_Shop;
    private String mCampaignId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppThemeBlue);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_cart);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Cart");

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "Montserrat-Regular.ttf");

        for(int i = 0; i < toolbar.getChildCount(); i++){
            View view = toolbar.getChildAt(i);
            if(view instanceof TextView){
                TextView tv = (TextView) view;
                if(tv.getText().equals(this.getTitle())){
                    tv.setTypeface(font);
                    tv.setAllCaps(true);
                    break;
                }
            }
        }

        mCampaignId = getIntent().getStringExtra("campaign_id");

        btn_CheckOut = (Button) findViewById(R.id.btn_checkout);
        btn_Shop = (Button) findViewById(R.id.btn_shop);
        mTxtTotalPrice = (TextView) findViewById(R.id.txtTotalPrice);
        mCartEmpty = (TextView) findViewById(R.id.txt_cart_empty);
        mTxtTotalPrice.setTypeface(font);
        btn_Shop.setTypeface(font);
        btn_CheckOut.setTypeface(font);
        mCartEmpty.setTypeface(font);

        getTotalamount();

        btn_Shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_cart);
        cartCount = ApplicationController.getInstance().getJustCartItemCount();
        if (cartCount == 0) {
            btn_CheckOut.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
            mTxtTotalPrice.setVisibility(View.GONE);
            mCartEmpty.setTypeface(font);
            mCartEmpty.setText("Your don't have any item in your cart!");
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) btn_Shop.getLayoutParams();
            lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            btn_Shop.setLayoutParams(lp);
            mCartEmpty.setVisibility(View.VISIBLE);

        } else {
            mCartEmpty.setVisibility(View.GONE);
            btn_CheckOut.setVisibility(View.VISIBLE);
            mTxtTotalPrice.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            btn_CheckOut.setEnabled(true);
            ArrayList<CartItem> cartItems = ApplicationController.getInstance().getListCartItems();
            RecyclerCartAdapter mAdapter = new RecyclerCartAdapter(this, cartItems);
            mRecyclerView.setAdapter(mAdapter);
            // mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLayoutManager);

            mRecyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            // TODO Handle item click

                        }
                    })
            );
        }

        btn_CheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ApplicationController.getInstance().getJustCartItemCount() == 0) {

                    Toast.makeText(getApplicationContext(),"Cart cannot be empty...",Toast.LENGTH_SHORT).show();

                }
                else {
                    Intent i = new Intent(getApplicationContext(), CustomerDetailActivity.class);
                    i.putExtra("campaign_id", CartActivity.this.mCampaignId);
                    startActivity(i);
                    finish();
                }
            }
        });

        btn_Shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra(ApiConstants.CAMPAIGN_TYPE, ApiConstants.CAMPAIGN_TYPE_CATALOG);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void getTotalamount() {

        mTxtTotalPrice.setText("Total : Rs." + ApplicationController.getInstance().getTotalCartAmount());

    }

}
