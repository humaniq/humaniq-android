package co.humaniq;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import proxypref.ProxyPreferences;


public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        extractVLData();
    }

    private void extractVLData() {
        Preferences preferences = getPreferences(this);

        if (!preferences.getFirstRun())
            return;

        if (VLUtils.unpackZipFromAssets(this)) {
            preferences.setFirstRun(false);
        } else {
            Toast.makeText(this, "Unable to unzip VLData", Toast.LENGTH_SHORT).show();
            Log.e("Application", "Unable to unzip VLData");
            System.exit(1);
        }
    }

    public static Preferences getPreferences(Context context) {
        return ProxyPreferences.build(Preferences.class,
                context.getSharedPreferences("preferences", 0));
    }
}
