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
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;

import co.humaniq.Client;
import co.humaniq.R;
import co.humaniq.models.AuthToken;
import co.humaniq.services.notification.FcmInstanceIDListenerService;
import co.humaniq.views.dashboard_fragments.HistoryFragment;
import co.humaniq.views.dashboard_fragments.ReceiveCoinsFragment;
import co.humaniq.views.dashboard_fragments.SettingsFragment;
import co.humaniq.views.dashboard_fragments.TransferCoinsFragment;
import co.humaniq.views.widgets.BottomMenuView;
import io.fabric.sdk.android.Fabric;

import java.util.ArrayList;


public class DashboardActivity extends ToolbarActivity {
    private BottomMenuView bottomMenuView;
    private PagerAdapter pagerAdapter;

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
//        getActivityActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white); TODO: убрать оттуда какую либо кнопку

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        View bottomMenu = findViewById(R.id.bottomMenu);

        pagerAdapter.addFragment(new HistoryFragment());
        pagerAdapter.addFragment(new TransferCoinsFragment());
        pagerAdapter.addFragment(new ReceiveCoinsFragment());
        pagerAdapter.addFragment(new SettingsFragment());

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
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FcmInstanceIDListenerService.checkUpdateToken(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
                new IntentFilter("fcm-message"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
    }

    @Override
    public void finish() {
        HistoryFragment.dataSetChanged = true;
        AuthToken.revoke();
        super.finish();
    }
}
