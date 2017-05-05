package co.humaniq.views;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import co.humaniq.HMQTokenContract;
import co.humaniq.R;
import co.humaniq.Router;
import co.humaniq.Web3;
import co.humaniq.models.AuthToken;
import co.humaniq.models.WalletHMQ;

import org.spongycastle.util.encoders.Hex;
import org.web3j.abi.datatypes.Address;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.RawTransaction;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

import static org.web3j.tx.Contract.GAS_LIMIT;
import static org.web3j.tx.ManagedTransaction.GAS_PRICE;


public class GreeterActivity extends BaseActivity {
    private final static int LOGIN_REQUEST = 1000;
    private final static int REGISTER_REQUEST = 1001;
    private final static int PIN_CODE_REQUEST = 1002;

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
                Router.goActivity(this, Router.PIN_CODE, PIN_CODE_REQUEST);
                RawTransaction transaction;
//                transaction.
//                WalletHMQ wallet = WalletHMQ.getOrCreateWallet(this, "123321");

//                Web3 web3 = Web3.getInstance();
//                Web3j web3j = web3.getWeb3();

//                try {
//                    Credentials credentials = WalletUtils.loadCredentials("123321", wallet.getWalletPath());
//                    HMQTokenContract contract = new HMQTokenContract(Web3.contractAddress, web3j, credentials, GAS_PRICE, GAS_LIMIT);
//
//                    Log.d("WALLET BALANCE", contract.balanceOf(new Address("0x9ddbd6be2d3a88f6877b562868385569e5d66fe5")).get().getValue().toString());
//
//                    Log.d("WALLET", credentials.getAddress());
//                    Log.d("WALLET", wallet.getWalletPath());

//                    web3j.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST).observable().subscribe(ethGetTransactionCount -> {
//                        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
//                        Log.d("Transaction Count", ethGetTransactionCount.getTransactionCount().toString());
//
//                        final BigInteger gasPrice = new BigInteger("18000000000");
//                        final BigInteger gasLimit = new BigInteger("19000000000");
//                        final BigInteger value = Convert.toWei("10.0", Convert.Unit.ETHER).toBigInteger();
//
//                        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, "0x9ddbd6be2d3a88f6877b562868385569e5d66fe5", value);
//
//                        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
//                        String hexValue = Hex.toHexString(signedMessage);
//                        web3j.ethSendRawTransaction("0x"+hexValue).observable().subscribe(ethSendTransaction -> {
//                            Log.d("TRANSACTION", hexValue);
//                            Log.d("TRANSACTION", Integer.toString(ethSendTransaction.getError().getCode()));
//                            Log.d("TRANSACTION", ethSendTransaction.getError().getMessage());
//                            Log.d("TRANSACTION", ethSendTransaction.getResult());
//                        });
//                    });
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (CipherException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }

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
        if (resultCode != RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (requestCode == PIN_CODE_REQUEST)
            Router.goActivity(this, Router.DASHBOARD);
    }
}
