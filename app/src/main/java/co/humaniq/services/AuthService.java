package co.humaniq.services;

import co.humaniq.Client;
import co.humaniq.models.AuthToken;
import co.humaniq.models.DummyModel;
import co.humaniq.views.ViewContext;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public class AuthService extends APIService<AuthToken> {
    static final String TAG = "AuthService";

    interface RetrofitService {
        @FormUrlEncoded
        @POST("user/register/")
        Call<AuthToken> register(@Field("photo") String photoBase64);

        @FormUrlEncoded
        @POST("user/login/")
        Call<AuthToken> login(@Field("photo") String photoBase64);

        @POST("user/logout/")
        Call<DummyModel> logout();
    }

    private RetrofitService anonymousRetrofitService;
    private RetrofitService authRetrofitService;

    public AuthService(ViewContext context) {
        super(context);

        anonymousRetrofitService = Client.getAnonymousRetrofitInstance()
                .create(RetrofitService.class);
        authRetrofitService = Client.getAnonymousRetrofitInstance()
                .create(RetrofitService.class);
    }

    public void register(final String photoBase64, final int requestCode) {
        Call<AuthToken> call = anonymousRetrofitService.register(photoBase64);
        requestCall(call, requestCode);
    }

    public void login(final String photoBase64, final int requestCode) {
        Call<AuthToken> call = anonymousRetrofitService.login(photoBase64);
        requestCall(call, requestCode);
    }

    public void logout(final int requestCode) {
        Call<DummyModel> call = authRetrofitService.logout();
        request(call, requestCode);
    }
}
