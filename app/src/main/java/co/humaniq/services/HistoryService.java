package co.humaniq.services;

import co.humaniq.Client;
import co.humaniq.models.HistoryItem;
import co.humaniq.models.Page;
import co.humaniq.views.ViewContext;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public class HistoryService extends APIService {
    static final String TAG = "HistoryService";

    interface RetrofitService {
        @GET("finance/history/")
        Call<Page<HistoryItem>> history(@Query("address") String address);
    }

    private RetrofitService retrofitService;

    public HistoryService(ViewContext context) {
        super(context);
        retrofitService = Client.getRetrofitInstance()
                .create(RetrofitService.class);
    }

    public void getHistory(final String address, final int requestCode) {
        Call<Page<HistoryItem>> call = retrofitService.history(address);
        APIService.doRequest(this, call, requestCode);
    }
}
