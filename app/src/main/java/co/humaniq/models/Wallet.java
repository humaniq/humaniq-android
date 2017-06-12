package co.humaniq.models;

import android.content.Context;
import android.os.AsyncTask;

import android.os.Environment;
import android.util.Log;
import co.humaniq.BuildConfig;
import co.humaniq.contracts.Emission;
import co.humaniq.contracts.HumaniqToken;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.concurrent.ExecutionException;

import co.humaniq.App;
import co.humaniq.Preferences;
import co.humaniq.Web3;


public class Wallet {
    private static final String TAG = "Wallet";
    private String walletFile;
    private static Wallet workWallet = null;
    private String address;
    private ECKeyPair ecKeyPair;
    private Credentials credentials;
    private WalletInfo walletInfo;
    private HumaniqToken tokenContract;
    private Emission emissionContract;
    public static Uint256 lastBalance = null;

    public interface BalanceCallback {
        void onFinish(Uint256 balance);
    }

    public class Existence {
        private boolean exist;

        public Existence(boolean exist) {
            this.exist = exist;
        }

        public boolean isExist() {
            return exist;
        }
    }

    public void getBalance(BalanceCallback callback) {
        new AsyncTask<Void, Void, Uint256>() {
            @Override
            protected Uint256 doInBackground(Void... params) {
                try {
                    return getTokenContract().balanceOf(new Address(address)).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Uint256 result) {
                if (result != null) {
                    lastBalance = result;
                    callback.onFinish(result);
                }
            }
        }.execute();
    }

    public static boolean hasKeyOnDevice(Context context) {
        final Preferences preferences = App.getPreferences(context);
        final String accountKeyFile = preferences.getAccountKeyFile();
        final String address = preferences.getAccountAddress();

        return !accountKeyFile.equals("") && !address.equals("");
    }

    public HumaniqToken getTokenContract() {
        return tokenContract;
    }

    public static Wallet getWorkWallet() {
        return workWallet;
    }

    public WalletInfo getWalletInfo() {
        return walletInfo;
    }

    public static class WalletNotGeneratedException extends Exception {
        public WalletNotGeneratedException() {
            super("Wallet not generated");
        }
    }

    public static class CantSignedException extends Exception {
        public CantSignedException() {
            super("Wallet can not be signed");
        }
    }

    private Wallet() {}

    public ECKeyPair getEcKeyPair() {
        return ecKeyPair;
    }

    public String getAddress() {
        return address;
    }

    private Wallet(ECKeyPair ecKeyPair, final String walletFile, final String address) {
        this.walletFile = walletFile;
        this.address = address;
        this.ecKeyPair = ecKeyPair;
    }

    private Wallet(final String walletFile, Credentials credentials) {
        this.walletFile = walletFile;
        this.credentials = credentials;
        this.ecKeyPair = credentials.getEcKeyPair();
        this.address = Numeric.prependHexPrefix(Keys.getAddress(this.ecKeyPair));
    }

    public static Wallet generateWallet(Context context, ECKeyPair ecKeyPair,
                                        final String password)
            throws WalletNotGeneratedException
    {
        try {
            final String address = Numeric.prependHexPrefix(Keys.getAddress(ecKeyPair));
            final File destDirectory = Environment.getExternalStorageDirectory().getAbsoluteFile();
            Log.d(TAG, Environment.getExternalStorageState());
            final String fileName = WalletUtils.generateWalletFile(password, ecKeyPair, destDirectory, false);
            final String destFilePath = destDirectory + "/" + fileName;

            return new Wallet(ecKeyPair, destFilePath, address);
        } catch (CipherException | IOException e)
        {
            e.printStackTrace();
            throw new WalletNotGeneratedException();
        }
    }

    public void setWalletInfo(WalletInfo walletInfo) {
        this.walletInfo = walletInfo;
    }

    public static Wallet generateWallet(Context context, final String password)
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

    public static Wallet getSignedWallet(String walletFile, String pinCode, WalletInfo walletInfo)
            throws CantSignedException
    {
        return getSignedWallet(walletFile, pinCode, walletInfo.getSalt());
    }

    public static Wallet getSignedWallet(String walletFile, String pinCode, String salt)
            throws CantSignedException
    {
        try {
            Credentials credentials = WalletUtils.loadCredentials(pinCode + salt, walletFile);
            return new Wallet(walletFile, credentials);
        } catch (IOException | CipherException e) {
            e.printStackTrace();
            throw new CantSignedException();
        }
    }

    public void sign(String pinCode, WalletInfo walletInfo) throws CantSignedException {
        try {
            final String fullPassword = pinCode + walletInfo.getSalt();

            if (getEcKeyPair() != null) {
                credentials = Credentials.create(getEcKeyPair());
            } else {
                credentials = WalletUtils.loadCredentials(fullPassword, getWalletPath());
            }
        } catch (IOException | CipherException e) {
            e.printStackTrace();
            throw new CantSignedException();
        }
    }

    public boolean setAsWorkWallet() {
        workWallet = this;
        Web3 web3 = Web3.getInstance();

        workWallet.emissionContract = new Emission(
                BuildConfig.EMISSION_CONTRACT_ADDRESS,
                web3.getWeb3(),
                workWallet.credentials,
                ManagedTransaction.GAS_PRICE,
                Contract.GAS_LIMIT
        );

        workWallet.tokenContract = new HumaniqToken(
                BuildConfig.HMQ_TOKEN_CONTRACT_ADDRESS,
                web3.getWeb3(),
                workWallet.credentials,
                ManagedTransaction.GAS_PRICE,
                Contract.GAS_LIMIT
        );

        return true;
    }

    public static void revoke() {
        workWallet = null;
    }

    public void save(Context context) {
        Preferences preferences = App.getPreferences(context);
        preferences.setAccountKeyFile(getWalletPath());
        preferences.setAccountAddress(getAddress());
    }

    public String getQRCodeImageURL() {
        return BuildConfig.SERVER_URL + "/qr_codes/" + getAddress();
    }

    public String getWalletPath() {
        return this.walletFile;
    }
}
