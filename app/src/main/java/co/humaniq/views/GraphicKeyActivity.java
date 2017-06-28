package co.humaniq.views;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;

import android.support.v4.content.FileProvider;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.io.File;
import java.io.IOException;

import co.humaniq.*;
import co.humaniq.models.*;
import co.humaniq.models.Wallet;
import co.humaniq.services.AccountService;
import co.humaniq.views.widgets.GraphicKeyView;


public class GraphicKeyActivity extends ToolbarActivity {
    final static int REQUEST_PHOTO_CAPTURE = 1001;
    final static int REQUEST_PHOTO_CAPTURE_PERMISSION = 4001;
    final static int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 4002;

    final static int REQUEST_GET_META = 2001;
    final static int REQUEST_GET_SALT = 2002;
    final static int REQUEST_GENERATE_SALT = 2003;
    final static int REQUEST_FINISH_REGISTRATION = 2004;

    GraphicKeyView graphicKeyView;

    private Preferences preferences;
    private ProgressDialog progressDialog;
    private String capturedPhotoPath;
    private WalletInfo walletInfo = null;
    private String photoBase64 = "";
    private String pinCode = "";
    private Wallet generatedWallet;
    private boolean triedGetMeta = false;

    final static int NO_ACTION = 0;
    final static int REGISTER_ACTION = 1;
    final static int LOGIN_ACTION = 2;

    private int action = NO_ACTION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphic_key);

        attachOnClickView(R.id.clear_key);
        attachOnClickView(R.id.save_key);

        initToolbar();

        graphicKeyView = (GraphicKeyView) findViewById(R.id.graphic_key);
        graphicKeyView.setCallback(new GraphicKeyView.GraphicKeyCallback() {
            @Override
            public void onFinish(String password) {
                if (password.length() >= 4) {  // Go to next step - nextStepOrLogin method
                    grantPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION);
                }
            }

            @Override
            public void onNewPassword(String password) {
                pinCode = password;
                int color;
                if (password.length() >= 4){
                    findViewById(R.id.clear_key).setClickable(true);
                    findViewById(R.id.save_key).setClickable(true);
                    color = Color.WHITE;
                } else {
                    graphicKeyView.clearKey();
                    color = Color.GRAY;
                }

                ((ImageButton)findViewById(R.id.clear_key)).setColorFilter(color);
                ((ImageButton)findViewById(R.id.save_key)).setColorFilter(color);
            }
        });

        preferences = App.getPreferences(this);

        if (!Wallet.hasKeyOnDevice(this)) {
            findViewById(R.id.clear_key).setVisibility(View.VISIBLE);
            findViewById(R.id.save_key).setVisibility(View.VISIBLE);
            graphicKeyView.setNewPasswordMode(true);
        } else {
            findViewById(R.id.clear_key).setVisibility(View.GONE);
            findViewById(R.id.save_key).setVisibility(View.GONE);
            findViewById(R.id.textView3).setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_reset_pin_code:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnPermissionResult(REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION)
    public void nextStepOrLogin() {  // TODO: rename
        pinCode = graphicKeyView.getEnteredPassword();

        // Every third login, request face
        // In this case if loginCount = 0 then request face else if 3 reset loginCount to 0
        if (preferences.getLoginCount() >= 3)
            preferences.setLoginCount(0);

        if (!Wallet.hasKeyOnDevice(this)) {
            // Try to get meta information about user - for get exist private key on device
            if (!triedGetMeta) {
                triedGetMeta = true;
                requestGetAccountMeta();
                return;
            }

            Bundle bundle = new Bundle();
            bundle.putString("pin_code", pinCode);
            Router.setBundle(bundle);

            requestRegisterAccount();
        } else {
            if (preferences.getLoginCount() == 0 || preferences.getAccountSalt().equals("")) {
                preferences.setAccountSalt("");  // We discard salt, it is necessary to get again
                requestLoginToAccount();
            } else {
                showProgressbar();
                new WalletAsyncTask().execute(
                        new WalletAsyncTaskParam(WalletAsyncTask.SIGN_WALLET)
                );
            }
        }
    }

    private void requestLoginToAccount() {
        action = LOGIN_ACTION;
        grantPermission(Manifest.permission.CAMERA, REQUEST_PHOTO_CAPTURE_PERMISSION);
    }

    private void requestRegisterAccount() {
        action = REGISTER_ACTION;
        grantPermission(Manifest.permission.CAMERA, REQUEST_PHOTO_CAPTURE_PERMISSION);
    }

    private void requestGetAccountMeta() {
        AccountService service = new AccountService(this);
        service.getMeta(Client.getDeviceId(this), REQUEST_GET_META);
    }

    private void showProgressbar() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideProgressbar() {
        if (progressDialog == null)
            return;

        progressDialog.hide();
        progressDialog = null;  // Revoke
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nextStepButton:
                grantPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION);
                break;

            case R.id.clear_key:
                onClearClick();
                break;

            case R.id.save_key:
                onSaveClick();
                break;

            default:
                break;
        }
    }

    private void onClearClick() {
        if (pinCode.length() < 4)
            return;

        ((ImageButton)findViewById(R.id.clear_key)).setColorFilter(Color.GRAY);
        ((ImageButton)findViewById(R.id.save_key)).setColorFilter(Color.GRAY);

        graphicKeyView.clearKey();
    }

    private void onSaveClick() {
        if (pinCode.length() < 4)
            return;

        pinCode = graphicKeyView.getKey();
        grantPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION);
    }

// Take photo --------------------------------------------------------------------------------------

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

        Uri photoURI = FileProvider.getUriForFile(this, "co.humaniq.fileprovider", photoFile);

        Bundle bundle = new Bundle();
        bundle.putString(MediaStore.EXTRA_OUTPUT, capturedPhotoPath);

        Router.setBundle(bundle);

        if (BuildConfig.DEBUG) {
            onActivityResult(REQUEST_PHOTO_CAPTURE, RESULT_OK, null);
        } else {
            Router.goActivity(this, Router.TAKE_PHOTO, REQUEST_PHOTO_CAPTURE);
        }
    }

    protected void retrieveBase64() {
        if (BuildConfig.DEBUG) {
            photoBase64 = GraphicKeyActivityDebug.photoBase64;
            return;
        }

        Bitmap requestImage = ImageTool.decodeSampledBitmap(capturedPhotoPath, 512, 512);
        photoBase64 = ImageTool.encodeToBase64(requestImage);
        requestImage.recycle();
    }

// Async tasks -------------------------------------------------------------------------------------

    static class WalletAsyncTaskParam {
        int action;
        WalletInfo walletInfo;
        Wallet wallet;

        WalletAsyncTaskParam(int action, WalletInfo walletInfo, Wallet wallet) {
            this.action = action;
            this.walletInfo = walletInfo;
            this.wallet = wallet;
        }

        WalletAsyncTaskParam(int action, WalletInfo walletInfo) {
            this.action = action;
            this.walletInfo = walletInfo;
        }

        WalletAsyncTaskParam(int action) {
            this.action = action;
        }
    }

    private class WalletAsyncTask extends AsyncTask<WalletAsyncTaskParam, Void, Wallet> {
        final static int SET_AS_WORK_WALLET = 0;
        final static int SAVE_WALLET = 1;
        final static int SIGN_WALLET = 2;

        private WalletAsyncTaskParam param;

        // Tasks
        private Wallet saveWalletTask() {
            final String finalPassword = pinCode + param.walletInfo.getSalt();

            try {
                return Wallet.generateWallet(GraphicKeyActivity.this, finalPassword);
            } catch (Wallet.WalletNotGeneratedException e) {
                e.printStackTrace();
                return null;
            }
        }

        private Wallet getSignedWalletTask() {
            final String accountFile = preferences.getAccountKeyFile();
            try {
                if (param.walletInfo == null) {
                    return Wallet.getSignedWallet(preferences.getAccountKeyFile(), pinCode,
                            preferences.getAccountSalt());
                } else {
                    return Wallet.getSignedWallet(accountFile, pinCode, param.walletInfo);
                }
            } catch (Wallet.CantSignedException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected Wallet doInBackground(WalletAsyncTaskParam... params) {
            param = params[0];

            switch (param.action) {
                case SAVE_WALLET:
                    return saveWalletTask();

                case SIGN_WALLET:
                    return getSignedWalletTask();

                case SET_AS_WORK_WALLET:
                    return setAsWorkWalletTask();

                default:
                    throw new UnsupportedOperationException();
            }
        }

        private void saveWalletPostExecute(Wallet wallet) {
            wallet.setWalletInfo(walletInfo);
            generatedWallet = wallet;
            AccountService service = new AccountService(GraphicKeyActivity.this);
            service.finishRegistration(wallet.getWalletInfo().getId(),
                    wallet.getAddress(),
                    wallet.getWalletPath(),
                    REQUEST_FINISH_REGISTRATION);
        }

        private Wallet setAsWorkWalletTask() {
            if (param.wallet.setAsWorkWallet(getInstance())) {
                return param.wallet;
            } else {
                return null;
            }
        }

        private void getSignedWalletPostExecute(Wallet signedWallet) {
            new WalletAsyncTask().execute(
                    new WalletAsyncTaskParam(WalletAsyncTask.SET_AS_WORK_WALLET,
                            param.walletInfo, signedWallet)
            );
        }

        private void setAsWorkWalletPostExecute(Wallet signedWallet) {
            if (param.walletInfo != null)
                preferences.setAccountSalt(param.walletInfo.getSalt());

            preferences.setLoginCount(preferences.getLoginCount() + 1);

            setResult(RESULT_OK);
            finish();
        }

        @Override
        protected void onPostExecute(Wallet result) {
            if (result == null) {
                DebugTool.showDialog(GraphicKeyActivity.this, "Error", "Wallet not generated");
                return;
            }

            switch (param.action) {
                case SAVE_WALLET:
                    saveWalletPostExecute(result);
                    break;

                case SIGN_WALLET:
                    getSignedWalletPostExecute(result);
                    break;

                case SET_AS_WORK_WALLET:
                    setAsWorkWalletPostExecute(result);
                    break;

                default:
                    throw new UnsupportedOperationException();
            }
        }
    }

// Request Action ----------------------------------------------------------------------------------

    private void requestAction() throws Wallet.WalletNotGeneratedException {
        switch (action) {
            case REGISTER_ACTION:
                generateNewAccount();
                break;

            case LOGIN_ACTION:
                doLogin();
                break;

            default:
                break;
        }
    }

    private void doLogin() {
        showProgressbar();
        AccountService service = new AccountService(this);
        service.getSalt(photoBase64, preferences.getAccountAddress(), REQUEST_GET_SALT);
    }

    private void generateNewAccount() throws Wallet.WalletNotGeneratedException {
        showProgressbar();
        AccountService service = new AccountService(this);
        service.generateSalt(Client.getDeviceId(this), photoBase64, REQUEST_GENERATE_SALT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK || requestCode != REQUEST_PHOTO_CAPTURE) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        try {
            retrieveBase64();
            requestAction();
        } catch (Wallet.WalletNotGeneratedException e) {
            e.printStackTrace();
        }
    }

// Api events --------------------------------------------------------------------------------------

    @Override
    public void onApiSuccess(ResultData result, int requestCode) {
        if (requestCode != REQUEST_GET_META)
            walletInfo = (WalletInfo) result.data();

        switch (requestCode) {
            case REQUEST_GET_META:
                WalletMeta walletMeta = (WalletMeta) result.data();
                walletMeta.save(this);
                nextStepOrLogin();
                break;

            case REQUEST_GET_SALT:
                new WalletAsyncTask().execute(
                        new WalletAsyncTaskParam(WalletAsyncTask.SIGN_WALLET, walletInfo)
                );
                break;

            case REQUEST_GENERATE_SALT:
                new WalletAsyncTask().execute(
                        new WalletAsyncTaskParam(WalletAsyncTask.SAVE_WALLET, walletInfo)
                );
                break;

            case REQUEST_FINISH_REGISTRATION:
                try {
                    generatedWallet.sign(pinCode, walletInfo);
                    generatedWallet.save(GraphicKeyActivity.this);
//                    generatedWallet.setAsWorkWallet(getInstance());

                    preferences.setAccountSalt(walletInfo.getSalt());
                    preferences.setLoginCount(preferences.getLoginCount() + 1);

//                    setResult(RESULT_OK);
//                    finish();
                    new WalletAsyncTask().execute(
                            new WalletAsyncTaskParam(WalletAsyncTask.SET_AS_WORK_WALLET,
                                    walletInfo, generatedWallet)
                    );
                } catch (Wallet.CantSignedException e) {
                    e.printStackTrace();
                    DebugTool.showDialog(this, "Error", "wallet can't be signed");
                }

                break;

            default:
                break;
        }
    }

    @Override
    public void onApiError(Errors errors, int type, int requestCode) {
        super.onApiError(errors, type, requestCode);
        hideProgressbar();

        if (type != API_CONNECTION_ERROR) {
            DebugTool.showDialog(this, "Error", "connection error");
            return;
        }

        if (requestCode == REQUEST_GET_META) {
            nextStepOrLogin();
        } else {
            DebugTool.showDialog(this, "Error", "something went wrong");
        }
    }
}
