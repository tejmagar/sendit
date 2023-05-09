package bca.sendit.filetransfer.ui.navigations;

import android.app.Activity;

import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class NavigationManager {

    private final Activity activity;
    private final List<NavigationItem> navigationItems;
    private boolean allowTitleChange = false;
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;

    private int currentPosition;

    private final OnNavigationItemChangeListener onNavigationItemChangeListener;

    public NavigationManager(Activity activity, List<NavigationItem> navigationItems,
                             OnNavigationItemChangeListener onNavigationItemChangeListener) {
        this.activity = activity;
        this.navigationItems = navigationItems;
        this.onNavigationItemChangeListener = onNavigationItemChangeListener;
    }

    public void setItem(int position) {
        NavigationItem navigationItem = navigationItems.get(position);

        if (allowTitleChange) {
            activity.setTitle(navigationItem.getTitle());
        }

        if (tabLayout != null) {
            selectTab(position);
        }

        if (viewPager2 != null) {
            selectPagerItem(position);
        }

        onNavigationItemChangeListener.onChange(position);
        this.currentPosition = position;
    }

    public void allowTitleChange(boolean allow) {
        this.allowTitleChange = allow;
    }

    public void attachTabs(TabLayout tabLayout) {
        this.tabLayout = tabLayout;
    }

    private void selectTab(int position) {
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        if (tab != null) {
            tab.select();
        }
    }

    public void attachViewPager(ViewPager2 viewPager2) {
        this.viewPager2 = viewPager2;
    }

    private void selectPagerItem(int position) {
        this.viewPager2.setCurrentItem(position);
    }

    public List<NavigationItem> getNavigationItems() {
        return navigationItems;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }
}
