package co.humaniq.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import co.humaniq.R;
import co.humaniq.Router;
import co.humaniq.models.AuthToken;


public class GreeterActivity extends BaseActivity {
    private final static int LOGIN_REQUEST = 1000;
    private final static int REGISTER_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeter);

        attachOnClick(R.id.buttonLogin);
        attachOnClick(R.id.buttonRegister);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonLogin:
                Router.goActivity(this, Router.LOGIN, LOGIN_REQUEST);
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
