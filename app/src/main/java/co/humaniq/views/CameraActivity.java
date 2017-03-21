package co.humaniq.views;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import co.humaniq.R;
import co.humaniq.views.widgets.CameraPreview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CameraActivity extends ToolbarActivity {
    static {
        System.loadLibrary("gnustl_shared");
        System.loadLibrary("camera-lib");
    }

    private native void initPhotoMaker(String path);
    private native void onFrame(int width, int height, byte[] NV21FrameData);

    void initPhotoMaker() {
        final String dataDir = "/data/data/" + getPackageName();
        Log.d("PhotoMaker", dataDir);
        initPhotoMaker(dataDir);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
    }
}
