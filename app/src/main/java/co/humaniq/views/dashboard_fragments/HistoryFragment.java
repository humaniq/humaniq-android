package co.humaniq.views.dashboard_fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import co.humaniq.R;
import co.humaniq.models.HistoryItem;
import co.humaniq.views.BaseFragment;
import co.humaniq.views.adapters.ItemRecyclerAdapter;
import co.humaniq.views.holders.HistoryItemHolder;

import java.util.ArrayList;


public class HistoryFragment extends BaseFragment {
    private ArrayList<HistoryItem> items = new ArrayList<>();
    private LinearLayoutManager recyclerLayoutManager;
    private ItemRecyclerAdapter<HistoryItem> recyclerAdapter;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        fakeItems();
        initRecycler();
        return view;
    }

    private void fakeItems() {
        for (int i = 0; i < 50; ++i) {
            items.add(new HistoryItem());
        }
    }

    private void initRecycler() {
        recyclerLayoutManager = new LinearLayoutManager(getActivityInstance());
        recyclerAdapter = new ItemRecyclerAdapter<>(getActivityInstance(),
                R.layout.recycler_item_history_recieved,
                items,
                HistoryItemHolder::new);

        recyclerView.setLayoutManager(recyclerLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }
}
