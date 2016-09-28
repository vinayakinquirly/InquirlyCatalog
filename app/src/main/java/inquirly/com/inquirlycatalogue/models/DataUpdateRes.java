package inquirly.com.inquirlycatalogue.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Vinayak on 9/27/2016.
 */
public class DataUpdateRes implements Serializable {

    private int resCode;
    private String resMessage;
    private String ts;
    private String update_on;
    private ArrayList<Updates> updates;

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    public String getResMessage() {
        return resMessage;
    }

    public void setResMessage(String resMessage) {
        this.resMessage = resMessage;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getUpdate_on() {
        return update_on;
    }

    public void setUpdate_on(String update_on) {
        this.update_on = update_on;
    }

    public ArrayList<Updates> getUpdates() {
        return updates;
    }

    public void setUpdates(ArrayList<Updates> updates) {
        this.updates = updates;
    }

    public class Updates implements Serializable{

        private String id;
        private String update;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUpdate() {
            return update;
        }

        public void setUpdate(String update) {
            this.update = update;
        }
    }
}
