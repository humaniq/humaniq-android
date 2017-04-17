package co.humaniq.views;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;

import com.crashlytics.android.Crashlytics;

import co.humaniq.Client;
import co.humaniq.R;
import co.humaniq.models.AuthToken;
import co.humaniq.services.AuthService;
import co.humaniq.views.dashboard_fragments.HistoryFragment;
import co.humaniq.views.dashboard_fragments.ReceiveCoinsFragment;
import co.humaniq.views.dashboard_fragments.SettingsFragment;
import co.humaniq.views.dashboard_fragments.TransferCoinsFragment;
import co.humaniq.views.widgets.BottomMenuView;
import io.fabric.sdk.android.Fabric;

import java.util.ArrayList;


public class DashboardActivity extends ToolbarActivity {
    private BottomMenuView bottomMenuView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_dashboard);
        initToolbar();
        getActivityActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
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

            }

            @Override
            public void onPageSelected(int position) {
                bottomMenuView.selectTab(position);
                pagerAdapter.getItem(position).onResume();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
    public void finish() {
        AuthService service = new AuthService(null);
        service.logout(GENERAL_REQUEST);
        HistoryFragment.dataSetChanged = true;
        AuthToken.revoke();
        super.finish();
    }
}
