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
    private String walletFile;
    private static WalletHMQ instance = null;
    public static final int RESULT_GOT_WALLET = 5000;

    private WalletHMQ() {}

    public WalletHMQ(final String walletFile) {
        this.walletFile = walletFile;
    }

    public static WalletHMQ generateWallet(Context context, final String passPhrase) {
        try {
            String fileName = WalletUtils.generateLightNewWalletFile(passPhrase, new File(context.getFilesDir() + "/keystore"));
            Log.d(TAG, context.getFilesDir()+"/keystore/"+fileName);
            Preferences preferences = App.getPreferences(context);
            preferences.setAccountKeyFile(context.getFilesDir()+"/keystore/"+fileName);

            return new WalletHMQ(context.getFilesDir()+"/keystore/"+fileName);
        } catch (CipherException | IOException | InvalidAlgorithmParameterException |
                NoSuchAlgorithmException | NoSuchProviderException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean getWalletFromPreferences(final String pinCode) {
        return false;
    }

    public static void revoke() {
        instance = null;
    }

    public static WalletHMQ getOrCreateWallet(Context context, final String passPhrase) {
        Preferences preferences = App.getPreferences(context);
        String accountKeyFile = preferences.getAccountKeyFile();

        if (accountKeyFile.equals("")) {
            return WalletHMQ.generateWallet(context, passPhrase);
        } else {
            return new WalletHMQ(accountKeyFile);
        }
    }

    public void save(Context context) {
        Preferences preferences = App.getPreferences(context);
    }

    public String getWalletPath() {
        return this.walletFile;
    }
}
