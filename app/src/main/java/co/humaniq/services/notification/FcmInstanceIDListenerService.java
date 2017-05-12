package co.humaniq.services.notification;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import co.humaniq.App;
import co.humaniq.Client;
import co.humaniq.Preferences;
import co.humaniq.models.DummyModel;
import co.humaniq.models.WalletHMQ;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public class FcmInstanceIDListenerService extends FirebaseInstanceIdService {
    private static final String TAG = "FcmInstanceIDLS";

    interface RetrofitService {
        @FormUrlEncoded
        @POST("account/update_fcm_token/")
        Call<DummyModel> updateFcmToken(
                @Field("address") String address,
                @Field("token") String token
        );
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        WalletHMQ wallet = null;

        while (wallet == null) {
            wallet = WalletHMQ.getWorkWallet();
        }

        final String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, token);

        Preferences preferences = App.getPreferences(this);
        preferences.setFCMToken(token);

        RetrofitService retrofitService = Client.getRetrofitInstance()
                .create(RetrofitService.class);

        Call<DummyModel> call = retrofitService.updateFcmToken(wallet.getAddress(), token);

        try {
            call.execute();
            preferences.setFCMTokenSent(true);
            Log.i(TAG, "FCM token was send to server: " + token);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Can't send a token to server, retry in 5 minutes");
            preferences.setFCMTokenSent(false);
            retryTokenRefresh();
        }
    }

    public static void checkUpdateToken(Context context) {
        FirebaseInstanceId firebaseInstanceId = FirebaseInstanceId.getInstance();

        if (firebaseInstanceId == null) {
            Log.w(TAG, "FirebaseInstanceId is null");
            return;
        }

        Preferences preferences = App.getPreferences(context);
        final boolean tokenSent = preferences.getFCMTokenSent();

//        if (tokenSent)
//            return;

        RetrofitService retrofitService = Client.getRetrofitInstance()
                .create(RetrofitService.class);

        WalletHMQ wallet = WalletHMQ.getWorkWallet();

        if (wallet == null) {
            Log.e(TAG, "Work wallet is null");
            return;
        }

        final String token = firebaseInstanceId.getToken();
        preferences.setFCMToken(token);

        Call<DummyModel> call = retrofitService.updateFcmToken(wallet.getAddress(), token);
        call.enqueue(new Callback<DummyModel>() {
            @Override
            public void onResponse(Call<DummyModel> call, Response<DummyModel> response) {
                preferences.setFCMTokenSent(true);
                Log.i(TAG, "FCM token was send to server: " + token);
            }

            @Override
            public void onFailure(Call<DummyModel> call, Throwable t) {
                preferences.setFCMTokenSent(false);
                Log.e(TAG, "Can't send a token to server");
            }
        });
    }

    private void retryTokenRefresh() {
        final int MINUTES_5_IN_MS = 5*60*1000;
        SystemClock.sleep(MINUTES_5_IN_MS);
        onTokenRefresh();
    }
}
