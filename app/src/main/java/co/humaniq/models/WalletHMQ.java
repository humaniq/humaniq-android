package co.humaniq.models;

import android.content.Context;
import android.util.Log;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import co.humaniq.App;
import co.humaniq.Preferences;


public class WalletHMQ {
    private static final String TAG = "WalletHMQ";
//    private Address ethAddress;
    private String walletFile;

    private WalletHMQ() {}

    private WalletHMQ(final String publicAddress, final String walletFile) {
        this.walletFile = walletFile;
    }

    public static WalletHMQ generateWallet(Context context, final String passPhrase) {
        try {
            String fileName = WalletUtils.generateLightNewWalletFile("123321", new File(context.getFilesDir() + "/keystore"));
            Log.d(TAG, context.getFilesDir()+"/keystore/"+fileName);
            Preferences preferences = App.getPreferences(context);
            preferences.setAccountKeyFile(context.getFilesDir()+"/keystore/"+fileName);

            return new WalletHMQ("0x", context.getFilesDir()+"/keystore/"+fileName);
        } catch (CipherException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
//        AccountManager accountManager = new AccountManager(context.getFilesDir() + "/keystore",
//                Geth.LightScryptN, Geth.LightScryptP);
//
//        try {
//            Account acc = accountManager.newAccount(passPhrase);
//            final String publicAddress = acc.getAddress().getHex();
//            Log.d(TAG, publicAddress);
//            Log.d(TAG, acc.getFile());
//
//            Preferences preferences = App.getPreferences(context);
//            preferences.setAccountAddress(publicAddress);
//            preferences.setAccountKeyFile(acc.getFile());
//
//            return new WalletHMQ(publicAddress, acc.getFile());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
        return null;
    }

    public static WalletHMQ getOrCreateWallet(Context context, final String passPhrase) {
        Preferences preferences = App.getPreferences(context);

        String accountKeyFile = preferences.getAccountKeyFile();
//        return WalletHMQ.generateWallet(context, passPhrase);

        if (accountKeyFile.equals("")) {
            return WalletHMQ.generateWallet(context, passPhrase);
        } else {
            return new WalletHMQ("", accountKeyFile);
        }
    }

    public void save(Context context) {
        Preferences preferences = App.getPreferences(context);
    }

    public String getWalletPath() {
        return this.walletFile;
    }
}
