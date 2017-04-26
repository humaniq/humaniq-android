package co.humaniq.services;

import co.humaniq.Client;
import co.humaniq.models.WalletInfo;
import co.humaniq.views.ViewContext;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public class AccountService extends APIService {
    static final String TAG = "AccountService";

    interface RetrofitService {
        @FormUrlEncoded
        @POST("account/get_salt/")
        Call<WalletInfo> getSalt(
                @Field("photo") String photoBase64,
                @Field("public_address") String publicAddress
        );

        @FormUrlEncoded
        @POST("account/generate_salt/")
        Call<WalletInfo> generateSalt(
                @Field("photo") String photoBase64,
                @Field("public_address") String publicAddress
        );
    }

    private RetrofitService retrofitService;

    public AccountService(ViewContext context) {
        super(context);
        retrofitService = Client.getAnonymousRetrofitInstance().create(RetrofitService.class);
    }

    public void getSalt(final String photoBase64, final String publicAddress,
                        final int requestCode)
    {
        Call<WalletInfo> call = retrofitService.getSalt(photoBase64, publicAddress);
        APIService.doRequest(this, call, requestCode);
    }

    public void generateSalt(final String photoBase64, final String publicAddress,
                             final int requestCode)
    {
        Call<WalletInfo> call = retrofitService.generateSalt(photoBase64, publicAddress);
        APIService.doRequest(this, call, requestCode);
    }
}
