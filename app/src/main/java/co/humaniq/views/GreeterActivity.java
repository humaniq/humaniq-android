package co.humaniq.views;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import co.humaniq.App;
import co.humaniq.Preferences;
import co.humaniq.R;
import co.humaniq.Router;
import co.humaniq.Web3;
import co.humaniq.models.AuthToken;
import co.humaniq.models.User;
import co.humaniq.models.Wallet;
import co.humaniq.models.WalletHMQ;
import co.humaniq.views.dashboard_fragments.HistoryFragment;
import com.crashlytics.android.Crashlytics;

import org.spongycastle.util.encoders.Hex;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.RawTransaction;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.parity.methods.response.PersonalUnlockAccount;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

import io.fabric.sdk.android.Fabric;


public class GreeterActivity extends BaseActivity {
    private final static int LOGIN_REQUEST = 1000;
    private final static int REGISTER_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeter);

        attachOnClickView(R.id.buttonLogin);
        attachOnClickView(R.id.buttonRegister);

        String ABI = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ABI = Build.SUPPORTED_ABIS[0];
        } else {
            ABI = Build.CPU_ABI;
        }
        Log.d("ABI", ABI);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonLogin:
//                Router.goActivity(this, Router.LOGIN, LOGIN_REQUEST);
//                Router.goActivity(this, Router.PIN_CODE, LOGIN_REQUEST);

//                Wallet wallet = new Wallet(45, "424d590c-84c7-492c-866a-176aa947ab5c", 444, false,
//                        "HMQ", "http://13.75.91.36/media/qr_codes/424d590c-84c7-492c-866a-176aa947ab5c.png");
//                User user = new User(45, "", wallet);
//                AuthToken token = new AuthToken("5f5d0f032d8908f4e6d253593bfba02f847ee823", user);
//                AuthToken.updateInstance(token);
//                Router.goActivity(this, Router.DASHBOARD);
//                Preferences preferences = App.getPreferences(this);
                WalletHMQ wallet = WalletHMQ.getOrCreateWallet(this, "123321");
                Log.d("WALLET", wallet.getEthAddress().getHex());
                Log.d("WALLET", wallet.getWalletPath());

                Web3 web3 = Web3.getInstance();
                Web3j web3j = web3.getWeb3();

//                PersonalUnlockAccount personalUnlockAccount = null;
//                try {
//                    personalUnlockAccount = web3.getParity().personalUnlockAccount("0x4a88ba24e71a20e3a3290531433de7ab50f074dd", "123321").sendAsync().get();
//
//                    if (personalUnlockAccount.accountUnlocked()) {
//                        Transaction transaction = new Transaction();
//                         web3.getParity().personalSignAndSendTransaction();
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }

                try {
                    Credentials credentials = WalletUtils.loadCredentials("123321", wallet.getWalletPath());
                    web3j.ethGetTransactionCount(wallet.getEthAddress().getHex(), DefaultBlockParameterName.LATEST).observable().subscribe(ethGetTransactionCount -> {
                        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
                        Log.d("Transaction Count", ethGetTransactionCount.getTransactionCount().toString());

                        final BigInteger gasPrice = new BigInteger("10000000000000");
                        final BigInteger gasLimit = BigInteger.valueOf(0x2710);
                        final BigInteger value = new BigInteger("2441406250");//BigInteger.valueOf(1000);

//                        Transaction.createEtherTransaction()
//                        RawTransaction.createEtherTransaction()
                        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, "0x4a88ba24e71a20e3a3290531433de7ab50f074dd", value);
//                        web3j.ethEstimateGas();

                        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
                        String hexValue = Hex.toHexString(signedMessage);
                        web3j.ethSendRawTransaction(hexValue).observable().subscribe(ethSendTransaction -> {
                            Log.d("TRANSACTION", hexValue);
                            Log.d("TRANSACTION", ethSendTransaction.getError().getMessage());
                        });
                    });

//                    EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(wallet.getEthAddress().getHex(), DefaultBlockParameterName.LATEST).send();
//                    BigInteger nonce = ethGetTransactionCount.getTransactionCount();

//                    Transaction transaction = new Transaction();
//                    web3.getWeb3().ethSendTransaction();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (CipherException e) {
                    e.printStackTrace();
                }

                break;

            case R.id.buttonRegister:
                Router.goActivity(this, Router.REGISTER, REGISTER_REQUEST);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != AuthToken.RESULT_GOT_TOKEN) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        Router.goActivity(this, Router.DASHBOARD);
    }
}
