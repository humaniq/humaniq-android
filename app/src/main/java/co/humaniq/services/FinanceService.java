package co.humaniq.services;

import co.humaniq.Client;
import co.humaniq.models.HistoryItem;
import co.humaniq.models.Page;
import co.humaniq.models.Wallet;
import co.humaniq.views.ViewContext;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;


public class FinanceService extends APIService {
    static final String TAG = "FinanceService";

    interface RetrofitService {
        @GET("finance/history/")
        Call<Page<HistoryItem>> history();

        @FormUrlEncoded
        @POST("finance/transfer/")
        Call<Wallet> transfer(
                @Field("from_wallet") String fromWallet,
                @Field("to_wallet") String toWallet,
                @Field("coins") String coins
        );

        @FormUrlEncoded
        @POST("finance/transfer/")
        Call<Wallet> transfer(
                @Field("to_wallet") String toWallet,
                @Field("coins") String coins
        );
    }

    private RetrofitService retrofitService;

    public FinanceService(ViewContext context) {
        super(context);
        retrofitService = Client.getAuthRetrofitInstance()
                .create(RetrofitService.class);
    }

    public void getHistory(final int requestCode) {
        Call<Page<HistoryItem>> call = retrofitService.history();
        APIService.doRequest(this, call, requestCode);
    }

    public void transfer(final String fromWallet, final String toWallet,
                         final String coins, final int requestCode)
    {
        Call<Wallet> call = retrofitService.transfer(fromWallet, toWallet, coins);
        APIService.doRequest(this, call, requestCode);
    }

    public void transfer(final String toWallet, final String coins,
                         final int requestCode)
    {
        Call<Wallet> call = retrofitService.transfer(toWallet, coins);
        APIService.doRequest(this, call, requestCode);
    }
}
