package co.humaniq.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import co.humaniq.R;
import co.humaniq.models.BaseModel;
import co.humaniq.models.HistoryItem;
import co.humaniq.views.ViewContext;
import co.humaniq.views.holders.HistoryHeaderHolder;
import co.humaniq.views.holders.RecyclerItemHolder;

import java.util.ArrayList;


public class HistoryRecyclerAdapter extends ItemRecyclerAdapter<HistoryItem> {
    public HistoryRecyclerAdapter(ViewContext viewContext,
                                  ArrayList<HistoryItem> items, HolderFactory holderFactory)
    {
        super(viewContext, R.layout.recycler_item_history_recieved, items, holderFactory);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case BaseModel.ViewType.HISTORY_HEADER:
                view = inflater.inflate(R.layout.recycler_item_history_head, parent, false);
                return new HistoryHeaderHolder(getViewContext(), view);

            case BaseModel.ViewType.HISTORY_RECEIVED:
                view = inflater.inflate(R.layout.recycler_item_history_recieved, parent, false);
                return getHolderFactory().createInstance(getViewContext(), view);

            case BaseModel.ViewType.HISTORY_BONUS:
                view = inflater.inflate(R.layout.recycler_item_history_bonus, parent, false);
                return getHolderFactory().createInstance(getViewContext(), view);

            case BaseModel.ViewType.HISTORY_TRANSFERRED:
                view = inflater.inflate(R.layout.recycler_item_history_transfered, parent, false);
                return getHolderFactory().createInstance(getViewContext(), view);

            default:
                return super.onCreateViewHolder(parent, viewType);
        }
    }
}
