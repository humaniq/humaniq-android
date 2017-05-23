package co.humaniq.views;

import android.content.Context;
import co.humaniq.models.Errors;
import co.humaniq.models.ResultData;


public interface ViewContext {
    public static final int GENERAL_REQUEST = 1000;
    public static final int API_UNSPECIFIED_ERROR = -1;
    public static final int API_VALIDATION_ERROR = 0;
    public static final int API_PERMISSION_ERROR = 1;
    public static final int API_AUTHORIZATION_ERROR = 2;
    public static final int API_CRITICAL_ERROR = 3;
    public static final int API_CONNECTION_ERROR = 4;

    Context getInstance();
    BaseActivity getActivityInstance();

    void onApiValidationError(Errors errors, int requestCode);
    void onApiPermissionError(Errors errors, int requestCode);
    void onApiAuthorizationError(Errors errors, int requestCode);
    void onApiCriticalError(Errors errors, int requestCode);
    void onApiConnectionError(int requestCode);
    void onApiSuccess(ResultData result, int requestCode);
    void onApiShowProgressbar(int requestCode);
    void onApiHideProgressbar(int requestCode);
}
