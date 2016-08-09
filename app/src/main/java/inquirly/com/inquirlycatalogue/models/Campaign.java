package inquirly.com.inquirlycatalogue.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by binvij on 12/12/15.
 */

public class Campaign {

    public String category;
    private String campaign_url;
    private FormAttributes form_attributes;

    public String getFeedback_link() {
        return campaign_url;
    }

    public FormAttributes getForm_attributes() {
        return form_attributes;
    }

    public void setFeedback_link(String feedback_link) {
        this.campaign_url = feedback_link;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setForm_attributes(FormAttributes form_attributes) {
        this.form_attributes = form_attributes;
    }

    public class FormAttributes {

        private String category_name;
        private String feedback_label;
        private String tac;
        private SubCategories[] sub_categories;
        private HashMap<String,ArrayList<Fields>> itemProperties;

        public String getTac() {
            return tac;
        }

        public void setTac(String tac) {
            this.tac = tac;
        }

        public String getFeedback_label() {
            return feedback_label;
        }

        public void setFeedback_label(String feedback_label) {
            this.feedback_label = feedback_label;
        }


        public void setItemProperties(HashMap<String,ArrayList<Fields>> properties) {
            itemProperties = properties;
        }

        public HashMap<String,ArrayList<Fields>> getItemProperties() {
            return itemProperties;
        }

        public SubCategories[] getSub_categories() {
            return sub_categories;
        }

        public void setSub_categories(SubCategories[] sub_categories) {
            this.sub_categories = sub_categories;
        }

        public String getCategory_name() {
            return category_name;
        }

        public void setCategory_name(String category_name) {
            this.category_name = category_name;
        }

        public class SubCategories {
            private Item[] items;
            private String name;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Item[] getItems() {
                return items;
            }

            public void setItems(Item[] items) {
                this.items = items;
            }

            public class Item implements Serializable{
                private String description;
                private String primary_image;
                private String name;
                private Media[] media;
                private String item_code;
                private int price;
                private String type;
                private boolean is_active;

                public void setType(String type) {
                    this.type = type;
                }
                public String getType() {
                    return type;
                }

                public String getItem_code() {
                    return item_code;
                }

                public void setItem_code(String item_code) {
                    this.item_code = item_code;
                }

                public int getPrice() {
                    return price;
                }

                public void setPrice(int price) {
                    this.price = price;
                }

                public boolean is_active() {
                    return is_active;
                }

                public void setIs_active(boolean is_active) {
                    this.is_active = is_active;
                }

                public String getDescription() {
                    return description;
                }

                public void setDescription(String description) {
                    this.description = description;
                }

                public String getPrimary_image() {
                    return primary_image;
                }

                public void setPrimary_image(String primary_image) {
                    this.primary_image = primary_image;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public Media[] getMedia() {
                    return media;
                }

                public void setMedia(Media[] media) {
                    this.media = media;
                }

                public class Media implements Serializable {
                    public String getType() {
                        return type;
                    }

                    public void setType(String type) {
                        this.type = type;
                    }

                    public String getUrl() {
                        return url;
                    }

                    public void setUrl(String url) {
                        this.url = url;
                    }

                    private String type;
                    private String url;

                }
            }
        }
    }
}