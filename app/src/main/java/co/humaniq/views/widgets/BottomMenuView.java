package co.humaniq.views.widgets;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import co.humaniq.R;


public class BottomMenuView implements View.OnClickListener {
    private ImageButton[] tabs;
    private ImageButton currentTab;
    private ViewPager viewPager;

    public BottomMenuView(View bottomMenu, ViewPager viewPager) {
        this.viewPager = viewPager;

        tabs = new ImageButton[4];
        tabs[0] = (ImageButton) bottomMenu.findViewById(R.id.tab1);
        tabs[1] = (ImageButton) bottomMenu.findViewById(R.id.tab2);
        tabs[2] = (ImageButton) bottomMenu.findViewById(R.id.tab3);
        tabs[3] = (ImageButton) bottomMenu.findViewById(R.id.tab4);

        currentTab = tabs[0];

        tabs[0].setOnClickListener(this);
        tabs[1].setOnClickListener(this);
        tabs[2].setOnClickListener(this);
        tabs[3].setOnClickListener(this);
    }

    public void selectTab(final int tabIndex) {
        currentTab.setBackgroundResource(R.color.lightBlue);
        currentTab = tabs[tabIndex];
        currentTab.setBackgroundResource(R.color.darkBlue);
        viewPager.setCurrentItem(tabIndex);
    }

    private void selectTab(ImageButton tab) {
        currentTab.setBackgroundResource(R.color.lightBlue);
        currentTab = tab;
        currentTab.setBackgroundResource(R.color.darkBlue);
        viewPager.setCurrentItem(getIndexByTab(tab));
    }

    private int getIndexByTab(ImageButton tab) {
        for (int i = 0; i < 4; ++i) {
            if (tabs[i] == tab)
                return i;
        }

        return 0;
    }

    @Override
    public void onClick(View v) {
        selectTab((ImageButton) v);
    }
}
