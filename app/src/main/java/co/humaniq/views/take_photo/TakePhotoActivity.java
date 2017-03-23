package co.humaniq.views.take_photo;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import co.humaniq.App;
import co.humaniq.Preferences;
import co.humaniq.R;
import co.humaniq.views.ToolbarActivity;


public class TakePhotoActivity extends ToolbarActivity implements PhotoFragment.Listener {
    private static final String TAG = "TakePhotoActivity";
    private PhotoFragment photoFragment;
    private Preferences preferences;

    private void showFaceAuth() {
        if (photoFragment == null) {
            photoFragment = PhotoFragment.newInstance(this);
            photoFragment.setListener(this);
        }
        preferences.setStartTime(String.valueOf(System.currentTimeMillis()));
        preferences.setNeedPortrait(true);
        showFragment(photoFragment);
    }

    private void showFragment(Fragment fragment) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(fragment.toString()) == null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container, fragment, fragment.toString())
                    .commit();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo_test);

        preferences = App.getPreferences(this);
        showFaceAuth();
    }

    @Override
    public void onBestShotReady(Bitmap bitmap) {
        String path = getIntent().getStringExtra(MediaStore.EXTRA_OUTPUT);
        File file = new File(path);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "Error accessing file: " + e.getMessage());
        }

        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onNeedCameraPermission() {

    }
}
