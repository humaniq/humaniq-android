package co.humaniq.views.dashboard_fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import co.humaniq.Config;
import co.humaniq.HMQTokenContract;
import co.humaniq.R;
import co.humaniq.Web3;
import co.humaniq.models.AuthToken;
import co.humaniq.models.BaseModel;
import co.humaniq.models.HistoryItem;
import co.humaniq.models.Page;
import co.humaniq.models.ResultData;
import co.humaniq.models.User;
import co.humaniq.models.Wallet;
import co.humaniq.models.WalletHMQ;
import co.humaniq.views.BaseFragment;
import co.humaniq.views.adapters.HistoryRecyclerAdapter;
import co.humaniq.views.adapters.ItemRecyclerAdapter;
import co.humaniq.views.holders.HistoryItemHolder;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class HistoryFragment extends BaseFragment {
    public static final int GET_HISTORY_REQUEST = 1001;
    public static final int GET_MY_USER_REQUEST = 1002;
    public static final String TAG = "HistoryFragment";

    private ArrayList<HistoryItem> items = new ArrayList<>();
    private LinearLayoutManager recyclerLayoutManager;
    private ItemRecyclerAdapter<HistoryItem> recyclerAdapter;
    private RecyclerView recyclerView;
    private Integer nextPage = 1;
    private boolean loaded = false;
    static public boolean dataSetChanged = true;
    private EthFilter transactionFilter;
    private Event transferEvent;

    EventValues extractEventParameters(
            Event event, org.web3j.protocol.core.methods.response.Log log) {

        List<String> topics = log.getTopics();
        String encodedEventSignature = EventEncoder.encode(event);
        if (!topics.get(0).equals(encodedEventSignature)) {
            return null;
        }

        List<Type> indexedValues = new ArrayList<Type>();
        List<Type> nonIndexedValues = FunctionReturnDecoder.decode(
                log.getData(), event.getNonIndexedParameters());

        List<TypeReference<Type>> indexedParameters = event.getIndexedParameters();
        for (int i = 0; i < indexedParameters.size(); i++) {
            Type value = FunctionReturnDecoder.decodeIndexedValue(
                    topics.get(i + 1), indexedParameters.get(i));
            indexedValues.add(value);
        }
        return new EventValues(indexedValues, nonIndexedValues);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = (RecyclerView) view;

        transferEvent = new Event("Transfer",
                Arrays.asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Collections.singletonList(new TypeReference<Uint256>() {}));
        transactionFilter = new EthFilter(
                DefaultBlockParameterName.EARLIEST,
                DefaultBlockParameterName.LATEST,
                Config.HMQ_TOKEN_CONTRACT_ADDRESS
        );
        transactionFilter.addSingleTopic(EventEncoder.encode(transferEvent));

        initRecycler();
        load();

        return view;
    }

    private List<HistoryItem> getHistory() {
        Web3 web3 = Web3.getInstance();
        Web3j web3j = web3.getWeb3();

        try {
            EthLog log = web3j.ethGetLogs(transactionFilter).sendAsync().get();
            List<HistoryItem> result = new ArrayList<>();
            WalletHMQ wallet = WalletHMQ.getWorkWallet();

            for (EthLog.LogResult logRes: log.getLogs()) {
                EthLog.LogObject logObj = (EthLog.LogObject) logRes.get();
                Log.d(TAG, logRes.get().toString());

                HMQTokenContract.TransferEventResponse ev = new HMQTokenContract.TransferEventResponse();
                EventValues eventValues = extractEventParameters(transferEvent, logObj);

                ev.from = (Address)eventValues.getIndexedValues().get(0);
                ev.to = (Address)eventValues.getIndexedValues().get(1);
                ev.value = (Uint256)eventValues.getNonIndexedValues().get(0);

                final int viewType = ev.from.toString().equals(wallet.getAddress()) ?
                        BaseModel.ViewType.HISTORY_TRANSFERRED :
                        BaseModel.ViewType.HISTORY_RECEIVED;


                result.add(new HistoryItem(viewType, ev.value, "HMQ", false, "",
                        ev.from.toString(), ev.to.toString()));
            }

            return result;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void load() {
//        if (!loaded || dataSetChanged) {
//        dataSetChanged = false;
//        loaded = true;
//        }
        new AsyncTask<Void, Void, List<HistoryItem>>() {
            @Override
            protected List<HistoryItem> doInBackground(Void... params) {
                return getHistory();
            }

            @Override
            protected void onPostExecute(List<HistoryItem> historyItems) {
                if (historyItems == null)
                    return;

                Collections.reverse(historyItems);

                items.clear();
                items.add(new HistoryItem(BaseModel.ViewType.HISTORY_HEADER));
                items.addAll(historyItems);

                recyclerAdapter.notifyDataSetChanged();
            }
        }.execute();
//
//            items.clear();
//            items.add(new HistoryItem(BaseModel.ViewType.HISTORY_HEADER));
//            items.addAll(getHistory());
//
//            recyclerAdapter.notifyDataSetChanged();
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
