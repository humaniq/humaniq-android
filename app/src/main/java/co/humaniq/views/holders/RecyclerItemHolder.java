package co.humaniq.views.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import co.humaniq.views.ViewContext;


public class RecyclerItemHolder<T> extends RecyclerView.ViewHolder {
    private ViewContext viewContext;

    public RecyclerItemHolder(ViewContext viewContext, View itemView) {
        super(itemView);
        this.viewContext = viewContext;
    }

    public void initViews(T data) {
    }

    protected ViewContext getViewContext() {
        return viewContext;
    }
}
