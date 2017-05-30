package co.humaniq.views;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import co.humaniq.R;

/**
 * Created by andrey on 30.05.17.
 */

public class VideoActivity extends ToolbarActivity {

    VideoView videoView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_layout);

        initToolbar();

        videoView = (VideoView) findViewById(R.id.video_view);


        Bundle bundle = getIntent().getExtras();
        String videoPath = bundle.getString("path");

        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);
        videoView.setMediaController(new MediaController(this));
        videoView.requestFocus();
        videoView.start();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.pin_code_menu, menu);
        return true;
    }

}
