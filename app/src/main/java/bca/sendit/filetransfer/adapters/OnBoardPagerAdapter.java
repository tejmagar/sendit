package bca.sendit.filetransfer.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class OnBoardPagerAdapter extends FragmentStateAdapter {
    private final List<Fragment> onBoardPages;

    public OnBoardPagerAdapter(List<Fragment> onBoardPages, @NonNull FragmentManager fragmentManager,
                               @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        this.onBoardPages = onBoardPages;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return onBoardPages.get(position);
    }

    @Override
    public int getItemCount() {
        return onBoardPages.size();
    }
}
