package co.humaniq.views.dashboard_fragments;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import co.humaniq.ImageTool;
import co.humaniq.R;
import co.humaniq.models.AuthToken;
import co.humaniq.models.User;
import co.humaniq.models.Wallet;
import co.humaniq.views.BaseFragment;


public class ReceiveCoinsFragment extends BaseFragment {
    private TextView textMyWallet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_receive_coins, container, false);

        final User user = AuthToken.getInstance().getUser();
        final Wallet wallet = user.getWallet();

        textMyWallet = (TextView) view.findViewById(R.id.textMyWallet);
        textMyWallet.setText(wallet.getHash());

        final ImageView qrCodeImageView = (ImageView) view.findViewById(R.id.qrCodeImageView);
        ImageTool.loadFromUrlToImageView(this, "http://13.75.91.36/media/qr_codes/424d590c-84c7-492c-866a-176aa947ab5c.png", qrCodeImageView);

        attachOnClickView(view, R.id.buttonCopyWallet);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonCopyWallet:
                copyWallet();
                break;
        }
    }

    private void copyWallet() {
        ClipboardManager clipboard = (ClipboardManager) getActivityInstance()
                .getSystemService(Context.CLIPBOARD_SERVICE);

        ClipData clip = ClipData.newPlainText("Wallet", textMyWallet.getText().toString());
        clipboard.setPrimaryClip(clip);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog alertDialog = builder
                .setTitle("Success")
                .setMessage("Copied")
                .setPositiveButton("Ok", (dialog, which) -> {})
                .create();

        alertDialog.show();
    }
}
