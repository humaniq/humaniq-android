package co.humaniq.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import co.humaniq.R;
import co.humaniq.models.BaseModel;
import co.humaniq.views.ViewContext;
import co.humaniq.views.holders.LoadingHolder;
import co.humaniq.views.holders.RecyclerItemHolder;

import java.util.ArrayList;
import java.util.zip.Inflater;


public class ItemRecyclerAdapter<T extends BaseModel>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    public interface HolderFactory {
        RecyclerView.ViewHolder createInstance(ViewContext viewContext, View view);
    }

    private ArrayList<T> items;
    private HolderFactory holderFactory;
    private final ViewContext viewContext;
    private final int layout;

    public ItemRecyclerAdapter(final ViewContext viewContext, final int layout, ArrayList<T> items,
                               HolderFactory holderFactory)
    {
        this.items = items;
        this.viewContext = viewContext;
        this.holderFactory = holderFactory;
        this.layout = layout;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == BaseModel.ViewType.LOADING) {
            view = inflater.inflate(R.layout.recycler_item_loading,parent, false);
            return new LoadingHolder(viewContext, view);
        } else {
            view = inflater.inflate(layout, parent, false);
            return holderFactory.createInstance(viewContext, view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        RecyclerItemHolder<T> holder = (RecyclerItemHolder) viewHolder;
        holder.initViews(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getViewType();
    }

    public ViewContext getViewContext() {
        return viewContext;
    }

    public HolderFactory getHolderFactory() {
        return holderFactory;
    }
}
