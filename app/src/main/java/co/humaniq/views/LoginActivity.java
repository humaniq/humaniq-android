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
    public void success(ResultData response, int requestCode) {
        super.success(response, requestCode);

        AuthToken token = (AuthToken) response.data();
        AuthToken.updateInstance(token);

        setResult(AuthToken.RESULT_GOT_TOKEN);
        finish();
    }
}
