package inquirly.com.inquirlycatalogue.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Vinayak on 7/20/2016.
 */
public class CustomSettingsRes implements Serializable {

    private CustomSettingsResponse config;
    private ResponseStatus status;
    private int resCode;

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    public CustomSettingsResponse getConfig() {
        return config;
    }

    public void setConfig(CustomSettingsResponse config) {
        this.config = config;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public class CustomSettingsResponse {

        private String catalog_group;
        private String catalog_view;
        private int pipeline_id;
        private boolean show_catalog;
        private boolean show_feedback;
        private CustomeTheme theme;

        public String getCatalog_group() {
            return catalog_group;
        }

        public void setCatalog_group(String catalog_group) {
            this.catalog_group = catalog_group;
        }

        public String getCatalog_view() {
            return catalog_view;
        }

        public void setCatalog_view(String catalog_view) {
            this.catalog_view = catalog_view;
        }

        public int getPipeline_id() {
            return pipeline_id;
        }

        public void setPipeline_id(int pipeline_id) {
            this.pipeline_id = pipeline_id;
        }

        public boolean isShow_catalog() {
            return show_catalog;
        }

        public void setShow_catalog(boolean show_catalog) {
            this.show_catalog = show_catalog;
        }

        public boolean isShow_feedback() {
            return show_feedback;
        }

        public void setShow_feedback(boolean show_feedback) {
            this.show_feedback = show_feedback;
        }

        public CustomeTheme getTheme() {
            return theme;
        }

        public void setTheme(CustomeTheme theme) {
            this.theme = theme;
        }
    }

    public class CustomeTheme implements Serializable {

        private String bg_image_1;
        private String bg_image_2;
        private String bg_image_3;
        private String color_1;
        private String color_2;
        private String image_place_holder;
        private String logo_large;

        public String getBg_image_1() {
            return bg_image_1;
        }

        public void setBg_image_1(String bg_image_1) {
            this.bg_image_1 = bg_image_1;
        }

        public String getBg_image_2() {
            return bg_image_2;
        }

        public void setBg_image_2(String bg_image_2) {
            this.bg_image_2 = bg_image_2;
        }

        public String getBg_image_3() {
            return bg_image_3;
        }

        public void setBg_image_3(String bg_image_3) {
            this.bg_image_3 = bg_image_3;
        }

        public String getColor_1() {
            return color_1;
        }

        public void setColor_1(String color_1) {
            this.color_1 = color_1;
        }

        public String getColor_2() {
            return color_2;
        }

        public void setColor_2(String color_2) {
            this.color_2 = color_2;
        }

        public String getImage_place_holder() {
            return image_place_holder;
        }

        public void setImage_place_holder(String image_place_holder) {
            this.image_place_holder = image_place_holder;
        }

        public String getLogo_large() {
            return logo_large;
        }

        public void setLogo_large(String logo_large) {
            this.logo_large = logo_large;
        }
    }
}
