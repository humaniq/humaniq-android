package co.humaniq.services;

import co.humaniq.Client;
import co.humaniq.models.WalletInfo;
import co.humaniq.models.WalletMeta;
import co.humaniq.views.ViewContext;
import retrofit2.Call;
import retrofit2.http.*;


public class AccountService extends APIService {
    static final String TAG = "AccountService";

    interface RetrofitService {
        @FormUrlEncoded
        @POST("account/get_salt/")
        Call<WalletInfo> getSalt(
                @Field("photo") String photoBase64,
                @Field("public_address") String publicAddress
        );

        @GET("account/get_meta/")
        Call<WalletMeta> getMeta(
                @Query("device_id") String deviceId
        );

        @FormUrlEncoded
        @POST("account/generate_salt/?final=0")
        Call<WalletInfo> generateSalt(
                @Field("device_id") String deviceId,
                @Field("photo") String photoBase64
        );

        @FormUrlEncoded
        @POST("account/finish_registration/")
        Call<WalletInfo> finishRegistration(
                @Field("id") int id,
                @Field("public_address") String publicAddress,
                @Field("key_file_path") String keyFilePath
        );
    }

    private RetrofitService retrofitService;

    public AccountService(ViewContext context) {
        super(context);
        retrofitService = Client.getRetrofitInstance().create(RetrofitService.class);
    }

    public void getSalt(final String photoBase64,
                        final String publicAddress,
                        final int requestCode)
    {
        Call<WalletInfo> call = retrofitService.getSalt(photoBase64, publicAddress);
        APIService.doRequest(this, call, requestCode);
    }

    public void getMeta(final String deviceId,
                        final int requestCode)
    {
        Call<WalletMeta> call = retrofitService.getMeta(deviceId);
        APIService.doRequest(this, call, requestCode);
    }

    public void generateSalt(final String deviceId, final String photoBase64, final int requestCode) {
        Call<WalletInfo> call = retrofitService.generateSalt(deviceId, photoBase64);
        APIService.doRequest(this, call, requestCode);
    }

    public void finishRegistration(final int id, final String publicAddress,
                                   final String keyFilePath, final int requestCode)
    {
        Call<WalletInfo> call = retrofitService.finishRegistration(id, publicAddress, keyFilePath);
        APIService.doRequest(this, call, requestCode);
    }
}
