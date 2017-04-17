package co.humaniq.views;

import android.util.Log;

import co.humaniq.App;
import co.humaniq.Preferences;
import co.humaniq.models.AuthToken;
import co.humaniq.models.ResultData;


public class LoginActivity extends LoginRegisterActivity {
    final private static int LOGIN_REQUEST = 2000;

    @Override
    protected void sendRequest() {
        getService().login(getPhotoBase64(), LOGIN_REQUEST);
    }

    @Override
    public void onApiSuccess(ResultData result, int requestCode) {
        super.onApiSuccess(result, requestCode);

        Preferences preferences = App.getPreferences(this);

        if (preferences.getPinCode().trim().equals(""))
            preferences.setPinCode(getPinCode());

        preferences.setLoginCount(0);

        AuthToken token = (AuthToken) result.data();
        AuthToken.updateInstance(this, token);

//        token.getUser().crashlyticsLog();

//        Answers.getInstance().logLogin(new LoginEvent()
//                .putMethod("Face")
//                .putSuccess(true));

//        Log.d(TAG, token.getAuthorization());

        setResult(AuthToken.RESULT_GOT_TOKEN);
        finish();
    }
}
