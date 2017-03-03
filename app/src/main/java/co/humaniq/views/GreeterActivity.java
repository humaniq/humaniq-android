package co.humaniq.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import co.humaniq.R;
import co.humaniq.Router;


public class GreeterActivity extends BaseActivity {

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
                Router.goActivity(this, Router.LOGIN);
                break;

            case R.id.buttonRegister:
                Router.goActivity(this, Router.REGISTER);
                break;

            default:
                break;
        }
    }
}
