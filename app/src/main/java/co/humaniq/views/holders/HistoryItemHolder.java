package co.humaniq.views.holders;

import android.view.View;
import android.widget.TextView;
import co.humaniq.R;
import co.humaniq.models.HistoryItem;
import co.humaniq.views.ViewContext;


public class HistoryItemHolder extends RecyclerItemHolder<HistoryItem> {
    private final TextView textViewDate;
    private final TextView textViewCoins;

    public HistoryItemHolder(ViewContext viewContext, View itemView) {
        super(viewContext, itemView);

        textViewDate = (TextView) itemView.findViewById(R.id.textViewDate);
        textViewCoins = (TextView) itemView.findViewById(R.id.textViewCoins);
    }

    @Override
    public void initViews(HistoryItem data) {
        final String format = getViewContext().getInstance().getString(R.string.total_hmq);
        final String total = String.format(format, data.getCoins());

        textViewCoins.setText(total);
        textViewDate.setText(data.getDate());

        super.initViews(data);
    }
}
