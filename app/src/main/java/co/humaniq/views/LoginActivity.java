package co.humaniq.views;

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

        AuthToken token = (AuthToken) result.data();
        AuthToken.updateInstance(this, token);

        setResult(AuthToken.RESULT_GOT_TOKEN);
        finish();
    }
}
