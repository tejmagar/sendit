package bca.sendit.filetransfer.ui.navigations;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class NavigationAdapter extends FragmentStateAdapter {
    private final List<NavigationItem> navigationItems;

    public NavigationAdapter(@NonNull FragmentActivity fragmentActivity,
                             List<NavigationItem> navigationItems) {
        super(fragmentActivity);
        this.navigationItems = navigationItems;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return navigationItems.get(position).getFragment();
    }

    @Override
    public int getItemCount() {
        return navigationItems.size();
    }
}
