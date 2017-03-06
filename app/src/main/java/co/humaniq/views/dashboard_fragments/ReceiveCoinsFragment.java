package co.humaniq.views.dashboard_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

        return view;
    }
}
