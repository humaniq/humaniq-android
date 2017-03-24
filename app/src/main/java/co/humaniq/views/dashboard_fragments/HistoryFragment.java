package co.humaniq.views.dashboard_fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import co.humaniq.R;
import co.humaniq.models.AuthToken;
import co.humaniq.models.BaseModel;
import co.humaniq.models.HistoryItem;
import co.humaniq.models.Page;
import co.humaniq.models.ResultData;
import co.humaniq.models.User;
import co.humaniq.services.FinanceService;
import co.humaniq.services.UserService;
import co.humaniq.views.BaseFragment;
import co.humaniq.views.adapters.HistoryRecyclerAdapter;
import co.humaniq.views.adapters.ItemRecyclerAdapter;
import co.humaniq.views.holders.HistoryItemHolder;

import java.util.ArrayList;


public class HistoryFragment extends BaseFragment {
    public static final int GET_HISTORY_REQUEST = 1001;
    public static final int GET_MY_USER_REQUEST = 1002;

    private ArrayList<HistoryItem> items = new ArrayList<>();
    private LinearLayoutManager recyclerLayoutManager;
    private ItemRecyclerAdapter<HistoryItem> recyclerAdapter;
    private RecyclerView recyclerView;
    private FinanceService financeService;
    private UserService userService;
    private Integer nextPage = 1;
    private boolean loaded = false;
    static public boolean dataSetChanged = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = (RecyclerView) view;

        financeService = new FinanceService(this);
        userService = new UserService(this);

        initRecycler();
        load();

        return view;
    }

    private void load() {
//        if (!loaded || dataSetChanged) {
        userService.getMy(GET_MY_USER_REQUEST);

        dataSetChanged = false;
        loaded = true;
//        }
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

    void fillHistoryViews(Page<HistoryItem> historyPage) {
        items.clear();
        items.add(new HistoryItem(BaseModel.ViewType.HISTORY_HEADER));
        items.addAll(historyPage.getResults());
        nextPage = historyPage.getNextPage();
        recyclerAdapter.notifyDataSetChanged();
    }

    void fillMyUsersViews(User user) {
        AuthToken.getInstance().setUser(user);
//        recyclerAdapter.notifyDataSetChanged();
        financeService.getHistory(GET_HISTORY_REQUEST);
    }

    @Override
    public void onApiSuccess(ResultData result, int requestCode) {
        switch (requestCode) {
            case GET_HISTORY_REQUEST:
                fillHistoryViews((Page<HistoryItem>) result.data());
                break;

            case GET_MY_USER_REQUEST:
                fillMyUsersViews((User) result.data());
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }
}
