package co.humaniq.views;

import android.util.Log;

//import org.web3j.protocol.core.methods.response.EthAccounts;

import co.humaniq.Web3;
import co.humaniq.models.AuthToken;
import co.humaniq.models.ResultData;


public class RegisterActivity extends LoginRegisterActivity {
    private final static int REGISTER_REQUEST = 2000;
    private final static String TAG = "RegisterActivity";

    @Override
    protected void sendRequest() {
//        Web3.getInstance().getWeb3().ethAccounts().observable().subscribe(accounts -> {
//            for (String account : accounts.getAccounts())
//                Log.d(TAG, account);
//        });
        getService().register(getPhotoBase64(), REGISTER_REQUEST);
//        W   eb3.getInstance().getParity().personalNewAccount("123321").observable().subscribe(account -> {
//            for (String account : accounts.getAccounts())
//                Log.d(TAG, account.getAccountId());
//        });
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
