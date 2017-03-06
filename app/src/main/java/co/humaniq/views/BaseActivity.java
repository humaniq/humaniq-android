package co.humaniq.views;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import co.humaniq.models.Errors;
import co.humaniq.models.ResultData;


public class BaseActivity extends AppCompatActivity implements ViewContext, View.OnClickListener {
    public void attachOnClickView(@IdRes int id) {
        this.findViewById(id).setOnClickListener(this);
    }

    final boolean grantPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            return false;
        }

        return true;
    }

    final boolean hasPermissions(String[] permissions) {
        for (String permission: permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }

        return true;
    }

    final boolean grantPermissions(String[] permissions, int requestCode) {
        if (!hasPermissions(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, requestCode);
            return false;
        }

        return true;
    }

    final boolean permissionGranted(final int requestCode, @NonNull int[] grantResults,
                                    final int targetRequestCode)
    {
        return requestCode == targetRequestCode && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    final boolean permissionGranted(@NonNull int[] grantResults) {
        return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public Context getInstance() {
        return this;
    }

    @Override
    public BaseActivity getActivityInstance() {
        return this;
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

    @Override
    public void onClick(View v) {

    }
}
