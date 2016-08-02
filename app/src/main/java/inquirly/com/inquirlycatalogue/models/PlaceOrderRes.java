package inquirly.com.inquirlycatalogue.models;

import java.io.Serializable;

/**
 * Created by Vinayak on 7/22/2016.
 */
public class PlaceOrderRes implements Serializable {

    private String message_large;
    private String message_small;
    private int status;
    private String resMessage;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getResMessage() {
        return resMessage;
    }

    public void setResMessage(String resMessage) {
        this.resMessage = resMessage;
    }

    public String getMessage_large() {
        return message_large;
    }

    public void setMessage_large(String message_large) {
        this.message_large = message_large;
    }

    public String getMessage_small() {
        return message_small;
    }

    public void setMessage_small(String message_small) {
        this.message_small = message_small;
    }
}
