package co.humaniq.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import co.humaniq.ImageTool;
import co.humaniq.R;
import co.humaniq.Router;
import co.humaniq.models.Errors;
import co.humaniq.models.ResultData;
import co.humaniq.services.AuthService;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class LoginRegisterActivity extends ToolbarActivity {
    private static final String TAG = "LoginRegisterActivity";
    final static int REQUEST_PHOTO_CAPTURE = 2000;
    final static int REQUEST_PHOTO_CAPTURE_PERMISSION = 4000;

    private String photoBase64 = "";
    private ImageView photo;
    private ImageView imageStatus;
    private View progressBar;
    private String capturedPhotoPath;
    private AuthService service;

    public String getPinCode() {
        return pinCode;
    }

    private String pinCode;

    public AuthService getService() {
        return service;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        initToolbar();

        attachOnClickView(R.id.buttonTakePhoto);
        attachOnClickView(R.id.buttonSignature);

        photo = (ImageView) findViewById(R.id.photo);
        imageStatus = (ImageView) findViewById(R.id.imageStatus);
        progressBar = findViewById(R.id.progressBar);

        imageStatus.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        service = new AuthService(this);

        pinCode = getIntent().getStringExtra("pin_code");
        Log.d(TAG, pinCode);
    }

    @SuppressWarnings("unused")
    @OnPermissionResult(REQUEST_PHOTO_CAPTURE_PERMISSION)
    public void openTakePhotoActivity() {
        File photoFile;

        try {
            photoFile = ImageTool.createImageFile(this);
            capturedPhotoPath = photoFile.getAbsolutePath();
        } catch (IOException ex) {
            onApiValidationError(null, 0);
            return;
        }

        Log.e("TakePhotoActivity", capturedPhotoPath);
        Uri photoURI = FileProvider.getUriForFile(this, "co.humaniq.fileprovider", photoFile);

        Bundle bundle = new Bundle();
        bundle.putString(MediaStore.EXTRA_OUTPUT, capturedPhotoPath);

        Router.setBundle(bundle);
        Router.goActivity(this, Router.TAKE_PHOTO, REQUEST_PHOTO_CAPTURE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonTakePhoto:
                grantPermission(Manifest.permission.CAMERA, REQUEST_PHOTO_CAPTURE_PERMISSION);
                break;

            case R.id.buttonSignature:
                Router.goActivity(this, Router.LOGIN_SIGNATURE);
                break;

            default:
                break;
        }
    }

    protected void updatePhotoView() {
        Bitmap thumbnail = ImageTool.decodeSampledBitmap(capturedPhotoPath, 512, 512);
        photo.setImageBitmap(thumbnail);

        Bitmap requestImage = ImageTool.decodeSampledBitmap(capturedPhotoPath, 512, 512);
        photoBase64 = ImageTool.encodeToBase64(requestImage);
        requestImage.recycle();
    }

    public String getPhotoBase64() {
        return photoBase64;
    }

    protected void sendRequest() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        switch (requestCode) {
            case REQUEST_PHOTO_CAPTURE:
                updatePhotoView();
                sendRequest();
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // States

    void errorStatusImage() {
        imageStatus.setVisibility(View.VISIBLE);
        imageStatus.setImageResource(R.drawable.ic_error);
        imageStatus.setColorFilter(ContextCompat.getColor(this, R.color.error));
    }

    void successStatusImage() {
        imageStatus.setVisibility(View.VISIBLE);
        imageStatus.setImageResource(R.drawable.ic_success);
        imageStatus.setColorFilter(ContextCompat.getColor(this, R.color.success));
    }

    @Override
    public void onApiShowProgressbar(int requestCode) {
        imageStatus.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onApiHideProgressbar(int requestCode) {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onApiValidationError(Errors errors, int requestCode) {
        errorStatusImage();
        Log.e(TAG, "Validation Error");
        Log.e(TAG, errors.toString());
    }

    @Override
    public void onApiPermissionError(Errors errors, int requestCode) {
        errorStatusImage();
        Log.e(TAG, "Permission Error");
        Log.e(TAG, errors.toString());
    }

    @Override
    public void onApiAuthorizationError(Errors errors, int requestCode) {
        errorStatusImage();
        Log.e(TAG, "Auth Error");
        Log.e(TAG, errors.toString());
    }

    @Override
    public void onApiCriticalError(Errors errors, int requestCode) {
        errorStatusImage();
        Log.e(TAG, "Critical Error");
        Log.e(TAG, errors.toString());
    }

    @Override
    public void onApiConnectionError(int requestCode) {
        errorStatusImage();
        Log.e(TAG, "Connection Error");
    }

    @Override
    public void onApiSuccess(ResultData result, int requestCode) {
        successStatusImage();
    }
}
