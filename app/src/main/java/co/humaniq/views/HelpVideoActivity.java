package co.humaniq.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;

import co.humaniq.R;
import pl.droidsonroids.gif.GifImageView;


public class HelpVideoActivity extends ToolbarActivity {
    GifImageView gifImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_help_video);
        initToolbar();

        gifImageView = (GifImageView) findViewById(R.id.gif_view);

        Bundle bundle = getIntent().getExtras();
        int gifId = bundle.getInt("gifId");
        gifImageView.setImageResource(gifId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
