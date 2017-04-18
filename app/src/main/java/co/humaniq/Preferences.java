package co.humaniq;

import proxypref.annotation.DefaultBoolean;
import proxypref.annotation.DefaultInteger;
import proxypref.annotation.DefaultString;


public interface Preferences {
    @DefaultBoolean(true)
    Boolean getFirstRun();
    void setFirstRun(Boolean value);

    @DefaultBoolean(true)
    Boolean getShowDetection();
    void setShowDetection(Boolean value);

    @DefaultBoolean(true)
    Boolean getNeedPortrait();
    void setNeedPortrait(Boolean needPortrait);

    @DefaultBoolean(true)
    Boolean getUseFrontCamera();
    void setUseFrontCamera(Boolean value);

    @DefaultBoolean(true)
    Boolean getLivenessAuth();
    void setLivenessAuth(Boolean value);

    @DefaultString("")
    String getStartTime();
    void setStartTime(String time);

    @DefaultInteger(0)
    Integer getLoginCount();
    void setLoginCount(Integer count);

    // User
    @DefaultString("")
    String getAccessToken();
    void setAccessToken(String token);

    @DefaultString("")
    String getPinCode();
    void setPinCode(String pinCode);

    @DefaultInteger(0)
    Integer getUserId();
    void setUserId(Integer id);

    @DefaultString("")
    String getAccount();
    void setAccount(String account);
}
