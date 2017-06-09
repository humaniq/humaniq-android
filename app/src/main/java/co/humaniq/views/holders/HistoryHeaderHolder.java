package co.humaniq.views.holders;

import android.view.View;
import android.widget.TextView;
import co.humaniq.R;
import co.humaniq.models.BaseModel;
import co.humaniq.models.Wallet;
import co.humaniq.views.ViewContext;


public class HistoryHeaderHolder extends RecyclerItemHolder<BaseModel> {
    final private TextView textTotalInWallet;

    public HistoryHeaderHolder(ViewContext viewContext, View itemView) {
        super(viewContext, itemView);

        textTotalInWallet = (TextView) itemView.findViewById(R.id.textTotalInWallet);
    }

    @Override
    public void initViews(BaseModel data) {
        final Wallet wallet = Wallet.getWorkWallet();
        if (wallet == null)
            return;

        if (Wallet.lastBalance != null) {
            final String total = Wallet.lastBalance.getValue().toString() + " HMQ";
            textTotalInWallet.setText(total);
        }

        wallet.getBalance(balance -> {
            final String total = balance.getValue().toString() + " HMQ";
            textTotalInWallet.setText(total);
        });
    }
}
