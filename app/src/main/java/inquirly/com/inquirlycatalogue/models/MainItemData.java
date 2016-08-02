package inquirly.com.inquirlycatalogue.models;

/**
 * Created by kaushal on 04-12-2015.
 */
public class MainItemData {

    private String title;
    private int imageUrl;

    public MainItemData(String title, int imageUrl){

        this.title = title;
        this.imageUrl = imageUrl;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }
}
