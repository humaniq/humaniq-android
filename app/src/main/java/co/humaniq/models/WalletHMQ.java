package co.humaniq.models;


import android.content.Context;
import android.util.Log;

import org.ethereum.geth.Account;
import org.ethereum.geth.AccountManager;
import org.ethereum.geth.Address;
import org.ethereum.geth.Geth;

import java.io.File;

import co.humaniq.App;
import co.humaniq.Preferences;


public class WalletHMQ {
    private static final String TAG = "WalletHMQ";
    private Address ethAddress;
    private String walletFile;

    public Address getEthAddress() {
        return ethAddress;
    }

    private WalletHMQ() {}

    private WalletHMQ(final String publicAddress, final String walletFile) {
        this.ethAddress = new Address(publicAddress);
        this.walletFile = walletFile;
    }

    public static WalletHMQ generateWallet(Context context, final String passPhrase) {
        AccountManager accountManager = new AccountManager(context.getFilesDir() + "/keystore",
                Geth.LightScryptN, Geth.LightScryptP);

        try {
            Account acc = accountManager.newAccount(passPhrase);
            final String publicAddress = acc.getAddress().getHex();
            Log.d(TAG, publicAddress);
            Log.d(TAG, acc.getFile());

            Preferences preferences = App.getPreferences(context);
            preferences.setAccountAddress(publicAddress);
            preferences.setAccountKeyFile(acc.getFile());

            return new WalletHMQ(publicAddress, acc.getFile());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static WalletHMQ getOrCreateWallet(Context context, final String passPhrase) {
        Preferences preferences = App.getPreferences(context);

        String publicAddress = preferences.getAccountAddress();
        String accountKeyFile = preferences.getAccountKeyFile();

        if (publicAddress.equals("") || accountKeyFile.equals("")) {
            return WalletHMQ.generateWallet(context, passPhrase);
        } else {
//            AccountManager accountManager = new AccountManager(context.getFilesDir() + "/keystore",
//                Geth.LightScryptN, Geth.LightScryptP);
//            accountManager.getAccounts();
            return new WalletHMQ(publicAddress, accountKeyFile);
//            Account acc = new Account();
//            ethAddress = new Address(publicAddress);
//            accountManager.signWithPassphrase();
        }
    }

    public void save(Context context) {
        Preferences preferences = App.getPreferences(context);
    }

    public String getWalletPath() {
        return this.walletFile;
    }
}
