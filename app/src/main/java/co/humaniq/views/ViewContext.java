package co.humaniq.views;

import android.content.Context;
import co.humaniq.models.Errors;
import co.humaniq.models.ResultData;


public interface ViewContext {
    public static final int GENERAL_REQUEST = 1000;

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
