package co.humaniq.models;

import android.content.Context;
import android.util.Log;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.parity.methods.response.PersonalAccountsInfo;
import org.web3j.utils.Numeric;

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
    private static WalletHMQ workWallet = null;
    private String address;
    private ECKeyPair ecKeyPair;
    public static final int RESULT_GOT_WALLET = 5000;

    public static class WalletNotGeneratedException extends Exception {
        public WalletNotGeneratedException() {
            super("Wallet not generated");
        }
    }

    public static class WalletNotUpdatedException extends Exception {
        public WalletNotUpdatedException() {
            super("Wallet not updated");
        }
    }

    private WalletHMQ() {}

    public ECKeyPair getEcKeyPair() {
        return ecKeyPair;
    }

    public String getAddress() {
        return address;
    }

    private WalletHMQ(ECKeyPair ecKeyPair, final String walletFile, final String address) {
        this.walletFile = walletFile;
        this.address = address;
        this.ecKeyPair = ecKeyPair;
    }

    public static WalletHMQ generateWallet(Context context, ECKeyPair ecKeyPair,
                                           final String password)
            throws WalletNotGeneratedException
    {
        try {
            final String address = Numeric.prependHexPrefix(Keys.getAddress(ecKeyPair));
            final File destDirectoryPath = context.getFilesDir();
            final String fileName = WalletUtils.generateNewWalletFile(password, destDirectoryPath, false);
            final String destFilePath = destDirectoryPath + "/" + fileName;
            Log.d(TAG, destFilePath);
//            Preferences preferences = App.getPreferences(context);
//            preferences.setAccountKeyFile(destFilePath);

            return new WalletHMQ(ecKeyPair, destFilePath, address);
        } catch (CipherException | IOException | InvalidAlgorithmParameterException |
                NoSuchAlgorithmException | NoSuchProviderException e)
        {
            e.printStackTrace();
            throw new WalletNotGeneratedException();
        }
    }

    public static WalletHMQ generateWallet(Context context, final String password)
            throws WalletNotGeneratedException
    {
        ECKeyPair ecKeyPair = null;
        try {
            ecKeyPair = Keys.createEcKeyPair();
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
            throw new WalletNotGeneratedException();
        }
        return generateWallet(context, ecKeyPair, password);
    }

    // Удаление временного файла ключа и создание нового с новым паролем
    // Сохранение настроек и получение Credentials для кошелька
    public static WalletHMQ finishRegistration(Context context, WalletHMQ wallet,
                                          final String password)
            throws WalletNotGeneratedException, WalletNotUpdatedException
    {
        File file = new File(wallet.getWalletPath());
        boolean deleted = file.delete();

        if (!deleted) {
            throw new WalletNotUpdatedException();
        }

        return generateWallet(context, wallet.getEcKeyPair(), password);
    }

    public void setAsWorkWallet() {
        workWallet = this;
    }

    public static boolean getWalletFromPreferences(final String pinCode) {
        return false;
    }

    public static void revoke() {
        workWallet = null;
    }

//    public static WalletHMQ getOrCreateWallet(Context context, final String passPhrase) {
//        Preferences preferences = App.getPreferences(context);
//        String accountKeyFile = preferences.getAccountKeyFile();
//
//        if (accountKeyFile.equals("")) {
//            return WalletHMQ.generateWallet(context, passPhrase);
//        } else {
//            return new WalletHMQ(accountKeyFile);
//        }
//    }

    public void save(Context context) {
        Preferences preferences = App.getPreferences(context);
        preferences.setAccountKeyFile(getWalletPath());
    }

    public String getWalletPath() {
        return this.walletFile;
    }
}
