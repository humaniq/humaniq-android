package co.humaniq;

import proxypref.annotation.DefaultBoolean;
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

    @DefaultBoolean(false)
    Boolean getLivenessAuth();
    void setLivenessAuth(Boolean value);

    @DefaultString("")
    String getStartTime();
    void setStartTime(String time);
}
