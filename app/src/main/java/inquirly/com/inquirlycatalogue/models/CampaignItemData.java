package inquirly.com.inquirlycatalogue.models;

/**
 * Created by kaushal on 04-12-2015.
 */
public class CampaignItemData {

    private String hash_tag;
    private String valid_till;
    private String id;
    private Preview preview;
    private String name;
    private String state;
    private String campaign_url;

    public String getCampaign_url(){
        return campaign_url;
    }

    public void setCampaign_url( String campaignUrl) {
        this.campaign_url = campaignUrl;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name=name;
    }

    public String getHash_tag() {
        return hash_tag;
    }

    public void setHash_tag(String hash_tag) {
        this.hash_tag = hash_tag;
    }

    public String getValid_till() {
        return valid_till.substring(0,17);
    }

    public void setValid_till(String valid_till) {
        this.valid_till = valid_till;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Preview getPreview() {
        return preview;
    }

    public void setPreview(Preview preview) {
        this.preview = preview;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public class Preview {
        private String description;
        private String image;
        private String title;

        public Preview() {
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
