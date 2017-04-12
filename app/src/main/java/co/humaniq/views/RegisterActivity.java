package co.humaniq.views;

import android.util.Log;

//import org.web3j.protocol.core.methods.response.EthAccounts;

import org.ethereum.geth.Account;
import org.ethereum.geth.AccountManager;
import org.ethereum.geth.Geth;

import co.humaniq.App;
import co.humaniq.Preferences;
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
//        getService().register(getPhotoBase64(), REGISTER_REQUEST);
//        W   eb3.getInstance().getParity().personalNewAccount("123321").observable().subscribe(account -> {
//            for (String account : accounts.getAccounts())
//                Log.d(TAG, account.getAccountId());
//        });
//        Geth.

//        Geth.newAccountManager();
        AccountManager accountManager = new AccountManager(this.getFilesDir() + "/keystore",
                Geth.LightScryptN, Geth.LightScryptP);

        try {
            Account acc =  accountManager.newAccount("123321");
            Log.d(TAG, acc.getAddress().getHex());
            Preferences preferences = App.getPreferences(this);
            preferences.setAccount(acc.getAddress().getHex());

            Log.d(TAG, "Stored acc: "+preferences.getAccount());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
