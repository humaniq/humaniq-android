package co.humaniq.views;

import android.content.Context;
import co.humaniq.models.Errors;
import co.humaniq.models.ResultData;


public interface ViewContext {
    public static final int GENERAL_REQUEST = 1000;

    Context getInstance();
    BaseActivity getActivityInstance();
    void validationError(Errors errors, int requestCode);
    void permissionError(Errors errors, int requestCode);
    void authorizationError(Errors errors, int requestCode);
    void criticalError(Errors errors, int requestCode);
    void connectionError(int requestCode);
    void success(ResultData result, int requestCode);
    void showProgressbar(int requestCode);
    void hideProgressbar(int requestCode);
}
