package inquirly.com.inquirlycatalogue.models;

import java.util.ArrayList;

/**
 * Created by kaushal on 24-12-2015.
 */
public interface CampaignDetails {

    public void addCampaigns(CampaignItemData itemdata);
    public ArrayList<CampaignItemData> getAllData();

}
