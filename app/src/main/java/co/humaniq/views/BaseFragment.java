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

    @Override
    public void validationError(Errors errors, int requestCode) {

    }

    @Override
    public void permissionError(Errors errors, int requestCode) {

    }

    @Override
    public void authorizationError(Errors errors, int requestCode) {

    }

    @Override
    public void criticalError(Errors errors, int requestCode) {

    }

    @Override
    public void connectionError(int requestCode) {

    }

    @Override
    public void success(ResultData result, int requestCode) {

    }

    @Override
    public void showProgressbar(int requestCode) {

    }

    @Override
    public void hideProgressbar(int requestCode) {

    }

    @Override
    public void onClick(View view) {

    }
}
