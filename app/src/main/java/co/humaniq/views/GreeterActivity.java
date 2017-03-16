package co.humaniq.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import co.humaniq.R;
import co.humaniq.Router;
import co.humaniq.models.AuthToken;
import co.humaniq.models.User;
import co.humaniq.models.Wallet;


public class GreeterActivity extends BaseActivity {
    private final static int LOGIN_REQUEST = 1000;
    private final static int REGISTER_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeter);

        attachOnClickView(R.id.buttonLogin);
        attachOnClickView(R.id.buttonRegister);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonLogin:
//                Router.goActivity(this, Router.LOGIN, LOGIN_REQUEST);
                Wallet wallet = new Wallet(45, "424d590c-84c7-492c-866a-176aa947ab5c", 444, false,
                        "HMQ", "http://13.75.91.36/media/qr_codes/424d590c-84c7-492c-866a-176aa947ab5c.png");
                User user = new User(45, "", wallet);
                AuthToken token = new AuthToken("e55ab33fa614c3b0107495ec4c4e3206d0d0c6be", user);
                AuthToken.updateInstance(token);
                Router.goActivity(this, Router.DASHBOARD);
                break;

            case R.id.buttonRegister:
                Router.goActivity(this, Router.REGISTER, REGISTER_REQUEST);
                break;

            default:
                break;
        }
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
