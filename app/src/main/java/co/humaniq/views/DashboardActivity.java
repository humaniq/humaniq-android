package co.humaniq.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.VideoView;

import com.crashlytics.android.Crashlytics;

import co.humaniq.DebugTool;
import co.humaniq.R;
import co.humaniq.Router;
import co.humaniq.models.AuthToken;
import co.humaniq.models.WalletHMQ;
import co.humaniq.services.notification.FcmInstanceIDListenerService;
import co.humaniq.views.dashboard_fragments.HistoryFragment;
import co.humaniq.views.dashboard_fragments.ReceiveCoinsFragment;
import co.humaniq.views.dashboard_fragments.WorksFragment;
import co.humaniq.views.dashboard_fragments.TransferCoinsFragment;
import co.humaniq.views.widgets.BottomMenuView;
import io.fabric.sdk.android.Fabric;
import pl.droidsonroids.gif.GifImageView;

import java.util.ArrayList;


public class DashboardActivity extends ToolbarActivity {
    private BottomMenuView bottomMenuView;
    private PagerAdapter pagerAdapter;
    ViewPager viewPager;

    private class PagerAdapter extends FragmentPagerAdapter {
        ArrayList<BaseFragment> fragments = new ArrayList<>();

        PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public BaseFragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public String getPageTitle(int position) {
            return "";
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        void addFragment(BaseFragment fragment) {
            fragments.add(fragment);
        }
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String method = intent.getStringExtra("message");
            pagerAdapter.fragments.get(bottomMenuView.getCurrentTabIndex()).onResume();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_dashboard);
        initToolbar();

        getActivityActionBar().setDisplayHomeAsUpEnabled(false);
        getActivityActionBar().setDisplayShowHomeEnabled(false);
        getActivityActionBar().setHomeButtonEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        View bottomMenu = findViewById(R.id.bottomMenu);

        pagerAdapter.addFragment(new HistoryFragment());
        pagerAdapter.addFragment(new TransferCoinsFragment());
        pagerAdapter.addFragment(new ReceiveCoinsFragment());
        pagerAdapter.addFragment(new WorksFragment());

        viewPager.setAdapter(pagerAdapter);
        bottomMenuView = new BottomMenuView(bottomMenu, viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                hideKeyboard();
            }

            @Override
            public void onPageSelected(int position) {
                hideKeyboard();

                bottomMenuView.selectTab(position);
                pagerAdapter.getItem(position).onResume();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public  void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getApplicationContext().
                getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;

            case R.id.help:
                showVideo();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showVideo() {
        String videoName = "";

        int gifId;
        switch (viewPager.getCurrentItem()){
            case 0:
                gifId = R.drawable.humaniq_2_medium;
                break;

            case 1:
                gifId = R.drawable.humaniq_3_medium;
                break;

            case 2:
                gifId = R.drawable.humaniq_3_medium;
                break;

            default:
                gifId = R.drawable.humaniq_2_medium;
        }


        Bundle bundle = new Bundle();
        bundle.putInt("gifId", gifId);
        Router.setBundle(bundle);
        Router.goActivity(this, Router.VIDEO);

    }

    @Override
    protected void onResume() {
        FcmInstanceIDListenerService.checkUpdateToken(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
                new IntentFilter("fcm-message"));
        super.onResume();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        super.onPause();
    }

    @Override
    public void finish() {
        HistoryFragment.dataSetChanged = true;
        AuthToken.revoke();
        WalletHMQ.revoke();
        super.finish();
    }


}
