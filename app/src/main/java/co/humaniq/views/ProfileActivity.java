package co.humaniq.views;

import android.os.Bundle;
import android.widget.ImageView;
import co.humaniq.ImageTool;
import co.humaniq.R;
import co.humaniq.models.AuthToken;


public class ProfileActivity extends ToolbarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initToolbar();

        ImageView photo = (ImageView) findViewById(R.id.photo);
        ImageTool.loadFromUrlToImageView(this, AuthToken.getInstance().getUser().getPhoto(), photo);
    }
}
