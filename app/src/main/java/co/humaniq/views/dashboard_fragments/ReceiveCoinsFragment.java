package co.humaniq.views.dashboard_fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import co.humaniq.DebugTool;
import co.humaniq.ImageTool;
import co.humaniq.R;
import co.humaniq.Router;
import co.humaniq.models.Wallet;
import co.humaniq.views.BaseFragment;


public class ReceiveCoinsFragment extends BaseFragment {
    private TextView textMyWallet;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate (R.menu.default_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_receive_coins, container, false);
        final Wallet wallet = Wallet.getWorkWallet();

        textMyWallet = (TextView) view.findViewById(R.id.textMyWallet);
        textMyWallet.setText(wallet.getAddress());

        final ImageView qrCodeImageView = (ImageView) view.findViewById(R.id.qrCodeImageView);
        ImageTool.loadFromUrlToImageView(this, wallet.getQRCodeImageURL(), qrCodeImageView);

        attachOnClickView(view, R.id.buttonCopyWallet);
        attachOnClickView(view, R.id.buttonShareWallet);
        attachOnClickView(view, R.id.buttonHelp);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonCopyWallet:
                copyWallet();
                break;

            case R.id.buttonShareWallet:
                shareWallet();
                break;

            case  R.id.buttonHelp:
                showHelp();
                break;
        }
    }

    private void showHelp() {
        Bundle bundle = new Bundle();
        bundle.putInt("gifId", R.drawable.humaniq_2_medium);
        Router.setBundle(bundle);
        Router.goActivity(this, Router.VIDEO);
    }

    private void shareWallet() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, textMyWallet.getText().toString());
        startActivity(sharingIntent);
    }

    private void copyWallet() {
        ClipboardManager clipboard = (ClipboardManager) getActivityInstance()
                .getSystemService(Context.CLIPBOARD_SERVICE);

        ClipData clip = ClipData.newPlainText("Wallet", textMyWallet.getText().toString());
        clipboard.setPrimaryClip(clip);

        DebugTool.showDialog(getActivity(), "Success", "Copied");
    }
}
