package co.humaniq.views;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import co.humaniq.models.Errors;
import co.humaniq.models.ResultData;


public class BaseActivity extends AppCompatActivity implements ViewContext {
    @Override
    public Context getInstance() {
        return null;
    }

    @Override
    public BaseActivity getActivityInstance() {
        return null;
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
    public void success(ResultData response, int requestCode) {

    }

    @Override
    public void showProgressbar(int requestCode) {

    }

    @Override
    public void hideProgressbar(int requestCode) {

    }
}
