package co.humaniq.views;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;

import co.humaniq.models.Errors;
import co.humaniq.models.ResultData;


public class BaseFragment extends Fragment implements ViewContext, View.OnClickListener {
    public void attachOnClickView(@NonNull View v, @IdRes int id) {
        v.findViewById(id).setOnClickListener(this);
    }

    @Override
    public Context getInstance() {
        return getContext();
    }

    @Override
    public BaseActivity getActivityInstance() {
        return (BaseActivity) getActivity();
    }

    public void onApiError(Errors errors, int type, int requestCode) {

    }

    @Override
    public void onApiValidationError(Errors errors, int requestCode) {
        onApiError(errors, API_VALIDATION_ERROR, requestCode);
    }

    @Override
    public void onApiPermissionError(Errors errors, int requestCode) {
        onApiError(errors, API_PERMISSION_ERROR, requestCode);
    }

    @Override
    public void onApiAuthorizationError(Errors errors, int requestCode) {
        onApiError(errors, API_AUTHORIZATION_ERROR, requestCode);
    }

    @Override
    public void onApiCriticalError(Errors errors, int requestCode) {
        onApiError(errors, API_CRITICAL_ERROR, requestCode);
    }

    @Override
    public void onApiConnectionError(int requestCode) {
        onApiError(null, API_CONNECTION_ERROR, requestCode);
    }

    @Override
    public void onApiSuccess(ResultData result, int requestCode) {

    }

    @Override
    public void onApiShowProgressbar(int requestCode) {

    }

    @Override
    public void onApiHideProgressbar(int requestCode) {

    }

    @Override
    public void onClick(View view) {

    }


}
