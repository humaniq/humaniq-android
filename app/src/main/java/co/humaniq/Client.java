package co.humaniq;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import co.humaniq.views.ViewContext;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Client {
    private static Retrofit retrofit = null;
    private static OkHttpClient httpClient = null;

    public static boolean isOnline(ViewContext context) {
        ConnectivityManager cm = (ConnectivityManager)(context.getActivityInstance())
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    static public OkHttpClient getHttpClientInstance() {
        if (httpClient != null)
            return httpClient;

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            HttpUrl originalHttpUrl = original.url();

            HttpUrl url = originalHttpUrl.newBuilder()
                    .build();

            Request.Builder requestBuilder = original.newBuilder()
                    .url(url);

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        Client.httpClient = httpClient.build();
        return Client.httpClient;
    }

    static public Retrofit getRetrofitInstance() {
        if (retrofit != null)
            return retrofit;

        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClientInstance())
                .baseUrl(Config.SERVER_URL+Config.API_URL)
                .build();

        return retrofit;
    }

    static public String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
