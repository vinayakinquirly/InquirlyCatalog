package inquirly.com.inquirlycatalogue.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import inquirly.com.inquirlycatalogue.R;
import inquirly.com.inquirlycatalogue.adapters.DetailsViewInfinitePagerAdapter;
import inquirly.com.inquirlycatalogue.models.CampaignDbItem;
import inquirly.com.inquirlycatalogue.utils.ApiConstants;

public class DetailViewActivity extends AppCompatActivity {

    private ViewPager mviewpager;
    private ArrayList<CampaignDbItem> mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppThemeBlue);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);

        Bundle bundle = getIntent().getExtras();
        final int image_pos = bundle.getInt("itemPosition");
        final String campaignId = bundle.getString(ApiConstants.CAMPAIGN_ID);
        mItems = (ArrayList<CampaignDbItem>)bundle.getSerializable("itemsList");

        DetailsViewInfinitePagerAdapter adapter = new DetailsViewInfinitePagerAdapter(this, mItems, image_pos,campaignId);
        mviewpager = (ViewPager) findViewById(R.id.frame);
        mviewpager.setOffscreenPageLimit(1);
        mviewpager.setAdapter(adapter);
    }

    public void endActivity()
    {
        finish();
    }
}
