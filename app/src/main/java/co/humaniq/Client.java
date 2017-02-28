package co.humaniq;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import co.humaniq.views.ViewContext;


public class Client {
    public static boolean isOnline(ViewContext context) {
        ConnectivityManager cm = (ConnectivityManager)(context.getInstance())
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
