package co.humaniq.views.dashboard_fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import co.humaniq.R;
import co.humaniq.Router;
import co.humaniq.models.AuthToken;
import co.humaniq.models.Errors;
import co.humaniq.models.ResultData;
import co.humaniq.views.BaseFragment;


public class TransferCoinsFragment extends BaseFragment {
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_transfer_coins, container, false);

        attachOnClickView(view, R.id.buttonTransfer);

        progressDialog = new ProgressDialog(getActivity());

        return view;
    }

    private void alert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        AlertDialog alertDialog = builder.setTitle("Error");
    }

    @Override
    public void validationError(Errors errors, int requestCode) {
        super.validationError(errors, requestCode);
    }

    @Override
    public void permissionError(Errors errors, int requestCode) {
        super.permissionError(errors, requestCode);
    }

    @Override
    public void authorizationError(Errors errors, int requestCode) {
        super.authorizationError(errors, requestCode);
    }

    @Override
    public void criticalError(Errors errors, int requestCode) {
        super.criticalError(errors, requestCode);
    }

    @Override
    public void connectionError(int requestCode) {
        super.connectionError(requestCode);
    }

    @Override
    public void success(ResultData response, int requestCode) {
        super.success(response, requestCode);
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
                AuthToken.updateInstance(new AuthToken("e55ab33fa614c3b0107495ec4c4e3206d0d0c6be"));
                Router.goActivity(this, Router.DASHBOARD);
                break;

            default:
                break;
        }
    }
}
