package inquirly.com.inquirlycatalogue.models;

/**
 * Created by binvij on 15/12/15.
 */

public class Fields {

    private String type;
    private String label;
    private String[] options;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}