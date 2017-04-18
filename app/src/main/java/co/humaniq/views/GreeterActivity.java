package co.humaniq.views;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import co.humaniq.App;
import co.humaniq.Preferences;
import co.humaniq.R;
import co.humaniq.Router;
import co.humaniq.models.AuthToken;
import co.humaniq.models.User;
import co.humaniq.models.Wallet;
import co.humaniq.services.AuthService;
import co.humaniq.views.dashboard_fragments.HistoryFragment;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;


public class GreeterActivity extends BaseActivity {
    private final static int LOGIN_REQUEST = 1000;
    private final static int REGISTER_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeter);

        attachOnClickView(R.id.buttonLogin);
        attachOnClickView(R.id.buttonRegister);

        String ABI = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ABI = Build.SUPPORTED_ABIS[0];
        } else {
            ABI = Build.CPU_ABI;
        }
        Log.d("ABI", ABI);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonLogin:
//                Router.goActivity(this, Router.LOGIN, LOGIN_REQUEST);
                Router.goActivity(this, Router.PIN_CODE, LOGIN_REQUEST);

//                Wallet wallet = new Wallet(45, "424d590c-84c7-492c-866a-176aa947ab5c", 444, false,
//                        "HMQ", "http://13.75.91.36/media/qr_codes/424d590c-84c7-492c-866a-176aa947ab5c.png");
//                User user = new User(45, "", wallet);
//                AuthToken token = new AuthToken("5f5d0f032d8908f4e6d253593bfba02f847ee823", user);
//                AuthToken.updateInstance(token);
                Router.goActivity(this, Router.DASHBOARD);
                Preferences preferences = App.getPreferences(this);
                Log.d("GreeterActivity", preferences.getAccount());
                break;

            case R.id.buttonRegister:
                Router.goActivity(this, Router.REGISTER, REGISTER_REQUEST);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != AuthToken.RESULT_GOT_TOKEN) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        Router.goActivity(this, Router.DASHBOARD);
    }
}
