package co.humaniq.views.dashboard_fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import co.humaniq.R;
import co.humaniq.models.*;
import co.humaniq.services.FinanceService;
import co.humaniq.views.BaseFragment;
import co.humaniq.views.DashboardActivity;


public class TransferCoinsFragment extends BaseFragment implements TextWatcher {
    private ProgressDialog progressDialog;
    private FinanceService service;

    private EditText editTextToWallet;
    private EditText editTextCoins;
    private TextView textTotalInWallet;
    private ImageView imageValidOk;
    private View coinsLayout;
    private boolean formIsValid = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_transfer_coins, container, false);

        editTextToWallet = (EditText) view.findViewById(R.id.textEditWallet);
        editTextCoins = (EditText) view.findViewById(R.id.textEditCoins);
        textTotalInWallet = (TextView) view.findViewById(R.id.textTotalInWallet);
        imageValidOk = (ImageView) view.findViewById(R.id.textValidOk);
        coinsLayout = view.findViewById(R.id.coinsLayout);

        imageValidOk.setVisibility(View.INVISIBLE);

        attachOnClickView(view, R.id.buttonTransfer);
        attachOnClickView(view, R.id.buttonScanQR);

        progressDialog = new ProgressDialog(getActivity());
        service = new FinanceService(this);
        updateView();

        editTextToWallet.addTextChangedListener(this);
        editTextCoins.addTextChangedListener(this);

        return view;
    }

    private void setValid(final boolean isValid) {
        imageValidOk.setVisibility(isValid ? View.VISIBLE : View.INVISIBLE);
        formIsValid = isValid;
    }

    private void alert(final String title, final String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog alertDialog = builder.setTitle(title).setMessage(message).create();
        alertDialog.show();
    }

    public void updateView() {
        final Wallet wallet = AuthToken.getInstance().getUser().getWallet();
        final String string = getActivityInstance().getString(R.string.total_b_hmq);
        final String total = String.format(string, wallet.getBalance());

        textTotalInWallet.setText(total);
    }

    @Override
    public void validationError(Errors errors, int requestCode) {
        alert("Error", errors.toString());
    }

    @Override
    public void permissionError(Errors errors, int requestCode) {
        alert("Error", "Permission denied");
    }

    @Override
    public void authorizationError(Errors errors, int requestCode) {
        alert("Error", "Authorization error");
    }

    @Override
    public void criticalError(Errors errors, int requestCode) {
        alert("Error", "Critical error");
    }

    @Override
    public void connectionError(int requestCode) {
        alert("Error", "Connection error");
    }

    @Override
    public void success(ResultData result, int requestCode) {
        final Wallet wallet = (Wallet) result.data();
        final User user = AuthToken.getInstance().getUser();
        user.setWallet(wallet);
        updateView();

        HistoryFragment.dataSetChanged = true;
        alert("Success", "Transferred");
    }

    @Override
    public void showProgressbar(int requestCode) {
        progressDialog.show();
    }

    @Override
    public void hideProgressbar(int requestCode) {
        progressDialog.hide();
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
                doTransfer();
                break;

            case R.id.buttonScanQR:
                openQRScanner();
                break;

            default:
                break;
        }
    }

    private void decorateViewToError(View view, boolean error) {
        if (error) {
            view.setBackgroundResource(R.drawable.border_error_edit_text);
        } else {
            view.setBackgroundResource(R.drawable.rounded_rect_white);
        }
    }

    private void doTransfer() {
        decorateViewToError(coinsLayout, !coinsIsValid());
        decorateViewToError(editTextToWallet, !walletIsValid());

        if (formIsValid) {
            service.transfer(
                    editTextToWallet.getText().toString(),
                    editTextCoins.getText().toString(),
                    GENERAL_REQUEST
            );
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    private boolean stringContainChars(final String inString, final String validChars) {
        for (int i = 0; i < inString.length(); ++i){
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

        if (result.getContents() == null) {
            Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
        } else {
            editTextToWallet.setText(result.getContents());
        }
    }

    private boolean coinsIsValid() {
        try {
            Float coins = Float.parseFloat(editTextCoins.getText().toString());

            if (coins < 1.0f)
                return false;
        } catch (NumberFormatException ignored) {
            return false;
        }

        return true;
    }

    private boolean walletIsValid() {
        final String hashPattern = "00000000-0000-0000-0000-000000000000";
        final String allowChars = "0123456789abcdefABCDEF-";
        final String hash = editTextToWallet.getText().toString().trim();

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
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
