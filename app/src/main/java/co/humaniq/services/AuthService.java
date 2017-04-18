package co.humaniq.services;

import android.content.ContentResolver;
import android.provider.Settings;

import co.humaniq.App;
import co.humaniq.Client;
import co.humaniq.Preferences;
import co.humaniq.models.AuthToken;
import co.humaniq.models.DummyModel;
import co.humaniq.views.ViewContext;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public class AuthService extends APIService {
    static final String TAG = "AuthService";

    interface RetrofitService {
        @FormUrlEncoded
        @POST("user/register/")
        Call<AuthToken> register(
                @Field("photo") String photoBase64,
                @Field("device_id") String deviceId
        );

        @FormUrlEncoded
        @POST("user/register/")
        Call<AuthToken> register(
                @Field("photo") String photoBase64,
                @Field("device_id") String deviceId,
                @Field("user_id") Integer userId
        );

        @FormUrlEncoded
        @POST("user/login/")
        Call<AuthToken> login(
                @Field("photo") String photoBase64,
                @Field("device_id") String deviceId
        );

        @FormUrlEncoded
        @POST("user/login/")
        Call<AuthToken> login(
                @Field("photo") String photoBase64,
                @Field("device_id") String deviceId,
                @Field("user_id") Integer userId
        );

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
        ContentResolver contentResolver = getContext().getInstance().getContentResolver();
        String deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);

        Call<AuthToken> call = anonymousRetrofitService.register(photoBase64, deviceId);
        APIService.doRequest(this, call, requestCode);
    }

    public void login(final String photoBase64, final int requestCode) {
        ContentResolver contentResolver = getContext().getInstance().getContentResolver();
        String deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);

        Preferences preferences = App.getPreferences(getContext().getInstance());

        Call<AuthToken> call = anonymousRetrofitService.login(photoBase64, deviceId);;
        APIService.doRequest(this, call, requestCode);
    }

    public void logout(final int requestCode) {
        Call<DummyModel> call = authRetrofitService.logout();
        APIService.doRequest(this, call, requestCode);
    }
}
