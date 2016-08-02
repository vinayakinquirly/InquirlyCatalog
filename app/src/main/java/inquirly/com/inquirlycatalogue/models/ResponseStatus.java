package inquirly.com.inquirlycatalogue.models;

/**
 * Created by binvij on 11/12/15.
 */
public class ResponseStatus {
    private String resMessage;
    private int resCode;

    public String getResMessage() {
        return resMessage;
    }

    public void setResMessage(String resMessage) {
        this.resMessage = resMessage;
    }

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    @Override
    public String toString() {
        return "ResponseStatus{" +
                "resMessage='" + resMessage + '\'' +
                ", resCode=" + resCode +
                '}';
    }
}

/*
db.execSQL("CREATE TABLE " + dataBase.CAMPAIGNDETAILS_TABLE + "(" + dataBase.ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + dataBase.CAMPAIGN_UUID + "INTEGER NOT NULL, FOREIGN KEY(" + dataBase.CAMPAIGN_UUID + ") REFERENCES " + dataBase.CAMPAIGN_TABLE + "(" + dataBase.CAMPAIGN_UUID + "))"
        + dataBase.ITEM_DESCRIPTION + " TEXT," + dataBase.ITEM_NAME + " TEXT," + dataBase.PRIMARY_IMAGE + " TEXT,"
        + dataBase.ITEM_TYPE + "TEXT ," + dataBase.ITEM_CODE + "TEXT ," + dataBase.ITEM_PRICE + "TEXT ,"
        + dataBase.MEDIA1 + " TEXT," + dataBase.MEDIA2 + "TEXT," + dataBase.MEDIA3 + "TEXT," + dataBase.MEDIA4 + "TEXT,"
        + dataBase.MEDIA5 + " TEXT);");
*/
