package inquirly.com.inquirlycatalogue.models;

import java.io.Serializable;
import java.security.Permissions;

/**
 * Created by binvij on 11/12/15.
 */
public class AuthenticateRes implements Serializable {

    private AuthenticateResponse response;
    private ResponseStatus status;

    public AuthenticateResponse getResponse() {
        return response;
    }

    public void setResponse(AuthenticateResponse response) {
        this.response = response;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public class AuthenticateResponse{

        private int userId;
        private int tenant_id;
        private int company_id;
        private String emailId;
        private String uniqueId;
        private String userName;
        private String businessName;
        private String catalogueView;
        private String profileImageUrl;
        private Permissions permissions;
        private String businessImageUrl;
        private String businessLocation;
        private String userSecurityToken;

        public int getCompany_id() {
            return company_id;
        }

        public void setCompany_id(int company_id) {
            this.company_id = company_id;
        }

        public int getTenant_id() {
            return tenant_id;
        }

        public void setTenant_id(int tenant_id) {
            this.tenant_id = tenant_id;
        }

        public String getcatalogueView() {
            return catalogueView;
        }

        public void setcatalogueView(String catalogueView) {
            this.catalogueView = catalogueView;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int id) {
            userId = id;
        }

        public String getProfileImageUrl() {
            return profileImageUrl;
        }

        public void setProfileImageUrl(String profileImageUrl) {
            this.profileImageUrl = profileImageUrl;
        }

        public Permissions getPermissions() {
            return permissions;
        }

        public void setPermissions(Permissions permissions) {
            this.permissions = permissions;
        }

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

        public String getBusinessLocation() {
            return businessLocation;
        }

        public void setBusinessLocation(String businessLocation) {
            this.businessLocation = businessLocation;
        }

        public String getBusinessImageUrl() {
            return businessImageUrl;
        }

        public void setBusinessImageUrl(String businessImageUrl) {
            this.businessImageUrl = businessImageUrl;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public void setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
        }

        @Override
        public String toString() {
            return "AuthenticateResponse{" +
                    "emailId='" + emailId + '\'' +
                    "uniqueId='" + uniqueId + '\'' +
                    "userId='" + userId + '\'' +
                    ", userName='" + userName + '\'' +
                    ", userSecurityToken='" + userSecurityToken + '\'' +
                    ", businessName='" + businessName + '\'' +
                    ", businessLocation='" + businessLocation + '\'' +
                    ", businessImageUrl='" + businessImageUrl + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "AuthenticateRes{" +
                "response=" + response +
                ", status=" + status +
                '}';
    }
}
