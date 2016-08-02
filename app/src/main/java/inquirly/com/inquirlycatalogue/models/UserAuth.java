package inquirly.com.inquirlycatalogue.models;

/**
 * Created by binvij on 11/12/15.
 */
public class UserAuth {

    private String emailId;
    private String userName;
    private String userSecurityToken;
    private String businessName;
    private String businessImageUrl;
    private int userId;



    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSecurityToken() {
        return userSecurityToken;
    }

    public void setUserSecurityToken(String userSecurityToken) {
        this.userSecurityToken = userSecurityToken;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessImageUrl() {
        return businessImageUrl;
    }

    public void setBusinessImageUrl(String businessImageUrl) {
        this.businessImageUrl = businessImageUrl;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
