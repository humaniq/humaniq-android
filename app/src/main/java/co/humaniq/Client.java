package co.humaniq;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import co.humaniq.models.AuthToken;
import co.humaniq.views.ViewContext;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Client {
    private static Retrofit anonymousRetrofit = null;
    private static Retrofit authRetrofit = null;
    private static OkHttpClient anonymousHttpClient = null;
    private static OkHttpClient authHttpClient = null;

    public static boolean isOnline(ViewContext context) {
        ConnectivityManager cm = (ConnectivityManager)(context.getActivityInstance())
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    static public OkHttpClient getAnonymousHttpClientInstance() {
        if (anonymousHttpClient != null)
            return anonymousHttpClient;

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

        anonymousHttpClient = httpClient.build();
        return anonymousHttpClient;
    }

    static public void revokeAuthClient() {
        authHttpClient = null;
        authRetrofit = null;
    }

    static public OkHttpClient getAuthHttpClientInstance() {
        if (authHttpClient != null)
            return authHttpClient;

        AuthToken authToken = AuthToken.getInstance();
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            HttpUrl originalHttpUrl = original.url();

            HttpUrl url = originalHttpUrl.newBuilder().build();
            Request.Builder requestBuilder = original.newBuilder()
                    .addHeader("Authorization", authToken.getAuthorization())
                    .url(url);

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        authHttpClient = httpClient.build();
        return authHttpClient;
    }

    static public Retrofit getAuthRetrofitInstance() {
        if (authRetrofit != null)
            return authRetrofit;

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        authRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(getAuthHttpClientInstance())
                .baseUrl(Config.SERVER_URL+Config.API_URL)
                .build();

        return authRetrofit;
    }

    static public Retrofit getAnonymousRetrofitInstance() {
        if (anonymousRetrofit != null)
            return anonymousRetrofit;

        anonymousRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(getAnonymousHttpClientInstance())
                .baseUrl(Config.SERVER_URL+Config.API_URL)
                .build();

        return anonymousRetrofit;
    }
}
