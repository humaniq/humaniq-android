package co.humaniq.views;

import android.util.Log;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;

import co.humaniq.App;
import co.humaniq.Preferences;
import co.humaniq.models.AuthToken;
import co.humaniq.models.ResultData;


public class RegisterActivity extends LoginRegisterActivity {
    private final static int REGISTER_REQUEST = 2000;
    private final static String TAG = "RegisterActivity";

    @Override
    protected void sendRequest() {
        getService().register(getPhotoBase64(), REGISTER_REQUEST);
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

        Log.d(TAG, token.getAuthorization());

        setResult(AuthToken.RESULT_GOT_TOKEN);
        finish();
    }
}
