package co.humaniq.views;

import android.util.Log;
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

        AuthToken token = (AuthToken) result.data();
        AuthToken.updateInstance(token);

        Log.d(TAG, token.getAuthorization());

        setResult(AuthToken.RESULT_GOT_TOKEN);
        finish();
    }
}
