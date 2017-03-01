package co.humaniq.views;

import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import co.humaniq.R;


public class ToolbarActivity extends BaseActivity {
    private Toolbar toolbar;
    private TextView toolbarTitle;

    public Toolbar getToolbar() {
        return toolbar;
    }

    protected void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
//        actionBar.setHomeAsUpIndicator(R.mipmap.back);

        toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
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
}
