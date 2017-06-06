package co.humaniq.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;

import co.humaniq.R;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by andrey on 30.05.17.
 */

public class VideoActivity extends ToolbarActivity {

    GifImageView gifImageView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_activity);

        initToolbar();

        gifImageView = (GifImageView) findViewById(R.id.gif_view);

        Bundle bundle = getIntent().getExtras();
        int gifId = bundle.getInt("gifId");
        gifImageView.setImageResource(gifId);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.pin_code_menu, menu);
        return true;
    }

}
