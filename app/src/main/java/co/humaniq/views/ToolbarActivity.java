package co.humaniq.views;

import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import co.humaniq.R;
import co.humaniq.Router;
import co.humaniq.models.AuthToken;


public class ToolbarActivity extends BaseActivity {
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ActionBar actionBar;

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
                if (AuthToken.getInstance() != null && AuthToken.getInstance().getUser() != null)
                    Router.goActivity(this, Router.PROFILE);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.default_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
