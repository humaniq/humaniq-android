package co.humaniq.views.dashboard_fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import co.humaniq.R;
import co.humaniq.models.BaseModel;
import co.humaniq.models.HistoryItem;
import co.humaniq.models.Page;
import co.humaniq.models.ResultData;
import co.humaniq.services.FinanceService;
import co.humaniq.views.BaseFragment;
import co.humaniq.views.adapters.HistoryRecyclerAdapter;
import co.humaniq.views.adapters.ItemRecyclerAdapter;
import co.humaniq.views.holders.HistoryItemHolder;

import java.util.ArrayList;


public class HistoryFragment extends BaseFragment {
    private ArrayList<HistoryItem> items = new ArrayList<>();
    private LinearLayoutManager recyclerLayoutManager;
    private ItemRecyclerAdapter<HistoryItem> recyclerAdapter;
    private RecyclerView recyclerView;
    private FinanceService service;
    private Integer nextPage = 1;
    private boolean loaded = false;
    static public boolean dataSetChanged = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = (RecyclerView) view;

        initRecycler();
        load();

        return view;
    }

    private void load() {
        if (!loaded || dataSetChanged) {
            items.clear();
            items.add(new HistoryItem(BaseModel.ViewType.HISTORY_HEADER));
            service = new FinanceService(this);
            service.getHistory(GENERAL_REQUEST);
            dataSetChanged = false;
            loaded = true;
        }
    }

    private void initRecycler() {
        recyclerLayoutManager = new LinearLayoutManager(getActivityInstance());
        recyclerAdapter = new HistoryRecyclerAdapter(getActivityInstance(),
                R.layout.recycler_item_history_recieved,
                items,
                HistoryItemHolder::new);

        recyclerView.setLayoutManager(recyclerLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    public void success(ResultData result, int requestCode) {
        Page<HistoryItem> page = (Page<HistoryItem>) result.data();
        items.addAll(page.getResults());
        nextPage = page.getNextPage();
        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }
}
