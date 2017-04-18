package co.humaniq.models;


import android.content.Context;
import android.util.Log;

import org.ethereum.geth.Account;
import org.ethereum.geth.AccountManager;
import org.ethereum.geth.Geth;

import co.humaniq.App;
import co.humaniq.Preferences;


public class WalletHMQ {
    private static final String TAG = "WalletHMQ";
    private String publicAddress;

    private WalletHMQ() {}

    private WalletHMQ(final String publicAddress) {
        this.publicAddress = publicAddress;
    }

    public String getPublicAddress() {
        return publicAddress;
    }

    public static WalletHMQ generateWallet(Context context, final String passPhrase) {
        AccountManager accountManager = new AccountManager(context.getFilesDir() + "/keystore",
                Geth.LightScryptN, Geth.LightScryptP);

        try {
            Account acc =  accountManager.newAccount(passPhrase);
            final String publicAddress = acc.getAddress().getHex();
            Log.d(TAG, publicAddress);

            return new WalletHMQ(publicAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void save(Context context) {
        Preferences preferences = App.getPreferences(context);
        preferences.setAccount(publicAddress);
    }
}
