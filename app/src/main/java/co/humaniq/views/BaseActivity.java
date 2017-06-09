package co.humaniq.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.humaniq.Router;
import co.humaniq.models.Errors;
import co.humaniq.models.ResultData;


public class BaseActivity extends AppCompatActivity implements ViewContext, View.OnClickListener {
    protected static final int OUT_TO_MENU_MINUTES = 3;

    boolean outToMenu;

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface OnPermissionResult {
        int value() default ViewContext.GENERAL_REQUEST;
    }

    private class PermissionCallbackItem {
        private Method method;
        private int requestCode;

        PermissionCallbackItem(Method method, int requestCode) {
            this.method = method;
            this.requestCode = requestCode;
        }

        Method getMethod() {
            return method;
        }

        int getRequestCode() {
            return requestCode;
        }
    }

    List<PermissionCallbackItem> permissionCallbacks = new ArrayList<>();

    private void parsePermissionCallbackAnnotation() {
        permissionCallbacks.clear();
        Method[] methods = getClass().getMethods();

        for (Method method : methods) {
            if (!method.isAnnotationPresent(OnPermissionResult.class))
                continue;

            OnPermissionResult callback = method.getAnnotation(OnPermissionResult.class);
            int requestCode = callback.value();

            permissionCallbacks.add(new PermissionCallbackItem(method, requestCode));
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        for (PermissionCallbackItem permissionCallback : permissionCallbacks) {
            if (permissionCallback.getRequestCode() == requestCode &&
                    permissionGranted(grantResults))
            {
                try {
                    permissionCallback.getMethod().invoke(this);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parsePermissionCallbackAnnotation();
    }

    protected List<WeakReference<Fragment>> fragments = new ArrayList<>();

    public void attachOnClickView(@IdRes int id) {
        this.findViewById(id).setOnClickListener(this);
    }

    private void invokePermissionCallbackMethod(final int requestCode) {
        for (PermissionCallbackItem permissionCallback : permissionCallbacks) {
            if (permissionCallback.getRequestCode() != requestCode)
                continue;

            try {
                permissionCallback.getMethod().invoke(this);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    final boolean grantPermission(String permission, int requestCode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            invokePermissionCallbackMethod(requestCode);
            return true;
        }

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            return false;
        }

        invokePermissionCallbackMethod(requestCode);
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
    public void onClick(View v) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (WeakReference<Fragment> fragmentRef : fragments) {
            Fragment fragment = fragmentRef.get();
            if (fragment != null)
                fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        fragments.add(new WeakReference<>(fragment));
    }

    long lastActivity = new Date().getTime();

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        lastActivity = new Date().getTime();
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onResume() {
        super.onResume();
        long now = new Date().getTime();
        if (now - lastActivity > 60000 * OUT_TO_MENU_MINUTES) {
            Router.goActivity(this, Router.GREETER);
            // startActivity and force logon
        }
    }
}
