package co.humaniq.views.holders;

import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import co.humaniq.R;
import co.humaniq.models.AuthToken;
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
        final Wallet wallet = AuthToken.getInstance().getUser().getWallet();
        final String string = getViewContext().getActivityInstance().getString(R.string.total_hmq);
        final String total = String.format(string, wallet.getBalance());

        textTotalInWallet.setText(total);
    }
}
