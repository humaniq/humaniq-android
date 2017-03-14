package co.humaniq.views;

import android.os.Bundle;
import co.humaniq.R;


public class SignatureActivity extends ToolbarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_passwd);
        initToolbar();
    }
}
