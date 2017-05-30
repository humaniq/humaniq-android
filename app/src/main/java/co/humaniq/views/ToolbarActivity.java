package co.humaniq.views;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import co.humaniq.R;
import co.humaniq.Router;
import co.humaniq.models.AuthToken;


public class ToolbarActivity extends BaseActivity {
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ActionBar actionBar;
    private ImageView toolbarImage;

    public Toolbar getToolbar() {
        return toolbar;
    }
    public ActionBar getActivityActionBar() {
        return actionBar;
    }

    protected void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
//        actionBar.setHomeAsUpIndicator(R.mipmap.back);

        toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getTitle());

        toolbarImage = (ImageView) toolbar.findViewById(R.id.toolbar_logo);

        if (getTitle().equals(getString(R.string.app_name))) {
            toolbarTitle.setVisibility(View.GONE);
            toolbarImage.setVisibility(View.VISIBLE);
        } else {
            toolbarTitle.setVisibility(View.VISIBLE);
            toolbarImage.setVisibility(View.GONE);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        toolbarTitle.setText(title);
        super.setTitle(title);
    }

    @Override
    public void setTitle(@StringRes int titleId) {
        toolbarTitle.setText(titleId);
        super.setTitle(titleId);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.help:
//                if (AuthToken.getInstance() != null && AuthToken.getInstance().getUser() != null)
//                    Router.goActivity(this, Router.PROFILE);
                showVideo();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showVideo() {
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.humaniq_2;

        Bundle bundle = new Bundle();
        bundle.putString("path", videoPath);
        Router.setBundle(bundle);
        Router.goActivity(this, Router.VIDEO);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.default_menu, menu);
        return true;
    }
}
