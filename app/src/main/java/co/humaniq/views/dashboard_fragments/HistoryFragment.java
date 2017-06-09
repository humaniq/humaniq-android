package co.humaniq.views.dashboard_fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

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
    public static final int UPDATE_HISTORY_REQUEST = 2002;
    public static final String TAG = "HistoryFragment";

    private ArrayList<HistoryItem> items = new ArrayList<>();
    private ItemRecyclerAdapter<HistoryItem> recyclerAdapter;
    private RecyclerView recyclerView;
    private Integer nextPage = 1;
    static public boolean dataSetChanged = true;
    private HistoryService historyService;
    private Wallet wallet;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate (R.menu.default_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = (RecyclerView) view;

        wallet = Wallet.getWorkWallet();
        historyService = new HistoryService(this);

        initRecycler();
        updateHistoryRequest();

        return view;
    }

    private void updateHistoryRequest() {
        historyService.updateHistory(wallet.getAddress(), UPDATE_HISTORY_REQUEST);
    }

    private void retrieveHistoryRequest() {
        historyService.getHistory(wallet.getAddress(), GET_HISTORY_REQUEST);
    }

    private void initRecycler() {

        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(getActivityInstance());
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

            case UPDATE_HISTORY_REQUEST:
//                retrieveHistoryRequest();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateHistoryRequest();
    }

    @Override
    public void onMessageReceived(String message) {
        switch (message) {
            case "update":
                updateHistoryRequest();
                break;

            case "retrieveHistory":
                retrieveHistoryRequest();
                break;

            default:
                break;
        }
    }
}
