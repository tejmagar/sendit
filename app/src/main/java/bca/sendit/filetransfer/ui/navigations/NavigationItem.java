package bca.sendit.filetransfer.ui.navigations;

import androidx.fragment.app.Fragment;

public class NavigationItem {
    private final Fragment fragment;
    private final String title;

    public NavigationItem(Fragment fragment, String title) {
        this.fragment = fragment;
        this.title = title;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public String getTitle() {
        return title;
    }
}
