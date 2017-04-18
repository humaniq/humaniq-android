package co.humaniq.views.dashboard_fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

import co.humaniq.DebugTool;
import co.humaniq.R;
import co.humaniq.models.*;
import co.humaniq.services.AccountService;
import co.humaniq.views.BaseFragment;


public class TransferCoinsFragment extends BaseFragment implements TextWatcher {
    private ProgressDialog progressDialog;
    private final String TAG = "TransferCoinsFragment";
    final static int REQUEST_IS_EXIST = 2001;

    private EditText editTextToWallet;
    private EditText editTextCoins;
    private TextView textTotalInWallet;
    private ImageView imageValidOk;
    private View coinsLayout;
    private View frameInput;
    private boolean formIsValid = false;
    private Wallet wallet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_transfer_coins, container, false);
        wallet = Wallet.getWorkWallet();

        editTextToWallet = (EditText) view.findViewById(R.id.textEditWallet);
        editTextCoins = (EditText) view.findViewById(R.id.textEditCoins);
        textTotalInWallet = (TextView) view.findViewById(R.id.textTotalInWallet);
        imageValidOk = (ImageView) view.findViewById(R.id.textValidOk);
        coinsLayout = view.findViewById(R.id.coinsLayout);
        frameInput = view.findViewById(R.id.inputWalletLayout);

        imageValidOk.setVisibility(View.INVISIBLE);

        attachOnClickView(view, R.id.buttonTransfer);
        attachOnClickView(view, R.id.buttonScanQR);
        attachOnClickView(view, R.id.buttonClear);

        progressDialog = new ProgressDialog(getActivity());
        updateView();

        editTextToWallet.addTextChangedListener(this);
        editTextCoins.addTextChangedListener(this);

        editTextCoins.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                if (isValidTransferCash())
                    new AccountService(this).isExist(editTextToWallet.getText().toString(), REQUEST_IS_EXIST);
                return true;
            }

            return false;
        });

        return view;
    }

    private void setValid(final boolean isValid) {
        imageValidOk.setVisibility(isValid ? View.VISIBLE : View.INVISIBLE);
        formIsValid = isValid;
    }

    public void updateView() {
        if (Wallet.lastBalance != null) {
            final String total = Wallet.lastBalance.getValue().toString() + " HMQ";
            textTotalInWallet.setText(total);
        }

        if (Wallet.getWorkWallet() != null) {//!
            Wallet.getWorkWallet().getBalance(
                    val -> textTotalInWallet.setText(val.getValue().toString() + " HMQ")
            );
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }

    void openQRScanner() {
        IntentIntegrator integrator = new IntentIntegrator(getActivity());
        integrator.initiateScan();
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan a qr code");
        integrator.initiateScan();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonTransfer:
                if (isValidTransferCash())
                    new AccountService(this).isExist(editTextToWallet.getText().toString(), REQUEST_IS_EXIST);
                break;

            case R.id.buttonScanQR:
                openQRScanner();
                break;

            case R.id.buttonClear:
                clearInputWallet(view);
                break;

            default:
                break;
        }
    }

    private void clearInputWallet(View view) {
        editTextToWallet.setText("");
        view.setVisibility(View.GONE);

    }

    private void decorateViewToError(View view, boolean error) {
        if (error) {
            view.setBackgroundResource(R.drawable.border_error_edit_text);
        } else {
            view.setBackgroundResource(R.drawable.rounded_rect_white);
        }
    }

    private void showProgressbar() {
        progressDialog = new ProgressDialog(TransferCoinsFragment.this.getContext());
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideProgressbar() {
        progressDialog.hide();
        progressDialog = null;  // Revoke
    }

    private boolean isValidTransferCash(){
        String balance = textTotalInWallet.getText().toString().split(" ")[0];
        int clientCash = Integer.parseInt(balance);
        int needCash = Integer.parseInt(editTextCoins.getText().toString());
        if (clientCash < needCash){
            DebugTool.showDialog(getContext(), "error", "not enough money");
            return false;
        }

        return true;
    }

    static class TransferTask extends AsyncTask<String, Void, Uint256> {
        private TransferCoinsFragment fragment;

        TransferTask(TransferCoinsFragment fragment) {
            this.fragment = fragment;
        }

        @Override
        protected Uint256 doInBackground(String... params) {
            Wallet wallet = Wallet.getWorkWallet();

            try {
                final String toAddress = params[0];
                final String tokens = params[1];

                final String address = wallet.getAddress();
                final Uint256 value = new Uint256(new BigInteger(tokens));

                return wallet.getTokenContract().balanceOf(new Address(address)).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Uint256 result) {
            fragment.hideProgressbar();

            if (result == null)
                DebugTool.showDialog(fragment.getContext(), "Error", "Can't do transfer");
        }
    };

    private void doTransfer() {
        decorateViewToError(coinsLayout, !coinsIsValid());
        decorateViewToError(frameInput, !walletIsValid());

        if (!formIsValid)
            return;

        showProgressbar();

        final String toAddress = editTextToWallet.getText().toString();
        final String tokens = editTextCoins.getText().toString();

        new TransferTask(this).execute(toAddress, tokens);
    }

    // Test that inString contains acceptable chars which in validChars
    private boolean stringContainChars(final String inString, final String validChars) {
        // i = 2 because of first two symbols always are '0', 'x'
        for (int i = 2; i < inString.length(); ++i){
            final char c1 = inString.charAt(i);
            boolean matched = false;

            for (int j = 0; j < validChars.length(); ++j) {
                final char c2 = validChars.charAt(j);
                if (c1 == c2)
                    matched = true;
            }

            if (!matched)
                return false;
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (result.getContents() != null) {
            editTextToWallet.setText(result.getContents());
        }
    }

    private boolean coinsIsValid() {
        if (editTextCoins.getText().toString().equals(""))
            return false;

        try {
            Integer coins = Integer.parseInt(editTextCoins.getText().toString());

            if (coins < 1)
                return false;
        } catch (NumberFormatException ignored) {
            return false;
        }

        return true;
    }

    private boolean walletIsValid() {
        final String hashPattern = "0x0000000000000000000000000000000000000000";
        final String allowChars = "0123456789abcdefABCDEF";
        final String hash = editTextToWallet.getText().toString().trim();

        if (!hash.startsWith("0x"))
            return false;

        final Wallet wallet = Wallet.getWorkWallet();

        if (wallet == null || wallet.getAddress().equals(hash))
            return false;

        if (hash.length() != hashPattern.length()) {
            return false;
        } else if (!stringContainChars(hash, allowChars)) {
            return false;
        }

        return true;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (coinsIsValid() && walletIsValid()) {
            setValid(true);
        } else {
            setValid(false);
        }

        View clearButton = getView().findViewById(R.id.buttonClear);

        if (editTextToWallet.getText().toString().equals("")) {
            clearButton.setVisibility(View.GONE);
        } else {
            clearButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onApiError(Errors errors, int type, int requestCode) {
        if (requestCode == REQUEST_IS_EXIST) {
            DebugTool.showDialog(getContext(), "Error", "Wallet doesn't exist");
        } else {
            DebugTool.showDialog(getContext(), "Error", "Something went wrong");
        }
    }

    @Override
    public void onApiSuccess(ResultData result, int requestCode) {
        super.onApiSuccess(result, requestCode);

        switch (requestCode) {
            case REQUEST_IS_EXIST:
                Wallet.Existence existence = (Wallet.Existence) result.data();

                if (existence.isExist()) {
                    doTransfer();
                } else {
                    onApiValidationError(null, REQUEST_IS_EXIST);
                }

            default:
                break;
        }
    }
}
