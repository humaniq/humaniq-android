package co.humaniq.views.dashboard_fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.web3j.abi.datatypes.generated.Uint256;

import co.humaniq.R;
import co.humaniq.models.*;
import co.humaniq.services.HistoryService;
import co.humaniq.views.BaseFragment;
import co.humaniq.views.adapters.HistoryRecyclerAdapter;
import co.humaniq.views.adapters.ItemRecyclerAdapter;
import co.humaniq.views.holders.HistoryItemHolder;

import java.util.*;


public class HistoryFragment extends BaseFragment {
    public static final int GET_HISTORY_REQUEST = 2001;
    public static final String TAG = "HistoryFragment";

    private ArrayList<HistoryItem> items = new ArrayList<>();
    private LinearLayoutManager recyclerLayoutManager;
    private ItemRecyclerAdapter<HistoryItem> recyclerAdapter;
    private RecyclerView recyclerView;
    private Integer nextPage = 1;
    private boolean loaded = false;
    static public boolean dataSetChanged = true;
    private HistoryService historyService;
    private WalletHMQ wallet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = (RecyclerView) view;

        wallet = WalletHMQ.getWorkWallet();
        historyService = new HistoryService(this);

        initRecycler();
        load();

        return view;
    }

    private void load() {
//        if (!loaded || dataSetChanged) {
        dataSetChanged = false;
        loaded = true;
//        }
        historyService.getHistory(wallet.getAddress(), GET_HISTORY_REQUEST);
    }

    private void initRecycler() {

        recyclerLayoutManager = new LinearLayoutManager(getActivityInstance());
        recyclerAdapter = new HistoryRecyclerAdapter(getActivityInstance(),
                items,
                HistoryItemHolder::new);

        recyclerView.setLayoutManager(recyclerLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }

    void fillHistoryViews(Page<HistoryItem> historyPage) {
        items.clear();
        items.add(new HistoryItem(BaseModel.ViewType.HISTORY_HEADER));
        items.addAll(historyPage.getResults());
        nextPage = historyPage.getNextPage();
        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onApiSuccess(ResultData result, int requestCode) {
        switch (requestCode) {
            case GET_HISTORY_REQUEST:
                fillHistoryViews((Page<HistoryItem>) result.data());
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }
}
