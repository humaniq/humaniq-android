package co.humaniq.views.dashboard_fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import co.humaniq.R;
import co.humaniq.Router;
import co.humaniq.models.*;
import co.humaniq.services.FinanceService;
import co.humaniq.views.BaseFragment;
import org.w3c.dom.Text;


public class TransferCoinsFragment extends BaseFragment {
    private ProgressDialog progressDialog;
    private FinanceService service;

    private EditText editTextToWallet;
    private EditText editTextCoins;
    private TextView textTotalInWallet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_transfer_coins, container, false);

        editTextToWallet = (EditText) view.findViewById(R.id.textEditWallet);
        editTextCoins = (EditText) view.findViewById(R.id.textEditCoins);
        textTotalInWallet = (TextView) view.findViewById(R.id.textTotalInWallet);

        attachOnClickView(view, R.id.buttonTransfer);

        progressDialog = new ProgressDialog(getActivity());
        service = new FinanceService(this);
        updateView();

        return view;
    }

    private void alert(final String title, final String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog alertDialog = builder.setTitle(title).setMessage(message).create();
        alertDialog.show();
    }

    public void updateView() {
        final Wallet wallet = AuthToken.getInstance().getUser().getWallet();
        final String string = getActivityInstance().getString(R.string.total_hmq);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonTransfer:
                service.transfer(
                        editTextToWallet.getText().toString(),
                        editTextCoins.getText().toString(),
                        GENERAL_REQUEST
                );
                break;

            default:
                break;
        }
    }
}
